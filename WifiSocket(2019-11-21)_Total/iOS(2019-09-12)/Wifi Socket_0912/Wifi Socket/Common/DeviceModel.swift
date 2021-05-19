//
//  DeviceInfo.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class DeviceModel: NSObject, Codable {
    
    var deviceUUID: String      // mac address of device
    var deviceImage: String
    var deviceName: String
    var deviceType: String
    var openStatus: Bool
    
    init(uuid: String, image: String, name: String, type: String, status: Bool) {
        self.deviceUUID = uuid
        self.deviceImage = image
        self.deviceName = name
        self.deviceType = type
        self.openStatus = status
    }
}
