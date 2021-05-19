//
//  ForgotPwdViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/6.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField

class ForgotPwdViewController: UIViewController {
    
    @IBOutlet weak var edtEmail: SkyFloatingLabelTextField!
    @IBOutlet weak var btnResetPassword: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        btnResetPassword.layer.cornerRadius = 2
        
        if __DEBUG_MODE {
            edtEmail.text = "yt000605@gmail.com"
        }
    }
    
    override var prefersStatusBarHidden: Bool {
        get {
            return true
        }
    }
    
    @IBAction func goBack(_ sender: UIButton) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func resetPassword(_ sender: UIButton) {
        // hide keyboard
        self.view.endEditing(true)
        
        let email = edtEmail.text        
        let emailValid = UtilFunctions.emailValidation(email!)
        if !emailValid {
            QMUITips.showError("Please enter valid email.", in: self.view, hideAfterDelay: 2)
            return
        }
        
        QMUITips.showLoading("", in: self.view)
        
        HttpRequest.forgotPassword(withAccount: email!) { result, error in
            DispatchQueue.main.async {
                QMUITips.hideAllTips(in: self.view)
                if error != nil {
                    let nsError = error! as NSError
                    // QMUITips.showError("Error message:\(nsError.description)", in: self.view, hideAfterDelay: 2)
                    
                    QMUITips.showError("Network Error", in: self.view, hideAfterDelay: 2)
                    print(error as Any)
                } else {
                    let msgContent: String! = "An email has been sent to\n" + email! + ". Click the link in that email to reset your password."
                    UtilFunctions.showAlertDialog("North Outlet", msgContent, "OK", self, {
                        self.dismiss(animated: true, completion: nil)
                    })
                }
            }
        }
    }
    

}
