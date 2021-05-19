#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "ESPTouchDelegate.h"
#import "ESPTouchResult.h"
#import "ESPDataCode.h"
#import "ESPDatumCode.h"
#import "ESPGuideCode.h"
#import "ESPTouchGenerator.h"
#import "ESPTouchTask.h"
#import "ESPTouchTaskParameter.h"
#import "ESPUDPSocketClient.h"
#import "ESPUDPSocketServer.h"
#import "ESPAES.h"
#import "ESPVersionMacro.h"
#import "ESP_ByteUtil.h"
#import "ESP_CRC8.h"
#import "ESP_NetUtil.h"
#import "ESP_WifiUtil.h"

FOUNDATION_EXPORT double EspTouchVersionNumber;
FOUNDATION_EXPORT const unsigned char EspTouchVersionString[];

