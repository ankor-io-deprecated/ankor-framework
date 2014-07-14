//
//  ANKConnectMessage.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 14/03/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKConnectMessage.h"

@implementation ANKConnectMessage

- (id)initWith:(NSString *)property {
    return [self initWith:property params:nil];
    self.property = property;
    return self;
}

- (id)initWith:(NSString *)property params:(NSDictionary*)params {
    self = [super initWith:property stateProps:nil stateValues:nil];
    self.params = params;
    return self;
}

@end
