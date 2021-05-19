//
//  UtilFunctions.swift
//  Wifi Socket
//
//  Created by king on 2019/6/5.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

typealias AlertFuncBlock = () -> Void

class UtilFunctions {
    
    // -----------------------
    // --- Get iOS Version ---
    // -----------------------
    
    public static func getiOSVersion() -> String {
        // print(UIDevice.current.systemName)
        return UIDevice.current.systemVersion
    }
    
    // -----------------------
    
    
    // --------------------
    // --- Alert Dialog ---
    // --------------------
    
    public static func showAlertDialog(_ title: String,
                                       _ content:String,
                                       _ btnTitle: String,
                                       _ viewController: UIViewController,
                                       _ clickBtn: @escaping AlertFuncBlock) {
        let alertController = UIAlertController(title: title, message: content, preferredStyle: .alert)
        
        alertController.addAction(UIAlertAction(title: btnTitle,
                                                style: UIAlertAction.Style.default,
                                                handler: {(_: UIAlertAction!) in
                                                    clickBtn()
        }))
        
        viewController.present(alertController, animated: true, completion: nil)
    }
    
    public static func showConfirmDialog(_ title: String,
                                         _ content:String,
                                         _ okBtnTitle: String,
                                         _ cancelBtnTitle: String,
                                         _ viewController: UIViewController,
                                         _ okClickBtn: @escaping AlertFuncBlock,
                                         _ cancelClickBtn: @escaping AlertFuncBlock) {
        let alertController = UIAlertController(title: title, message: content, preferredStyle: .alert)
        
        alertController.addAction(UIAlertAction(title: okBtnTitle,
                                                style: .default,
                                                handler: {(_: UIAlertAction!) in
                                                    okClickBtn()
        }))
        alertController.addAction(UIAlertAction(title: cancelBtnTitle,
                                                style: .cancel,
                                                handler: {(_: UIAlertAction!) in
                                                    cancelClickBtn()
        }))
        
        viewController.present(alertController, animated: true)
    }
    // --------------------
    
    
    // -------------------------
    // --- Commmon Functions ---
    // -------------------------
    
    public static func UIColorFromRGB(rgbValue: UInt) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    public static func setStatusBarBackgroundColor(color: UIColor) {
        let systemVersion = UIDevice.current.systemVersion
        if systemVersion.contains("13.") {
            
        } else {
            guard let statusBar = UIApplication.shared.value(forKeyPath: "statusBarWindow.statusBar") as? UIView else { return }
            statusBar.backgroundColor = color
        }
    }
    
    public static func checkEveryString(str: String) -> String {
        return str.replacingOccurrences(of: " ", with: "", options: .literal, range: nil)
    }
    
    public static func getErrorCode(error: Error) -> Int {
        return (error as NSError).code
    }
    
    public static func preventTouchEvent() {
        UIApplication.shared.beginIgnoringInteractionEvents()
    }
    
    public static func restoreTouchEvent() {
        UIApplication.shared.endIgnoringInteractionEvents()
    }
    
    public static func changeTimeInChineseTimeZone(_ time: String) -> String {
        //print(TimeZone.current.identifier)
        //print(TimeZone.current.abbreviation())
        
        var chinaTimeStr = ""
        
        if time != "" {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "HH:mm"
            let currentTime = dateFormatter.date(from: time)
            
            dateFormatter.timeZone = TimeZone(abbreviation: "GMT")
            let UTCTimeStr = dateFormatter.string(from: currentTime!)
            
            dateFormatter.timeZone = TimeZone(abbreviation: "GMT")
            let UTCTime = dateFormatter.date(from: UTCTimeStr)
            
            dateFormatter.timeZone = TimeZone(abbreviation: "HKT")
            chinaTimeStr = dateFormatter.string(from: UTCTime!)
        }
        
        return chinaTimeStr
    }
    
    public static func getFullStringOfInt(_ intValue: Int) -> String {
        var result: String = ""
        
        if String(intValue).count == 1{
            result = String.init(format: "0%d", intValue)
        } else {
            result = String(intValue)
        }
        
        return result
    }
    
