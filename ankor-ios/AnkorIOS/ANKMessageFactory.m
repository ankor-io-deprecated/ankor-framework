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

-(ANKChangeMessage*)createValueChangeMessage:(NSString *)property value:(NSString *)value stateValues:(NSDictionary *)stateValues {
    return [[ANKChangeMessage alloc] initWith:property type:ct_value key:Nil value:value stateProps:nil stateValues:stateValues];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action stateValues:(NSDictionary *)stateValues {
    return [self createActionMessage:property action:action params:nil stateValues:stateValues];
}

-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action params:(NSDictionary *)params stateValues:(NSDictionary *)stateValues {
    return [[ANKActionMessage alloc] initWith:property action:action params:params stateProps:nil stateValues:stateValues];
}

-(ANKConnectMessage *)createConnectMessage:(NSString *)property {
    return [[ANKConnectMessage alloc] initWith:property];
}

-(ANKConnectMessage *)createConnectMessage:(NSString *)property params:(NSDictionary *)params{
    return [[ANKConnectMessage alloc] initWith:property params:params];
}

@end