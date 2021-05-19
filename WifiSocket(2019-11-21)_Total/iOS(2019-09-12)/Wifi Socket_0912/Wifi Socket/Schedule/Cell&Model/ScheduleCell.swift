//
//  ScheduleCell.swift
//  Wifi Socket
//
//  Created by king on 2019/6/13.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScheduleCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceName: UILabel!
    @IBOutlet weak var lblDeviceType: UILabel!
    @IBOutlet weak var btnOpen: UIButton!
    @IBOutlet weak var lblOpenTime: UILabel!
    @IBOutlet weak var btnClose: UIButton!
    @IBOutlet weak var lblCloseTime: UILabel!
    
    var cellId: Int!
    
    func set(id: Int, scheduleModel: ScheduleModel) {
        cellId = id
        imgDevice.image = UIImage(named: scheduleModel.deviceImage)
        lblDeviceName.text = scheduleModel.deviceName
        lblDeviceType.text = scheduleModel.deviceType
//        if scheduleModel.openTime == "" {
//            btnOpen.isHidden = true
//        }
        lblOpenTime.text = scheduleModel.openTime
//        if scheduleModel.closeTime == "" {
//            btnClose.isHidden = true
//        }
        lblCloseTime.text = scheduleModel.closeTime
        
        btnClose.isUserInteractionEnabled = false
        btnOpen.isUserInteractionEnabled = false
    }
    
}
