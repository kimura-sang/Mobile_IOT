//
//  RegisterViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/6.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField

class RegisterViewController: UIViewController {
    
    @IBOutlet weak var edtUserName: SkyFloatingLabelTextField!
    @IBOutlet weak var edtEmail: SkyFloatingLabelTextField!
    @IBOutlet weak var edtPassword: SkyFloatingLabelTextField!
    @IBOutlet weak var edtConfirmPassword: SkyFloatingLabelTextField!
    @IBOutlet weak var btnCreateAccount: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        btnCreateAccount.layer.cornerRadius = 2
        
        if __DEBUG_MODE {
            edtUserName.text = "Young Tomas"
            edtEmail.text = "yt000605@gmail.com"
            edtPassword.text = "123456"
            edtConfirmPassword.text = "123456"
        }
        
        edtPassword.extendType()
    }
    
    override var prefersStatusBarHidden: Bool {
        get {
            return true
        }
    }
    
    @IBAction func goBack(_ sender: UIButton) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func createAccount(_ sender: Any) {
        // hide keyboard
        self.view.endEditing(true)
        
        let name = edtUserName.text
        let email = edtEmail.text
        let password = edtPassword.text
        let confirmPassword = edtConfirmPassword.text
        
        let nameValid = UtilFunctions.nameValidation(name!)
        if !nameValid {
            QMUITips.showError("The Name should not be blank", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let emailValid = UtilFunctions.emailValidation(email!)
        if !emailValid {
            QMUITips.showError("Please enter valid email", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let passwordValid = UtilFunctions.passwordValidation(password!)
        if !passwordValid {
            QMUITips.showError("Please choose password with \n more than 6 character", in: self.view, hideAfterDelay: 2)
            return
        }
        
        let twoPasswordValid = UtilFunctions.twoPasswordIsSame(password!, confirmPassword!)
        if !twoPasswordValid {
            QMUITips.showError("Confirm password must match with Password", in: self.view, hideAfterDelay: 2)
            return
        }
        
        QMUITips.showLoading("", in: self.view)
        
        HttpRequest.register(withAccount: email!, withNickname: name!, withVerifyCode: nil, withPassword: password!) {result, error in
            DispatchQueue.main.async {
                QMUITips.hideAllTips(in: self.view)
                if error != nil {
                    let nsError = error! as NSError
                    switch (nsError.code ) {
                    case 4001006:
                        QMUITips.showError("This Email was already registered", in: self.view, hideAfterDelay: 2)
                        break
                    default:
                        QMUITips.showError("Network Error", in: self.view, hideAfterDelay: 2)
                        // QMUITips.showError("Error message:\(nsError.description)", in: self.view, hideAfterDelay: 2)
                        break
                    }
                        
                    print(error as Any)
                } else {
                    UtilFunctions.showAlertDialog("North Outlet", "Register Success!", "OK", self, {
                        self.dismiss(animated: true, completion: nil)
                    })
                }
            }
        }
    }
    
}
