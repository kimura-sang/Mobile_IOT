//
//  ScannedDeviceCell.swift
//  Wifi Socket
//
//  Created by king on 2019/6/14.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScannedDeviceCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceUUID: UILabel!
    
    func set(deviceEntity: [String: Any]) {
        imgDevice.image = UIImage(named: "ScannedSocket")
        
        var macAddressStr = String(format: "%@", deviceEntity["macAddress"] as! CVarArg)
        if let i = macAddressStr.firstIndex(of: "<") {
            macAddressStr.remove(at: i)
        }
        if let i = macAddressStr.firstIndex(of: ">") {
            macAddressStr.remove(at: i)
        }
        lblDeviceUUID.text = UtilFunctions.checkEveryString(str: macAddressStr.uppercased())
    }
}
