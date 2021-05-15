package com.test.librarytimer


const val START_TIME = "startTime"
const val IS_TIMER_RUNNING = "isTimerRunning"
const val SCAN_DETAIL = "scan_details"

//SharedPreference name
const val PREF_NAME = "prefs"

const val TIME_FORMAT = "HH:mm:ss"
const val TIMEZONE_GMT = "GMT"

//Toast messages
const val SCAN_CORRECT_BARCODE = "Please scan the correct barcode!"
const val UNKNOWN_ERROR_OCCURRED = "Some unknown error occurred!"
const val RESULT_NOT_FOUND = "Result Not Found"

//ViewFlippers State
const val WELCOME_VIEW = 0
const val ENTRY_DETAIL_VIEW = 1
const val EXIT_DETAIL_VIEW = 1

//Service Actions
const val START_FOREGROUND_ACTION = "START_FOREGROUND_ACTION"
const val STOP_FOREGROUND_ACTION = "STOP_FOREGROUND_ACTION"

//Notification
const val NOTIFICATION_ID = 22
const val NOTIFICATION_CHANNEL_NAME = "Timer Channel"
const val NOTIFICATION_CHANNEL_ID: String = "NOTIF_ID"