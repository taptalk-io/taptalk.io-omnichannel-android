package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.databinding.TtlActivityCaseListBinding

class TTLCaseListActivity : TAPBaseActivity() {

    private lateinit var vb: TtlActivityCaseListBinding

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TTLCaseListActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_slide_left, io.taptalk.TapTalk.R.anim.tap_stay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TtlActivityCaseListBinding.inflate(layoutInflater)
        setContentView(vb.root)

        instanceKey = TAPTALK_INSTANCE_KEY
        initView()
        initFragment()
    }

    override fun onBackPressed() {
        try {
            super.onBackPressed()
            overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_right)
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onCloseButtonInCaseListTapped(this)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, R.color.ttlDefaultNavBarBackgroundColor))
    }

    private fun initView() {
        vb.ivButtonClose.setOnClickListener { onBackPressed() }
        vb.ivButtonNewMessage.setOnClickListener { openCreateCaseForm() }
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_case_list, TapTalkLive.getCaseListFragment() ?: return)
            .commit()
    }

    private fun openCreateCaseForm() {
        TapTalkLive.getInstance()?.tapTalkLiveListener?.onCreateNewMessageButtonTapped(this)
    }
}
