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
    NSLock *lock;
    ANKMessageSerialization *msgSerialization;
    ANKMessageFactory *messageFactory;
    dispatch_semaphore_t sema;
    bool connected;
    
    SRWebSocket *_webSocket;
}

@end

@implementation ANKWebSocketMessageLoop

#pragma mark - ANKMessageLoop

- (id)initWith:(id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory *)factory url:(NSString*)sUrl {
    messageListener = listener;
    messageFactory = factory;
    messages = [[NSMutableArray alloc] init];
    lock = [NSLock new];
    msgSerialization = [ANKMessageSerialization new];
    url = sUrl;
    return self;
}

- (void)start {
    connected = false;
    [self connect];
}

- (void)stop {
    
}

- (void)doSend {
    if (!connected) {
        return;
    }
    [lock lock];

    for (id msg in messages) {
        NSData* data = [msgSerialization serialize:msg];
        NSLog(@"Sending json %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
        [_webSocket send:data];
    }
    
    [messages removeAllObjects];
    [lock unlock];
}


#pragma mark - MessageBus

- (id <ANKMessageBus>) messageBus {
    return self;
}

- (void) sendMessage:(ANKMessage*)message {
    [lock lock];
    [messages addObject:message];
    [lock unlock];
    [self doSend];
}

#pragma mark - WebSocket

- (void)open {
    
}

- (void)close {
    
}

- (void)connect;
{
    _webSocket.delegate = nil;
    [_webSocket close];
    
    _webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:url]]];
    _webSocket.delegate = self;
    
    [_webSocket open];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)data {
    NSLog(@"webSocketDidOpen %@", data);
    if (!connected) {
        connected = true;
        messageFactory.senderId = data;
        ANKActionMessage *initMessage = [messageFactory createActionMessage:@"root" action:@"init"];
        [messages removeAllObjects];
        [messages addObject:initMessage];
        [self doSend];
        [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(sendHeartbeat) userInfo:Nil repeats:YES];
    } else {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            NSMutableData *dataBytes = [NSMutableData new];
            [dataBytes appendData: [(NSString*) data dataUsingEncoding:NSUTF8StringEncoding]];
            for (id message in [msgSerialization deserialize:dataBytes]) {
                if (message) {
                    [messageListener onMessage:message];
                }
            }
        }];
    }
}

- (void)sendHeartbeat {
    NSString* heartbeatMsg = @" ";
    NSData* data = [heartbeatMsg dataUsingEncoding:NSUTF8StringEncoding];
    [_webSocket send:data];
}

- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    NSLog(@"webSocketDidOpen");
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    NSLog(@"didFailWithError");
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    NSLog(@"didCloseWithCode");
}


@end
