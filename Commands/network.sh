adb -s $1 shell cat /proc/net/xt_qtaguid/stats | grep $2

