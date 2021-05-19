//
//  HttpRequest.swift
//  Wifi Socket
//
//  Created by king on 2019/6/3.
//  Copyright © 2019 king. All rights reserved.
//

import Foundation
import Alamofire

typealias FuncBlock = (_ result: Any?, _ error: Error?) -> Void

class CommonHttpRequest {
    
    var myBlock: FuncBlock? = nil
    
    // ------------------------------
    // --- Http Request functions ---
    // ------------------------------
    func requestWithRequestTypeWithResult(requestType: HTTPMethod, withUrl: String, withHeader: [String: String], withContent: [String: Any]?) {
        Alamofire.request(withUrl, method: requestType, parameters: withContent, encoding: JSONEncoding.default, headers: withHeader).responseJSON{response in
            let result = response.result
            let err = response.error
            switch (result) {
            case .success(_):
                if (self.myBlock != nil) {
                    self.myBlock!(result.value, err)
                }
                break
            case .failure(_):
                print("Error message:\(err!)")
                if (self.myBlock != nil) {
                    self.myBlock!(nil, err)
                }
            }
        }
    }
    
    func requestWithRequestType(requestType: HTTPMethod, withUrl: String, withHeader: [String: String], withContent: [String: Any]?) {
        Alamofire.request(withUrl, method: requestType, parameters: withContent, encoding: JSONEncoding.default, headers: withHeader).response{response in
            let code = response.response?.statusCode
            let err = response.error
            if code == 200 {
                if (self.myBlock != nil) {
                    self.myBlock!(["result": "success"], nil)
                }
            } else {
                print("Error message:\(err!)")
                if (self.myBlock != nil) {
                    self.myBlock!(nil, err)
                }
            }
        }
    }
    // ------------------------------
    
    static func changeTimeInChineseTimeZone(_ time: String, didFuncLoad: @escaping FuncBlock) {
        var url: String = __NN_TIME_ZONE_DB_API
        url += "?key=" + __NN_TIME_ZONE_DB_KEY
            + "&format=json"
            + "&from=" + __NN_CURRENT_TIME_ZONE
            + "&to=" + __NN_CHINA_SHANGHAI_TIME_ZONO_IDENTIFIER
            + "&time=" + time

        let header: HTTPHeaders = [ "Content-Type" : "application/json" ]

        let httpRequest = CommonHttpRequest()
        httpRequest.myBlock = didFuncLoad
        httpRequest.requestWithRequestTypeWithResult(requestType: HTTPMethod.get, withUrl: url, withHeader: header, withContent: nil)
    }
    
    static func getAllTimezone(didFuncLoad: @escaping FuncBlock) {
        var url: String = __NN_TIME_ZONE_ALL_DB_API
        url += "?key=" + __NN_TIME_ZONE_DB_KEY
            + "&format=json"
        
        let header: HTTPHeaders = [ "Content-Type" : "application/json" ]
        
        let httpRequest = CommonHttpRequest()
        httpRequest.myBlock = didFuncLoad
        httpRequest.requestWithRequestTypeWithResult(requestType: HTTPMethod.get, withUrl: url, withHeader: header, withContent: nil)
    }
    
}


