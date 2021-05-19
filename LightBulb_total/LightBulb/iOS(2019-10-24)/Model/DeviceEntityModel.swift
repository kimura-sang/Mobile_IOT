//
//  DeviceEntityModel.swift
//  Wifi Socket
//
//  Created by king on 2019/6/25.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class DeviceEntityModel: DeviceEntity, Codable {
    
    init (_ device: DeviceEntity) {
        super.init(dictionary: device.getDictionaryFormat())
    }
}
