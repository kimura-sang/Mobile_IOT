//
//  ScannedDevicesViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/14.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScannedDevicesViewController: UIViewController {

    var fromWifiPage = 0
    
    @IBOutlet weak var resultTableView: UITableView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if fromWifiPage == 1 {
            navigationBar.topItem?.title = "New Scanned Devices"
        } else {
            navigationBar.topItem?.title = "Scanned Devices"
        }
        
        UtilFunctions.syncUserData()
        
        UtilFunctions.preventTouchEvent()
        DispatchQueue.main.async {
            QMUITips.showLoading("Loading...", in: self.view, hideAfterDelay: 2)
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.resultTableView.reloadData()
            UtilFunctions.restoreTouchEvent()
            
            if __CACHE_DEVICE_ENTITIES.count <= 0 {
                UtilFunctions.showAlertDialog("No Devices Found", "Wifi can use two diiferent frquencies, like radio sations. 2.4ghz or 5ghz. Our device uses 2.4ghxz which is slower, but often has a stronger signal.", "Okay", self, {
                    self.dismiss(animated: true, completion: nil)
                    __LEFT_MENU_CONTROLLER.goToNextView(3)
                })
            }
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        resultTableView.reloadData()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let editDeviceViewController = segue.destination as! EditDeviceViewController
        editDeviceViewController.deviceId = -1
        editDeviceViewController.deviceMacAddress = (sender as! String)
    }
}

extension ScannedDevicesViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __DEVICE_ENTITIES.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let device = __DEVICE_ENTITIES[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "ScannedDeviceCell") as! ScannedDeviceCell
        cell.set(deviceEntity: device)
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let macAddress = __DEVICE_ENTITIES[indexPath.row]["macAddress"]
        self.performSegue(withIdentifier: "scanned2editDevice", sender: macAddress)
    }
}
