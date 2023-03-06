package io.taptalk.taptalklive.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R

class TTLHomeAdapter(
    context: Context,
    itemList: List<TTLScfPathModel>,
    val listener: TTLHomeAdapterInterface?
) : TAPBaseAdapter<TTLScfPathModel, TAPBaseViewHolder<TTLScfPathModel>>() {

    private val filteredChannelLinks: ArrayList<TTLChannelLinkModel>?
    private val channelLinksAdapter: TTLChannelLinksAdapter
    private val channelLinksListener: TTLItemListInterface
    private val channelLinksLayoutManager: GridLayoutManager

    init {
        items = itemList

        val channelLinks = TTLDataManager.getInstance().channelLinks
        filteredChannelLinks = ArrayList()
        for (channel in channelLinks) {
            if (channel.isEnabled) {
                filteredChannelLinks.add(channel)
            }
        }

        channelLinksListener = object: TTLItemListInterface {
            override fun onItemSelected(position: Int) {
                if (position > 0 && filteredChannelLinks.size > position) {
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
        private var rvMessage: RecyclerView = itemView.findViewById(R.id.rv_message)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            if (filteredChannelLinks.isNullOrEmpty()) {
//                tvLabelChannel.visibility = View.GONE
//                rvChannelLinks.visibility = View.GONE
//                flOr.visibility = View.GONE
                rvChannelLinks.adapter = null
                rvChannelLinks.layoutManager = null
            }
            else {
//                tvLabelChannel.visibility = View.VISIBLE
//                rvChannelLinks.visibility = View.VISIBLE
//                flOr.visibility = View.VISIBLE
                rvChannelLinks.adapter = channelLinksAdapter
                rvChannelLinks.layoutManager = channelLinksLayoutManager
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
