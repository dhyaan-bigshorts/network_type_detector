import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:network_type_detector/src/network_status.dart';

import 'network_type_detector_platform_interface.dart';

/// An implementation of [NetworkTypeDetectorPlatform] that uses method channels.
class MethodChannelNetworkTypeDetector extends NetworkTypeDetectorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('network_type_detector');

  @visibleForTesting
  final eventChannel = const EventChannel('network_type_detector_status');

  Stream<NetworkStatus>? _onNetworkStateChanged;

  @override
  Stream<NetworkStatus> get onNetworkStateChanged {
    _onNetworkStateChanged ??= eventChannel
        .receiveBroadcastStream()
        .map((event) => event.toString())
        .map(_convertFromState);
    return _onNetworkStateChanged!;
  }

  @override
  Future<NetworkStatus> currentNetworkStatus() async {
    final String state = await methodChannel.invokeMethod("networkStatus");
    return _convertFromState(state);
  }

  /// NetworkStatus identify type of connection
  NetworkStatus _convertFromState(String state) {
    switch (state) {
      case "UNREACHABLE":
        return NetworkStatus.unreachable;
      case "MOBILE_2G":
        return NetworkStatus.mobile2G;
      case "MOBILE_3G":
        return NetworkStatus.mobile3G;
      case "WIFI":
        return NetworkStatus.wifi;
      case "MOBILE_4G":
        return NetworkStatus.mobile4G;
      case "MOBILE_5G":
        return NetworkStatus.mobile5G;
      case "MOBILE_OTHER":
        return NetworkStatus.otherMobile;
      default:
        return NetworkStatus.unreachable;
    }
  }
}
