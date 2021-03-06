/*****
 * Tencent is pleased to support the open source community by making QMUI_iOS available.
 * Copyright (C) 2016-2019 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/MIT
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *****/

//
//  QMUIPopupContainerView.m
//  qmui
//
//  Created by QMUI Team on 15/12/17.
//

#import "QMUIPopupContainerView.h"
#import "QMUICore.h"
#import "QMUICommonViewController.h"
#import "UIViewController+QMUI.h"
#import "QMUILog.h"
#import "UIView+QMUI.h"
#import "UIWindow+QMUI.h"
#import "UIBarItem+QMUI.h"

@interface QMUIPopupContainerViewWindow : UIWindow

@end

@interface QMUIPopContainerViewController : QMUICommonViewController

@end

@interface QMUIPopContainerMaskControl : UIControl

@property(nonatomic, weak) QMUIPopupContainerView *popupContainerView;
@end

@interface QMUIPopupContainerView (UIAppearance)

- (void)updateAppearance;
@end

@interface QMUIPopupContainerView () {
    UIImageView                     *_imageView;
    UILabel                         *_textLabel;
}

@property(nonatomic, strong) QMUIPopupContainerViewWindow *popupWindow;
@property(nonatomic, weak) UIWindow *previousKeyWindow;
@property(nonatomic, assign) BOOL hidesByUserTap;
@end

@implementation QMUIPopupContainerView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self didInitialize];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    if (self = [super initWithCoder:aDecoder]) {
        [self didInitialize];
    }
    return self;
}

- (void)dealloc {
    _sourceView.qmui_frameDidChangeBlock = nil;
}

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.contentMode = UIViewContentModeCenter;
        [self.contentView addSubview:_imageView];
    }
    return _imageView;
}

- (UILabel *)textLabel {
    if (!_textLabel) {
        _textLabel = [[UILabel alloc] init];
        _textLabel.font = UIFontMake(12);
        _textLabel.textColor = UIColorBlack;
        _textLabel.numberOfLines = 0;
        [self.contentView addSubview:_textLabel];
    }
    return _textLabel;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *result = [super hitTest:point withEvent:event];
    if (result == self.contentView) {
        return self;
    }
    return result;
}

- (void)setBackgroundColor:(UIColor *)backgroundColor {
    _backgroundColor = backgroundColor;
    _backgroundLayer.fillColor = _backgroundColor.CGColor;
}

- (void)setMaskViewBackgroundColor:(UIColor *)maskViewBackgroundColor {
    _maskViewBackgroundColor = maskViewBackgroundColor;
    if (self.popupWindow) {
        self.popupWindow.rootViewController.view.backgroundColor = maskViewBackgroundColor;
    }
}

- (void)setShadowColor:(UIColor *)shadowColor {
    _shadowColor = shadowColor;
    _backgroundLayer.shadowColor = shadowColor.CGColor;
}

- (void)setBorderColor:(UIColor *)borderColor {
    _borderColor = borderColor;
    _backgroundLayer.strokeColor = borderColor.CGColor;
}

- (void)setBorderWidth:(CGFloat)borderWidth {
    _borderWidth = borderWidth;
    _backgroundLayer.lineWidth = _borderWidth;
}

- (void)setCornerRadius:(CGFloat)cornerRadius {
    _cornerRadius = cornerRadius;
    [self setNeedsLayout];
}

- (void)setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    if (self.highlightedBackgroundColor) {
        _backgroundLayer.fillColor = highlighted ? self.highlightedBackgroundColor.CGColor : self.backgroundColor.CGColor;
    }
}

