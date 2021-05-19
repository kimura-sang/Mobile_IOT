//
//  AboutUsViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import MessageUI
import Foundation

class AboutUsViewController: UIViewController {
    
    @IBOutlet weak var lblVersion: UILabel!
    
    var leftMenuController: LeftMenuViewController!

    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationController?.navigationBar.barStyle = .black
        navigationController?.navigationBar.barTintColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x29C6A7)
        
        leftMenuController = (self.sideMenuViewController?.leftMenuViewController as! LeftMenuViewController)
        
        let appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
        lblVersion.text = String.init(format: "Version No : %@", appVersion!)
    }
    
    @IBAction func goToNext(_ sender: Any) {
        leftMenuController.goToNextView(0)
    }
    
    @IBAction func openHomeSite(_ sender: Any) {
        UIApplication.shared.open(URL(string: "http://www.weliveupnorth.com")!)
    }
    
    @IBAction func sendEmail(_ sender: Any) {
        // This needs to be ran on a device
        showMailComposer()
    }
    
    func showMailComposer() {
        if !MFMailComposeViewController.canSendMail() {
            QMUITips.showInfo("Your device could not send e-mail. \nPlease check e-mail configuration and try again.", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let composer = MFMailComposeViewController()
        composer.mailComposeDelegate = self
        composer.setToRecipients(["Support@weliveupnorth.com"])
        composer.setSubject("")
        composer.setMessageBody("", isHTML: false)
        
        present(composer, animated: true)
    }
    
}

extension AboutUsViewController: MFMailComposeViewControllerDelegate {
    
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        if let _ = error {
            QMUITips.showError("Cannot send email", in: self.view, hideAfterDelay: 2)
            controller.dismiss(animated: true)
            return
        }
        
        switch result {
        case .cancelled:
            QMUITips.showInfo("Cancel sending", in: self.view, hideAfterDelay: 2)
            print("Cancelled")
        case .failed:
            QMUITips.showError("Failed to send", in: self.view, hideAfterDelay: 2)
            print("Failed to send")
        case .saved:
            QMUITips.showInfo("Draft saved", in: self.view, hideAfterDelay: 2)
            print("Saved")
        case .sent:
            QMUITips.showSucceed("Email sent", in: self.view, hideAfterDelay: 2)
            print("Email sent")
        }
        
        controller.dismiss(animated: true)
    }
    
}
