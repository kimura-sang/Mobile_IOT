//
//  TimerCell.swift
//  Light Bulb
//
//  Created by king on 2019/9/13.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class TimerCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceName: UILabel!
    @IBOutlet weak var btnImgStatus: UIButton!
    @IBOutlet weak var lblTimeRemaining: UILabel!
    
    var cellId: Int!
    
    func set(id: Int, timerModel: TimerModel) {
        cellId = id
        imgDevice.image = UIImage(named: timerModel.deviceImage)
        lblDeviceName.text = timerModel.deviceName
        lblTimeRemaining.text = timerModel.deviceName
        
        let timestamp = Int64(Date().timeIntervalSince1970)
        let restTime = timerModel.minute * 60 + timerModel.second - Int(timestamp - timerModel.startDateTimeInt)
        let min = Int(restTime / 60)
        let sec = Int(restTime % 60)
        
        lblTimeRemaining.text = String.init(format: "%@:%@ remaining", UtilFunctions.getFullStringOfInt(min), UtilFunctions.getFullStringOfInt(sec))
        if timerModel.isOpen {
            btnImgStatus.setImage(UIImage(named: "On"), for: .normal)
        } else {
            btnImgStatus.setImage(UIImage(named: "Off"), for: .normal)
        }
        
        btnImgStatus.isUserInteractionEnabled = false
    }
    
}
