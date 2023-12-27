import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:network_type_detector/network_type_detector.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _networkStatus = 'Unknown';
  String _networkStatusStream = 'Unknown';
  late final StreamSubscription _networkStatusSubscription;
  final _networkTypeDetectorPlugin = NetworkTypeDetector();

  @override
  void initState() {
    super.initState();
    initPlatformState();
    _networkStatusSubscription = _networkTypeDetectorPlugin.onNetworkStateChanged.listen((event) {
      setState(() {
        _networkStatusStream = event.toString();
      });
    });
  }

  @override
  void dispose() {
    _networkStatusSubscription.cancel();
    super.dispose();
  }

  Future<void> initPlatformState() async {
    String status;
    try {
      status =
          (await _networkTypeDetectorPlugin.currentNetworkStatus()).toString();
    } on PlatformException {
      status = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _networkStatus = status;
    });
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_networkStatus\n'),
              Text('Running on Change: $_networkStatusStream'),
            ],
          ),
        ),
      ),
    );
  }
}
