adb -s $1 logcat -c # clear buffer
## select the application pid and ActivityManager , ActivityThread
adb -s $1 logcat -v long | grep -A 2 -e $2 -e ActivityManager -e ActivityThread > $3