    public static func getOffsetTime(_ serverTime: Int, _ localTime: Int) -> String {
        var serverOffset: String = "Server 00:00"
        
        let offset = serverTime - localTime
        var divoffset: Int = offset / 3600
        let modoffset: Int = offset % 3600
        
        if offset > 0 {
            if __NN_IS_DST {
                divoffset -= 1
            }
            
            var minValue: String = "00"
            if (modoffset == 1800) {
                minValue = "30"
            } else if (modoffset == 1200) {
                minValue = "20"
            } else if (modoffset == 900) {
                minValue = "15"
            }
            
            serverOffset = String.init(format: "Server +%@:%@", UtilFunctions.getFullStringOfInt(divoffset), minValue)
        } else if offset < 0 {
            if __NN_IS_DST {
                divoffset += 1
            }
            
            var minValue: String = "00"
            if (modoffset == 1800) {
                minValue = "30"
            } else if (modoffset == 1200) {
                minValue = "20"
            } else if (modoffset == 900) {
                minValue = "15"
            }
            
            serverOffset = String.init(format: "Server -%@:%@", UtilFunctions.getFullStringOfInt(divoffset), minValue)
        }
        
        return serverOffset
    }
    
    // -------------------------
    
    
    // ------------------
    // --- Validation ---
    // ------------------
    
    public static func emailValidation(_ email: String) -> Bool {
        var returnValue = true
        let emailRegEx = "[A-Z0-9a-z.-_]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}"
        
        do {
            let regex = try NSRegularExpression(pattern: emailRegEx)
            let nsString = email as NSString
            let results = regex.matches(in: email, range: NSRange(location: 0, length: nsString.length))
            
            if results.count == 0
            {
                returnValue = false
            }
            
        } catch let error as NSError {
            print("invalid regex: \(error.localizedDescription)")
            returnValue = false
        }
        
        return returnValue
    }
    
    public static func passwordValidation(_ password: String) -> Bool {
        var result = false;
        let length = password.count
        
        if length >= 6 {
            result = true
        }
        
        return result
    }
    
    public static func nameValidation(_ name: String) -> Bool {
        var result = false;
        let length = name.count
        
        if length > 0 {
            result = true
        }
        
        return result
    }
    
    public static func twoPasswordIsSame(_ password: String, _ confirmPassword: String) -> Bool {
        return (password == confirmPassword)
    }
    
    // ------------------
    
    
    // -----------------------------------
    // --- Management of User Defaults ---
    // -----------------------------------
    
    public static func dumpUserDefaults() {
        for (key, value) in UserDefaults.standard.dictionaryRepresentation() {
            print("\(key) = \(value) \n")
        }
        
        // Using dump since the keys are an array of strings.
        //        dump(Array(UserDefaults.standard.dictionaryRepresentation().keys))
        //        print(Array(UserDefaults.standard.dictionaryRepresentation().values))
    }
    
    public static func removeUserDefaultDataByKey(key: String) {
        UserDefaults.standard.removeObject(forKey: key)
        UserDefaults.standard.synchronize()
    }
    
    public static func removeUserData() {
        removeUserDefaultDataByKey(key: __USER_ID)
    }
    
    public static func getDeviceDataFromUserDefaults() {
        if UserDefaults.standard.dictionary(forKey: __USER_ID) != nil {
            UtilFunctions.loadUserDeviceData()
        } else {
            __USER_DEVICE_DATA = [String: Any]()
            __DEVICES = []
            __GROUPS = []
            __SCHEDULES = []
            __CACHE_DEVICE_ENTITIES = []
        }
    }
    
    public static func syncUserData() {
        saveUserDeviceData()
        loadUserDeviceData()
    }
    
