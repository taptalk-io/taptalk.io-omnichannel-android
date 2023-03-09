package io.taptalk.taptalklive.Fragment

import android.R.attr.maxLength
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.taptalklive.Const.TTLConstant.Form.REVIEW_CHARACTER_LIMIT
import io.taptalk.taptalklive.R


class TTLReviewBottomSheetFragment(private val reviewBottomSheetListener: ReviewBottomSheetListener) : BottomSheetDialogFragment() {

    private lateinit var clReviewLayoutContainer: ConstraintLayout
    private lateinit var clCommentError: ConstraintLayout
    private lateinit var llButtonSubmitReview: LinearLayout
    private lateinit var ivButtonDismissReview: ImageView
    private lateinit var ivReviewStar1: ImageView
    private lateinit var ivReviewStar2: ImageView
    private lateinit var ivReviewStar3: ImageView
    private lateinit var ivReviewStar4: ImageView
    private lateinit var ivReviewStar5: ImageView
    private lateinit var pbButtonSubmitReviewLoading: ProgressBar
    private lateinit var tvLabelReviewRating: TextView
    private lateinit var tvLabelCharacterCount: TextView
    private lateinit var tvButtonSubmitReview: TextView
    private lateinit var etReviewComment: EditText

    private var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater.inflate(R.layout.ttl_layout_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clReviewLayoutContainer = view.findViewById(R.id.cl_review_layout_container)
        clCommentError = view.findViewById(R.id.cl_comment_error)
        llButtonSubmitReview = view.findViewById(R.id.ll_button_submit_review)
        ivButtonDismissReview = view.findViewById(R.id.iv_button_dismiss_review)
        ivReviewStar1 = view.findViewById(R.id.iv_review_star_1)
        ivReviewStar2 = view.findViewById(R.id.iv_review_star_2)
        ivReviewStar3 = view.findViewById(R.id.iv_review_star_3)
        ivReviewStar4 = view.findViewById(R.id.iv_review_star_4)
        ivReviewStar5 = view.findViewById(R.id.iv_review_star_5)
        pbButtonSubmitReviewLoading = view.findViewById(R.id.pb_button_submit_review_loading)
        tvLabelReviewRating = view.findViewById(R.id.tv_label_review_rating)
        tvLabelCharacterCount = view.findViewById(R.id.tv_label_character_count)
        tvButtonSubmitReview = view.findViewById(R.id.tv_button_submit_review)
        etReviewComment = view.findViewById(R.id.et_review_comment)

        etReviewComment.filters = arrayOf<InputFilter>(LengthFilter(REVIEW_CHARACTER_LIMIT))
        etReviewComment.onFocusChangeListener = formFocusListener
        etReviewComment.addTextChangedListener(textWatcher)
        updateCharacterCount()

        ivButtonDismissReview.setOnClickListener { dismiss() }
        ivReviewStar1.setOnClickListener { updateRating(1) }
        ivReviewStar2.setOnClickListener { updateRating(2) }
        ivReviewStar3.setOnClickListener { updateRating(3) }
        ivReviewStar4.setOnClickListener { updateRating(4) }
        ivReviewStar5.setOnClickListener { updateRating(5) }
        clReviewLayoutContainer.setOnClickListener { dismissKeyboard() }
        llButtonSubmitReview.setOnClickListener(null)
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
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating.text = requireContext().getString(R.string.ttl_rating_1)
            }
            2 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating.text = requireContext().getString(R.string.ttl_rating_2)
            }
            3 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating.text = requireContext().getString(R.string.ttl_rating_3)
            }
            4 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_inactive))
                tvLabelReviewRating.text = requireContext().getString(R.string.ttl_rating_4)
                clCommentError.visibility = View.GONE
                activity?.let {
                    etReviewComment.background = ContextCompat.getDrawable(it, R.drawable.ttl_bg_text_field_inactive)
                }
            }
            5 -> {
                ivReviewStar1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar3.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar4.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                ivReviewStar5.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ttl_ic_star_active))
                tvLabelReviewRating.text = requireContext().getString(R.string.ttl_rating_5)
                clCommentError.visibility = View.GONE
                activity?.let {
                    etReviewComment.background = ContextCompat.getDrawable(it, R.drawable.ttl_bg_text_field_inactive)
                }
            }
        }
        dismissKeyboard()
        activity?.let {
            tvButtonSubmitReview.setTextColor(ContextCompat.getColor(it, R.color.ttlButtonLabelColor))
        }
        llButtonSubmitReview.background = ContextCompat.getDrawable(requireContext(), R.drawable.ttl_bg_button_active_ripple)
        llButtonSubmitReview.setOnClickListener {
            submitReview()
        }
    }

    private val formFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) {
            val bottomSheet : FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.ttl_bg_text_field_active)
        } else {
            view.background = ContextCompat.getDrawable(requireContext(), R.drawable.ttl_bg_text_field_inactive)
        }
    }

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            updateCharacterCount()
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    private fun updateCharacterCount() {
        tvLabelCharacterCount.text = String.format("(%d/%d)", etReviewComment.text.length, REVIEW_CHARACTER_LIMIT)
        if (!etReviewComment.text.isNullOrEmpty()) {
            clCommentError.visibility = View.GONE
        }
    }

    private fun dismissKeyboard() {
        etReviewComment.clearFocus()
        if (activity != null) {
            TAPUtils.dismissKeyboard(activity, etReviewComment)
        }
    }

    private fun submitReview() {
        if (rating <= 3 && etReviewComment.text.isNullOrEmpty()) {
            clCommentError.visibility = View.VISIBLE
            activity?.let {
                etReviewComment.background = ContextCompat.getDrawable(it, R.drawable.ttl_bg_text_field_error)
            }
            return
        }
        ivReviewStar1.setOnClickListener(null)
        ivReviewStar2.setOnClickListener(null)
        ivReviewStar3.setOnClickListener(null)
        ivReviewStar4.setOnClickListener(null)
        ivReviewStar5.setOnClickListener(null)
        llButtonSubmitReview.setOnClickListener(null)
        etReviewComment.isEnabled = false
        etReviewComment.clearFocus()
        activity?.let {
            etReviewComment.setTextColor(ContextCompat.getColor(it, R.color.ttlFormTextFieldPlaceholderColor))
            etReviewComment.background = ContextCompat.getDrawable(it, R.drawable.ttl_bg_text_field_disabled)
        }
        pbButtonSubmitReviewLoading.visibility = View.VISIBLE
        // TODO: TEST
//        reviewBottomSheetListener.onSubmitReviewButtonTapped(rating, etReviewComment.text.toString())
    }

    fun onSubmitReviewFailed() {
        ivReviewStar1.setOnClickListener { updateRating(1) }
        ivReviewStar2.setOnClickListener { updateRating(2) }
        ivReviewStar3.setOnClickListener { updateRating(3) }
        ivReviewStar4.setOnClickListener { updateRating(4) }
        ivReviewStar5.setOnClickListener { updateRating(5) }
        llButtonSubmitReview.setOnClickListener { submitReview() }
        etReviewComment.isEnabled = true
        activity?.let {
            etReviewComment.setTextColor(ContextCompat.getColor(it, R.color.ttlFormTextFieldColor))
            etReviewComment.background = ContextCompat.getDrawable(it, R.drawable.ttl_bg_text_field_inactive)
        }
        pbButtonSubmitReviewLoading.visibility = View.GONE
    }

    interface ReviewBottomSheetListener {
        fun onSubmitReviewButtonTapped(rating: Int, comment: String)
        fun onBottomSheetCollapsed()
    }
}
