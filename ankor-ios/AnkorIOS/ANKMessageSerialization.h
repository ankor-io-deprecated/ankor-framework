//
//  ANKMessageSerializer.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANKMessageSerialization : NSObject

- (NSData*) serialize: (id) message;
- (NSArray*) deserialize: (NSData*) data;

@end
