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

-(id)initWith:(NSString*)connectProperty connectParams:(NSDictionary*)connectParams url:(NSString*)url useWebsocket:(BOOL)useWebsocket {
    
    ANKMessageFactory *messageFactory = [[ANKMessageFactory alloc] init];
    
    ANKRefContextFactory* refContextFactory = [[ANKRefContextFactory alloc] init];
    
    ANKModelContext* modelContext = [[ANKModelContext alloc] initWith:connectProperty];
    
    _refContext = [refContextFactory createRefContextFor:modelContext];
    
    ANKRemoteMessageListener* messageListener = [[ANKRemoteMessageListener alloc]initWith:_refContext];
    
    if (useWebsocket) {
        _messageLoop = [[ANKWebSocketMessageLoop alloc] initWith:messageListener messageFactory:messageFactory url:url connectProperty:connectProperty params:connectParams];
    } else {
        _messageLoop = [[ANKHttpMessageLoop alloc] initWith:messageListener messageFactory:messageFactory url:url connectProperty:connectProperty params:connectParams];
    }
    
    ANKRemoteEventListener* remoteEventListener = [[ANKRemoteEventListener alloc]initWith:messageFactory messageLoop:_messageLoop];
    [[modelContext eventListeners]add:remoteEventListener];
    return self;
}

-(void)start {
    [_messageLoop start];
}

@end
