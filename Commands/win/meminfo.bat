adb -s %1 shell dumpsys meminfo | grep %2 | cut -d ' ' -f 5
