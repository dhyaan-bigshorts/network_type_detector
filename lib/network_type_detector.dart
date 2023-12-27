export 'src/network_status.dart';
import 'package:network_type_detector/network_type_detector.dart';
import 'package:network_type_detector/src/network_type_detector_platform_interface.dart';

/// The `NetworkTypeDetector` class provides methods to get the current network status and listen for network status changes.
class NetworkTypeDetector {

  /// This getter returns a Stream that emits NetworkStatus events whenever the network status changes.
  Stream<NetworkStatus> get onNetworkStateChanged {
    return NetworkTypeDetectorPlatform.instance.onNetworkStateChanged;
  }
  
  /// This method returns a Future that completes with the current NetworkStatus.
  Future<NetworkStatus> currentNetworkStatus() async {
    return NetworkTypeDetectorPlatform.instance.currentNetworkStatus();
  }
}
