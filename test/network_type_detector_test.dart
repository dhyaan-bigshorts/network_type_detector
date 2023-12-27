import 'package:flutter_test/flutter_test.dart';
import 'package:network_type_detector/network_type_detector.dart';
import 'package:network_type_detector/network_type_detector_platform_interface.dart';
import 'package:network_type_detector/network_type_detector_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNetworkTypeDetectorPlatform
    with MockPlatformInterfaceMixin
    implements NetworkTypeDetectorPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final NetworkTypeDetectorPlatform initialPlatform = NetworkTypeDetectorPlatform.instance;

  test('$MethodChannelNetworkTypeDetector is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNetworkTypeDetector>());
  });

  test('getPlatformVersion', () async {
    NetworkTypeDetector networkTypeDetectorPlugin = NetworkTypeDetector();
    MockNetworkTypeDetectorPlatform fakePlatform = MockNetworkTypeDetectorPlatform();
    NetworkTypeDetectorPlatform.instance = fakePlatform;

    expect(await networkTypeDetectorPlugin.getPlatformVersion(), '42');
  });
}