- (CGSize)sizeThatFits:(CGSize)size {
    size.width = MIN(size.width, CGRectGetWidth(self.superview.bounds) - UIEdgeInsetsGetHorizontalValue(self.safetyMarginsOfSuperview));
    size.height = MIN(size.height, CGRectGetHeight(self.superview.bounds) - UIEdgeInsetsGetVerticalValue(self.safetyMarginsOfSuperview));
    
    CGSize contentLimitSize = [self contentSizeInSize:size];
    CGSize contentSize = [self sizeThatFitsInContentView:contentLimitSize];
    CGSize resultSize = [self sizeWithContentSize:contentSize sizeThatFits:size];
    return resultSize;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGSize arrowSize = self.arrowSize;
    CGRect roundedRect = CGRectMake(self.borderWidth / 2.0, self.borderWidth / 2.0 + (self.currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionAbove ? 0 : arrowSize.height), CGRectGetWidth(self.bounds) - self.borderWidth, CGRectGetHeight(self.bounds) - arrowSize.height - self.borderWidth);
    CGFloat cornerRadius = self.cornerRadius;
    
    CGPoint leftTopArcCenter = CGPointMake(CGRectGetMinX(roundedRect) + cornerRadius, CGRectGetMinY(roundedRect) + cornerRadius);
    CGPoint leftBottomArcCenter = CGPointMake(leftTopArcCenter.x, CGRectGetMaxY(roundedRect) - cornerRadius);
    CGPoint rightTopArcCenter = CGPointMake(CGRectGetMaxX(roundedRect) - cornerRadius, leftTopArcCenter.y);
    CGPoint rightBottomArcCenter = CGPointMake(rightTopArcCenter.x, leftBottomArcCenter.y);
    
    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(leftTopArcCenter.x, CGRectGetMinY(roundedRect))];
    [path addArcWithCenter:leftTopArcCenter radius:cornerRadius startAngle:M_PI * 1.5 endAngle:M_PI clockwise:NO];
    [path addLineToPoint:CGPointMake(CGRectGetMinX(roundedRect), leftBottomArcCenter.y)];
    [path addArcWithCenter:leftBottomArcCenter radius:cornerRadius startAngle:M_PI endAngle:M_PI * 0.5 clockwise:NO];
    
    if (self.currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionAbove) {
        // ?????????????????????????????????????????????????????????
        [path addLineToPoint:CGPointMake(_arrowMinX, CGRectGetMaxY(roundedRect))];
        [path addLineToPoint:CGPointMake(_arrowMinX + arrowSize.width / 2, CGRectGetMaxY(roundedRect) + arrowSize.height)];
        [path addLineToPoint:CGPointMake(_arrowMinX + arrowSize.width, CGRectGetMaxY(roundedRect))];
    }
    
    [path addLineToPoint:CGPointMake(rightBottomArcCenter.x, CGRectGetMaxY(roundedRect))];
    [path addArcWithCenter:rightBottomArcCenter radius:cornerRadius startAngle:M_PI * 0.5 endAngle:0.0 clockwise:NO];
    [path addLineToPoint:CGPointMake(CGRectGetMaxX(roundedRect), rightTopArcCenter.y)];
    [path addArcWithCenter:rightTopArcCenter radius:cornerRadius startAngle:0.0 endAngle:M_PI * 1.5 clockwise:NO];
    
    if (self.currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionBelow) {
        // ????????????
        [path addLineToPoint:CGPointMake(_arrowMinX + arrowSize.width, CGRectGetMinY(roundedRect))];
        [path addLineToPoint:CGPointMake(_arrowMinX + arrowSize.width / 2, CGRectGetMinY(roundedRect) - arrowSize.height)];
        [path addLineToPoint:CGPointMake(_arrowMinX, CGRectGetMinY(roundedRect))];
    }
    [path closePath];
    
    _backgroundLayer.path = path.CGPath;
    _backgroundLayer.shadowPath = path.CGPath;
    _backgroundLayer.frame = self.bounds;
    
    [self layoutDefaultSubviews];
}

