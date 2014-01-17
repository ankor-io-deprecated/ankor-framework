//
//  ANKAction.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKAction.h"

@implementation ANKAction

- (id) initWith:(NSString*)name params:(NSDictionary*)params {
    _name = name;
    _params = params;
    return self;
}

@end
