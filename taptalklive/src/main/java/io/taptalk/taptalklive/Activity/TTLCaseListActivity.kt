package io.taptalk.taptalklive.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON
import io.taptalk.taptalklive.R
import kotlinx.android.synthetic.main.ttl_activity_case_list.*

class TTLCaseListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_case_list)

        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    private fun initView() {
        iv_button_close.setOnClickListener { onBackPressed() }
        ll_button_new_conversation.setOnClickListener { openCreateCaseForm() }

        // TODO SET CLIENT LOGO
        iv_logo.visibility = View.INVISIBLE
    }

    private fun openCreateCaseForm() {
        val intent = Intent(this@TTLCaseListActivity, TTLCreateCaseFormActivity::class.java)
        intent.putExtra(SHOW_CLOSE_BUTTON, true)
        startActivity(intent)
        overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
    }
}
