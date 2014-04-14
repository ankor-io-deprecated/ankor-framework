//
//  ANKWebSocketMessageLoop.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 13/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKWebSocketMessageLoop.h"
#import "ANKMessageSerialization.h"
#import "SRWebSocket.h"
#import "ANKMessage.h"

typedef enum {
    ANK_WS_UNDEFINED         = 0,
    ANK_WS_INIT_CONNECTION   = 1,
    ANK_WS_CONNECTED         = 2,
} ANKWebSocketState;

@interface ANKWebSocketMessageLoop() <SRWebSocketDelegate> {
    
    id <ANKMessageListener> messageListener;
    NSString* url;
    NSInteger pollingInterval;
    NSMutableArray *messages;
    ANKMessageSerialization *msgSerialization;
    ANKMessageFactory *messageFactory;
    dispatch_semaphore_t sema;
    NSString* clientId;
    NSString* connProperty;
    NSDictionary* connParams;

    SRWebSocket *_webSocket;
    ANKWebSocketState _state;
}

@end


@implementation ANKWebSocketMessageLoop

#pragma mark - ANKMessageLoop

- (id)initWith:(id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory *)factory url:(NSString*)sUrl
connectProperty:(NSString*)connectProperty params:(NSDictionary*)connectParams {
    messageListener = listener;
    messageFactory = factory;
    messages = [[NSMutableArray alloc] init];
    msgSerialization = [ANKMessageSerialization new];
    clientId = [[NSUUID UUID] UUIDString];
    connProperty = connectProperty;
    connParams = connectParams;
    url = [sUrl stringByAppendingString:[NSString stringWithFormat:@"/%@", clientId]];
    [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(sendHeartbeat) userInfo:Nil repeats:YES];
    _state = ANK_WS_UNDEFINED;
    return self;
}

- (void)start {
    [self connect];
}

- (void)stop {
    
}

#pragma mark - MessageBus

- (id <ANKMessageBus>) messageBus {
    return self;
}

- (void) sendMessage:(ANKMessage*)message {
    @synchronized(self) {
        [messages addObject:message];
        [self doSend];
    }
}

- (void)doSend {
    if (!_webSocket.readyState == SR_OPEN) {
        [self reconnect];
        return;
    }
    @synchronized(self) {
        for (id msg in messages) {
            NSData* data = [msgSerialization serialize:msg];
            NSLog(@"Sending json %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
            [_webSocket send:data];
        }
        [messages removeAllObjects];
    }
}

#pragma mark - WebSocket

- (void)open {
    
}

- (void)close {
    
}

- (void)reconnect {
    if (_state == ANK_WS_INIT_CONNECTION) {
        return; // already connecting or reconnect scheduled
    } else {
        _state = ANK_WS_INIT_CONNECTION;
        [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(executeReconnect) userInfo:Nil repeats:NO];
    }
}

- (void)executeReconnect {
    _webSocket = nil;
    [self connect];
}

- (void)connect;
{
    @synchronized(self) {
        _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
        if (_webSocket && _webSocket.readyState == SR_OPEN) {
            return;
        }
        _webSocket.delegate = self;
        
        _state = ANK_WS_INIT_CONNECTION;
        [_webSocket open];
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)data {
    NSLog(@"didReceiveMessage %@", data);
    @autoreleasepool {
        @synchronized(self) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                @autoreleasepool {
                    NSMutableData *dataBytes = [NSMutableData new];
                    [dataBytes appendData: [(NSString*) data dataUsingEncoding:NSUTF8StringEncoding]];
                    for (id message in [msgSerialization deserialize:dataBytes]) {
                        if (message) {
                            [messageListener onMessage:message];
                        }
                    }
                }
            }];
        }
    }
}

- (void)sendHeartbeat {
    @autoreleasepool {
        if (_webSocket.readyState == SR_OPEN) {
            NSString* heartbeatMsg = @" ";
            NSData* data = [heartbeatMsg dataUsingEncoding:NSUTF8StringEncoding];
            @synchronized(self) {
                [_webSocket send:data];
            }
        }
    }
}

- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    NSLog(@"webSocketDidOpen");
    _state = ANK_WS_CONNECTED;
    ANKConnectMessage* initMessage = [messageFactory createConnectMessage:connProperty params:connParams];
    [messages insertObject:initMessage atIndex:0];
    [self doSend];
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    NSLog(@"didFailWithError");
    _state = ANK_WS_UNDEFINED;
    [self reconnect];
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    NSLog(@"didCloseWithCode");
    _state = ANK_WS_UNDEFINED;
    [self reconnect];
}


@end
