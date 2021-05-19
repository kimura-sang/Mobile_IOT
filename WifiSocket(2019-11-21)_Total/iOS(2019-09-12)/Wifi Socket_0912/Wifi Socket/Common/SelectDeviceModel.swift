//
//  EditGroupModel.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class SelectDeviceModel {
    
    var id: Int
    var selectStatus: Bool
    var deviceModel: DeviceModel
    
    init(id: Int, status: Bool, deviceModel: DeviceModel) {
        self.id = id
        self.selectStatus = status
        self.deviceModel = deviceModel
    }
    
}
