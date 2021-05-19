//
//  Extentions.swift
//  Light Bulb
//
//  Created by king on 2019/9/7.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation
import SkyFloatingLabelTextField

fileprivate var disclosureButtonAction: (() -> Void)?

extension AppDelegate: CBCentralManagerDelegate, CBPeripheralDelegate {
    
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        var msg = ""
        
        switch central.state {
        case .poweredOff:
            msg = "Bluetooth Off"
        case .poweredOn:
            msg = "BLE On"
        case .unsupported:
            msg = "BLE unsupported"
        default:
            break
        }
        
        print("State: \(msg)")
    }
    
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        if peripheral.identifier.description == "" || peripheral.name == nil {
            return
        }
        
        if  (peripheral.name?.contains("M_light")) != false
        {
            let rssi = abs(RSSI.intValue)
            let power: Double = Double(rssi - 59)/(10*2.0)
            print("power: @d", power)
            
            __CB_CURRENT_PERIPHERAL = peripheral
            
            var existInDeviceEntities = false
            if __DEVICE_ENTITIES.count > 0 {
                existEntityLoop: for periph in __DEVICE_ENTITIES {
                    if periph.identifier.uuidString == peripheral.identifier.uuidString {
                        existInDeviceEntities = true
                        break existEntityLoop
                    }
                }
            }
            if !existInDeviceEntities {
                __DEVICE_ENTITIES.append(peripheral)
            }
            
            var existInDevices = false
            if __DEVICES.count > 0 {
                existLoop: for i in 0...(__DEVICES.count - 1) {
                    if __DEVICES[i].deviceUUID == peripheral.identifier.uuidString {
                        existInDevices = true
                        break existLoop
                    }
                }
            }
            
            var existInCacheDeviceEntities = false
            if !existInDevices {
                if __CACHE_DEVICE_ENTITIES.count > 0 {
                    existLoop: for periph in __CACHE_DEVICE_ENTITIES {
                        if periph.identifier.uuidString == peripheral.identifier.uuidString {
                            existInCacheDeviceEntities = true
                            break existLoop
                        }
                    }
                }
            } else {
                existInCacheDeviceEntities = true
            }
            
            if !existInCacheDeviceEntities {
                __CACHE_DEVICE_ENTITIES.append(peripheral)
            }
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                __CB_CENTRAL_MANAGER.connect(peripheral, options: nil)
            }
            
            UtilFunctions.syncUserData()
        }
    }
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("connect peripheral: @%", peripheral.name!)
        
        if __DEVICES.count > 0 {
            existLoop: for i in 0...(__DEVICES.count - 1) {
                if __DEVICES[i].deviceUUID == peripheral.identifier.uuidString {
                    __DEVICES[i].onlineStatus = true
                    UtilFunctions.syncUserData()
                    break existLoop
                }
            }
        }
        
        peripheral.discoverServices(nil)
        peripheral.delegate = self
    }
    
    private func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: NSError?) {
        print(error as Any)
    }
    
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        print(peripheral.state.rawValue)
        
        let uuid = ["deviceUUID": peripheral.identifier.uuidString]
        NotificationCenter.default.post(name: __NN_DEVICE_OFFLINE_STATUS, object: nil, userInfo: uuid as [AnyHashable : Any])
        
        __CB_CENTRAL_MANAGER.connect(peripheral, options: nil)
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        print("success services: @%", peripheral.name!)
        
        for service in peripheral.services! {
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        print("success characteires: @%", peripheral.name!)
        
        UtilFunctions.sendBLEData(peripheral, "mk:0000")
    }
}

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
