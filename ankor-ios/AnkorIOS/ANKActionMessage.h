//
//  ANKActionMessage.h
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ANKMessage.h"

@interface ANKActionMessage : ANKMessage

@property NSString *property;
@property NSString *action;
@property NSDictionary *params;

- (id) initWith:(NSString *)senderId modelId:(NSString *)modelId messageId:(NSString *)messageId property:(NSString*)property action:(NSString*)name;

- (id) initWith: (NSString *)senderId modelId:(NSString *)modelId messageId:(NSString *)messageId property:(NSString*)property action:(NSString*)action params:(NSDictionary*)params;

@end
