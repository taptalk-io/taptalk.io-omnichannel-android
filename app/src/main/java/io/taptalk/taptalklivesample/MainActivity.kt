package io.taptalk.taptalklivesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.taptalklive.Listener.TapTalkLiveListener
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklivesample.BuildConfig.GOOGLE_MAPS_API_KEY
import io.taptalk.taptalklivesample.BuildConfig.TAPLIVE_SDK_APP_KEY_SECRET
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

        ll_button_launch_live_chat.setOnClickListener { openTapTalkLiveView() }
    }

    private val tapTalkLiveListener = object : TapTalkLiveListener() {
        override fun onInitializationCompleted() {
            if (isOpenTapTalkLiveViewPending) {
                isOpenTapTalkLiveViewPending = false
                openTapTalkLiveView()
            }
        }

//        override fun onInitializationFailed(error: TTLErrorModel?) {
//            if (DEBUG) {
//                Log.e(">>>>", "onInitializationFailed: ${error?.message}")
//            }
//        }
//
//        override fun onNotificationReceived(message: TAPMessageModel?) {
//            Log.e(">>>>", "onNotificationReceived: ${message?.body}")
//        }
//
//        override fun onCloseButtonInHomePageTapped(activity: Activity?) {
//            Log.e(">>>>", "onCloseButtonInHomePageTapped:")
//        }
//
//        override fun onCloseButtonInCreateCaseFormTapped(activity: Activity?) {
//            Log.e(">>>>", "onCloseButtonInCreateCaseFormTapped:")
//        }
//
//        override fun onCloseButtonInCaseListTapped(activity: Activity?) {
//            Log.e(">>>>", "onCloseButtonInCaseListTapped:")
//        }
//
//        override fun onTaskRootChatRoomClosed(activity: Activity?) {
//            Log.e(">>>>", "onTaskRootChatRoomClosed:")
//        }
//
//        override fun onSeeAllMessagesButtonTapped(activity: Activity?) {
//            super.onSeeAllMessagesButtonTapped(activity)
//            Log.e(">>>>", "onSeeAllMessagesButtonTapped:")
//        }
//
//        override fun onCreateNewMessageButtonTapped(activity: Activity?) {
//            super.onCreateNewMessageButtonTapped(activity)
//            Log.e(">>>>", "onCreateNewMessageButtonTapped:")
//        }
//
//        override fun onFaqChildTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onFaqChildTapped(activity, scfPath)
//            Log.e(">>>>", "onFaqChildTapped: ${scfPath?.title}")
//        }
//
//        override fun onCloseButtonInFaqDetailsTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onCloseButtonInFaqDetailsTapped(activity, scfPath)
//            Log.e(">>>>", "onCloseButtonInFaqDetailsTapped: ${scfPath?.title}")
//        }
//
//        override fun onTalkToAgentButtonTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onTalkToAgentButtonTapped(activity, scfPath)
//            Log.e(">>>>", "onTalkToAgentButtonTapped: ${scfPath?.title}")
//        }
//
//        override fun onCaseListItemTapped(activity: Activity?, lastMessage: TAPMessageModel?) {
//            super.onCaseListItemTapped(activity, lastMessage)
//            Log.e(">>>>", "onCaseListItemTapped: ${lastMessage?.room?.name}")
//        }
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
