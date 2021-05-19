//
//  ShareData.m
//  OpenGroup 0.0.1
//
//  Created by TestIOS on 15-4-2.
//  Copyright (c) 2015年 XiaMen_Leadhead. All rights reserved.
//

#import "ShareData.h"

BluetoothState bluetoothState;
BluetoothFailState bluetoothFailState;

int changeTheme = 0;
int brightValue = 0;
int minCurrenValue = 0;
int timerValue = 0;
int colorAlpha = 1;
int colorR = 255;
int colorG = 255;
int colorB = 255;
float colorRScale = 1.0;
float colorGScale = 1.0;
float colorBScale = 1.0;
NSString * bleVersion = nil;
CBPeripheral * discoveredPeripheral=nil;
CBCharacteristic * writeCharachter = nil;
NSArray* writeCharachterMuArray = nil;
BOOL isOpenProfiles = NO;
BOOL isOpenTempCheck = NO;
BOOL isSelectColorPalette = YES;
