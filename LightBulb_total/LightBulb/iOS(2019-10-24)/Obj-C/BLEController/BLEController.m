//
//  BLEController.m
//  全彩照明
//
//  Created by kejin HE on 2018/12/3.
//  Copyright © 2018 mlight. All rights reserved.
//

#import "BLEController.h"
#import "../Share/ShareData.h"

@implementation BLEController

#pragma mark - 颜色调节和亮度调节
+(NSData *)bleColorAlpha:(int)alpha andRed:(int)red andGreen:(int)green andBlue:(int)blue{
    NSData * data = [[NSData alloc]init];
    NSString * colorStr = [NSString stringWithFormat:@"ctl:%d:%d:%d:%d:",alpha,red,green,blue];
    data = [colorStr dataUsingEncoding:NSUTF8StringEncoding];

    NSLog(@"调节颜色的值：%@",data);
    return data;
}

#pragma mark - 保存当前颜色
+(NSData *)savecurrenColor{
    NSData * data = [[NSData alloc]init];
    NSString * saveStr = [NSString stringWithFormat:@"save"];
    data = [saveStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}
#pragma mark - 修改名字
+(NSData *)changeBleName:(NSString *)name{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"sn:%@",name];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 开灯
+(NSData *)openBle{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"%@",@"open"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 逛灯
+(NSData *)closeBle{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"%@",@"close"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 开关灯
+(NSData *)switchLight:(BOOL)isOpen{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr;
    if (isOpen) {
        nameStr = [NSString stringWithFormat:@"%@",@"open"];
    }
    else{
        nameStr = [NSString stringWithFormat:@"%@",@"close"];
    }
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    
    return data;
}

#pragma mark - 密码
+(NSData *)blePasswork:(NSString *)passwork{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"mk:%@",passwork];
    NSLog(@"Blecontroller 发送密码：%@",nameStr);
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 修改密码
+(NSData *)changeOldPasswork:(NSString *)oldPasswork andNewPasswork:(NSString *)newPasswork{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"mg:%@:%@",oldPasswork,newPasswork];
    NSLog(@"Blecontroller界面更换密码：%@",nameStr);
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    data = [data subdataWithRange:NSMakeRange(0, 12)];
    NSLog(@"测试更换密码：%@",data);
    return data;
}

#pragma mark - 延时关灯
+(NSData *)delayTimeTurnOffBle:(NSString *)time{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"de:%@",time];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 延时开灯
+(NSData *)delayTimeOpenBle:(NSString *)time{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"dl:%@",time];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 查询延时时间
+(NSData *)queryDelayTime{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"req:d"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 关闭延时开灯
+(NSData *)closeDelayTimeOpenBle{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"dl off"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 关闭延时关灯
+(NSData *)closeDelayTimeTurnOffBle{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"de off"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 请求版本
+(NSData *)requestVersion{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"%@",@"version"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 律动模式开启
+(NSData *)musicOn{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"%@",@"music on"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
    
}
#pragma mark - 律动模式关闭
+(NSData *)musicOff{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"%@",@"music off"];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 渐变模式
+(NSData *)colorFocusRGB:(NSMutableArray *)array{
    Byte bytes[8];
    bytes[0] = (Byte)([array[0] intValue]);
    bytes[1] = (Byte)([array[1] intValue]);
    bytes[2] = (Byte)([array[2] intValue]);
    bytes[3] = (Byte)([array[3] intValue]);
    bytes[4] = (Byte)([array[4] intValue]);
    bytes[5] = (Byte)([array[5] intValue]);
    bytes[6] = (Byte)([array[6] intValue]);
    bytes[7] = (Byte)([array[7] intValue]);
    NSData *data = [NSData dataWithBytes:bytes length:8];
    return data;
}


+(NSData *)hexToBytes:(NSString *)str {
    NSMutableData* data = [NSMutableData data];
    int idx;
    for (idx = 0; idx+2 <= str.length; idx+=2) {
        NSRange range = NSMakeRange(idx, 2);
        NSString* hexStr = [str substringWithRange:range];
        NSScanner* scanner = [NSScanner scannerWithString:hexStr];
        unsigned int intValue;
        [scanner scanHexInt:&intValue];
        [data appendBytes:&intValue length:1];
    }
    
    NSLog(@"hexToBytes:data:%@",data);
    return data;
}

+(NSData *)dataWithHexString:(NSString *)hexString {
    int j=0;
    Byte bytes[8];  ///3ds key的Byte 数组， 128位
    for(int i=0;i<[hexString length];i++) {
        int int_ch;  /// 两位16进制数转化后的10进制数
        unichar hex_char1 = [hexString characterAtIndex:i]; ////两位16进制数中的第一位(高位*16)
        int int_ch1;
        if(hex_char1 >= '0' && hex_char1 <='9')
            int_ch1 = (hex_char1-48)*16;   //// 0 的Ascll - 48
        else if(hex_char1 >= 'A' && hex_char1 <='F')
            int_ch1 = (hex_char1-55)*16; //// A 的Ascll - 65
        else
            int_ch1 = (hex_char1-87)*16; //// a 的Ascll - 97
        
        i++;
        unichar hex_char2 = [hexString characterAtIndex:i]; ///两位16进制数中的第二位(低位)
        int int_ch2;
        if(hex_char2 >= '0' && hex_char2 <='9')
            int_ch2 = (hex_char2-48); //// 0 的Ascll - 48
        else if(hex_char1 >= 'A' && hex_char1 <='F')
            int_ch2 = hex_char2-55; //// A 的Ascll - 65
        else
            int_ch2 = hex_char2-87; //// a 的Ascll - 97
        
        int_ch = int_ch1+int_ch2;
        NSLog(@"int_ch=%d",int_ch);
        
        bytes[j] = int_ch;  ///将转化后的数放入Byte数组里
        j++;
    }
    NSData *newData = [[NSData alloc] initWithBytes:bytes length:8];
    NSLog(@"dataWithHexString:newData=%@",newData);
    return newData;
}
#pragma mark - 定时开灯
+(NSData *)TimingOpenLight:(NSString *)timeStr{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"dl:%@",timeStr];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 进入闪烁模式  num为存储序号，num范围为‘0’到‘10’设置序号后可以替换相应序号的存储数据
+(NSData *)GotoBlink:(int)num{
    NSData * data = [[NSData alloc]init];
    NSString * nameStr = [NSString stringWithFormat:@"rec:%@",[NSString stringWithFormat:@"%d",num]];
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    return data;
}

#pragma mark - 闪烁数据传输--num为存储序号，与进入闪烁设置相对应范围为0x00到0x09；head格式：0x1rgb0000定义rgb三色是增加还是减少，Sr、Sg、Sb为rgb三色起始颜色，Dr、Dg、Db为rgb最终到达颜色。注：如果rgb中的颜色增量为零将使该模式异常无法使用！将返回错误标示
+(NSData *)blinkDataArray:(NSMutableArray *)muArray{
    NSData * data = [[NSData alloc]init];
    NSString * dataString = [[NSString alloc]init];
    for (int i = 0; i < muArray.count ; i++) {
        dataString = [dataString stringByAppendingString:muArray[i]];
    }
    data = [self hexToBytes:dataString];
    return data;
}

#pragma mark - 设置开机模式 -- mode为开机模式，范围为‘0’到‘9’设置后开机将执行该模式。模式说明：mode=‘0’为手机定义模式；mode=‘1’为正常开机长亮设定颜色模式；mode=‘2’为预定义保存闪烁模式1
+(NSData *)setDeviceStart:(int)model{
    
    NSData * data = [[NSData alloc]init];
    
    NSString * nameStr = [NSString stringWithFormat:@"device start%@",[NSString stringWithFormat:@"%d",model]];
    
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    
    return data;
}


#pragma mark - 查询当前状态
+(NSData *)requestDevicesStatus:(int)number{
    
    NSData * data = [[NSData alloc]init];
    
    NSString * nameStr = [NSString stringWithFormat:@"req:s%@",[NSString stringWithFormat:@"%d",number]];
    
    data = [nameStr dataUsingEncoding:NSUTF8StringEncoding];
    
    return data;
}

#pragma mark - 开始温度检测
+(NSData *)startTemperatureDetection {
//    NSData * dataClog = [@"clog:" dataUsingEncoding:NSUTF8StringEncoding];
//    NSData * dataMao = [@":" dataUsingEncoding:NSUTF8StringEncoding];
//    NSData * dataNowTime = [ShareFunction convertHexStrToData:[ShareFunction tenToHex:[ShareFunction backNowTimeSecond]]];
//    NSData * dataContinueTime = [ShareFunction convertHexStrToData:[ShareFunction tenToHex:TIME_24]];
    NSMutableData * muData = [[NSMutableData alloc]init];
//    [muData appendData:dataClog];
//    [muData appendData:dataNowTime];
//    [muData appendData:dataMao];
//    [muData appendData:dataContinueTime];
    
//    NSLog(@"得出结果:%@",muData);
    return muData;
}
#pragma mark - 停止检测
+(NSData *)stopTemperatureDetection{
    NSData * data = [[NSData alloc]init];
    NSString * string = [NSString stringWithFormat:@"slog"];
    data = [string dataUsingEncoding:NSUTF8StringEncoding];
    
    return data;
}
#pragma mark - 请求温度日志
/**
 *   响应结果：log:mac:tim1:tim2:checksum:data
 */
+(NSData *)requestTemperatureLog{
    NSData * data = [[NSData alloc]init];
    NSString * string = [NSString stringWithFormat:@"log"];
    data = [string dataUsingEncoding:NSUTF8StringEncoding];
    
    return data;
}

+(void)setWriteArray:(NSArray *) array {
    writeCharachterMuArray = array;
}

#pragma mark - 发送指令
+(void)writeInstructionData:(NSData *)data1{
    //开通一个子线程
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        if (writeCharachterMuArray.count != 0) {
            //进行写的控制
            for (int i = 0; i < writeCharachterMuArray.count; i++) {
                CBPeripheral * model = writeCharachterMuArray[i];
                for (CBService * service in model.services) {
                    for (CBCharacteristic * characterstic in service.characteristics) {
                        // 耗时的操作
                        dispatch_async(dispatch_get_main_queue(), ^{
                            // 更新界面
                            if ([characterstic.UUID isEqual:[CBUUID UUIDWithString:UUID_CHARACTERISTIC]]) {
                                if (characterstic.properties & CBCharacteristicPropertyWrite) {
                                    [model writeValue:data1 forCharacteristic:characterstic type:CBCharacteristicWriteWithResponse];
                                }
                            }
                        });
                    }
                }
            }
        }
    });
}

+(void)writeBleData:(NSData *)data peripheral:(CBPeripheral *)peripheral{
    for (CBService * service in peripheral.services) {
        for (CBCharacteristic * characterstic in service.characteristics) {
            // 耗时的操作
            dispatch_async(dispatch_get_main_queue(), ^{
                // 更新界面
                if ([characterstic.UUID isEqual:[CBUUID UUIDWithString:UUID_CHARACTERISTIC]]) {
                    if (characterstic.properties & CBCharacteristicPropertyWrite) {
                        [peripheral writeValue:data forCharacteristic:characterstic type:CBCharacteristicWriteWithResponse];
                    }
                }
            });
        }
    }
}

@end
