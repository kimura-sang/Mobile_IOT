//
//  ImageCollectionViewCell.swift
//  Light Bulb
//
//  Created by king on 2019/9/12.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

protocol SelectDeviceImageDelegate {
    func selectImage(imageId: Int)
}

class ImageCollectionViewCell: UICollectionViewCell {
    
    @IBOutlet weak var btnImageDevice: UIButton!
    
    var selectDelegate: SelectDeviceImageDelegate!
    var imageId: Int!
    
    @IBAction func clickImageDevice(_ sender: Any) {
        selectDelegate.selectImage(imageId: imageId!)
    }
}
