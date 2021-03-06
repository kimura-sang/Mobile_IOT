/*****
 * Tencent is pleased to support the open source community by making QMUI_iOS available.
 * Copyright (C) 2016-2019 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/MIT
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *****/

//
//  UISearchBar+QMUI.m
//  qmui
//
//  Created by QMUI Team on 16/5/26.
//

#import "UISearchBar+QMUI.h"
#import "QMUICore.h"
#import "UIImage+QMUI.h"
#import "UIView+QMUI.h"

#define SearchBarActiveHeightIOS11Later (IS_NOTCHED_SCREEN ? 55.0f : 50.0f)
#define SearchBarNormalHeightIOS11Later 56.0f


@implementation UISearchBar (QMUI)

QMUISynthesizeBOOLProperty(qmui_usedAsTableHeaderView, setQmui_usedAsTableHeaderView)
QMUISynthesizeUIEdgeInsetsProperty(qmui_textFieldMargins, setQmui_textFieldMargins)

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        
        ExtendImplementationOfVoidMethodWithTwoArguments([UISearchBar class], @selector(setShowsCancelButton:animated:), BOOL, BOOL, ^(UISearchBar *selfObject, BOOL firstArgv, BOOL secondArgv) {
            if (selfObject.qmui_cancelButton && selfObject.qmui_cancelButtonFont) {
                selfObject.qmui_cancelButton.titleLabel.font = selfObject.qmui_cancelButtonFont;
            }
        });
        
        ExtendImplementationOfVoidMethodWithSingleArgument([UISearchBar class], @selector(setPlaceholder:), NSString *, (^(UISearchBar *selfObject, NSString *placeholder) {
            if (selfObject.qmui_placeholderColor || selfObject.qmui_font) {
                NSMutableDictionary<NSString *, id> *attributes = [[NSMutableDictionary alloc] init];
                if (selfObject.qmui_placeholderColor) {
                    attributes[NSForegroundColorAttributeName] = selfObject.qmui_placeholderColor;
                }
                if (selfObject.qmui_font) {
                    attributes[NSFontAttributeName] = selfObject.qmui_font;
                }
                selfObject.qmui_textField.attributedPlaceholder = [[NSAttributedString alloc] initWithString:placeholder attributes:attributes];
            }
        }));
        
        ExtendImplementationOfVoidMethodWithoutArguments([UISearchBar class], @selector(layoutSubviews), ^(UISearchBar *selfObject) {
            [selfObject fixLandscapeStyle];
            
            [selfObject fixDismissingAnimation];
            
            if (!UIEdgeInsetsEqualToEdgeInsets(selfObject.qmui_textFieldMargins, UIEdgeInsetsZero)) {
                selfObject.qmui_textField.frame = CGRectInsetEdges(selfObject.qmui_textField.frame, selfObject.qmui_textFieldMargins);
            }
            
            CGFloat textFieldCornerRadius = SearchBarTextFieldCornerRadius;
            if (textFieldCornerRadius != 0) {
                textFieldCornerRadius = textFieldCornerRadius > 0 ? textFieldCornerRadius : CGRectGetHeight(selfObject.qmui_textField.frame) / 2.0;
            }
            selfObject.qmui_textField.layer.cornerRadius = textFieldCornerRadius;
            selfObject.qmui_textField.clipsToBounds = textFieldCornerRadius != 0;
        });
        
        OverrideImplementation([UISearchBar class], @selector(setFrame:), ^id(__unsafe_unretained Class originClass, SEL originCMD, IMP (^originalIMPProvider)(void)) {
            return ^(UISearchBar *selfObject, CGRect frame) {
                
                // call super
                void (^callSuperBlock)(CGRect) = ^void(CGRect aFrame) {
                    void (*originSelectorIMP)(id, SEL, CGRect);
                    originSelectorIMP = (void (*)(id, SEL, CGRect))originalIMPProvider();
                    originSelectorIMP(selfObject, originCMD, aFrame);
                };
                
                // avoid superclass
                if ([selfObject isKindOfClass:originClass]) {
                    if (!selfObject.qmui_usedAsTableHeaderView) {
                        callSuperBlock(frame);
                        return;
                    }
                    
                    // ?????? setFrame: ??????????????? issue???https://github.com/Tencent/QMUI_iOS/issues/233
                    
                    if (@available(iOS 11, *)) {
                        // iOS 11 ?????? tableHeaderView ??????????????? searchBar ?????????????????????????????? y ??????????????????????????????
                        
                        if (![selfObject qmui_isActive]) {
                            callSuperBlock(frame);
                            return;
                        }
                        
                        if (IS_NOTCHED_SCREEN) {
                            // ??????
                            if (CGRectGetMinY(frame) == 38) {
                                // searching
                                frame = CGRectSetY(frame, 44);
                            }
                            
                            // ??????
                            if (CGRectGetMinY(frame) == -6) {
                                frame = CGRectSetY(frame, 0);
                            }
                        } else {
                            
                            // ??????
                            if (CGRectGetMinY(frame) == 14) {
                                frame = CGRectSetY(frame, 20);
                            }
                            
                            // ??????
                            if (CGRectGetMinY(frame) == -6) {
                                frame = CGRectSetY(frame, 0);
                            }
                        }
                        // ???????????????????????? ???????????? 56???????????????????????????????????? (iOS 11 ????????????????????????????????????????????? 50???????????????????????? 55)
                        if (frame.size.height == SearchBarActiveHeightIOS11Later) {
                            frame.size.height = 56;
                        }
                    }
                }
                callSuperBlock(frame);
            };
        });
    });
}

