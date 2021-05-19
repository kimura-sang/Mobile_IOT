//
//  DeviceInfo.swift
//  Light Bulb
//
//  Created by king on 2019/9/12.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class DeviceModel: NSObject, Codable {
    
    var deviceUUID: String      // mac address of device
    var deviceImage: String
    var deviceName: String
    var openStatus: Bool
    var onlineStatus: Bool
    
    init(uuid: String, image: String, name: String, status: Bool, online: Bool) {
        self.deviceUUID = uuid
        self.deviceImage = image
        self.deviceName = name
        self.openStatus = status
        self.onlineStatus = online
    }
}