    public static func saveUserDeviceData() {
        __USER_DEVICE_DATA["localDevices"] = try? JSONEncoder().encode(__DEVICES)
        __USER_DEVICE_DATA["localGroups"] = try? JSONEncoder().encode(__GROUPS)
        __USER_DEVICE_DATA["localSchedules"] = try? JSONEncoder().encode(__SCHEDULES)
        __USER_DEVICE_DATA["cacheDeviceEntities"] = __CACHE_DEVICE_ENTITIES
        UserDefaults.standard.set(__USER_DEVICE_DATA, forKey: __USER_ID)
        UserDefaults.standard.synchronize()
    }
    
    public static func loadUserDeviceData() {
        __USER_DEVICE_DATA = UserDefaults.standard.dictionary(forKey: __USER_ID)!
        
        if __USER_DEVICE_DATA["localDevices"] != nil {
            do {
                __DEVICES = try JSONDecoder().decode(Array.self, from: __USER_DEVICE_DATA["localDevices"] as! Data)
            } catch let error {
                __DEVICES = []
                print(error.localizedDescription)
            }
        } else {
            __DEVICES = []
        }
        
        if __USER_DEVICE_DATA["localGroups"] != nil {
            do {
                __GROUPS = try JSONDecoder().decode(Array.self, from: __USER_DEVICE_DATA["localGroups"] as! Data)
            } catch let error {
                __GROUPS = []
                print(error.localizedDescription)
            }
        } else {
            __GROUPS = []
        }
        
        if __USER_DEVICE_DATA["localSchedules"] != nil {
            do {
                __SCHEDULES = try JSONDecoder().decode(Array.self, from: __USER_DEVICE_DATA["localSchedules"] as! Data)
            } catch let error {
                __SCHEDULES = []
                print(error.localizedDescription)
            }
        } else {
            __SCHEDULES = []
        }
        
        if __USER_DEVICE_DATA["cacheDeviceEntities"] != nil {
            __CACHE_DEVICE_ENTITIES = __USER_DEVICE_DATA["cacheDeviceEntities"] as! [[AnyHashable : Any]]
        } else {
            __CACHE_DEVICE_ENTITIES = []
        }
    }
    
    public static func deleteFromLocalDevice(_ index: Int) {
        let macAddress = __DEVICES[index].deviceUUID
        
        __DEVICES.remove(at: index)
        
        if (__CACHE_DEVICE_ENTITIES.count > 0) {
            deleteCacheLoop: for i in 0...(__CACHE_DEVICE_ENTITIES.count - 1) {
                if (__CACHE_DEVICE_ENTITIES[i]["macAddress"] as! String) == macAddress {
                    __CACHE_DEVICE_ENTITIES.remove(at: i)
                    break deleteCacheLoop
                }
            }
        }
        
        if __SCHEDULES.count > 0 {
            for scheduleEntity in __SCHEDULES {
                if scheduleEntity.deviceUUID == macAddress {
                    let index = __SCHEDULES.index(of: scheduleEntity)
                    __SCHEDULES.remove(at: index!)
                }
            }
        }
        
        // TODO:
        // delete from __GROUPS
        
        UtilFunctions.syncUserData()
        
        if (__REAL_DEVICE_ENTITIES.count > 0) {
            deleteRealLoop: for i in 0...(__REAL_DEVICE_ENTITIES.count - 1) {
                let deviceData = __REAL_DEVICE_ENTITIES[i].getDictionaryFormat()
                if (deviceData!["macAddress"] as! String) == macAddress {
                    __REAL_DEVICE_ENTITIES.remove(at: i)
                    break deleteRealLoop
                }
            }
        }
    }
    
    public static func deleteFromDeviceEntity(_ macAddress: String) {
        if __DEVICE_ENTITIES.count > 0 {
            deleteLoop: for i in 0...(__DEVICE_ENTITIES.count - 1) {
                if (__DEVICE_ENTITIES[i]["macAddress"] as! String) == macAddress {
                    __DEVICE_ENTITIES.remove(at: i)
                    break deleteLoop
                }
            }
            
            UserDefaults.standard.set(__DEVICE_ENTITIES, forKey: "deviceEntities")
            UserDefaults.standard.synchronize()
        }
    }
    
