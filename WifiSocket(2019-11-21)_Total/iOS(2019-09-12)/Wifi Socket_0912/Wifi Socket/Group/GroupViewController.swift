//
//  GroupViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/4.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class GroupViewController: UIViewController {
    
    @IBOutlet weak var groupTable: UITableView!
    var leftMenuController: LeftMenuViewController!
    var groupModel: [GroupModel] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.navigationBar.barStyle = .black
        navigationController?.navigationBar.barTintColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7)
        
        leftMenuController = (self.sideMenuViewController?.leftMenuViewController as! LeftMenuViewController)

        createObservers()
        
        if __FIRST_LOAD == 0 {
            QMUITips.showLoading("Synchronizing...", in: self.view, hideAfterDelay: 2)
            __FIRST_LOAD = 1
            UtilFunctions.restartRealDeviceEntities()
        }
    }
    
    @IBAction func goToNext(_ sender: Any) {
        leftMenuController.goToNextView(2)
    }
    
    @IBAction func clickAddGroup(_ sender: Any) {
        self.performSegue(withIdentifier: "group2edit", sender: -1)
    }
    
    // please remember this
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func createObservers() {
//        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_DEVICE_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_GROUP_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(showOffLineStatus(_:)), name: __NN_DEVICE_OFFLINE_STATUS, object: nil)
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.groupTable.reloadData()
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
    
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        
        let editGroupViewController = segue.destination as! EditGroupViewController
        editGroupViewController.groupId = (sender as! Int)
    }
    
}

extension GroupViewController: GroupDelegate, UITableViewDelegate, UITableViewDataSource {
    
    func editGroup(id: Int, name: String) {
        // print(id)
        self.performSegue(withIdentifier: "group2edit", sender: id)
    }
    
    func changeStatus(id: Int, status: Bool) {
        let switchOn = status;
        
        for deviceEntity in __REAL_DEVICE_ENTITIES {
            let dicData = deviceEntity.getDictionaryFormat()
            let macAddress = dicData!["macAddress"] as! String
            
            if __GROUPS[id].deviceMacAddresses.contains(macAddress) {
                var switchString = UtilFunctions.getBytes(1, macAddress)
                if !switchOn {
                    switchString = UtilFunctions.getBytes(2, macAddress)
                }
                
                DispatchQueue.main.async {
//                    UtilFunctions.preventTouchEvent()
                    QMUITips.showLoading("Loading...", in: self.view, hideAfterDelay: 5)
                }
                
                let data = Data(hexString: switchString)
                UtilFunctions.sendDataToDevice(deviceEntity, macAddress, data!)
                UtilFunctions.sendRefreshData(deviceEntity, macAddress)
                UtilFunctions.sendDataToDevice(deviceEntity, macAddress, data!)
            }
        }
        
        __GROUPS[id].groupStatus = status
        
        UtilFunctions.saveUserDeviceData()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5) { // Change `5.0` to the desired number of seconds.
//            UtilFunctions.restoreTouchEvent()
            self.groupTable.reloadData()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __GROUPS.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // prevent click event
        tableView.allowsSelection = false
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "GroupCell") as! GroupCell
        if __GROUPS.count > 0 {
            let group = __GROUPS[indexPath.row]
            cell.set(indexPath.row, groupModel: group)
            cell.groupCellDelegate = self
        }
        
        return cell
    }
    
    // click body - prevent now!
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(indexPath.row)
    }
}
