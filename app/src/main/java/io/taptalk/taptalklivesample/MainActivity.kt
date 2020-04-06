package io.taptalk.taptalklivesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.taptalklive.Listener.TapTalkLiveListener
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklivesample.BuildConfig.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isOpenTapTalkLiveViewPending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TapTalkLive.init(applicationContext,
                TAPLIVE_SDK_APP_KEY_SECRET,
                R.drawable.ic_taptalk_logo,
                "TapTalk.live Sample App",
                tapTalkLiveListener)

        ll_button_launch_live_chat.setOnClickListener{ openTapTalkLiveView() }
    }

    private val tapTalkLiveListener = object : TapTalkLiveListener() {
        override fun onInitializationCompleted() {
            if (isOpenTapTalkLiveViewPending) {
                isOpenTapTalkLiveViewPending = false
                openTapTalkLiveView()
            }
        }
    }

    private fun openTapTalkLiveView() {
        if (TapTalkLive.openTapTalkLiveView(this@MainActivity)) {
            isOpenTapTalkLiveViewPending = false
            TapTalkLive.initializeGooglePlacesApiKey(GOOGLE_MAPS_API_KEY)
            //finish()
        } else {
            isOpenTapTalkLiveViewPending = true
        }
    }
}
