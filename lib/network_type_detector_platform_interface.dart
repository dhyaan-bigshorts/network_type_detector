import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'network_type_detector_method_channel.dart';

abstract class NetworkTypeDetectorPlatform extends PlatformInterface {
  /// Constructs a NetworkTypeDetectorPlatform.
  NetworkTypeDetectorPlatform() : super(token: _token);

  static final Object _token = Object();

  static NetworkTypeDetectorPlatform _instance = MethodChannelNetworkTypeDetector();

  /// The default instance of [NetworkTypeDetectorPlatform] to use.
  ///
  /// Defaults to [MethodChannelNetworkTypeDetector].
  static NetworkTypeDetectorPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [NetworkTypeDetectorPlatform] when
  /// they register themselves.
  static set instance(NetworkTypeDetectorPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
