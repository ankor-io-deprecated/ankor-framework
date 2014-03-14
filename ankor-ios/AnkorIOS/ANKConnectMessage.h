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

// TODO connectParams

@property NSString *property;
@property NSDictionary *params;

- (id)initWith:(NSString *)senderId property:(NSString *)property messageId:(NSString *)messageId;
- (id)initWith:(NSString *)senderId property:(NSString *)property messageId:(NSString *)messageId params:(NSDictionary*)params;

@end
