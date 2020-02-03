package io.taptalk.taptalklive.CustomBubble

import android.content.Context
import io.taptalk.TapTalk.Interface.TapTalkBaseCustomInterface
import io.taptalk.TapTalk.Model.TAPMessageModel

interface TTLReviewChatBubbleListener : TapTalkBaseCustomInterface {
    fun onReviewButtonTapped(context: Context, message: TAPMessageModel)
}
