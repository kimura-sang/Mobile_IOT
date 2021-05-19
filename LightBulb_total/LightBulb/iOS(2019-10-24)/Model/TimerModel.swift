//
//  TimerModel.swift
//  Light Bulb
//
//  Created by king on 2019/9/27.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class TimerModel: NSObject, Codable {
    
    var deviceUUID: String      // mac address of device
    var deviceImage: String
    var deviceName: String
    var isOpen: Bool
    var startDateTimeInt: Int64
    var minute: Int
    var second: Int
    
    init(uuid: String, image: String, name: String, isOp: Bool, startTimeInt: Int64, min: Int, sec: Int) {
        self.deviceUUID = uuid
        self.deviceImage = image
        self.deviceName = name
        self.isOpen = isOp
        self.startDateTimeInt = startTimeInt
        self.minute = min
        self.second = sec
    }
}

