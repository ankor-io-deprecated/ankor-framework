//
//  ANKMessage.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKMessage.h"

@implementation ANKMessage

- (id)initWith:(NSString *)property stateProps:(NSArray *)stateProps stateValues:(NSDictionary *)stateValues {
    self.property = property;
    self.stateProps = stateProps;
    self.stateValues = stateValues;
    return self;
}

@end
