//
//  MessageFactory.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKMessageFactory.h"
#import "ANKActionMessage.h"
#import "ANKChangeMessage.h"
#import "ANKConnectMessage.h"
#import "ANKChange.h"

@implementation ANKMessageFactory

int currentMessageId;
NSString *modelId;

-(id)initWith:(NSString *)senderId {
    currentMessageId = 0;
    _senderId = senderId;
    modelId = @"collabTest";
    return self;
}

-(ANKChangeMessage*)createValueChangeMessage:(NSString *)property value:(NSString *)value {
    NSString *messageId;
    @synchronized(self) {
        messageId = [NSString stringWithFormat:@"%i",currentMessageId++];
    }
    return [[ANKChangeMessage alloc] initWith:_senderId modelId:modelId messageId:messageId property:property type:ct_value key:Nil value:value];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action {
    return [self createActionMessage:property action:action params:nil];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action params:(NSDictionary *)params {
    NSString *messageId;
    @synchronized(self) {
        messageId = [NSString stringWithFormat:@"%i",currentMessageId++];
    }
    return [[ANKActionMessage alloc] initWith:_senderId modelId:modelId messageId:messageId property:property action:action params:params];
}

-(ANKConnectMessage *)createConnectMessage:(NSString *)property {
    NSString *messageId;
    @synchronized(self) {
        messageId = [NSString stringWithFormat:@"%i",currentMessageId++];
    }
    return [[ANKConnectMessage alloc] initWith:_senderId property:property messageId:messageId];
}

@end
