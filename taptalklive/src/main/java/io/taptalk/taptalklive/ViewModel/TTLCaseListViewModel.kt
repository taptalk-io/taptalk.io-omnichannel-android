package io.taptalk.taptalklive.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.taptalk.taptalklive.model.TTLCaseListModel

class TTLCaseListViewModel(application: Application) : AndroidViewModel(application) {
    var caseLists: ArrayList<TTLCaseListModel> = ArrayList()
    var caseListMap: HashMap<String, TTLCaseListModel> = HashMap()
    var roomBadgeCount = 0
    var lastBadgeCount = 0
}
