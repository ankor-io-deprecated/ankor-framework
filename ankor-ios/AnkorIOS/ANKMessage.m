//
//  ANKMessage.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKMessage.h"

@implementation ANKMessage

- (id)initWith:(NSString *)senderId modelId:(NSString *)modelId messageId:(NSString *)messageId {
    self.senderId = senderId;
    self.modelId = modelId;
    self.messageId = messageId;
    return self;
}

@end
