//
//  ANKEventListeners.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKEventListeners.h"

@implementation ANKEventListeners

-(id)init {
    _listeners = [[NSMutableArray alloc] init];
    return self;
}

- (void)add:(id <ANKEventListener>)listener {
    [_listeners addObject:listener];
}

- (void)remove:(id <ANKEventListener>)listener {
    [_listeners removeObject:listener];
}

@end