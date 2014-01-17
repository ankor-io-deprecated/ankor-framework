//
//  ANKSystem.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 13/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKSystem.h"
#import "ANKMessageLoop.h"
#import "ANKHttpMessageLoop.h"
#import "ANKActionMessage.h"
#import "ANKMessageFactory.h"
#import "ANKRemoteMessageListener.h"
#import "ANKModelContext.h"
#import "ANKRefContextFactory.h"
#import "ANKRemoteEventListener.h"
#import "ANKWebSocketMessageLoop.h"

@interface ANKSystem()

@property(readonly) id messageLoop;
@property(readonly) ANKRefContext* refContext;

@end

@implementation ANKSystem

-(id)initWith:(NSString*)modelId url:(NSString*)url useWebsocket:(BOOL)useWebsocket {
    
    NSString *senderId = [NSString stringWithFormat:@"%i", arc4random_uniform(1000)];
    ANKMessageFactory *messageFactory = [[ANKMessageFactory alloc] initWith:senderId];
    
    ANKRefContextFactory* refContextFactory = [[ANKRefContextFactory alloc] init];
    
    ANKModelContext* modelContext = [[ANKModelContext alloc] initWith:modelId];
    
    _refContext = [refContextFactory createRefContextFor:modelContext];
    
    ANKRemoteMessageListener* messageListener = [[ANKRemoteMessageListener alloc]initWith:_refContext];
    
    if (useWebsocket) {
        _messageLoop = [[ANKWebSocketMessageLoop alloc] initWith:messageListener messageFactory:messageFactory url:url];
    } else {
        _messageLoop = [[ANKHttpMessageLoop alloc] initWith:messageListener messageFactory:messageFactory senderId:senderId url:url];
    }
    
    ANKRemoteEventListener* remoteEventListener = [[ANKRemoteEventListener alloc]initWith:messageFactory messageLoop:_messageLoop];
    [[modelContext eventListeners]add:remoteEventListener];
    return self;
}

-(void)start {
    [_messageLoop start];
}

@end