- (void)layoutDefaultSubviews {
    self.contentView.frame = CGRectMake(self.borderWidth + self.contentEdgeInsets.left, (self.currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionAbove ? self.borderWidth : self.arrowSize.height + self.borderWidth) + self.contentEdgeInsets.top, CGRectGetWidth(self.bounds) - self.borderWidth * 2 - UIEdgeInsetsGetHorizontalValue(self.contentEdgeInsets), CGRectGetHeight(self.bounds) - self.arrowSize.height - self.borderWidth * 2 - UIEdgeInsetsGetVerticalValue(self.contentEdgeInsets));
    // contentView???????????????????????????path????????????????????????????????????????????????self.contentEdgeInsets.left???self.cornerRadius????????????????????????contentView?????????????????????
    // ??????????????????????????????contentView?????????????????????????????????????????????????????????????????????
    CGFloat contentViewCornerRadius = fabs(MIN(CGRectGetMinX(self.contentView.frame) - self.cornerRadius, 0));
    self.contentView.layer.cornerRadius = contentViewCornerRadius;
    
    BOOL isImageViewShowing = [self isSubviewShowing:_imageView];
    BOOL isTextLabelShowing = [self isSubviewShowing:_textLabel];
    if (isImageViewShowing) {
        [_imageView sizeToFit];
        _imageView.frame = CGRectSetXY(_imageView.frame, self.imageEdgeInsets.left, CGFloatGetCenter(CGRectGetHeight(self.contentView.bounds), CGRectGetHeight(_imageView.frame) + self.imageEdgeInsets.top));
    }
    if (isTextLabelShowing) {
        CGFloat textLabelMinX = (isImageViewShowing ? ceil(CGRectGetMaxX(_imageView.frame) + self.imageEdgeInsets.right) : 0) + self.textEdgeInsets.left;
        CGSize textLabelLimitSize = CGSizeMake(ceil(CGRectGetWidth(self.contentView.bounds) - textLabelMinX), ceil(CGRectGetHeight(self.contentView.bounds) - self.textEdgeInsets.top - self.textEdgeInsets.bottom));
        CGSize textLabelSize = [_textLabel sizeThatFits:textLabelLimitSize];
        CGPoint textLabelOrigin = CGPointMake(textLabelMinX, flat(CGFloatGetCenter(CGRectGetHeight(self.contentView.bounds), ceil(textLabelSize.height)) + self.textEdgeInsets.top));
        _textLabel.frame = CGRectMake(textLabelOrigin.x, textLabelOrigin.y, textLabelLimitSize.width, ceil(textLabelSize.height));
    }
}

- (void)setSourceBarItem:(__kindof UIBarItem *)sourceBarItem {
    _sourceBarItem = sourceBarItem;
    __weak __typeof(self)weakSelf = self;
    if (!sourceBarItem.qmui_viewLayoutDidChangeBlock) {
        sourceBarItem.qmui_viewLayoutDidChangeBlock = ^(__kindof UIBarItem * _Nonnull item, UIView * _Nullable view) {
            if (!view.window || !weakSelf.superview) return;
            UIView *convertToView = weakSelf.popupWindow ? UIApplication.sharedApplication.delegate.window : weakSelf.superview;// ????????? window ????????????????????????????????????????????????????????? window ?????????????????????????????????????????? sourceBarItem ????????? window ?????????????????? popupWindow ???????????????iOS 11 ??????????????????????????????????????????????????????????????????????????? UIApplication window
            CGRect rect = [view qmui_convertRect:view.bounds toView:convertToView];
            weakSelf.sourceRect = rect;
        };
    }
    if (sourceBarItem.qmui_view && sourceBarItem.qmui_viewLayoutDidChangeBlock) {
        sourceBarItem.qmui_viewLayoutDidChangeBlock(sourceBarItem, sourceBarItem.qmui_view);// update layout immediately
    }
}

- (void)setSourceView:(__kindof UIView *)sourceView {
    _sourceView = sourceView;
    __weak __typeof(self)weakSelf = self;
    sourceView.qmui_frameDidChangeBlock = ^(__kindof UIView * _Nonnull view, CGRect precedingFrame) {
        if (!view.window || !weakSelf.superview) return;
        UIView *convertToView = weakSelf.popupWindow ? UIApplication.sharedApplication.delegate.window : weakSelf.superview;// ????????? window ????????????????????????????????????????????????????????? window ?????????????????????????????????????????? sourceBarItem ????????? window ?????????????????? popupWindow ???????????????iOS 11 ??????????????????????????????????????????????????????????????????????????? UIApplication window
        CGRect rect = [view qmui_convertRect:view.bounds toView:convertToView];
        weakSelf.sourceRect = rect;
    };
    sourceView.qmui_frameDidChangeBlock(sourceView, sourceView.frame);// update layout immediately
}

- (void)setSourceRect:(CGRect)sourceRect {
    _sourceRect = sourceRect;
    if (self.isShowing) {
        [self layoutWithTargetRect:sourceRect];
    }
}

