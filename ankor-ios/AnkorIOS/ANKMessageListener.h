//
//  ANKMessageListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessage.h"

@protocol ANKMessageListener <NSObject>

-(void) onMessage:(ANKMessage*) message;

@end
