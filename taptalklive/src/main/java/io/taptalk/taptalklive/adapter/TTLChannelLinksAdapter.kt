package io.taptalk.taptalklive.adapter

import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.FACEBOOK
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.INSTAGRAM
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.LINE
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.TELEGRAM
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.TWITTER
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.WHATSAPP_BA
import io.taptalk.taptalklive.Const.TTLConstant.CaseMedium.WHATSAPP_SME
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.R

class TTLChannelLinksAdapter(
    itemList: List<TTLChannelLinkModel>,
    val listener: TTLItemListInterface?
) : TAPBaseAdapter<TTLChannelLinkModel, TAPBaseViewHolder<TTLChannelLinkModel>>() {

    init {
        items = itemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TTLChannelLinkModel> {
        return ChannelLinkViewHolder(parent, R.layout.ttl_cell_channel_grid)
    }

    internal inner class ChannelLinkViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLChannelLinkModel>(parent, itemLayoutId) {

        private var ivChannelIcon: ImageView = itemView.findViewById(R.id.iv_channel_icon)

        override fun onBind(item: TTLChannelLinkModel?, position: Int) {
            if (item == null) {
                return
            }

            // Set channel icon
            when (item.channel) {
                WHATSAPP_SME -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_whatsapp_sme))
                }
                WHATSAPP_BA -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_whatsapp_ba))
                }
                INSTAGRAM -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_instagram))
                }
                TELEGRAM -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_telegram))
                }
                LINE -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_line))
                }
                TWITTER -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_twitter))
                }
                FACEBOOK -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_messenger))
                }
                else -> {
                    ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_channel_taptalk))
                }
            }

            ivChannelIcon.setOnClickListener {
                listener?.onItemSelected(bindingAdapterPosition)
            }

            if (item.title.isNotEmpty()) {
                ivChannelIcon.setOnLongClickListener {
                    if (itemView.context != null) {
                        Toast.makeText(itemView.context, item.title, Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }
            else {
                ivChannelIcon.setOnLongClickListener(null)
            }
        }
    }
}