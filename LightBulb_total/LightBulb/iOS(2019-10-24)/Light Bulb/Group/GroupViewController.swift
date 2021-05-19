//
//  GroupViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/4.
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
                    }
                    
                    break loop
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
        
        if __DEVICES.count > 0 && __GROUPS.count > 0 && __GROUPS[id].deviceUUIDs.count > 0 {
            var peripheral: CBPeripheral!
            grouploop: for uuid in __GROUPS[id].deviceUUIDs {
                loop: for i in 0...(__DEVICE_ENTITIES.count - 1) {
                    if __DEVICE_ENTITIES[i].identifier.uuidString == uuid {
                        if __DEVICE_ENTITIES[i].state == .connected {
                            peripheral = __DEVICE_ENTITIES[i]
                            UtilFunctions.setDeviceStatus(uuid: uuid, status: switchOn, needSave: false)
                            break loop
                        }
                    }
                }
                
                var switchString = "open"
                if !switchOn {
                    switchString = "close"
                }
                UtilFunctions.sendBLEData(peripheral, switchString)
            }
            
            __GROUPS[id].groupStatus = status
            UtilFunctions.saveUserDeviceData()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __GROUPS.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // prevent click event
//        tableView.allowsSelection = false
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "GroupCell") as! GroupCell
        if __GROUPS.count > 0 {
            let group = __GROUPS[indexPath.row]
            cell.set(indexPath.row, groupModel: group)
            cell.groupCellDelegate = self
        }
        
        // modify selection style
        cell.selectionStyle = .none
        
        return cell
    }
    
    // click body - prevent now!
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print(indexPath.row)
        
        var peripherals = [CBPeripheral]()
        for periph in __DEVICE_ENTITIES {
            if __GROUPS[indexPath.row].deviceUUIDs.contains(periph.identifier.uuidString) {
                if periph.state == .connected {
                    peripherals.append(periph)
                }
            }
        }
        
        if peripherals.count == 0 {
            DispatchQueue.main.async {
                QMUITips.hideAllTips()
                QMUITips.showInfo("All of devices in this group was offlined", in: self.view, hideAfterDelay: 2)
            }
            
            return
        } else {
            let moveController = self.storyboard?.instantiateViewController(withIdentifier: "ColorPaletteViewController") as! ColorPaletteViewController
            moveController.peripheralArray = peripherals
            self.present(moveController, animated: true, completion: nil)
        }
    }
}
