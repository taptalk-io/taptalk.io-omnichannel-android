package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCommonResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.Extras.MESSAGE
import io.taptalk.taptalklive.Fragment.TTLReviewBottomSheetFragment
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.ViewModel.TTLReviewViewModel
import java.lang.Exception

class TTLReviewActivity : AppCompatActivity() {

    private lateinit var vm: TTLReviewViewModel

    private lateinit var reviewBottomSheetFragment: TTLReviewBottomSheetFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_review)

        initViewModel()
        initView()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        reviewBottomSheetFragment.dismiss()
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this@TTLReviewActivity).get(TTLReviewViewModel::class.java)

        vm.message = intent.getParcelableExtra(MESSAGE)!!

        try {
            vm.caseID = vm.message.room.xcRoomID.replace("case:", "").toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out)
        }
    }

    private fun initView() {
        window?.setBackgroundDrawable(ContextCompat.getDrawable(this@TTLReviewActivity, R.color.ttlTextDark80))

        reviewBottomSheetFragment = TTLReviewBottomSheetFragment(reviewBottomSheetListener)
        reviewBottomSheetFragment.show(supportFragmentManager, "")
    }

    private val reviewBottomSheetListener = object : TTLReviewBottomSheetFragment.ReviewBottomSheetListener {
        override fun onBottomSheetCollapsed() {
            finish()
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_fade_out)
        }

        override fun onSubmitReviewButtonTapped(rating: Int, comment: String) {
            if (vm.isReviewSubmitting || vm.caseID == -1) {
                return
            }
            TTLDataManager.getInstance().rateConversation(vm.caseID, rating, comment, reviewDataView)
            reviewBottomSheetFragment.dismiss()
        }
    }

    private val reviewDataView = object : TTLDefaultDataView<TTLCommonResponse>() {
        override fun startLoading() {
            vm.isReviewSubmitting = true
        }

        override fun endLoading() {
            vm.isReviewSubmitting = false
        }

        override fun onSuccess(response: TTLCommonResponse?) {
            setResult(Activity.RESULT_OK)
            reviewBottomSheetFragment.dismiss()
        }

        override fun onError(error: TTLErrorModel?) {
            reviewBottomSheetFragment.onSubmitReviewFailed()
            showDefaultErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            reviewBottomSheetFragment.onSubmitReviewFailed()
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.tap_error_message_general))
        }
    }

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val message = if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(this@TTLReviewActivity)) {
            getString(R.string.ttl_error_message_offline)
        } else if (!errorMessage.isNullOrEmpty()) {
            errorMessage
        } else  {
            getString(R.string.ttl_error_message_general)
        }
        TapTalkDialog.Builder(this@TTLReviewActivity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.ttl_error))
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                .show()
    }
}
