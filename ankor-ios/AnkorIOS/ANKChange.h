//
//  ANKChange.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 04/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

enum ANKChangeTypeValues {
    ct_value = 0,
    ct_insert = 1,
    ct_delete = 2,
    ct_replace = 3
};

typedef enum ANKChangeTypeValues ANKChangeType;

@interface ANKChange : NSObject

-(id)initWith:(ANKChangeType)type key:(id)key value:(id)value;

@property(readonly)ANKChangeType type;
@property(readonly)id key;
@property(readonly)id value;

@end
