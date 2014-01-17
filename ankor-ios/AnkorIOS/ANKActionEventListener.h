//
//  ANKActionEventListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKActionEvent.h"

@protocol ANKActionEventListener <NSObject>

- (void)process:(ANKActionEvent*)event;

@end
