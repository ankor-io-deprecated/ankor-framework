//
//  ANKRefChangeListener.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKChangeEventListener.h"
#import "ANKRef.h"

@interface ANKRefChangeListener : NSObject <ANKChangeEventListener>

-(id)initWith:(ANKRef*)ref target:(id)target changeListener:(SEL)changeListener;

@end
