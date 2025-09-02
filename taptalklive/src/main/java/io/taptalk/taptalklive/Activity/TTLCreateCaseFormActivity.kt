package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapCustomSnackbarView
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateCaseResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse
import io.taptalk.taptalklive.API.Model.TTLCaseModel
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.NEW_CASE_CREATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.CASE_DETAILS
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SHOW_CLOSE_BUTTON
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Interface.TTLItemListInterface
import io.taptalk.taptalklive.Listener.TTLCommonListener
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.ViewModel.TTLCreateCaseViewModel
import io.taptalk.taptalklive.adapter.TTLItemDropdownAdapter
import io.taptalk.taptalklive.databinding.TtlActivityCreateCaseFormBinding
import io.taptalk.taptalklive.helper.TTLUtil

class TTLCreateCaseFormActivity : TAPBaseActivity() {

    private lateinit var vb: TtlActivityCreateCaseFormBinding
    private lateinit var vm: TTLCreateCaseViewModel
    private lateinit var glide: RequestManager
    private lateinit var topicAdapter: TTLItemDropdownAdapter

    companion object {
        fun start(context: Context, showCloseButton: Boolean) {
            val intent = Intent(context, TTLCreateCaseFormActivity::class.java)
            intent.putExtra(SHOW_CLOSE_BUTTON, showCloseButton)
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
        vb = TtlActivityCreateCaseFormBinding.inflate(layoutInflater)
        setContentView(vb.root)

        glide = Glide.with(this)
        initViewModel()
        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("topic", vm.selectedTopicIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        vm.selectedTopicIndex = savedInstanceState.getInt("topic", -1)
    }

    override fun onBackPressed() {
        if (!vm.showCloseButton) {
            return
        }
        try {
            super.onBackPressed()
            overridePendingTransition(io.taptalk.TapTalk.R.anim.tap_stay, io.taptalk.TapTalk.R.anim.tap_slide_right)
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onCloseButtonInCreateCaseFormTapped(this)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, R.color.ttlDefaultNavBarBackgroundColor))
    }

    private fun initViewModel() {
        vm = ViewModelProviders.of(this).get(TTLCreateCaseViewModel::class.java)

        vm.showCloseButton = intent.getBooleanExtra(SHOW_CLOSE_BUTTON, false)
    }

    private fun initView() {
        window?.setBackgroundDrawable(null)

        if (vm.showCloseButton) {
            vb.ivButtonClose.visibility = View.VISIBLE
            vb.ivButtonClose.setOnClickListener {
                onBackPressed()
            }
        }

        if (!TTLDataManager.getInstance().checkActiveUserExists() || TTLDataManager.getInstance().accessToken.isNullOrEmpty()) {
            // Show name and email fields if user does not exist
            vb.llFullName.visibility = View.VISIBLE
            vb.etFullName.onFocusChangeListener = formFocusListener
            vb.etFullName.removeTextChangedListener(fullNameTextWatcher)
            vb.etFullName.addTextChangedListener(fullNameTextWatcher)

            vb.llEmailAddress.visibility = View.VISIBLE
            vb.etEmailAddress.onFocusChangeListener = formFocusListener
            vb.etEmailAddress.removeTextChangedListener(emailAddressTextWatcher)
            vb.etEmailAddress.addTextChangedListener(emailAddressTextWatcher)

            vm.openRoomListOnComplete = true
        }
        else {
            vb.llFullName.visibility = View.GONE
            vb.llEmailAddress.visibility = View.GONE
            val activeUser = TTLDataManager.getInstance().activeUser
            if (activeUser != null) {
                vb.etFullName.setText(activeUser.fullName)
                vb.etEmailAddress.setText(activeUser.email)
            }
        }
        vb.etMessage.onFocusChangeListener = formFocusListener
        vb.etMessage.removeTextChangedListener(messageTextWatcher)
        vb.etMessage.addTextChangedListener(messageTextWatcher)

        initTopics()

        vb.clScrollViewContent.setOnClickListener { TAPUtils.dismissKeyboard(this) }
        vb.llButtonSendMessage.setOnClickListener { validateSendMessage() }

        if (vm.isShowingFullNameError) {
            vb.etFullName.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clFullNameError.visibility = View.VISIBLE
        }
        if (vm.isShowingEmailAddressError) {
            vb.etEmailAddress.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clEmailAddressError.visibility = View.VISIBLE
        }
        if (vm.isShowingTopicError) {
            vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clTopicError.visibility = View.VISIBLE
        }
        if (vm.isShowingMessageError) {
            vb.etMessage.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clMessageError.visibility = View.VISIBLE
        }
        if (vm.isShowingTopicDropdown) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                vb.llTopic.post {
                    vb.svContainer.smoothScrollTo(0, vb.clNewMessageForm.top + vb.llTopic.top)
                    vb.svContainer.post {
                        Handler(Looper.getMainLooper()).postDelayed({
                            vb.clTopic.post {
                                showTopicDropdown()
                            }
                        }, 300L)
                    }
                }
            }
            else {
                vb.clTopic.post {
                    showTopicDropdown()
                }
            }
        }
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        var isShowingError = false
        if (view == vb.etFullName) {
            isShowingError = vm.isShowingFullNameError
        }
        else if (view == vb.etEmailAddress) {
            isShowingError = vm.isShowingEmailAddressError
        }
        else if (view == vb.etMessage) {
            isShowingError = vm.isShowingMessageError
        }
        if (!isShowingError) {
            if (hasFocus) {
                view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_active)
            }
            else {
                view.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
            }
        }
        if (hasFocus) {
            hideTopicDropdown()
        }
    }

    private val fullNameTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!text.isNullOrEmpty()) {
                formFocusListener.onFocusChange(vb.etFullName, vb.etFullName.hasFocus())
                vb.clFullNameError.visibility = View.GONE
                vm.isShowingFullNameError = false
            }
        }
    }

    private val emailAddressTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!text.isNullOrEmpty()) {
                formFocusListener.onFocusChange(vb.etEmailAddress, vb.etEmailAddress.hasFocus())
                vb.clEmailAddressError.visibility = View.GONE
                vm.isShowingEmailAddressError = false
            }
        }
    }

    private val messageTextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!text.isNullOrEmpty()) {
                formFocusListener.onFocusChange(vb.etMessage, vb.etMessage.hasFocus())
                vb.clMessageError.visibility = View.GONE
                vm.isShowingMessageError = false
            }
        }
    }

    private fun initTopics() {
        fetchTopicList(true)

        topicAdapter = TTLItemDropdownAdapter(
            vm.topics,
            object : TTLItemListInterface {
                override fun onItemSelected(position: Int) {
                    if (position > -1 && position <= vm.topics.size) {
                        vm.selectedTopicIndex = position
                        vb.tvTopic.text = vm.topics[vm.selectedTopicIndex]
                        vb.clTopicError.visibility = View.GONE
                        vm.isShowingTopicError = false
                        hideTopicDropdown()
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
        vb.rvTopicDropdown.adapter = topicAdapter
        vb.rvTopicDropdown.layoutManager = layoutManager
        vb.rvTopicDropdown.setMaxHeight(TAPUtils.dpToPx(resources, 144f))
        vb.clTopic.setOnClickListener {
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

                if (vm.isShowTopicDropdownPending) {
                    vm.isShowTopicDropdownPending = false
                    showTopicDropdown()
                }
            }
        }

        override fun onError(error: TTLErrorModel?) {
            onError(error?.message)
        }
        override fun onError(errorMessage: String?) {
            setGetTopicListAsPending()
            showTopicLoadingFinished()

            if (vm.isShowTopicDropdownPending) {
                vm.isShowTopicDropdownPending = false
                vb.tapCustomSnackbar.show(
                    TapCustomSnackbarView.Companion.Type.ERROR,
                    R.drawable.ttl_ic_info,
                    errorMessage ?: getString(io.taptalk.TapTalk.R.string.tap_error_message_general)
                )
            }
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
        if (vb.clTopicError.visibility != View.VISIBLE) {
            vb.clTopic.background = ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.ttl_bg_text_field_inactive)
        }
        vb.pbSelectTopicLoading.visibility = View.GONE
        vb.ivSelectTopicDropDown.visibility = View.VISIBLE

        if (vm.selectedTopicIndex > -1 && vm.selectedTopicIndex <= vm.topics.size) {
            vb.tvTopic.text = vm.topics[vm.selectedTopicIndex]
        }
    }

    private fun showTopicDropdown() {
        if (vm.topics.isEmpty() || vb.cvTopicDropdown.visibility == View.VISIBLE) {
            vm.isShowTopicDropdownPending = true
            fetchTopicList(false)
            return
        }
        vm.isShowingTopicDropdown = true
        TAPUtils.dismissKeyboard(this)
        val location = IntArray(2)
        vb.clTopic.getLocationInWindow(location)
        vb.cvTopicDropdown.translationY = location[1].toFloat() + (vb.clTopic.height.toFloat() / 2)
        vb.cvTopicDropdown.visibility = View.VISIBLE
        if (!vm.isShowingTopicError) {
            vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_active)
        }
        vb.clTopic.setOnClickListener {
            hideTopicDropdown()
        }
        vb.ivSelectTopicDropDown.animate()
            .rotation(180f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                vb.vDismissDropdown.visibility = View.VISIBLE
                vb.vDismissDropdown.setOnClickListener {
                    hideTopicDropdown()
                }
            }
            .start()
    }

    private fun hideTopicDropdown() {
        if (vb.cvTopicDropdown.visibility != View.VISIBLE) {
            return
        }
        vm.isShowingTopicDropdown = false
        vb.cvTopicDropdown.visibility = View.INVISIBLE
        vb.vDismissDropdown.visibility = View.GONE
        vb.vDismissDropdown.setOnClickListener(null)
        if (!vm.isShowingTopicError) {
            vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        }
        vb.clTopic.setOnClickListener {
            showTopicDropdown()
        }
        vb.ivSelectTopicDropDown.animate()
            .rotation(0f)
            .setDuration(200L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun validateFullName(): Boolean {
        return if (!vb.etFullName.text.isNullOrEmpty()) {
            true
        }
        else {
            vb.etFullName.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clFullNameError.visibility = View.VISIBLE
            vm.isShowingFullNameError = true
            false
        }
    }

    private fun validateEmail(): Boolean {
        try {
            if (vb.etEmailAddress.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(vb.etEmailAddress.text).matches()) {
                return true
            }
            else if (vb.etEmailAddress.text.isNotEmpty()) {
                vb.tvEmailAddressErrorMessage.text = getString(R.string.ttl_error_message_email_invalid)
            }
            else {
                vb.tvEmailAddressErrorMessage.text = getString(R.string.ttl_error_field_required)
            }
            vb.etEmailAddress.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clEmailAddressError.visibility = View.VISIBLE
            vm.isShowingEmailAddressError = true
        }
        catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }

    private fun validateTopic(): Boolean {
        return if (vm.selectedTopicIndex >= 0 && vm.selectedTopicIndex < vm.topics.size) {
            true
        }
        else {
            vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clTopicError.visibility = View.VISIBLE
            vm.isShowingTopicError = true
            false
        }
    }

    private fun validateMessage(): Boolean {
        return if (!vb.etMessage.text.isNullOrEmpty()) {
            true
        }
        else {
            vb.etMessage.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_error)
            vb.clMessageError.visibility = View.VISIBLE
            vm.isShowingMessageError = true
            false
        }
    }

    private fun validateSendMessage() {
        hideTopicDropdown()
        val topicValidated = validateTopic()
        val messageValidated = validateMessage()

        if (!TTLDataManager.getInstance().checkActiveUserExists() || !TTLDataManager.getInstance().checkAccessTokenAvailable()) {
            val fullNameValidated = validateFullName()
            val emailValidated = validateEmail()
            if (fullNameValidated && emailValidated && topicValidated && messageValidated) {
                vb.llButtonSendMessage.setOnClickListener(null)
                showLoading()
                TapTalkLive.authenticateUser(vb.etFullName.text.toString(), vb.etEmailAddress.text.toString(), authenticationListener)
            }
        }
        else {
            if (topicValidated && messageValidated) {
                vb.llButtonSendMessage.setOnClickListener(null)
                showLoading()
                if (!TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
                    TapTalkLive.requestTapTalkAuthTicket(authenticationListener)
                }
                else {
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
        vb.tvButtonSendMessage.visibility = View.GONE
        vb.pbButtonSendMessageLoading.visibility = View.VISIBLE
        vb.etFullName.isEnabled = false
        vb.etEmailAddress.isEnabled = false
        vb.etMessage.isEnabled = false
        vb.clTopic.setOnClickListener(null)
        vb.etFullName.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        vb.etEmailAddress.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        vb.etMessage.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_disabled)
        vb.etFullName.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        vb.etEmailAddress.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        vb.etMessage.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        vb.tvTopic.setTextColor(ContextCompat.getColor(this, R.color.ttlColorTextMedium))
        vb.llButtonSendMessage.setOnClickListener { }
    }

    private fun hideLoading() {
        vb.tvButtonSendMessage.visibility = View.VISIBLE
        vb.pbButtonSendMessageLoading.visibility = View.GONE
        vb.etFullName.isEnabled = true
        vb.etEmailAddress.isEnabled = true
        vb.etMessage.isEnabled = true
        vb.etFullName.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        vb.etEmailAddress.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        vb.etMessage.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        vb.clTopic.background = ContextCompat.getDrawable(this, R.drawable.ttl_bg_text_field_inactive)
        vb.etFullName.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        vb.etEmailAddress.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        vb.etMessage.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        vb.tvTopic.setTextColor(ContextCompat.getColor(this, R.color.ttlFormTextFieldColor))
        vb.clTopic.setOnClickListener {
            showTopicDropdown()
        }
        vb.llButtonSendMessage.setOnClickListener { validateSendMessage() }
    }

    private fun createCase() {
        TTLDataManager.getInstance().createCase(
            vm.topicsMap[vm.topics[vm.selectedTopicIndex]]?.id,
            vb.etMessage.text.toString().trim(),
            createCaseDataView
        )
    }

    private val createCaseDataView = object : TTLDefaultDataView<TTLCreateCaseResponse>() {
        override fun onSuccess(response: TTLCreateCaseResponse?) {
            if (!response?.caseResponse?.tapTalkXCRoomID.isNullOrEmpty()) {
                openCaseChatRoom(response?.caseResponse)
            }
            else {
                Toast.makeText(
                    TapTalkLive.context,
                    getString(R.string.ttl_case_created_chat_room_error),
                    Toast.LENGTH_LONG
                ).show()
                sendCaseCreatedBroadcast(response?.caseResponse)
                finish()
            }
            response?.caseResponse?.let {
                // Save case to map
                TapTalkLive.getCaseMap()[it.tapTalkXCRoomID] = it
            }
            // Refresh case list count
            TTLDataManager.getInstance().getCaseList(object : TTLDefaultDataView<TTLGetCaseListResponse>() {
                override fun onSuccess(response: TTLGetCaseListResponse?) {
                    TTLUtil.processGetCaseListResponse(response, null)
                }
            })
        }

        override fun onError(error: TTLErrorModel?) {
            if (error?.code?.contains("401") == true ||
                TTLDataManager.getInstance().activeUser == null ||
                TTLDataManager.getInstance().accessToken.isNullOrEmpty()
            ) {
                // Re-authenticate if refresh token is expired
                TapTalkLive.authenticateUser(vb.etFullName.text.toString(), vb.etEmailAddress.text.toString(), authenticationListener)
                return
            }
            showDefaultErrorDialog(error?.message)
        }

        override fun onError(errorMessage: String?) {
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(io.taptalk.TapTalk.R.string.tap_error_message_general))
        }
    }

    private fun openCaseChatRoom(caseModel: TTLCaseModel?) {
        TTLDataManager.getInstance().saveActiveUserHasExistingCase(true)
        TapCoreChatRoomManager.getInstance(TAPTALK_INSTANCE_KEY).getChatRoomByXcRoomID(caseModel?.tapTalkXCRoomID, object : TapCoreGetRoomListener() {
            override fun onSuccess(roomModel: TAPRoomModel?) {
                if (vm.openRoomListOnComplete) {
                    TTLCaseListActivity.start(this@TTLCreateCaseFormActivity)
                }
                if (TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
                    //TTLCaseListActivity.start(this@TTLCreateCaseFormActivity)
                    TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(this@TTLCreateCaseFormActivity, roomModel)
                }
                else {
                    TapTalk.connect(TAPTALK_INSTANCE_KEY, object : TapCommonListener() {
                        override fun onSuccess(successMessage: String?) {
                            TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(this@TTLCreateCaseFormActivity, roomModel)
                        }

                        override fun onError(errorCode: String?, errorMessage: String?) {
                            TapUI.getInstance(TAPTALK_INSTANCE_KEY).openChatRoomWithRoomModel(this@TTLCreateCaseFormActivity, roomModel)
                        }
                    })
                }
                sendCaseCreatedBroadcast(caseModel)
                finish()
            }

            override fun onError(errorCode: String?, errorMessage: String?) {
                //showDefaultErrorDialog(errorMessage)
                Toast.makeText(
                    TapTalkLive.context,
                    getString(R.string.ttl_case_created_chat_room_error),
                    Toast.LENGTH_LONG
                ).show()
                sendCaseCreatedBroadcast(caseModel)
                finish()
            }
        })
    }

    private fun sendCaseCreatedBroadcast(caseModel: TTLCaseModel?) {
        val intent = Intent(NEW_CASE_CREATED)
        intent.putExtra(CASE_DETAILS, caseModel)
        LocalBroadcastManager.getInstance(this@TTLCreateCaseFormActivity).sendBroadcast(intent)
    }

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val icon: Int
        val message: String
        if (!TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).hasNetworkConnection(this@TTLCreateCaseFormActivity)) {
            icon = io.taptalk.TapTalk.R.drawable.tap_ic_wifi_off_red
            message = getString(R.string.ttl_error_message_offline)
        }
        else if (!errorMessage.isNullOrEmpty()) {
            icon = R.drawable.ttl_ic_info
            message = errorMessage
        }
        else {
            icon = R.drawable.ttl_ic_info
            message = getString(R.string.ttl_error_message_general)
        }
        vb.tapCustomSnackbar.show(
            TapCustomSnackbarView.Companion.Type.ERROR,
            icon,
            message
        )
//        TapTalkDialog.Builder(this@TTLCreateCaseFormActivity)
//            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
//            .setCancelable(true)
//            .setTitle(getString(R.string.ttl_error))
//            .setMessage(message)
//            .setPrimaryButtonTitle(getString(R.string.ttl_ok))
//            .show()
        vb.llButtonSendMessage.setOnClickListener { validateSendMessage() }
        hideLoading()
    }
}
