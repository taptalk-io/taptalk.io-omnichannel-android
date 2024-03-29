package io.taptalk.taptalklive.helper

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
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
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.TapTalkLive

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
                val lastMessage = TAPEncryptorManager.getInstance().decryptMessage(caseModel.tapTalkRoom.lastMessage)
                entities.add(TAPMessageEntity.fromMessageModel(lastMessage))
            }
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).insertToDatabase(entities, false, object : TAPDatabaseListener<Any?>() {
                override fun onInsertFinished() {
                    listener?.onSuccess("Successfully saved messages.")
                }

                override fun onInsertFailed(errorMessage: String) {
                    listener?.onError(ClientErrorCodes.ERROR_CODE_OTHERS, "Failed to save messages.")
                }
            })
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
}
