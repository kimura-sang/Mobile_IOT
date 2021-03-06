//
//  FXRecordArcView.h
//  FXRecordArc
//
//  Created by 方 霄 on 14-6-10.
//  Copyright (c) 2014年 方 霄. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import <CoreBluetooth/CoreBluetooth.h>

#define MAX_RECORD_DURATION 60000.0
#define WAVE_UPDATE_FREQUENCY   0.1
#define SILENCE_VOLUME   45.0
#define SOUND_METER_COUNT  6
#define HUD_SIZE  [[UIScreen mainScreen] bounds].size.width

@class FXRecordArcView;
@protocol FXRecordArcViewDelegate <NSObject>

- (void)recordArcView:(FXRecordArcView *)arcView voiceRecorded:(NSString *)recordPath length:(float)recordLength;
- (void)DeviceDisconnected;
@end

@interface FXRecordArcView : UIView<AVAudioRecorderDelegate>
@property(weak, nonatomic) id<FXRecordArcViewDelegate> delegate;

@property (nonatomic,strong) CBPeripheral *device;
@property (nonatomic,strong) NSArray * peripheralArray;

- (void)startForFilePath:(NSString *)filePath;
- (void)commitRecording;

@end
