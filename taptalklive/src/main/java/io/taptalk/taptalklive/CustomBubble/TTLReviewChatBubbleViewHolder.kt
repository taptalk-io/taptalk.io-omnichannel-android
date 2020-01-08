package io.taptalk.taptalklive.CustomBubble

import android.app.Activity
import android.content.res.ColorStateList
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Adapter.TAPBaseChatViewHolder
import io.taptalk.taptalklive.R

class TTLReviewChatBubbleViewHolder internal constructor(
        parent: ViewGroup,
        itemLayoutId: Int,
        private val listener: TTLReviewChatBubbleListener) :
    TAPBaseChatViewHolder(parent, itemLayoutId) {

    private val clBubble: ConstraintLayout = itemView.findViewById(R.id.cl_bubble)
    private val flBubble: FrameLayout = itemView.findViewById(R.id.fl_bubble)
    private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
    private val ivMessageStatus: ImageView = itemView.findViewById(R.id.iv_message_status)
    private val ivSending: ImageView = itemView.findViewById(R.id.iv_sending)
    private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
    private val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
    private val tvMessageBody: TextView = itemView.findViewById(R.id.tv_message_body)
    private val tvMessageStatus: TextView = itemView.findViewById(R.id.tv_message_status)
    private val tvButtonReview: TextView = itemView.findViewById(R.id.tv_button_review)
    private val vMarginLeft: View = itemView.findViewById(R.id.v_margin_left)
    private val vMarginRight: View = itemView.findViewById(R.id.v_margin_right)

    private val user = TapTalk.getTaptalkActiveUser()

    private fun isMessageFromMySelf(messageModel: TAPMessageModel): Boolean {
        return user.userID == messageModel.user.userID
    }

    override fun onBind(item: TAPMessageModel?, position: Int) {
        if (isMessageFromMySelf(item!!)) {
            // Message from active user
            clBubble.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_chat_bubble_right_default)
            tvMessageBody.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapColorTextLight))
            civAvatar.visibility = View.GONE
            tvAvatarLabel.visibility = View.GONE
            tvUserName.visibility = View.GONE
            tvButtonReview.visibility = View.GONE
            ivMessageStatus.visibility = View.VISIBLE
            vMarginRight.visibility = View.GONE
            vMarginLeft.visibility = View.VISIBLE

            if (null != item.isRead && item.isRead!!) {
                receiveReadEvent(item)
            } else if (null != item.delivered && item.delivered!!) {
                receiveDeliveredEvent(item)
            } else if (null != item.failedSend && item.failedSend!!) {
                setMessage(item)
            } else if (null != item.sending && !item.sending!!) {
                receiveSentEvent(item)
            } else {
                setMessage(item)
            }
        } else {
            // Message from others
            clBubble.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_chat_bubble_left_default)
            tvMessageBody.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapColorTextDark))
            tvButtonReview.visibility = View.VISIBLE
            ivMessageStatus.visibility = View.GONE
            vMarginRight.visibility = View.VISIBLE
            vMarginLeft.visibility = View.GONE

            if (item.room.roomType == RoomType.TYPE_GROUP) {
                // Load avatar and name if room type is group
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
                    ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(item.user.name)))
                    civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, io.taptalk.Taptalk.R.drawable.tap_bg_circle_9b9b9b))
                    tvAvatarLabel.text = TAPUtils.getInstance().getInitials(item.user.name, 2)
                    civAvatar.visibility = View.VISIBLE
                    tvAvatarLabel.visibility = View.VISIBLE
                }
                tvUserName.text = item.user.name
                tvUserName.visibility = View.VISIBLE
            } else {
                // Hide avatar and name
                civAvatar.visibility = View.GONE
                tvAvatarLabel.visibility = View.GONE
                tvUserName.visibility = View.GONE
            }
        }

        tvMessageBody.text = item.body

        markMessageAsRead(item, TapTalk.getTaptalkActiveUser())

        tvButtonReview.setOnClickListener { onReviewButtonTapped() }
    }

    override fun receiveSentEvent(message: TAPMessageModel?) {
        if (!isMessageFromMySelf(message!!)) {
            return
        }
        ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, io.taptalk.Taptalk.R.drawable.tap_ic_sent_grey))
        ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, io.taptalk.Taptalk.R.color.tapIconMessageSent)))
        ivMessageStatus.visibility = View.VISIBLE
            tvMessageStatus.visibility = View.VISIBLE
        animateSend(item, flBubble, ivSending, ivMessageStatus)
    }

    override fun receiveDeliveredEvent(message: TAPMessageModel?) {
        if (isMessageFromMySelf(message!!)) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, io.taptalk.Taptalk.R.drawable.tap_ic_delivered_grey))
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, io.taptalk.Taptalk.R.color.tapIconMessageDelivered)))
            ivMessageStatus.visibility = View.VISIBLE
            ivSending.alpha = 0f
        }
        flBubble.translationX = 0f
        tvMessageStatus.visibility = View.VISIBLE
    }

    override fun receiveReadEvent(message: TAPMessageModel?) {
        if (isMessageFromMySelf(message!!)) {
            ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, io.taptalk.Taptalk.R.drawable.tap_ic_read_orange))
            ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, io.taptalk.Taptalk.R.color.tapIconMessageRead)))
            ivMessageStatus.visibility = View.VISIBLE
            ivSending.alpha = 0f
        }
        flBubble.translationX = 0f
        tvMessageStatus.visibility = View.VISIBLE
    }


    private fun animateSend(item: TAPMessageModel, flBubble: FrameLayout,
                                 ivSending: ImageView, ivMessageStatus: ImageView) {
        if (!item.isNeedAnimateSend) {
            // Set bubble state to post-animation
            flBubble.translationX = 0f
            ivMessageStatus.translationX = 0f
            ivSending.alpha = 0f
        } else {
            // Animate bubble
            item.isNeedAnimateSend = false
            item.isAnimating = true
            flBubble.translationX = TAPUtils.getInstance().dpToPx(-22).toFloat()
            ivSending.translationX = 0f
            ivSending.translationY = 0f
            Handler().postDelayed({
                flBubble.animate()
                        .translationX(0f)
                        .setDuration(160L)
                        .start()
                ivSending.animate()
                        .translationX(TAPUtils.getInstance().dpToPx(36).toFloat())
                        .translationY(TAPUtils.getInstance().dpToPx(-23).toFloat())
                        .setDuration(360L)
                        .setInterpolator(AccelerateInterpolator(0.5f))
                        .withEndAction {
                            ivSending.alpha = 0f
                            item.isAnimating = false
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
            listener.onReviewButtonTapped(itemView.context as Activity, item.user)
        } else {
            listener.onReviewButtonTapped(itemView.context, item.user)
        }
    }
}
