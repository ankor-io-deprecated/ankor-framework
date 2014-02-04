//
//  ANKRemoteEventListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKChangeEventListener.h"
#import "ANKActionEventListener.h"
#import "ANKMessageLoop.h"
#import "ANKMessageFactory.h"

@interface ANKRemoteEventListener : NSObject <ANKChangeEventListener, ANKActionEventListener>

-(id)initWith:(ANKMessageFactory*)messageFactory messageLoop:(id <ANKMessageLoop>)messageLoop;

@end
