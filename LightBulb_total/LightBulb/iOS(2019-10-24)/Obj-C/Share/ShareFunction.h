//
//  ShareFunction.h
//  OpenGroup 0.0.1
//
//  Created by TestIOS on 15-4-2.
//  Copyright (c) 2015年 XiaMen_Leadhead. All rights reserved.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

#import <MediaPlayer/MediaPlayer.h>

@interface ShareFunction : NSObject


+ (UIColor *)colorWithHexString:(NSString *)color andAlpha:(CGFloat)alpha;
+(NSString *)getCurrentTimeWithYMd;
+(NSString *)getNowDate;

//Phone number verification
+ (BOOL)checkPhoneNumInput:(NSString *)_text;
#pragma mark - Get the week
+(NSString *)getWeekData:(int)Num;

#pragma mark - Get the version number
+(NSString *)getVersion;
//Get the current time
+ (NSDate *)getCurrentTime;
#pragma mark - mailbox
+ (BOOL) validateEmail:(NSString *)email;
#pragma mark - Get the current volume of the system
+(float)getVolumeLevel;
#pragma mark Gets the maximum and minimum values ​​in the array
+(NSMutableArray *)GetMaxAndMin:(NSMutableArray *)arr;
#pragma mark Returns the number of days in the month
+(NSInteger)BackNowMonthCount;
#pragma mark Date plus one
+(NSString *)DateAdd:(NSString *)date;
#pragma mark Date minus one
+(NSString *)DateSub:(NSString *)date;


#pragma mark - Returns the order status
+(NSString *)OrderState:(NSString *)orderString;

#pragma mark - By judging the model, return different parameters
+(int)JudgmentPhoneModel:(int)number andOne:(int)one;

#pragma mark - Read bytes
+(NSDictionary *)readData:(NSData *)data;

#pragma mark - Converts a string to data
+ (NSData*)dataForHexString:(NSString*)hexString;

#pragma mark - To determine whether all the numbers
+(BOOL)isPureNumandCharacters:(NSString *)string;

#pragma mark - Turn the white background transparent
+(UIImage*) imageToTransparent:(UIImage*) image;

#pragma mark - To determine whether all the numbers
+(BOOL)isPureInt:(NSString *)string;

#pragma mark - Converts a string to hexadecimal
+ (NSString *)hexStringFromString:(NSString *)string;

#pragma mark - Converts decimal to hexadecimal
+(NSString *)tenToHex:(long long int)tmpid;

#pragma mark - Converts Binary to Hexadecimal
+ (NSString *)data2Hex:(NSData *)data;

#pragma mark -Converts View to image
+ (UIImage *) imageWithView:(UIView *)view;

#pragma mark - Show Prompt
+(void)showProgressHUD:(NSString *)string andView:(UIView *)view andTime:(float)time;

#pragma mark - Get the current language
+(NSString *)getCurrentLanguage;

#pragma mark - Label Height
+(CGFloat)getLabelHeight:(UILabel*)label;



@end