    public static func runDeviceConnection() {
        for devEntity in __CACHE_DEVICE_ENTITIES {
            let device = DeviceEntity.init(dictionary: devEntity)
            XLinkExportObject.shared()?.initDevice(device)
            XLinkExportObject.shared()?.connectDevice(device, andAuthKey: __DEVICE_PASSWORD as NSNumber)
            
            __REAL_DEVICE_ENTITIES.append(device!)
        }
    }
    
    public static func restartRealDeviceEntities() {
        for device in __REAL_DEVICE_ENTITIES {
            XLinkExportObject.shared()?.initDevice(device)
            XLinkExportObject.shared()?.connectDevice(device, andAuthKey: __DEVICE_PASSWORD as NSNumber)
        }
    }
    
    public static func haveAnyDeviceEntity() -> Bool {
        var result = false
        if __DEVICE_ENTITIES.count > 0 {
            result = true
        }
        
        if !result && __USER_ID != "" && UserDefaults.standard.dictionary(forKey: __USER_ID) != nil {
            __USER_DEVICE_DATA = UserDefaults.standard.dictionary(forKey: __USER_ID)!
            
            if __USER_DEVICE_DATA["cacheDeviceEntities"] != nil {
                __CACHE_DEVICE_ENTITIES = __USER_DEVICE_DATA["cacheDeviceEntities"] as! [[AnyHashable : Any]]
                
                if __CACHE_DEVICE_ENTITIES.count > 0 {
                    result = true
                }
            }
        }
        
        return result
    }
    
    // -----------------------------------
    
    
    // ----------------------
    // --- Byte Functions ---
    // ----------------------
    
    public static func getCurrentDateTimeToUInt8Array() -> [UInt8] {
        let currentDate = Date()
        //        let timeString = Int(String(currentDate.timeIntervalSince1970).split(separator: ".")[0])?.hw_to4Bytes()
        let timeString = Int(currentDate.timeIntervalSince1970).hw_to4Bytes()
        //print(Int(String(currentDate.timeIntervalSince1970).split(separator: ".")[0]) as Any)
        // print(timeString as Any)
        
        return timeString
    }
    
    // type 0 : refresh data
    // type 1 : switch on
    // type 2 : swiftch off
    public static func getBytes (_ type: Int, _ macAddress: String) -> String {
        var result = [UInt8]()
        var prefix = __BYTE_SEND_REFRESH
        
        if type == 0 {
            prefix = __BYTE_SEND_REFRESH
        } else {
            prefix = __BYTE_SEND_COMMAND
        }
        
        result.append(prefix)
        result += __BYTE_LEXIN_OUTLETS
        result += getCurrentDateTimeToUInt8Array()
        result += Array(__USER_ID.utf8)
        for _ in __USER_ID.count...19 {
            result.append(0)
        }
        
        result += macAddress.hexaBytes
        
        if type == 1 || type == 2 {
            result.append(1)
            
            if type == 1 {
                result.append(1)
            }
        }
        
        for _ in result.count...59 {
            result.append(0)
        }
        
        return result.hexa
    }
    
    public static func getScheduleBytes (_ sendFlag: UInt8, _ macAddress: String, _ openTime: String, _ closeTime: String, _ commandNumber: UInt8, _ selectedDay: UInt8, _ uniqueNo: UInt8) -> String {
        var result = [UInt8]()
        
        result.append(__BYTE_SEND_SCHEDULE)
        result += __BYTE_LEXIN_OUTLETS
        result += getCurrentDateTimeToUInt8Array()
        result += Array(__USER_ID.utf8)
        for _ in __USER_ID.count...19 {
            result.append(0)
        }
        
        result += macAddress.hexaBytes
        
        result.append(sendFlag) // sendFlag
        result.append(0)
        result.append(0)    // commandNumber
        
        let onTimeBytes = UtilFunctions.getTimeArrayByBytes(openTime)
        let offTimeBytes = UtilFunctions.getTimeArrayByBytes(closeTime)
        if openTime == "" {
            result += [110, 111]
        } else {
            result += onTimeBytes
        }
        if closeTime == "" {
            result += [110, 111]
        } else {
            result += offTimeBytes
        }
        
        result.append(1)
        result.append(selectedDay)  // repeat flag
        
        result.append(1)        // on and off mark
        result.append(uniqueNo) // the unique number of scheduling no
        
        for _ in result.count...(__BYTE_LENGTH_60 - 1) {
            result.append(0)
        }
        
        return result.hexa
    }
    
