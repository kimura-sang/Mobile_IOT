//
//  RootViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import AKSideMenu

class RootViewController: AKSideMenu, AKSideMenuDelegate {
    
    override public func awakeFromNib() {
        super.awakeFromNib()
        self.menuPreferredStatusBarStyle = .lightContent
        self.contentViewShadowColor = .black
        self.contentViewShadowOffset = CGSize(width: 0, height: 0)
        self.contentViewShadowOpacity = 0.6
        self.contentViewShadowRadius = 12
        self.contentViewShadowEnabled = true
        
        self.backgroundImage = UIImage(named: "MenuBG")
        self.delegate = self
        
        if let storyboard = self.storyboard {
            self.contentViewController = storyboard.instantiateViewController(withIdentifier: "ContentViewController")
            self.leftMenuViewController = storyboard.instantiateViewController(withIdentifier: "LeftMenuViewController")
        }
    }
    
    // MARK: - <AKSideMenuDelegate>
    
    public func sideMenu(_ sideMenu: AKSideMenu, willShowMenuViewController menuViewController: UIViewController) {
//        print("willShowMenuViewController")
    }
    
    public func sideMenu(_ sideMenu: AKSideMenu, didShowMenuViewController menuViewController: UIViewController) {
//        print("didShowMenuViewController")
    }
    
    public func sideMenu(_ sideMenu: AKSideMenu, willHideMenuViewController menuViewController: UIViewController) {
//        print("willHideMenuViewController")
    }
    
    public func sideMenu(_ sideMenu: AKSideMenu, didHideMenuViewController menuViewController: UIViewController) {
//        print("didHideMenuViewController")
    }
    
}
