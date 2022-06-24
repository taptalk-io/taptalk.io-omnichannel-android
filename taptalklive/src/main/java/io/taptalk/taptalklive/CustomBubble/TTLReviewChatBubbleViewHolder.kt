package io.taptalk.taptalklive.CustomBubble

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder
import io.taptalk.taptalklive.BuildConfig
import io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW
import io.taptalk.taptalklive.Const.TTLConstant.MessageType.TYPE_REVIEW_SUBMITTED
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.R

class TTLReviewChatBubbleViewHolder internal constructor(
        parent: ViewGroup,
        itemLayoutId: Int,
        private val listener: TTLReviewChatBubbleListener) :
    TAPBaseChatViewHolder(parent, itemLayoutId) {

    private val clBubble: ConstraintLayout = itemView.findViewById(R.id.cl_bubble)
    private val flBubble: FrameLayout = itemView.findViewById(R.id.fl_bubble)
    private val llButtonReview: LinearLayout = itemView.findViewById(R.id.ll_button_review)
    private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
    private val ivMessageStatus: ImageView = itemView.findViewById(R.id.iv_message_status)
    private val ivSending: ImageView = itemView.findViewById(R.id.iv_sending)
    private val ivButtonReview: ImageView = itemView.findViewById(R.id.iv_button_review)
    private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
    private val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
    private val tvMessageBody: TextView = itemView.findViewById(R.id.tv_message_body)
    private val tvMessageStatus: TextView = itemView.findViewById(R.id.tv_message_status)
    private val tvButtonReview: TextView = itemView.findViewById(R.id.tv_button_review)
    private val vMarginLeft: View = itemView.findViewById(R.id.v_margin_left)
    private val vMarginRight: View = itemView.findViewById(R.id.v_margin_right)

    private val user = TapTalk.getTapTalkActiveUser(TAPTALK_INSTANCE_KEY)

    private var isNeedAnimateSend = false
    private var isAnimating = false

//    private lateinit var caseModel: TTLCaseModel

    private fun isMessageFromMySelf(messageModel: TAPMessageModel): Boolean {
        return user.userID == messageModel.user.xcUserID
    }

    override fun onBind(item: TAPMessageModel?, position: Int) {
        if (null == item) {
            return
        }
        if (isMessageFromMySelf(item)) {
            // Message from active user
            clBubble.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_chat_bubble_right_default)
            tvMessageBody.setTextColor(ContextCompat.getColor(itemView.context, R.color.ttlColorTextLight))
            civAvatar.visibility = View.GONE
            tvAvatarLabel.visibility = View.GONE
            tvUserName.visibility = View.GONE
            llButtonReview.visibility = View.GONE
            ivMessageStatus.visibility = View.VISIBLE
            vMarginRight.visibility = View.GONE
            vMarginLeft.visibility = View.VISIBLE

            if (null != item.isRead && item.isRead!!) {
                showMessageAsRead(item)
            } else if (null != item.delivered && item.delivered!!) {
                showMessageAsDelivered(item)
            } else if (null != item.failedSend && item.failedSend!!) {
                showMessageFailedToSend()
            } else if (null != item.sending && !item.sending!!) {
                showMessageAsSent(item)
            } else {
                showMessageAsSending()
            }
        } else {
            // Message from others
            clBubble.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_chat_bubble_left_default)
            tvMessageBody.setTextColor(ContextCompat.getColor(itemView.context, R.color.ttlColorTextDark))
            llButtonReview.visibility = View.VISIBLE
            ivMessageStatus.visibility = View.GONE
            vMarginRight.visibility = View.VISIBLE
            vMarginLeft.visibility = View.GONE

            if (item.room.roomType == TYPE_PERSONAL) {
                // Hide avatar and name for personal room
                civAvatar.visibility = View.GONE
                tvAvatarLabel.visibility = View.GONE
                tvUserName.visibility = View.GONE
            } else {
                // Load avatar and name for other room types
                if (null != user && null != user.avatarURL && user.avatarURL.thumbnail.isNotEmpty()) {
                    Glide.with(itemView.context).load(user.avatarURL.thumbnail).into(civAvatar)
                    ImageViewCompat.setImageTintList(civAvatar, null)
                    civAvatar.visibility = View.VISIBLE
                    tvAvatarLabel.visibility = View.GONE
                } else if (null != item.user.avatarURL && item.user.avatarURL.thumbnail.isNotEmpty()) {
                    Glide.with(itemView.context).load(item.user.avatarURL.thumbnail).into(civAvatar)
                    ImageViewCompat.setImageTintList(civAvatar, null)
                    civAvatar.visibility = View.VISIBLE
                    tvAvatarLabel.visibility = View.GONE
                } else {
                    ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, item.user.name)))
                    civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_circle_9b9b9b))
                    tvAvatarLabel.text = TAPUtils.getInitials(item.user.name, 2)
                    civAvatar.visibility = View.VISIBLE
                    tvAvatarLabel.visibility = View.VISIBLE
                }
                tvUserName.text = item.user.name
                tvUserName.visibility = View.VISIBLE
            }

