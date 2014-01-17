//
//  ANKRemoteMessageListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessageListener.h"
#import "ANKRefContext.h"

@interface ANKRemoteMessageListener : NSObject <ANKMessageListener>

-(id)initWith:(ANKRefContext*)refContext;

@end
