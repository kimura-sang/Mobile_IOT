//
//  Extentions.swift
//  Wifi Socket
//
//  Created by king on 2019/6/7.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation
import SkyFloatingLabelTextField


extension AppDelegate: XlinkExportObjectDelegate {
    
    func onStart() {
        print("---------------onStart-----------------------")
    }
    
    func onLogin(_ result: Int32) {
        print("---------------onLogin-----------------------")
        print(result)
    }
    
    func onGotDevice(byScan device: DeviceEntity!) {
        print("---------------onGotDevice-----------------------")
        
        if !device.isDeviceInitted() {
            if device.version == 1 {
                
            } else {
                let result = XLinkExportObject.shared()?.setAccessKey(8888, withDevice: device)
                print("SetAccessKey Result : \(result!)")
            }
        }
        
        var existLocalDevice = false
        var existCacheEntity = false
        var existRealEntity = false
        let deviceDictionary = device.getDictionaryFormat()
        
        deviceEntityLoop: for deviceModel in __DEVICES {
            if deviceModel.deviceUUID == (deviceDictionary!["macAddress"] as! String) {
                existLocalDevice = true
                
                break deviceEntityLoop
            }
        }
        
        cacheLoop: for cacheDeviceEntity in __CACHE_DEVICE_ENTITIES {
            if (cacheDeviceEntity["macAddress"] as! String) == (deviceDictionary!["macAddress"] as! String) {
                existCacheEntity = true
                
                break cacheLoop
            }
        }
        if !existCacheEntity {
            __CACHE_DEVICE_ENTITIES.append(device.getDictionaryFormat())
        }
        
        if !existLocalDevice {
            localDeviceLoop: for deviceEntity in __DEVICE_ENTITIES {
                if (deviceEntity["macAddress"] as! String) == (deviceDictionary!["macAddress"] as! String) {
                    existCacheEntity = true
                    
                    break localDeviceLoop
                }
            }
            
            if !existCacheEntity {
                __DEVICE_ENTITIES.append(deviceDictionary as! [String : Any])
                UserDefaults.standard.set(__DEVICE_ENTITIES, forKey: "deviceEntities")
                UserDefaults.standard.synchronize()
            }
        }
        
        realEntityLoop: for realEntity in __REAL_DEVICE_ENTITIES {
            let realDicData = realEntity.getDictionaryFormat()
            if (realDicData!["macAddress"] as! String) == (deviceDictionary!["macAddress"] as! String) {
                existRealEntity = true
                break realEntityLoop
            }
        }
        if !existRealEntity {
            __REAL_DEVICE_ENTITIES.append(device)
        }
        
        if !(device!.isConnected) || !(device!.isConnecting) {
            XLinkExportObject.shared()?.initDevice(device)
            XLinkExportObject.shared()?.connectDevice(device, andAuthKey: __DEVICE_PASSWORD as NSNumber)
        }
    }
    
    func onSetDeviceAccessKey(_ device: DeviceEntity!, withResult result: UInt8, withMessageID messageID: UInt16) {
        print("---------------onSetDeviceAccessKey-----------------------")
    }
    
    func onSetLocalDeviceAuthorizeCode(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
        print("---------------onSetLocalDeviceAuthorizeCode-----------------------")
    }
    
    func onSetDeviceAuthorizeCode(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
        print("---------------onSetDeviceAuthorizeCode-----------------------")
    }
    
    func onSendLocalPipeData(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
        print("---------------onSendLocalPipeData-----------------------")
//        print(result)
    }
    
    func onSendPipeData(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
//        print("---------------onSendPipeData-----------------------")
//        print(result)
        print(String.init(format: "--onSendPipeData : Result-- %d", result))
    }
    
    func onRecvLocalPipeData(_ device: DeviceEntity!, withPayload data: Data!) {
        print("---------------onRecvLocalPipeData-----------------------")
//        print(data.hexa)
    }
    
