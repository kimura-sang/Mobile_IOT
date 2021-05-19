//
//  EditDeviceViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/11.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField

class EditDeviceViewController: UIViewController {
    
    @IBOutlet weak var btnDeviceIcon: UIButton!
    @IBOutlet weak var edtDeviceName: SkyFloatingLabelTextField!
    @IBOutlet weak var btnDelete: UIButton!
    @IBOutlet weak var navItem: UINavigationItem!
    
    var deviceId: Int!
    var deviceUUID: String!
    var deviceImageName: String! = "AddDeviceDefault"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initUI()
    }
    
    func initUI() {
        btnDeviceIcon.setImage(UIImage(named: "AddDeviceDefault"), for: .normal)
        btnDeviceIcon.layer.cornerRadius = 10
        btnDeviceIcon.layer.borderWidth = 2
        btnDeviceIcon.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnDelete.layer.cornerRadius = 2
        
        if deviceId == -1 {
            navItem.title = "Add Device"
            btnDelete.isHidden = true
        } else {
            navItem.title = "Edit Device"
            btnDelete.isHidden = false
            
            edtDeviceName.text = __DEVICES[deviceId].deviceName
            deviceUUID = __DEVICES[deviceId].deviceUUID
            deviceImageName = __DEVICES[deviceId].deviceImage
            
            setDeviceImage()
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickNavSave(_ sender: Any) {
        // hide keyboard
        self.view.endEditing(true)
        
        let deviceName = edtDeviceName.text
        
        if deviceImageName == "AddDeviceDefault" {
            QMUITips.showError("Please select device image", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let nameValid = UtilFunctions.nameValidation(deviceName!)
        if !nameValid {
            QMUITips.showError("Device name should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }
        
        saveDeviceEntity(deviceName!)
        
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickImageIcon(_ sender: Any) {
        let popOverVC = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "ImageDialogViewController") as! ImageDialogViewController
        self.addChild(popOverVC)
        
        popOverVC.view.frame = self.view.frame
        self.view.addSubview(popOverVC.view)
        
        popOverVC.didMove(toParent: self)
        popOverVC.selectImageDelegate = self
    }
    
    @IBAction func clickDelete(_ sender: Any) {
        UtilFunctions.showConfirmDialog("North Bulb", "You want to delete this device?", "Yes", "No", self, {
            // print("Deleted!")
            UtilFunctions.deleteFromLocalDevice(self.deviceId)
            
            // declare & post NOTIFICATION
            NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)

            self.dismiss(animated: true, completion: nil)
        }, {
            // print("Canceled")
        })
    }
    
    
    func saveDeviceEntity(_ deviceName: String) {
        if deviceId == -1 {
            let deviceModel = DeviceModel(uuid: deviceUUID, image: deviceImageName, name: deviceName, status: true, online: true)
            
            __DEVICES.append(deviceModel)
            
            UtilFunctions.deleteFromDeviceEntity(deviceUUID)
        } else {
            __DEVICES[deviceId].deviceName = edtDeviceName.text!
            __DEVICES[deviceId].deviceUUID = deviceUUID
            __DEVICES[deviceId].deviceImage = deviceImageName
            
            if __TIMERS.count > 0 {
                for i in 0...(__TIMERS.count - 1) {
                    if __TIMERS[i].deviceUUID == deviceUUID {
                        __TIMERS[i].deviceName = edtDeviceName.text!
                        __TIMERS[i].deviceImage = deviceImageName
                    }
                }
            }
        }
        
        UtilFunctions.saveUserDeviceData()
        
        // declare & post NOTIFICATION
        NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)
    }
    
    func setDeviceImage() {
        btnDeviceIcon.setImage(UIImage(named: deviceImageName), for: .normal)
    }
    
}

extension EditDeviceViewController: SelectImageDelegate {
    func selectImageId(imageId: Int) {
        deviceImageName = "image_" + String(imageId)
        setDeviceImage()
    }
}
