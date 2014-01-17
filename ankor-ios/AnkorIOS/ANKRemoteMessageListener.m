//
//  ANKRemoteMessageListener.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRemoteMessageListener.h"
#import "ANKRef.h"
#import "ANKChange.h"
#import "ANKActionMessage.h"
#import "ANKChangeMessage.h"


@implementation ANKRemoteMessageListener

ANKRefContext* _refContext;

-(id)initWith:(ANKRefContext*)refContext {
    _refContext = refContext;
    return self;
}

-(void) onMessage:(ANKMessage*) msg {
    NSLog(@"Received message %@", msg);
    if ([msg isKindOfClass:[ANKActionMessage class]]) {
        // ignore
    } else {
        ANKChangeMessage* message = (ANKChangeMessage*) msg;
        ANKRef* ref = [_refContext.refFactory ref:message.property];
        ANKChange* change = [[ANKChange alloc]initWith:message.type key:message.key value:message.value];
        [ref apply:self change:change];
    }
}

@end
