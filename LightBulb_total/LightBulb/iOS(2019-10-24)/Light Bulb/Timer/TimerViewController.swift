//
//  TimerViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/4.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class TimerViewController: UIViewController {
    
    var leftMenuController: LeftMenuViewController!
    @IBOutlet weak var timerTable: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.navigationBar.barStyle = .black
        navigationController?.navigationBar.barTintColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7)
        
        leftMenuController = (self.sideMenuViewController?.leftMenuViewController as! LeftMenuViewController)
        
        createObservers()
    }
    
    // please remember this
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func createObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_CHANGE_TIMER_STATUS, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(showOffLineStatus(_:)), name: __NN_DEVICE_OFFLINE_STATUS, object: nil)
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.timerTable.reloadData()
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
    
    @IBAction func goToNext(_ sender: Any) {
        leftMenuController.goToNextView(3)
    }
    
    @IBAction func clickAddTimer(_ sender: Any) {
        self.performSegue(withIdentifier: "timer2add", sender: -1)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {        
    }
}

extension TimerViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __TIMERS.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        // prevent click event
        tableView.allowsSelection = false
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "TimerCell") as! TimerCell
        
        if __TIMERS.count > -1 {
            let timer = __TIMERS[indexPath.row]
            cell.set(id: indexPath.row, timerModel: timer)
            cell.selectionStyle = .none
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
}
