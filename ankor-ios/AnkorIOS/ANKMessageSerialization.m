//
//  ANKMessageSerializer.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKMessageSerialization.h"
#import "ANKActionMessage.h"
#import "ANKChangeMessage.h"
#import "ANKChange.h"
#import "ANKConnectMessage.h"

static NSString* property   = @"property";
static NSString* key        = @"key";
static NSString* _value     = @"value";
static NSString* change     = @"change";
static NSString* type       = @"type";
static NSString* action     = @"action";
static NSString* params     = @"params";
static NSString* connectParams = @"connectParams";
static NSString* name       = @"name";
static NSString* _stateProps = @"stateProps";
static NSString* _stateValues = @"stateValues";

static NSString* _insert    = @"insert";
static NSString* _delete    = @"delete";
static NSString* _replace   = @"replace";

@implementation ANKMessageSerialization

- (NSData *)serialize:(id) message {
    NSError *jsonError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[self messagesAsArrayOfDict:message] options:0 error:&jsonError];
    if (jsonError) {
        NSLog(@"Error parsing json %@", jsonError.description);
    }
    return jsonData;
}

-(id) messagesAsArrayOfDict:(id) msg {
    NSArray* messages;
    if ([msg isKindOfClass:[NSArray class]]) {
        messages = (NSArray*) msg;
        NSMutableArray *msgArray = [[NSMutableArray alloc]initWithCapacity:[messages count]];
        for (id msg in messages) {
            NSMutableDictionary *dict = [NSMutableDictionary dictionary];
            if ([msg isKindOfClass:[ANKChangeMessage class]]) {
                ANKChangeMessage *message = (ANKChangeMessage*) msg;
                [dict setValue:message.property forKey:property];
                NSMutableDictionary *changeDict = [NSMutableDictionary dictionary];
                [changeDict setValue:[self changeTypeToString:message.type] forKey:type];
                [changeDict setValue:message.key forKey:key];
                [changeDict setValue:message.value forKey:_value];
                [dict setValue:changeDict forKey:change];
                if (message.stateValues) {
                    [dict setValue:message.stateValues forKey:_stateValues];
                }
            } else if ([msg isKindOfClass:[ANKActionMessage class]]) {
                ANKActionMessage *message = (ANKActionMessage*) msg;
                [dict setValue:message.property forKey:property];
                if (message.params && [message.params count] > 0) {
                    NSMutableDictionary *actionDict = [NSMutableDictionary dictionary];
                    [actionDict setValue:message.action forKey:name];
                    [actionDict setValue:message.params forKey:params];
                    [dict setValue:actionDict forKey:action];
                } else {
                    [dict setValue:message.action forKey:action];
                }
                if (message.stateValues) {
                    [dict setValue:message.stateValues forKey:_stateValues];
                }
            } else if ([msg isKindOfClass:[ANKConnectMessage class]]) {
                ANKConnectMessage *message = (ANKConnectMessage*) msg;
                [dict setValue:message.property forKey:property];
                if (message.params && [message.params count] > 0) {
                    [dict setValue:message.params forKey:connectParams];
                }
            }
            [msgArray addObject:dict];
        }
        return msgArray;
    } else {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        if ([msg isKindOfClass:[ANKChangeMessage class]]) {
            ANKChangeMessage *message = (ANKChangeMessage*) msg;
            [dict setValue:message.property forKey:property];
            NSMutableDictionary *changeDict = [NSMutableDictionary dictionary];
            [changeDict setValue:[self changeTypeToString:message.type] forKey:type];
            [changeDict setValue:message.key forKey:key];
            [changeDict setValue:message.value forKey:_value];
            [dict setValue:changeDict forKey:change];
            if (message.stateValues) {
                [dict setValue:message.stateValues forKey:_stateValues];
            }
        } else if ([msg isKindOfClass:[ANKActionMessage class]]) {
            ANKActionMessage *message = (ANKActionMessage*) msg;
            [dict setValue:message.property forKey:property];
            if (message.params && [message.params count] > 0) {
                NSMutableDictionary *paramDict = [NSMutableDictionary dictionary];
                [paramDict setValue:message.action forKey:name];
                [paramDict setValue:message.params forKey:params];
                [dict setValue:paramDict forKey:action];
            } else {
                [dict setValue:message.action forKey:action];
            }
            if (message.stateValues) {
                [dict setValue:message.stateValues forKey:_stateValues];
            }
        } else if ([msg isKindOfClass:[ANKConnectMessage class]]) {
            ANKConnectMessage *message = (ANKConnectMessage*) msg;
            [dict setValue:message.property forKey:property];
            if (message.params && [message.params count] > 0) {
                [dict setValue:message.params forKey:connectParams];
            }
        }
        return dict;
    }
}

