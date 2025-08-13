package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Helper.TapTalkDialog.DialogType.DEFAULT
import io.taptalk.TapTalk.Helper.TapTalkDialog.DialogType.ERROR_DIALOG
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.Extras.MESSAGE
import io.taptalk.taptalklive.Const.TTLConstant.RequestCode.REVIEW
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Fragment.TTLReviewBottomSheetFragment
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.ViewModel.TTLReviewViewModel
import io.taptalk.taptalklive.databinding.TtlActivityReviewBinding

class TTLReviewActivity : TAPBaseActivity() {

    private lateinit var vb: TtlActivityReviewBinding
    private lateinit var vm: TTLReviewViewModel
    private lateinit var reviewBottomSheetFragment: TTLReviewBottomSheetFragment

    companion object {
        fun start(context: Context, message: TAPMessageModel) {
            val intent = Intent(context, TTLReviewActivity::class.java)
            intent.putExtra(MESSAGE, message)
            if (context is Activity) {
                context.startActivityForResult(intent, REVIEW)
                context.overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_fade_in, io.taptalk.TapTalk.R.anim.tap_stay)
            }
            else {
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = TtlActivityReviewBinding.inflate(layoutInflater)
        setContentView(vb.root)

        initViewModel()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_fade_out)
    }

    override fun onBackPressed() {
        try {
            setResult(Activity.RESULT_CANCELED)
            reviewBottomSheetFragment.dismiss()
            super.onBackPressed()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, R.color.ttlTransparentBlack1940))
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this@TTLReviewActivity).get(TTLReviewViewModel::class.java)

        vm.message = intent.getParcelableExtra(MESSAGE)!!

        try {
            vm.caseID = vm.message.room.xcRoomID.replace("case:", "").toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun initView() {
        window?.setBackgroundDrawable(ContextCompat.getDrawable(this@TTLReviewActivity, R.color.ttlTransparentBlack1940))

        reviewBottomSheetFragment = TTLReviewBottomSheetFragment(reviewBottomSheetListener)
        reviewBottomSheetFragment.show(supportFragmentManager, "")
    }

    private val reviewBottomSheetListener = object : TTLReviewBottomSheetFragment.ReviewBottomSheetListener {
        override fun onBottomSheetCollapsed() {
            if (vm.isReviewSubmitting) {
                return
            }
            finish()
        }

        override fun onSubmitReviewButtonTapped(rating: Int, comment: String) {
            if (vm.isReviewSubmitting || vm.caseID == -1) {
                return
            }
            vm.pendingRating = rating
            vm.pendingComment = comment
            vm.isReviewSubmitting = true
            TTLDataManager.getInstance().rateConversation(vm.caseID, rating, comment, reviewDataView)
        }
    }

    private val reviewDataView = object : TTLDefaultDataView<TTLCommonResponse>() {
        override fun onSuccess(response: TTLCommonResponse?) {
            setResult(Activity.RESULT_OK)
            reviewBottomSheetFragment.dismiss()
            TapTalkDialog.Builder(this@TTLReviewActivity)
                .setDialogType(DEFAULT)
                .setCancelable(false)
                .setTitle(getString(R.string.ttl_thank_you_exclamation))
                .setMessage(getString(R.string.ttl_review_submitted_message))
                .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                .setPrimaryButtonListener {
                    finish()
                }
                .show()
        }

        override fun onError(error: TTLErrorModel?) {
            reviewBottomSheetFragment.onSubmitReviewFailed()
            showDefaultErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            reviewBottomSheetFragment.onSubmitReviewFailed()
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
        }
    }

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val message = if (!TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(this@TTLReviewActivity)) {
            getString(R.string.ttl_error_message_offline)
        } else if (!errorMessage.isNullOrEmpty()) {
            errorMessage
        } else  {
            getString(R.string.ttl_error_message_submit_review)
        }
        reviewBottomSheetFragment.dismiss()
        TapTalkDialog.Builder(this@TTLReviewActivity)
            .setDialogType(ERROR_DIALOG)
            .setCancelable(false)
            .setTitle(getString(R.string.ttl_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.ttl_ok))
            .setPrimaryButtonListener {
                vm.isReviewSubmitting = false
                reviewBottomSheetFragment = TTLReviewBottomSheetFragment(reviewBottomSheetListener, vm.pendingRating, vm.pendingComment)
                reviewBottomSheetFragment.show(supportFragmentManager, "")
            }
            .show()
    }
}
