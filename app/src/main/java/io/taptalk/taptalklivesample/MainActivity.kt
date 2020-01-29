package io.taptalk.taptalklivesample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklivesample.BuildConfig.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TapTalkLive.init(applicationContext, TAPLIVE_SDK_APP_KEY_SECRET, TAPLIVE_SDK_BASE_URL, R.drawable.ic_taptalk_logo, "TapTalk.live Sample App")

        ll_button_launch_live_chat.setOnClickListener(buttonListener)
    }

    private val buttonListener = View.OnClickListener {
        if (TapTalkLive.openTapTalkLiveView(this@MainActivity)) {
            TapTalkLive.initializeGooglePlacesApiKey(GOOGLE_MAPS_API_KEY)
            finish()
        }
    }
}
