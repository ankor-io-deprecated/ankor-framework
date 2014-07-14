//
//  ANKWebSocketMessageLoop.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 13/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessageListener.h"
#import "ANKMessageFactory.h"
#import "ANKMessageLoop.h"
#import "ANKMessageBus.h"
 
@interface ANKWebSocketMessageLoop : NSObject <ANKMessageLoop, ANKMessageBus>

- (id) initWith: (id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory*) messageFactory url:(NSString*)url
    connectProperty:(NSString*)connectProperty params:(NSDictionary*)connectParams;

@end
