//
//  ANKRefChangeListener.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 09/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRefChangeListener.h"

@interface ANKRefChangeListener()

@property ANKRef* ref;
@property id target;
@property SEL changeListener;

@end

@implementation ANKRefChangeListener {
    ChangeListenerBlock _changeListenerBlock;
}

-(id)initWith:(ANKRef*)ref target:(id)target changeListener:(SEL)changeListener {
    self.ref = ref;
    self.target = target;
    self.changeListener = changeListener;
    return self;
}

-(id)initWith:(ANKRef*)ref changeListener:(ChangeListenerBlock)changeListener {
    self.ref = ref;
    _changeListenerBlock = changeListener;
    return self;
}


- (void)process:(ANKChangeEvent*)event {
    if ([event.changedProperty.path rangeOfString:self.ref.path].location != NSNotFound ||
        [self.ref.path rangeOfString:event.changedProperty.path].location != NSNotFound) {
        if (self.target) {
            IMP listener = [[_target class] instanceMethodForSelector:_changeListener];
            listener(_target, _changeListener, self.ref.value);
        } else {
            _changeListenerBlock(self.ref.value);
        }
    }
}

@end