    func onRecvPipeData(_ device: DeviceEntity!, withMsgID msgID: UInt16, withPayload payload: Data!) {
//        print("---------------onRecvPipeData-----------------------")
//        print(payload.hexa)
        print(String.init(format: "--onRecvPipeData-- %@", payload.hexa))
    }
    
    func onRecvPipeSyncData(_ device: DeviceEntity!, withPayload payload: Data!) {
        print("---------------onRecvPipeSyncData-----------------------")
        
        if __DEVICES.count > 0 {
            let macAddress = (device.getDictionaryFormat()!["macAddress"] as! String)
            saveStatusLoop: for i in 0...(__DEVICES.count - 1) {
                if __DEVICES[i].deviceUUID == macAddress {
                    if ((Array(payload)[43] == 0) && __DEVICES[i].openStatus) || ((Array(payload)[43] == 1) && !__DEVICES[i].openStatus) {
//                        print("----onRecvPipeSyncData_Sub----")
//                        print(payload.hexa)
                        print(String.init(format: "--onRecvPipeSyncData_Sub-- %@", payload.hexa))
                        
                        if Array(payload)[43] == 0 {
                            __DEVICES[i].openStatus = false
                        } else {
                            __DEVICES[i].openStatus = true
                        }
                        
                        UtilFunctions.syncUserData()
                        
                        // declare & post NOTIFICATION
                        NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)
                        // NotificationCenter.default.post(name: __NN_CHANGE_GROUP_STATUS, object: nil)
                    }
                    
                    break saveStatusLoop
                }
            }
        }
    }
    
    func onDeviceProbe(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
        print("---------------onDeviceProbe-----------------------")
    }
    
    func onConnectDevice(_ device: DeviceEntity!, andResult result: Int32, andTaskID taskID: Int32) {
        print("---------------onConnectDevice-----------------------")
//        print(result)
//        print(device.isConnected)
//        print(device.isConnecting)
//        print(device.isLANOnline)
//        print(device.isWANOnline)
        
        UtilFunctions.sendSubscriptionData(device, 1)
    }
    
    func onSubscription(_ device: DeviceEntity!, withResult result: Int32, withMessageID messageID: Int32) {
        print("---------------onSubscription-----------------------")
        print(result)
        
        if result == 10 {
            let devDicData = device.getDictionaryFormat()
            let macAddress = ["macAddress": devDicData!["macAddress"]]
            NotificationCenter.default.post(name: __NN_DEVICE_OFFLINE_STATUS, object: nil, userInfo: macAddress as [AnyHashable : Any])
        }
    }
    
    func onDeviceStatusChanged(_ device: DeviceEntity!) {
        print("---------------onDeviceStatusChanged-----------------------")
    }
    
}



fileprivate var disclosureButtonAction: (() -> Void)?

extension SkyFloatingLabelTextField {
    
    func add(disclosureButton button: UIButton, action: @escaping (() -> Void)) {
        let selector = #selector(disclosureButtonPressed)
        if disclosureButtonAction != nil, let previousButton = rightView as? UIButton {
            previousButton.removeTarget(self, action: selector, for: .touchUpInside)
        }
        disclosureButtonAction = action
        button.addTarget(self, action: selector, for: .touchUpInside)
        rightView = button
    }
    
    func extendType() {
        self.rightViewMode = .whileEditing
        self.isSecureTextEntry = true
        self.autocapitalizationType = .none
        
        let disclosureButton = UIButton(type: .custom)
        disclosureButton.frame = CGRect(origin: CGPoint.zero, size: CGSize(width: 40, height: 40))
        
        let normalImage = UIImage(named: "EyeSlash")
        let selectedImage = UIImage(named: "EyeOpen")
        disclosureButton.setImage(normalImage, for: .normal)
        disclosureButton.setImage(selectedImage, for: .selected)
        self.add(disclosureButton: disclosureButton) {
            disclosureButton.isSelected = !disclosureButton.isSelected
            self.resignFirstResponder()
            self.isSecureTextEntry = !self.isSecureTextEntry
            self.becomeFirstResponder()
        }
    }
    
