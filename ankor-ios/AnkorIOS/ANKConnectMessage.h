//
//  ANKConnectMessage.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 14/03/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessage.h"

@interface ANKConnectMessage : ANKMessage

@property NSDictionary *params;

- (id)initWith:(NSString *)property;
- (id)initWith:(NSString *)property params:(NSDictionary*)params;

@end
