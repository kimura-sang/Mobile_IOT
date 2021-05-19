//
//  EditScheduleViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/13.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import WWCalendarTimeSelector

class EditScheduleViewController: UIViewController, WWCalendarTimeSelectorProtocol {
    
    @IBOutlet weak var btnStartTime: UIButton!
    @IBOutlet weak var btnEndTime: UIButton!
    
    @IBOutlet weak var btnSunday: UIButton!
    @IBOutlet weak var btnMonday: UIButton!
    @IBOutlet weak var btnTuesday: UIButton!
    @IBOutlet weak var btnWendesday: UIButton!
    @IBOutlet weak var btnThursday: UIButton!
    @IBOutlet weak var btnFriday: UIButton!
    @IBOutlet weak var btnSaturday: UIButton!
    
    var isSun: Bool = true
    var isMon: Bool = true
    var isTue: Bool = true
    var isWen: Bool = true
    var isThu: Bool = true
    var isFri: Bool = true
    var isSat: Bool = true
    
    var checkAll: Bool = true
    var noSelectedDay: Bool = false
    
    var editScheduleModel: [SelectDeviceModel] = []
    var isSelectStart: Bool = true
    var isSelectDevice: Bool = false
    var selectedId: Int = -1

    override func viewDidLoad() {
        super.viewDidLoad()

        initUI()
        editScheduleModel = createArray()
    }
    
