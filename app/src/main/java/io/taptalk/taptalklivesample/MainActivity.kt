package io.taptalk.taptalklivesample

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.TapTalkLive
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TapTalkLive.init(applicationContext, R.mipmap.ic_launcher, "TapTalk.live Sample App")

        ll_button_launch_live_chat.setOnClickListener(buttonListener)
    }

    private val buttonListener = View.OnClickListener {
        if (TapTalkLive.openChatRoomList(this@MainActivity)) {
            if (null == TTLDataManager.getInstance().activeUser || null == TTLDataManager.getInstance().checkAccessTokenAvailable()) {
                TapTalkLive.openCreateCaseForm(this@MainActivity, false)
            }
            finish()
        }
    }
}
