//
//  ScannedDevicesViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/14.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ScannedDevicesViewController: UIViewController {
    
    @IBOutlet weak var resultTableView: UITableView!
    @IBOutlet weak var navigationBar: UINavigationBar!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationBar.topItem?.title = "Scanned Devices"
        
//        UtilFunctions.syncUserData()
        
        createObservers()
        
        UtilFunctions.preventTouchEvent()
        DispatchQueue.main.async {
            UtilFunctions.searchBLEDevices()
            QMUITips.showLoading("Scanning...", in: self.view, hideAfterDelay: 3)
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
            self.resultTableView.reloadData()
            UtilFunctions.restoreTouchEvent()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        resultTableView.reloadData()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    func createObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(reloadTableList(_:)), name: __NN_BLE_DEVICE_SCANNED, object: nil)
    }
    
    @objc private func reloadTableList(_ notification: Notification) {
        print("--- reloadTableList ---")
        DispatchQueue.main.async {
            self.resultTableView.reloadData()
        }
    }

    @IBAction func clickNavBack(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let editDeviceViewController = segue.destination as! EditDeviceViewController
        editDeviceViewController.deviceId = -1
        editDeviceViewController.deviceUUID = (sender as! String)
    }
}

extension ScannedDevicesViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return __CACHE_DEVICE_ENTITIES.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let device = __CACHE_DEVICE_ENTITIES[indexPath.row]
        let cell = tableView.dequeueReusableCell(withIdentifier: "ScannedDeviceCell") as! ScannedDeviceCell
        cell.set(deviceEntity: device)
        cell.selectionStyle = .none
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let deviceUUID = __CACHE_DEVICE_ENTITIES[indexPath.row].identifier.uuidString
        self.performSegue(withIdentifier: "scanned2editDevice", sender: deviceUUID)
    }
}
