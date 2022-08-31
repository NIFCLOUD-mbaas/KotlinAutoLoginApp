# 【Android】自動ログイン機能を実装しよう！ for Kotlin
![画像1](readme-img/001.png)

## 概要
* [ニフクラ mobile backend](https://mbaas.nifcloud.com/) の『会員管理機能』を利用して、ゲームアプリによく見られる「自動ログイン機能」を実装したサンプルプロジェクトです
  - 「自動ログイン機能」とは、ユーザーが会員登録やログイン/ログアウトを意識することなく会員管理を行う機能です
  - 通常の「会員管理機能」を応用して実装することが可能です
* 簡単な操作ですぐに [ニフクラ mobile backend](https://mbaas.nifcloud.com/) の機能を体験いただけます

## ニフクラ mobile backendって何？？
スマートフォンアプリのバックエンド機能（プッシュ通知・データストア・会員管理・ファイルストア・SNS連携・位置情報検索・スクリプト）が**開発不要**、しかも基本**無料**(注1)で使えるクラウドサービス！

注1：詳しくは[こちら](https://mbaas.nifcloud.com/price.htm)をご覧ください

![画像2](readme-img/002.png)

## 動作環境

* MacOS Monterey version 12.5
* Android Studio Chipmunk | 2021.2.1 Patch 2
* Pixel 2 - Android 13 (Simulator)
* KotlinSDK v1.2.0

※上記内容で動作確認をしています。

## 手順
### 1. ニフクラ mobile backend の会員登録・ログインとアプリの新規作成
* 下記リンクから会員登録（無料）をします
  * https://console.mbaas.nifcloud.com/signup
* 登録ができたら下記リンクからログインします
  * https://console.mbaas.nifcloud.com/
* 下図のように「アプリの新規作成」画面が出るのでアプリを作成します
  * 既に mobile backend を利用したことがある方は左上の「＋新しいアプリ」をクリックすると同じ画面が表示されます

![画像3](readme-img/003.png)

* アプリ作成されるとAPIキー（アプリケーションキーとクライアントキー）が発行されます
* この２種類のAPIキーはこの後ダウンロードするサンプルアプリと ニフクラ mobile backend を紐付けるため、あとで使います

![画像4](readme-img/004.png)

### 2. サンプルプロジェクトのダウンロード
* 下記リンクをクリックしてプロジェクトをダウンロードします
 * https://github.com/NiFCloud-mbaas/AndroidAutoLoginApp/archive/master.zip
* ダウンロードしたプロジェクトを解凍します
* AndroidStudio を開きます、「Open an existing Android Studio projct」をクリックして解凍したプロジェクトを選択します

![画像8-1](readme-img/android_studio.png)

* 選択したプロジェクトが開かれます

![画像6](readme-img/006.png)

### 3. SDKの導入（実装済み）

※このサンプルアプリには既にSDKが実装済み（下記手順）となっています。（ver.1.2.0)<br>　最新版をご利用の場合は入れ替えてご利用ください。

* SDKダウンロード
SDKはここ（[SDK リリースページ](https://github.com/NIFCLOUD-mbaas/ncmb_kotlin/releases)）から取得してください.
  - NCMB.jarファイルがダウンロードします。
* SDKをインポート
  - app/libsフォルダにNCMB.jarをコピーします
* 設定追加
  - app/build.gradleファイルに以下を追加します
```gradle
dependencies {
    implementation 'com.squareup.okhttp3:okhttp:4.8.1'
    implementation 'com.google.code.gson:gson:2.3.1'
    api files('libs/NCMB.jar')

    //同期処理を使用する場合はこちらを追加していただく必要があります
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9'
}
```
  - androidManifestの設定
    - <application>タグの直前に以下のpermissionを追加します
```html
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 4. APIキーの設定
* AndroidStudio で MainActivity.kt を開きます
  * ディレクトリはデフォルトで「Android」が選択されていますので、「Project」に切り替えてから探してください

![画像14](/readme-img/014.png)

* APIキー（アプリケーションキーとクライアントキー）の設定をします

![画像7](readme-img/007.png)

* それぞれ`YOUR_NCMB_APPLICATION_KEY`と`YOUR_NCMB_CLIENT_KEY`の部分を書き換えます
   - このとき、ダブルクォーテーション（`"`）を消さないように注意してください！
* 書き換え終わったら保存してください
   * Windowsの場合、Ctrl + Sで保存できます。
   * Macの場合、command + Sで保存できます。

### 5. 動作確認と解説

* エミュレーターでアプリをビルドします
 * 失敗する場合は一度「Clean Project」を実行してから再度ビルドしてください
* アプリが起動します

#### 初回起動時
ユーザー側では特に操作をすることなく、裏では新規会員登録とログインが行われます

##### アプリ側
* 画面は次のようになります

![画像10](readme-img/first_login.png)

##### クラウド側
* [ニフクラ mobile backend](https://mbaas.nifcloud.com/) の管理画面を確認してみましょう
* 「会員管理」の中にユーザー登録がされていることが確認できます

![画像11](readme-img/011.png)

* [ニフクラ mobile backend](https://mbaas.nifcloud.com/) の「ユーザー名/パスワード」を使用して会員管理を行う機能を「自動ログイン機能」へ応用しています
 * ここでは、ユーザー名とパスワードとして、「端末ID（UDID）」を取得し起動時に認証を行うことで自動ログインを実現しています。
 * ダッシュボードの「userName」フィールドで登録された端末IDが確認できます。

#### ２回目以降起動時
初回起動時に端末IDで会員情報が登録されているため、２回目以降起動時はログインが行われます

* 上の「■」ボタンをクリックしてプログラムを停止します
* 再度、実行ボタン（さんかくの再生マーク）をクリックします

##### アプリ側
* 画面は次のようになります

![画像12](readme-img/second_login.png)

* [ニフクラ mobile backend](https://mbaas.nifcloud.com/) の会員機能では会員毎に、会員登録やログインを行うたびに更新される「lastLoginDate」というフィールドを持ち、最終ログイン日時を表示します

![画像13](readme-img/013.png)

* 何度か起動し、アプリ側とダッシュボード側を確認してみましょう

## 参考
ここではサンプルアプリに実装済みの内容について紹介します

### SDKのインポートと初期設定

ニフクラ mobile backend のドキュメント（クイックスタート）をご用意していますので、ご活用ください
* [Androidのクイックスタート](https://mbaas.nifcloud.com/doc/current/introduction/quickstart_android.html#/Android/)


### ロジック
* `activity_main.xml`でデザインを作成し、`MainActivity.kt`にロジックを書いています

#### 自動ログイン処理
```kotlin
// UUIDを取得します
uuid = Secure.getString(
    applicationContext.contentResolver,
    Secure.ANDROID_ID
)
Log.d(TAG, uuid)

setContentView(R.layout.activity_main)
_txtMessage = findViewById<TextView>(R.id.txtMessage)
_txtLogin = findViewById<TextView>(R.id.txtLogin)

//Login
try {
    var user = NCMBUser()
    user.loginInBackground(uuid, uuid, NCMBCallback { e, login_user ->
        if (e != null) {
            //エラー時の処理
            Log.d(TAG, e.code)
            if (e.code == "E401002") {
                //NCMBUserのインスタンスを作成
                val signupUser = NCMBUser()
                signupUser.signUpInBackground(uuid, uuid, NCMBCallback { er, signUpUser ->
                    runOnUiThread {
                        if (er != null) {
                            //会員登録時にエラーが発生した場合の処理
                            Log.d(TAG, "Signup error$er")
                        } else {
                            _txtMessage.text = "はじめまして"
                            _txtLogin.text = "１回目ログイン、ありがとうございます。"
                            //lastLoginを更新します（ラストログインのタイミングを取得するために）
                            try {
                                val curUser = NCMBUser.currentuser
                                val now = Date()
                                curUser!!.put("lastLoginDate", now)
                                curUser.save()
                            } catch (e1: NCMBException) {
                                e1.printStackTrace()
                            }

                        }
                    }

                })
            }
        } else {
            runOnUiThread {
                val curUser = NCMBUser.currentuser
                val lastLogin = curUser!!.getDate("lastLoginDate")
                val simpleDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val strDt = simpleDate.format(lastLogin)
                _txtMessage.text = "お帰りなさい！"
                _txtLogin.text = "最終ログインは：$strDt"

                //lastLoginを更新します（ラストログインのタイミングを取得するために）
                try {
                    val now = Date()
                    curUser.put("lastLoginDate", now)
                    curUser.save()
                } catch (e1: NCMBException) {
                    e1.printStackTrace()
                }
            }
        }
    })
} catch (e: NCMBException) {
    e.printStackTrace()
}
```
