//
//  ANKRefContext.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKModelContext.h"
#import "ANKRefFactory.h"

@class ANKRefFactory;
@class ANKModelContext;

@interface ANKRefContext : NSObject

- (id)initWith:(ANKModelContext*)modelContext;
+ (ANKRefContext*) instance;

@property(readonly) ANKModelContext* modelContext;
@property(readonly) ANKRefFactory* refFactory;


@end
