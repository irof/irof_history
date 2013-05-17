# fbbrowser でチェックする用
keytool -exportcert -alias irofworld -keystore ./irof.keystore | openssl sha1 -binary | openssl base64