static char kAssociatedObjectKey_PlaceholderColor;
- (void)setQmui_placeholderColor:(UIColor *)qmui_placeholderColor {
    objc_setAssociatedObject(self, &kAssociatedObjectKey_PlaceholderColor, qmui_placeholderColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    if (self.placeholder) {
        // ?????? setPlaceholder ????????? placeholder ???????????????
        self.placeholder = self.placeholder;
    }
}

- (UIColor *)qmui_placeholderColor {
    return (UIColor *)objc_getAssociatedObject(self, &kAssociatedObjectKey_PlaceholderColor);
}

static char kAssociatedObjectKey_TextColor;
- (void)setQmui_textColor:(UIColor *)qmui_textColor {
    objc_setAssociatedObject(self, &kAssociatedObjectKey_TextColor, qmui_textColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    self.qmui_textField.textColor = qmui_textColor;
}

- (UIColor *)qmui_textColor {
    return (UIColor *)objc_getAssociatedObject(self, &kAssociatedObjectKey_TextColor);
}

static char kAssociatedObjectKey_font;
- (void)setQmui_font:(UIFont *)qmui_font {
    objc_setAssociatedObject(self, &kAssociatedObjectKey_font, qmui_font, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    if (self.placeholder) {
        // ?????? setPlaceholder ????????? placeholder ???????????????
        self.placeholder = self.placeholder;
    }
    
    // ??????????????????????????????
    self.qmui_textField.font = qmui_font;
}

- (UIFont *)qmui_font {
    return (UIFont *)objc_getAssociatedObject(self, &kAssociatedObjectKey_font);
}

- (UITextField *)qmui_textField {
    UITextField *textField = [self valueForKey:@"searchField"];
    return textField;
}

- (UIButton *)qmui_cancelButton {
    UIButton *cancelButton = [self valueForKey:@"cancelButton"];
    return cancelButton;
}

static char kAssociatedObjectKey_cancelButtonFont;
- (void)setQmui_cancelButtonFont:(UIFont *)qmui_cancelButtonFont {
    objc_setAssociatedObject(self, &kAssociatedObjectKey_cancelButtonFont, qmui_cancelButtonFont, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    self.qmui_cancelButton.titleLabel.font = qmui_cancelButtonFont;
}

- (UIFont *)qmui_cancelButtonFont {
    return (UIFont *)objc_getAssociatedObject(self, &kAssociatedObjectKey_cancelButtonFont);
}

- (UISegmentedControl *)qmui_segmentedControl {
    // ?????????segmentedControl ???????????? scopeBar ?????????????????????????????? key ?????????scopeBar???
    UISegmentedControl *segmentedControl = [self valueForKey:@"scopeBar"];
    return segmentedControl;
}

- (BOOL)qmui_isActive {
    return (self.qmui_searchController.isBeingPresented || self.qmui_searchController.isActive);
}

- (UISearchController *)qmui_searchController {
    return [self valueForKey:@"_searchController"];
}

- (void)fixLandscapeStyle {
    if (self.qmui_usedAsTableHeaderView) {
        if (@available(iOS 11, *)) {
            if ([self qmui_isActive] && IS_LANDSCAPE) {
                // 11.0 ?????????????????????????????????searchBar ???????????????????????????????????????????????????????????????
                CGFloat fixedOffset = (SearchBarActiveHeightIOS11Later - SearchBarNormalHeightIOS11Later) / 2.0;
                self.qmui_textField.frame = CGRectSetY(self.qmui_textField.frame, self.qmui_textField.qmui_topWhenCenterInSuperview + fixedOffset);
                self.qmui_cancelButton.frame = CGRectSetY(self.qmui_cancelButton.frame, self.qmui_cancelButton.qmui_topWhenCenterInSuperview + fixedOffset);
                if (self.qmui_segmentedControl.superview.qmui_top < self.qmui_textField.qmui_bottom) {
                    // scopeBar ????????????????????????
                    self.qmui_segmentedControl.superview.qmui_top = self.qmui_segmentedControl.superview.qmui_topWhenCenterInSuperview + fixedOffset;
                }
            }
        }
    }
}

- (void)fixDismissingAnimation {
    if (self.qmui_usedAsTableHeaderView) {
        if (@available(iOS 11, *)) {
            if (self.qmui_searchController.isBeingDismissed) {
                self.qmui_textField.superview.qmui_height = SearchBarNormalHeightIOS11Later;
                self.qmui_textField.frame = CGRectSetY(self.qmui_textField.frame, self.qmui_textField.qmui_topWhenCenterInSuperview);
                self.qmui_backgroundView.frame = self.qmui_textField.superview.bounds;
                if (IS_NOTCHED_SCREEN && self.frame.origin.y == 43) { // ????????????????????????????????????????????? px
                    self.frame = CGRectSetY(self.frame, StatusBarHeightConstant);
                }
                
                UIView *searchBarContainerView = self.superview;
                if (searchBarContainerView.layer.masksToBounds) {
                    searchBarContainerView.layer.masksToBounds = NO;
                    if (self.showsScopeBar && !IS_LANDSCAPE) {
                        //????????????????????? ScopeBar ??????????????????????????????????????? mask ??????
                        return;
                    }
                    // ???????????? searchBarContainerView ??????mask ??????, ?????? backgroundView ????????? searchBarContainerView ???????????????????????????????????????????????? masksToBounds ??????????????????????????????????????????
                    CAShapeLayer *maskLayer = [CAShapeLayer layer];
                    CGMutablePathRef path = CGPathCreateMutable();
                    CGPathAddRect(path, NULL, CGRectMake(0, 0, searchBarContainerView.qmui_width, StatusBarHeight + SearchBarActiveHeightIOS11Later));
                    maskLayer.path = path;
                    searchBarContainerView.layer.mask = maskLayer;
                    
                    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"path"];
                    CGMutablePathRef animationPath = CGPathCreateMutable();
                    CGPathAddRect(animationPath, NULL, CGRectMake(0, 0, searchBarContainerView.qmui_width, StatusBarHeight + SearchBarNormalHeightIOS11Later));
                    animation.toValue   = (__bridge id)animationPath;
                    animation.removedOnCompletion = NO;
                    animation.fillMode = kCAFillModeForwards;
                    [searchBarContainerView.layer.mask addAnimation:animation forKey:nil];
                }
            }
        }
    }
}

- (UIView *)qmui_backgroundView {
    UIView *backgroundView = [self valueForKey:@"background"];
    return backgroundView;
}

- (void)qmuisb_setFrame:(CGRect)frame {
    
    if (!self.qmui_usedAsTableHeaderView) {
        [self qmuisb_setFrame:frame];
        return;
    }
    
    // ?????? setFrame: ??????????????? issue???https://github.com/Tencent/QMUI_iOS/issues/233
    
    if (@available(iOS 11, *)) {
        // iOS 11 ?????? tableHeaderView ??????????????? searchBar ?????????????????????????????? y ??????????????????????????????
        
        if (![self qmui_isActive]) {
            [self qmuisb_setFrame:frame];
            return;
        }
        
        if (IS_NOTCHED_SCREEN) {
            // ??????
            if (CGRectGetMinY(frame) == 38) {
                // searching
                frame = CGRectSetY(frame, 44);
            }
            
            // ??????
            if (CGRectGetMinY(frame) == -6) {
                frame = CGRectSetY(frame, 0);
            }
        } else {
            
            // ??????
            if (CGRectGetMinY(frame) == 14) {
                frame = CGRectSetY(frame, 20);
            }
            
            // ??????
            if (CGRectGetMinY(frame) == -6) {
                frame = CGRectSetY(frame, 0);
            }
        }
        // ???????????????????????? ???????????? 56???????????????????????????????????? (iOS 11 ????????????????????????????????????????????? 50???????????????????????? 55)
        if (frame.size.height == SearchBarActiveHeightIOS11Later) {
            frame.size.height = 56;
        }
    }
    
    [self qmuisb_setFrame:frame];
}

- (void)qmui_styledAsQMUISearchBar {
    if (!QMUICMIActivated) {
        return;
    }
    
    // ????????????????????? placeholder ?????????
    UIFont *font = SearchBarFont;
    if (font) {
        self.qmui_font = font;
    }

    // ????????????????????????
    UIColor *textColor = SearchBarTextColor;
    if (textColor) {
        self.qmui_textColor = textColor;
    }

    // placeholder ???????????????
    UIColor *placeholderColor = SearchBarPlaceholderColor;
    if (placeholderColor) {
        self.qmui_placeholderColor = placeholderColor;
    }

    self.placeholder = @"??????";
    self.autocorrectionType = UITextAutocorrectionTypeNo;
    self.autocapitalizationType = UITextAutocapitalizationTypeNone;

    // ????????????icon
    UIImage *searchIconImage = SearchBarSearchIconImage;
    if (searchIconImage) {
        if (!CGSizeEqualToSize(searchIconImage.size, CGSizeMake(14, 14))) {
            NSLog(@"???????????????????????????SearchBarSearchIconImage????????????????????? (14, 14)??????????????????????????????????????? %@", NSStringFromCGSize(searchIconImage.size));
        }
        [self setImage:searchIconImage forSearchBarIcon:UISearchBarIconSearch state:UIControlStateNormal];
    }

    // ????????????????????????????????????icon
    UIImage *clearIconImage = SearchBarClearIconImage;
    if (clearIconImage) {
        [self setImage:clearIconImage forSearchBarIcon:UISearchBarIconClear state:UIControlStateNormal];
    }

    // ??????SearchBar??????????????????
    self.tintColor = SearchBarTintColor;

    // ??????????????????
    UIColor *textFieldBackgroundColor = SearchBarTextFieldBackground;
    if (textFieldBackgroundColor) {
        [self setSearchFieldBackgroundImage:[[UIImage qmui_imageWithColor:textFieldBackgroundColor size:CGSizeMake(60, 28) cornerRadius:0] resizableImageWithCapInsets:UIEdgeInsetsMake(10, 10, 10, 10)] forState:UIControlStateNormal];
    }
    
    // ???????????????
    UIColor *textFieldBorderColor = SearchBarTextFieldBorderColor;
    if (textFieldBorderColor) {
        self.qmui_textField.layer.borderWidth = PixelOne;
        self.qmui_textField.layer.borderColor = textFieldBorderColor.CGColor;
    }
    
    // ??????bar?????????
    // ????????? searchBar ?????????????????????????????????????????????????????? barTintColor ??????????????????????????? backgroundImage
    UIImage *backgroundImage = nil;
    
    UIColor *barTintColor = SearchBarBarTintColor;
    if (barTintColor) {
        backgroundImage = [UIImage qmui_imageWithColor:barTintColor size:CGSizeMake(10, 10) cornerRadius:0];
    }
    
    UIColor *bottomBorderColor = SearchBarBottomBorderColor;
    if (bottomBorderColor) {
        if (!backgroundImage) {
            backgroundImage = [UIImage qmui_imageWithColor:UIColorWhite size:CGSizeMake(10, 10) cornerRadius:0];
        }
        backgroundImage = [backgroundImage qmui_imageWithBorderColor:bottomBorderColor borderWidth:PixelOne borderPosition:QMUIImageBorderPositionBottom];
    }
    
    if (backgroundImage) {
        backgroundImage = [backgroundImage resizableImageWithCapInsets:UIEdgeInsetsMake(1, 1, 1, 1)];
        [self setBackgroundImage:backgroundImage forBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefault];
        [self setBackgroundImage:backgroundImage forBarPosition:UIBarPositionAny barMetrics:UIBarMetricsDefaultPrompt];
    }
}

@end
