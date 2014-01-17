//
//  ANKRefFactory.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRefFactory.h"

@implementation ANKRefFactory

- (id) initWith: (ANKRefContext*) refContext {
    _refContext = refContext;
    return self;
}

- (ANKRef*) ref:(NSString*) path {
    return [[ANKRef alloc] initWith:path refContext:_refContext];
}
@end
