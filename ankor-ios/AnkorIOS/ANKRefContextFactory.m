//
//  ANKRefContextFactory.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKRefContextFactory.h"

@implementation ANKRefContextFactory

- (ANKRefContext*) createRefContextFor:(ANKModelContext*) modelContext {
    return [[ANKRefContext alloc] initWith:modelContext];
}

@end
