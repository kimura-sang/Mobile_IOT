//
//  ScheduleModel.swift
//  Wifi Socket
//
//  Created by king on 2019/6/27.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class ScheduleModel: NSObject, Codable {
    
    var uniqueNo: Int
    
    var deviceUUID: String      // mac address of device
    var deviceImage: String
    var deviceName: String
    var deviceType: String
    var openTime: String
    var closeTime: String
    
    var startChineseTime: String
    var endChineseTime: String
    
    var startSelectedDate: UInt8
    var endSelectedDate: UInt8
    
    var isSun: Bool = true
    var isMon: Bool = true
    var isTue: Bool = true
    var isWen: Bool = true
    var isThu: Bool = true
    var isFri: Bool = true
    var isSat: Bool = true
    
    var offset: String = ""
    
    init(uniqueNo: Int, uuid: String, image: String, name: String, type: String, openTime: String, closeTime: String, startChnTime: String, endChnTime: String, startSDate: UInt8, endSDate: UInt8, _ isMon: Bool, _ isTue: Bool, _ isWen: Bool, _ isThu: Bool, _ isFri: Bool, _ isSat: Bool, _ isSun: Bool, _ offset: String) {
        
        self.uniqueNo = uniqueNo
        
        self.deviceUUID = uuid
        self.deviceImage = image
        self.deviceName = name
        self.deviceType = type
        self.openTime = openTime
        self.closeTime = closeTime
        
        self.startChineseTime = startChnTime
        self.endChineseTime = endChnTime
        
        self.startSelectedDate = startSDate
        self.endSelectedDate = endSDate
        
        self.isMon = isMon
        self.isTue = isTue
        self.isWen = isWen
        self.isThu = isThu
        self.isFri = isFri
        self.isSat = isSat
        self.isSun = isSun
        
        self.offset = offset
    }
}

