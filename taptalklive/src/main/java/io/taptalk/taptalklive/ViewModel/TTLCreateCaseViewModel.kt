package io.taptalk.taptalklive.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.taptalk.taptalklive.API.Model.TTLTopicModel

class TTLCreateCaseViewModel(application: Application) : AndroidViewModel(application) {
    var topicsMap = LinkedHashMap<String, TTLTopicModel>()
    var topics =  ArrayList<String>()
    var selectedTopicIndex = -1
    var showCloseButton = false
    var openRoomListOnComplete = false
    var isShowTopicDropdownPending = false
    var isShowingTopicDropdown = false
    var isShowingFullNameError = false
    var isShowingEmailAddressError = false
    var isShowingTopicError = false
    var isShowingMessageError = false
}
