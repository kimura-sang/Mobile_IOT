//
//  DetailScheduleViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/27.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField
import WWCalendarTimeSelector

class DetailScheduleViewController: UIViewController, WWCalendarTimeSelectorProtocol {
    
    @IBOutlet weak var btnDeviceIcon: UIButton!
    @IBOutlet weak var edtDeviceName: SkyFloatingLabelTextField!
    @IBOutlet weak var edtDeviceType: SkyFloatingLabelTextField!
    @IBOutlet weak var btnDelete: UIButton!

    @IBOutlet weak var btnStartTime: UIButton!
    @IBOutlet weak var btnEndTime: UIButton!
    
    @IBOutlet weak var btnSun: UIButton!
    @IBOutlet weak var btnSat: UIButton!
    @IBOutlet weak var btnFri: UIButton!
    @IBOutlet weak var btnThu: UIButton!
    @IBOutlet weak var btnWed: UIButton!
    @IBOutlet weak var btnTue: UIButton!
    @IBOutlet weak var btnMon: UIButton!
    
    @IBOutlet weak var lblServerTimeOffset: UILabel!
    
    var isSun: Bool = false
    var isMon: Bool = false
    var isTue: Bool = false
    var isWen: Bool = false
    var isThu: Bool = false
    var isFri: Bool = false
    var isSat: Bool = false
    
    var checkAll: Bool = true
    var noSelectedDay: Bool = false
    
    var isSelectStart: Bool = true
    
    var cellId: Int!
    var deviceMacAddress: String!
    var deviceImageName: String! = "AddDeviceDefault"
    
    var scheduleModel: ScheduleModel? = nil
    var editScheduleModel: [SelectDeviceModel] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        
        scheduleModel = __SCHEDULES[self.cellId]
        
        initUI()
        editScheduleModel = createArray()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        btnStartTime.setTitle(scheduleModel?.openTime, for: .normal)
        btnEndTime.setTitle(scheduleModel?.closeTime, for: .normal)
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    func initUI() {
        btnDeviceIcon.setImage(UIImage(named: "AddDeviceDefault"), for: .normal)
        btnDeviceIcon.layer.cornerRadius = 10
        btnDeviceIcon.layer.borderWidth = 2
        btnDeviceIcon.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnDelete.layer.cornerRadius = 2
        
        edtDeviceName.text = __SCHEDULES[cellId].deviceName
        edtDeviceType.text = __SCHEDULES[cellId].deviceType
        deviceMacAddress = __SCHEDULES[cellId].deviceUUID
        deviceImageName = __SCHEDULES[cellId].deviceImage
        
        btnStartTime.layer.cornerRadius = 10
        btnStartTime.layer.borderWidth = 2
        btnStartTime.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnStartTime.layer.cornerRadius = 5
        
        btnEndTime.layer.cornerRadius = 10
        btnEndTime.layer.borderWidth = 2
        btnEndTime.layer.borderColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7).cgColor
        btnEndTime.layer.cornerRadius = 5
        
        btnDeviceIcon.isUserInteractionEnabled = false
        edtDeviceName.isUserInteractionEnabled = false
        edtDeviceType.isUserInteractionEnabled = false
        
        if scheduleModel!.isSun {
            btnSun.isSelected = true
            isSun = true
        }
        if scheduleModel!.isSat {
            btnSat.isSelected = true
            isSat = true
        }
        if scheduleModel!.isFri {
            btnFri.isSelected = true
            isFri = true
        }
        if scheduleModel!.isThu {
            btnThu.isSelected = true
            isThu = true
        }
        if scheduleModel!.isWen {
            btnWed.isSelected = true
            isWen = true
        }
        if scheduleModel!.isTue {
            btnTue.isSelected = true
            isTue = true
        }
        if scheduleModel!.isMon {
            btnMon.isSelected = true
            isMon = true
        }
        
        setDeviceImage()
        
