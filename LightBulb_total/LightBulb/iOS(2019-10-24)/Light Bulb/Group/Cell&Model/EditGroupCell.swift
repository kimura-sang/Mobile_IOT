//
//  EditGroupCell.swift
//  Light Bulb
//
//  Created by king on 2019/9/12.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class EditGroupCell: UITableViewCell {

    @IBOutlet weak var imgDevice: UIImageView!
    @IBOutlet weak var lblDeviceName: UILabel!
    @IBOutlet weak var imgChecked: UIImageView!
    
    func set(editModel: SelectDeviceModel) {
        imgDevice.image = UIImage(named: __DEVICES[editModel.id].deviceImage)
        lblDeviceName.text = __DEVICES[editModel.id].deviceName
        if editModel.selectStatus {
            imgChecked.image = UIImage(named: "ItemChecked")
        } else {
            imgChecked.image = nil
        }
    }
    
    override func prepareForReuse() {
        imgChecked.image = nil
    }

}
