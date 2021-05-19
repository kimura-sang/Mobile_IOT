//
//  SplashViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import Ahoy

class SplashViewController: OnboardingViewController {
       
    @IBOutlet weak var nextButton: UIButton!
    
    override func viewDidLoad() {
        presenter = MovieFanPresenter()
        super.viewDidLoad()
        setupNextButton()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        let modelName = UIDevice.modelName
        if modelName.contains("iPhone X") {
            self.view.backgroundColor = UIColor(hex: "#88E1CDFF")
            UtilFunctions.setStatusBarBackgroundColor(color: UIColor(hex: "#57C0ACFF") ?? .white)
        }
    }
    
    override var prefersStatusBarHidden: Bool {
        get {
            return false
        }
    }
    
    open func setupNextButton() {
        nextButton.setTitle(NSLocalizedString("Next", comment: ""), for: .normal)
        nextButton.setTitleColor(.white, for: .normal)
        nextButton.addTarget(self, action: #selector(BottomOnobardingController.nextTapped(sender:)), for: .touchUpInside)
    }
    
    override open func pageChanged(to page: Int) {
        pageControl?.currentPage = page
        nextButton.setTitle(page == presenter.pageCount - 1 ? NSLocalizedString("Done", comment: "") : NSLocalizedString("Next", comment: ""), for: .normal)
    }
    
    @objc open func nextTapped(sender: UIButton) {
        if currentPage == presenter.pageCount - 1 {
            let moveController = self.storyboard?.instantiateViewController(withIdentifier: "rootController") as! RootViewController
            self.present(moveController, animated: true, completion: nil)
        } else {
            currentPage = currentPage + 1
            let index = IndexPath(row: currentPage, section: 0)
            collectionView?.scrollToItem(at: index, at: UICollectionView.ScrollPosition.left, animated: true)
        }
    }
    
    @IBAction func clickSkip(_ sender: UIButton) {
        let moveController = self.storyboard?.instantiateViewController(withIdentifier: "rootController") as! RootViewController
        self.present(moveController, animated: true, completion: nil)
    }
    
}

extension UIApplication {
    var statusBarView: UIView? {
        if responds(to: Selector(("statusBar"))) {
            return value(forKey: "statusBar") as? UIView
        }
        return nil
    }
}

class MovieFanPresenter: BasePresenter {
    
    override init() {
        super.init()
        model = [
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-0")),
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-1")),
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-2")),
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-3")),
//            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-4")),
//            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-5")),
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-6")),
            OnboardingSlide(titleText: "", bodyText: "", image: #imageLiteral(resourceName: "slide-7"))
        ]
        doneButtonColor = UIColor(red: 255/255, green: 78/255, blue: 73/255, alpha: 1)
        doneButtonTextColor = .white
        cellBackgroundColor = UtilFunctions.UIColorFromRGB(rgbValue: 0x88E1CD)
        textColor = .white
        titleFont = UIFont(name: "Hoefler text", size: 28)!
        bodyFont = UIFont(name: "Hoefler text", size: 20)!
    }
    
    override func style(cell: UICollectionViewCell, for page: Int) {
        super.style(cell: cell, for: page)
        guard let cell = cell as? OnboardingCell else { return }
        cell.doneButton.isHidden = true
    }
    
    override func visibilityChanged(for cell: UICollectionViewCell, at index: Int, amount: CGFloat) {
        guard let cell = cell as? OnboardingCell, index == pageCount - 1  else { return }
        cell.doneButtonBottomConstraint?.constant = 60 * amount
        cell.setNeedsLayout()
        cell.layoutIfNeeded()
    }
    
}

