//
//  AddTimerViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/13.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import PGDatePicker

class AddTimerViewController: UIViewController {
    
    @IBOutlet weak var btnTime: UIButton!
    @IBOutlet weak var segControl: UISegmentedControl!
    
    var addTimerModel: [SelectDeviceModel] = []
    var isSelectDevice: Bool = false
    var selectedId: Int = -1
    var isOn: Bool = true
    
    var minute: Int = 0
    var second: Int = 0

    override func viewDidLoad() {
        super.viewDidLoad()

        initUI()
        addTimerModel = createArray()
    }
    
    func initUI() {
        btnTime.layer.cornerRadius = 10
        btnTime.layer.borderWidth = 2
        btnTime.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnTime.layer.cornerRadius = 5
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func clickBtnTime(_ sender: Any) {
        let datePickerManager = PGDatePickManager()
        datePickerManager.isShadeBackground = true
        datePickerManager.confirmButtonText = "OK"
        
        let datePicker = datePickerManager.datePicker!
        datePicker.datePickerMode = .minuteAndSecond
        
        datePicker.delegate = self
        
        self.present(datePickerManager, animated: false, completion: nil)
    }
    
    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickSegment(_ sender: Any) {
        if segControl.selectedSegmentIndex == 0 {
            isOn = true
        } else {
            isOn = false
        }
    }
    
    @IBAction func clickNavSave(_ sender: Any) {
        let time = btnTime.titleLabel?.text ?? ""
        
        if time == "" {
            QMUITips.showError("Please select time", in: self.view, hideAfterDelay: 2)
            return
        }
        
        if !isSelectDevice {
            QMUITips.showError("Please select at least one device", in: self.view, hideAfterDelay: 2)
            return
        }
        
        DispatchQueue.main.async {
            UtilFunctions.preventTouchEvent()
//            QMUITips.showLoading("Saving...", in: self.view, hideAfterDelay: 4)
        }
        
        var selectedDevice: DeviceModel!
        for timerModel in addTimerModel {
            if timerModel.selectStatus {
                selectedDevice = timerModel.deviceModel
            }
        }
        
        loop: for periph in __DEVICE_ENTITIES {
            if periph.identifier.uuidString == selectedDevice.deviceUUID {
                if periph.state == .connected {
                    if isOn {
                        BLEController.writeBleData(BLEController.delayTimeOpenBle(String(format: "%d", minute * 60 + second)), peripheral: periph)
                    } else {
                        BLEController.writeBleData(BLEController.delayTimeTurnOffBle(String(format: "%d", minute * 60 + second)), peripheral: periph)
                    }
                    saveDateTime(selectedDevice)
                    break loop
                }
            }
        }
    }
    
    func saveDateTime(_ selectedDevice: DeviceModel) {
        let timestamp = Int64(Date().timeIntervalSince1970)
        __TIMERS.append(TimerModel.init(uuid: selectedDevice.deviceUUID, image: selectedDevice.deviceImage, name: selectedDevice.deviceName, isOp: isOn, startTimeInt: timestamp, min: minute, sec: second))
        
        UtilFunctions.saveUserDeviceData()
        
        DispatchQueue.main.async {
            UtilFunctions.restoreTouchEvent()
            
            NotificationCenter.default.post(name: __NN_CHANGE_TIMER_STATUS, object: nil)
            
            self.dismiss(animated: true, completion: nil)
        }
    }
    
    func createArray() -> [SelectDeviceModel]{
        var temp: [SelectDeviceModel] = []
        
        if (__DEVICES.count > 0) {
            for i in 0...(__DEVICES.count - 1) {
                temp.append(SelectDeviceModel(id: i, status: false, deviceModel: __DEVICES[i]))
            }
        }
        
        return temp
    }
}

extension AddTimerViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return addTimerModel.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let timerModel = addTimerModel[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "AddTimerCell", for: indexPath) as! AddTimerCell
        
        // modify selection style
        cell.selectionStyle = .none
        
        cell.set(editModel: timerModel)
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if let tableCell: AddTimerCell = cell as? AddTimerCell {
            tableCell.imgChecked.image = nil
        }
        
        let editModel = addTimerModel[indexPath.row]
        if editModel.selectStatus {
            (cell as! AddTimerCell).imgChecked.image = UIImage(named: "ItemChecked")
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        isSelectDevice = false
        let tableCell = tableView.cellForRow(at: indexPath)
        if let cell: AddTimerCell = tableCell as? AddTimerCell {
            if selectedId != indexPath.row {
                initEditModelStatus()
            
                cell.imgChecked.image = UIImage(named: "ItemChecked")
            
                let editModel = addTimerModel[indexPath.row]
                editModel.selectStatus = true
            
                isSelectDevice = true
            } else {
                if !addTimerModel[indexPath.row].selectStatus {
                    cell.imgChecked.image = UIImage(named: "ItemChecked")
                    
                    let editModel = addTimerModel[indexPath.row]
                    editModel.selectStatus = true
                    
                    isSelectDevice = true
                } else {
                    initEditModelStatus()
                    
                    cell.imgChecked.image = nil
                    
                    let editModel = addTimerModel[indexPath.row]
                    editModel.selectStatus = false

                }
            }
        }
        
        selectedId = indexPath.row
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        let tableCell = tableView.cellForRow(at: indexPath)
        if let cell: AddTimerCell = tableCell as? AddTimerCell {
            cell.imgChecked.image = nil
        }
    }
    
    func initEditModelStatus() {
        for i in 0...(addTimerModel.count - 1) {
            addTimerModel[i].selectStatus = false
        }
    }
}

extension AddTimerViewController: PGDatePickerDelegate {
    
    func datePicker(_ datePicker: PGDatePicker!, didSelectDate dateComponents: DateComponents!) {
        print("dateComponents = ", dateComponents)
        
        minute = dateComponents.minute!
        second = dateComponents.second!
        
        btnTime.setTitle(String.init(format: "%@ min   %@ sec", UtilFunctions.getFullStringOfInt(minute), UtilFunctions.getFullStringOfInt(second)), for: .normal)
    }
    
}