-(NSArray*)deserialize:(NSData *)data {
    NSError *error;
    NSArray *messagesDict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&error];
    NSMutableArray *messages;
    if ([messagesDict isKindOfClass:[NSArray class]]) {
        messages = [[NSMutableArray alloc]initWithCapacity:[messagesDict count]];
        for (id dict in messagesDict) {
            if ([dict valueForKey:action]) {
                [messages addObject:
                 [[ANKActionMessage alloc]
                  initWith: [dict valueForKey:property]
                  action:[dict valueForKey:action]
                  stateProps:[dict valueForKey:_stateProps] stateValues:nil]];
            } else if ([dict valueForKey:change]) {
                NSDictionary *changeDict = [dict valueForKey:change];
                [messages addObject:
                 [[ANKChangeMessage alloc] initWith:
                  [dict valueForKey:property]
                  type:[self changeTypeFromString:[changeDict valueForKey:type]]
                  key:[changeDict valueForKey:key]
                  value:[changeDict valueForKey:_value]
                  stateProps:[dict valueForKey:_stateProps] stateValues:nil]];
            }
        }
    } else {
        messages = [[NSMutableArray alloc]initWithCapacity:1];
        id dict = messagesDict;
        if ([dict valueForKey:action]) {
            [messages addObject:
             [[ANKActionMessage alloc]
              initWith: [dict valueForKey:property]
              action:[dict valueForKey:action]
              stateProps:[dict valueForKey:_stateProps] stateValues:nil]
             ];
        } else if ([dict valueForKey:change]) {
            NSDictionary *changeDict = [dict valueForKey:change];
            id valueObj;
            ANKChangeType typeObj;
            if ([[changeDict allKeys] containsObject:type]) {
                valueObj = [changeDict valueForKey:_value];
                typeObj = [self changeTypeFromString:[changeDict valueForKey:type]];
            } else {
                // TODO why do I get different change formats?
                valueObj = [[changeDict valueForKey:_value] valueForKey:_value];
                typeObj = [self changeTypeFromString:[[changeDict valueForKey:_value] valueForKey:type]];
            }
            [messages addObject:
             [[ANKChangeMessage alloc]
              initWith: [dict valueForKey:property]
              type:typeObj
              key:[changeDict valueForKey:key]
              value:valueObj stateProps:[dict valueForKey:_stateProps] stateValues:nil]];
        }
    }
    return messages;
}

-(ANKChangeType) changeTypeFromString:(NSString*) value {
    if ([value isEqualToString:_value]) {
        return ct_value;
    } else if ([value isEqualToString:_insert]) {
        return ct_insert;
    } else if ([value isEqualToString:_delete]) {
        return ct_delete;
    } else if ([value isEqualToString:_replace]) {
        return ct_replace;
    } else {
        // TODO error
        return -1;
    }
}

-(NSString*) changeTypeToString:(ANKChangeType) type {
    switch(type) {
        case ct_value: return _value;
        case ct_insert: return _insert;
        case ct_delete: return _delete;
        case ct_replace: return _replace;
        default: return nil; // TODO error
    }
}


@end
