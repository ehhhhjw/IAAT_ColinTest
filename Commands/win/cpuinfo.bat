adb -s %1 shell dumpsys cpuinfo | grep %2 | cut -d ' ' -f 3

