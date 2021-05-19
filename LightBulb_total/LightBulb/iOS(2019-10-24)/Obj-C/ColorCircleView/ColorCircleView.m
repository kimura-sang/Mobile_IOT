//
//  ColorCircleView.m
//  MLight
//
//  Created by guest on 16/7/13.
//  Copyright © 2016年 guest. All rights reserved.
//

#import "ColorCircleView.h"

@implementation ColorCircleView


-(instancetype)initWithFrame:(CGRect)frame {

    self = [super initWithFrame:frame];
    
    if (self) {
        
        [self initColorCircle];
    }
    return self;
    
}

-(UIColor *)color{

    if (!_selecCcolor) {
        _selecCcolor = [[UIColor alloc]init];
    }
    return _selecCcolor;
}

-(void)setColorImageSize: (CGRect)rect {
    self.colorCircleImageView.frame = rect;
    self.colorCircleImageView.layer.cornerRadius = self.colorCircleImageView.frame.size.width / 2;
    self.colorCircleImageView.clipsToBounds = YES;
}


-(void)initColorCircle{
    
    self.colorCircleImageView = [[UIImageView alloc]init];
    self.colorCircleImageView.layer.masksToBounds = YES;
    self.colorCircleImageView.image = [UIImage imageNamed:@"ColorCircle"];
    //Automatically adapt, keep the image aspect ratio
    self.colorCircleImageView.contentMode = UIViewContentModeScaleAspectFit;
    
    self.colorCircleImageView.userInteractionEnabled = YES;
    
    self.backgroundColor = [UIColor clearColor];
    
    //self.backgroundColor = [UIColor whiteColor];
    
    [self addSubview:self.colorCircleImageView];
    
//    [self.colorCircleImageView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.left.equalTo(self).offset(0);
//        make.top.equalTo(self).offset(0);
//        make.size.mas_equalTo(CGSizeMake(self.frame.size.width, self.frame.size.width));
//    }];

}

#pragma mark - Get the image pixel color

#pragma mark - Create a work area with points

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    
}

#pragma mark - mobile
-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    
    
    UITouch * touch = [touches anyObject];
    //The position inside the control
    CGPoint location = [touch locationInView:self.colorCircleImageView];
    //The center of the control
    CGFloat centerX = self.bounds.size.width / 2;
    CGFloat centerY = self.bounds.size.height / 2;
    //The current position to the center of the distance
    CGFloat distance = sqrt((centerX - location.x) * (centerX - location.x) + (centerY - location.y) * (centerY - location.y));
    //If the distance is less than the radius is the effective area
    if(distance < self.bounds.size.width / 2) {
        //Change the color
        CGPoint pointtwo = [touch locationInView:self.colorCircleImageView];  //Get the last point of contact after sliding
        
        if(fabs(pointtwo.x-self.startPoint.x)>40)
        {  //Determine the distance between two points
            self.slider = YES;
            self.startPoint = pointtwo;
            self.selecCcolor = [self colorAtPixel:location];
            [self sendActionsForControlEvents:UIControlEventValueChanged];
        }
    }
}


#pragma mark - End
-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{

    UITouch * touch = [touches anyObject];
    //The position inside the control
    CGPoint location = [touch locationInView:self.colorCircleImageView];
    
    if (self.slider == YES) {
        self.slider = NO;
        
        return;
    }else{
        NSLog(@"slide");
        self.selecCcolor = [self colorAtPixel:location];
        [self sendActionsForControlEvents:UIControlEventValueChanged];
    }
}

#pragma mark - Get the color
- (UIColor *)colorAtPixel:(CGPoint)point {
    unsigned char pixel[4] = {0};
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(pixel, 1, 1, 8, 4, colorSpace, (CGBitmapInfo)kCGImageAlphaPremultipliedLast);
    
    CGContextTranslateCTM(context, -point.x, -point.y);
    
    [self.layer renderInContext:context];
    
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    UIColor *color = [UIColor colorWithRed:pixel[0]/255.0 green:pixel[1]/255.0 blue:pixel[2]/255.0 alpha:pixel[3]/255.0];
    
    return color;
}

@end
