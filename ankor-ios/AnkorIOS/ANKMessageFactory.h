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

-(ANKChangeMessage*)createValueChangeMessage:(NSString *)property value:(NSString *)value;
-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action;
-(ANKActionMessage *)createActionMessage:(NSString *)property action:(NSString *)action params:(NSDictionary *)params;
-(ANKConnectMessage *)createConnectMessage:(NSString *)property;
-(ANKConnectMessage *)createConnectMessage:(NSString *)property params:(NSDictionary *)params;

@end
