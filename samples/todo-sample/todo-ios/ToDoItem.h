//
//  ToDoItem.h
//  TodoList
//
//  Created by Thomas Spiegl on 27/11/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ToDoItem : NSObject

@property NSString *itemName;
@property BOOL completed;
@property (readonly) NSDate *creationDate;

- (id) initWith:(NSString*) itemName;

@end
