package io.taptalk.taptalklive.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.Listener.TTLItemListInterface
import io.taptalk.taptalklive.R

class TTLItemDropdownAdapter(
    itemList: List<String>,
//    var selectedItem: String,
    val listener: TTLItemListInterface
) : TAPBaseAdapter<String, TAPBaseViewHolder<String>>() {

    init {
        items = itemList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TAPBaseViewHolder<String> {
        return TopicViewHolder(parent, R.layout.ttl_cell_item_dropdown_list)
    }

    internal inner class TopicViewHolder(
        parent: ViewGroup,
        itemLayoutId: Int
    ) : TAPBaseViewHolder<String>(parent, itemLayoutId) {

//        private val clContainer: ConstraintLayout = itemView.findViewById(R.id.cl_container)
//        private val ivChecklist: ImageView = itemView.findViewById(R.id.iv_checklist)
        private val tvItemName: TextView = itemView.findViewById(R.id.tv_item_name)

        override fun onBind(item: String?, position: Int) {
            if (null == item) {
                return
            }

            tvItemName.text = item

//            if (bindingAdapterPosition == 0 && itemCount <= 1) {
//                clContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_dropdown_single_ripple)
//            }
//            else if (bindingAdapterPosition == 0) {
//                clContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_dropdown_top_ripple)
//            }
//            else if (bindingAdapterPosition >= itemCount - 1) {
//                clContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_dropdown_bottom_ripple)
//            }
//            else {
//                clContainer.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_dropdown_ripple)
//            }

//            if (selectedItem == item) {
//                ivChecklist.visibility = View.VISIBLE
//                tvItemName.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapColorPrimary))
//            }
//            else {
//                ivChecklist.visibility = View.INVISIBLE
//                tvItemName.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapColorTextDark))
//            }

            itemView.setOnClickListener {
//                updateSelectedItem(item, bindingAdapterPosition)
                listener.onItemSelected(bindingAdapterPosition)
            }
        }
    }

//    fun updateSelectedItem(item: String, position: Int) {
//        val previousSelectedIndex = items.indexOf(selectedItem)
//        selectedItem = item
//        if (previousSelectedIndex > 0 && previousSelectedIndex < itemCount - 1) {
//            notifyItemChanged(previousSelectedIndex)
//        }
//        if (position > 0 && position < itemCount - 1) {
//            notifyItemChanged(position)
//        }
//    }
}
