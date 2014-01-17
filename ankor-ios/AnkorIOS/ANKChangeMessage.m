//
//  ANKChangeMessage.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKChangeMessage.h"

@implementation ANKChangeMessage

-(id)initWith:(NSString *)senderId modelId:(NSString *)modelId messageId:(NSString *)messageId
     property:(NSString *)property type:(ANKChangeType)type key:(id)key value:(NSObject *)value {
    self = [super initWith:senderId modelId:modelId messageId:messageId];
    self.property = property;
    self.type = type;
    self.key = key;
    self.value = value;
    return self;
}

@end
