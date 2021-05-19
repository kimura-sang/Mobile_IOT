//
//  ImageDialogViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

protocol SelectImageDelegate {
    func selectImageId(imageId: Int)
}

class ImageDialogViewController: UIViewController {

    @IBOutlet weak var btnCancel: UIButton!
    
    var selectImageDelegate: SelectImageDelegate!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.view.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        btnCancel.layer.cornerRadius = 2
        
        showAnimate()
    }
    
    @IBAction func clickCancel(_ sender: Any) {
        self.removeAnimate()
    }
    
    func showAnimate() {
        self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
        self.view.alpha = 0.0
        UIView.animate(withDuration: 0.25, animations: {
            self.view.alpha = 1.0
            self.view.transform = CGAffineTransform(scaleX: 1.0, y: 1.0)
        })
    }
    
    func removeAnimate() {
        UIView.animate(withDuration: 0.25, animations: {
            self.view.transform = CGAffineTransform(scaleX: 1.3, y: 1.3)
            self.view.alpha = 0.0
        }, completion: { (finished: Bool) in
            if (finished) {
                self.view.removeFromSuperview()
            }
        })
    }

}

extension ImageDialogViewController: UICollectionViewDelegate, UICollectionViewDataSource, SelectDeviceImageDelegate {
    
    func selectImage(imageId: Int) {
        self.removeAnimate()
        selectImageDelegate.selectImageId(imageId: (imageId) + 1)
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 135
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cellId", for: indexPath) as! ImageCollectionViewCell
        let imageName = "image_" + String(indexPath.row + 1)
        cell.imageId = indexPath.row
        cell.btnImageDevice.setImage(UIImage(named: imageName), for: .normal)
        cell.selectDelegate = self
        return cell
    }
}
