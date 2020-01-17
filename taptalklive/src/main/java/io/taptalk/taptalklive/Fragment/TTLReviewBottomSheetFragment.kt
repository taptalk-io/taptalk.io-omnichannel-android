package io.taptalk.taptalklive.Fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.taptalklive.R

class TTLReviewBottomSheetFragment(private val reviewBottomSheetListener: ReviewBottomSheetListener) : BottomSheetDialogFragment() {

    private lateinit var flButtonSubmitReview : FrameLayout
    private lateinit var ivButtonDismissReview : ImageView
    private lateinit var ivReviewStar1 : ImageView
    private lateinit var ivReviewStar2 : ImageView
    private lateinit var ivReviewStar3 : ImageView
    private lateinit var ivReviewStar4 : ImageView
    private lateinit var ivReviewStar5 : ImageView
    private lateinit var ivButtonSubmitReviewLoading : ImageView
    private lateinit var tvLabelReviewRating : TextView
    private lateinit var tvButtonSubmitReview : TextView
    private lateinit var etReviewComment : EditText

    private var rating = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ttl_layout_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flButtonSubmitReview = view.findViewById(R.id.fl_button_submit_review)
        ivButtonDismissReview = view.findViewById(R.id.iv_button_dismiss_review)
        ivReviewStar1 = view.findViewById(R.id.iv_review_star_1)
        ivReviewStar2 = view.findViewById(R.id.iv_review_star_2)
        ivReviewStar3 = view.findViewById(R.id.iv_review_star_3)
        ivReviewStar4 = view.findViewById(R.id.iv_review_star_4)
        ivReviewStar5 = view.findViewById(R.id.iv_review_star_5)
        ivButtonSubmitReviewLoading = view.findViewById(R.id.iv_button_submit_review_loading)
        tvLabelReviewRating = view.findViewById(R.id.tv_label_review_rating)
        tvButtonSubmitReview = view.findViewById(R.id.tv_button_submit_review)
        etReviewComment = view.findViewById(R.id.et_review_comment)

        etReviewComment.onFocusChangeListener = formFocusListener

        ivButtonDismissReview.setOnClickListener { dismiss() }
        ivReviewStar1.setOnClickListener { updateRating(1) }
        ivReviewStar2.setOnClickListener { updateRating(2) }
        ivReviewStar3.setOnClickListener { updateRating(3) }
        ivReviewStar4.setOnClickListener { updateRating(4) }
        ivReviewStar5.setOnClickListener { updateRating(5) }
        flButtonSubmitReview.setOnClickListener { }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        reviewBottomSheetListener.onBottomSheetCollapsed()
    }

    private fun updateRating(rating: Int) {
        if (null == context) {
            return
        }
        this.rating = rating
        when (rating) {
            1 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating. text = context!!.getString(R.string.ttl_rating_1)
            }
            2 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating. text = context!!.getString(R.string.ttl_rating_2)
            }
            3 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating. text = context!!.getString(R.string.ttl_rating_3)
            }
            4 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating. text = context!!.getString(R.string.ttl_rating_4)
            }
            5 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ttl_ic_star_active))
                tvLabelReviewRating. text = context!!.getString(R.string.ttl_rating_5)
            }
        }
        flButtonSubmitReview.background = ContextCompat.getDrawable(context!!, R.drawable.ttl_bg_button_active_ripple)
        flButtonSubmitReview.setOnClickListener { submitReview() }
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            view.background = ContextCompat.getDrawable(context!!, R.drawable.ttl_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(context!!, R.drawable.ttl_bg_text_field_inactive)
        }
    }

    private fun submitReview() {
        tvButtonSubmitReview.visibility = View.GONE
        ivButtonSubmitReviewLoading.visibility = View.VISIBLE
        context?.let {
            TAPUtils.getInstance().rotateAnimateInfinitely(context, ivButtonSubmitReviewLoading)
        }
        reviewBottomSheetListener.onSubmitReviewButtonTapped(rating, etReviewComment.text.toString())
    }

    interface ReviewBottomSheetListener {
        fun onSubmitReviewButtonTapped(rating: Int, comment: String)
        fun onBottomSheetCollapsed()
    }
}