    @objc fileprivate func disclosureButtonPressed() {
        disclosureButtonAction?()
    }
}

extension UIDevice {
    
    static let modelName: String = {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }
        
        func mapToDevice(identifier: String) -> String { // swiftlint:disable:this cyclomatic_complexity
            #if os(iOS)
            switch identifier {
            case "iPod5,1":                                 return "iPod Touch 5"
            case "iPod7,1":                                 return "iPod Touch 6"
            case "iPhone3,1", "iPhone3,2", "iPhone3,3":     return "iPhone 4"
            case "iPhone4,1":                               return "iPhone 4s"
            case "iPhone5,1", "iPhone5,2":                  return "iPhone 5"
            case "iPhone5,3", "iPhone5,4":                  return "iPhone 5c"
            case "iPhone6,1", "iPhone6,2":                  return "iPhone 5s"
            case "iPhone7,2":                               return "iPhone 6"
            case "iPhone7,1":                               return "iPhone 6 Plus"
            case "iPhone8,1":                               return "iPhone 6s"
            case "iPhone8,2":                               return "iPhone 6s Plus"
            case "iPhone9,1", "iPhone9,3":                  return "iPhone 7"
            case "iPhone9,2", "iPhone9,4":                  return "iPhone 7 Plus"
            case "iPhone8,4":                               return "iPhone SE"
            case "iPhone10,1", "iPhone10,4":                return "iPhone 8"
            case "iPhone10,2", "iPhone10,5":                return "iPhone 8 Plus"
            case "iPhone10,3", "iPhone10,6":                return "iPhone X"
            case "iPhone11,2":                              return "iPhone XS"
            case "iPhone11,4", "iPhone11,6":                return "iPhone XS Max"
            case "iPhone11,8":                              return "iPhone XR"
            case "iPad2,1", "iPad2,2", "iPad2,3", "iPad2,4":return "iPad 2"
            case "iPad3,1", "iPad3,2", "iPad3,3":           return "iPad 3"
            case "iPad3,4", "iPad3,5", "iPad3,6":           return "iPad 4"
            case "iPad4,1", "iPad4,2", "iPad4,3":           return "iPad Air"
            case "iPad5,3", "iPad5,4":                      return "iPad Air 2"
            case "iPad6,11", "iPad6,12":                    return "iPad 5"
            case "iPad7,5", "iPad7,6":                      return "iPad 6"
            case "iPad11,4", "iPad11,5":                    return "iPad Air (3rd generation)"
            case "iPad2,5", "iPad2,6", "iPad2,7":           return "iPad Mini"
            case "iPad4,4", "iPad4,5", "iPad4,6":           return "iPad Mini 2"
            case "iPad4,7", "iPad4,8", "iPad4,9":           return "iPad Mini 3"
            case "iPad5,1", "iPad5,2":                      return "iPad Mini 4"
            case "iPad11,1", "iPad11,2":                    return "iPad Mini 5"
            case "iPad6,3", "iPad6,4":                      return "iPad Pro (9.7-inch)"
            case "iPad6,7", "iPad6,8":                      return "iPad Pro (12.9-inch)"
            case "iPad7,1", "iPad7,2":                      return "iPad Pro (12.9-inch) (2nd generation)"
            case "iPad7,3", "iPad7,4":                      return "iPad Pro (10.5-inch)"
            case "iPad8,1", "iPad8,2", "iPad8,3", "iPad8,4":return "iPad Pro (11-inch)"
            case "iPad8,5", "iPad8,6", "iPad8,7", "iPad8,8":return "iPad Pro (12.9-inch) (3rd generation)"
            case "AppleTV5,3":                              return "Apple TV"
            case "AppleTV6,2":                              return "Apple TV 4K"
            case "AudioAccessory1,1":                       return "HomePod"
            case "i386", "x86_64":                          return "Simulator \(mapToDevice(identifier: ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "iOS"))"
            default:                                        return identifier
            }
            #elseif os(tvOS)
            switch identifier {
            case "AppleTV5,3": return "Apple TV 4"
            case "AppleTV6,2": return "Apple TV 4K"
            case "i386", "x86_64": return "Simulator \(mapToDevice(identifier: ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "tvOS"))"
            default: return identifier
            }
            #endif
        }
        
