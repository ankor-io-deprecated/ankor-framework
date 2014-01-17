//
//  ANKRefs.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRefs.h"
#import "ANKRef.h"
#import "ANKRefContext.h"
#import "ANKRefChangeListener.h"
#import "ANKAction.h"

@implementation ANKRefs

+(id)observe:(NSString*)refPath target:(id)target listener:(SEL)listener {
    ANKRefContext* refContext = [ANKRefContext instance];
    ANKRefChangeListener* changeListener = [[ANKRefChangeListener alloc]initWith:[ANKRefs ref:refPath] target:target changeListener:listener];
    [[refContext.modelContext eventListeners]add:changeListener];
    return self;
}

+(void)fireAction:(NSString*)refPath name:(NSString*)name {
    [self fireAction:refPath name:name params:NULL];
}


+(void)fireAction:(NSString*)refPath name:(NSString*)name params:(NSDictionary*)params {
    ANKAction* action = [[ANKAction alloc]initWith:name params:params];
    [[ANKRefs ref:refPath] fireAction:action];
}

+(void)changeValue:(NSString*)refPath value:(id)value {
    [[ANKRefs ref:refPath] setValue:value];
}

+(ANKRef*)ref:(NSString*)refPath {
    return [[ANKRefContext instance].refFactory ref:refPath];
}

@end
