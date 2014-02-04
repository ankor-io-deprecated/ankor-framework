//
//  ANKRefFactory.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKRef.h"
#import "ANKRefContext.h"

@class ANKRefContext;
@class ANKRef;

@interface ANKRefFactory : NSObject

-(id)initWith:(ANKRefContext*)refContext;

- (ANKRef*) ref:(NSString*)path;

@property(readonly) ANKRefContext* refContext;

@end
