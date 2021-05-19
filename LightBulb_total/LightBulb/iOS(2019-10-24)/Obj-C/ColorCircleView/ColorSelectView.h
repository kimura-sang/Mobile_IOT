//
//  ColorSelectView.h
//  全彩照明
//
//  Created by kejin HE on 2018/12/4.
//  Copyright © 2018 mlight. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Share/ShareData.h"
#import "../PrefixHeader.pch"

NS_ASSUME_NONNULL_BEGIN

@protocol ColorSelectViewDelegate <NSObject>

-(void)backColor:(UIColor *)color andTag:(int)tag;
-(void)saveColor:(int)tag;

@end

@interface ColorSelectView : UIView

@property (nonatomic,strong) NSArray * colorArray;

@property (nonatomic,strong) NSArray * nameArray;

@property (nonatomic,weak) id<ColorSelectViewDelegate>delegate;

-(void)createButton;

@end

NS_ASSUME_NONNULL_END
