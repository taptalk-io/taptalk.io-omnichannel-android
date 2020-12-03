package io.taptalk.taptalklive.Activity

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.taptalklive.API.Model.ResponseModel.*
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.Manager.TTLNetworkStateManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.ViewModel.TTLCreateCaseViewModel
import kotlinx.android.synthetic.main.ttl_activity_create_case_form.*

class TTLCreateCaseFormActivity : AppCompatActivity() {

    private lateinit var vm: TTLCreateCaseViewModel

    private lateinit var glide: RequestManager

    private lateinit var topicSpinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_create_case_form)

        glide = Glide.with(this)
        initViewModel()
        initView()
    }

    override fun onBackPressed() {
        if (!vm.showCloseButton) {
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this).get(TTLCreateCaseViewModel::class.java)

        vm.showCloseButton = intent.getBooleanExtra(SHOW_CLOSE_BUTTON, false)
    }

    private fun initView() {
        window?.setBackgroundDrawable(null)

        if (vm.showCloseButton) {
            iv_button_close.visibility = View.VISIBLE
            iv_button_close.setOnClickListener {
                TapTalkLive.tapTalkLiveListener.onCloseButtonInCreateCaseFormTapped()
                onBackPressed()
            }
        }

        if (!TTLDataManager.getInstance().checkActiveUserExists() || TTLDataManager.getInstance().accessToken.isNullOrEmpty()) {
            // Show name and email fields if user does not exist
            tv_label_full_name.visibility = View.VISIBLE
            et_full_name.visibility = View.VISIBLE
            tv_label_email_address.visibility = View.VISIBLE
            et_email_address.visibility = View.VISIBLE
            et_full_name.onFocusChangeListener = formFocusListener
            et_email_address.onFocusChangeListener = formFocusListener
            vm.openRoomListOnComplete = true
        }
        et_message.onFocusChangeListener = formFocusListener

        initTopicSpinnerAdapter()

        ll_button_send_message.setOnClickListener { validateSendMessage() }

        // TODO SET CLIENT LOGO
        iv_logo.visibility = View.INVISIBLE
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        }
    }

    private fun initTopicSpinnerAdapter() {
        val spinnerPlaceholder = getString(R.string.ttl_select_topic)
        vm.topics.add(spinnerPlaceholder)

        TAPUtils.rotateAnimateInfinitely(this@TTLCreateCaseFormActivity, iv_select_topic_loading)

        getTopicList()

        topicSpinnerAdapter = object : ArrayAdapter<String>(
                this, R.layout.ttl_cell_default_spinner_item, vm.topics) {
            override fun isEnabled(position: Int): Boolean {
                return vm.topics[position] != spinnerPlaceholder
            }
        }

        val spinnerAdapterListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val tv = view as TextView
                if (vm.topics[position] == spinnerPlaceholder) {
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.ttlFormTextFieldPlaceholderColor))
                } else {
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.ttlFormTextFieldColor))
                    if (vm.topics.contains(spinnerPlaceholder)) {
                        vm.topics.remove(spinnerPlaceholder)
                        sp_select_topic.setSelection(position - 1)
                        topicSpinnerAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        topicSpinnerAdapter.setDropDownViewResource(R.layout.ttl_cell_default_spinner_dropdown_item)
        sp_select_topic.adapter = topicSpinnerAdapter
        sp_select_topic.onItemSelectedListener = spinnerAdapterListener
        sp_select_topic.setOnTouchListener { view, motionEvent ->
            TAPUtils.dismissKeyboard(this@TTLCreateCaseFormActivity)
            false
        }
    }

    private fun getTopicList() {
        TTLDataManager.getInstance().getTopicList(topicListDataView)
    }

    private val topicListDataView = object : TTLDefaultDataView<TTLGetTopicListResponse>() {
        override fun onSuccess(response: TTLGetTopicListResponse?) {
            if (null != response) {
                for (topic in response.topics) {
                    vm.topicsMap[topic.name] = topic
                }
                vm.topics.addAll(vm.topicsMap.keys)
                topicSpinnerAdapter.notifyDataSetChanged()

                iv_select_topic_loading.clearAnimation()
                iv_select_topic_loading.visibility = View.GONE
                iv_select_topic_drop_down.visibility = View.VISIBLE
            }
        }

        override fun onError(error: TTLErrorModel?) {
            setGetTopicListAsPending()
        }

        override fun onError(errorMessage: String?) {
            setGetTopicListAsPending()
        }
    }

    private fun setGetTopicListAsPending() {
        TTLNetworkStateManager.getInstance().addNetworkListener(object : TapTalkNetworkInterface {
            override fun onNetworkAvailable() {
                getTopicList()
                TTLNetworkStateManager.getInstance().removeNetworkListener(this)
            }
        })
    }

    private fun validateFullName(): Boolean {
        return if (!et_full_name.text.isNullOrEmpty()) {
            true
        } else {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_full_name_empty))
                    .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                    .show()
            false
        }
    }

    private fun validateEmail(): Boolean {
        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            return true
        } else if (et_email_address.text.isNotEmpty()) {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_email_invalid))
                    .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                    .show()
        } else {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_email_empty))
                    .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                    .show()
        }
        return false
    }

    private fun validateTopic(): Boolean {
        return if (vm.topics[sp_select_topic.selectedItemPosition] != getString(R.string.ttl_select_topic)) {
            true
        } else {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_topic_empty))
                    .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                    .show()
            false
        }
    }

    private fun validateMessage(): Boolean {
        return if (!et_message.text.isNullOrEmpty()) {
            true
        } else {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_message_empty))
                    .setPrimaryButtonTitle(getString(R.string.ttl_ok))
                    .show()
            false
        }
    }

    private fun validateSendMessage() {
        if (!TTLDataManager.getInstance().checkActiveUserExists() || !TTLDataManager.getInstance().checkAccessTokenAvailable()) {
            if (validateFullName() && validateEmail() && validateTopic() && validateMessage()) {
                createUser()
            }
        } else {
            if (validateTopic() && validateMessage()) {
                if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                    requestTapTalkAuthTicket()
                } else {
                    createCase()
                }
            }
        }
    }

    private fun showLoading() {
        tv_button_send_message.visibility = View.GONE
        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_ic_loading_progress_circle_white))
        TAPUtils.rotateAnimateInfinitely(this@TTLCreateCaseFormActivity, iv_button_send_message)
        ll_button_send_message.setOnClickListener { }
    }

    private fun hideLoading() {
        iv_button_send_message.clearAnimation()
        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_ic_send_white))
        tv_button_send_message.visibility = View.VISIBLE
        ll_button_send_message.setOnClickListener { validateSendMessage() }
    }

    private fun createUser() {
        TTLDataManager.getInstance().createUser(
                et_full_name.text.toString(),
                et_email_address.text.toString(),
                createUserDataView)
    }

    private val createUserDataView = object : TTLDefaultDataView<TTLCreateUserResponse>() {
        override fun startLoading() {
            ll_button_send_message.setOnClickListener { }
            showLoading()
        }

        override fun onSuccess(response: TTLCreateUserResponse?) {
            if (null != response) {
                TTLDataManager.getInstance().saveAuthTicket(response.ticket)
                TTLDataManager.getInstance().saveActiveUser(response.user)
                requestAccessToken()
            }
        }

        override fun onError(error: TTLErrorModel?) {
            showDefaultErrorDialog(error?.message)
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.ttl_error_message_general))
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }
    }

    private fun requestAccessToken() {
        TTLDataManager.getInstance().requestAccessToken(requestAccessTokenDataView)
    }

    private val requestAccessTokenDataView = object : TTLDefaultDataView<TTLRequestAccessTokenResponse>() {
        override fun onSuccess(response: TTLRequestAccessTokenResponse?) {
            if (null != response) {
                TTLDataManager.getInstance().removeAuthTicket()
                TTLDataManager.getInstance().saveAccessToken(response.accessToken)
                TTLDataManager.getInstance().saveRefreshToken(response.refreshToken)
                TTLDataManager.getInstance().saveRefreshTokenExpiry(response.refreshTokenExpiry)
                TTLDataManager.getInstance().saveAccessTokenExpiry(response.accessTokenExpiry)
                TTLDataManager.getInstance().saveActiveUser(response.user)
                if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                    requestTapTalkAuthTicket()
                } else {
                    createCase()
                }
            }
        }

        override fun onError(error: TTLErrorModel?) {
            showDefaultErrorDialog(error?.message)
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.tap_error_message_general))
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }
    }

    private fun requestTapTalkAuthTicket() {
        TTLDataManager.getInstance().requestTapTalkAuthTicket(requestTapTalkAuthTicketDataView)
    }

    private val requestTapTalkAuthTicketDataView = object : TTLDefaultDataView<TTLRequestTicketResponse>() {
        override fun startLoading() {
            ll_button_send_message.setOnClickListener { }
            showLoading()
        }

        override fun onSuccess(response: TTLRequestTicketResponse?) {
            if (null != response) {
                TTLDataManager.getInstance().saveTapTalkAuthTicket(response.ticket)
                authenticateTapTalkSDK(response.ticket)
            } else {
                onError(getString(R.string.ttl_error_taptalk_auth_ticket_empty))
            }
        }

        override fun onError(error: TTLErrorModel?) {
            showDefaultErrorDialog(error?.message)
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.ttl_error_message_general))
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }
    }

    private fun authenticateTapTalkSDK(ticket: String) {
        TapTalkLive.authenticateTapTalkSDK(ticket, authenticateTapTalkSDKListener)
    }

    private val authenticateTapTalkSDKListener = object : TapCommonListener() {
        override fun onSuccess(p0: String?) {
            TTLDataManager.getInstance().removeTapTalkAuthTicket()
            createCase()
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            showDefaultErrorDialog(errorMessage)
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }
    }

    private fun createCase() {
        TTLDataManager.getInstance().createCase(
                vm.topicsMap[vm.topics[sp_select_topic.selectedItemPosition]]?.id,
                et_message.text.toString(),
                createCaseDataView)
    }

    private val createCaseDataView = object : TTLDefaultDataView<TTLCreateCaseResponse>() {
        override fun startLoading() {
            ll_button_send_message.setOnClickListener { }
            showLoading()
        }

        override fun onSuccess(response: TTLCreateCaseResponse?) {
            if (!response?.caseResponse?.tapTalkXCRoomID.isNullOrEmpty()) {
                openCaseChatRoom(response?.caseResponse?.tapTalkXCRoomID!!)
            } else {
                onError(getString(R.string.ttl_error_xc_room_id_empty))
            }
        }

        override fun onError(error: TTLErrorModel?) {
            showDefaultErrorDialog(error?.message)
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.tap_error_message_general))
            ll_button_send_message.setOnClickListener { validateSendMessage() }
            hideLoading()
        }
    }

    private fun openCaseChatRoom(tapTalkXCRoomID: String) {
        TTLDataManager.getInstance().saveActiveUserHasExistingCase(true)
        TapCoreChatRoomManager.getInstance(TAPTALK_INSTANCE_KEY).getChatRoomByXcRoomID(tapTalkXCRoomID, object : TapCoreGetRoomListener() {
            override fun onSuccess(roomModel: TAPRoomModel?) {
                if (vm.openRoomListOnComplete) {
                    TapUI.getInstance(TAPTALK_INSTANCE_KEY).openRoomList(this@TTLCreateCaseFormActivity)
                }
                TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(this@TTLCreateCaseFormActivity, roomModel)
                finish()
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                showDefaultErrorDialog(errorMessage)
                ll_button_send_message.setOnClickListener { validateSendMessage() }
                hideLoading()
            }
        })
    }

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val message = if (!TTLNetworkStateManager.getInstance().hasNetworkConnection(this@TTLCreateCaseFormActivity)) {
            getString(R.string.ttl_error_message_offline)
        } else if (!errorMessage.isNullOrEmpty()) {
            errorMessage
        } else  {
            getString(R.string.ttl_error_message_general)
        }
        TapTalkDialog.Builder(this@TTLCreateCaseFormActivity)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(getString(R.string.ttl_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.ttl_ok))
            .show()
    }
}
