//
//  ANKAction.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANKAction : NSObject

- (id) initWith:(NSString*)name params:(NSDictionary*)params;

@property(readonly) NSString* name;
@property(readonly) NSDictionary* params;

@end
