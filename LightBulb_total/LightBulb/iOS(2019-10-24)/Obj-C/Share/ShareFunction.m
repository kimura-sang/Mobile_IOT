//
//  ShareFunction.m
//  OpenGroup 0.0.1
//
//  Created by TestIOS on 15-4-2.
//  Copyright (c) 2015年 XiaMen_Leadhead. All rights reserved.
//

#import "ShareFunction.h"
#import "PrefixHeader.pch"

@implementation ShareFunction
{
//    NSTimer *time;
}


//Phone number verification
+ (BOOL)checkPhoneNumInput:(NSString *)_text
{
    NSString *Regex =@"(13[0-9]|14[57]|15[012356789]|18[02356789])\\d{8}";
    NSPredicate *mobileTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", Regex];
    return [mobileTest evaluateWithObject:_text];
}

#pragma mark - Get the version number
+(NSString *)getVersion{

    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    // app版本
    NSString *app_Version = [infoDictionary objectForKey:@"CFBundleShortVersionString"];
    
    return app_Version;
}

+(NSString *)getNowDate{
    NSDate *date_one=[NSDate date];
    NSDateFormatter *formatter=[[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"yyyy-MM-dd hh:mm:ss"];
    NSString *str=[formatter stringFromDate:date_one];
    //NSLog(@"%@",str);
    return str;
}
#pragma mark Returns the number of days in the month
+(NSInteger)BackNowMonthCount{
    NSString *NowDate=[self getNowDate];
//    2015-12-04
    NSRange yearR={0,4};
    NSRange monthR={5,2};
    
    NSString *year=[NowDate substringWithRange:yearR];
    NSString *month=[NowDate substringWithRange:monthR];
    NSInteger monthInt=[month integerValue];
    //NSLog(@"月%d",monthInt);
    NSInteger yearInt=[year integerValue];
    NSInteger daycount=0;
    //NSString *str=[NSString stringWithFormat:@"%@.%@",year,month];
    if(monthInt==1||monthInt==3||monthInt==5||monthInt==7||monthInt==8||monthInt==10||monthInt==12){
        daycount=31;
    }
    if(monthInt==4||monthInt==6||monthInt==9||monthInt==11){
        
        daycount=30;
    }
    if(monthInt==2){
        if((yearInt%4==0&&yearInt%100!=0)||yearInt%400==0)
        {
            daycount=29;
            
        }
        else{
            daycount=28;
        }
    }
    return daycount;
}
#pragma mark - Color conversion IOS hexadecimal color is converted to UIColor

+ (UIColor *)colorWithHexString:(NSString *)color andAlpha:(CGFloat)alpha
{
    NSString *cString = [[color stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
    
    if ([cString length] < 6) {
        return [UIColor clearColor];
    }
    
    if ([cString hasPrefix:@"0X"])
        cString = [cString substringFromIndex:2];
    if ([cString hasPrefix:@"#"])
        cString = [cString substringFromIndex:1];
    if ([cString length] != 6)
        return [UIColor clearColor];
    
    NSRange range;
    range.location = 0;
    range.length = 2;
    
    NSString *rString = [cString substringWithRange:range];
    
    range.location = 2;
    NSString *gString = [cString substringWithRange:range];
    
    range.location = 4;
    NSString *bString = [cString substringWithRange:range];
    
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString] scanHexInt:&r];
    [[NSScanner scannerWithString:gString] scanHexInt:&g];
    [[NSScanner scannerWithString:bString] scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r / 255.0f) green:((float) g / 255.0f) blue:((float) b / 255.0f) alpha:alpha];
}
#pragma mark -Get the current date
+(NSString *)getCurrentTimeWithYMd
{
    NSDate *now = [NSDate date];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyyMMdd"];
    NSString *dateString = [dateFormatter stringFromDate:now];
    return dateString;
}
#pragma mark - Get the week
+(NSString *)getWeekData:(int)Num{
    NSLog(@"%d",Num);
    NSString *dateStr,*strMon,*strSum;
    dateStr=[self getNowDate];
    for(int i=0;i<Num;i++){
       
        dateStr=[self DateSub:dateStr];
        //NSLog(@"self date %@",dateStr);
    }
    
    strMon=dateStr;
    for (int i=0; i<7; i++) {
        //NSMutableArray *day=[DatabaseDeal getDataWithDate:self.dateStr anduserID:userid];
        dateStr=[self DateAdd:dateStr];
        
        }
    
        
    
    strSum=[self DateSub:dateStr];
    strMon=[strMon substringWithRange:NSMakeRange(5, 5)];
    strMon=[strMon stringByReplacingOccurrencesOfString:@"-" withString:@"/"];
    strSum=[strSum substringWithRange:NSMakeRange(5, 5)];
    strSum=[strSum stringByReplacingOccurrencesOfString:@"-" withString:@"/"];
    NSString *str=[NSString stringWithFormat:@"%@-%@",strMon,strSum];
    return str;
    
}
+(NSString *)DateSub:(NSString *)date{
    NSRange yearR={0,4};
    NSRange monthR={5,2};
    NSRange dayR={8,2};
    NSString *year=[date substringWithRange:yearR];
    NSString *month=[date substringWithRange:monthR];
    NSString *day=[date substringWithRange:dayR];
    int intyear=[year intValue];
    int intday=[day intValue];
    int intmonth=[month intValue];
    intday=intday-1;
    if(intday==0){
        intmonth=intmonth-1;
        if(intmonth==1||intmonth==3||intmonth==5||intmonth==7||intmonth==8||intmonth==10){
            intday=31;
        }
        if(intmonth==4||intmonth==6||intmonth==9||intmonth==11){
            intday=30;
        }
        if(intmonth==2){
            if((intyear%4==0&&intyear%100!=0)||intyear%400==0)
            {
                intday=29;
            }
            else{
                intday=28;
            }
        }
        if(intmonth==0){
            intday=31;
            intmonth=12;
            intyear=intyear-1;
        }
    }
    
    
    NSString *str;
    if(intday>=10&&intmonth>=10){
        str=[NSString stringWithFormat:@"%d-%d-%d",intyear,intmonth,intday];
       // NSLog(@"%@",str);
        
    }
    if(intday<10&&intmonth<10){
        str=[NSString stringWithFormat:@"%d-0%d-0%d",intyear,intmonth,intday];
        //NSLog(@"%@",str);
        
    }
    if(intday<10&&intmonth>=10){
        str=[NSString stringWithFormat:@"%d-%d-0%d",intyear,intmonth,intday];
        //NSLog(@"%@",str);
        
    }
    if(intday>=10&&intmonth<10){
        str=[NSString stringWithFormat:@"%d-0%d-%d",intyear,intmonth,intday];
       // NSLog(@"%@",str);
        
    }
    return str;
}
+(NSString *)DateAdd:(NSString *)date{
    NSRange yearR={0,4};
    NSRange monthR={5,2};
    NSRange dayR={8,2};
    NSString *year=[date substringWithRange:yearR];
    NSString *month=[date substringWithRange:monthR];
    NSString *day=[date substringWithRange:dayR];
    int intyear=[year intValue];
    int intday=[day intValue];
    int intmonth=[month intValue];
    intday=intday+1;
   // NSLog(@"%d",intday);
    if(intmonth==1||intmonth==3||intmonth==5||intmonth==7||intmonth==8||intmonth==10){
        if(intday>31){
            intmonth+=1;
            intday=1;
        }
    }
    else if(intmonth==4||intmonth==6||intmonth==9||intmonth==11){
        if(intday>30){
            intmonth+=1;
            intday=1;
        }
    }
    else if(intmonth==2){
        if((intyear%4==0&&intyear%100!=0)||intyear%400==0)
        {
            if(intday>29){
                intday=1;
                intmonth+=1;
            }
        }
        else{
            if(intday>28){
                intday=1;
                intmonth+=1;
            }
        }
    }
    else if(intmonth==12){
        if(intday>31){
            intyear+=1;
            intmonth=1;
            intday=1;
        }
    }
    NSString *str;
    if(intday>=10&&intmonth>=10){
        str=[NSString stringWithFormat:@"%d-%d-%d",intyear,intmonth,intday];
       // NSLog(@"%@",str);
        
    }
    if(intday<10&&intmonth<10){
        str=[NSString stringWithFormat:@"%d-0%d-0%d",intyear,intmonth,intday];
       // NSLog(@"%@",str);
        
    }
    if(intday<10&&intmonth>=10){
        str=[NSString stringWithFormat:@"%d-%d-0%d",intyear,intmonth,intday];
        //NSLog(@"%@",str);
        
    }
    if(intday>=10&&intmonth<10){
        str=[NSString stringWithFormat:@"%d-0%d-%d",intyear,intmonth,intday];
       // NSLog(@"%@",str);
        
    }
    return str;
}

