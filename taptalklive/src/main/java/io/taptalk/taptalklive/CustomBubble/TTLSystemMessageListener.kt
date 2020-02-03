package io.taptalk.taptalklive.CustomBubble

import android.content.Context
import io.taptalk.TapTalk.Interface.TapTalkBaseCustomInterface
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPUserModel

interface TTLSystemMessageListener : TapTalkBaseCustomInterface {
    fun onClick(context: Context, message: TAPMessageModel)
}
