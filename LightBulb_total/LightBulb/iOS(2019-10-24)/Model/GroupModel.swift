//
//  GroupModel.swift
//  Light Bulb
//
//  Created by king on 2019/9/12.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation

public class GroupModel: NSObject, Codable {
    
    var index: Int
    var groupImage: String
    var groupName: String
    var deviceNames: String
    var deviceUUIDs: [String]
    var groupStatus: Bool
    
    init(id: Int, image: String, name: String, deviceNames: String, uuids: [String], status: Bool) {
        self.index = id
        self.groupImage = image
        self.groupName = name
        self.deviceNames = deviceNames
        self.deviceUUIDs = uuids
        self.groupStatus = status
    }
    
}
