//
//  OnboardingViewController.swift
//  Ahoy ( https://github.com/xmartlabs/Ahoy)
//
//  Copyright (c) 2017 Xmartlabs ( http://xmartlabs.com )
//
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

import Foundation
import UIKit

open class OnboardingViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource {

    public var presenter: OnboardingPresenter = BasePresenter()
    public var pageControlBottomConstant: CGFloat = 10.0

    public var currentPage = 0 {
        didSet {
            if isViewLoaded {
                pageChanged(to: currentPage)
            }
        }
    }

    @IBOutlet public weak var collectionView: UICollectionView?
    @IBOutlet public weak var pageControl: UIPageControl?
    @IBOutlet public weak var skipButton: UIButton?

    override open var prefersStatusBarHidden: Bool {
        return true
    }

    override open func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        setupCollectionView()
        setupPageControl()
        setupSkipButton()
    }

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }

    override open func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        let layout = collectionView?.collectionViewLayout as? UICollectionViewFlowLayout
        layout?.itemSize = collectionView?.bounds.size ?? UIScreen.main.bounds.size
        
        let modelName = UIDevice.modelName
        if modelName.contains("iPhone X") {
            layout?.itemSize = CGSize(width: (layout?.itemSize.width)!, height: ((layout?.itemSize.height)! - 68))
        }
//        if #available(iOS 11.0, *) {
//            print(collectionView?.safeAreaInsets as Any)
//            
//            let window = UIApplication.shared.keyWindow
//            let topPadding = window?.safeAreaInsets.top
//            let bottomPadding = window?.safeAreaInsets.bottom
//            
//            collectionView!.contentInset = UIEdgeInsets(top: 88, left: 0, bottom: 68, right: 0)
//        } else {
//            // Fallback on earlier versions
//        }
//        print(collectionView?.bounds.size)
//        print(UIScreen.main.bounds.size)
        layout?.minimumInteritemSpacing = 0
        layout?.minimumLineSpacing = 0
    }

    open func setupCollectionView() {
        if collectionView == nil {
            createCollectionView()
        }

        collectionView?.dataSource = self
        collectionView?.delegate = self

        presenter.cellProviders.values.forEach(register(with:))
        
        register(with: presenter.defaultProvider)

        presenter.style(collection: collectionView)
    }

    open func setupPageControl() {
        if pageControl == nil {
            createPageControl()
        }

        presenter.style(pageControl: pageControl)
    }

    open func setupSkipButton() {
        presenter.style(skip: skipButton)
        skipButton?.addTarget(self, action: #selector(skipPressed), for: .touchUpInside)
    }

    @objc func skipPressed() {
        guard let skipAction = presenter.onOnboardingSkipped else {
            return
        }

        skipAction()
    }

    open func skipOnboarding() {
        presenter.onOnboardingSkipped?()
    }

    open func pageChanged(to page: Int) {
        pageControl?.currentPage = min(page, presenter.pageCount - 1)
    }

    open func createCollectionView() {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        let collection: UICollectionView = {
            let collection = UICollectionView(frame: UIScreen.main.bounds, collectionViewLayout: layout)
            collection.backgroundColor = .clear
            collection.isPagingEnabled = true
            collection.bounces = false
            collection.translatesAutoresizingMaskIntoConstraints = false
            collection.showsHorizontalScrollIndicator = false
            return collection
        }()
        let views: [String: Any] = ["collectionView": collection]
        view.addSubview(collection)
        view.sendSubviewToBack(collection)
        view.addConstraints(NSLayoutConstraint.constraints(withVisualFormat: "H:|[collectionView]|", options: [], metrics: nil, views: views))
        view.addConstraints(NSLayoutConstraint.constraints(withVisualFormat: "V:|[collectionView]|", options: [], metrics: nil, views: views))
        collectionView = collection
    }

    open func createPageControl() {
        let pageControl: UIPageControl = {
            let frame = CGRect(x: 0, y: 0, width: 100, height: 30)
            let control = UIPageControl(frame: frame)
            control.translatesAutoresizingMaskIntoConstraints = false
            return control
        }()
        view.addSubview(pageControl)
        view.addConstraint(NSLayoutConstraint(item: view, attribute: .centerX, relatedBy: .equal, toItem: pageControl, attribute: .centerX, multiplier: 1.0, constant: 0.0))
        
        let modelName = UIDevice.modelName
        if modelName.contains("iPhone X") {
            pageControlBottomConstant = 44
        }
        
        view.addConstraint(NSLayoutConstraint(item: view, attribute: .bottom, relatedBy: .equal, toItem: pageControl, attribute: .bottom, multiplier: 1.0, constant: pageControlBottomConstant))
        view.bringSubviewToFront(pageControl)
        self.pageControl = pageControl
    }

    open func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        guard let collection = collectionView else { return }
        let page = Int(collection.contentOffset.x / collection.bounds.width)
        currentPage = page
    }

    open func scrollViewDidScroll(_ scrollView: UIScrollView) {
        guard let collection = collectionView else { return }
        collection.visibleCells.forEach {
            let width = self.view.convert($0.frame, from: self.collectionView).intersection(collection.frame).width
            let amountVisible = width / collection.bounds.width
            guard let index = collection.indexPath(for: $0)?.row else { return }
            presenter.visibilityChanged(for: $0, at: index, amount: amountVisible)
        }
    }

    open func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return presenter.pageCount
    }

    open func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let reuseIdentifier = presenter.reuseIdentifier(for: indexPath.row)
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier:  reuseIdentifier, for: indexPath)
        presenter.style(cell: cell, for: indexPath.row)
        return cell
    }

    private func register(with provider: CellProvider?) {
        guard let provider = provider else { return }
        switch provider {
        case .nib(let name, let identifier, let bundle):
            collectionView?.register(UINib(nibName: name, bundle: bundle), forCellWithReuseIdentifier: identifier)
        case .cellClass(let className, let identifier):
            collectionView?.register(className, forCellWithReuseIdentifier: identifier)
        }
    }

}

