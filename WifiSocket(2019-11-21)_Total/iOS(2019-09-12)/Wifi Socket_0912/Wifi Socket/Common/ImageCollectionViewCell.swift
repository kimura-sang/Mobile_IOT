//
//  ImageCollectionViewCell.swift
//  Wifi Socket
//
//  Created by king on 2019/6/12.
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
