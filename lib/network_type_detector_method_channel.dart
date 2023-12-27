import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'network_type_detector_platform_interface.dart';

/// An implementation of [NetworkTypeDetectorPlatform] that uses method channels.
class MethodChannelNetworkTypeDetector extends NetworkTypeDetectorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('network_type_detector');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
