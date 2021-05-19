# Uncomment the next line to define a global platform for your project
platform :ios, '9.0'
use_frameworks!

target 'Wifi Socket' do
  # Comment the next line if you don't want to use dynamic frameworks
  use_frameworks!

  # Pods for Wifi Socket
  pod 'QMUIKit'
  pod 'EspTouch'
  pod 'Alamofire'
  pod 'AKSideMenu'
  pod 'Ahoy'
  pod 'SkyFloatingLabelTextField'
  pod 'IQKeyboardManagerSwift'
  pod 'WWCalendarTimeSelector'
  pod 'CocoaAsyncSocket' 
  pod 'Alamofire'

  target 'Wifi SocketTests' do
    inherit! :search_paths
    # Pods for testing
  end

  target 'Wifi SocketUITests' do
    inherit! :search_paths
    # Pods for testing
  end

  post_install do |installer|
  installer.pods_project.targets.each do |target|
    target.build_configurations.each do |config|
      config.build_settings['ENABLE_BITCODE'] = 'NO'
      config.build_settings['ARCHS'] = 'arm64'
    end
  end
end


end
