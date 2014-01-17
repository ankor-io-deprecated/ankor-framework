//
//  ANKEventListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKModelEvent.h"

@protocol ANKEventListener <NSObject>

-(void)process:(id <ANKModelEvent>) event;

@end
