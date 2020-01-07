package io.taptalk.taptalklive.ViewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.TAPUserModel

// TODO IMPLEMENT VIEW MODEL
class TTLCreateCaseViewModel(application: Application) : AndroidViewModel(application) {
    var formCheck = intArrayOf(0, 0, 0, 0)
    var countryID = 0
    var fontResourceId = 0
    var textFieldFontColor = 0
    var textFieldFontColorHint = 0
    var clickableLabelFontColor = 0
    var isUpdatingProfile = false
    var isUploadingProfilePicture = false
    var countryCallingCode = "62"
    var countryFlagUrl: String? = null
    var currentProfilePicture: String? = null
    var profilePictureUri: Uri? = null
    var myUserModel: TAPUserModel? = null
        get() = if (null == field) TAPChatManager.getInstance().activeUser else field

}