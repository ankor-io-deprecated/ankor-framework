//
//  ANKRef.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKRefContext.h"
#import "ANKAction.h"
#import "ANKChange.h"

@class ANKRefContext;

@interface ANKRef : NSObject

- (id)initWith:(NSString*) path refContext:(ANKRefContext*)refContext;

- (id) value;
- (void) setValue:(id)value;
- (BOOL*) isValid;
- (ANKRef*) appendPath:(NSString*)path;
- (void) fireAction:(ANKAction*)action;

-(void)apply:(id)source change:(ANKChange*)change;

@property(readonly) ANKRefContext* refContext;
@property (readonly) ANKRef* parent;
@property (readonly) NSString* path;
@property (readonly) NSString* propertyName;

@end
