package com.tatsuyuki25.network_type_detector

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/*
 * Unit test for the Kotlin portion of the plugin's implementation.
 */

internal class NetworkTypeDetectorPluginTest {

  private lateinit var mockResult: MethodChannel.Result

  @BeforeTest
  fun setup() {
    MockitoAnnotations.openMocks(this) // Initializes mocks before tests
    mockResult = Mockito.mock(MethodChannel.Result::class.java) // Mock MethodChannel.Result
  }

  @Test
  fun onMethodCall_getPlatformVersion_returnsExpectedValue() {
    val plugin = NetworkTypeDetectorPlugin() // Create an instance of your plugin

    // Simulate a method call for "getPlatformVersion"
    val call = MethodCall("getPlatformVersion", null)

    // Call the method in the plugin
    plugin.onMethodCall(call, mockResult)

    // Capture and verify the success method call
    val captor = ArgumentCaptor.forClass(String::class.java)
    Mockito.verify(mockResult).success(captor.capture())

    // Assert the captured result (Android version in this case)
    assertEquals("Android " + android.os.Build.VERSION.RELEASE, captor.value)
  }
}
