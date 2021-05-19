//
//  ScannedDeviceCell.swift
//  Light Bulb
//
//  Created by king on 2019/9/14.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScannedDeviceCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceName: UILabel!
    @IBOutlet weak var lblDeviceUUID: UILabel!
    
    func set(deviceEntity: CBPeripheral) {
        imgDevice.image = UIImage(named: "OpenLight")
        lblDeviceName.text = deviceEntity.name!
        lblDeviceUUID.text = deviceEntity.identifier.uuidString
    }
}
