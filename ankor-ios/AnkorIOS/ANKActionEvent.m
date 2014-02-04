//
//  ANKActionEvent.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKActionEvent.h"
#import "ANKActionEventListener.h"

@implementation ANKActionEvent

-(id)initWith:(ANKEventSource)source ref:(ANKRef*)actionProperty action:(ANKAction*)action {
    _source = source;
    _actionProperty = actionProperty;
    _action = action;
    return self;
}

-(BOOL)isAppropriateListener:(id <ANKEventListener>) listener {
    return [listener conformsToProtocol:@protocol(ANKActionEventListener)];
}

-  (void) processBy:(id <ANKEventListener>) listener {
    [listener process:self];
}

@end
