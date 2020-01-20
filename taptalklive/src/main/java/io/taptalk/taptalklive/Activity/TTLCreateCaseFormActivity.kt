package io.taptalk.taptalklive.Activity

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetTopicListResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.Manager.TTLDataManager
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

    private fun initViewModel() {
        vm = ViewModelProviders.of(this).get(TTLCreateCaseViewModel::class.java)
    }

    private fun initView() {
        window?.setBackgroundDrawable(null)

        et_full_name.onFocusChangeListener = formFocusListener
        et_email_address.onFocusChangeListener = formFocusListener
        et_message.onFocusChangeListener = formFocusListener

        initTopicSpinnerAdapter()

        ll_button_send_message.setOnClickListener { validateSendMessage() }
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_inactive)
        }
    }

    private fun initTopicSpinnerAdapter() {
        val spinnerPlaceholder = getString(R.string.ttl_select_topic)
        vm.topics.add(spinnerPlaceholder)

        TAPUtils.getInstance().rotateAnimateInfinitely(this@TTLCreateCaseFormActivity, iv_select_topic_loading)

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
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.tapFormTextFieldPlaceholderColor))
                } else {
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.tapFormTextFieldColor))
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
        TAPNetworkStateManager.getInstance().addNetworkListener(object : TapTalkNetworkInterface {
            override fun onNetworkAvailable() {
                getTopicList()
                TAPNetworkStateManager.getInstance().removeNetworkListener(this)
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
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
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
                    .setMessage(getString(R.string.tap_error_invalid_email_address))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show()
        } else {
            TapTalkDialog.Builder(this)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setMessage(getString(R.string.ttl_error_message_email_empty))
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
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
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
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
                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                    .show()
            false
        }
    }

    private fun validateSendMessage() {
        if (validateFullName() && validateEmail() && validateTopic() && validateMessage()) {
            createUser()
        }
    }

    private fun showLoading() {
        tv_button_send_message.visibility = View.GONE
        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.tap_ic_loading_progress_circle_white))
        TAPUtils.getInstance().rotateAnimateInfinitely(this@TTLCreateCaseFormActivity, iv_button_send_message)
        ll_button_send_message.setOnClickListener { }
    }

    private fun hideLoading() {
        iv_button_send_message.clearAnimation()
        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.tap_ic_send_white))
        tv_button_send_message.visibility = View.VISIBLE
        ll_button_send_message.setOnClickListener { validateSendMessage() }
    }

    // TODO REQUEST TAPLIVE USER FROM CLIENT
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
            showDefaultErrorDialog(if (BuildConfig.DEBUG) errorMessage else getString(R.string.tap_error_message_general))
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
                // TODO GET TAPTALK AUTH TICKET, SEND MESSAGE
                hideLoading()
                Toast.makeText(this@TTLCreateCaseFormActivity, "requestAccessToken onSuccess: ${response.user.userID}", Toast.LENGTH_LONG).show()
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

    private fun showDefaultErrorDialog(errorMessage: String?) {
        val message = if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(this@TTLCreateCaseFormActivity)) {
            getString(R.string.tap_no_internet_show_error)
        } else if (!errorMessage.isNullOrEmpty()) {
            errorMessage
        } else  {
            getString(R.string.tap_error_message_general)
        }
        TapTalkDialog.Builder(this@TTLCreateCaseFormActivity)
            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
            .setTitle(getString(R.string.tap_error))
            .setMessage(message)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .show()
    }
}
