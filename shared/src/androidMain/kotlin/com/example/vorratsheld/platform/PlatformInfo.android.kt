package com.example.vorratsheld.platform

import android.os.Build

actual val platformName: String = "Android ${Build.VERSION.RELEASE}"
