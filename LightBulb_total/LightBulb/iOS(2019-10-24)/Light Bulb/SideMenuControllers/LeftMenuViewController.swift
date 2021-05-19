//
//  LeftMenuViewController.swift
//  Light Bulb
//
//  Created by king on 2019/9/3.
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
            self.sideMenuViewController!.setContentViewController(UINavigationController(rootViewController: self.storyboard!.instantiateViewController(withIdentifier: "TimerViewController")), animated: true)
            self.sideMenuViewController!.hideMenuViewController()
            break
        case 3:
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
        return 4
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
        
        var titles = ["DEVICES", "GROUPS", "TIMERS", "ABOUT"]
        var images = ["Transparency", "Transparency", "Transparency", "Transparency"]
        cell!.textLabel?.text = titles[indexPath.row]
        cell!.imageView?.image = UIImage(named: images[indexPath.row])
        
        return cell!
    }
}
