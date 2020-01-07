package io.taptalk.taptalklive.CustomBubble

import android.view.ViewGroup
import io.taptalk.TapTalk.Helper.TAPBaseCustomBubble
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter

class TTLReviewChatBubbleClass(
    customBubbleLayoutRes: Int,
    messageType: Int,
    listener: TTLReviewChatBubbleListener
) : TAPBaseCustomBubble<TTLReviewChatBubbleViewHolder, TTLReviewChatBubbleListener>(
    customBubbleLayoutRes,
    messageType,
    listener
) {
    override fun createCustomViewHolder(
        parent: ViewGroup,
        adapter: TAPMessageAdapter,
        activeUser: TAPUserModel?,
        customBubbleListener: TTLReviewChatBubbleListener
    ): TTLReviewChatBubbleViewHolder {
        return TTLReviewChatBubbleViewHolder(
            parent, customBubbleLayoutRes, customBubbleListener)
    }
}