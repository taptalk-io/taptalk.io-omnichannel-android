package io.taptalk.taptalklive.helper

import android.app.Activity
import android.content.Intent
import android.text.util.Linkify
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBetterLinkMovementMethod
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPEncryptorManager
import io.taptalk.taptalklive.API.Model.ResponseModel.TTLGetCaseListResponse
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.JSON_TASK_COMPLETED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_STRING
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_URL
import io.taptalk.taptalklive.Const.TTLConstant.ScfPathType.QNA_VIA_API
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Interface.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.TapTalkLive
import java.util.regex.Matcher
import java.util.regex.Pattern

object TTLUtil {
    @JvmStatic
    fun processGetCaseListResponse(response: TTLGetCaseListResponse?, listener: TapCommonListener?) {
        if (response == null) {
            listener?.onError(ClientErrorCodes.ERROR_CODE_OTHERS, "Response is null")
            return
        }
        val cases = response.cases
        val hasActiveCase = !cases.isNullOrEmpty()
        TTLDataManager.getInstance().saveActiveUserHasExistingCase(hasActiveCase)
        if (hasActiveCase) {
            val entities = ArrayList<TAPMessageEntity>()
            for (caseModel in cases) {
                TapTalkLive.getCaseMap()[caseModel.tapTalkXCRoomID] = caseModel
                if (caseModel.tapTalkRoom.lastMessage != null) {
                    val lastMessage = TAPEncryptorManager.getInstance().decryptMessage(caseModel.tapTalkRoom.lastMessage)
                    entities.add(TAPMessageEntity.fromMessageModel(lastMessage))
                }
            }
            if (entities.isNotEmpty()) {
                TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).insertToDatabase(entities, false, object : TAPDatabaseListener<Any?>() {
                    override fun onInsertFinished() {
                        listener?.onSuccess("Successfully saved messages.")
                    }

                    override fun onInsertFailed(errorMessage: String) {
                        listener?.onError(ClientErrorCodes.ERROR_CODE_OTHERS, "Failed to save messages.")
                    }
                })
            }
        }
        else {
            listener?.onSuccess("Result is empty.")
        }
    }

    @JvmStatic
    fun fetchScfPathContentResponse(scfPath: TTLScfPathModel, fetchChildContents: Boolean): HashMap<String, TTLScfPathModel> {
        val scfMap = HashMap<String, TTLScfPathModel>()
        if (scfPath.type == QNA_VIA_API &&
            !scfPath.apiURL.isNullOrEmpty() &&
            (scfPath.contentResponse.isNullOrEmpty())
        ) {
            scfMap[scfPath.apiURL] = scfPath
            val savedContentResponse = TapTalkLive.getContentResponseMap()[scfPath.apiURL]
            if (!savedContentResponse.isNullOrEmpty()) {
                scfPath.contentResponse = savedContentResponse
                val intent = Intent(JSON_TASK_COMPLETED)
                intent.putExtra(JSON_URL, scfPath.apiURL)
                intent.putExtra(JSON_STRING, savedContentResponse)
                LocalBroadcastManager.getInstance(TapTalkLive.context).sendBroadcast(intent)
            }
            else {
                JsonTask(scfPath.apiURL).execute()
            }
        }
        if (fetchChildContents && scfPath.childItems.isNotEmpty()) {
            for (child in scfPath.childItems) {
                fetchScfPathContentResponse(child, false)
                scfMap[child.apiURL] = child
            }
        }
        return scfMap
    }

    @JvmStatic
    fun setLinkDetection(activity: Activity?, textView: TextView?, scfPath: TTLScfPathModel, listener: TTLHomeAdapterInterface?) {
        if (null == activity || null == textView) {
            return
        }
        val movementMethod = TAPBetterLinkMovementMethod.newInstance()
            .setOnLinkClickListener { _: TextView?, url: String?, original: String? ->
                if (null != url && url.contains("mailto:")) {
                    // Email
                    listener?.onFaqContentEmailTapped(scfPath, url)
                    return@setOnLinkClickListener true
                }
                else if (null != url && url.contains("tel:")) {
                    // Phone Number
                    listener?.onFaqContentPhoneTapped(scfPath, url)
                    return@setOnLinkClickListener true
                }
                else if (null != url) {
                    // URL
                    listener?.onFaqContentUrlTapped(scfPath, url)
                    return@setOnLinkClickListener true
                }
                false
            }
            .setOnLinkLongClickListener { _: TextView?, url: String?, original: String? ->
                if (null != url && url.contains("mailto:")) {
                    // Email
                    listener?.onFaqContentEmailLongPressed(scfPath, url)
                }
                else if (null != url && url.contains("tel:")) {
                    // Phone Number
                    listener?.onFaqContentPhoneLongPressed(scfPath, url)
                }
                else if (null != url) {
                    listener?.onFaqContentUrlLongPressed(scfPath, url)
                }
                true
            }
        textView.movementMethod = movementMethod
        textView.isClickable = false
        textView.isLongClickable = false
        Linkify.addLinks(textView, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)

        try {
            val filter = Linkify.TransformFilter { match: Matcher?, url: String ->
                url.replace("/".toRegex(), "")
            }
            val pattern = Pattern.compile("[0-9/]+")
            Linkify.addLinks(textView, pattern, "tel:",
                { s: CharSequence, start: Int, end: Int ->
                    var digitCount = 0
                    for (i in start until end) {
                        if (Character.isDigit(s[i])) {
                            digitCount++
                            if (digitCount >= 7) {
                                return@addLinks true
                            }
                        }
                    }
                    false
                }, filter
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
