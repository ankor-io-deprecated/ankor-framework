//
//  ANKRefs.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKRef.h"

@interface ANKRefs : NSObject

+(id)observe:(NSString*)refPath target:(id)target listener:(SEL)listener;

+(void)fireAction:(NSString*)refPath name:(NSString*)name params:(NSDictionary*)params;

+(void)fireAction:(NSString*)refPath name:(NSString*)name;

+(void)changeValue:(NSString*)refPath value:(id)value;

@end
