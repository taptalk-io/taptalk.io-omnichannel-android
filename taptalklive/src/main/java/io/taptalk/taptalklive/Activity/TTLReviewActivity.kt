package io.taptalk.taptalklive.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.taptalk.taptalklive.Fragment.TTLReviewBottomSheetFragment
import io.taptalk.taptalklive.R

class TTLReviewActivity : AppCompatActivity() {

    private lateinit var reviewBottomSheetFragment: TTLReviewBottomSheetFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_review)
        initView()
    }

    override fun onBackPressed() {
        reviewBottomSheetFragment.dismiss()
    }

    private fun initView() {
        window?.setBackgroundDrawable(ContextCompat.getDrawable(this@TTLReviewActivity, R.color.ttlTextDark80))

        reviewBottomSheetFragment = TTLReviewBottomSheetFragment(reviewBottomSheetListener)
        reviewBottomSheetFragment.show(supportFragmentManager, "")
    }

    private val reviewBottomSheetListener = object : TTLReviewBottomSheetFragment.ReviewBottomSheetListener {
        override fun onBottomSheetCollapsed() {
            // TODO SET RESULT
            finish()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out)
        }

        override fun onSubmitReviewButtonTapped(rating: Int, comment: String) {
            // TODO CHECK IF API CALL IS RUNNING AND SUBMIT REVIEW
            reviewBottomSheetFragment.dismiss()
        }
    }
}
