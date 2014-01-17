//
//  AnkorIOSTests.m
//  AnkorIOSTests
//
//  Created by Thomas Spiegl on 03/12/13.
//  Copyright (c) 2013 Thomas Spiegl. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ANKMessageLoop.h"
#import "ANKHttpMessageLoop.h"
#import "ANKActionMessage.h"
#import "ANKMessageFactory.h"

@interface AnkorIOSTests : XCTestCase

@end

@implementation AnkorIOSTests

- (void)setUp
{
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)test
{
    @autoreleasepool {
        
        ANKHttpMessageLoop  *messageLoop;
        NSString *senderId = [NSString stringWithFormat:@"%i", arc4random_uniform(1000)];
        
        ANKMessageFactory *messageFactory = [[ANKMessageFactory alloc] initWith:senderId];
        
        id messageListener = ^(id message){
            //NSLog(@"Received message %@");
            // TODO received message
        };
        
        messageLoop = [[ANKHttpMessageLoop alloc] initWith:messageListener messageFactory:messageFactory senderId:senderId];
        [messageLoop start];
        dispatch_async(dispatch_get_global_queue( DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            NSLog(@"Main %@", [NSThread isMainThread] ? @"yes" : @"no");
            [NSThread sleepForTimeInterval:4.0f];
            ANKMessage *message = [messageFactory createValueChangeMessage:@"root.userName" value:@"Thomas Spiegl"];
            [messageLoop.messageBus sendMessage:message];
        });
        [[NSRunLoop currentRunLoop] run];
    }

}

@end
