//
//  ANKRefContextFactory.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKRefContext.h"
#import "ANKModelContext.h"

@interface ANKRefContextFactory : NSObject

- (ANKRefContext*) createRefContextFor:(ANKModelContext*) modelContext;

@end