#pragma mark    - toast

+ (NSDate *)getCurrentTime
{
    NSDate *datenow = [NSDate date];
    NSTimeZone *zone = [NSTimeZone systemTimeZone];
    NSInteger interval = [zone secondsFromGMTForDate:datenow];
    NSDate *localeDate = [datenow dateByAddingTimeInterval:interval];
    return localeDate;
}

#pragma mark mailbox
+ (BOOL) validateEmail:(NSString *)email
{
    NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    return [emailTest evaluateWithObject:email];
}
#pragma mark Get the current volume of the system
+(float) getVolumeLevel
{
    MPVolumeView *slide =[MPVolumeView new];
    UISlider *volumeViewSlider;
    for(UIView *view in[slide subviews])
    {
        if([[[view class] description] isEqualToString:@"MPVolumeSlider"]){
    volumeViewSlider =(UISlider*) view;
        }
    }
    float val =[volumeViewSlider value];
    
    return val;
}
#pragma mark Gets the maximum and minimum values ​​in the array
+(NSMutableArray *)GetMaxAndMin:(NSMutableArray *)arr{
    NSMutableArray *ValueArr=[[NSMutableArray alloc]init];
    
    [ValueArr addObject:[arr valueForKeyPath:@"@min.floatValue"]];
    [ValueArr addObject:[arr valueForKeyPath: @"@max.floatValue"]];
    
    return ValueArr;
}

