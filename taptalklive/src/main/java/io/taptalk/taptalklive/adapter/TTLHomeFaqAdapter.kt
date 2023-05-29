package io.taptalk.taptalklive.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.ScfPathType.TALK_TO_AGENT
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.model.TTLCaseListModel

class TTLHomeFaqAdapter(
    val context: Context,
    itemList: List<TTLScfPathModel>,
    val listener: TTLHomeAdapterInterface?,
    val containsHeader: Boolean
) : TAPBaseAdapter<TTLScfPathModel, TAPBaseViewHolder<TTLScfPathModel>>() {

    companion object {
        const val HEADER = 0
        const val FAQ_PARENT = 1
        const val FAQ_CHILD = 2
    }

    private var filteredChannelLinks: ArrayList<TTLChannelLinkModel>? = null
    private var channelLinksAdapter: TTLChannelLinksAdapter? = null
    private var channelLinksListener: TTLItemListInterface? = null

    private var caseListArray: ArrayList<TTLCaseListModel>? = null
    private var caseListAdapter: TTLCaseListAdapter? = null
    private var caseListListener: TTLCaseListAdapter.TTLCaseListInterface? = null

    init {
        items = itemList

        if (containsHeader) {
            // Setup channel links
            val channelLinks = TTLDataManager.getInstance().channelLinks
            filteredChannelLinks = ArrayList()
            if (!channelLinks.isNullOrEmpty()) {
                for (channel in channelLinks) {
                    if (channel.isEnabled) {
                        filteredChannelLinks!!.add(channel)
                    }
                }
            }
            channelLinksListener = object: TTLItemListInterface {
                override fun onItemSelected(position: Int) {
                    if (position >= 0 && filteredChannelLinks!!.size > position) {
                        listener?.onChannelLinkSelected(filteredChannelLinks!![position], position)
                    }
                }
            }
            channelLinksAdapter = TTLChannelLinksAdapter(filteredChannelLinks!!, channelLinksListener)

            // Setup case list
            caseListArray = ArrayList()
            caseListListener = object: TTLCaseListAdapter.TTLCaseListInterface {
                override fun onCaseSelected(caseListModel: TTLCaseListModel?, position: Int) {
                    if (caseListModel != null) {
                        listener?.onCaseListTapped(caseListModel)
                    }
                }
            }
            caseListAdapter = TTLCaseListAdapter(caseListArray!!, Glide.with(context), caseListListener!!)
            refreshLatestCaseList()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val buffer = if (containsHeader) 0 else 1
        return position + buffer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TTLScfPathModel> {
        when (viewType) {
            HEADER -> {
                return HeaderViewHolder(parent, R.layout.ttl_cell_home_header)
            }
            FAQ_PARENT -> {
                return FaqParentViewHolder(parent, R.layout.ttl_cell_faq_parent)
            }
            else -> {
                return FaqChildViewHolder(parent, R.layout.ttl_cell_faq_child)
            }
        }
    }

    internal inner class HeaderViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var flOr: FrameLayout = itemView.findViewById(R.id.fl_or)
        private var clChannelAndDirectMessage: ConstraintLayout = itemView.findViewById(R.id.cl_channel_and_direct_message)
        private var clCaseList: ConstraintLayout = itemView.findViewById(R.id.cl_case_list)
        private var llButtonMessageDirectly: LinearLayout = itemView.findViewById(R.id.ll_button_message_directly)
        private var llButtonNewMessage: LinearLayout = itemView.findViewById(R.id.ll_button_new_message)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvLabelChannel: TextView = itemView.findViewById(R.id.tv_label_channel)
        private var tvButtonSeeAllMessages: TextView = itemView.findViewById(R.id.tv_button_see_all_messages)
        private var rvChannelLinks: RecyclerView = itemView.findViewById(R.id.rv_channel_links)
        private var rvCaseList: TAPChatRecyclerView = itemView.findViewById(R.id.rv_case_list)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null || !containsHeader) {
                return
            }

            if (filteredChannelLinks?.isEmpty() == true) {
                // Hide channel links
                tvLabelChannel.visibility = View.GONE
                rvChannelLinks.visibility = View.GONE
                rvChannelLinks.adapter = null
                rvChannelLinks.layoutManager = null
            }
            else {
                // Show channel links
                tvLabelChannel.visibility = View.VISIBLE
                rvChannelLinks.visibility = View.VISIBLE
                rvChannelLinks.adapter = channelLinksAdapter
                if (rvChannelLinks.layoutManager == null) {
                    rvChannelLinks.layoutManager = object : GridLayoutManager(context, 5) {
                        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                            try {
                                super.onLayoutChildren(recycler, state)
                            }
                            catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            val messageAnimator = rvCaseList.itemAnimator as SimpleItemAnimator?
            if (null != messageAnimator) {
                messageAnimator.supportsChangeAnimations = false
            }
            if (caseListArray?.isEmpty() == true) {
                // Hide latest message
                clCaseList.visibility = View.GONE
                llButtonMessageDirectly.visibility = View.VISIBLE
                rvCaseList.adapter = null
                rvCaseList.layoutManager = null
            }
            else {
                // Show latest message
                clCaseList.visibility = View.VISIBLE
                llButtonMessageDirectly.visibility = View.GONE
                rvCaseList.adapter = caseListAdapter
                if (rvCaseList.layoutManager == null) {
                    rvCaseList.layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
                        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                            try {
                                super.onLayoutChildren(recycler, state)
                            }
                            catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            if (rvChannelLinks.visibility == View.VISIBLE && llButtonMessageDirectly.visibility == View.VISIBLE) {
                // Show channel links and message us directly button
                clChannelAndDirectMessage.visibility = View.VISIBLE
                flOr.visibility = View.VISIBLE
            }
            else if (rvChannelLinks.visibility == View.GONE && llButtonMessageDirectly.visibility == View.GONE) {
                // Hide channel links and message us directly button
                clChannelAndDirectMessage.visibility = View.GONE
                flOr.visibility = View.GONE
            }
            else {
                // Show either channel links or message us directly button
                clChannelAndDirectMessage.visibility = View.VISIBLE
                flOr.visibility = View.GONE
            }

            ivButtonClose.setOnClickListener {
                listener?.onCloseButtonTapped()
            }
            llButtonMessageDirectly.setOnClickListener {
                listener?.onNewMessageButtonTapped()
            }
            llButtonNewMessage.setOnClickListener {
                listener?.onNewMessageButtonTapped()
            }
            tvButtonSeeAllMessages.setOnClickListener {
                listener?.onSeeAllMessagesButtonTapped()
            }
        }
    }

    internal inner class FaqParentViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var llButtonTalkToAgent: LinearLayout = itemView.findViewById(R.id.ll_button_talk_to_agent)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvLabelFaq: TextView = itemView.findViewById(R.id.tv_label_faq)
        private var tvFaqTitle: TextView = itemView.findViewById(R.id.tv_faq_title)
        private var tvFaqContent: TextView = itemView.findViewById(R.id.tv_faq_content)
        private var vHeaderBackground: View = itemView.findViewById(R.id.v_header_background)
        private var vBottomDecoration: View = itemView.findViewById(R.id.v_bottom_decoration)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            if (containsHeader) {
                // Home page FAQ parent
                vHeaderBackground.visibility = View.GONE
                ivButtonClose.visibility = View.GONE
                tvLabelFaq.visibility = View.VISIBLE
                ivButtonClose.setOnClickListener(null)
                if (tvFaqContent.layoutParams is ViewGroup.MarginLayoutParams) {
                    val layoutParams = tvFaqContent.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = TAPUtils.dpToPx(itemView.context.resources, 12f)
                    layoutParams.leftMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    layoutParams.rightMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    tvFaqContent.requestLayout()
                }
            }
            else {
                // FAQ child details page
                vHeaderBackground.visibility = View.VISIBLE
                ivButtonClose.visibility = View.VISIBLE
                tvLabelFaq.visibility = View.GONE
                ivButtonClose.setOnClickListener {
                    listener?.onCloseButtonTapped()
                }
                if (tvFaqContent.layoutParams is ViewGroup.MarginLayoutParams) {
                    val layoutParams = tvFaqContent.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    layoutParams.leftMargin = 0
                    layoutParams.rightMargin = 0
                    tvFaqContent.requestLayout()
                }
            }

            tvFaqTitle.text = item.title
            tvFaqContent.text = item.content

            if (item.type == TALK_TO_AGENT) {
                llButtonTalkToAgent.visibility = View.VISIBLE
                llButtonTalkToAgent.setOnClickListener {
                    listener?.onTalkToAgentButtonTapped(item)
                }
            }
            else {
                llButtonTalkToAgent.visibility = View.GONE
                llButtonTalkToAgent.setOnClickListener(null)
            }

            if (bindingAdapterPosition >= itemCount - 1) {
                vBottomDecoration.visibility = View.VISIBLE
            }
            else {
                vBottomDecoration.visibility = View.GONE
            }
        }
    }

    internal inner class FaqChildViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var clFaqChildContainer: ConstraintLayout = itemView.findViewById(R.id.cl_faq_child_container)
        private var tvFaqChildTitle: TextView = itemView.findViewById(R.id.tv_faq_child_title)
        private var tvFaqChildContent: TextView = itemView.findViewById(R.id.tv_faq_child_content)
        private var vBottomDecoration: View = itemView.findViewById(R.id.v_bottom_decoration)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            tvFaqChildTitle.text = item.title
            tvFaqChildContent.text = item.content

            if (bindingAdapterPosition >= itemCount - 1) {
                vBottomDecoration.visibility = View.VISIBLE
            }
            else {
                vBottomDecoration.visibility = View.GONE
            }

            clFaqChildContainer.setOnClickListener {
                listener?.onFaqChildTapped(item)
            }
        }
    }

    fun refreshLatestCaseList() {
        if (context !is Activity) {
            return
        }
        TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getRoomList(true, object: TAPDatabaseListener<TAPMessageEntity>() {
            override fun onSelectedRoomList(
                entities: MutableList<TAPMessageEntity>?,
                unreadMap: MutableMap<String, Int>?,
                mentionMap: MutableMap<String, Int>?
            ) {
                if (!entities.isNullOrEmpty()) {
                    val entity = entities[0]
                    val message = TAPMessageModel.fromMessageEntity(entity)
                    val caseList = TTLCaseListModel(message)
                    if (null != unreadMap && null != unreadMap[entity.roomID]) {
                        caseList.unreadCount = unreadMap[entity.roomID]!!
                    }
                    if (null != mentionMap && null != mentionMap[entity.roomID]) {
                        caseList.unreadMentions = mentionMap[entity.roomID]!!
                    }
                    caseListArray?.clear()
                    caseListArray?.add(caseList)
                }

                context.runOnUiThread {
                    notifyItemChanged(HEADER)
                }
            }
        })
    }

    fun setLastMessage(message: TAPMessageModel) {
        if (context !is Activity) {
            return
        }
        if (caseListArray?.isNotEmpty() == true && caseListArray!![0].lastMessage.room.roomID == message.room.roomID) {
            // Received message in the same room as previous case list
            val previousCaseList = caseListArray!![0]
            val previousMessage = caseListArray!![0].lastMessage
            if (previousMessage.localID == message.localID) {
                // Update case list's last message data
                previousMessage.updateValue(message)
            }
            else if (previousMessage.created < message.created) {
                // Update case list's last message with the new message from socket
                previousCaseList.lastMessage = message

                val activeUser = TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).activeUser
                if (message.user.userID != activeUser.userID) {
                    // Add unread count by 1 if sender is not self
                    previousCaseList.unreadCount++

                    if (TAPUtils.isActiveUserMentioned(message, activeUser)) {
                        // Show mention badge if user is mentioned
                        previousCaseList.unreadMentions++
                    }
                }

            }
            context.runOnUiThread {
                notifyItemChanged(HEADER)
            }
        }
        else {
            // Received new message in a different case list
            val caseList = TTLCaseListModel(message)
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadCountPerRoom(
                message.room.roomID,
                object: TAPDatabaseListener<TAPMessageEntity>() {
                    override fun onCountedUnreadCount(
                        roomID: String?,
                        unreadCount: Int,
                        mentionCount: Int
                    ) {
                        if (roomID == message.room.roomID) {
                            caseList.unreadCount = unreadCount
                            caseList.unreadMentions = mentionCount
                        }

                        caseListArray?.clear()
                        caseListArray?.add(caseList)

                        context.runOnUiThread {
                            notifyItemChanged(HEADER)
                        }
                    }
                }
            )
        }
    }
}
