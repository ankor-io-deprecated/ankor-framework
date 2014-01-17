//
//  ANKEventListeners.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 08/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKEventListener.h"

@interface ANKEventListeners : NSObject

- (void)add:(id <ANKEventListener>)listener;

- (void)remove:(id <ANKEventListener>)listener;

@property(readonly) NSMutableArray* listeners;

@end
