package com.example.eventflow.extensions

import java.util.Locale


fun Double.round(): String =
    String.format(Locale.US, "%.2f", this)

