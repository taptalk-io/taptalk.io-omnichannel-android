package io.taptalk.taptalklive.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.R

class TTLHomeAdapter(
    itemList: List<TTLScfPathModel>,
    val listener: TTLHomeAdapterInterface?
) : TAPBaseAdapter<TTLScfPathModel, TAPBaseViewHolder<TTLScfPathModel>>() {

    init {
        items = itemList
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TTLScfPathModel> {
        return HeaderViewHolder(parent, R.layout.ttl_cell_home_header)
    }

    internal inner class HeaderViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var llButtonMessageDirectly: LinearLayout = itemView.findViewById(R.id.ll_button_message_directly)
        private var llButtonNewMessage: LinearLayout = itemView.findViewById(R.id.ll_button_new_message)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvButtonSeeAllMessages: TextView = itemView.findViewById(R.id.tv_button_see_all_messages)
        private var rvChannelLinks: RecyclerView = itemView.findViewById(R.id.rv_channel_links)
        private var rvMessage: RecyclerView = itemView.findViewById(R.id.rv_message)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
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
