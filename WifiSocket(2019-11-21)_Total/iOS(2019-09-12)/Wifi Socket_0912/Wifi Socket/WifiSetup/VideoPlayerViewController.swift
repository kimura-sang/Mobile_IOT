//
//  VideoPlayerViewController.swift
//  Wifi Socket
//
//  Created by king on 2019/7/28.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit

class VideoPlayerViewController: UIViewController {
    
    var playerLayer = AVPlayerLayer()

    override func viewDidLoad() {
        super.viewDidLoad()

        playHelpVideo()
    }
    
    func playHelpVideo() {
        let screenSize: CGRect = UIScreen.main.bounds
        let screenWidth = screenSize.width
        let screenHeight = screenSize.height
        
        let path = Bundle.main.path(forResource: "WifiHelp", ofType:"mp4")
        let player = AVPlayer(url: URL(fileURLWithPath: path!))
        
        playerLayer = AVPlayerLayer(player: player)
        playerLayer.videoGravity = AVLayerVideoGravity.resizeAspectFill
        playerLayer.frame = CGRect(x: 0, y: (screenHeight - 720)/2, width: screenWidth, height: 720)
        self.view!.layer.addSublayer(playerLayer)
        player.play()
        
        NotificationCenter.default.addObserver(self, selector: #selector(playerDidFinishPlaying), name: NSNotification.Name.AVPlayerItemDidPlayToEndTime, object: playerLayer.player?.currentItem)
    }
    
    @objc func playerDidFinishPlaying(note: NSNotification) {
        playerLayer.removeFromSuperlayer()
        self.dismiss(animated: true, completion: nil)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
