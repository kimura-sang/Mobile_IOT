//
//  WifiManager.swift
//  Wifi Socket
//
//  Created by king on 2019/6/7.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation
import SystemConfiguration.CaptiveNetwork

struct WifiInfo {
    public let interface:String
    public let ssid:String
    public let bssid:String
    init(_ interface:String, _ ssid:String,_ bssid:String) {
        self.interface = interface
        self.ssid = ssid
        self.bssid = bssid
    }
}

struct WifiStruct: Codable {
    var ssid: String
    var bssid: String
    var password: String
}

class WifiManager {
    
    public static func getWifiInfo() -> Array<WifiInfo> {
        guard let interfaceNames = CNCopySupportedInterfaces() as? [String] else {
            return []
        }
        let wifiInfo:[WifiInfo] = interfaceNames.compactMap{ name in
            guard let info = CNCopyCurrentNetworkInfo(name as CFString) as? [String:AnyObject] else {
                return nil
            }
            guard let ssid = info[kCNNetworkInfoKeySSID as String] as? String else {
                return nil
            }
            guard let bssid = info[kCNNetworkInfoKeyBSSID as String] as? String else {
                return nil
            }
            return WifiInfo(name, ssid, bssid)
        }
        return wifiInfo
    }
    
    public static func getSSID() -> String? {
        var ssid: String?
        if let interfaces = CNCopySupportedInterfaces() as NSArray? {
            for interface in interfaces {
                if let interfaceInfo = CNCopyCurrentNetworkInfo(interface as! CFString) as NSDictionary? {
                    ssid = interfaceInfo[kCNNetworkInfoKeySSID as String] as? String
                    break
                }
            }
        }
        return ssid
    }
    
    public static func getBSSID() -> String? {
        var bssid: String = ""
        if let interfaces = CNCopySupportedInterfaces() as NSArray? {
            for interface in interfaces {
                if let interfaceInfo = CNCopyCurrentNetworkInfo(interface as! CFString) as NSDictionary? {
                    bssid = interfaceInfo[kCNNetworkInfoKeyBSSID as String] as! String
                    break
                }
            }
        }
        return bssid
    }
    
    public static func getWifiInfo() -> [WifiStruct] {
        var wifiInfoList: [WifiStruct] = []
        
        if UserDefaults.standard.data(forKey: "WifiInfo") != nil {
            do {
                wifiInfoList = try JSONDecoder().decode(Array.self, from: UserDefaults.standard.data(forKey: "WifiInfo")!)
            } catch let error {
                print(error.localizedDescription)
            }
        }
        
        return wifiInfoList
    }
    
    public static func saveWifiInfo(_ ssid: String, _ bssid: String, _ password: String) {
        var wifiInfoList: [WifiStruct] = WifiManager.getWifiInfo()
        
        var isExistSSID = false
        if wifiInfoList.count > 0 {
            ssidLoop: for i in 0...(wifiInfoList.count - 1) {
                if wifiInfoList[i].ssid == ssid {
                    isExistSSID = true
                    
                    wifiInfoList[i].bssid = bssid
                    wifiInfoList[i].password = password
                    
                    break ssidLoop
                }
            }
        }
        
        if !isExistSSID {
            wifiInfoList.append(WifiStruct(ssid: ssid, bssid: bssid, password: password))
        }
        
        UserDefaults.standard.set(try? JSONEncoder().encode(wifiInfoList), forKey: "WifiInfo")
        UserDefaults.standard.synchronize()
    }
    
    public static func loadWifiInfo(_ ssid: String) -> WifiStruct {
        let wifiInfoList: [WifiStruct] = WifiManager.getWifiInfo()
        var wifiInfo: WifiStruct = WifiStruct(ssid: ssid, bssid: "", password: "")
        
        if wifiInfoList.count > 0 {
            ssidLoop: for i in 0...(wifiInfoList.count - 1) {
                if wifiInfoList[i].ssid == ssid {
                    
                    wifiInfo.bssid = wifiInfoList[i].bssid
                    wifiInfo.password = wifiInfoList[i].password
                    
                    break ssidLoop
                }
            }
        }
        
        return wifiInfo
    }
}



