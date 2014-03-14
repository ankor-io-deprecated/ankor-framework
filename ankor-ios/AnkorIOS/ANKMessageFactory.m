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

-(ANKChangeMessage*)createValueChangeMessage:(NSString *)property value:(NSString *)value {
    return [[ANKChangeMessage alloc] initWith:property type:ct_value key:Nil value:value];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action {
    return [self createActionMessage:property action:action params:nil];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action params:(NSDictionary *)params {
    return [[ANKActionMessage alloc] initWith:property action:action params:params];
}

-(ANKConnectMessage *)createConnectMessage:(NSString *)property {
    return [[ANKConnectMessage alloc] initWith:property];
}

-(ANKConnectMessage *)createConnectMessage:(NSString *)property params:(NSDictionary *)params{
    return [[ANKConnectMessage alloc] initWith:property params:params];
}

@end
