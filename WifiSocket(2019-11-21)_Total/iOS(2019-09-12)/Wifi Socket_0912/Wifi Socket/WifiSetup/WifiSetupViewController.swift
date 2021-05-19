//
//  WifiSetupViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/4.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import NetworkExtension
import SkyFloatingLabelTextField
import AVKit
import EspTouch
import CocoaAsyncSocket
import CoreLocation

class WifiSetupViewController: UIViewController, CLLocationManagerDelegate {
    
    @IBOutlet weak var edtSSID: SkyFloatingLabelTextField!
    @IBOutlet weak var edtPassword: SkyFloatingLabelTextField!
    @IBOutlet weak var btnSetup: UIButton!
    
    var leftMenuController: LeftMenuViewController!
    var wifiSSID: String!
    var wifiPassword: String!
    
    var locationManager = CLLocationManager()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestAlwaysAuthorization()
        
        QMUITips.showLoading("Loading...", in: self.view, hideAfterDelay: 2)
        
        navigationController?.navigationBar.barStyle = .black
        navigationController?.navigationBar.barTintColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7)
        
        btnSetup.layer.cornerRadius = 2
        
        leftMenuController = (self.sideMenuViewController?.leftMenuViewController as! LeftMenuViewController)
        __LEFT_MENU_CONTROLLER = leftMenuController
        
        // auto detect wifi ssid and set it
        wifiSSID = WifiManager.getSSID()
        if wifiSSID != nil && wifiSSID != "" {
            edtSSID.text = wifiSSID
            
            let wifiStruct: WifiStruct = WifiManager.loadWifiInfo(wifiSSID)
            if wifiStruct.password != "" {
                edtPassword.text = wifiStruct.password
            }
        }
        
        if __DEBUG_MODE {
            edtSSID.text = "CU_VkHn"
//            edtSSID.text = "szerby@gamil.com"
//            edtPassword.text = "Qwert!2345Qwert!2345"
            edtPassword.text = "vkhn0h8t"
        }
        
        edtPassword.extendType()