    func initUI() {
        btnStartTime.layer.cornerRadius = 10
        btnStartTime.layer.borderWidth = 2
        btnStartTime.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnStartTime.layer.cornerRadius = 5
        
        btnEndTime.layer.cornerRadius = 10
        btnEndTime.layer.borderWidth = 2
        btnEndTime.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnEndTime.layer.cornerRadius = 5
        
        isSelectDevice = false
        
        if isSun { btnSunday.isSelected = true }
        if isMon { btnMonday.isSelected = true }
        if isTue { btnTuesday.isSelected = true }
        if isWen { btnWendesday.isSelected = true }
        if isThu { btnThursday.isSelected = true }
        if isFri { btnFriday.isSelected = true }
        if isSat { btnSaturday.isSelected = true }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func clickSun(_ sender: Any) {
        isSun = !isSun
        if isSun {
            btnSunday.isSelected = true
            isSelectedAll()
        } else {
            btnSunday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickMon(_ sender: Any) {
        isMon = !isMon
        if isMon {
            btnMonday.isSelected = true
            isSelectedAll()
        } else {
            btnMonday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickTue(_ sender: Any) {
        isTue = !isTue
        if isTue {
            btnTuesday.isSelected = true
            isSelectedAll()
        } else {
            btnTuesday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickWen(_ sender: Any) {
        isWen = !isWen
        if isWen {
            btnWendesday.isSelected = true
            isSelectedAll()
        } else {
            btnWendesday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickThu(_ sender: Any) {
        isThu = !isThu
        if isThu {
            btnThursday.isSelected = true
            isSelectedAll()
        } else {
            btnThursday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickFri(_ sender: Any) {
        isFri = !isFri
        if isFri {
            btnFriday.isSelected = true
            isSelectedAll()
        } else {
            btnFriday.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickSat(_ sender: Any) {
        isSat = !isSat
        if isSat {
            btnSaturday.isSelected = true
            isSelectedAll()
        } else {
            btnSaturday.isSelected = false
            checkAll = false
        }
    }
    
    func isSelectedAll() {
        if isSun && isMon && isTue && isWen && isThu && isFri && isSat {
            checkAll = true
        } else {
            checkAll = false
        }
    }
    
    func checkNoSelectDay() {
        if !isSun && !isMon && !isTue && !isWen && !isThu && !isFri && !isSat {
            noSelectedDay = true
        } else {
            noSelectedDay = false
        }
    }
    
    
    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clickNavSave(_ sender: Any) {
        let startTime = btnStartTime.titleLabel?.text ?? ""
        let endTime = btnEndTime.titleLabel?.text ?? ""
        
        if startTime == "" && endTime == "" {
            QMUITips.showError("Please select either on time or off time", in: self.view, hideAfterDelay: 2)
            return
        }
        
        checkNoSelectDay()
        if noSelectedDay {
            QMUITips.showError("Please select at least one day", in: self.view, hideAfterDelay: 2)
            return
        }
        
        if !isSelectDevice {
            QMUITips.showError("Please select at least one device", in: self.view, hideAfterDelay: 2)
            return
        }
        
        DispatchQueue.main.async {
            UtilFunctions.preventTouchEvent()
            QMUITips.showLoading("Saving...", in: self.view, hideAfterDelay: 4)
        }
        
        var selectedDevice: DeviceModel!
        for editModel in editScheduleModel {
            if editModel.selectStatus {
                selectedDevice = editModel.deviceModel
            }
        }
        
        var selectedDeviceEntity: DeviceEntity!
        for deviceEntity in __REAL_DEVICE_ENTITIES {
            let deviceDic = deviceEntity.getDictionaryFormat()
            if (deviceDic!["macAddress"] as! String) == selectedDevice.deviceUUID {
                selectedDeviceEntity = deviceEntity
            }
        }
        
        if startTime != "" && endTime != "" {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "HH:mm"

            let starTimestamp = UtilFunctions.getTimestampFromDateTime(UtilFunctions.getDateTimeFromTimeStampString(startTime))
            CommonHttpRequest.changeTimeInChineseTimeZone(starTimestamp, didFuncLoad: { result, error in
                if error != nil {
                    UtilFunctions.restoreTouchEvent()
                    QMUITips.hideAllTips()
                    
                    QMUITips.showError("Network error!", in: self.view, hideAfterDelay: 2)
                    print(error as Any)
                } else {
                    var resultDicData: Dictionary = [String: Any]()
                    resultDicData = result as! Dictionary
                    print(resultDicData.debugDescription)
                    if (resultDicData["status"] as! String) != "OK" {
                        UtilFunctions.restoreTouchEvent()
                        QMUITips.hideAllTips()
                        
                        QMUITips.showError("Failed to save", in: self.view, hideAfterDelay: 2)
                    } else {
                        let startTimestampFromServer = TimeInterval.init(resultDicData["toTimestamp"] as! Int)
                        let startDateTime = UtilFunctions.getDateTimeFromTimeStamp(startTimestampFromServer)
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: startDateTime, uniqueNo: 0, isOn: true) {
                            UtilFunctions.restoreTouchEvent()
                            QMUITips.hideAllTips()
                            
                            QMUITips.showError("Start time already exist", in: self.view, hideAfterDelay: 2)
                        } else {
                            let endTimestamp = UtilFunctions.getTimestampFromDateTime(UtilFunctions.getDateTimeFromTimeStampString(endTime))
                            CommonHttpRequest.changeTimeInChineseTimeZone(endTimestamp, didFuncLoad: { result, error in
                                if error != nil {
                                    UtilFunctions.restoreTouchEvent()
                                    QMUITips.hideAllTips()
                                    
                                    QMUITips.showError("Network error!", in: self.view, hideAfterDelay: 2)
                                    print(error as Any)
                                } else {
                                    var resultDicData: Dictionary = [String: Any]()
                                    resultDicData = result as! Dictionary
                                    print(resultDicData.debugDescription)
                                    if (resultDicData["status"] as! String) != "OK" {
                                        UtilFunctions.restoreTouchEvent()
                                        QMUITips.hideAllTips()
                                        
                                        QMUITips.showError("Failed to save", in: self.view, hideAfterDelay: 2)
                                    } else {
                                        let endTimestampFromServer = TimeInterval.init(resultDicData["toTimestamp"] as! Int)
                                        let endDateTime = UtilFunctions.getDateTimeFromTimeStamp(endTimestampFromServer)
                                        
                                        if UtilFunctions.existCurrentTime(chineseTimeStr: endDateTime, uniqueNo: 0, isOn: false) {
                                            UtilFunctions.restoreTouchEvent()
                                            QMUITips.hideAllTips()
                                            
                                            QMUITips.showError("End time already exist", in: self.view, hideAfterDelay: 2)
                                        } else {
                                            let startSelectedDate: UInt8 = UtilFunctions.getDayDecimal(timeStr: startTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun)
                                            let endSelectedDate = UInt8(UtilFunctions.getDayDecimal(timeStr: endTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun))
                                            
                                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_ADD, selectedDevice.deviceUUID, startDateTime, endDateTime, 1, startSelectedDate, 0))
                                            UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
                                            
                                            self.saveDateTime(selectedDevice, startTime, endTime, startDateTime, endDateTime, startSelectedDate, endSelectedDate, Int(startTimestampFromServer), Int(starTimestamp)!, Int(endTimestampFromServer), Int(endTimestamp)!)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            })
        } else if startTime != "" {
            let starTimestamp = UtilFunctions.getTimestampFromDateTime(UtilFunctions.getDateTimeFromTimeStampString(startTime))
            CommonHttpRequest.changeTimeInChineseTimeZone(starTimestamp, didFuncLoad: { result, error in
                if error != nil {
                    UtilFunctions.restoreTouchEvent()
                    QMUITips.hideAllTips()
                    
                    QMUITips.showError("Network error!", in: self.view, hideAfterDelay: 2)
                    print(error as Any)
                } else {
                    var resultDicData: Dictionary = [String: Any]()
                    resultDicData = result as! Dictionary
                    print(resultDicData.debugDescription)
                    if (resultDicData["status"] as! String) != "OK" {
                        UtilFunctions.restoreTouchEvent()
                        QMUITips.hideAllTips()
                        
                        QMUITips.showError("Failed to save", in: self.view, hideAfterDelay: 2)
                    } else {
                        let startTimestampFromServer = TimeInterval.init(resultDicData["toTimestamp"] as! Int)
                        let startDateTime = UtilFunctions.getDateTimeFromTimeStamp(startTimestampFromServer)
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: startDateTime, uniqueNo: 0, isOn: true) {
                            UtilFunctions.restoreTouchEvent()
                            QMUITips.hideAllTips()
                            
                            QMUITips.showError("Start time already exist", in: self.view, hideAfterDelay: 2)
                        } else {
                            let startSelectedDate: UInt8 = UtilFunctions.getDayDecimal(timeStr: startTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun)
                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_ADD, selectedDevice.deviceUUID, startDateTime, "", 1, startSelectedDate, 0))
                            UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
                            
                            self.saveDateTime(selectedDevice, startTime, endTime, startDateTime, "", startSelectedDate, 0, Int(startTimestampFromServer), Int(starTimestamp)!, 0, 0)
                        }
                    }
                }
            })
        } else if endTime != "" {
            let endTimestamp = UtilFunctions.getTimestampFromDateTime(UtilFunctions.getDateTimeFromTimeStampString(endTime))
            CommonHttpRequest.changeTimeInChineseTimeZone(endTimestamp, didFuncLoad: { result, error in
                if error != nil {
                    UtilFunctions.restoreTouchEvent()
                    QMUITips.hideAllTips()
                    
                    QMUITips.showError("Network error!", in: self.view, hideAfterDelay: 2)
                    print(error as Any)
                } else {
                    var resultDicData: Dictionary = [String: Any]()
                    resultDicData = result as! Dictionary
                    print(resultDicData.debugDescription)
                    if (resultDicData["status"] as! String) != "OK" {
                        UtilFunctions.restoreTouchEvent()
                        QMUITips.hideAllTips()
                        
                        QMUITips.showError("Failed to save", in: self.view, hideAfterDelay: 2)
                    } else {
                        let endTimestampFromServer = TimeInterval.init(resultDicData["toTimestamp"] as! Int)
                        let endDateTime = UtilFunctions.getDateTimeFromTimeStamp(endTimestampFromServer)
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: endDateTime, uniqueNo: 0, isOn: false) {
                            UtilFunctions.restoreTouchEvent()
                            QMUITips.hideAllTips()
                            
                            QMUITips.showError("End time already exist", in: self.view, hideAfterDelay: 2)
                        } else {
                            let endSelectedDate = UInt8(UtilFunctions.getDayDecimal(timeStr: endTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun))
                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_ADD, selectedDevice.deviceUUID, "", endDateTime, 1, endSelectedDate, 0))
                            UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
                            
                            self.saveDateTime(selectedDevice, startTime, endTime, "", endDateTime, 0, endSelectedDate, 0, 0, Int(endTimestampFromServer), Int(endTimestamp)!)
                        }
                    }
                }
            })
        }
    }
    
    func saveDateTime(_ selectedDevice: DeviceModel, _ startTime: String, _ endTime: String, _ startChineseTime: String, _ endChineseTime: String, _ startSelectedDate: UInt8, _ endSelectedDate: UInt8, _ startTimestampFromServer: Int, _ starTimestamp: Int, _ endTimestampFromServer: Int, _ endTimestamp:Int) {
        var serverOffset: String = "Server 00:00"
        if startTime != "" {
            serverOffset = UtilFunctions.getOffsetTime(startTimestampFromServer, starTimestamp)
        } else if endTime != "" {
            serverOffset = UtilFunctions.getOffsetTime(endTimestampFromServer, endTimestamp)
        }
        
        __SCHEDULES.append(ScheduleModel.init(uniqueNo: UtilFunctions.getLastestScheduleUniqueNumber(), uuid: selectedDevice.deviceUUID, image: selectedDevice.deviceImage, name: selectedDevice.deviceName, type: selectedDevice.deviceType, openTime: startTime, closeTime: endTime, startChnTime: startChineseTime, endChnTime: endChineseTime, startSDate: startSelectedDate, endSDate: endSelectedDate, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun, serverOffset))
        
        UtilFunctions.saveUserDeviceData()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 4) { // Change `5.0` to the desired number of seconds.// declare & post NOTIFICATION
            UtilFunctions.restoreTouchEvent()
            
            NotificationCenter.default.post(name: __NN_CHANGE_SCHEDULE_STATUS, object: nil)
            
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
    
    
    func showCalender(_ isStartTime: Bool) {
        // 1. You must instantiate with the class function instantiate()
        let selector = WWCalendarTimeSelector.instantiate()
        
        // 2. You can then set delegate, and any customization options
        selector.delegate = self
        selector.optionStyles.showDateMonth(false)
        selector.optionStyles.showMonth(false)
        selector.optionStyles.showYear(false)
        
        if isStartTime {
            isSelectStart = true
            selector.optionTopPanelTitle = "Select Start Time"
        } else {
            isSelectStart = false
            selector.optionTopPanelTitle = "Select End Time"
        }
        
        // 3. Then you simply present it from your view controller when necessary!
        self.present(selector, animated: true, completion: nil)
    }
    
    func WWCalendarTimeSelectorDone(_ selector: WWCalendarTimeSelector, date: Date) {
        let format = DateFormatter()
        format.timeZone = .current
//        format.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ"
        format.dateFormat = "HH:mm"
        let dateString = format.string(from: date)
        
        if isSelectStart {
            btnStartTime.setTitle(dateString, for: .normal)
        } else {
            btnEndTime.setTitle(dateString, for: .normal)
        }
    }
    
    @IBAction func clickStartTime(_ sender: Any) {
        showCalender(true)
    }
    
    @IBAction func clickEndTime(_ sender: Any) {
        showCalender(false)
    }

}

extension EditScheduleViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return editScheduleModel.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let editModel = editScheduleModel[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "EditScheduleCell", for: indexPath) as! EditScheduleCell
        
        // modify selection style
        cell.selectionStyle = .none
        
        cell.set(editModel: editModel)
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if let tableCell: EditScheduleCell = cell as? EditScheduleCell {
            tableCell.imgChecked.image = nil
        }
        
        let editModel = editScheduleModel[indexPath.row]
        if editModel.selectStatus {
            (cell as! EditScheduleCell).imgChecked.image = UIImage(named: "ItemChecked")
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        isSelectDevice = false
        let tableCell = tableView.cellForRow(at: indexPath)
        if let cell: EditScheduleCell = tableCell as? EditScheduleCell {
            if selectedId != indexPath.row {
                initEditModelStatus()
            
                cell.imgChecked.image = UIImage(named: "ItemChecked")
            
                let editModel = editScheduleModel[indexPath.row]
                editModel.selectStatus = true
            
                isSelectDevice = true
            } else {
                if !editScheduleModel[indexPath.row].selectStatus {
                    cell.imgChecked.image = UIImage(named: "ItemChecked")
                    
                    let editModel = editScheduleModel[indexPath.row]
                    editModel.selectStatus = true
                    
                    isSelectDevice = true
                } else {
                    initEditModelStatus()
                    
                    cell.imgChecked.image = nil
                    
                    let editModel = editScheduleModel[indexPath.row]
                    editModel.selectStatus = false

                }
            }
        }
        
        selectedId = indexPath.row
    }
    
    func tableView(_ tableView: UITableView, didDeselectRowAt indexPath: IndexPath) {
        let tableCell = tableView.cellForRow(at: indexPath)
        if let cell: EditScheduleCell = tableCell as? EditScheduleCell {
            cell.imgChecked.image = nil
        }
    }
    
    func initEditModelStatus() {
        for i in 0...(editScheduleModel.count - 1) {
            editScheduleModel[i].selectStatus = false
        }
    }
}

