//
//  AnkorHttpMessageLoop.m
//  HttpTest
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import "ANKHttpMessageLoop.h"
#import "ANKActionMessage.h"
#import "ANKMessageSerialization.h"

@interface ANKHttpMessageLoop() {
    id <ANKMessageListener> messageListener;
    NSString* url;
    NSMutableArray *messages;
    NSLock *lock;
    NSString *senderId;
    ANKMessageSerialization *msgSerialization;
    ANKMessageFactory *messageFactory;
    dispatch_semaphore_t sema;
    bool sendInitMessage;
    NSString* connProperty;
    NSDictionary* connParams;
}

@end

@implementation ANKHttpMessageLoop

static dispatch_once_t once;
static NSOperationQueue *connectionQueue;

- (id)initWith:(id <ANKMessageListener>)listener messageFactory:(ANKMessageFactory *)factory senderId:(NSString *)sId url:(NSString*)sUrl
    connectProperty:(NSString*)connectProperty params:(NSDictionary*)connectParams {
    messageListener = listener;
    messageFactory = factory;
    senderId = [[NSUUID UUID] UUIDString];
    messages = [[NSMutableArray alloc] init];
    lock = [NSLock new];
    connProperty = connectProperty;
    connParams = connectParams;
    msgSerialization = [ANKMessageSerialization new];
    url = sUrl;
    return self;
}

- (void) start {
    sendInitMessage = true;
    [self doSend];
}

+ (NSOperationQueue *)connectionQueue {
    dispatch_once(&once, ^{
        connectionQueue = [[NSOperationQueue alloc] init];
        [connectionQueue setMaxConcurrentOperationCount:1];
        [connectionQueue setName:@"ANKHttpMessageLoop"];
    });
    return connectionQueue;
}

- (void)stop {
    
}

-(id<ANKMessageBus>)messageBus {
    return self;
}

- (void)doSend {
    // Create the request.
    NSMutableURLRequest *request=[NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]
                                                            cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                        timeoutInterval:5.0];
    
    [request setHTTPMethod:@"POST"];
    
    NSMutableData *postData = [NSMutableData new];

    [lock lock];
    if (sendInitMessage) {
        ANKConnectMessage* initMessage = [messageFactory createConnectMessage:connProperty params:connParams];
        [messages removeAllObjects];
        [messages addObject:initMessage];
        sendInitMessage = false;
    }
    [postData appendData: [[[NSString alloc] initWithFormat:@"clientId=%@", senderId] dataUsingEncoding:NSUTF8StringEncoding]];
    [postData appendData: [@"&messages=" dataUsingEncoding:NSUTF8StringEncoding]];
    [postData appendData:[msgSerialization serialize:messages]];
    [messages removeAllObjects];
    [lock unlock];
    
    //NSLog(@"Sending json %@", [[NSString alloc] initWithData:postData encoding:NSUTF8StringEncoding]);
    
    [request setHTTPBody: postData];
    
    [NSURLConnection sendAsynchronousRequest:request queue:[ANKHttpMessageLoop connectionQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error) {
        if (error) {
            NSLog(@"Error in updateInfoFromServer: %@ %@", error, [error localizedDescription]);
            sendInitMessage = true;
        } else if (!response) {
            NSLog(@"Could not reach server!");
            sendInitMessage = true;
        } else if (!data) {
            NSLog(@"Server did not return any data!");
            sendInitMessage = true;
        } else {
            //NSLog(@"Received json %@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                for (id message in [msgSerialization deserialize:data]) {
                    if (message) {
                        [messageListener onMessage:message];
                    }
                }
            }];
        }
        [self doSend];
    }];
}

// MessageBus

-(void)sendMessage:(ANKMessage*)message {
    [lock lock];
    [messages addObject:message];
    [lock unlock];
}


@end
