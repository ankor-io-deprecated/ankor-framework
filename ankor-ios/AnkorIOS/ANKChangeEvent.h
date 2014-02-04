//
//  ANKChangeEvent.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKRef.h"
#import "ANKChange.h"
#import "ANKModelEvent.h"
#import "ANKEventListener.h"

@interface ANKChangeEvent : NSObject <ANKModelEvent>

-(id)initWith:(ANKEventSource)source ref:(ANKRef*)changedProperty change:(ANKChange*)change;

@property(readonly) ANKRef* changedProperty;
@property(readonly) ANKChange* change;
@property(readonly) ANKEventSource source;

@end
