//
//  ANKModelContext.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKModelContext.h"

@implementation ANKModelContext

- (id)initWith:(NSString*) modelId {
    modelId = modelId;
    _root = [[NSMutableDictionary alloc]init];
    _eventListeners = [[ANKEventListeners alloc]init];
    return self;
}

- (void)dispatch:(id <ANKModelEvent>) event {
    for (id listener in _eventListeners.listeners) {
        if ([event isAppropriateListener:listener]) {
            [event  processBy:listener];
        }
    }
}

@end