#pragma mark - Returns the order status
+(NSString *)OrderState:(NSString *)orderString{
    
    NSString * orderState = [[NSString alloc]init];
    
    if ([orderString isEqualToString:@"wait"]) {//wait
        
        orderState = @"Dispatch";
 
    }else if ([orderString isEqualToString:@"order"]){//Has received orders
        
        orderState = @"Has received orders";
        
    }else if ([orderString isEqualToString:@"close"]){//completed
        
        orderState = @"completed";
        
    }else if ([orderString isEqualToString:@"service"]){//in service
        
        orderState = @"in service";

    }else if ([orderString isEqualToString:@"cancel"]){//Cancelled
        
        orderState = @"Cancelled";
        
    }else if ([orderString isEqualToString:@"evaluate"]){//Be evaluated
        
        orderState = @"Be evaluated";
        
    }
    return orderState;
}
#pragma mark - By judging the model, return different parameters
+(int)JudgmentPhoneModel:(int)number andOne:(int)one{
    int line = 0;
    
    if (k_screenHeight <= 480) {//4s
        if (one == 2) {
            line = 1;
        }else{
        if (number>= 2) {
            line = 2;
        }else{
            line = number;
        }
        }
    }else if (k_screenHeight == 568){//5s
        if (one == 2) {
            if (number>= 2) {
                line = 2;
            }else{
                line = number;
            }
        }else{
        if (number>= 3) {
            line = 3;
        }else{
            line = number;
        }
        }
    
    }else if (k_screenHeight == 667){//6s
        if (one == 2) {
            if (number>= 2) {
                line = 2;
            }else{
                line = number;
            }
        }else{
        if (number>= 4) {
            line = 4;
        }else{
            line = number;
        }
        }
    
    }else if (k_screenHeight== 736){
        
        if (one == 2) {
            
            if (number>= 3) {
                line = 3;
            }else{
                line = number;
            }
            
        }else{
        
        if (number>= 4) {
            line = 4;
        }else{
            line = number;
        }
        }
    }
    
    return line;
}

