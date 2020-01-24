package io.taptalk.taptalklive.CustomBubble

import android.view.ViewGroup
import android.widget.TextView
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder
import io.taptalk.taptalklive.R

class TTLSystemMessageViewHolder internal constructor(
        parent: ViewGroup,
        itemLayoutId: Int,
        private val listener: TTLSystemMessageListener) :
    TAPBaseChatViewHolder(parent, itemLayoutId) {

    private val tvMessageBody: TextView = itemView.findViewById(R.id.tv_message)

    override fun onBind(item: TAPMessageModel?, position: Int) {

        tvMessageBody.text = item!!.body

        markMessageAsRead(item, TapTalk.getTaptalkActiveUser())
    }
}