        if __DEBUG_SHOW_SERVER_TIME {
            lblServerTimeOffset.isHidden = false
            lblServerTimeOffset.text = scheduleModel?.offset
        } else {
            lblServerTimeOffset.isHidden = true
        }
    }
    
    func setDeviceImage() {
        btnDeviceIcon.setImage(UIImage(named: deviceImageName), for: .normal)
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
    
    
    @IBAction func clickSun(_ sender: Any) {
        isSun = !isSun
        if isSun {
            btnSun.isSelected = true
            isSelectedAll()
        } else {
            btnSun.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickMon(_ sender: Any) {
        isMon = !isMon
        if isMon {
            btnMon.isSelected = true
            isSelectedAll()
        } else {
            btnMon.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickTue(_ sender: Any) {
        isTue = !isTue
        if isTue {
            btnTue.isSelected = true
            isSelectedAll()
        } else {
            btnTue.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickWen(_ sender: Any) {
        isWen = !isWen
        if isWen {
            btnWed.isSelected = true
            isSelectedAll()
        } else {
            btnWed.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickThu(_ sender: Any) {
        isThu = !isThu
        if isThu {
            btnThu.isSelected = true
            isSelectedAll()
        } else {
            btnThu.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickFri(_ sender: Any) {
        isFri = !isFri
        if isFri {
            btnFri.isSelected = true
            isSelectedAll()
        } else {
            btnFri.isSelected = false
            checkAll = false
        }
    }
    
    @IBAction func clickSat(_ sender: Any) {
        isSat = !isSat
        if isSat {
            btnSat.isSelected = true
            isSelectedAll()
        } else {
            btnSat.isSelected = false
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
    
    @IBAction func clickSave(_ sender: Any) {
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
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: startDateTime, uniqueNo: self.scheduleModel!.uniqueNo, isOn: true) {
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
                                        
                                        if UtilFunctions.existCurrentTime(chineseTimeStr: endDateTime, uniqueNo: self.scheduleModel!.uniqueNo, isOn: false) {
                                            UtilFunctions.restoreTouchEvent()
                                            QMUITips.hideAllTips()
                                            
                                            QMUITips.showError("End time already exist", in: self.view, hideAfterDelay: 2)
                                        } else {
                                            let startSelectedDate: UInt8 = UtilFunctions.getDayDecimal(timeStr: startTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun)
                                            let endSelectedDate = UtilFunctions.getDayDecimal(timeStr: endTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun)
                                            
                                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_CHANGE, selectedDevice.deviceUUID, startDateTime, endDateTime, 1, startSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
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
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: startDateTime, uniqueNo: self.scheduleModel!.uniqueNo, isOn: true) {
                            UtilFunctions.restoreTouchEvent()
                            QMUITips.hideAllTips()
                            
                            QMUITips.showError("Start time already exist", in: self.view, hideAfterDelay: 2)
                        } else {
                            let startSelectedDate: UInt8 = UtilFunctions.getDayDecimal(timeStr: startTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun)
                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_CHANGE, selectedDevice.deviceUUID, startDateTime, "", 1, startSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
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
                        
                        if UtilFunctions.existCurrentTime(chineseTimeStr: endDateTime, uniqueNo: self.scheduleModel!.uniqueNo, isOn: false) {
                            UtilFunctions.restoreTouchEvent()
                            QMUITips.hideAllTips()
                            
                            QMUITips.showError("End time already exist", in: self.view, hideAfterDelay: 2)
                        } else {
                            let endSelectedDate = UInt8(UtilFunctions.getDayDecimal(timeStr: endTime, offsetTime: resultDicData["offset"] as! Int, self.isMon, self.isTue, self.isWen, self.isThu, self.isFri, self.isSat, self.isSun))
                            let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_CHANGE, selectedDevice.deviceUUID, "", endDateTime, 1, endSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
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
        
        __SCHEDULES[self.cellId].openTime = startTime
        __SCHEDULES[self.cellId].closeTime = endTime
        __SCHEDULES[self.cellId].startChineseTime = startChineseTime
        __SCHEDULES[self.cellId].endChineseTime = endChineseTime
        __SCHEDULES[self.cellId].startSelectedDate = startSelectedDate
        __SCHEDULES[self.cellId].endSelectedDate = endSelectedDate
        __SCHEDULES[self.cellId].isMon = self.isMon
        __SCHEDULES[self.cellId].isTue = self.isTue
        __SCHEDULES[self.cellId].isWen = self.isWen
        __SCHEDULES[self.cellId].isThu = self.isThu
        __SCHEDULES[self.cellId].isFri = self.isFri
        __SCHEDULES[self.cellId].isSat = self.isSat
        __SCHEDULES[self.cellId].isSun = self.isSun
        __SCHEDULES[self.cellId].offset = serverOffset
        
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
                var status = false
                if __DEVICES[i].deviceUUID == scheduleModel?.deviceUUID { status = true }
                temp.append(SelectDeviceModel(id: i, status: status, deviceModel: __DEVICES[i]))
            }
        }
        
        return temp
    }
    
    @IBAction func clickDelete(_ sender: Any) {
        UtilFunctions.showConfirmDialog("North Outlet", "You want to delete this schedule?", "Yes", "No", self, {
            
            DispatchQueue.main.async {
                UtilFunctions.preventTouchEvent()
                QMUITips.showLoading("Deleting...", in: self.view, hideAfterDelay: 4)
            }
            
            let selectedDevice = __SCHEDULES[self.cellId]
            let startTime = selectedDevice.openTime
            let endTime = selectedDevice.closeTime
            
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
                
                let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_DELETE, selectedDevice.deviceUUID, self.scheduleModel!.startChineseTime, self.scheduleModel!.endChineseTime, 1, self.scheduleModel!.startSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
                UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
            } else if startTime != "" {
                let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_DELETE, selectedDevice.deviceUUID, self.scheduleModel!.startChineseTime, self.scheduleModel!.endChineseTime, 1, self.scheduleModel!.startSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
                UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
            } else if endTime != "" {
                let data = Data(hexString: UtilFunctions.getScheduleBytes(__BYTE_SCHEDULE_DELETE, selectedDevice.deviceUUID, self.scheduleModel!.startChineseTime, self.scheduleModel!.endChineseTime, 1, self.scheduleModel!.endSelectedDate, UInt8(self.scheduleModel!.uniqueNo)))
                UtilFunctions.sendDataToDevice(selectedDeviceEntity, selectedDevice.deviceUUID, data!)
            }
            
            __SCHEDULES.remove(at: self.cellId)

            UtilFunctions.saveUserDeviceData()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 4) { // Change `5.0` to the desired number of seconds.// declare & post NOTIFICATION
                UtilFunctions.restoreTouchEvent()
                
                NotificationCenter.default.post(name: __NN_CHANGE_SCHEDULE_STATUS, object: nil)
                
                self.dismiss(animated: true, completion: nil)
            }
        }, {
            print("Canceled")
        })
    }

}
