export JAVA_OPTS='-Dfile.encoding=UTF-8 -Dgroovy.source.encoding=UTF-8'
export _JAVA_OPTIONS='-Dfile.encoding=UTF-8 -Dgroovy.source.encoding=UTF-8'

# apk署名キー作成用
keytool -genkey -v -keyalg RSA -keystore ./irof.keystore -alias irofworld -validity 10000
