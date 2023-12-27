// You have generated a new plugin project without specifying the `--platforms`
// flag. A plugin project with no platform support was generated. To add a
// platform, run `flutter create -t plugin --platforms <platforms> .` under the
// same directory. You can also find a detailed instruction on how to add
// platforms in the `pubspec.yaml` at
// https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.
export 'src/network_status.dart';
import 'package:network_type_detector/network_type_detector.dart';
import 'package:network_type_detector/src/network_type_detector_platform_interface.dart';

class NetworkTypeDetector {
  Stream<NetworkStatus> get onNetworkStateChanged {
    return NetworkTypeDetectorPlatform.instance.onNetworkStateChanged;
  }

  Future<NetworkStatus> currentNetworkStatus() async {
    return NetworkTypeDetectorPlatform.instance.currentNetworkStatus();
  }
}