//            try {
//                caseModel = TTLCaseModel(item.data)
//            } catch (e: Exception) {
//                caseModel = TTLCaseModel()
//                e.printStackTrace()
//            }

            if (item.type == TYPE_REVIEW_SUBMITTED) {
                // Show review submitted
                llButtonReview.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_button_inactive_ripple)
                tvButtonReview.text = itemView.context.getString(R.string.ttl_review_submitted)
                ivButtonReview.visibility = View.VISIBLE
                llButtonReview.setOnClickListener { }
            } else if (item.type == TYPE_REVIEW) {
                // Show review button available
                llButtonReview.background = ContextCompat.getDrawable(itemView.context, R.drawable.ttl_bg_button_active_ripple)
                tvButtonReview.text = itemView.context.getString(R.string.ttl_leave_a_review)
                ivButtonReview.visibility = View.GONE
                llButtonReview.setOnClickListener { onReviewButtonTapped() }
            }
        }

        tvMessageBody.text = item.body

        tvMessageStatus.text = item.messageStatusText

        markMessageAsRead(item, TapTalk.getTapTalkActiveUser(TAPTALK_INSTANCE_KEY))

        if (BuildConfig.DEBUG) {
            itemView.setOnLongClickListener{
                Log.d(this.javaClass.simpleName, "Message model: " + TAPUtils.toJsonString(item))
                return@setOnLongClickListener true
            }
        }
    }

    private fun showMessageAsSending() {
        isNeedAnimateSend = true
        flBubble.translationX = TAPUtils.dpToPx(-22).toFloat()
        ivSending.translationX = 0f
        ivSending.translationY = 0f
        ivSending.alpha = 1f
        ivMessageStatus.visibility = View.INVISIBLE
        tvMessageStatus.visibility = View.GONE
    }

    private fun showMessageFailedToSend() {
        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_warning_red_circle_background))
        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconChatRoomMessageFailed)))
        ivMessageStatus.visibility = View.VISIBLE
        tvMessageStatus.text = itemView.context.getString(R.string.tap_message_send_failed)
        tvMessageStatus.visibility = View.VISIBLE
        ivSending.alpha = 0f
        flBubble.translationX = 0f
    }

    private fun showMessageAsSent(message: TAPMessageModel?) {
        if (!isMessageFromMySelf(message!!)) {
            return
        }
        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_sent_grey))
        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.ttlIconMessageSent)))
        ivMessageStatus.visibility = View.VISIBLE
        tvMessageStatus.visibility = View.VISIBLE
        animateSend(item, flBubble, ivSending, ivMessageStatus)
    }

    private fun showMessageAsDelivered(message: TAPMessageModel?) {
        if (isMessageFromMySelf(message!!)) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_delivered_grey))
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.ttlIconMessageDelivered)))
            ivMessageStatus.visibility = View.VISIBLE
            ivSending.alpha = 0f
        }
        flBubble.translationX = 0f
        tvMessageStatus.visibility = View.VISIBLE
    }

    private fun showMessageAsRead(message: TAPMessageModel?) {
        if (isMessageFromMySelf(message!!)) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ttl_ic_read_orange))
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.ttlIconMessageRead)))
            ivMessageStatus.visibility = View.VISIBLE
            ivSending.alpha = 0f
        }
        flBubble.translationX = 0f
        tvMessageStatus.visibility = View.VISIBLE
    }

    private fun animateSend(item: TAPMessageModel, flBubble: FrameLayout, ivSending: ImageView, ivMessageStatus: ImageView) {
        if (!isNeedAnimateSend) {
            // Set bubble state to post-animation
            flBubble.translationX = 0f
            ivMessageStatus.translationX = 0f
            ivSending.alpha = 0f
        } else {
            // Animate bubble
            isNeedAnimateSend = false
            isAnimating = true
            flBubble.translationX = TAPUtils.dpToPx(-22).toFloat()
            ivSending.translationX = 0f
            ivSending.translationY = 0f
            Handler().postDelayed({
                flBubble.animate()
                        .translationX(0f)
                        .setDuration(160L)
                        .start()
                ivSending.animate()
                        .translationX(TAPUtils.dpToPx(36).toFloat())
                        .translationY(TAPUtils.dpToPx(-23).toFloat())
                        .setDuration(360L)
                        .setInterpolator(AccelerateInterpolator(0.5f))
                        .withEndAction {
                            ivSending.alpha = 0f
                            isAnimating = false
                            if (null != item.isRead && item.isRead!! ||
                                    null != item.delivered && item.delivered!!) {
                                //notifyItemChanged(getItems().indexOf(item))
                                onBind(item, position)
                            }
                        }
                        .start()
            }, 200L)
        }
    }

    private fun onReviewButtonTapped() {
        if (itemView.context is Activity) {
            listener.onReviewButtonTapped(itemView.context as Activity, item)
        } else {
            listener.onReviewButtonTapped(itemView.context, item)
        }
    }
}
