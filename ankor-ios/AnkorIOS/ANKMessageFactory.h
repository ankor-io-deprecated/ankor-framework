//
//  MessageFactory.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKActionMessage.h"
#import "ANKChangeMessage.h"
#import "ANKConnectMessage.h"

@interface ANKMessageFactory : NSObject

-(ANKChangeMessage*)createValueChangeMessage:(NSString *)property value:(NSString *)value stateValues:(NSDictionary *)stateValues;
-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action stateValues:(NSDictionary *)stateValues;
-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action params:(NSDictionary *)params stateValues:(NSDictionary *)stateValues;
-(ANKConnectMessage *)createConnectMessage:(NSString *)property;
-(ANKConnectMessage *)createConnectMessage:(NSString *)property params:(NSDictionary *)params;

@end
