package io.taptalk.taptalklive.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.model.TTLCaseListModel

class TTLCaseListAdapter(
    caseLists: List<TTLCaseListModel>,
    glide: RequestManager,
    caseListInterface: TTLCaseListInterface
) : TAPBaseAdapter<TTLCaseListModel, TAPBaseViewHolder<TTLCaseListModel>>() {

    interface TTLCaseListInterface {
        fun onCaseSelected(caseListModel: TTLCaseListModel?, position: Int)
    }

    private val caseListInterface: TTLCaseListInterface
    private val glide: RequestManager

    init {
        setItems(caseLists, false)
        this.glide = glide
        this.caseListInterface = caseListInterface
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TAPBaseViewHolder<TTLCaseListModel> {
        return CaseListViewHolder(parent, R.layout.ttl_cell_case_list)
    }

    internal inner class CaseListViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLCaseListModel>(parent, itemLayoutId) {

        private val clContainer: ConstraintLayout = itemView.findViewById(R.id.cl_container)
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val ivAvatarIcon: ImageView = itemView.findViewById(R.id.iv_avatar_icon)
        private val ivMute: ImageView = itemView.findViewById(R.id.iv_mute)
        private val ivMessageStatus: ImageView = itemView.findViewById(R.id.iv_message_status)
        private val ivPersonalRoomTypingIndicator: ImageView = itemView.findViewById(R.id.iv_personal_room_typing_indicator)
        private val ivBadgeMention: ImageView = itemView.findViewById(R.id.iv_badge_mention)
        private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tv_last_message)
        private val tvLastMessageTime: TextView = itemView.findViewById(R.id.tv_last_message_time)
        private val tvBadgeUnread: TextView = itemView.findViewById(R.id.tv_badge_unread)
        private val vSeparator: View = itemView.findViewById(R.id.v_separator)
        private val vSeparatorFull: View = itemView.findViewById(R.id.v_separator_full)
        private val llMarkRead: LinearLayout = itemView.findViewById(R.id.ll_mark_read)
        private val tvMarkRead: TextView = itemView.findViewById(R.id.tv_mark_read)
        private val ivMarkRead: ImageView = itemView.findViewById(R.id.iv_mark_read)

        override fun onBind(item: TTLCaseListModel, position: Int) {
            val activeUser = TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).activeUser ?: return
            val message = item.lastMessage ?: return
            val room = message.room

            // Set room image
            if (!room?.imageURL?.thumbnail.isNullOrEmpty()) {
                // Load room image
                glide.load(room?.imageURL?.thumbnail).listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        // Show initial
                        if (itemView.context is Activity) {
                            (itemView.context as Activity).runOnUiThread {
                                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.defaultAvatarBackgroundColor))
                                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                                tvAvatarLabel.text = TAPUtils.getInitials(room.name, 1)
                                tvAvatarLabel.visibility = View.VISIBLE
                            }
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            }
            else {
                // Show initial
                glide.clear(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.defaultAvatarBackgroundColor))
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                tvAvatarLabel.text = TAPUtils.getInitials(room.name, 1)
                tvAvatarLabel.visibility = View.VISIBLE
            }

            ivAvatarIcon.visibility = View.GONE

            // Show/hide separator
//            if (position == itemCount - 1) {
//                vSeparator.visibility = View.GONE
//                vSeparatorFull.visibility = View.VISIBLE
//            }
//            else {
                vSeparator.visibility = View.VISIBLE
                vSeparatorFull.visibility = View.GONE
//            }

            // Set room name
            tvFullName.text = room?.name ?: ""

            // Set last message timestamp
            tvLastMessageTime.text = item.lastMessageTimestamp
            val draft = TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getMessageFromDraft(message.room.roomID)
            if (null != draft && !draft.isEmpty()) {
                // Show draft
                tvLastMessage.text = String.format(itemView.context.getString(R.string.tap_format_s_draft), draft)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            }
            else if (0 < item.typingUsers.size) {
                // Set message to Typing
                tvLastMessage.text = itemView.context.getString(R.string.tap_typing)
                ivPersonalRoomTypingIndicator.visibility = View.VISIBLE
                if (null == ivPersonalRoomTypingIndicator.drawable) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator)
                }
            }
            else if (null != message.user && activeUser.userID == message.user.userID && null != message.isDeleted && message.isDeleted!!) {
                // Show last message deleted by active user
                tvLastMessage.text = itemView.resources.getString(R.string.tap_you_deleted_this_message)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            }
            else if (null != message.isDeleted && message.isDeleted!!) {
                // Show last message deleted by sender
                tvLastMessage.text = itemView.resources.getString(R.string.tap_this_deleted_message)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            }
            else {
                // Set text from last message
                tvLastMessage.text = message.body
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            }
            
            ivMute.visibility = View.GONE
            tvBadgeUnread.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_room_list_unread_badge)

            // Change Status Message Icon
            // Message sender is not the active user / last message is system message / room draft exists
            if ((message.user.userID != activeUser.userID || TYPE_SYSTEM_MESSAGE == message.type) ||
                !draft.isNullOrEmpty()
            ) {
                ivMessageStatus.setImageDrawable(null)
            }
            else if (message.isDeleted == true) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_block_red))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageDeleted)))
            }
            else if (message.isRead == true &&
                !TapUI.getInstance(TAPTALK_INSTANCE_KEY).isReadStatusHidden
            ) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_read_orange))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageRead)))
            }
            else if (message.isDelivered == true) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_delivered_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageDelivered)))
            }
            else if (message.isFailedSend == true) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_warning_red_circle_background))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageFailed)))
            }
            else if (message.isSending == true) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_sending_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageSending)))
            }
            else {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_sent_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageSent)))
            }

            // Show unread count
            val unreadCount: Int = item.unreadCount
            if (unreadCount > 0) {
                if (unreadCount == 0) {
                    tvBadgeUnread.text = ""
                } else if (unreadCount >= 100) {
                    tvBadgeUnread.setText(R.string.tap_over_99)
                } else {
                    tvBadgeUnread.text = unreadCount.toString()
                }
                ivMessageStatus.visibility = View.GONE
                tvBadgeUnread.visibility = View.VISIBLE
                //glide.load(R.drawable.tap_ic_mark_read_white).fitCenter().into(ivMarkRead)
                //tvMarkRead.setText(R.string.tap_read)
            } else {
                ivMessageStatus.visibility = View.VISIBLE
                tvBadgeUnread.visibility = View.GONE
                //glide.load(R.drawable.tap_ic_mark_unread_white).fitCenter().into(ivMarkRead)
                //tvMarkRead.setText(R.string.tap_unread)
            }

            // Show mention badge
            if (!TapUI.getInstance(TAPTALK_INSTANCE_KEY).isMentionUsernameDisabled && item.unreadMentions > 0) {
                ivBadgeMention.visibility = View.VISIBLE
            } else {
                ivBadgeMention.visibility = View.GONE
            }
            itemView.setOnClickListener { v: View? -> caseListInterface.onCaseSelected(item, bindingAdapterPosition) }
        }
    }
}
