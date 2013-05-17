pkgname=com.irof.irof_history
activity=.IrofActivity


if [ $# -eq 2 ]; then
adb -s $2 uninstall $pkgname
adb -s $2 install $1
adb -s $2 shell am start -a android.intent.action.MAIN -n $pkgname/$activity
else
adb uninstall jp.tekunodo.ttn
adb install $1
adb shell am start -a android.intent.action.MAIN -n $pkgname/$activity
fi
exit 0