    public static func getTimeArrayByBytes(_ timeStr: String) -> [UInt8] {
        var result = [UInt8]()
        if timeStr != "" {
            let timeArray = timeStr.split(separator: ":")
            for timeStrValue in timeArray {
                let str = String(timeStrValue)
                result.append(UInt8(str)!)
            }
        } else {
            result = [0, 0]
        }
        
        return result
    }
    
    public static func getBindBytes() -> String {
        var result = [UInt8]()
        
        result += __BYTE_LEXIN_SCAN_WIFI_DEVICE
        result += Array(__USER_ID.utf8)
        
        for _ in result.count...(__BYTE_LENGTH_40 - 1) {
            result.append(0)
        }
        
        return result.hexa
    }
    
    public static func getLastestScheduleUniqueNumber() -> Int {
        var result = 0
        
        if (__SCHEDULES.count > 0)  {
            for schedule in __SCHEDULES {
                if schedule.uniqueNo > result {
                    result = schedule.uniqueNo
                }
            }
        }
        
        return (result + 1)
    }
    
    public static func existCurrentTime(chineseTimeStr: String, uniqueNo: Int, isOn: Bool) -> Bool {
        var result = false
        
        if chineseTimeStr != "" {
            scheduleLoop: for schedule in __SCHEDULES {
                if uniqueNo != schedule.uniqueNo {
                    if isOn {
                        if schedule.startChineseTime == chineseTimeStr {
                            result = true
                        }
                    } else {
                        if schedule.endChineseTime == chineseTimeStr {
                            result = true
                        }
                    }
                }
            }
        }
        
        return result
    }
    
    // ----------------------
    
    
    // -----------------------------
    // ---- Timezone converting ----
    // -----------------------------
    
    public static func getTimestampFromDateTime(_ date: Date) -> String {
        let timestamp = date.timeIntervalSince1970.description.split(separator: ".")[0]
        return String(timestamp)
    }
    
    public static func getDateTimeFromTimeStampString(_ str: String) -> Date {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy'-'MM'-'dd'T'HH':'mm':'ss"
        dateFormatter.timeZone = .current
        let date = dateFormatter.date(from: String.init(format: "2019-01-01T%@:00", str))
        
        return date!
    }
    
    public static func getDateTimeFromTimeStamp(_ timestamp: TimeInterval) -> String {
        var dstTimestamp = timestamp
        if __NN_IS_DST {
            dstTimestamp = timestamp - 3600
        }
        
        let date = Date(timeIntervalSince1970: dstTimestamp)
        let dateFormatter = DateFormatter()
        dateFormatter.timeZone = TimeZone(identifier: __NN_CURRENT_TIME_ZONE) //Set timezone that you want
        dateFormatter.dateFormat = "HH:mm" //Specify your format that you want
        let strDate = dateFormatter.string(from: date)
        return strDate
    }
    
