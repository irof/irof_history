開発環境の構築

- android sdk のセットアップ
 - android update sdk
 - target:16(4.1)を入れてください
- antの設定
 - eclipse3.7系以降
 - eclipse3.6
 - コマンドライン
 
------
emuratorで動かす場合の設定



------
キーの設定、取得

 - setting/ad_key_mst.xml => res/values/ad_key.xmlをコピー作成してください(.gitignoreには上がらない設定になっています)
 - nakamapのキーを取得してください
  - http://developer.nakamap.com/
  - nakamap自体のユーザ登録が必要です（別途公式アプリ等が必要かも）
  - CreateAppで作成した瞬間にemail認証が飛びますが、Gmail等では迷惑メールになるようです。直ぐ認証しない場合は、あとから再認証できませんので早急の確認を御願いします
  
 - adMobのキーを取得してください
  - 現在、irofさんのサイト向けに、自社広告の設定をしています
  - [自社広告の設定](http://support.google.com/admob/bin/answer.py?hl=ja&answer=1619751&topic=1619748&ctx=topic)
  
------
ライブラリプロジェクト追加後の build.xmlの生成

- ライブラリプロジェクト  
```
android update project -p ./  
```
- メインプロジェクト  
```
android update project -p ./ -l ../NakamapSDK  
```
- テストプロジェクト  
```
android update test-project -m ../irof_history -p ./   
```

なイメージになります