extension UIDevice {
    
    static let modelName: String = {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value)))
        }
        
        func mapToDevice(identifier: String) -> String { // swiftlint:disable:this cyclomatic_complexity
            #if os(iOS)
            switch identifier {
            case "iPod5,1":                                 return "iPod Touch 5"
            case "iPod7,1":                                 return "iPod Touch 6"
            case "iPhone3,1", "iPhone3,2", "iPhone3,3":     return "iPhone 4"
            case "iPhone4,1":                               return "iPhone 4s"
            case "iPhone5,1", "iPhone5,2":                  return "iPhone 5"
            case "iPhone5,3", "iPhone5,4":                  return "iPhone 5c"
            case "iPhone6,1", "iPhone6,2":                  return "iPhone 5s"
            case "iPhone7,2":                               return "iPhone 6"
            case "iPhone7,1":                               return "iPhone 6 Plus"
            case "iPhone8,1":                               return "iPhone 6s"
            case "iPhone8,2":                               return "iPhone 6s Plus"
            case "iPhone9,1", "iPhone9,3":                  return "iPhone 7"
            case "iPhone9,2", "iPhone9,4":                  return "iPhone 7 Plus"
            case "iPhone8,4":                               return "iPhone SE"
            case "iPhone10,1", "iPhone10,4":                return "iPhone 8"
            case "iPhone10,2", "iPhone10,5":                return "iPhone 8 Plus"
            case "iPhone10,3", "iPhone10,6":                return "iPhone X"
            case "iPhone11,2":                              return "iPhone XS"
            case "iPhone11,4", "iPhone11,6":                return "iPhone XS Max"
            case "iPhone11,8":                              return "iPhone XR"
            case "iPad2,1", "iPad2,2", "iPad2,3", "iPad2,4":return "iPad 2"
            case "iPad3,1", "iPad3,2", "iPad3,3":           return "iPad 3"
            case "iPad3,4", "iPad3,5", "iPad3,6":           return "iPad 4"
            case "iPad4,1", "iPad4,2", "iPad4,3":           return "iPad Air"
            case "iPad5,3", "iPad5,4":                      return "iPad Air 2"
            case "iPad6,11", "iPad6,12":                    return "iPad 5"
            case "iPad7,5", "iPad7,6":                      return "iPad 6"
            case "iPad11,4", "iPad11,5":                    return "iPad Air (3rd generation)"
            case "iPad2,5", "iPad2,6", "iPad2,7":           return "iPad Mini"
            case "iPad4,4", "iPad4,5", "iPad4,6":           return "iPad Mini 2"
            case "iPad4,7", "iPad4,8", "iPad4,9":           return "iPad Mini 3"
            case "iPad5,1", "iPad5,2":                      return "iPad Mini 4"
            case "iPad11,1", "iPad11,2":                    return "iPad Mini 5"
            case "iPad6,3", "iPad6,4":                      return "iPad Pro (9.7-inch)"
            case "iPad6,7", "iPad6,8":                      return "iPad Pro (12.9-inch)"
            case "iPad7,1", "iPad7,2":                      return "iPad Pro (12.9-inch) (2nd generation)"
            case "iPad7,3", "iPad7,4":                      return "iPad Pro (10.5-inch)"
            case "iPad8,1", "iPad8,2", "iPad8,3", "iPad8,4":return "iPad Pro (11-inch)"
            case "iPad8,5", "iPad8,6", "iPad8,7", "iPad8,8":return "iPad Pro (12.9-inch) (3rd generation)"
            case "AppleTV5,3":                              return "Apple TV"
            case "AppleTV6,2":                              return "Apple TV 4K"
            case "AudioAccessory1,1":                       return "HomePod"
            case "i386", "x86_64":                          return "Simulator \(mapToDevice(identifier: ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "iOS"))"
            default:                                        return identifier
            }
            #elseif os(tvOS)
            switch identifier {
            case "AppleTV5,3": return "Apple TV 4"
            case "AppleTV6,2": return "Apple TV 4K"
            case "i386", "x86_64": return "Simulator \(mapToDevice(identifier: ProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"] ?? "tvOS"))"
            default: return identifier
            }
            #endif
        }
        
        return mapToDevice(identifier: identifier)
    }()
    
}
