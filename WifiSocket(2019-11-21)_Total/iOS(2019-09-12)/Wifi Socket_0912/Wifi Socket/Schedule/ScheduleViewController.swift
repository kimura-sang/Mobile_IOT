//
//  ScheduleViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/4.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScheduleViewController: UIViewController {
    
    var leftMenuController: LeftMenuViewController!
    @IBOutlet weak var scheduleTable: UITableView!
    
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
    
    // please remember this
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func createObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_SCHEDULE_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(showOffLineStatus(_:)), name: __NN_DEVICE_OFFLINE_STATUS, object: nil)
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.scheduleTable.reloadData()
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
        leftMenuController.goToNextView(3)
    }
    
    @IBAction func clickAddSchedule(_ sender: Any) {
        self.performSegue(withIdentifier: "schedule2add", sender: -1)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (sender as! Int) > -1 {
            let detailScheduleViewController = segue.destination as! DetailScheduleViewController
            detailScheduleViewController.cellId = (sender as! Int)
        } else {
            
        }
        
    }
}

extension ScheduleViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __SCHEDULES.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ScheduleCell") as! ScheduleCell
        
        if __SCHEDULES.count > -1 {
            let schedule = __SCHEDULES[indexPath.row]
            cell.set(id: indexPath.row, scheduleModel: schedule)
            cell.selectionStyle = .none
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let cell = tableView.cellForRow(at: indexPath) as! ScheduleCell
        self.performSegue(withIdentifier: "schedule2detail", sender: cell.cellId)
    }
}
