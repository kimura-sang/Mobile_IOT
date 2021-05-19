//
//  DeviceViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/4.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class DeviceViewController: UIViewController{
    
    @IBOutlet weak var deviceTable: UITableView!
    
    var leftMenuController: LeftMenuViewController!
    // var deviceModel: [DeviceModel] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.navigationBar.barStyle = .black
        navigationController?.navigationBar.barTintColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7)
        leftMenuController = (self.sideMenuViewController?.leftMenuViewController as! LeftMenuViewController)
        
        createObservers()
        
        // __DEVICES = createArray()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        deviceTable.reloadData()
        
        if __FIRST_LOAD == 0 {
            QMUITips.showLoading("Synchronizing...", in: self.view, hideAfterDelay: 6)
            __FIRST_LOAD = 1
            UtilFunctions.restartRealDeviceEntities()
        }
    }
    
    // please remember this
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func createObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_DEVICE_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(showOffLineStatus(_:)), name: __NN_DEVICE_OFFLINE_STATUS, object: nil)
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.deviceTable.reloadData()
//            UtilFunctions.restoreTouchEvent()
        }
    }
    
    @objc private func showOffLineStatus(_ notification: Notification) {
        let macAddress = notification.userInfo!["macAddress"] as! String
        for device in __DEVICES {
            if device.deviceUUID == macAddress {
                let messageContent = String(format: "Device \"%@\" is offline", device.deviceName)
                DispatchQueue.main.async {
                    QMUITips.hideAllTips()
                    QMUITips.showInfo(messageContent, in: self.view, hideAfterDelay: 2)
                }
            }
        }
    }
    
    @IBAction func goToNext(_ sender: Any) {
        leftMenuController.goToNextView(1)
    }
    
    @IBAction func clickAddDevice(_ sender: Any) {
         self.performSegue(withIdentifier: "device2scanned", sender: -1)
    }
    
    func sendSwitchSignal(_ macAddress: String) {
        if __DEVICES.count > 0 {
            var switchOn = false;
            checkStatusLoop: for i in 0...(__DEVICES.count - 1) {
                if __DEVICES[i].deviceUUID == macAddress {
                    switchOn = !__DEVICES[i].openStatus
                    break checkStatusLoop
                }
            }
            
            
            var switchString = UtilFunctions.getBytes(1, macAddress)
            if !switchOn {
                switchString = UtilFunctions.getBytes(2, macAddress)
            }
            
            sendPacketLoop: for deviceEntity in __REAL_DEVICE_ENTITIES {
                let deviceDicData = deviceEntity.getDictionaryFormat()
                if deviceDicData!["macAddress"] as! String == macAddress {
//                    UtilFunctions.preventTouchEvent()
                    DispatchQueue.main.async {
                        QMUITips.showLoading("Loading...", in: self.view, hideAfterDelay: 5)
                    }
                    
                    let data = Data(hexString: switchString)
                    UtilFunctions.sendDataToDevice(deviceEntity, macAddress, data!)
                    UtilFunctions.sendRefreshData(deviceEntity, macAddress)
                    UtilFunctions.sendDataToDevice(deviceEntity, macAddress, data!)
                    break sendPacketLoop
                }
            }
        }
    }
    
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        
        if (sender as! Int) > -1 {
            let editDeviceViewController = segue.destination as! EditDeviceViewController
            editDeviceViewController.deviceId = (sender as! Int)
            editDeviceViewController.deviceMacAddress = ""
        } else {
            let scannedDevicesViewController = segue.destination as! ScannedDevicesViewController
            scannedDevicesViewController.fromWifiPage = 0
        }
    }
}

extension DeviceViewController: DeviceDelegate, UITableViewDelegate, UITableViewDataSource {
    
    func editDevice(id: Int, name: String) {
        // print(id)
        self.performSegue(withIdentifier: "device2edit", sender: id)
    }
    
    func changeStatus(id: Int) {
        sendSwitchSignal(__DEVICES[id].deviceUUID)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __DEVICES.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // prevent click event
        tableView.allowsSelection = false
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceCell") as! DeviceCell
        if __DEVICES.count > 0 {
            let device = __DEVICES[indexPath.row]
            cell.set(id: indexPath.row, deviceModel: device)
            cell.deviceCellDelegate = self
        }
        
        return cell
    }
    
    // click body - prevent now!
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(indexPath.row)
    }
}

extension Data {
    init?(hexString: String) {
        let len = hexString.count / 2
        var data = Data(capacity: len)
        for i in 0..<len {
            let j = hexString.index(hexString.startIndex, offsetBy: i*2)
            let k = hexString.index(j, offsetBy: 2)
            let bytes = hexString[j..<k]
            if var num = UInt8(bytes, radix: 16) {
                data.append(&num, count: 1)
            } else {
                return nil
            }
        }
        self = data
    }
}

