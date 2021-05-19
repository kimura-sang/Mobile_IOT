//
//  DeviceCell.swift
//  Wifi Socket
//
//  Created by king on 2019/6/11.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

protocol DeviceDelegate {
    func editDevice(id: Int, name: String)
    func changeStatus(id: Int)
}

class DeviceCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceName: UILabel!
    @IBOutlet weak var lblDeviceType: UILabel!
    @IBOutlet weak var lblStatus: UILabel!
    @IBOutlet weak var btnImage: UIButton!
    @IBOutlet weak var btnEdit: UIButton!
    
    var deviceCellDelegate: DeviceDelegate?
    var cellId: Int!
    
    func set(id: Int, deviceModel: DeviceModel) {
        cellId = id
        imgDevice.image = UIImage(named: deviceModel.deviceImage)
        lblDeviceName.text = deviceModel.deviceName
        lblDeviceType.text = deviceModel.deviceType
        if deviceModel.openStatus == true {
            lblStatus.text = "ON"
            btnImage.isSelected = true
        } else {
            lblStatus.text = "OFF"
            btnImage.isSelected = false
        }
    }
    
    @IBAction func changeStatus(_ sender: Any) {
//        btnImage.isSelected = !btnImage.isSelected
//        if btnImage.isSelected == true {
//            lblStatus.text = "ON"
//        } else {
//            lblStatus.text = "OFF"
//        }
        deviceCellDelegate?.changeStatus(id: cellId)
    }
    
    @IBAction func editDeviceInfo(_ sender: Any) {
        deviceCellDelegate?.editDevice(id: cellId, name: lblDeviceName.text!)
    }
    
}
