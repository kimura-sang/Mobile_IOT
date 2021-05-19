//
//  BLEController.h
//  全彩照明
//
//  Created by kejin HE on 2018/12/3.
//  Copyright © 2018 mlight. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "../PrefixHeader.pch"

NS_ASSUME_NONNULL_BEGIN

@interface BLEController : NSObject

#pragma mark - 颜色变化 -- 亮度调节
+(NSData *)bleColorAlpha:(int)alpha andRed:(int)red andGreen:(int)green andBlue:(int)blue;

#pragma mark - 保存当前颜色
+(NSData *)savecurrenColor;

#pragma mark - 修改名字
+(NSData *)changeBleName:(NSString *)name;

#pragma mark - 开灯
+(NSData *)openBle;

#pragma mark - 逛灯
+(NSData *)closeBle;

#pragma mark - 开关灯
+(NSData *)switchLight:(BOOL)isOpen;

#pragma mark - 密码
+(NSData *)blePasswork:(NSString *)passwork;

#pragma mark - 修改密码
+(NSData *)changeOldPasswork:(NSString *)oldPasswork andNewPasswork:(NSString *)newPasswork;

#pragma mark - 延时关灯
+(NSData *)delayTimeTurnOffBle:(NSString *)time;

#pragma mark - 延时开灯
+(NSData *)delayTimeOpenBle:(NSString *)time;

#pragma mark - 查询延时时间
+(NSData *)queryDelayTime;

#pragma mark - 关闭延时开灯
+(NSData *)closeDelayTimeOpenBle;

#pragma mark - 关闭延时关灯
+(NSData *)closeDelayTimeTurnOffBle;

#pragma mark - 请求版本
+(NSData *)requestVersion;

#pragma mark - 颜色渐变
+(NSData *)colorFocusRGB:(NSMutableArray *)array;


+(NSData *)dataWithHexString:(NSString *)hexString;

+(NSData *)hexToBytes:(NSString *)str;

#pragma mark - 律动模式开启
+(NSData *)musicOn;
#pragma mark - 律动模式关闭
+(NSData *)musicOff;

#pragma mark - 定时开灯
+(NSData *)TimingOpenLight:(NSString *)timeStr;

#pragma mark - 进入闪烁模式  num为存储序号，num范围为‘0’到‘10’设置序号后可以替换相应序号的存储数据
+(NSData *)GotoBlink:(int)num;

#pragma mark - 闪烁数据传输--num为存储序号，与进入闪烁设置相对应范围为0x00到0x09；head格式：0x1rgb0000定义rgb三色是增加还是减少，Sr、Sg、Sb为rgb三色起始颜色，Dr、Dg、Db为rgb最终到达颜色。注：如果rgb中的颜色增量为零将使该模式异常无法使用！将返回错误标示
+(NSData *)blinkDataArray:(NSMutableArray *)muArray;

#pragma mark - 设置开机模式 -- mode为开机模式，范围为‘0’到‘9’设置后开机将执行该模式。模式说明：mode=‘0’为手机定义模式；mode=‘1’为正常开机长亮设定颜色模式；mode=‘2’为预定义保存闪烁模式1
+(NSData *)setDeviceStart:(int)model;

#pragma mark - 查询当前状态
+(NSData *)requestDevicesStatus:(int)number;

#pragma mark - 开始温度检测
+(NSData *)startTemperatureDetection;
#pragma mark - 停止检测
+(NSData *)stopTemperatureDetection;
#pragma mark - 请求温度日志
/**
 *   响应结果：log:mac:tim1:tim2:checksum:data
 */
+(NSData *)requestTemperatureLog;

#pragma mark - 发送指令
+(void)setWriteArray:(NSArray *) array;
+(void)writeInstructionData:(NSData *)data1;
+(void)writeBleData:(NSData *)data peripheral:(CBPeripheral *)peripheral;


@end

NS_ASSUME_NONNULL_END
