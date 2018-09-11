package mbaas.com.nifcloud.androidautologinapp

import android.os.Bundle
import android.provider.Settings.Secure
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBException
import com.nifcloud.mbaas.core.NCMBUser
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var _txtMessage: TextView
    lateinit var _txtLogin: TextView
    lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //**************** APIキーの設定 **************
        NCMB.initialize(applicationContext, "YOUR_APPLICATION_KEY", "YOUR_CLIENT_KEY")

        // UUIDを取得します
        uuid = Secure.getString(applicationContext.contentResolver,
                Secure.ANDROID_ID)
        Log.d(TAG, uuid)

        setContentView(R.layout.activity_main)
        _txtMessage = findViewById<TextView>(R.id.txtMessage)
        _txtLogin = findViewById<TextView>(R.id.txtLogin)

        //Login
        try {
            NCMBUser.loginInBackground(uuid, uuid) { login_user, e ->
                if (e != null) {
                    //エラー時の処理
                    Log.d(TAG, e.code)
                    if (e.code == "E401002") {

                        //NCMBUserのインスタンスを作成
                        val user = NCMBUser()
                        //ユーザ名を設定
                        user.userName = uuid
                        //パスワードを設定
                        user.setPassword(uuid)
                        //設定したユーザ名とパスワードで会員登録を行う
                        user.signUpInBackground { er ->
                            if (er != null) {
                                //会員登録時にエラーが発生した場合の処理
                                Log.d(TAG, "Signup error$er")
                            } else {
                                _txtMessage.text = "はじめまして"
                                _txtLogin.text = "１回目ログイン、ありがとうございます。"
                                //lastLoginを更新します（ラストログインのタイミングを取得するために）
                                try {
                                    val curUser = NCMBUser.getCurrentUser()
                                    val now = Date()
                                    curUser.put("lastLoginDate", now)
                                    curUser.save()
                                } catch (e1: NCMBException) {
                                    e1.printStackTrace()
                                }

                            }
                        }
                    }
                } else {
                    val curUser = NCMBUser.getCurrentUser()

                    val lastLogin = curUser.getDate("lastLoginDate")
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
        } catch (e: NCMBException) {
            e.printStackTrace()
        }

    }

    companion object {
        private val TAG = "MainActivity"
    }
}
