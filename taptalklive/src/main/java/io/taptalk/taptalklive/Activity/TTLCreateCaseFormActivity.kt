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
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLCreateUserResponse
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLErrorModel
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLRequestAccessTokenResponse
import io.taptalk.taptalklive.API.View.TTLDefaultDataView
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TTLDataManager
import io.taptalk.taptalklive.ViewModel.TTLCreateCaseViewModel
import kotlinx.android.synthetic.main.ttl_activity_create_case_form.*

class TTLCreateCaseFormActivity : AppCompatActivity() {

    private lateinit var vm: TTLCreateCaseViewModel

    private lateinit var glide: RequestManager

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

        initTopicSpinnerAdapter()

        et_full_name.onFocusChangeListener = formFocusListener
        et_email_address.onFocusChangeListener = formFocusListener
        et_message.onFocusChangeListener = formFocusListener

        ll_button_send_message.setOnClickListener{ validateSendMessage() }
    }

    private fun initTopicSpinnerAdapter() {
        val topics: MutableList<String> = ArrayList()
        // TODO GET TOPICS FROM API
        topics.add("Select Topic")
        topics.add("General")
        topics.add("Technical")
        topics.add("Finance")

        val topicSpinnerAdapter = object : ArrayAdapter<String>(
                this, R.layout.ttl_cell_default_spinner_item, topics) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }

        val spinnerAdapterListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.tapFormTextFieldPlaceholderColor))
                } else {
                    tv.setTextColor(ContextCompat.getColor(this@TTLCreateCaseFormActivity, R.color.tapFormTextFieldColor))
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        topicSpinnerAdapter.setDropDownViewResource(R.layout.ttl_cell_default_spinner_dropdown_item)
        sp_select_topic.adapter = topicSpinnerAdapter
        sp_select_topic.onItemSelectedListener = spinnerAdapterListener
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(this, R.drawable.tap_bg_text_field_inactive)
        }
    }

    private fun validateFullName() : Boolean {
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

    private fun validateEmail() : Boolean {
        if (et_email_address.text.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(et_email_address.text).matches()) {
            return  true
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

    private fun validateTopic() : Boolean {
        return if (sp_select_topic.selectedItemPosition != 0) {
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

    private fun validateMessage() : Boolean {
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
        ll_button_send_message.setOnClickListener{ }
    }

    private fun hideLoading() {
        iv_button_send_message.clearAnimation()
        iv_button_send_message.setImageDrawable(ContextCompat.getDrawable(this@TTLCreateCaseFormActivity, R.drawable.tap_ic_send_white))
        tv_button_send_message.visibility = View.VISIBLE
        ll_button_send_message.setOnClickListener{ validateSendMessage() }
    }

    private fun createUser() {
        TTLDataManager.getInstance().createUser(
                et_full_name.text.toString(),
                et_email_address.text.toString(),
                createUserDataView)
    }

    private val createUserDataView = object : TTLDefaultDataView<TTLCreateUserResponse>() {
        override fun startLoading() {
            showLoading()
        }

        override fun onSuccess(response: TTLCreateUserResponse?) {
            if (null != response) {
                TTLDataManager.getInstance().saveAuthTicket(response.ticket)
                requestAccessToken()
            }
        }

        override fun onError(error: TTLErrorModel?) {
            Toast.makeText(this@TTLCreateCaseFormActivity, "createUser onError: ${error?.message}", Toast.LENGTH_LONG).show()
        }

        override fun onError(errorMessage: String?) {
            Toast.makeText(this@TTLCreateCaseFormActivity, "createUser onError: $errorMessage", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@TTLCreateCaseFormActivity, "requestAccessToken onSuccess: ${response.user.userID}", Toast.LENGTH_LONG).show()
            }
        }

        override fun onError(error: TTLErrorModel?) {
            Toast.makeText(this@TTLCreateCaseFormActivity, "requestAccessToken onError: ${error?.message}", Toast.LENGTH_LONG).show()
        }

        override fun onError(errorMessage: String?) {
            Toast.makeText(this@TTLCreateCaseFormActivity, "requestAccessToken onError: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }
}
