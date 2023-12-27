#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint network_type_detector.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'network_type_detector'
  s.version          = '0.0.1'
  s.summary          = 'This package allows your Flutter app to detect the current network type, supporting 2G, 3G, 4G, 5G, and Wi-Fi.'
  s.description      = <<-DESC
  This package allows your Flutter app to detect the current network type, supporting 2G, 3G, 4G, 5G, and Wi-Fi.
                       DESC
  s.homepage         = 'https://github.com/tatsuyuki25/network_type_detector'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'tatsuyuki' => 'jordandes@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'ReachabilitySwift'
  s.platform = :ios, '11.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
