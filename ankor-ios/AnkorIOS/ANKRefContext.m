//
//  ANKRefContext.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRefContext.h"

@implementation ANKRefContext

static ANKRefContext* instance = nil;

- (id)initWith:(ANKModelContext*)modelContext {
    _modelContext = modelContext;
    _refFactory = [[ANKRefFactory alloc]initWith:self];
    instance = self;
    return self;
}

+ (ANKRefContext*) instance {
    return instance;
}

@end