        return mapToDevice(identifier: identifier)
    }()
    
}

extension UIColor {
    public convenience init?(hex: String) {
        let r, g, b, a: CGFloat
        
        if hex.hasPrefix("#") {
            let start = hex.index(hex.startIndex, offsetBy: 1)
            let hexColor = String(hex[start...])
            
            if hexColor.count == 8 {
                let scanner = Scanner(string: hexColor)
                var hexNumber: UInt64 = 0
                
                if scanner.scanHexInt64(&hexNumber) {
                    r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
                    g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
                    b = CGFloat((hexNumber & 0x0000ff00) >> 8) / 255
                    a = CGFloat(hexNumber & 0x000000ff) / 255
                    
                    self.init(red: r, green: g, blue: b, alpha: a)
                    return
                }
            }
        }
        
        return nil
    }
}

extension UIViewController {
    var appDelegate: AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
}

extension Int {
    // MARK:- 转成 2位byte
    func hw_to2Bytes() -> [UInt8] {
        let UInt = UInt16.init(Double.init(self))
        return [UInt8(truncatingIfNeeded: UInt >> 8),UInt8(truncatingIfNeeded: UInt)]
    }
    // MARK:- 转成 4字节的bytes
    func hw_to4Bytes() -> [UInt8] {
        let UInt = UInt32.init(Double.init(self))
        return [UInt8(truncatingIfNeeded: UInt >> 24),
                UInt8(truncatingIfNeeded: UInt >> 16),
                UInt8(truncatingIfNeeded: UInt >> 8),
                UInt8(truncatingIfNeeded: UInt)]
//        return [UInt8(truncatingIfNeeded: UInt >> 3),
//                UInt8(truncatingIfNeeded: UInt >> 11),
//                UInt8(truncatingIfNeeded: UInt >> 19),
//                UInt8(truncatingIfNeeded: UInt >> 27)]
    }
    // MARK:- 转成 8位 bytes
    func intToEightBytes() -> [UInt8] {
        let UInt = UInt64.init(Double.init(self))
        return [UInt8(truncatingIfNeeded: UInt >> 56),
                UInt8(truncatingIfNeeded: UInt >> 48),
                UInt8(truncatingIfNeeded: UInt >> 40),
                UInt8(truncatingIfNeeded: UInt >> 32),
                UInt8(truncatingIfNeeded: UInt >> 24),
                UInt8(truncatingIfNeeded: UInt >> 16),
                UInt8(truncatingIfNeeded: UInt >> 8),
                UInt8(truncatingIfNeeded: UInt)]
    }
}

extension String {
    var hexaBytes: [UInt8] {
        var position = startIndex
        return (0..<count/2).flatMap { _ in    // for Swift 4.1 or later use compactMap instead of flatMap
            defer { position = index(position, offsetBy: 2) }
            return UInt8(self[position...index(after: position)], radix: 16)
        }
    }
    var hexaData: Data { return hexaBytes.data }
}

extension Collection where Element == UInt8 {
    var data: Data {
        return Data(self)
    }
    var hexa: String {
        return map{ String(format: "%02X", $0) }.joined()
    }
}

// array to dictionary
extension Array {
    public func toDictionary<Key: Hashable>(with selectKey: (Element) -> Key) -> [Key:Element] {
        var dict = [Key:Element]()
        for element in self {
            dict[selectKey(element)] = element
        }
        return dict
    }
}
