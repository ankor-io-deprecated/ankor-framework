//
//  ANKSystem.h
//  AnkorIOS
//
//  Created by Thomas Spiegl on 13/01/14.
//  Copyright (c) 2014 Thomas Spiegl. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANKSystem : NSObject

-(id)initWith:(NSString*)modelId url:(NSString*)url useWebsocket:(BOOL)useWebSocket;

-(void)start;

@end
