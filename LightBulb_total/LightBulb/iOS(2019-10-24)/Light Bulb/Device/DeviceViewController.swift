//
//  DeviceViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/4.
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
    }
    
    override func viewDidAppear(_ animated: Bool) {
        deviceTable.reloadData()
        
        if __FIRST_LOAD == 0 {
            if __DEVICES.count > 0 {
                QMUITips.showLoading("Synchronizing...", in: self.view)
                DispatchQueue.global().asyncAfter(deadline: .now() + 0.5) {
                    UtilFunctions.searchBLEDevices()
                }
            }
            
            __FIRST_LOAD = 1
            
            appDelegate.timerRunning()
        }
    }
    
    // please remember this
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func createObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(finishSyncDialog(_:)), name: __NN_BLE_DEVICE_SCANNED, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_DEVICE_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(showOffLineStatus(_:)), name: __NN_DEVICE_OFFLINE_STATUS, object: nil)
    }
    
    @objc private func finishSyncDialog(_ notification: Notification) {
        print("--- finishSyncDialog ---")
        DispatchQueue.main.async {
            QMUITips.hideAllTips()
        }
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.deviceTable.reloadData()
        }
    }
    
    @objc private func showOffLineStatus(_ notification: Notification) {
        if __DEVICES.count > 0 {
            let deviceUUID = notification.userInfo!["deviceUUID"] as! String
            loop: for i in 0...(__DEVICES.count - 1) {
                if __DEVICES[i].deviceUUID == deviceUUID {
                    __DEVICES[i].onlineStatus = false
                    UtilFunctions.syncUserData()
                    
                    let messageContent = String(format: "Device \"%@\" is offline", __DEVICES[i].deviceName)
                    DispatchQueue.main.async {
                        QMUITips.hideAllTips()
                        QMUITips.showInfo(messageContent, in: self.view, hideAfterDelay: 2)
                        
                        self.deviceTable.reloadData()
                    }
                    
                    break loop
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
    
    func sendSwitchSignal(_ id: Int) {
        if __DEVICES.count > 0 {
            var switchOn = false;
            var peripheral: CBPeripheral!
            loop: for periph in __DEVICE_ENTITIES {
                if periph.identifier.uuidString == __DEVICES[id].deviceUUID {
                    if periph.state == .connected {
                        peripheral = periph
                        switchOn = !__DEVICES[id].openStatus
                        break loop
                    }
                }
            }
            
            var switchString = "open"
            if !switchOn {
                switchString = "close"
            }
            UtilFunctions.sendBLEData(peripheral, switchString)
            
            __DEVICES[id].openStatus = !__DEVICES[id].openStatus
            UtilFunctions.syncUserData()
        }
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (sender as! Int) > -1 {
            let editDeviceViewController = segue.destination as! EditDeviceViewController
            editDeviceViewController.deviceId = (sender as! Int)
            editDeviceViewController.deviceUUID = ""
        }
    }
}

extension DeviceViewController: DeviceDelegate, UITableViewDelegate, UITableViewDataSource {
    
    func editDevice(id: Int, name: String) {
        self.performSegue(withIdentifier: "device2edit", sender: id)
    }
    
    func changeStatus(id: Int) {
        if !__DEVICES[id].onlineStatus {
            let messageContent = String(format: "Device \"%@\" is offline", __DEVICES[id].deviceName)
            DispatchQueue.main.async {
                QMUITips.hideAllTips()
                QMUITips.showInfo(messageContent, in: self.view, hideAfterDelay: 2)
            }

            return
        }
        
        sendSwitchSignal(id)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __DEVICES.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // prevent click event
//        tableView.allowsSelection = false
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceCell") as! DeviceCell
        if __DEVICES.count > 0 {
            let device = __DEVICES[indexPath.row]
            cell.set(id: indexPath.row, deviceModel: device)
            cell.deviceCellDelegate = self
        }
        
        // modify selection style
        cell.selectionStyle = .none
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(indexPath.row)
        
        if !__DEVICES[indexPath.row].onlineStatus {
            let messageContent = String(format: "Device \"%@\" is offline", __DEVICES[indexPath.row].deviceName)
            DispatchQueue.main.async {
                QMUITips.hideAllTips()
                QMUITips.showInfo(messageContent, in: self.view, hideAfterDelay: 2)
            }
            
            return
        }
        
        var peripheral: CBPeripheral!
        loop: for periph in __DEVICE_ENTITIES {
            if periph.identifier.uuidString == __DEVICES[indexPath.row].deviceUUID {
                if periph.state == .connected {
                    peripheral = periph
                    break loop
                }
            }
        }
        
        if peripheral == nil {
            let messageContent = String(format: "Device \"%@\" is offline", __DEVICES[indexPath.row].deviceName)
            DispatchQueue.main.async {
                QMUITips.hideAllTips()
                QMUITips.showInfo(messageContent, in: self.view, hideAfterDelay: 2)
            }
            
            return
        } else {
            let moveController = self.storyboard?.instantiateViewController(withIdentifier: "ColorPaletteViewController") as! ColorPaletteViewController
            moveController.peripheralArray = [peripheral]
            self.present(moveController, animated: true, completion: nil)
        }
    }
}