- (void)updateLayout {
    // call setter to layout immediately
    if (self.sourceBarItem) {
        self.sourceBarItem = self.sourceBarItem;
    } else if (self.sourceView) {
        self.sourceView = self.sourceView;
    } else {
        self.sourceRect = self.sourceRect;
    }
}

// ?????? targetRect ??? window ???????????? window ?????????????????????????????? subview ??????????????? superview ???????????????
- (void)layoutWithTargetRect:(CGRect)targetRect {
    UIView *superview = self.superview;
    if (!superview) {
        return;
    }
    
    targetRect = self.popupWindow ? [self.popupWindow convertRect:targetRect toView:superview] : targetRect;
    CGRect containerRect = superview.bounds;
    
    CGSize tipSize = [self sizeThatFits:CGSizeMake(self.maximumWidth, self.maximumHeight)];
    CGFloat preferredTipWidth = tipSize.width;
    
    // ??????tips?????????????????????self.safetyMarginsOfSuperview.left
    CGFloat a = CGRectGetMidX(targetRect) - tipSize.width / 2;
    CGFloat tipMinX = MAX(CGRectGetMinX(containerRect) + self.safetyMarginsOfSuperview.left, a);
    
    CGFloat tipMaxX = tipMinX + tipSize.width;
    if (tipMaxX + self.safetyMarginsOfSuperview.right > CGRectGetMaxX(containerRect)) {
        // ???????????????
        // ????????????????????????????????????????????????????????????????????????????????????
        CGFloat distanceCanMoveToLeft = tipMaxX - (CGRectGetMaxX(containerRect) - self.safetyMarginsOfSuperview.right);
        if (tipMinX - distanceCanMoveToLeft >= CGRectGetMinX(containerRect) + self.safetyMarginsOfSuperview.left) {
            // ??????????????????
            tipMinX -= distanceCanMoveToLeft;
        } else {
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            tipMinX = CGRectGetMinX(containerRect) + self.safetyMarginsOfSuperview.left;
            tipMaxX = CGRectGetMaxX(containerRect) - self.safetyMarginsOfSuperview.right;
            tipSize.width = MIN(tipSize.width, tipMaxX - tipMinX);
        }
    }
    
    // ?????????????????????????????????tipSize.width????????????????????????????????????????????????????????????????????????????????????sizeThatFits
    BOOL tipWidthChanged = tipSize.width != preferredTipWidth;
    if (tipWidthChanged) {
        tipSize = [self sizeThatFits:tipSize];
    }
    
    _currentLayoutDirection = self.preferLayoutDirection;
    
    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    BOOL canShowAtAbove = [self canTipShowAtSpecifiedLayoutDirect:QMUIPopupContainerViewLayoutDirectionAbove targetRect:targetRect tipSize:tipSize];
    BOOL canShowAtBelow = [self canTipShowAtSpecifiedLayoutDirect:QMUIPopupContainerViewLayoutDirectionBelow targetRect:targetRect tipSize:tipSize];
    
    if (!canShowAtAbove && !canShowAtBelow) {
        // ????????????????????????????????????????????????maximumHeight
        CGFloat maximumHeightAbove = CGRectGetMinY(targetRect) - CGRectGetMinY(containerRect) - self.distanceBetweenSource - self.safetyMarginsOfSuperview.top;
        CGFloat maximumHeightBelow = CGRectGetMaxY(containerRect) - self.safetyMarginsOfSuperview.bottom - self.distanceBetweenSource - CGRectGetMaxY(targetRect);
        self.maximumHeight = MAX(self.minimumHeight, MAX(maximumHeightAbove, maximumHeightBelow));
        tipSize.height = self.maximumHeight;
        _currentLayoutDirection = maximumHeightAbove > maximumHeightBelow ? QMUIPopupContainerViewLayoutDirectionAbove : QMUIPopupContainerViewLayoutDirectionBelow;
        
        QMUILog(NSStringFromClass(self.class), @"%@, ???????????????????????????????????????????????????????????????%@, ???????????????%@", self, @(self.maximumHeight), maximumHeightAbove > maximumHeightBelow ? @"??????" : @"??????");
        
    } else if (_currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionAbove && !canShowAtAbove) {
        _currentLayoutDirection = QMUIPopupContainerViewLayoutDirectionBelow;
    } else if (_currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionBelow && !canShowAtBelow) {
        _currentLayoutDirection = QMUIPopupContainerViewLayoutDirectionAbove;
    }
    
    CGFloat tipMinY = [self tipMinYWithTargetRect:targetRect tipSize:tipSize preferLayoutDirection:_currentLayoutDirection];
    
    // ????????????????????????????????????????????????????????????tip?????????safetyMargins??????????????????????????????
    if (_currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionAbove) {
        CGFloat tipMinYIfAlignSafetyMarginTop = CGRectGetMinY(containerRect) + self.safetyMarginsOfSuperview.top;
        tipMinY = MAX(tipMinY, tipMinYIfAlignSafetyMarginTop);
    } else if (_currentLayoutDirection == QMUIPopupContainerViewLayoutDirectionBelow) {
        CGFloat tipMinYIfAlignSafetyMarginBottom = CGRectGetMaxY(containerRect) - self.safetyMarginsOfSuperview.bottom - tipSize.height;
        tipMinY = MIN(tipMinY, tipMinYIfAlignSafetyMarginBottom);
    }
    
    self.frame = CGRectFlatMake(tipMinX, tipMinY, tipSize.width, tipSize.height);
    
    // ?????????????????????????????????
    CGPoint targetRectCenter = CGPointGetCenterWithRect(targetRect);
    CGFloat selfMidX = targetRectCenter.x - (CGRectGetMinX(containerRect) + CGRectGetMinX(self.frame));
    _arrowMinX = selfMidX - self.arrowSize.width / 2;
    [self setNeedsLayout];
    
    if (self.debug) {
        self.contentView.backgroundColor = UIColorTestGreen;
        self.borderColor = UIColorRed;
        self.borderWidth = PixelOne;
        _imageView.backgroundColor = UIColorTestRed;
        _textLabel.backgroundColor = UIColorTestBlue;
    }
}

