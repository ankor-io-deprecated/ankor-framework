//
//  ANKChangeEventListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKChangeEvent.h"
#import "ANKEventListener.h"

@protocol ANKChangeEventListener <ANKEventListener>

- (void)process:(ANKChangeEvent*)event;

@end
