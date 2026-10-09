package com.example.vorratsheld.platform

import platform.UIKit.UIDevice

actual val platformName: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
