//
//  ANKMessageLoop.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessageBus.h"

@protocol ANKMessageLoop <NSObject>

- (void) start;
- (void) stop;
- (id <ANKMessageBus>) messageBus;

@end
