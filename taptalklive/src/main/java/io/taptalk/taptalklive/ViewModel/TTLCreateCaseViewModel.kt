package io.taptalk.taptalklive.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.taptalk.taptalklive.API.Model.TTLTopic

class TTLCreateCaseViewModel(application: Application) : AndroidViewModel(application) {
    var topicsMap = LinkedHashMap<String, TTLTopic>()
    var topics =  ArrayList<String>()
}
