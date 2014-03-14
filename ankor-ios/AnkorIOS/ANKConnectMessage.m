//
//  ANKConnectMessage.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 14/03/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKConnectMessage.h"

@implementation ANKConnectMessage

- (id)initWith:(NSString *)senderId property:(NSString *)property messageId:(NSString *)messageId {
    self = [super initWith:senderId modelId:nil messageId:messageId];
    self.property = property;
    return self;
}

- (id)initWith:(NSString *)senderId property:(NSString *)property messageId:(NSString *)messageId params:(NSDictionary*)params {
    self = [super initWith:senderId modelId:nil messageId:messageId];
    self.property = property;
    self.params = params;
    return self;
}

@end
