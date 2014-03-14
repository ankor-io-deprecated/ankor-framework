//
//  ANKActionMessage.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKActionMessage.h"

@implementation ANKActionMessage



- (id) initWith:(NSString*)property action:(NSString*)action {
    return [self initWith:property action:action params:nil];
}

- (id) initWith:(NSString*)property action:(NSString*)action params:(NSDictionary*)params {
    self = [super initWith:property];
    self.action = action;
    self.params = params;
    return self;
}

@end
