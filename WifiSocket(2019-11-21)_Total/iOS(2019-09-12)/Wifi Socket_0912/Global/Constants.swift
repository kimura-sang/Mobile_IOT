//
//  Global.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation


// ------------------------
// --- Global Constants ---
// ------------------------

public let __DEBUG_MODE = false
public let __DEBUG_SHOW_SERVER_TIME = true

public var __FIRST_LOAD = 0
public var __LEFT_MENU_CONTROLLER: LeftMenuViewController!

public let __NN_LOCAL_DEVICE = NSNotification.Name(rawValue: "EditLocalDevice")
public let __NN_CHANGE_DEVICE_STATUS = NSNotification.Name(rawValue: "ChangeDeviceStatus")
public let __NN_CHANGE_GROUP_STATUS = NSNotification.Name(rawValue: "ChangeGroupStatus")
public let __NN_CHANGE_SCHEDULE_STATUS = NSNotification.Name(rawValue: "ChangeScheduleStatus")
public let __NN_DEVICE_OFFLINE_STATUS = NSNotification.Name(rawValue: "DeviceOfflineStatus")


public var __USER_ID = ""

// ---- for every user
public var __USER_DEVICE_DATA: Dictionary = [String: Any]()

// ---- for dictionary data of Real Device Entity and saved in __USER_DEVICE_DATA
public var __CACHE_DEVICE_ENTITIES: [[AnyHashable: Any]] = []

// ---- Real Device Entity for running device and don't saved
public var __REAL_DEVICE_ENTITIES: [DeviceEntity] = []

// ---- not yet connected Real Device Entity, dictionary class and saved in UserDefaults as "deviceEntities"
public var __DEVICE_ENTITIES: [[String: Any]] = []

// ---- for display on "DEVICE" page and saved in __USER_DEVICE_DATA
public var __DEVICES: [DeviceModel] = []

// ---- for display on "GROUP" page and saved in __USER_DEVICE_DATA
public var __GROUPS: [GroupModel] = []

// ---- for display on "SCHEDULE" page and saved in __USER_DEVICE_DATA
public var __SCHEDULES: [ScheduleModel] = []



// -----------------------
// --- XLink Variables ---
// -----------------------

public let __CORP_ID = "1007d2ada2c78c04"     // real
public let __PRODUCT_LEXIN_SOCKET = "160fa2afb49eaa00160fa2afb49eaa01"
public let __XLINK_ACCOUNT = "18129679136"
public let __XLINK_PASSWORD = "66666666"

public let __CONFIG_RESOURCE = ""
public let __CONFIG_API_SERVER = "https://api2.xlink.cn"
public let __CONFIG_CLOUD_SERVER = "mqtt.xlink.cn"
public let __CLOUD_SERVER_PORT = "1884"
public let __CONFIG_SSL = true

public let __MOBILE_PHONE = "18129679136"
public let __DEVICE_PASSWORD = 8888

public let __BIND_HOST = "255.255.255.255"
public let __BIND_PORT = 5876


public let __URL_DOMAIN = "https://api2.xlink.cn:443"
public let __URL_AUTH_USER = "/v2/user_auth"
public let __URL_REGISTER_USER = "/v2/user_register"
public let __URL_RESET_PASSWORD = "/v2/user/password/forgot"

public let __URL_GET_PRODUCT_DATA = "/v2/product/%@/datapoints"



// -------------
// --- Bytes ---
// -------------

public let __BYTE_LENGTH_40 = 40
public let __BYTE_LENGTH_60 = 60

public let __BYTE_SEND_COMMAND: UInt8 = 2
public let __BYTE_SEND_SCHEDULE: UInt8 = 3
public let __BYTE_SEND_REFRESH: UInt8 = 12

public let __BYTE_SCHEDULE_ADD: UInt8 = 1
public let __BYTE_SCHEDULE_CHANGE: UInt8 = 2
public let __BYTE_SCHEDULE_DELETE: UInt8 = 3
public let __BYTE_SCHEDULE_TEST: UInt8 = 5

public let __BYTE_LEXIN_OUTLETS: [UInt8] = [0, 25]
public let __BYTE_LEXIN_SCAN_WIFI_DEVICE: [UInt8] = [1, 119, 98, 105, 110, 100]



// -----------------------------
// --- Timezone DB API & Key ---
// -----------------------------

public let __NN_TIME_ZONE_DB_KEY = "5I8XS1Q0HMCW"
public let __NN_TIME_ZONE_DB_API = "http://api.timezonedb.com/v2.1/convert-time-zone"
public let __NN_TIME_ZONE_ALL_DB_API = "http://api.timezonedb.com/v2.1/list-time-zone"

public let __NN_CHINA_SHANGHAI_TIME_ZONO_IDENTIFIER = "Asia/Shanghai"
public var __NN_CURRENT_TIME_ZONE: String = ""
public var __NN_IS_DST: Bool = false
