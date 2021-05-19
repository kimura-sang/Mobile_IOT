//
//  ColorCircleView.h
//  MLight
//
//  Created by guest on 16/7/13.
//  Copyright © 2016年 guest. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../Masonry/MASConstraintMaker.h"


@interface ColorCircleView : UIControl

@property (nonatomic,strong) UIImageView * colorCircleImageView;

@property (nonatomic,strong) UIColor * selecCcolor;

@property (nonatomic,assign) CGPoint startPoint;
@property (nonatomic,assign) BOOL slider;

-(void)setColorImageSize: (CGRect)rect;


@end
