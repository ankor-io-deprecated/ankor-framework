//
//  ANKActionEvent.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKModelEvent.h"
#import "ANKAction.h"
#import "ANKRef.h"

@interface ANKActionEvent : NSObject <ANKModelEvent>

-(id)initWith:(ANKEventSource)source ref:(ANKRef*)actionProperty action:(ANKAction*)action;

@property(readonly) ANKRef* actionProperty;
@property(readonly) ANKAction* action;
@property(readonly) ANKEventSource source;

@end
