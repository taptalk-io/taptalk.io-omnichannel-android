package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.taptalklive.API.Model.ResponseModel.*
import io.taptalk.taptalklive.API.Model.TTLCaseModel
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.NEW_CASE_CREATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.CASE_DETAILS
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLCommonListener
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.ViewModel.TTLCreateCaseViewModel
import io.taptalk.taptalklive.adapter.TTLItemDropdownAdapter
import kotlinx.android.synthetic.main.ttl_activity_create_case_form.*

class TTLCreateCaseFormActivity : AppCompatActivity() {

    private lateinit var vm: TTLCreateCaseViewModel
    private lateinit var glide: RequestManager
    private lateinit var topicAdapter: TTLItemDropdownAdapter

    private var selectedTopicIndex = -1

    companion object {
        fun start(context: Context, showCloseButton: Boolean) {
            val intent = Intent(context, TTLCreateCaseFormActivity::class.java)
            intent.putExtra(SHOW_CLOSE_BUTTON, showCloseButton)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
            }
        }
    }

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
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        TapTalkLive.getInstance()?.tapTalkLiveListener?.onCloseButtonInCreateCaseFormTapped(this)
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
                onBackPressed()
            }
        }

        if (!TTLDataManager.getInstance().checkActiveUserExists() || TTLDataManager.getInstance().accessToken.isNullOrEmpty()) {
            // Show name and email fields if user does not exist
            ll_full_name.visibility = View.VISIBLE
            ll_email_address.visibility = View.VISIBLE
            et_full_name.onFocusChangeListener = formFocusListener
            et_email_address.onFocusChangeListener = formFocusListener
            vm.openRoomListOnComplete = true
        }
        else {
            ll_full_name.visibility = View.GONE
            ll_email_address.visibility = View.GONE
        }
        et_message.onFocusChangeListener = formFocusListener

        initTopics()

        ll_button_send_message.setOnClickListener { validateSendMessage() }
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_active)
            hideTopicDropdown()
        }
        else {
            view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        }
        if (view == et_full_name) {
            cl_full_name_error.visibility = View.GONE
        }
        else if (view == et_email_address) {
            cl_email_address_error.visibility = View.GONE
        }
        else if (view == et_message) {
            cl_message_error.visibility = View.GONE
        }
    }

    private fun initTopics() {
        fetchTopicList(true)

        topicAdapter = TTLItemDropdownAdapter(
            vm.topics,
            object : TTLItemListInterface {
                override fun onItemSelected(position: Int) {
                    if (position > -1 && position <= vm.topics.size) {
                        selectedTopicIndex = position
                        tv_topic.text = vm.topics[selectedTopicIndex]
                        hideTopicDropdown()
                        cl_topic_error.visibility = View.GONE
                    }
                }
            }
        )
        val layoutManager: LinearLayoutManager =
            object : LinearLayoutManager(this, VERTICAL, false) {
                override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                    try {
                        super.onLayoutChildren(recycler, state)
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
            }
        rv_topic_dropdown.adapter = topicAdapter
        rv_topic_dropdown.layoutManager = layoutManager
        rv_topic_dropdown.setMaxHeight(TAPUtils.dpToPx(resources, 144f))
        cl_topic.setOnClickListener {
            showTopicDropdown()
        }
    }

    private fun fetchTopicList(getFromPreference: Boolean) {
        if (getFromPreference) {
            val topics = TTLDataManager.getInstance().topics
            if (!topics.isNullOrEmpty()) {
                for (topic in topics) {
                    vm.topicsMap[topic.name] = topic
                }
                vm.topics.clear()
                vm.topics.addAll(vm.topicsMap.keys)

                showTopicLoadingFinished()
            }
        }
        TTLDataManager.getInstance().getTopicList(topicListDataView)
    }

    private val topicListDataView = object : TTLDefaultDataView<TTLGetTopicListResponse>() {
        override fun onSuccess(response: TTLGetTopicListResponse?) {
            if (null != response) {
                TTLDataManager.getInstance().saveTopics(response.topics)
                for (topic in response.topics) {
                    vm.topicsMap[topic.name] = topic
                }
                vm.topics.clear()
                vm.topics.addAll(vm.topicsMap.keys)
                topicAdapter.items = vm.topics

                showTopicLoadingFinished()
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
        TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).addNetworkListener(object : TapTalkNetworkInterface {
            override fun onNetworkAvailable() {
                fetchTopicList(false)
                TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).removeNetworkListener(this)
            }
        })
    }

    private fun showTopicLoadingFinished() {
        cl_topic.background = ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_bg_text_field_inactive)
        pb_select_topic_loading.visibility = View.GONE
        iv_select_topic_drop_down.visibility = View.VISIBLE
    }

    private fun showTopicDropdown() {
        if (vm.topics.isEmpty() || cv_topic_dropdown.visibility == View.VISIBLE) {
            return
        }
        TAPUtils.dismissKeyboard(this)
        val location = IntArray(2)
        cl_topic.getLocationInWindow(location)
        cv_topic_dropdown.translationY = location[1].toFloat() + (cl_topic.height.toFloat() / 2)
        cv_topic_dropdown.visibility = View.VISIBLE
        cl_topic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_active)
        cl_topic.setOnClickListener {
            hideTopicDropdown()
        }
        iv_select_topic_drop_down.animate()
            .rotation(180f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                v_dismiss_dropdown.visibility = View.VISIBLE
                v_dismiss_dropdown.setOnClickListener {
                    hideTopicDropdown()
                }
            }
            .start()
    }

    private fun hideTopicDropdown() {
        if (cv_topic_dropdown.visibility != View.VISIBLE) {
            return
        }
        cv_topic_dropdown.visibility = View.INVISIBLE
        v_dismiss_dropdown.visibility = View.GONE
        v_dismiss_dropdown.setOnClickListener(null)
        cl_topic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        cl_topic.setOnClickListener {
            showTopicDropdown()
        }
        iv_select_topic_drop_down.animate()
            .rotation(0f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun validateFullName(): Boolean {
        return if (!et_full_name.text.isNullOrEmpty()) {
            true
        } else {
//            showValidationErrorDialog(getString(R.string.ttl_error_message_full_name_empty))
            et_full_name.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            cl_full_name_error.visibility = View.VISIBLE
            false
        }
    }

    private fun validateEmail(): Boolean {
//        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
//            return true
//        } else if (et_email_address.text.isNotEmpty()) {
//            showValidationErrorDialog(getString(R.string.ttl_error_message_email_invalid))
//        } else {
//            showValidationErrorDialog(getString(R.string.ttl_error_message_email_empty))
//        }
        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            return true
        } else if (et_email_address.text.isNotEmpty()) {
            tv_email_address_error_message.text = getString(R.string.ttl_error_message_email_invalid)
        } else {
            tv_email_address_error_message.text = getString(R.string.ttl_error_field_required)
        }
        et_email_address.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
        cl_email_address_error.visibility = View.VISIBLE
        return false
    }

    private fun validateTopic(): Boolean {
        return if (selectedTopicIndex >= 0 && selectedTopicIndex < vm.topics.size) {
            true
        } else {
//            showValidationErrorDialog(getString(R.string.ttl_error_message_topic_empty))
            cl_topic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            cl_topic_error.visibility = View.VISIBLE
            false
        }
    }

    private fun validateMessage(): Boolean {
        return if (!et_message.text.isNullOrEmpty()) {
            true
        } else {
//            showValidationErrorDialog(getString(R.string.ttl_error_message_message_empty))
            et_message.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            cl_message_error.visibility = View.VISIBLE
            false
        }
    }

    private fun validateSendMessage() {
        val topicValidated = validateTopic()
        val messageValidated = validateMessage()

        if (!TTLDataManager.getInstance().checkActiveUserExists() || !TTLDataManager.getInstance().checkAccessTokenAvailable()) {
            val fullNameValidated = validateFullName()
            val emailValidated = validateEmail()
            if (fullNameValidated && emailValidated && topicValidated && messageValidated) {
                ll_button_send_message.setOnClickListener(null)
                showLoading()
                TapTalkLive.authenticateUser(et_full_name.text.toString(), et_email_address.text.toString(), authenticationListener)
            }
        } else {
            if (topicValidated && messageValidated) {
                ll_button_send_message.setOnClickListener(null)
                showLoading()
                if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                    TapTalkLive.requestTapTalkAuthTicket(authenticationListener)
                } else {
                    createCase()
                }
            }
        }
    }

    private val authenticationListener = object : TTLCommonListener() {
        override fun onSuccess(successMessage: String?) {
            createCase()
        }

        override fun onError(errorCode: String?, errorMessage: String?) {
            showDefaultErrorDialog(errorMessage)
        }
    }

    private fun showLoading() {
        tv_button_send_message.visibility = View.GONE
        pb_button_send_message_loading.visibility = View.VISIBLE
        et_full_name.isEnabled = false
        et_email_address.isEnabled = false
        et_message.isEnabled = false
        cl_topic.setOnClickListener(null)
        et_full_name.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        et_email_address.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        et_message.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        cl_topic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        et_full_name.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        et_email_address.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        et_message.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        tv_topic.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
//        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_ic_loading_progress_circle_white))
//        TAPUtils.rotateAnimateInfinitely(this@TTLCreateCaseFormActivity, iv_button_send_message)
        ll_button_send_message.setOnClickListener { }
    }

    private fun hideLoading() {
//        iv_button_send_message.clearAnimation()
//        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_ic_send_white))
        tv_button_send_message.visibility = View.VISIBLE
        pb_button_send_message_loading.visibility = View.GONE
        et_full_name.isEnabled = true
        et_email_address.isEnabled = true
        et_message.isEnabled = true
        et_full_name.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        et_email_address.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        et_message.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        cl_topic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        et_full_name.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        et_email_address.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        et_message.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        tv_topic.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        cl_topic.setOnClickListener {
            showTopicDropdown()
        }
        ll_button_send_message.setOnClickListener { validateSendMessage() }
    }

    private fun createCase() {
        TTLDataManager.getInstance().createCase(
            vm.topicsMap[vm.topics[selectedTopicIndex]]?.id,
            et_message.text.toString(),
            createCaseDataView
        )
    }

    private val createCaseDataView = object : TTLDefaultDataView<TTLCreateCaseResponse>() {
        override fun onSuccess(response: TTLCreateCaseResponse?) {
            if (!response?.caseResponse?.tapTalkXCRoomID.isNullOrEmpty()) {
                openCaseChatRoom(response?.caseResponse)
            } else {
                onError(getString(R.string.ttl_error_xc_room_id_empty))
                sendCaseCreatedBroadcast(response?.caseResponse)
            }
        }

        override fun onError(error: TTLErrorModel?) {
            showDefaultErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.tap_error_message_general))
        }
    }

    private fun openCaseChatRoom(caseModel: TTLCaseModel?) {
        TTLDataManager.getInstance().saveActiveUserHasExistingCase(true)
        TapCoreChatRoomManager.getInstance(TAPTALK_INSTANCE_KEY).getChatRoomByXcRoomID(caseModel?.tapTalkXCRoomID, object : TapCoreGetRoomListener() {
            override fun onSuccess(roomModel: TAPRoomModel?) {
                if (vm.openRoomListOnComplete) {
                    TTLCaseListActivity.start(this@TTLCreateCaseFormActivity);
                }
                TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(this@TTLCreateCaseFormActivity, roomModel)
                sendCaseCreatedBroadcast(caseModel)
                finish()
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                showDefaultErrorDialog(errorMessage)
            }
        })
    }

    private fun sendCaseCreatedBroadcast(caseModel: TTLCaseModel?) {
        val intent = Intent(NEW_CASE_CREATED)
        intent.putExtra(CASE_DETAILS, caseModel)
        LocalBroadcastManager.getInstance(this@TTLCreateCaseFormActivity).sendBroadcast(intent)
    }

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val message = if (!TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(this@TTLCreateCaseFormActivity)) {
            getString(R.string.ttl_error_message_offline)
        } else if (!errorMessage.isNullOrEmpty()) {
            errorMessage
        } else  {
            getString(R.string.ttl_error_message_general)
        }
        TapTalkDialog.Builder(this@TTLCreateCaseFormActivity)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setCancelable(true)
            .setTitle(getString(R.string.ttl_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.ttl_ok))
            .show()
        ll_button_send_message.setOnClickListener { validateSendMessage() }
        hideLoading()
    }

    private fun showValidationErrorDialog(errorMessage: String?) {
        TapTalkDialog.Builder(this@TTLCreateCaseFormActivity)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setCancelable(true)
            .setMessage(errorMessage)
            .setPrimaryButtonTitle(getString(R.string.ttl_ok))
            .show()
        ll_button_send_message.setOnClickListener { validateSendMessage() }
        hideLoading()
    }
}
