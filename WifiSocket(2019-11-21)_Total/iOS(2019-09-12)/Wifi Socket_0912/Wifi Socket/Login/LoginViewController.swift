//
//  ViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import SkyFloatingLabelTextField
import IQKeyboardManagerSwift

class LoginViewController: UIViewController {
    
    @IBOutlet weak var edtEmail: SkyFloatingLabelTextField!
    @IBOutlet weak var edtPassword: SkyFloatingLabelTextField!
    @IBOutlet weak var btnLogin: UIButton!
    @IBOutlet weak var btnRemember: UIButton!
    
    var isRemember: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        btnLogin.layer.cornerRadius = 2
//        UIApplication.shared.statusBarView?.backgroundColor = .white
        
//        edtPassword.iconType = IconType.font
//        edtPassword.iconFont = UIFont(name: "Font Awesome 5 Free", size: 15)
//        edtPassword.iconText = "\u{f070}" // "\u{f06e}"
        
        if __DEBUG_MODE {
            edtEmail.text = "yt000605@gmail.com"
            edtPassword.text = "123456"
        }
        
        edtPassword.extendType()
        
        let autoLogin = UserDefaults.standard.bool(forKey: "autoLogin")
        if autoLogin {
            if UserDefaults.standard.string(forKey: "userId") != nil {
                let email = UserDefaults.standard.string(forKey: "account")
                let password = UserDefaults.standard.string(forKey: "password")
                
                edtEmail.text = email
                edtPassword.text = password
                
                btnRemember.isSelected = true
            }
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        // UIApplication.shared.statusBarView?.backgroundColor = .white
        
        let strIOSVersion = UtilFunctions.getiOSVersion()
        let firstNumberOfVersion = Int(strIOSVersion.split(separator: ".")[0])
        if firstNumberOfVersion! < 11 {
            UtilFunctions.showAlertDialog("North Outlet", "This app can run on iOS 11 or higher", "OK", self, {
                exit(EXIT_SUCCESS)
            })
        }
        
        if btnRemember.isSelected {
            isRemember = true
        }
    }
    
    //    override var preferredStatusBarStyle: UIStatusBarStyle {
    //        return .default
    //    }
    
    override var prefersStatusBarHidden: Bool {
        get {
            return true
        }
    }
    
    @IBAction func checkboxTapped(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
        isRemember = sender.isSelected
    }
    
    @IBAction func tryLogin(_ sender: Any) {
        
        // hide keyboard
        self.view.endEditing(true)
        
        let email = edtEmail.text
        let password = edtPassword.text
        
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
        
//        UserDefaults.standard.set(email, forKey: "account")
//        UserDefaults.standard.set(password, forKey: "password")
//        UserDefaults.standard.synchronize()
//
//        self.goToSplashPage()
        
        QMUITips.showLoading("", in: self.view)
        
        HttpRequest.auth(withAccount: email!, withPassword: password!) { result, error in
            DispatchQueue.main.async {
                if error != nil {
                    QMUITips.hideAllTips(in: self.view)
                    
                    let nsError = error! as NSError
                    switch (nsError.code ) {
                    case 4001001:
                        QMUITips.showError("Password length is short than six", in: self.view, hideAfterDelay: 2)
                        break
                    case 4001007:
                        QMUITips.showError("Please enter valid password", in: self.view, hideAfterDelay: 2)
                        break
                    case 4041011:
                        QMUITips.showError("User does not exist", in: self.view, hideAfterDelay: 2)
                        break
                    default:
                        QMUITips.showError("Error message:\(nsError.description)", in: self.view, hideAfterDelay: 2)
                        break
                    }

                    // print(error as Any)
                } else {
                    print(result as Any)

                    var resultDicData: Dictionary = [String: Any]()
                    resultDicData = result as! Dictionary

                    if self.isRemember {
                        UserDefaults.standard.set(true, forKey: "autoLogin")
                    } else {
                        UserDefaults.standard.set(false, forKey: "autoLogin")
                    }

                    let userId = UtilFunctions.checkEveryString(str: String(resultDicData["user_id"] as! Int))
                    let authorize = UtilFunctions.checkEveryString(str: resultDicData["authorize"] as! String)
                    let accessToken = UtilFunctions.checkEveryString(str: resultDicData["access_token"] as! String)
                    UserDefaults.standard.set(userId, forKey: "userId")
                    UserDefaults.standard.set(authorize, forKey: "authorize")
                    UserDefaults.standard.set(email, forKey: "account")
                    UserDefaults.standard.set(password, forKey: "password")
                    UserDefaults.standard.set(resultDicData, forKey: "userData")
                    UserDefaults.standard.synchronize()

                    __USER_ID = userId
                    UtilFunctions.getDeviceDataFromUserDefaults()

                    self.getDataPointWithAccessToken(accessToken: accessToken)
                }
            }
        }
    }
    
    func getDataPointWithAccessToken(accessToken: String) {
        HttpRequest.getDataPointList(withProductID: __PRODUCT_LEXIN_SOCKET, withAccessToken: accessToken) { result, error in
            DispatchQueue.main.async {
                if error != nil {
                    QMUITips.hideAllTips(in: self.view)
                    
                    QMUITips.showError("Network error!", in: self.view, hideAfterDelay: 2)
                } else {
                    // print(result as Any)
                    
//                    UserDefaults.standard.set(result, forKey: "dataPoints")
//                    UserDefaults.standard.synchronize()
 
                    self.goToNextPage()
                }
            }
        }
    }
    
    func goToNextPage() {
        QMUITips.hideAllTips(in: self.view)
        
        if UtilFunctions.haveAnyDeviceEntity() {
            let moveController = self.storyboard?.instantiateViewController(withIdentifier: "rootController") as! RootViewController
            self.present(moveController, animated: true, completion: nil)
        } else {
            let moveController = self.storyboard?.instantiateViewController(withIdentifier: "SplashViewController") as! SplashViewController
            self.present(moveController, animated: true, completion: nil)
        }
    }
    
}
