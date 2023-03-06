package io.taptalk.taptalklive.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.R

class TTLHomeAdapter(
    itemList: List<TTLScfPathModel>/*,
    val listener: ProductListInterface*/
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
//        private var clProductContainer: ConstraintLayout = itemView.findViewById(R.id.cl_product_container)
//        private var rcivProductImage: TAPRoundedCornerImageView = itemView.findViewById(R.id.rciv_product_image)
//        private var ivRadioButtonCheck: ImageView = itemView.findViewById(R.id.iv_radio_button_check)
//        private var tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
//        private var tvProductPrice: TextView = itemView.findViewById(R.id.tv_product_price)
//        private var tvProductStock: TextView = itemView.findViewById(R.id.tv_product_stock)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            // Product image
        }
    }
}
