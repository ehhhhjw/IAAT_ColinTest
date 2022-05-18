adb logcat -c # clear buffer
## select the application pid and ActivityManager , ActivityThread
adb -s %1 logcat -v long | grep -A 2 -e `adb shell ps | grep %2 | cut -c10-15` -e ActivityManager -e ActivityThread > %3