    public static func getCurrentTimezoneIdentifier() {
        let currentGMTOffSet = TimeZone.current.secondsFromGMT()
        let countryCode = (Locale.current as NSLocale).object(forKey: .countryCode) as? String
        
        CommonHttpRequest.getAllTimezone(didFuncLoad: { result, error in
            if error != nil {
                print(error as Any)
                __NN_CURRENT_TIME_ZONE = __NN_CHINA_SHANGHAI_TIME_ZONO_IDENTIFIER
            } else {
                var resultDicData: Dictionary = [String: Any]()
                resultDicData = result as! Dictionary
                if (resultDicData["status"] as! String) != "OK" {
                    __NN_CURRENT_TIME_ZONE = __NN_CHINA_SHANGHAI_TIME_ZONO_IDENTIFIER
                } else {
                    let zones = resultDicData["zones"] as! [[String: Any]]
                    zoneLoop: for zone in zones {
                        if (zone["countryCode"] as! String) == countryCode && (zone["gmtOffset"] as! Int) == currentGMTOffSet {
                            __NN_CURRENT_TIME_ZONE = zone["zoneName"] as! String
                            break zoneLoop
                        }
                    }
                }
            }
        })
    }
    
    public static func getDayDecimal(timeStr: String, offsetTime: Int, _ isMon: Bool, _ isTue: Bool, _ isWen: Bool, _ isThu: Bool, _ isFri: Bool, _ isSat: Bool, _ isSun: Bool) -> UInt8 {
        var result:UInt8 = 0
        
        if isSun && isMon && isTue && isWen && isThu && isFri && isSat {
            result = 128
        } else {
            var binary: String = ""
            
            let offsetHour = offsetTime / 3600
            let strHour = Int(timeStr.split(separator: ":")[0])!
            if (strHour + offsetHour) < 0 {
                if isMon { binary += "1" } else { binary += "0" }
                if isSun { binary += "1" } else { binary += "0" }
                if isSat { binary += "1" } else { binary += "0" }
                if isFri { binary += "1" } else { binary += "0" }
                if isThu { binary += "1" } else { binary += "0" }
                if isWen { binary += "1" } else { binary += "0" }
                if isTue { binary += "1" } else { binary += "0" }
            } else if (strHour + offsetHour) > 23 {
                if isSat { binary += "1" } else { binary += "0" }
                if isFri { binary += "1" } else { binary += "0" }
                if isThu { binary += "1" } else { binary += "0" }
                if isWen { binary += "1" } else { binary += "0" }
                if isTue { binary += "1" } else { binary += "0" }
                if isMon { binary += "1" } else { binary += "0" }
                if isSun { binary += "1" } else { binary += "0" }
            } else {
                if isSun { binary += "1" } else { binary += "0" }
                if isSat { binary += "1" } else { binary += "0" }
                if isFri { binary += "1" } else { binary += "0" }
                if isThu { binary += "1" } else { binary += "0" }
                if isWen { binary += "1" } else { binary += "0" }
                if isTue { binary += "1" } else { binary += "0" }
                if isMon { binary += "1" } else { binary += "0" }
            }
            
            result = UInt8(Int(binary, radix:2)!)
        }
        
        return result
    }
    
    // ----------------------
    
    
    // ----------------------------------
    // ---- Send Refresh & Bind data ----
    // ----------------------------------
    
    public static func sendRefreshData(_ deviceEntity: DeviceEntity, _ macAddress: String) {
        XLinkExportObject.shared()?.initDevice(deviceEntity)
        XLinkExportObject.shared()?.connectDevice(deviceEntity, andAuthKey: __DEVICE_PASSWORD as NSNumber)
        
        let refreshString = UtilFunctions.getBytes(0, macAddress)
        let data = Data(hexString: refreshString)
        XLinkExportObject.shared()?.sendPipeData(deviceEntity, andPayload: data!)
        print(data!.hexa)
    }
    
    public static func sendDataToDevice(_ deviceEntity: DeviceEntity, _ macAddress: String, _ data: Data) {
        XLinkExportObject.shared()?.sendPipeData(deviceEntity, andPayload: data)
        print(data.hexa)
    }
    
    public static func sendSubscriptionData(_ deviceEntity: DeviceEntity, _ andFlag: Int8) {
        XLinkExportObject.shared()?.subscribeDevice(deviceEntity, andAuthKey: __DEVICE_PASSWORD as NSNumber, andFlag: andFlag)
    }
    
    // ----------------------------------
}
