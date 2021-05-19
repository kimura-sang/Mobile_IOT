//
//  Global.swift
//  Light Bulb
//
//  Created by king on 2019/9/3.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation


// ------------------------
// --- Global Constants ---
// ------------------------

public let __DEBUG_MODE = false

public var __FIRST_LOAD = 0
public var __LEFT_MENU_CONTROLLER: LeftMenuViewController!

public var __CB_CENTRAL_MANAGER: CBCentralManager!
public var __CB_CURRENT_PERIPHERAL: CBPeripheral!

public let __NN_BLE_DEVICE_SCANNED = NSNotification.Name(rawValue: "BLEScanned")
public let __NN_BLE_DEVICE_CONNECTED = NSNotification.Name(rawValue: "BLEConnected")
public let __NN_BLE_DEVICE_DISCONNECTED = NSNotification.Name(rawValue: "BLEDisConnected")

public let __NN_LOCAL_DEVICE = NSNotification.Name(rawValue: "EditLocalDevice")
public let __NN_CHANGE_DEVICE_STATUS = NSNotification.Name(rawValue: "ChangeDeviceStatus")
public let __NN_CHANGE_GROUP_STATUS = NSNotification.Name(rawValue: "ChangeGroupStatus")
public let __NN_CHANGE_TIMER_STATUS = NSNotification.Name(rawValue: "ChangeTimerStatus")
public let __NN_DEVICE_OFFLINE_STATUS = NSNotification.Name(rawValue: "DeviceOfflineStatus")


// ---- for every user
public var __USER_DEVICE_DATA: Dictionary = [String: Any]()

// ---- for not connected devices - cannot save in UserDefault data
public var __CACHE_DEVICE_ENTITIES: [CBPeripheral] = []

// ---- already connected data - cannot save in UserDefault data
public var __DEVICE_ENTITIES: [CBPeripheral] = []

// ---- for display on "DEVICE" page and saved in __USER_DEVICE_DATA
public var __DEVICES: [DeviceModel] = []

// ---- for display on "GROUP" page and saved in __USER_DEVICE_DATA
public var __GROUPS: [GroupModel] = []

// ---- for display on "TIMER" page and saved in __USER_DEVICE_DATA
public var __TIMERS: [TimerModel] = []

public var __STR_DEVICE_DATA = "UserDefaultDeviceData"

public let __RECORD_AUDIO_FILE = "5018.wav"
