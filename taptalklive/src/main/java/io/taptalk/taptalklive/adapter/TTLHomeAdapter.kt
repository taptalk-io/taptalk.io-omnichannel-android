package io.taptalk.taptalklive.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.model.TTLCaseListModel

class TTLHomeAdapter(
    context: Context,
    itemList: List<TTLScfPathModel>,
    val listener: TTLHomeAdapterInterface?
) : TAPBaseAdapter<TTLScfPathModel, TAPBaseViewHolder<TTLScfPathModel>>() {

    companion object {
        const val INDEX_HEADER = 0
        const val INDEX_SCF_PARENT = 1
    }

    private val filteredChannelLinks: ArrayList<TTLChannelLinkModel>
    private val channelLinksAdapter: TTLChannelLinksAdapter
    private val channelLinksListener: TTLItemListInterface
    private val channelLinksLayoutManager: GridLayoutManager

    private val caseListArray: ArrayList<TTLCaseListModel>
    private val caseListAdapter: TTLCaseListAdapter
    private val caseListListener: TTLCaseListAdapter.TTLCaseListInterface
    private val caseListLayoutManager: LinearLayoutManager

    init {
        items = itemList

        // Setup channel links
        val channelLinks = TTLDataManager.getInstance().channelLinks
        filteredChannelLinks = ArrayList()
        for (channel in channelLinks) {
            if (channel.isEnabled) {
                filteredChannelLinks.add(channel)
            }
        }
        channelLinksListener = object: TTLItemListInterface {
            override fun onItemSelected(position: Int) {
                if (position >= 0 && filteredChannelLinks.size > position) {
                    listener?.onChannelLinkSelected(filteredChannelLinks[position], position)
                }
            }
        }
        channelLinksAdapter = TTLChannelLinksAdapter(filteredChannelLinks, channelLinksListener)
        channelLinksLayoutManager = object : GridLayoutManager(context, 5) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }

        // Setup case list
        caseListArray = ArrayList()
        caseListListener = object: TTLCaseListAdapter.TTLCaseListInterface {
            override fun onCaseSelected(caseListModel: TTLCaseListModel?, position: Int) {
                if (caseListModel != null) {
                    listener?.onCaseListTapped(caseListModel)
                }
            }
        }
        caseListAdapter = TTLCaseListAdapter(caseListArray, Glide.with(context), caseListListener)
        caseListLayoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
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
                    caseListArray.add(caseList)
                }
                notifyItemChanged(INDEX_HEADER)
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TTLScfPathModel> {
        return HeaderViewHolder(parent, R.layout.ttl_cell_home_header)
    }

    internal inner class HeaderViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var flOr: FrameLayout = itemView.findViewById(R.id.fl_or)
        private var llButtonMessageDirectly: LinearLayout = itemView.findViewById(R.id.ll_button_message_directly)
        private var llButtonNewMessage: LinearLayout = itemView.findViewById(R.id.ll_button_new_message)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvLabelChannel: TextView = itemView.findViewById(R.id.tv_label_channel)
        private var tvButtonSeeAllMessages: TextView = itemView.findViewById(R.id.tv_button_see_all_messages)
        private var rvChannelLinks: RecyclerView = itemView.findViewById(R.id.rv_channel_links)
        private var rvCaseList: TAPChatRecyclerView = itemView.findViewById(R.id.rv_case_list)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            if (filteredChannelLinks.isEmpty()) {
                // Hide channel links
//                tvLabelChannel.visibility = View.GONE
//                rvChannelLinks.visibility = View.GONE
//                flOr.visibility = View.GONE
                rvChannelLinks.adapter = null
                rvChannelLinks.layoutManager = null
            }
            else {
                // Show channel links
//                tvLabelChannel.visibility = View.VISIBLE
//                rvChannelLinks.visibility = View.VISIBLE
//                flOr.visibility = View.VISIBLE
                rvChannelLinks.adapter = channelLinksAdapter
                rvChannelLinks.layoutManager = channelLinksLayoutManager
            }

            val messageAnimator = rvCaseList.itemAnimator as SimpleItemAnimator?
            if (null != messageAnimator) {
                messageAnimator.supportsChangeAnimations = false
            }
            if (caseListArray.isEmpty()) {
                // Hide latest case list
                rvCaseList.adapter = null
                rvCaseList.layoutManager = null
            }
            else {
                // Show latest case list
                rvCaseList.adapter = caseListAdapter
                rvCaseList.layoutManager = caseListLayoutManager
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
}
