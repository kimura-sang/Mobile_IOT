//
//  MusicViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/19.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class MusicViewController: UIViewController {
    
    var peripheralArray: [CBPeripheral]!
    var recordView: FXRecordArcView!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        UIApplication.shared.isIdleTimerDisabled = true
        
        recordView = FXRecordArcView.init(frame: CGRect(x: 0, y: 50, width: self.view.frame.width, height: self.view.frame.height - 50))
        recordView.peripheralArray = peripheralArray
        recordView.delegate = self
        
        self.view.addSubview(recordView)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        recordView.start(forFilePath: fullPathAtCache("record.wav"))
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    func fullPathAtCache(_ fileName: String) -> String {
        var path = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).last
        let fileManager = FileManager.default
        if fileManager.fileExists(atPath: path!) {
            do {
                try fileManager.createDirectory(atPath: path!, withIntermediateDirectories: true, attributes: nil)
            } catch let error {
                print(error.localizedDescription)
            }
        }
        
        path!.append("/")
        path!.append(fileName)
        return path!
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    
    @IBAction func clickNavBack(_ sender: Any) {
        finish()
    }
    
    func finish() {
        recordView.commitRecording()
        UIApplication.shared.isIdleTimerDisabled = false
        
        self.dismiss(animated: true, completion: nil)
    }
    
}

extension MusicViewController: FXRecordArcViewDelegate {
    func recordArcView(_ arcView: FXRecordArcView!, voiceRecorded recordPath: String!, length recordLength: Float) {
        
    }
    
    func deviceDisconnected() {
        DispatchQueue.main.async {
            QMUITips.showInfo("Device is offline", in: self.view, hideAfterDelay: 2)
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                self.finish()
            }
        }
    }
    
}
