//
//  EditGroupViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField

class EditGroupViewController: UIViewController {
    
    @IBOutlet weak var btnGroupIcon: UIButton!
    @IBOutlet weak var edtGroupName: SkyFloatingLabelTextField!
    @IBOutlet weak var btnDelete: UIButton!
    @IBOutlet weak var navItem: UINavigationItem!
    
    var groupId: Int!
    var editGroupModel: [SelectDeviceModel] = []
    var groupImageName: String! = "AddDeviceDefault"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // print(groupId)
        
        initUI()
        
        editGroupModel = createArray()
    }
    
    func initUI() {
        btnGroupIcon.setImage(UIImage(named: "AddDeviceDefault"), for: .normal)
        btnGroupIcon.layer.cornerRadius = 10
        btnGroupIcon.layer.borderWidth = 2
        btnGroupIcon.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnDelete.layer.cornerRadius = 2
        
        if groupId == -1 {
            navItem.title = "Add Group"
            btnDelete.isHidden = true
        } else {
            navItem.title = "Edit Group"
            btnDelete.isHidden = false
            
            edtGroupName.text = __GROUPS[groupId].groupName
            groupImageName = __GROUPS[groupId].groupImage
            
            setGroupImage()
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickNavSave(_ sender: Any) {
        
        let groupName = edtGroupName.text
        
        if groupImageName == "AddDeviceDefault" {
            QMUITips.showError("Please select group image", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let nameValid = UtilFunctions.nameValidation(groupName!)
        if !nameValid {
            QMUITips.showError("Group name should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let selectedDevices = getSelectedDevices()
        if (selectedDevices[0] as! String) == "" {
            QMUITips.showError("Please select at least two devices", in: self.view, hideAfterDelay: 2)
            return
        } else {
            if (selectedDevices[1] as AnyObject).count < 2 {
                QMUITips.showError("Please select at least two devices", in: self.view, hideAfterDelay: 2)
                return
            }
        }
        
        saveGroup((selectedDevices[0] as! String), (selectedDevices[1] as! [String]))
        
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickDelete(_ sender: Any) {
        UtilFunctions.showConfirmDialog("North Outlet", "You want to delete this group?", "Yes", "No", self, {
            __GROUPS.remove(at: self.groupId)
            
            UtilFunctions.saveUserDeviceData()
            
            // declare & post NOTIFICATION
            NotificationCenter.default.post(name: __NN_CHANGE_GROUP_STATUS, object: nil)
            
            self.dismiss(animated: true, completion: nil)
        }, {
            print("Canceled")
        })
    }
    
    @IBAction func clickImageIcon(_ sender: Any) {
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ImageDialogViewController") as! ImageDialogViewController
        self.addChild(popOverVC)
        popOverVC.view.frame = self.view.frame
        self.view.addSubview(popOverVC.view)
        popOverVC.didMove(toParent: self)
        popOverVC.selectImageDelegate = self
    }
    
    func createArray() -> [SelectDeviceModel]{
        var temp: [SelectDeviceModel] = []
        
        if __DEVICES.count > 0 {
            for i in 0...(__DEVICES.count - 1) {
                var selectedFlag = false
                if __GROUPS.count > 0 && groupId > -1 {
                    selectedFlag = __GROUPS[groupId].deviceMacAddresses.contains(__DEVICES[i].deviceUUID) ? true : false
                }
                temp.append(SelectDeviceModel(id: i, status: selectedFlag, deviceModel: __DEVICES[i]))
            }
        }
        
        return temp
    }
    
    func setGroupImage() {
        btnGroupIcon.setImage(UIImage(named: groupImageName), for: .normal)
    }
    
    func getSelectedDevices() -> [Any] {
        var deviceNames: String! = ""
        var deviceMacs: [String]! = [String]()
        
        for groupEntity in editGroupModel {
            if groupEntity.selectStatus {
                deviceMacs.append(groupEntity.deviceModel.deviceUUID)
                
                if deviceNames != "" {
                    deviceNames += ", "
                }
                deviceNames += groupEntity.deviceModel.deviceName
            }
        }
        
        var result: [Any] = [Any]()
        result.append(deviceNames)
        result.append(deviceMacs)
        
        return result
    }
    
    func getLastGroupId() -> Int {
        var result = 0
        if __GROUPS.count > 0 {
            result = __GROUPS[__GROUPS.count - 1].index + 1
        }
        
        return result
    }
    
    func saveGroup(_ selectedDeviceNames: String, _ selectedDevices: [String]) {
        let groupName = edtGroupName.text
        
        if groupId == -1 {
            let groupModel = GroupModel.init(id: getLastGroupId(), image: groupImageName, name: groupName!, deviceNames: selectedDeviceNames, macAddresses: selectedDevices, status: false)
            
            __GROUPS.append(groupModel)
        } else {
            __GROUPS[groupId].groupName = edtGroupName.text!
            __GROUPS[groupId].groupImage = groupImageName
            __GROUPS[groupId].deviceMacAddresses = selectedDevices
            __GROUPS[groupId].deviceNames = selectedDeviceNames
        }
        
        UtilFunctions.saveUserDeviceData()
        
        // declare & post NOTIFICATION
        NotificationCenter.default.post(name: __NN_CHANGE_GROUP_STATUS, object: nil)
    }
}

extension EditGroupViewController: SelectImageDelegate, UITableViewDelegate, UITableViewDataSource {
    
    func selectImageId(imageId: Int) {
        // print(imageId)
        groupImageName = "image_" + String(imageId)
        setGroupImage()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return editGroupModel.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let editModel = editGroupModel[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "EditGroupCell", for: indexPath) as! EditGroupCell
        
        // modify selection style
        cell.selectionStyle = .none
        
        cell.set(editModel: editModel)
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if let tableCell: EditGroupCell = cell as? EditGroupCell {
            tableCell.imgChecked.image = nil
        }
        
        let editModel = editGroupModel[indexPath.row]
        if editModel.selectStatus {
            (cell as! EditGroupCell).imgChecked.image = UIImage(named: "ItemChecked")
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let tableCell = tableView.cellForRow(at: indexPath)
        if let cell: EditGroupCell = tableCell as? EditGroupCell {
            let editModel = editGroupModel[indexPath.row]
            if editModel.selectStatus {
                cell.imgChecked.image = nil
                editModel.selectStatus = false
            } else  {
                cell.imgChecked.image = UIImage(named: "ItemChecked")            
                editModel.selectStatus = true
            }
        }
    }
}
