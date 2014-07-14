//
//  ANKActionMessage.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKActionMessage.h"

@implementation ANKActionMessage



- (id) initWith:(NSString*)property action:(NSString*)action stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues {
    return [self initWith:property action:action params:nil stateProps:stateProps stateValues:stateValues];
}

- (id) initWith:(NSString*)property action:(NSString*)action params:(NSDictionary*)params stateProps:(NSArray*)stateProps stateValues:(NSDictionary*) stateValues {
    self = [super initWith:property stateProps:stateProps stateValues:stateValues];
    self.action = action;
    self.params = params;
    return self;
}

@end
