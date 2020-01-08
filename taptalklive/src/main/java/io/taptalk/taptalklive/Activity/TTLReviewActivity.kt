package io.taptalk.taptalklive.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import io.taptalk.taptalklive.R
import kotlinx.android.synthetic.main.ttl_layout_review.*

class TTLReviewActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_review)

        window?.setBackgroundDrawable(ContextCompat.getDrawable(this@TTLReviewActivity, R.color.tapLiveTextDark80))

        initBottomSheet()

        iv_button_dismiss_review.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        bottomSheetBehavior.state = STATE_COLLAPSED
    }

    private fun initBottomSheet() {
        bottomSheetBehavior = from(cl_review_layout_container)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_HIDDEN -> {

                    }
                    STATE_EXPANDED -> {

                    }
                    STATE_COLLAPSED -> {
                        finish()
                        overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out)
                    }
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })
        bottomSheetBehavior.skipCollapsed = false
        bottomSheetBehavior.state = STATE_EXPANDED
    }
}
