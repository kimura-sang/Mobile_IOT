//
//  ColorSelectView.m
//  全彩照明
//
//  Created by kejin HE on 2018/12/4.
//  Copyright © 2018 mlight. All rights reserved.
//

#import "ColorSelectView.h"
#define Start_X 20.0f           // 第一个按钮的X坐标
#define Start_Y 0.0f           // 第一个按钮的Y坐标
#define Height_Space 10.0f      // 竖间距
#define Button_Height 70  // 高
#define Button_Width 70     // 宽
#define Label_Height 25

#define UIColorFromRGB(rgbHex) [UIColor colorWithRed:((float)((rgbHex & 0xFF0000) >> 16))/255.0 green:((float)((rgbHex & 0xFF00) >> 8))/255.0 blue:((float)(rgbHex & 0xFF))/255.0 alpha:1.0]


@implementation ColorSelectView

#pragma mark - 颜色数组
//-(NSArray *)colorArray{
//
//    if (!_colorArray) {
//        _colorArray = [[NSArray alloc]init];
//        _colorArray = @[UIColorFromRGB(0xFF6A58),
//                        UIColorFromRGB(0x8BC1FF),
//                        UIColorFromRGB(0xF5E1E1),
//                        UIColorFromRGB(0xFF8B2D),
//                        [UIColor whiteColor],
//                        [UIColor whiteColor],
//                        [UIColor whiteColor],
//                        [UIColor whiteColor]];
//    }
//    return _colorArray;
//}

-(NSArray *)nameArray{
    
    if (!_nameArray) {
        _nameArray = [[NSArray alloc]init];
        _nameArray = @[@"Warm", @"Cool", @"Day", @"Sunset", @"Fav1", @"Fav2", @"Fav3", @"Fav4"];
    }
    return _nameArray;
}

-(instancetype)initWithFrame:(CGRect)frame{

    self  = [super initWithFrame:frame];
//    if (self) {
//        [self createButton];
//    }
    return self;
}


-(void)createButton{
    CGFloat Width_Space = self.frame.size.width / 4 - 70;
    
    for (int i = 0 ; i < 8; i++) {
        NSInteger index = i % 4;
        NSInteger page = i / 4;
        
        // 圆角按钮
        UIButton *aBt = [UIButton buttonWithType:UIButtonTypeCustom];
        if (page == 0) {
            [aBt setBackgroundImage:[UIImage imageNamed:self.nameArray[i]] forState:UIControlStateNormal];
        } else {
            aBt.backgroundColor = self.colorArray[i];
        }
        aBt.layer.masksToBounds = YES;
        [aBt.layer setCornerRadius:Button_Height/2];
        aBt.layer.borderWidth = 0.5;
        aBt.layer.borderColor = [UIColor lightGrayColor].CGColor;
        aBt.tag = i+100;
        
        // click delegate
        [aBt addTarget: self action:@selector(clickColor:) forControlEvents:UIControlEventTouchUpInside];
        
        // long click delegate
        [aBt addGestureRecognizer:[[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longClickColor:)]];
        
        aBt.frame = CGRectMake((Width_Space)/2*(index+1) + (Width_Space-(Width_Space)/2+Button_Width)*index, page  * (Label_Height + Button_Height + Height_Space - 5)+Start_Y, Button_Width, Button_Height);
        
        [self addSubview:aBt];
        
        UIView * labelView = [[UIView alloc]init];
        
        // 圆角按钮
        UILabel *lbl = [[UILabel alloc]init];
        [lbl setText:self.nameArray[i]];
        lbl.font = [UIFont systemFontOfSize:16];
        lbl.textColor = [UIColor darkGrayColor];
        lbl.textAlignment = NSTextAlignmentCenter;
        lbl.frame = CGRectMake((Width_Space)/2*(index+1) + (Width_Space-(Width_Space)/2+Button_Width)*index, Button_Height + page  * (Label_Height + Button_Height + Height_Space - 5)+Start_Y, Button_Width, Label_Height);
        
        [labelView addSubview:lbl];
        [self addSubview:labelView];
    }
}

-(void)clickColor:(UIButton *)buttom{
    if ([self.delegate respondsToSelector:@selector(backColor:andTag:)]) {
        [self.delegate backColor:[self.colorArray objectAtIndex:buttom.tag-100] andTag:(int)buttom.tag];
    }
}

-(void)longClickColor:(UILongPressGestureRecognizer *) gesture {
    if (gesture.state == UIGestureRecognizerStateEnded) {
        if (gesture.view.tag < 104) {
            return;
        }
        
//        NSLog(@"%d", colorR);
//        printf("%d\n", colorR);
        
        UIButton *aBt = (UIButton *)gesture.view;
        if (isSelectColorPalette == YES) {
            aBt.backgroundColor = [UIColor colorWithRed:(colorR * colorRScale/255.0) green:(colorG * colorGScale/255.0) blue:(colorB * colorBScale/255.0) alpha:(colorAlpha/1.0)];
        } else {
            aBt.backgroundColor = [UIColor colorWithRed:(colorR/255.0) green:(colorG/255.0) blue:(colorB/255.0) alpha:(colorAlpha/1.0)];
        }

        if ([self.delegate respondsToSelector:@selector(saveColor:)]) {
            [self.delegate saveColor:(int)gesture.view.tag - 104];
        }
    }
}

@end
