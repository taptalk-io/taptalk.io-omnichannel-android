package io.taptalk.taptalklive.CustomBubble

import android.view.ViewGroup
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter

class TTLSystemMessageBubbleClass(
    customBubbleLayoutRes: Int,
    messageType: Int,
    listener: TTLSystemMessageListener
) : TAPBaseCustomBubble<TTLSystemMessageViewHolder, TTLSystemMessageListener>(
    customBubbleLayoutRes,
    messageType,
    listener
) {
    override fun createCustomViewHolder(
        parent: ViewGroup,
        adapter: TAPMessageAdapter,
        activeUser: TAPUserModel?,
        customBubbleListener: TTLSystemMessageListener
    ): TTLSystemMessageViewHolder {
        return TTLSystemMessageViewHolder(
            parent, customBubbleLayoutRes, customBubbleListener)
    }
}