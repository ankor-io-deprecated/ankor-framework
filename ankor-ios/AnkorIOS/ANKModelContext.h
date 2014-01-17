//
//  ANKModelContext.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKEventListeners.h"
#import "ANKModelEvent.h"

@interface ANKModelContext : NSObject

- (id)initWith:(NSString*) modelId;
- (void)dispatch:(id <ANKModelEvent>) event;

@property (readonly) NSString* modelId;
@property NSDictionary* root;
@property (readonly) ANKEventListeners* eventListeners;

@end