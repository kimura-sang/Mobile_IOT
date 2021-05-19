//
//  UtilFunctions.swift
//  Light Bulb
//
//  Created by king on 2019/9/5.
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
    
    public static func preventTouchEvent() {
        UIApplication.shared.beginIgnoringInteractionEvents()
    }
    
    public static func restoreTouchEvent() {
        UIApplication.shared.endIgnoringInteractionEvents()
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
    
    // -------------------------
    
    
    // ------------------
    // --- Validation ---
    // ------------------
    
    public static func nameValidation(_ name: String) -> Bool {
        var result = false;
        let length = name.count
        
        if length > 0 {
            result = true
        }
        
        return result
    }
    
    // ------------------
    
    
    // -----------------------------------
    // --- Management of User Defaults ---
    // -----------------------------------
    
    public static func dumpUserDefaults() {
        for (key, value) in UserDefaults.standard.dictionaryRepresentation() {
            print("\(key) = \(value) \n")
        }
    }
    
    public static func removeUserDefaultDataByKey(key: String) {
        UserDefaults.standard.removeObject(forKey: key)
        UserDefaults.standard.synchronize()
    }
    
    public static func getDeviceDataFromUserDefaults() {
        if UserDefaults.standard.dictionary(forKey: __STR_DEVICE_DATA) != nil {
            UtilFunctions.loadUserDeviceData()
        } else {
            __USER_DEVICE_DATA = [String: Any]()
            __DEVICES = []
            __GROUPS = []
            __TIMERS = []
            __CACHE_DEVICE_ENTITIES = []
            __DEVICE_ENTITIES = []
        }
    }
    
    public static func syncUserData() {
        saveUserDeviceData()
        loadUserDeviceData()
    }
    
    public static func saveUserDeviceData() {
        __USER_DEVICE_DATA["localDevices"] = try? JSONEncoder().encode(__DEVICES)
        __USER_DEVICE_DATA["localGroups"] = try? JSONEncoder().encode(__GROUPS)
        __USER_DEVICE_DATA["localTimers"] = try? JSONEncoder().encode(__TIMERS)
        UserDefaults.standard.set(__USER_DEVICE_DATA, forKey: __STR_DEVICE_DATA)
        UserDefaults.standard.synchronize()
    }
    
    public static func loadUserDeviceData() {
        __USER_DEVICE_DATA = UserDefaults.standard.dictionary(forKey: __STR_DEVICE_DATA)!
        
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
        
        if __USER_DEVICE_DATA["localTimers"] != nil {
            do {
                __TIMERS = try JSONDecoder().decode(Array.self, from: __USER_DEVICE_DATA["localTimers"] as! Data)
            } catch let error {
                __TIMERS = []
                print(error.localizedDescription)
            }
        } else {
            __TIMERS = []
        }
    }
    
    public static func deleteFromLocalDevice(_ index: Int) {
        let deviceUUID = __DEVICES[index].deviceUUID
        
        __DEVICES.remove(at: index)
        
        if (__DEVICE_ENTITIES.count > 0) {
            deleteCacheLoop: for i in 0...(__DEVICE_ENTITIES.count - 1) {
                if __DEVICE_ENTITIES[i].identifier.uuidString == deviceUUID {
                    __CACHE_DEVICE_ENTITIES.append(__DEVICE_ENTITIES[i])
                    break deleteCacheLoop
                }
            }
        }
        
        if __TIMERS.count > 0 {
            for timerEntity in __TIMERS {
                if timerEntity.deviceUUID == deviceUUID {
                    let index = __TIMERS.index(of: timerEntity)
                    __TIMERS.remove(at: index!)
                }
            }
        }
        
        UtilFunctions.syncUserData()
    }
    
    public static func deleteFromDeviceEntity(_ deviceUUID: String) {
        if __CACHE_DEVICE_ENTITIES.count > 0 {
            deleteLoop: for i in 0...(__CACHE_DEVICE_ENTITIES.count - 1) {
                if __CACHE_DEVICE_ENTITIES[i].identifier.uuidString == deviceUUID {
                    __CACHE_DEVICE_ENTITIES.remove(at: i)
                    break deleteLoop
                }
            }
        }
    }
    
    public static func setDeviceStatus(uuid: String, status: Bool, needSave: Bool) {
        loop: for i in 0...(__DEVICES.count - 1) {
            if __DEVICES[i].deviceUUID == uuid {
                __DEVICES[i].openStatus = status
                break loop
            }
        }
        
        if needSave {
            UtilFunctions.syncUserData()
        }
    }
    
    // -----------------------------------
    
    
    // ----------------------------------
    // ---- Send Refresh & Bind data ----
    // ----------------------------------
    
    public static func searchBLEDevices() {
        __CB_CENTRAL_MANAGER.scanForPeripherals(withServices: nil, options: nil)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            __CB_CENTRAL_MANAGER.stopScan()
            NotificationCenter.default.post(name: __NN_BLE_DEVICE_SCANNED, object: nil)
        }
    }
    
    public static func sendBLEData(_ peripheral: CBPeripheral, _ string: String) {
        let data = String(string).data(using: .utf8)
        
        for service in peripheral.services! {
            if service.characteristics != nil {
                for chars in service.characteristics! {
                    peripheral.writeValue(data!, for: chars, type: CBCharacteristicWriteType.withResponse)
                }
            }
        }
    }
    
    // ----------------------------------
}
