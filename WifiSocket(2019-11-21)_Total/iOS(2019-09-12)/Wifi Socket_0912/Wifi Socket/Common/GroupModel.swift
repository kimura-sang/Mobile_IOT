//
//  GroupModel.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class GroupModel: NSObject, Codable {
    
    var index: Int
    var groupImage: String
    var groupName: String
    var deviceNames: String
    var deviceMacAddresses: [String]
    var groupStatus: Bool
    
    init(id: Int, image: String, name: String, deviceNames: String, macAddresses: [String], status: Bool) {
        self.index = id
        self.groupImage = image
        self.groupName = name
        self.deviceNames = deviceNames
        self.deviceMacAddresses = macAddresses
        self.groupStatus = status
    }
    
}
