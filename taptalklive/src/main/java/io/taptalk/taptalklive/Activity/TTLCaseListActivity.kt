package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import kotlinx.android.synthetic.main.ttl_activity_case_list.*

class TTLCaseListActivity : TAPBaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TTLCaseListActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_case_list)

        instanceKey = TAPTALK_INSTANCE_KEY
        initView()
        initFragment()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    private fun initView() {
        iv_button_close.setOnClickListener { onBackPressed() }
        ll_button_new_message.setOnClickListener { openCreateCaseForm() }
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_case_list, TapTalkLive.getCaseListFragment())
            .commit()
    }

    private fun openCreateCaseForm() {
        val intent = Intent(this@TTLCaseListActivity, TTLCreateCaseFormActivity::class.java)
        intent.putExtra(SHOW_CLOSE_BUTTON, true)
        startActivity(intent)
        overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
    }
}
