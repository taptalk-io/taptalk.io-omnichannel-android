package io.taptalk.taptalklive.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.taptalk.TapTalk.Model.TAPMessageModel

class TTLReviewViewModel(application: Application) : AndroidViewModel(application) {
    var isReviewSubmitting = false
    var caseID = -1
    var pendingRating = 0
    var pendingComment = ""
    lateinit var message : TAPMessageModel
}