//        edtSSID.isUserInteractionEnabled = false
    }
    
    @IBAction func goToNext(_ sender: Any) {
        leftMenuController.goToNextView(4)
    }
    
    @IBAction func connectWifi(_ sender: Any) {
        
        // hide keyboard
        self.view.endEditing(true)

        wifiSSID = edtSSID.text
        wifiPassword = edtPassword.text

        if wifiSSID.count == 0 {
            QMUITips.showError("SSID should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }

//        if wifiPassword.count == 0 {
//            QMUITips.showError("Please should not be blank", in: self.view, hideAfterDelay: 2)
//            return
//        }

        var configuration: NEHotspotConfiguration!
        if wifiPassword.count == 0 {
            configuration = NEHotspotConfiguration.init(ssid: wifiSSID)
        } else {
            configuration = NEHotspotConfiguration.init(ssid: wifiSSID, passphrase: wifiPassword, isWEP: false)
        }
        configuration.joinOnce = false   /*set to 'true' if you only want to join
                                         the network while the user is within the
                                         app, then have it disconnect when user
                                         leaves your app*/

        UtilFunctions.preventTouchEvent()
        let strSSID = WifiManager.getSSID()
        if strSSID == self.wifiSSID {
            self.loginHttpRequest()
            
            print("Connected")
            DispatchQueue.main.async {
                let bssid = WifiManager.getBSSID() ?? ""
                WifiManager.saveWifiInfo(self.wifiSSID, bssid, self.wifiPassword)
                self.sendSmartConfig()
            }
        } else {
            QMUITips.showLoading("Connecting...", in: self.view)
            
            NEHotspotConfigurationManager.shared.apply(configuration) { (error) in
                QMUITips.hideAllTips(in: self.view)
                if error != nil {
                    if error?.localizedDescription == "already associated." {
                        self.loginHttpRequest()
                        
                        print("already associated.")
                        QMUITips.showSucceed("Wifi Connected", in: self.view, hideAfterDelay: 2)
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { // Change `2.0` to the desired number of seconds.
                            let bssid = WifiManager.getBSSID() ?? ""
                            WifiManager.saveWifiInfo(self.wifiSSID, bssid, self.wifiPassword)
                            self.sendSmartConfig()
                        }
                    } else{
                        UtilFunctions.restoreTouchEvent()
                        let errorCode = UtilFunctions.getErrorCode(error: error!)
                        switch errorCode {
                        case 2:
                            QMUITips.showError("Invalid WPA/WPA2 SSID or Password", in: self.view, hideAfterDelay: 2)
                            break
                        case 7:
                            break
                        default:
                            QMUITips.showError(error?.localizedDescription, in: self.view, hideAfterDelay: 2)
                            break
                        }
                        print("No Connected : \(String(describing: error?.localizedDescription))")
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                            self.showThreeConfirmDialog()
                        }
                    }
                } else {
                    let strSSID = WifiManager.getSSID()
                    //                let wifiStr = String.init(format: "Old Wifi: %@, New Wifi: %@", self.wifiSSID, strSSID!)
                    //                QMUITips.showSucceed(wifiStr, in: self.view, hideAfterDelay: 3)
                    if strSSID == self.wifiSSID {
                        self.loginHttpRequest()
                        
                        print("Connected")
                        QMUITips.showSucceed("Wifi Connected", in: self.view, hideAfterDelay: 2)
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) { // Change `2.0` to the desired number of seconds.
                            let bssid = WifiManager.getBSSID() ?? ""
                            WifiManager.saveWifiInfo(self.wifiSSID, bssid, self.wifiPassword)
                            self.sendSmartConfig()
                        }
                    } else {
                        UtilFunctions.restoreTouchEvent()
                        self.showThreeConfirmDialog()
                    }
                }
            }
        }
    }
    
    func scanDevice() {
        __DEVICE_ENTITIES = []
        
        DispatchQueue.main.async {
            QMUITips.hideAllTips(in: self.view)
            UtilFunctions.restoreTouchEvent()
            
            let result = XLinkExportObject.shared()?.scan(byDeviceProductID: __PRODUCT_LEXIN_SOCKET)
            print("Scan Result" + result.debugDescription)
            
            self.goToScannedPage()
        }
    }
    
    func loginHttpRequest() {
        let userId = UserDefaults.standard.integer(forKey: "userId")
        let authorize = UserDefaults.standard.string(forKey: "authorize")
        
        __USER_ID = String(userId)
        
        if XLinkExportObject.shared()?.login(withAppID: Int32(userId), andAuthStr: authorize) == 0 {
            __REAL_DEVICE_ENTITIES = []
            UtilFunctions.getDeviceDataFromUserDefaults()
            UtilFunctions.restartRealDeviceEntities()
        }
    }
    
    func showThreeConfirmDialog() {
        let alert = UIAlertController(title: "No Devices Found", message: "Do you know if your phone is connected to the 2.4ghz frequency?", preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Yes",
                                      style: .default,
                                      handler: {(_: UIAlertAction!) in
                                        UtilFunctions.showConfirmDialog("No Devices Found",
                                            "Is the LED light flashing red and blue on device?",
                                            "Yes",
                                            "No",
                                            self,
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Please try unplugging the device and repeat the setup process.", "Okay", self, { }) },
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Press and hold the clear button on the device for 30 seconds and release to reset the device and repeat the setup process.", "Okay", self, { }) })
        }))
        alert.addAction(UIAlertAction(title: "No",
                                      style: .cancel,
                                      handler: {(_: UIAlertAction!) in
                                        UtilFunctions.showConfirmDialog("No Devices Found",
                                            "Do you know how to access your routers setup screen?",
                                            "Yes",
                                            "No",
                                            self,
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Please follow your routers instructions to disable the 5ghz band. You can turn it back on after the device setup is complete.", "Okay", self, { }) },
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Please watch this animation for a way that may help force the phone to use the 2.4ghz band in your router.", "Okay", self, { self.playHelpVideo() }) })
        }))
        alert.addAction(UIAlertAction(title: "Unsure",
                                      style: .destructive,
                                      handler: {(_: UIAlertAction!) in
                                        UtilFunctions.showConfirmDialog("No Devices Found",
                                            "Do you see a choice in your wifi settings with the numbers 2.4ghz in the wifi name?",
                                            "Yes",
                                            "No",
                                            self,
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Choose the 2.4ghz name in your phones setup screen and repeat the setup process.", "Okay", self, { }) },
                                            { UtilFunctions.showAlertDialog("No Devices Found", "Please watch this animation for a way that may help force the phone to use the 2.4ghz band in your router.", "Okay", self, { self.playHelpVideo() }) })
        }))
        
        self.present(alert, animated: true)
    }
    
    func playHelpVideo() {
        self.performSegue(withIdentifier: "wifi2video", sender: "video")
    }
    
    func goToScannedPage() {
        self.leftMenuController.goToNextView(0)
        self.performSegue(withIdentifier: "wifi2scanned", sender: "scanned")
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (sender as! String) == "scanned" {
            let scannedDevicesViewController = segue.destination as! ScannedDevicesViewController
            scannedDevicesViewController.fromWifiPage = 1
        }
    }
    
    
    
    var esptouchTask : ESPTouchTask?
    var resultExpected = 10
    var results : Array<ESPTouchResult> = Array()
    var resultCount = 0
    
    func sendSmartConfig(){
        QMUITips.showLoading("Scanning...", in: self.view)
        
        results.removeAll()
        DispatchQueue.main.async {
            self.results = self.executeESPTouchTask() as! Array<ESPTouchResult>
//            for result in self.results {
//            }
            
            self.sendUDPBroadCast()
        }
    }
    
    
    func executeESPTouchTask() -> NSArray {
        var results: NSArray! = []
        let bssid = WifiManager.getBSSID()
        self.esptouchTask = ESPTouchTask.init(apSsid: self.wifiSSID, andApBssid: bssid!, andApPwd: self.wifiPassword)
        if let task = self.esptouchTask {
            task.setEsptouchDelegate(self)
            results = (task.execute(forResults: Int32(self.resultExpected == 0 ? Int.max : self.resultExpected))! as NSArray)
        }
        
        return results!
    }
    
    func sendUDPBroadCast() {
        let data = Data(hexString: UtilFunctions.getBindBytes())
        let udpSocket = GCDAsyncUdpSocket.init(delegate: self, delegateQueue: DispatchQueue.main)
        
        do {
            try udpSocket.enableBroadcast(true)
        } catch {
            print(error)
        }
        
        udpSocket.send(data!, toHost: __BIND_HOST, port: UInt16(__BIND_PORT), withTimeout: -1, tag: 1)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) {
            udpSocket.close()
            self.scanDevice()
        }
    }
}


extension WifiSetupViewController: ESPTouchDelegate, GCDAsyncUdpSocketDelegate  {
    
    func onEsptouchResultAdded(with result: ESPTouchResult!) {
        results.append(result)
        DispatchQueue.main.async {
            if(self.results.count == self.resultExpected ){
                self.esptouchTask?.interrupt()
            }
            self.resultCount = self.resultCount + 1
        }
    }
    
    
    // GCDAsyncUdpSocketDelegate
    func udpSocket(_ sock: GCDAsyncUdpSocket, didSendDataWithTag tag: Int) {
        print("didSendDataWithTag")
    }
    
    private func udpSocket(sock: GCDAsyncUdpSocket!, didNotSendDataWithTag tag: Int, dueToError error: NSError!) {
        print("didNotSendDataWithTag")
    }
}