- (CGFloat)tipMinYWithTargetRect:(CGRect)itemRect tipSize:(CGSize)tipSize preferLayoutDirection:(QMUIPopupContainerViewLayoutDirection)direction {
    CGFloat tipMinY = 0;
    if (direction == QMUIPopupContainerViewLayoutDirectionAbove) {
        tipMinY = CGRectGetMinY(itemRect) - tipSize.height - self.distanceBetweenSource;
    } else if (direction == QMUIPopupContainerViewLayoutDirectionBelow) {
        tipMinY = CGRectGetMaxY(itemRect) + self.distanceBetweenSource;
    }
    return tipMinY;
}

- (BOOL)canTipShowAtSpecifiedLayoutDirect:(QMUIPopupContainerViewLayoutDirection)direction targetRect:(CGRect)itemRect tipSize:(CGSize)tipSize {
    BOOL canShow = NO;
    CGFloat tipMinY = [self tipMinYWithTargetRect:itemRect tipSize:tipSize preferLayoutDirection:direction];
    if (direction == QMUIPopupContainerViewLayoutDirectionAbove) {
        canShow = tipMinY >= self.safetyMarginsOfSuperview.top;
    } else if (direction == QMUIPopupContainerViewLayoutDirectionBelow) {
        canShow = tipMinY + tipSize.height + self.safetyMarginsOfSuperview.bottom <= CGRectGetHeight(self.superview.bounds);
    }
    return canShow;
}

- (void)showWithAnimated:(BOOL)animated {
    [self showWithAnimated:animated completion:nil];
}

