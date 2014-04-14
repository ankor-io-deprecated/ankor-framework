//
//  ANKRemoteEventListener.m
//  AnkorIOS
//
//  Created by Thomas Spiegl on 10/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import "ANKRemoteEventListener.h"
#import "ANKChangeEvent.h"
#import "ANKActionEvent.h"
#import "ANKMessage.h"

@interface ANKRemoteEventListener()

@property ANKMessageFactory* messageFactory;
@property id <ANKMessageLoop> messageLoop;
@property ANKRefContext* refContext;

@end

@implementation ANKRemoteEventListener

-(id)initWith:(ANKMessageFactory*)messageFactory messageLoop:(id <ANKMessageLoop>)messageLoop refContext:(ANKRefContext*) refContext{
    _messageFactory = messageFactory;
    _messageLoop = messageLoop;
    _refContext = refContext;
    return self;
}

- (void)process:(id)event {
    if ([((id <ANKModelEvent>) event) source] != LOCAL) {
        return;
    }
    ANKMessage* message = nil;
    if ([event isKindOfClass:[ANKChangeEvent class]]) {
        ANKChangeEvent* changeEvent = (ANKChangeEvent*) event;
        message = [_messageFactory createValueChangeMessage:changeEvent.changedProperty.path value:changeEvent.change.value stateValues:[self getStateValues]];
    } else if ([event isKindOfClass:[ANKActionEvent class]]) {
        ANKActionEvent* actionEvent = (ANKActionEvent*) event;
        message = [_messageFactory createActionMessage:actionEvent.actionProperty.path action:actionEvent.action.name params:actionEvent.action.params stateValues:[self getStateValues]];
    }
    [_messageLoop.messageBus sendMessage:message];
}

- (NSDictionary*) getStateValues {
    NSMutableDictionary* stateValues = [NSMutableDictionary new];
    [_refContext.modelContext.stateProps enumerateObjectsUsingBlock:^(id stateProp, NSUInteger idx, BOOL *stop) {
        [stateValues setValue:[[self.refContext.refFactory ref:stateProp] value] forKey:stateProp];
    }];
    return stateValues;
}


@end
