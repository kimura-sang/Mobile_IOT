//
//  GroupCell.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

protocol GroupDelegate {
    func editGroup(id: Int, name: String)
    func changeStatus(id: Int, status: Bool)
}

class GroupCell: UITableViewCell {
    
    @IBOutlet weak var imgGroup: UIImageView!
    @IBOutlet weak var lblGroupName: UILabel!
    @IBOutlet weak var lblDeviceNames: UILabel!
    @IBOutlet weak var lblStatus: UILabel!
    @IBOutlet weak var btnImage: UIButton!
    @IBOutlet weak var btnEdit: UIButton!
    
    var groupCellDelegate:GroupDelegate?
    var cellId: Int!
    
    func set(_ index: Int, groupModel: GroupModel) {
        imgGroup.image = UIImage(named: groupModel.groupImage)
        cellId = index
        lblGroupName.text = groupModel.groupName
        lblDeviceNames.text = groupModel.deviceNames
        if groupModel.groupStatus == true {
            lblStatus.text = "ON"
            btnImage.isSelected = true
        } else {
            lblStatus.text = "OFF"
            btnImage.isSelected = false
        }
    }
    
    @IBAction func changeStatus(_ sender: Any) {
        btnImage.isSelected = !btnImage.isSelected
        if btnImage.isSelected == true {
            lblStatus.text = "ON"
        } else {
            lblStatus.text = "OFF"
        }
        groupCellDelegate?.changeStatus(id: cellId, status: btnImage.isSelected)
    }
    
    @IBAction func editGroupInfo(_ sender: Any) {
        groupCellDelegate?.editGroup(id: cellId, name: lblGroupName.text!)
    }

}
