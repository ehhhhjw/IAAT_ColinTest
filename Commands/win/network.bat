adb -s %1 shell cat /proc/net/xt_qtaguid/stats | grep `adb -s %1 shell dumpsys package %2 | grep userId= | cut -d ' ' -f 5 | cut -d '=' -f 2` | cut -d ' ' -f 6

