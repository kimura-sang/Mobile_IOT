//
//  AppDelegate.swift
//  Light Bulb
//
//  Created by king on 2019/9/3.
//  Copyright © 2019 king. All rights reserved.
//

import UIKit
import CoreData
import IQKeyboardManagerSwift

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        UtilFunctions.getDeviceDataFromUserDefaults()
        
        __CB_CENTRAL_MANAGER = CBCentralManager(delegate: self, queue: nil)
        
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.shouldResignOnTouchOutside = true
        IQKeyboardManager.shared.shouldShowToolbarPlaceholder = true
        
        // UtilFunctions.removeUserData()
        
        let isLoadedOnce = UserDefaults.standard.bool(forKey: "isLoadedOnce")
        if isLoadedOnce {
            moveNextPage(pageName: "rootController")
        } else {
            UserDefaults.standard.set(true, forKey: "isLoadedOnce")
            UserDefaults.standard.synchronize()
        }
        
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        self.saveContext()
    }
    
    // MARK: - Core Data stack
    
    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
         */
        let container = NSPersistentContainer(name: "Light_Bulb")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()
    
    // MARK: - Core Data Saving support
    
    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }
    
    
    // my code
    func moveNextPage(pageName: String) {
        self.window = UIWindow(frame: UIScreen.main.bounds)
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        
        var initialViewController: UIViewController
        initialViewController = storyboard.instantiateViewController(withIdentifier: pageName)
        
        self.window?.rootViewController = initialViewController
        self.window?.makeKeyAndVisible()
    }
    
    func timerRunning() {
        Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { time in
            if __TIMERS.count > 0 {
                var removeArray = [Int]()
                for i in 0...(__TIMERS.count - 1) {
                    let timestamp = Int64(Date().timeIntervalSince1970)
                    if (timestamp - __TIMERS[i].startDateTimeInt) > (__TIMERS[i].minute * 60 + __TIMERS[i].second) {
                        removeArray.append(i)
                    }
                }
                
                if removeArray.count > 0 {
                    for i in (removeArray.count - 1)...0 {
                        UtilFunctions.setDeviceStatus(uuid: __TIMERS[i].deviceUUID, status: __TIMERS[i].isOpen, needSave: false)
                        __TIMERS.remove(at: i)
                    }
                }
                
                UtilFunctions.syncUserData()
                NotificationCenter.default.post(name: __NN_CHANGE_TIMER_STATUS, object: nil)
                if removeArray.count > 0 {
                    NotificationCenter.default.post(name: __NN_CHANGE_DEVICE_STATUS, object: nil)
                }
            }
        }
    }
}
