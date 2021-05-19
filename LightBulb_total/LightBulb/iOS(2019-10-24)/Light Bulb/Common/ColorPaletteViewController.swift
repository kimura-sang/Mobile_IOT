//
//  ColorPaletteViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/19.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class ColorPaletteViewController: UIViewController {
    
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var parentView: UIView!
    
    var colorCircleView: ColorCircleView!
    var label: UILabel!
    var colorSelectView: ColorSelectView!
    var brightSlider: UISlider!
    
    var brightSliderValues: Float!
    
    var peripheralArray: [CBPeripheral]!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        BLEController.setWrite(self.peripheralArray)

        initCircleView()
        initLabel()
        initColorSelect()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    func initBrightSlider() {
        brightSliderValues = 0
        
        if brightSlider == nil {
            brightSlider = UISlider.init(frame: CGRect(x: 35, y: colorCircleView.frame.origin.y + colorCircleView.frame.height + 20, width: self.view.frame.width - 70, height: 20))
            brightSlider.minimumValue = 0
            brightSlider.maximumValue = 255
            brightSlider.value = 255
            
            brightSlider.setThumbImage(UIImage(named: "poin"), for: UIControl.State.normal)
            brightSlider.setThumbImage(UIImage(named: "poin"), for: UIControl.State.highlighted)
            
            brightSlider.tintColor = UIColor.clear
            brightSlider.minimumTrackTintColor = UIColor.clear
            brightSlider.maximumTrackTintColor = UIColor.clear
            
            brightSlider.addTarget(self, action: #selector(brightSliderClick(_:)), for: UIControl.Event.touchUpInside)
        }
    }
    
    @objc func brightSliderClick(_ sender: Any) {
        let slider = sender as! UISlider
        let val = slider.value
        if fabsf(val - brightSliderValues) >= 15.0 {
            brightnessSliderChangeValue(Int(val))
            brightSliderValues = val
        }
    }
    
    func brightnessSliderChangeValue(_ value: Int) {
        if peripheralArray.count > 1 || (peripheralArray.count == 1 && peripheralArray[0].state == CBPeripheralState.connected) {
            isSelectColorPalette = true
            
            colorRScale = Float(value) / 255.0
            colorGScale = Float(value) / 255.0
            colorBScale = Float(value) / 255.0
            
            let alpha = 1
            let R = Float(colorR) * colorRScale
            let G = Float(colorG) * colorGScale
            let B = Float(colorB) * colorBScale
            
            DispatchQueue.global().async {
                BLEController.writeInstructionData(BLEController.bleColorAlpha(Int32(alpha), andRed: Int32(R), andGreen: Int32(G), andBlue: Int32(B)))
                self.updateDeviceStatus()
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
    
    
    func initCircleView() {
        colorCircleView = ColorCircleView.init(frame: CGRect(x: 0, y: 0, width: self.view.frame.width - 30, height: self.view.frame.width - 30))
        colorCircleView.addTarget(self, action: #selector(touchMoved(_:)), for: UIControl.Event.valueChanged)
        colorCircleView.setColorImageSize(CGRect(x: 10, y: 10, width: self.view.frame.width - 50, height: self.view.frame.width - 50))
        self.parentView.addSubview(colorCircleView)
        
        var img: UIImageView!
        img = UIImageView.init(frame: CGRect.init(x: 10, y: colorCircleView.frame.origin.y + colorCircleView.frame.height + 20, width: 20, height: 20))
        img.image = UIImage.init(named: "sun1")

        initBrightSlider()
//        brightSlider.frame = CGRect.init(x: 30, y: img!.frame.origin.y, width: self.view.frame.width - 60, height: 20)

        var img1: UIImageView!
        img1 = UIImageView.init(frame: CGRect.init(x: brightSlider.frame.origin.x + brightSlider.frame.width + 5, y: img!.frame.origin.y, width: 20, height: 20))
        img1.image = UIImage.init(named: "sun")

        var sliderBG: UIImageView!
        sliderBG = UIImageView.init(frame: brightSlider.frame)
        sliderBG.contentMode = UIView.ContentMode.scaleToFill
        sliderBG.image = UIImage.init(named: "bgslider")

        scrollView.addSubview(sliderBG)
        scrollView.addSubview(brightSlider)
        scrollView.addSubview(img!)
        scrollView.addSubview(img1!)
    }
    
    @objc func touchMoved(_ sender: Any) {
        if peripheralArray.count > 1 || (peripheralArray.count == 1 && peripheralArray[0].state == CBPeripheralState.connected) {
            isSelectColorPalette = true
            
            let colView = sender as! ColorCircleView
            let color = colView.selecCcolor
            
            let components = color?.cgColor.components
            
            let R = components![0] * 255
            let G = components![1] * 255
            let B = components![2] * 255
            let alpha = components![3]
            
            if (R < 10 && G < 10 && B < 10) {
                return
            }
            
            colorAlpha = Int32(alpha)
            colorR = Int32(R)
            colorG = Int32(G)
            colorB = Int32(B)
            
            DispatchQueue.global().async {
                BLEController.writeInstructionData(BLEController.bleColorAlpha(colorAlpha, andRed: Int32(Float(colorR) * colorRScale), andGreen: Int32(Float(colorG) * colorRScale), andBlue: Int32(Float(colorB) * colorRScale)))
                self.updateDeviceStatus()
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
    
    
    func initLabel() {
        label = UILabel.init(frame: CGRect(x: brightSlider.frame.origin.x, y: brightSlider.frame.origin.y + 30, width: brightSlider.frame.width, height: 30))
        label.text = "Brightness"
        label.textAlignment = NSTextAlignment.center
        label.textColor = UIColor.lightGray
        label.font = UIFont.systemFont(ofSize: 16, weight: UIFont.Weight.light)
        
        scrollView.addSubview(label)
    }
    
    
    func initColorSelect() {
        var colorArray: [String]!
        if UserDefaults.standard.array(forKey: "FavColors") != nil {
            colorArray = (UserDefaults.standard.array(forKey: "FavColors") as! [String])
        }
        if colorArray == nil || colorArray.count == 0 {
            colorArray = ["#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF"]
        }
        
        colorSelectView = ColorSelectView.init(frame: CGRect(x: 0, y: label.frame.origin.y + 35, width: self.view.frame.width, height: 200))
        colorSelectView!.colorArray = [UIColor.init(hex: "#FF6A58FF")!,
                                       UIColor.init(hex: "#8BC1FFFF")!,
                                       UIColor.init(hex: "#F5E1E1FF")!,
                                       UIColor.init(hex: "#FF8B2DFF")!,
                                       UIColor.init(hex: colorArray[0])!,
                                       UIColor.init(hex: colorArray[1])!,
                                       UIColor.init(hex: colorArray[2])!,
                                       UIColor.init(hex: colorArray[3])!]
        colorSelectView!.createButton()
        colorSelectView.delegate = self
        scrollView.addSubview(colorSelectView)
    }
    
    func updateDeviceStatus() {
        if peripheralArray.count > 0 && __DEVICES.count > 0 {
            for i in 0...(__DEVICES.count - 1) {
                periphLoop: for j in 0...(peripheralArray.count - 1) {
                    if __DEVICES[i].deviceUUID == peripheralArray[j].identifier.uuidString && __DEVICES[i].openStatus == false {
                        __DEVICES[i].openStatus = true
                        break periphLoop
                    }
                }
            }
        }
        
        UtilFunctions.syncUserData()
        NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
    }
    
    func finish() {
        self.dismiss(animated: true, completion: nil)
    }

    @IBAction func clickNavBack(_ sender: Any) {
        finish()
    }
    
    @IBAction func clickMusic(_ sender: Any) {
        let moveController = self.storyboard?.instantiateViewController(withIdentifier: "MusicViewController") as! MusicViewController
        moveController.peripheralArray = peripheralArray
        self.present(moveController, animated: true, completion: nil)
    }
    
    @IBAction func clickShake(_ sender: Any) {
        let moveController = self.storyboard?.instantiateViewController(withIdentifier: "ShakeViewController") as! ShakeViewController
        moveController.peripheralArray = peripheralArray
        self.present(moveController, animated: true, completion: nil)
    }
    
}

extension ColorPaletteViewController: ColorSelectViewDelegate {
    func back(_ color: UIColor, andTag tag: Int32) {
        if peripheralArray.count > 1 || (peripheralArray.count == 1 && peripheralArray[0].state == CBPeripheralState.connected) {
            isSelectColorPalette = false
            
            let components = color.cgColor.components
            
            let R = components![0] * 255
            let G = components![1] * 255
            let B = components![2] * 255
            let alpha = components![3]
            
            colorAlpha = Int32(alpha)
            colorR = Int32(R)
            colorG = Int32(G)
            colorB = Int32(B)
            
            DispatchQueue.global().async {
                BLEController.writeInstructionData(BLEController.bleColorAlpha(colorAlpha, andRed: Int32(Float(colorR) * colorRScale), andGreen: Int32(Float(colorG) * colorRScale), andBlue: Int32(Float(colorB) * colorRScale)))
                self.updateDeviceStatus()
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
    
    func saveColor(_ tag: Int32) {
        var color: String!
        if isSelectColorPalette.boolValue {
            color = String(format: "#%02X%02X%02XFF", Int(Float(colorR) * colorRScale), Int(Float(colorG) * colorGScale), Int(Float(colorB) * colorBScale))
        } else {
            color = String(format: "#%02X%02X%02XFF", Int(colorR), Int(colorG), Int(colorB))
        }
        
        var colorArray: [String]!
        if UserDefaults.standard.array(forKey: "FavColors") != nil {
            colorArray = (UserDefaults.standard.array(forKey: "FavColors") as! [String])
        }
        if colorArray == nil || colorArray.count == 0 {
            colorArray = ["#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF", "#FFFFFFFF"]
        }
        
        colorArray[Int(tag)] = color!
        
        UserDefaults.standard.set(colorArray, forKey: "FavColors")
        UserDefaults.standard.synchronize()
        
        colorSelectView!.colorArray = [UIColor.init(hex: "#FF6A58FF")!,
                                       UIColor.init(hex: "#8BC1FFFF")!,
                                       UIColor.init(hex: "#F5E1E1FF")!,
                                       UIColor.init(hex: "#FF8B2DFF")!,
                                       UIColor.init(hex: colorArray[0])!,
                                       UIColor.init(hex: colorArray[1])!,
                                       UIColor.init(hex: colorArray[2])!,
                                       UIColor.init(hex: colorArray[3])!]
    }
}
