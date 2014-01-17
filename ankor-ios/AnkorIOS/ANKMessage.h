//
//  ANKMessage.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANKMessage : NSObject

@property NSString *senderId;
@property NSString *modelId;
@property NSString *messageId;

- (id) initWith: (NSString*)senderId modelId:(NSString*)modelId messageId:(NSString*)messageId;

@end
