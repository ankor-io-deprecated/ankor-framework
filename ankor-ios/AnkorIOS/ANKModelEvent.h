//
//  ANKModelEvent.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, ANKEventSource) {
    LOCAL = 0,
    REMOTE = 1
};

@protocol ANKModelEvent <NSObject>

-(BOOL)isAppropriateListener:(id) listener;

-(void)processBy:(id) listener;

-(ANKEventSource)source;

@end