- (void)showWithAnimated:(BOOL)animated completion:(void (^)(BOOL))completion {
    
    BOOL isShowingByWindowMode = NO;
    if (!self.superview) {
        [self initPopupContainerViewWindowIfNeeded];
        
        QMUICommonViewController *viewController = (QMUICommonViewController *)self.popupWindow.rootViewController;
        viewController.supportedOrientationMask = [QMUIHelper visibleViewController].supportedInterfaceOrientations;
        
        self.previousKeyWindow = [UIApplication sharedApplication].keyWindow;
        [self.popupWindow makeKeyAndVisible];
        
        isShowingByWindowMode = YES;
    } else {
        self.hidden = NO;
    }
    
    [self updateLayout];
    
    if (self.willShowBlock) {
        self.willShowBlock(animated);
    }
    
    if (animated) {
        if (isShowingByWindowMode) {
            self.popupWindow.alpha = 0;
        } else {
            self.alpha = 0;
        }
        self.layer.transform = CATransform3DMakeScale(0.98, 0.98, 1);
        [UIView animateWithDuration:0.4 delay:0 usingSpringWithDamping:0.3 initialSpringVelocity:12 options:UIViewAnimationOptionCurveLinear animations:^{
            self.layer.transform = CATransform3DMakeScale(1, 1, 1);
        } completion:^(BOOL finished) {
            if (completion) {
                completion(finished);
            }
        }];
        [UIView animateWithDuration:0.2 delay:0 options:UIViewAnimationOptionCurveLinear animations:^{
            if (isShowingByWindowMode) {
                self.popupWindow.alpha = 1;
            } else {
                self.alpha = 1;
            }
        } completion:nil];
    } else {
        if (isShowingByWindowMode) {
            self.popupWindow.alpha = 1;
        } else {
            self.alpha = 1;
        }
        if (completion) {
            completion(YES);
        }
    }
}

- (void)hideWithAnimated:(BOOL)animated {
    [self hideWithAnimated:animated completion:nil];
}

- (void)hideWithAnimated:(BOOL)animated completion:(void (^)(BOOL))completion {
    if (self.willHideBlock) {
        self.willHideBlock(self.hidesByUserTap, animated);
    }
    
    BOOL isShowingByWindowMode = !!self.popupWindow;
    
    if (animated) {
        [UIView animateWithDuration:0.2 delay:0 options:UIViewAnimationOptionCurveLinear animations:^{
            if (isShowingByWindowMode) {
                self.popupWindow.alpha = 0;
            } else {
                self.alpha = 0;
            }
        } completion:^(BOOL finished) {
            [self hideCompletionWithWindowMode:isShowingByWindowMode completion:completion];
        }];
    } else {
        [self hideCompletionWithWindowMode:isShowingByWindowMode completion:completion];
    }
}

- (void)hideCompletionWithWindowMode:(BOOL)windowMode completion:(void (^)(BOOL))completion {
    if (windowMode) {
        // ?????? keyWindow ?????????????????????????????????????????? https://github.com/Tencent/QMUI_iOS/issues/90
        if ([[UIApplication sharedApplication] keyWindow] == self.popupWindow) {
            [self.previousKeyWindow makeKeyWindow];
        }
        
        // iOS 9 ??????iOS 8 ??? 10 ????????????????????????????????????????????? rootViewController ??? popupWindow ????????????????????????????????? layout ??????????????????????????????????????? popupWindow ??????????????? nil??????????????????????????????View ?????????????????????
        // https://github.com/Tencent/QMUI_iOS/issues/75
        [self removeFromSuperview];
        self.popupWindow.rootViewController = nil;
        
        self.popupWindow.hidden = YES;
        self.popupWindow = nil;
    } else {
        self.hidden = YES;
    }
    if (completion) {
        completion(YES);
    }
    if (self.didHideBlock) {
        self.didHideBlock(self.hidesByUserTap);
    }
    self.hidesByUserTap = NO;
}

- (BOOL)isShowing {
    BOOL isShowingIfAddedToView = self.superview && !self.hidden && !self.popupWindow;
    BOOL isShowingIfInWindow = self.superview && self.popupWindow && !self.popupWindow.hidden;
    return isShowingIfAddedToView || isShowingIfInWindow;
}

#pragma mark - Private Tools

- (BOOL)isSubviewShowing:(UIView *)subview {
    return subview && !subview.hidden && subview.superview;
}

