//
//  ToDoItem.m
//  TodoList
//
//  Created by Thomas Spiegl on 27/11/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ToDoItem.h"

@implementation ToDoItem

-(id)initWith:(NSString *)itemName {
    self = [super init];
    self.itemName = itemName;
    self.completed = NO;
    return self;
}

@end
