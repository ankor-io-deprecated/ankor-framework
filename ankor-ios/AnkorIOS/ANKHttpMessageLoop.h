//
//  AnkorHttpMessageLoop.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessageBus.h"
#import "ANKMessageLoop.h"
#import "ANKMessageFactory.h"
#import "ANKMessageListener.h"

@interface ANKHttpMessageLoop : NSObject <ANKMessageLoop, ANKMessageBus>

- (id) initWith: (id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory*) messageFactory senderId:(NSString*)senderId url:(NSString*)url;

@end