- (void)initPopupContainerViewWindowIfNeeded {
    if (!self.popupWindow) {
        self.popupWindow = [[QMUIPopupContainerViewWindow alloc] init];
        self.popupWindow.qmui_capturesStatusBarAppearance = NO;
        self.popupWindow.backgroundColor = UIColorClear;
        self.popupWindow.windowLevel = UIWindowLevelQMUIAlertView;
        QMUIPopContainerViewController *viewController = [[QMUIPopContainerViewController alloc] init];
        ((QMUIPopContainerMaskControl *)viewController.view).popupContainerView = self;
        if (self.automaticallyHidesWhenUserTap) {
            viewController.view.backgroundColor = self.maskViewBackgroundColor;
        } else {
            viewController.view.backgroundColor = UIColorClear;
        }
        viewController.supportedOrientationMask = [QMUIHelper visibleViewController].supportedInterfaceOrientations;
        self.popupWindow.rootViewController = viewController;// ?????? rootViewController ??????????????????
        [self.popupWindow.rootViewController.view addSubview:self];
    }
}

/// ????????????????????????????????????????????????????????????????????????
- (CGSize)contentSizeInSize:(CGSize)size {
    CGSize contentSize = CGSizeMake(size.width - UIEdgeInsetsGetHorizontalValue(self.contentEdgeInsets) - self.borderWidth * 2, size.height - self.arrowSize.height - UIEdgeInsetsGetVerticalValue(self.contentEdgeInsets) - self.borderWidth * 2);
    return contentSize;
}

/// ???????????????????????????????????????????????????????????????self size??????????????????
- (CGSize)sizeWithContentSize:(CGSize)contentSize sizeThatFits:(CGSize)sizeThatFits {
    CGFloat resultWidth = contentSize.width + UIEdgeInsetsGetHorizontalValue(self.contentEdgeInsets) + self.borderWidth * 2;
    resultWidth = MIN(resultWidth, sizeThatFits.width);// ??????????????????????????????size.width
    resultWidth = MAX(MIN(resultWidth, self.maximumWidth), self.minimumWidth);// ??????????????????????????????????????????
    resultWidth = ceil(resultWidth);
    
    CGFloat resultHeight = contentSize.height + UIEdgeInsetsGetVerticalValue(self.contentEdgeInsets) + self.arrowSize.height + self.borderWidth * 2;
    resultHeight = MIN(resultHeight, sizeThatFits.height);
    resultHeight = MAX(MIN(resultHeight, self.maximumHeight), self.minimumHeight);
    resultHeight = ceil(resultHeight);
    
    return CGSizeMake(resultWidth, resultHeight);
}

@end

@implementation QMUIPopupContainerView (UISubclassingHooks)

- (void)didInitialize {
    _backgroundLayer = [CAShapeLayer layer];
    _backgroundLayer.shadowOffset = CGSizeMake(0, 2);
    _backgroundLayer.shadowOpacity = 1;
    _backgroundLayer.shadowRadius = 10;
    [self.layer addSublayer:_backgroundLayer];
    
    _contentView = [[UIView alloc] init];
    self.contentView.clipsToBounds = YES;
    [self addSubview:self.contentView];
    
    // ???????????????????????? showWithAnimated: ????????????????????? window ???????????? appearance ????????? showWithAnimated: ??????????????????????????????????????? showWithAnimated: ???????????????????????? appearance ????????????????????????????????????????????????????????????
    [self updateAppearance];
}

- (CGSize)sizeThatFitsInContentView:(CGSize)size {
    // ????????????????????????????????????
    if (![self isSubviewShowing:_imageView] && ![self isSubviewShowing:_textLabel]) {
        CGSize selfSize = [self contentSizeInSize:self.bounds.size];
        return selfSize;
    }
    
    CGSize resultSize = CGSizeZero;
    
    BOOL isImageViewShowing = [self isSubviewShowing:_imageView];
    if (isImageViewShowing) {
        CGSize imageViewSize = [_imageView sizeThatFits:size];
        resultSize.width += ceil(imageViewSize.width) + self.imageEdgeInsets.left;
        resultSize.height += ceil(imageViewSize.height) + self.imageEdgeInsets.top;
    }
    
    BOOL isTextLabelShowing = [self isSubviewShowing:_textLabel];
    if (isTextLabelShowing) {
        CGSize textLabelLimitSize = CGSizeMake(size.width - resultSize.width - self.imageEdgeInsets.right, size.height);
        CGSize textLabelSize = [_textLabel sizeThatFits:textLabelLimitSize];
        resultSize.width += (isImageViewShowing ? self.imageEdgeInsets.right : 0) + ceil(textLabelSize.width) + self.textEdgeInsets.left;
        resultSize.height = MAX(resultSize.height, ceil(textLabelSize.height) + self.textEdgeInsets.top);
    }
    resultSize.width = MIN(size.width, resultSize.width);
    resultSize.height = MIN(size.height, resultSize.height);
    return resultSize;
}

