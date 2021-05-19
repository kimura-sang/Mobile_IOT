//
//  LeftMenuViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

public class LeftMenuViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var tableView: UITableView?
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        UtilFunctions.setStatusBarBackgroundColor(color: UIColor(hex: "#29C6A7FF") ?? .white)
        
        let tableView = UITableView(frame: CGRect(x: 0, y: (self.view.frame.size.height - 54 * 6) / 2.0, width: self.view.frame.size.width, height: 54 * 6), style: .plain)
        tableView.autoresizingMask = [.flexibleTopMargin, .flexibleBottomMargin, .flexibleWidth]
        tableView.delegate = self
        tableView.dataSource = self
        tableView.isOpaque = false
        tableView.backgroundColor = .clear
        tableView.backgroundView = nil
        tableView.separatorStyle = .none
        tableView.bounces = false
        
        self.tableView = tableView
        self.view.addSubview(self.tableView!)
        
        autoLoginWithAppId()
        
        if UtilFunctions.haveAnyDeviceEntity() {
            goToNextView(0)
        }
    }
    
    func autoLoginWithAppId() {
        let userId = UserDefaults.standard.string(forKey: "userId")
        let authorize = UserDefaults.standard.string(forKey: "authorize")
        
        __USER_ID = userId!
        
        if XLinkExportObject.shared()?.login(withAppID: Int32(userId!)!, andAuthStr: authorize) == 0 {
            __REAL_DEVICE_ENTITIES = []
            UtilFunctions.getDeviceDataFromUserDefaults()
            UtilFunctions.runDeviceConnection()
            UtilFunctions.restartRealDeviceEntities()
        } else {
            tryLogout()
        }
    }
    
    // MARK: - <UITableViewDelegate>
    
    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        switch indexPath.row {
        case 0:
            goToNextView(0)
            
        case 1:
            goToNextView(1)
            
        case 2:
            goToNextView(2)
            
        case 3:
            goToNextView(3)
            
        case 4:
            goToNextView(4)
            
        case 5:
            tryLogout()
            
        default:
            break
        }
    }
    
    public func goToNextView(_ pageNo: Int) {
        switch pageNo {
        case 0:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "DeviceViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        case 1:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "GroupViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        case 2:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "ScheduleViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        case 3:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "WifiSetupViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        case 4:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "AboutUsViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        default:
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "DeviceViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        }
    }
    
    // MARK: - <UITableViewDataSource>
    
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 54
    }
    
    public func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection sectionIndex: Int) -> Int {
        return 6
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier: String = "Cell"
        
        var cell: UITableViewCell? = tableView.dequeueReusableCell(withIdentifier: cellIdentifier)
        
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: cellIdentifier)
            cell!.backgroundColor = .clear
            cell!.textLabel?.font = UIFont(name: "HelveticaNeue", size: 17)
            cell!.textLabel?.textColor = .white
            cell!.textLabel?.highlightedTextColor = .lightGray
            cell!.selectedBackgroundView = UIView()
        }
        
        var titles = ["DEVICES", "GROUPS", "SCHEDULE", "WIFI SETUP", "ABOUT","LOGOUT"]
        var images = ["Transparency", "Transparency", "Transparency", "Transparency", "Transparency", "Transparency"]
        cell!.textLabel?.text = titles[indexPath.row]
        cell!.imageView?.image = UIImage(named: images[indexPath.row])
        
        return cell!
    }
    
    func tryLogout() {
        XLinkExportObject.shared()?.logout()
        
        let autoLogin = UserDefaults.standard.bool(forKey: "autoLogin")
        if !autoLogin {
            UserDefaults.standard.set(false, forKey: "autoLogin")
            UserDefaults.standard.set("", forKey: "userId")
            UserDefaults.standard.set("", forKey: "account")
            UserDefaults.standard.set("", forKey: "password")
            UserDefaults.standard.set(nil, forKey: "userData")
            UserDefaults.standard.set("", forKey: "authorize")
            UserDefaults.standard.synchronize()
        }
        
        let transition: CATransition = CATransition()
        transition.duration = 0.5
        transition.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.default)
        transition.type = CATransitionType.reveal
        transition.subtype = CATransitionSubtype.fromRight
        if self.view != nil && self.view.window != nil {
            self.view.window!.layer.add(transition, forKey: nil)
        }
        
        let moveController = self.storyboard?.instantiateViewController(withIdentifier: "LoginViewController") as! LoginViewController
        self.present(moveController, animated: true, completion: nil)
    }
}
