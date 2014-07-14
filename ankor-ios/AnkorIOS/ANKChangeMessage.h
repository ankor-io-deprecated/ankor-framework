//
//  ANKChangeMessage.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKMessage.h"
#import "ANKChange.h"

@interface ANKChangeMessage : ANKMessage

@property ANKChangeType type;
@property id key;
@property NSObject *value;

- (id) initWith:(NSString*)property type:(ANKChangeType)type key:(id)key value:(NSObject*)value stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues;

@end