@end

@implementation QMUIPopupContainerView (UIAppearance)

+ (void)initialize {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self setDefaultAppearance];
    });
}

+ (void)setDefaultAppearance {
    QMUIPopupContainerView *appearance = [QMUIPopupContainerView appearance];
    appearance.contentEdgeInsets = UIEdgeInsetsMake(8, 8, 8, 8);
    appearance.arrowSize = CGSizeMake(18, 9);
    appearance.maximumWidth = CGFLOAT_MAX;
    appearance.minimumWidth = 0;
    appearance.maximumHeight = CGFLOAT_MAX;
    appearance.minimumHeight = 0;
    appearance.preferLayoutDirection = QMUIPopupContainerViewLayoutDirectionAbove;
    appearance.distanceBetweenSource = 5;
    appearance.safetyMarginsOfSuperview = UIEdgeInsetsMake(10, 10, 10, 10);
    appearance.backgroundColor = UIColorWhite;
    appearance.maskViewBackgroundColor = UIColorMask;
    appearance.highlightedBackgroundColor = nil;
    appearance.shadowColor = UIColorMakeWithRGBA(0, 0, 0, .1);
    appearance.borderColor = UIColorGrayLighten;
    appearance.borderWidth = PixelOne;
    appearance.cornerRadius = 10;
    appearance.qmui_outsideEdge = UIEdgeInsetsZero;
    
}

- (void)updateAppearance {
    QMUIPopupContainerView *appearance = [QMUIPopupContainerView appearance];
    self.contentEdgeInsets = appearance.contentEdgeInsets;
    self.arrowSize = appearance.arrowSize;
    self.maximumWidth = appearance.maximumWidth;
    self.minimumWidth = appearance.minimumWidth;
    self.maximumHeight = appearance.maximumHeight;
    self.minimumHeight = appearance.minimumHeight;
    self.preferLayoutDirection = appearance.preferLayoutDirection;
    self.safetyMarginsOfSuperview = appearance.safetyMarginsOfSuperview;
    self.distanceBetweenSource = appearance.distanceBetweenSource;
    self.backgroundColor = appearance.backgroundColor;
    self.maskViewBackgroundColor = appearance.maskViewBackgroundColor;
    self.shadowColor = appearance.shadowColor;
    self.borderColor = appearance.borderColor;
    self.borderWidth = appearance.borderWidth;
    self.cornerRadius = appearance.cornerRadius;
    self.qmui_outsideEdge = appearance.qmui_outsideEdge;
}

@end

@implementation QMUIPopContainerViewController

- (void)loadView {
    QMUIPopContainerMaskControl *maskControl = [[QMUIPopContainerMaskControl alloc] init];
    self.view = maskControl;
}

@end

@implementation QMUIPopContainerMaskControl

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self addTarget:self action:@selector(handleMaskEvent:) forControlEvents:UIControlEventTouchDown];
    }
    return self;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *result = [super hitTest:point withEvent:event];
    if (result == self) {
        if (!self.popupContainerView.automaticallyHidesWhenUserTap) {
            return nil;
        }
    }
    return result;
}

// ?????????????????????????????? addTarget: ?????????????????? hitTest:withEvent: ?????????????????? hitTest:withEvent: ??????????????????
- (void)handleMaskEvent:(id)sender {
    if (self.popupContainerView.automaticallyHidesWhenUserTap) {
        self.popupContainerView.hidesByUserTap = YES;
        [self.popupContainerView hideWithAnimated:YES];
    }
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self.popupContainerView updateLayout];// ??????????????????????????? sourceView window ?????????????????? popupWindow ???????????????????????? popupWindow ???????????????????????????????????? popup ?????????
}

@end

@implementation QMUIPopupContainerViewWindow

// ?????? UIWindow ??????????????????????????????????????????????????????
- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *result = [super hitTest:point withEvent:event];
    if (result == self) {
        return nil;
    }
    return result;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.rootViewController.view.frame = self.bounds;// ???????????????????????????????????????
}

@end
