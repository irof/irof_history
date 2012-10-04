keytool -exportcert -alias androiddebugkey -keystore ./irof.keystore | openssl sha1 -binary | openssl base64
