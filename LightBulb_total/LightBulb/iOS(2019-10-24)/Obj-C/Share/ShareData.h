//
//  ShareData.h
//  OpenGroup 0.0.1
//
//  Created by TestIOS on 15-4-2.
//  Copyright (c) 2015年 XiaMen_Leadhead. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <QuartzCore/QuartzCore.h>
#import "ShareData.h"
#import "ShareFunction.h"
//#import "TopNavView.h"

typedef NS_ENUM(NSInteger, BluetoothState){
    BluetoothStateDisconnect = 0,
    BluetoothStateScanSuccess,
    BluetoothStateScaning,
    BluetoothStateConnected,
    BluetoothStateConnecting
};

typedef NS_ENUM(NSInteger, BluetoothFailState){
    BluetoothFailStateUnExit = 0,
    BluetoothFailStateUnKnow,
    BluetoothFailStateByHW,
    BluetoothFailStateByOff,
    BluetoothFailStateUnauthorized,
    BluetoothFailStateByTimeout
};
typedef NS_ENUM(NSInteger, JumpingState){
    JumpingStateBegin = 0,
    JumpingStateJumping,
    JumpingStateFinish,
    JumpingStateNextGroup,
    JumpingStateDisConnect,
    JumpingStateComplete
};

extern int changeTheme;
extern int brightValue;
extern int minCurrenValue;
extern int timerValue;
extern int colorAlpha;
extern int colorR;
extern int colorG;
extern int colorB;
extern float colorRScale;
extern float colorGScale;
extern float colorBScale;
extern NSString * bleVersion;
extern BluetoothState bluetoothState;
extern BluetoothFailState bluetoothFailState;
extern CBPeripheral * discoveredPeripheral;
extern CBCharacteristic * writeCharachter;
extern NSArray * writeCharachterMuArray;
extern BOOL isOpenProfiles;
extern BOOL isOpenTempCheck;
extern BOOL isSelectColorPalette;
