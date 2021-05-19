//
//  DevEntityModel.swift
//  Wifi Socket
//
//  Created by king on 2019/6/25.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public struct DevEntityModel<AnyHashable: Any>: Codable {
    
    var deviceName: String
    var macAddress: Data
    var devicePort: Int32
    var fromIP: String
    var version: Int8
    var productID: String
    var deviceType: UInt16
    var mcuHardVersion: Int32
    var flag: Int32
    var deviceID: Int32
    var accessKey: Int
    var mcuSoftVersion: Int32
    
    public mutating func setData(_ device: DeviceEntity) -> DevEntityModel! {
        deviceName = device.deviceName
        macAddress = device.macAddress
        devicePort = device.devicePort
        fromIP = device.fromIP
        version = device.version
        productID = device.productID
        deviceType = device.deviceType
        mcuHardVersion = device.mcuHardVersion
        flag = device.flag
        deviceID = device.deviceID
        accessKey = device.accessKey as! Int
        mcuSoftVersion = device.mcuSoftVersion
        
        return self
    }
    
}
