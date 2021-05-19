//
//  ShakeViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/19.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ShakeViewController: UIViewController {
    
    @IBOutlet weak var imgMotion: UIImageView!
    @IBOutlet weak var imgShake: UIImageView!
    @IBOutlet weak var btnLight: UIButton!
    @IBOutlet weak var btnRandom: UIButton!
    
    var peripheralArray: [CBPeripheral]!
    var isSelectRandom: Bool!
    var switchLamp: Int! = 0
    var soundID: SystemSoundID! = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        
        BLEController.setWrite(peripheralArray)
        
        isSelectRandom = false
        UIApplication.shared.applicationSupportsShakeToEdit = true
        
        becomeFirstResponder()
        
        let path = Bundle.main.path(forResource: "5018", ofType: "wav")
        AudioServicesCreateSystemSoundID(URL.init(fileURLWithPath: path!) as CFURL, &soundID)
    }
    
    override var canBecomeFirstResponder: Bool {
        return true
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    
    override func motionBegan(_ motion: UIEvent.EventSubtype, with event: UIEvent?) {
        addAnimations()
        AudioServicesPlaySystemSound(soundID)
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
    }
    
    override func motionCancelled(_ motion: UIEvent.EventSubtype, with event: UIEvent?) {
        print("Cancel shaking")
    }
    
    override func motionEnded(_ motion: UIEvent.EventSubtype, with event: UIEvent?) {
        if event?.subtype == UIEvent.EventSubtype.motionShake {
            
        }
        
        print("Shaking ended!")
        
        if isSelectRandom {
            selectRandomColor()
        } else {
            switchLampBulb()
        }
    }
    
    func addAnimations() {
        let translation = CABasicAnimation.init(keyPath: "transform")
        translation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeInEaseOut)
        translation.toValue = NSValue.init(caTransform3D: CATransform3DMakeRotation(-.pi/4, 0, 0, 100))
        
        translation.duration = 0.2
        translation.repeatCount = 2
        translation.autoreverses = true
        
        imgShake.layer.add(translation, forKey: "translation")
    }
    
    func switchLampBulb() {
        switchLamp += 1
        
        if peripheralArray.count > 1 || (peripheralArray.count == 1 && peripheralArray[0].state == CBPeripheralState.connected) {
            var switchOn = false
            var switchString = "close"
            if switchLamp % 2 == 1 {
                switchString = "open"
                switchOn = true
            }
            
            for periph in peripheralArray {
                UtilFunctions.sendBLEData(periph, switchString)
                UtilFunctions.setDeviceStatus(uuid: periph.identifier.uuidString, status: switchOn, needSave: false)
            }
            
            UtilFunctions.syncUserData()

        } else {
            DispatchQueue.main.async {
                QMUITips.showInfo("Device is offline", in: self.view, hideAfterDelay: 2)
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.finish()
                }
            }
        }
    }
    
    func selectRandomColor() {
        if peripheralArray.count > 1 || (peripheralArray.count == 1 && peripheralArray[0].state == CBPeripheralState.connected) {
            let R = arc4random() % 255
            let G = arc4random() % 255
            let B = arc4random() % 255
            let alpha = 1
            
            DispatchQueue.global().async {
                BLEController.writeInstructionData(BLEController.bleColorAlpha(Int32(alpha), andRed: Int32(R), andGreen: Int32(G), andBlue: Int32(B)))
            }
        } else {
            DispatchQueue.main.async {
                QMUITips.showInfo("Device is offline", in: self.view, hideAfterDelay: 2)
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.finish()
                }
            }
        }
    }
    
    func finish() {
        self.dismiss(animated: true, completion: nil)
    }

    @IBAction func clickNavBack(_ sender: Any) {
        finish()
    }
    
    @IBAction func clickLight(_ sender: Any) {
        isSelectRandom = false
        
        btnLight.backgroundColor = UIColor(hex: "#29C6A7FF")
        btnLight.setTitleColor(UIColor.white, for: .normal)
        
        btnRandom.backgroundColor = UIColor.white
        btnRandom.titleLabel?.textColor = UIColor(hex: "#29C6A7FF")
    }
    
    @IBAction func clickRandom(_ sender: Any) {
        isSelectRandom = true
        
        btnRandom.backgroundColor = UIColor(hex: "#29C6A7FF")
        btnRandom.setTitleColor(UIColor.white, for: .normal)
        
        btnLight.backgroundColor = UIColor.white
        btnLight.titleLabel?.textColor = UIColor(hex: "#29C6A7FF")
    }
    
}
