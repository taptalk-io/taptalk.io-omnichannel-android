package io.taptalk.taptalklivesample

import android.os.Bundle
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import android.util.Log
import android.view.View
import io.taptalk.TapTalk.Helper.TapCustomSnackbarView
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.Listener.TTLCommonListener
import io.taptalk.taptalklive.Listener.TapTalkLiveListener
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklivesample.BuildConfig.GOOGLE_MAPS_API_KEY
import io.taptalk.taptalklivesample.BuildConfig.TAPLIVE_SDK_APP_KEY_SECRET
import io.taptalk.taptalklivesample.databinding.ActivityMainBinding

class MainActivity : TAPBaseActivity() {

    private lateinit var vb: ActivityMainBinding

    private var isInitializing = false
    private var isOpenTapTalkLiveViewPending = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)

        initTapTalkLive()
        vb.llButtonLaunchLiveChat.setOnClickListener { openTapTalkLiveView() }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, io.taptalk.taptalklive.R.color.ttlDefaultBackgroundColor))
    }

    private fun initTapTalkLive() {
        showLoading()
        TapTalkLive.init(
            applicationContext,
            TAPLIVE_SDK_APP_KEY_SECRET,
            R.drawable.ic_taptalk_logo,
            "TapTalk.live Sample App",
            tapTalkLiveListener
        )
    }

    private val tapTalkLiveListener = object : TapTalkLiveListener() {
        override fun onInitializationCompleted() {
            hideLoading()
            if (isOpenTapTalkLiveViewPending) {
                isOpenTapTalkLiveViewPending = false
                openTapTalkLiveView()
            }

            // Authentication test
//            TapTalkLive.authenticateUser("tesdev", "tesdev@tapta.lk", object : TTLCommonListener() {
//                override fun onSuccess(successMessage: String?) {
//                    hideLoading()
//                    if (isOpenTapTalkLiveViewPending) {
//                        isOpenTapTalkLiveViewPending = false
//                        openTapTalkLiveView()
//                    }
//                    Log.e(">>>>", "TEST authenticateUser onSuccess: $successMessage")
//                }
//
//                override fun onError(errorCode: String?, errorMessage: String?) {
//                    Log.e(">>>>", "TEST authenticateUser onError: $errorMessage")
//                }
//            })
        }

        override fun onInitializationFailed(error: TTLErrorModel?) {
            hideLoading()
            vb.tapCustomSnackbar.show(
                TapCustomSnackbarView.Companion.Type.ERROR,
                io.taptalk.taptalklive.R.drawable.ttl_ic_info,
                error?.message ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
            )
            Log.e(">>>>", "onInitializationFailed: ${error?.message}")
        }

//        override fun onTapTalkLiveRefreshTokenExpired() {
//
//        }
//
//        override fun onNotificationReceived(message: TAPMessageModel?) {
//
//        }
//
//        override fun onCloseButtonInHomePageTapped(activity: Activity?) {
//
//        }
//
//        override fun onCloseButtonInCreateCaseFormTapped(activity: Activity?) {
//
//        }
//
//        override fun onCloseButtonInCaseListTapped(activity: Activity?) {
//
//        }
//
//        override fun onTaskRootChatRoomClosed(activity: Activity?) {
//
//        }
//
//        override fun onSeeAllMessagesButtonTapped(activity: Activity?) {
//            super.onSeeAllMessagesButtonTapped(activity)
//
//        }
//
//        override fun onCreateNewMessageButtonTapped(activity: Activity?) {
//            super.onCreateNewMessageButtonTapped(activity)
//
//        }
//
//        override fun onFaqChildTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onFaqChildTapped(activity, scfPath)
//
//        }
//
//        override fun onCloseButtonInFaqDetailsTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onCloseButtonInFaqDetailsTapped(activity, scfPath)
//
//        }
//
//        override fun onTalkToAgentButtonTapped(activity: Activity?, scfPath: TTLScfPathModel?) {
//            super.onTalkToAgentButtonTapped(activity, scfPath)
//
//        }
//
//        override fun onCaseListItemTapped(activity: Activity?, lastMessage: TAPMessageModel?) {
//            super.onCaseListItemTapped(activity, lastMessage)
//
//        }
//        
//        override fun onFaqContentUrlTapped(activity: Activity?, scfPath: TTLScfPathModel?, url: String?) {
//
//        }
//
//        override fun onFaqContentUrlLongPressed(activity: Activity?, scfPath: TTLScfPathModel?, url: String?) {
//
//        }
    }

    private fun openTapTalkLiveView() {
        if (!TapTalkLive.isTapTalkLiveInitialized) {
            isOpenTapTalkLiveViewPending = true
            initTapTalkLive()
        }
        else if (TapTalkLive.openTapTalkLiveView(this@MainActivity)) {
            isOpenTapTalkLiveViewPending = false
            TapTalkLive.initializeGooglePlacesApiKey(GOOGLE_MAPS_API_KEY)
            //finish()
        }
        else {
            isOpenTapTalkLiveViewPending = true
        }
    }

    private fun showLoading() {
        runOnUiThread {
            isInitializing = true
            vb.tvButtonLaunchLiveChat.text = getString(R.string.ttl_initializing_taptalklive)
            vb.ivButtonLaunchLiveChat.visibility = View.GONE
            vb.pbButtonLaunchLiveChat.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            isInitializing = false
            vb.tvButtonLaunchLiveChat.text = getString(R.string.ttl_launch_live_chat)
            vb.ivButtonLaunchLiveChat.visibility = View.VISIBLE
            vb.pbButtonLaunchLiveChat.visibility = View.GONE
        }
    }
}
