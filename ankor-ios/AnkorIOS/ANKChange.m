//
//  ANKChange.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKChange.h"
#import "ANKChangeEventListener.h"

@implementation ANKChange

-(id)initWith:(ANKChangeType)type key:(id)key value:(id)value {
    _type = type;
    _key = key;
    _value = value;
    return self;
}

@end
