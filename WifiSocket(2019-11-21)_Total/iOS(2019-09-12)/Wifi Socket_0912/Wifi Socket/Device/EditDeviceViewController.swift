//
//  EditDeviceViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/11.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField

class EditDeviceViewController: UIViewController {
    
    @IBOutlet weak var btnDeviceIcon: UIButton!
    @IBOutlet weak var edtDeviceName: SkyFloatingLabelTextField!
    @IBOutlet weak var edtDeviceType: SkyFloatingLabelTextField!
    @IBOutlet weak var btnDelete: UIButton!
    @IBOutlet weak var navItem: UINavigationItem!
    
    var deviceId: Int!
    var deviceMacAddress: String!
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
            edtDeviceType.text = __DEVICES[deviceId].deviceType
            deviceMacAddress = __DEVICES[deviceId].deviceUUID
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
        let deviceType = edtDeviceType.text
        
        if deviceImageName == "AddDeviceDefault" {
            QMUITips.showError("Please select device image", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let nameValid = UtilFunctions.nameValidation(deviceName!)
        if !nameValid {
            QMUITips.showError("Device name should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let typeValid = UtilFunctions.nameValidation(deviceType!)
        if !typeValid {
            QMUITips.showError("Device type should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }
        
        saveDeviceEntity(deviceName!, deviceType!)
        
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
        UtilFunctions.showConfirmDialog("North Outlet", "You want to delete this device?", "Yes", "No", self, {
            // print("Deleted!")
            UtilFunctions.deleteFromLocalDevice(self.deviceId)
            
            // declare & post NOTIFICATION
            NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)

            self.dismiss(animated: true, completion: nil)
        }, {
            // print("Canceled")
        })
    }
    
    
    func saveDeviceEntity(_ deviceName: String, _ deviceType: String) {
        if deviceId == -1 {
            let deviceModel = DeviceModel(uuid: deviceMacAddress, image: deviceImageName, name: deviceName, type: deviceType, status: false)
            
            __DEVICES.append(deviceModel)
            
            UtilFunctions.deleteFromDeviceEntity(deviceMacAddress)
        } else {
            __DEVICES[deviceId].deviceType = edtDeviceType.text!
            __DEVICES[deviceId].deviceName = edtDeviceName.text!
            __DEVICES[deviceId].deviceUUID = deviceMacAddress
            __DEVICES[deviceId].deviceImage = deviceImageName
            
            if __SCHEDULES.count > 0 {
                for i in 0...(__SCHEDULES.count - 1) {
                    if __SCHEDULES[i].deviceUUID == deviceMacAddress {
                        __SCHEDULES[i].deviceType = edtDeviceType.text!
                        __SCHEDULES[i].deviceName = edtDeviceName.text!
                        __SCHEDULES[i].deviceUUID = deviceMacAddress
                        __SCHEDULES[i].deviceImage = deviceImageName
                    }
                }
            }

            // TODO:
//            for device in __REAL_DEVICE_ENTITIES {
//                let deviceDictionary = device.getDictionaryFormat()
//                if (deviceDictionary?["macAddress"] as! String) == deviceMacAddress {
//                    UtilFunctions.sendRefreshData(device, deviceMacAddress)
//                }
//            }
        }
        
        UtilFunctions.saveUserDeviceData()
        
        // declare & post NOTIFICATION
        NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)
        
        if deviceId < 0 {
            var existRealEntity = false
            var deviceEntity: DeviceEntity?
            realEntityLoop: for realEntity in __REAL_DEVICE_ENTITIES {
                let realDicData = realEntity.getDictionaryFormat()
                if (realDicData!["macAddress"] as! String) == deviceMacAddress {
                    deviceEntity = realEntity
                    existRealEntity = true
                    break realEntityLoop
                }
            }
            if existRealEntity {
                let switchString = UtilFunctions.getBytes(2, deviceMacAddress)
                let data = Data(hexString: switchString)
                UtilFunctions.sendDataToDevice(deviceEntity!, deviceMacAddress, data!)
                UtilFunctions.sendRefreshData(deviceEntity!, deviceMacAddress)
                UtilFunctions.sendDataToDevice(deviceEntity!, deviceMacAddress, data!)
            }
        }
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
