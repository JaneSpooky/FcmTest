package com.example.fcmtest

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val toToken = "ec36Z2ZvS1-3DHGGT5y0lh:APA91bHzjUvMUXrJjXlhAa4NLztm78tRaOMTBRHpiopY49clzIbBbSO_OpsWKBMs4dy-1sjYRacYxhnxfEbEa-QHJdUNutk29yMYC1bGdBqqiSzOK2MVOr7HKNvuYnfB1zadw1EUVp-u"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFcmTokenButton.setOnClickListener {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener {
                    val token = it.result?.token
                    logTextView.text = token
                    copy(token)
                }
        }

        sendFcmButton.setOnClickListener {
            sendFcm()
        }
    }

    private fun copy(text: String?) {
        if (text == null)
            return
        val clipboardManager: ClipboardManager =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", text))
    }

    private fun sendFcm() {
        val json = Gson().toJson(FcmRequest().apply {
            to = toToken
        })
        Timber.d("json:\n$json")
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .addHeader("Authorization", "key=AAAAYoCkPaY:APA91bFAxP61YE38Sc33dA8uVCzmU0FtIKzNdTjk5LUXkhPkQneTFGjKHpcqlQyaXHPfFkcE7SVVNo0pDZms4fmfIT7reBFopEnPimgvz4229howecP8kmDj2pMfE4UMXXIh0x3XUN-O")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
            }
        })
    }

    class FcmRequest {
        var notification = Notification()

        var to = ""

        class Notification {
            var title = "こんにちは"
            var body = "Hello"
        }
    }
}
