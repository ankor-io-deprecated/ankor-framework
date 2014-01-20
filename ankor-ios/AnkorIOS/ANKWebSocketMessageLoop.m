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

@interface ANKWebSocketMessageLoop() <SRWebSocketDelegate> {
    id <ANKMessageListener> messageListener;
    NSString* url;
    NSInteger pollingInterval;
    NSMutableArray *messages;
    ANKMessageSerialization *msgSerialization;
    ANKMessageFactory *messageFactory;
    dispatch_semaphore_t sema;
    
    SRWebSocket *_webSocket;
    
    bool _nextMessageIsClientId;
}

@end

@implementation ANKWebSocketMessageLoop

#pragma mark - ANKMessageLoop

- (id)initWith:(id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory *)factory url:(NSString*)sUrl {
    messageListener = listener;
    messageFactory = factory;
    messages = [[NSMutableArray alloc] init];
    msgSerialization = [ANKMessageSerialization new];
    url = sUrl;
    return self;
}

- (void)start {
    [self connect];
}

- (void)stop {
    
}

- (void)doSend {
    if (!_webSocket.readyState == SR_OPEN) {
        [self connect];
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

#pragma mark - WebSocket

- (void)open {
    
}

- (void)close {
    
}

- (void)connect;
{
    @synchronized(self) {
        SRReadyState readyState = _webSocket.readyState;
        if (_webSocket && (_webSocket.readyState == SR_OPEN || _webSocket.readyState == SR_CONNECTING)) {
            return;
        }
        _webSocket = nil;
        _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
        _webSocket.delegate = self;
        
        _nextMessageIsClientId = true;
        readyState = _webSocket.readyState;
        [_webSocket open];
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)data {
    @autoreleasepool {
        @synchronized(self) {
            NSLog(@"webSocketDidOpen %@", data);
            if (_nextMessageIsClientId) {
                _nextMessageIsClientId = false;
                messageFactory.senderId = data;
                ANKActionMessage *initMessage = [messageFactory createActionMessage:@"root" action:@"init"];
                [messages insertObject:initMessage atIndex:0];
                [self doSend];
                [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(sendHeartbeat) userInfo:Nil repeats:YES];
            } else {
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
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    NSLog(@"didFailWithError");
    [self connect];
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    NSLog(@"didCloseWithCode");
    [self connect];
}


@end
