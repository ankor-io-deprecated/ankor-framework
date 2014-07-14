//
//  ANKActionMessage.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessage.h"

@interface ANKActionMessage : ANKMessage

@property NSString *action;
@property NSDictionary *params;

- (id) initWith:(NSString*)property action:(NSString*)action stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues;
- (id) initWith:(NSString*)property action:(NSString*)action params:(NSDictionary*)params stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues;

@end
