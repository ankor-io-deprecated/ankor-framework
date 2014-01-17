//
//  ANKRef.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRef.h"
#import "ANKChangeEvent.h"
#import "ANKActionEvent.h"
#import "ANKModelEvent.h"

@interface ANKRef() {
    NSArray* _pathElements;
}

@end

@implementation ANKRef

static NSString* _pathPattern;

- (id)initWith:(NSString*) path refContext:(ANKRefContext*)refContext {
    _path = path;
    _propertyName = [_pathElements lastObject];
    _refContext = refContext;
    if (!_pathPattern) {
        _pathPattern = @"([a-zA-Z0-9])*";
    }
    _pathElements = [ANKRef initPathElements:path];
    _propertyName = [_pathElements lastObject];
    return self;
}

+ (NSArray*) initPathElements:(NSString*)path {
    NSMutableArray* pathElements = [NSMutableArray new];
    
    NSError  *error = nil;
    NSRange searchedRange = NSMakeRange(0, [path length]);
    NSRegularExpression* regex = [NSRegularExpression regularExpressionWithPattern: _pathPattern options:0 error:&error];
    NSArray* matches = [regex matchesInString:path options:0 range: searchedRange];
    
    for (NSTextCheckingResult* match in matches) {
        if ([match range].length > 0) {
            [pathElements addObject:[path substringWithRange:[match range]]];
        }
    }
    
    return pathElements;
}

- (id)value {
    NSDictionary* dict = [self findDictonary];
    if (dict) {
        return [dict objectForKey:_propertyName];
    }
    return nil;
}

-(void) setValue:(id)value {
    ANKChange* change = [[ANKChange alloc]initWith:ct_value key:NULL value:value];
    [self apply:self change:change];
}

- (BOOL *)isValid {
    return NO;
}

-(ANKRef *)appendPath:(NSString *)path {
    return nil;
}

-(void)fireAction:(ANKAction *)action {
    [_refContext.modelContext dispatch:[[ANKActionEvent alloc]initWith:LOCAL ref:self action:action]];
}

-(void)apply:(id)source change:(ANKChange*)change {
    NSDictionary* dict = [self findDictonary];
    if (dict) {
        id v = [dict valueForKey:self.propertyName];
        if (change.type == ct_value) {
            [dict setValue:change.value forKey:self.propertyName];
        } else if (change.type == ct_insert) {
            id v = [dict valueForKey:self.propertyName];
            if ([v isKindOfClass:[NSMutableArray class]]) {
                NSMutableArray* list = (NSMutableArray*) v;
                int index = [(NSNumber*) change.key intValue];
                if ([list count] <= index) {
                    [list addObject:change.value];
                } else {
                    [list insertObject:change.value atIndex:index];
                }
            }
            
        } else if (change.type == ct_delete) {
            if ([v isKindOfClass:[NSMutableArray class]]) {
                NSMutableArray* list = (NSMutableArray*) v;
                int index = [(NSNumber*) change.key intValue];
                [list removeObjectAtIndex:index];
            }
        } else if (change.type == ct_replace) {
            if ([v isKindOfClass:[NSMutableArray class]]) {
                NSMutableArray* list = (NSMutableArray*) v;
                int index = [(NSNumber*) change.key intValue];
                [list replaceObjectAtIndex:index withObject:change.value];
            }
        } else {
            // TODO error
        }
        if (source == self) {
            [_refContext.modelContext dispatch:[[ANKChangeEvent alloc]initWith:LOCAL ref:self change:change]];
        } else {
            [_refContext.modelContext dispatch:[[ANKChangeEvent alloc]initWith:REMOTE ref:self change:change]];
        }
    }
}

- (NSDictionary*) findDictonary {
    id currentModelObject = _refContext.modelContext.root;
    for (int i = 0; i < [_pathElements count] - 1; i++) {
        NSString* pathElement = [_pathElements objectAtIndex:i];
        if ([currentModelObject isKindOfClass:[NSArray class]]) {
            int index = [pathElement intValue];
            currentModelObject = [(NSArray*) currentModelObject objectAtIndex:index];
        } else {
            currentModelObject = [currentModelObject objectForKey:pathElement];
        }
        if (currentModelObject == NULL) {
            return nil;
        }
    }
    if ([currentModelObject isKindOfClass:[NSMutableDictionary class]]) {
        return currentModelObject;
    } else {
        return nil;
    }
}

@end