+(NSDictionary *)readData:(NSData *)data{

    if (data == nil || [data length] == 0|| [data length] < 6) {
        return nil;
    }
    
    Byte *byte = (Byte *)[data bytes];
    
    NSString *hexStr = @"";
    NSString *newHexStr = [NSString stringWithFormat:@"%x",byte[2]&0xff];///Hexadecimal number
    if([newHexStr length]==1)
        hexStr = [NSString stringWithFormat:@"%@0%@",hexStr,newHexStr];
    else
        hexStr = [NSString stringWithFormat:@"%@%@",hexStr,newHexStr];
    
    NSMutableArray * aryData = [[NSMutableArray alloc] init];
    
    for(int i=4;i<[data length]-2;i++){
        [aryData addObject:[NSNumber numberWithInt:byte[i]]];
    }
    
    NSDictionary * result = @{@"tag":hexStr,@"data":aryData};
    
    return result;
}

#pragma mark - Converts a string to data
+ (NSData*)dataForHexString:(NSString*)hexString

{
    
    if (hexString == nil) {
        
        return nil;
        
    }
    
    
    
    const char* ch = [[hexString lowercaseString] cStringUsingEncoding:NSUTF8StringEncoding];
    
    NSMutableData* data = [NSMutableData data];
    
    while (*ch) {
        
        if (*ch == ' ') {
            
            continue;
            
        }
        
        char byte = 0;
        
        if ('0' <= *ch && *ch <= '9') {
            
            byte = *ch - '0';
            
        }
        
        else if ('a' <= *ch && *ch <= 'f') {
            
            byte = *ch - 'a' + 10;
            
        }
        
        else if ('A' <= *ch && *ch <= 'F') {
            
            byte = *ch - 'A' + 10;
            
        }
        
        ch++;
        
        byte = byte << 4;
        
        if (*ch) {
            
            if ('0' <= *ch && *ch <= '9') {
                
                byte += *ch - '0';
                
            } else if ('a' <= *ch && *ch <= 'f') {
                
                byte += *ch - 'a' + 10;
                
            }
            
            else if('A' <= *ch && *ch <= 'F')
                
            {
                
                byte += *ch - 'A' + 10;
                
            }
            
            ch++;
            
        }
        
        [data appendBytes:&byte length:1];
        
    }
    
    return data;
    
}

#pragma mark - To determine whether all the numbers
+(BOOL)isPureNumandCharacters:(NSString *)string
{
    string = [string stringByTrimmingCharactersInSet:[NSCharacterSet decimalDigitCharacterSet]];
    if (string.length > 0) {
        
        return NO;
    }
    return YES;
}

#pragma mark - Turn the white background transparent
+(UIImage*) imageToTransparent:(UIImage*) image
{
    // Allocate memory
    const int imageWidth = image.size.width;
    const int imageHeight = image.size.height;
    size_t bytesPerRow = imageWidth * 4;
    uint32_t* rgbImageBuf = (uint32_t*)malloc(bytesPerRow * imageHeight);
    
    // Create context
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(rgbImageBuf, imageWidth, imageHeight, 8, bytesPerRow, colorSpace,
                                                 kCGBitmapByteOrder32Little | kCGImageAlphaNoneSkipLast);
    CGContextDrawImage(context, CGRectMake(0, 0, imageWidth, imageHeight), image.CGImage);
    
    // Traverse the pixels
    int pixelNum = imageWidth * imageHeight;
    uint32_t* pCurPtr = rgbImageBuf;
    for (int i = 0; i < pixelNum; i++, pCurPtr++)
    {
        
        if ((*pCurPtr & 0xFFFFFF00) == 0xffffff00) {
            
            uint8_t* ptr = (uint8_t*)pCurPtr;
            ptr[0] = 0;
            
        }
        
    }
    
    // Turn memory into image
    CGDataProviderRef dataProvider =CGDataProviderCreateWithData(NULL, rgbImageBuf, bytesPerRow * imageHeight, ProviderReleaseData);
    
    CGImageRef imageRef = CGImageCreate(imageWidth, imageHeight,8, 32, bytesPerRow, colorSpace,
                                        kCGImageAlphaLast |kCGBitmapByteOrder32Little, dataProvider,
                                        NULL, true,kCGRenderingIntentDefault);
    CGDataProviderRelease(dataProvider);
    
    UIImage* resultUIImage = [UIImage imageWithCGImage:imageRef];
    
    // freed
    CGImageRelease(imageRef);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    return resultUIImage;
}

