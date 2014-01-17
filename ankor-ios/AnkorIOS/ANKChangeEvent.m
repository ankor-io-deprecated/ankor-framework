//
//  ANKChangeEvent.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKChangeEvent.h"
#import "ANKChangeEventListener.h"

@implementation ANKChangeEvent

-(id)initWith:(ANKEventSource)source ref:(ANKRef*)changedProperty change:(ANKChange*)change {
    _source = source;
    _changedProperty = changedProperty;
    _change = change;
    return self;
}

-(BOOL)isAppropriateListener:(id <ANKEventListener>) listener {
    return [listener conformsToProtocol:@protocol(ANKChangeEventListener)];
}

-  (void) processBy:(id <ANKEventListener>) listener {
    [listener process:self];
}

@end