/** Color changes */

void ProviderReleaseData (void *info, const void *data, size_t size)
{
    free((void*)data);
}

#pragma mark - To determine whether all the numbers
+(BOOL)isPureInt:(NSString *)string{
    NSScanner* scan = [NSScanner scannerWithString:string];
    int val;
    return [scan scanInt:&val] && [scan isAtEnd];
}

#pragma mark - Converts a string to hexadecimal
+ (NSString *)hexStringFromString:(NSString *)string{

    NSData *myD = [string dataUsingEncoding:NSUTF8StringEncoding];
    Byte *bytes = (Byte *)[myD bytes];
    //The following is the Byte conversion to hexadecimal。
    NSString *hexStr=@"";
    for(int i=0;i<[myD length];i++)
        
    {
        NSString *newHexStr = [NSString stringWithFormat:@"%x",bytes[i]&0xff];///Hexadecimal number
        
        if([newHexStr length]==1)
            
            hexStr = [NSString stringWithFormat:@"%@0%@",hexStr,newHexStr];
        
        else
            
            hexStr = [NSString stringWithFormat:@"%@%@",hexStr,newHexStr]; 
    } 
    return hexStr;
}

#pragma mark - 将十进制转换为16进制
+(NSString *)tenToHex:(long long int)tmpid

{
    
    NSString *nLetterValue;
    NSString *str =@"";
    int ttmpig;
    for (int i = 0; i<9; i++) {
        ttmpig=tmpid%16;
        tmpid=tmpid/16;
        switch (ttmpig)
        {
            case 10:
                nLetterValue =@"A";break;
            case 11:
                nLetterValue =@"B";break;
            case 12:
                nLetterValue =@"C";break;
            case 13:
                nLetterValue =@"D";break;
            case 14:
                nLetterValue =@"E";break;
            case 15:
                nLetterValue =@"F";break;
            default:
                nLetterValue = [NSString stringWithFormat:@"%u",ttmpig];
                
        }
        str = [nLetterValue stringByAppendingString:str];
        if (tmpid == 0) {
            break;
        }
    }
    //Not enough for a byte
    if(str.length == 1){
        return [NSString stringWithFormat:@"0%@",str];
    }else{
        return str;
    }
}

/**
 
 * Converts binary data to a hexadecimal string
 *
 * @param data binary data
 *
 * @return hexadecimal string
 */
+ (NSString *)data2Hex:(NSData *)data {
    if (!data) {
        return nil;
    }
    Byte *bytes = (Byte *)[data bytes];
    NSMutableString *str = [NSMutableString stringWithCapacity:data.length * 2];
    for (int i=0; i < data.length; i++){
        [str appendFormat:@"%0x", bytes[i]];
    }
    return str;
}


#pragma mark - Converts View to image
+ (UIImage *) imageWithView:(UIView *)view
{
    UIGraphicsBeginImageContextWithOptions(view.bounds.size, view.opaque, [[UIScreen mainScreen] scale]);
    [view.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage * img = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    return img;
}


#pragma mark - Get the current language
+(NSString *)getCurrentLanguage{
    
    NSArray *languages = [NSLocale preferredLanguages];
    NSString *currentLanguage = [languages objectAtIndex:0];
    currentLanguage = [currentLanguage substringToIndex:2];
    
    return currentLanguage;
}

#pragma mark - Label Height
+(CGFloat)getLabelHeight:(UILabel*)label;
{
    CGSize constraint = CGSizeMake(label.frame.size.width, CGFLOAT_MAX);
    CGSize size;
    
    NSStringDrawingContext *context = [[NSStringDrawingContext alloc] init];
    CGSize boundingBox = [label.text boundingRectWithSize:constraint
                                                  options:NSStringDrawingUsesLineFragmentOrigin
                                               attributes:@{NSFontAttributeName:label.font}
                                                  context:context].size;
    
    size = CGSizeMake(ceil(boundingBox.width), ceil(boundingBox.height));
    
    return size.height;
}


@end
