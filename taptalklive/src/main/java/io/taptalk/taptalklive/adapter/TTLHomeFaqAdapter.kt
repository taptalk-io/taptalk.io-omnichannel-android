package io.taptalk.taptalklive.adapter

import android.app.Activity
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MediaType.IMAGE_JPEG
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_NAME
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.SIZE
import io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Listener.TAPDatabaseListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPDataManager
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.View.Activity.TAPImageDetailPreviewActivity
import io.taptalk.TapTalk.View.Activity.TAPVideoPlayerActivity
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.DOCUMENT
import io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.FILE
import io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.IMAGE
import io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.TEXT
import io.taptalk.taptalklive.Const.TTLConstant.MessageTypeString.VIDEO
import io.taptalk.taptalklive.Const.TTLConstant.ScfPathType.TALK_TO_AGENT
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Interface.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Interface.TTLItemListInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.helper.TTLUtil
import io.taptalk.taptalklive.model.TTLCaseListModel
import java.net.URLConnection

class TTLHomeFaqAdapter(
    val activity: Activity,
    itemList: List<TTLScfPathModel>,
    val listener: TTLHomeAdapterInterface?,
    val containsHeader: Boolean
) : TAPBaseAdapter<TTLScfPathModel, TAPBaseViewHolder<TTLScfPathModel>>() {

    companion object {
        const val HEADER = 0
        const val FAQ_PARENT = 1
        const val FAQ_CHILD = 2
    }

    private var filteredChannelLinks: ArrayList<TTLChannelLinkModel>? = null
    private var channelLinksAdapter: TTLChannelLinksAdapter? = null
    private var channelLinksListener: TTLItemListInterface? = null

    private var caseListArray: ArrayList<TTLCaseListModel>? = null
    private var caseListAdapter: TTLCaseListAdapter? = null
    private var caseListListener: TTLCaseListAdapter.TTLCaseListInterface? = null

    var fileDownloadProgress: Int = 0

    init {
        items = itemList

        if (containsHeader) {
            // Setup channel links
            val channelLinks = TTLDataManager.getInstance().channelLinks
            filteredChannelLinks = ArrayList()
            if (!channelLinks.isNullOrEmpty()) {
                for (channel in channelLinks) {
                    if (channel.isEnabled) {
                        filteredChannelLinks!!.add(channel)
                    }
                }
            }
            channelLinksListener = object: TTLItemListInterface {
                override fun onItemSelected(position: Int) {
                    if (position >= 0 && filteredChannelLinks!!.size > position) {
                        listener?.onChannelLinkSelected(filteredChannelLinks!![position], position)
                    }
                }
            }
            channelLinksAdapter = TTLChannelLinksAdapter(filteredChannelLinks!!, channelLinksListener)

            // Setup case list
            caseListArray = ArrayList()
            caseListListener = object: TTLCaseListAdapter.TTLCaseListInterface {
                override fun onCaseSelected(caseListModel: TTLCaseListModel?, position: Int) {
                    if (caseListModel != null) {
                        listener?.onCaseListTapped(caseListModel)
                    }
                }
            }
            caseListAdapter = TTLCaseListAdapter(caseListArray!!, Glide.with(activity), caseListListener!!)
            refreshLatestCaseList()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val buffer = if (containsHeader) 0 else 1
        return position + buffer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TTLScfPathModel> {
        when (viewType) {
            HEADER -> {
                return HeaderViewHolder(parent, R.layout.ttl_cell_home_header)
            }
            FAQ_PARENT -> {
                return FaqParentViewHolder(parent, R.layout.ttl_cell_faq_parent)
            }
            else -> {
                return FaqChildViewHolder(parent, R.layout.ttl_cell_faq_child)
            }
        }
    }

    internal inner class HeaderViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var flOr: FrameLayout = itemView.findViewById(R.id.fl_or)
        private var clChannelAndDirectMessage: ConstraintLayout = itemView.findViewById(R.id.cl_channel_and_direct_message)
        private var clCaseList: ConstraintLayout = itemView.findViewById(R.id.cl_case_list)
        private var llButtonMessageDirectly: LinearLayout = itemView.findViewById(R.id.ll_button_message_directly)
        private var llButtonNewMessage: LinearLayout = itemView.findViewById(R.id.ll_button_new_message)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvLabelChannel: TextView = itemView.findViewById(R.id.tv_label_channel)
        private var tvButtonSeeAllMessages: TextView = itemView.findViewById(R.id.tv_button_see_all_messages)
        private var rvChannelLinks: RecyclerView = itemView.findViewById(R.id.rv_channel_links)
        private var rvCaseList: TAPChatRecyclerView = itemView.findViewById(R.id.rv_case_list)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null || !containsHeader) {
                return
            }

            if (filteredChannelLinks?.isEmpty() == true) {
                // Hide channel links
                tvLabelChannel.visibility = View.GONE
                rvChannelLinks.visibility = View.GONE
                rvChannelLinks.adapter = null
                rvChannelLinks.layoutManager = null
            }
            else {
                // Show channel links
                tvLabelChannel.visibility = View.VISIBLE
                rvChannelLinks.visibility = View.VISIBLE
                rvChannelLinks.adapter = channelLinksAdapter
                if (rvChannelLinks.layoutManager == null) {
                    val spanCount = if (activity.resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
                        12
                    }
                    else {
                        5
                    }
                    rvChannelLinks.layoutManager = object : GridLayoutManager(activity, spanCount) {
                        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                            try {
                                super.onLayoutChildren(recycler, state)
                            }
                            catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            val messageAnimator = rvCaseList.itemAnimator as SimpleItemAnimator?
            if (null != messageAnimator) {
                messageAnimator.supportsChangeAnimations = false
            }
            if (caseListArray?.isEmpty() == true) {
                // Hide latest message
                clCaseList.visibility = View.GONE
                llButtonMessageDirectly.visibility = View.VISIBLE
                rvCaseList.adapter = null
                rvCaseList.layoutManager = null
            }
            else {
                // Show latest message
                clCaseList.visibility = View.VISIBLE
                llButtonMessageDirectly.visibility = View.GONE
                rvCaseList.adapter = caseListAdapter
                if (rvCaseList.layoutManager == null) {
                    rvCaseList.layoutManager = object : LinearLayoutManager(activity, VERTICAL, false) {
                        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                            try {
                                super.onLayoutChildren(recycler, state)
                            }
                            catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            if (rvChannelLinks.visibility == View.VISIBLE && llButtonMessageDirectly.visibility == View.VISIBLE) {
                // Show channel links and message us directly button
                clChannelAndDirectMessage.visibility = View.VISIBLE
                flOr.visibility = View.VISIBLE
            }
            else if (rvChannelLinks.visibility == View.GONE && llButtonMessageDirectly.visibility == View.GONE) {
                // Hide channel links and message us directly button
                clChannelAndDirectMessage.visibility = View.GONE
                flOr.visibility = View.GONE
            }
            else {
                // Show either channel links or message us directly button
                clChannelAndDirectMessage.visibility = View.VISIBLE
                flOr.visibility = View.GONE
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

    internal inner class FaqParentViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var llButtonTalkToAgent: LinearLayout = itemView.findViewById(R.id.ll_button_talk_to_agent)
        private var flImagePreview: FrameLayout = itemView.findViewById(R.id.fl_image_preview)
        private var clFilePreview: ConstraintLayout = itemView.findViewById(R.id.cl_file_preview)
        private var ivButtonClose: ImageView = itemView.findViewById(R.id.iv_button_close)
        private var tvLabelFaq: TextView = itemView.findViewById(R.id.tv_label_faq)
        private var tvFaqTitle: TextView = itemView.findViewById(R.id.tv_faq_title)
        private var tvFaqContent: TextView = itemView.findViewById(R.id.tv_faq_content)
        private var tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
        private var tvFileInfo: TextView = itemView.findViewById(R.id.tv_file_info)
        private var ivImagePreview: ImageView = itemView.findViewById(R.id.iv_image_preview)
        private var ivButtonPlay: ImageView = itemView.findViewById(R.id.iv_button_play)
        private var ivFileStatusIcon: ImageView = itemView.findViewById(R.id.iv_file_status_icon)
        private var vHeaderBackground: View = itemView.findViewById(R.id.v_header_background)
        private var vBottomDecoration: View = itemView.findViewById(R.id.v_bottom_decoration)
        private var pbContentResponseLoading: ProgressBar = itemView.findViewById(R.id.pb_content_response_loading)
        private var pbFileDownloadProgress: ProgressBar = itemView.findViewById(R.id.pb_file_download_progress)

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            if (containsHeader) {
                // Home page FAQ parent
                vHeaderBackground.visibility = View.GONE
                ivButtonClose.visibility = View.GONE
                tvLabelFaq.visibility = View.VISIBLE
                ivButtonClose.setOnClickListener(null)
                if (tvFaqContent.layoutParams is ViewGroup.MarginLayoutParams) {
                    val layoutParams = tvFaqContent.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = TAPUtils.dpToPx(itemView.context.resources, 12f)
                    layoutParams.leftMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    layoutParams.rightMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    tvFaqContent.requestLayout()
                }
            }
            else {
                // FAQ child details page
                vHeaderBackground.visibility = View.VISIBLE
                ivButtonClose.visibility = View.VISIBLE
                tvLabelFaq.visibility = View.GONE
                ivButtonClose.setOnClickListener {
                    listener?.onCloseButtonTapped()
                }
                if (tvFaqContent.layoutParams is ViewGroup.MarginLayoutParams) {
                    val layoutParams = tvFaqContent.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.topMargin = TAPUtils.dpToPx(itemView.context.resources, 4f)
                    layoutParams.leftMargin = 0
                    layoutParams.rightMargin = 0
                    tvFaqContent.requestLayout()
                }
            }

            // Title
            tvFaqTitle.text = item.title

            // Media Preview
            if (!item.apiURL.isNullOrEmpty() && !item.contentResponse.isNullOrEmpty()) {
                val contentResponseMap = TAPUtils.toHashMap(item.contentResponse)
                if (contentResponseMap != null) {
                    val type = contentResponseMap["type"] as String?
                    if (type == IMAGE || type == VIDEO) {
                        val mediaMap = contentResponseMap[type] as HashMap<*, *>?
                        val url = mediaMap?.get("url") as String?
                        if (!url.isNullOrEmpty()) {
                            // Show image/video thumbnail
                            flImagePreview.visibility = View.VISIBLE
                            Glide.with(itemView.context).load(url).listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                                    (itemView.context as Activity?)?.runOnUiThread {
                                        flImagePreview.visibility = View.GONE
                                    }
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                    (itemView.context as Activity?)?.runOnUiThread {
                                        if (type == VIDEO) {
                                            ivButtonPlay.visibility = View.VISIBLE
                                            ivImagePreview.setOnClickListener {
                                                playVideo(url)
                                            }
                                        }
                                        else {
                                            ivButtonPlay.visibility = View.GONE
                                            ivImagePreview.setOnClickListener {
                                                openImageDetailPreview(mediaMap)
                                            }
                                        }
                                    }
                                    return false
                                }
                            }).into(ivImagePreview)
                        }
                        else {
                            flImagePreview.visibility = View.GONE
                        }
                        clFilePreview.visibility = View.GONE
                    }
                    else if (type == FILE || type == DOCUMENT) {
                        val mediaMap = contentResponseMap[type] as HashMap<*, *>?
                        val url = mediaMap?.get("url") as String?
                        if (!url.isNullOrEmpty()) {
                            // Show file preview
                            val filename = mediaMap?.get("filename") as String?
                            if (!filename.isNullOrEmpty()) {
                                tvFileName.text = filename
                            }
                            else {
                                tvFileName.text = DOCUMENT
                            }

                            val size = mediaMap?.get("size") as Number?
                            if (size != null && size.toLong() > 0L) {
                                tvFileInfo.text = TAPUtils.getStringSizeLengthFile(size.toLong())
                                tvFileInfo.visibility = View.VISIBLE
                            }
                            else {
                                tvFileInfo.visibility = View.GONE
                            }

                            // Generate message to manage file
                            val message = TAPMessageModel()
                            var fileNameData = filename
                            var mimeType = ""
                            if (filename?.contains('.') == true) {
                                val dotIndex = filename.lastIndexOf('.')
                                fileNameData = filename.substring(0, dotIndex)
                                try {
                                    mimeType = URLConnection.guessContentTypeFromName(filename)
                                }
                                catch (e: StringIndexOutOfBoundsException) {
                                    e.printStackTrace()
                                }
                            }
                            message.localID = item.itemID.toString()
                            message.type = TYPE_FILE
                            val room = TAPRoomModel()
                            room.roomID = ""
                            message.room = room
                            val data = HashMap<String, Any>()
                            data[FILE_URL] = url
                            data[FILE_ID] = ""
                            data[FILE_NAME] = fileNameData ?: ""
                            data[MEDIA_TYPE] = mimeType
                            data[SIZE] = 0L
                            message.data = data
                            message.created = item.createdTime

                            val fileUri = TAPFileDownloadManager.getInstance(TAPTALK_INSTANCE_KEY).getFileMessageUri(url)
                            if (fileUri != null) {
                                ivFileStatusIcon.setImageDrawable(ContextCompat.getDrawable(activity, io.taptalk.TapTalk.R.drawable.tap_ic_documents_white))
                                pbFileDownloadProgress.visibility = View.GONE
                                ivFileStatusIcon.setOnClickListener {
                                    listener?.onOpenFileButtonTapped(fileUri)
                                }
                            }
                            else if (fileDownloadProgress in 1..100) {
                                ivFileStatusIcon.setImageDrawable(null)
                                pbFileDownloadProgress.visibility = View.VISIBLE
                                pbFileDownloadProgress.progress = fileDownloadProgress
                                ivFileStatusIcon.setOnClickListener(null)
                            }
                            else {
                                ivFileStatusIcon.setImageDrawable(ContextCompat.getDrawable(activity, io.taptalk.TapTalk.R.drawable.tap_ic_download_orange))
                                pbFileDownloadProgress.visibility = View.GONE
                                ivFileStatusIcon.setOnClickListener {
                                    listener?.onDownloadFileButtonTapped(message)
                                }
                            }

                            clFilePreview.visibility = View.VISIBLE
                        }
                        else {
                            clFilePreview.visibility = View.GONE
                        }
                        flImagePreview.visibility = View.GONE
                    }
                    else {
                        flImagePreview.visibility = View.GONE
                        clFilePreview.visibility = View.GONE
                    }
                }
                else {
                    flImagePreview.visibility = View.GONE
                    clFilePreview.visibility = View.GONE
                }
                pbContentResponseLoading.visibility = View.GONE
            }
            else {
                flImagePreview.visibility = View.GONE
                clFilePreview.visibility = View.GONE
                if (TapTalkLive.getLoadingContentResponseList().contains(item.apiURL)) {
                    pbContentResponseLoading.visibility = View.VISIBLE
                }
                else {
                    pbContentResponseLoading.visibility = View.GONE
                }
            }

            // Content
            if (!item.content.isNullOrEmpty()) {
                tvFaqContent.text = item.content
                TTLUtil.setLinkDetection(activity, tvFaqContent, item, listener)
            }
            else if (!item.apiURL.isNullOrEmpty()) {
                if (!item.contentResponse.isNullOrEmpty()) {
                    val contentResponseMap = TAPUtils.toHashMap(item.contentResponse)
                    if (contentResponseMap != null) {
                        val type = contentResponseMap["type"] as String?
                        if (type == TEXT) {
                            val textMap = contentResponseMap["text"] as HashMap<*, *>?
                            val body = textMap?.get("body") as String?
                            tvFaqContent.text = body ?: ""
                        }
                        else if (
                            type == IMAGE ||
                            type == VIDEO ||
                            type == FILE ||
                            type == DOCUMENT
                        ) {
                            val mediaMap = contentResponseMap[type] as HashMap<*, *>?
                            val caption = mediaMap?.get("caption") as String?
                            tvFaqContent.text = caption ?: ""
                        }
                        else {
                            tvFaqContent.text = ""
                        }
                    }
                    else {
                        tvFaqContent.text = ""
                    }
                }
                else if (TapTalkLive.getLoadingContentResponseList().contains(item.apiURL)) {
                    tvFaqContent.text = ""
                }
                else {
                    tvFaqContent.text = item.contentOnAPIError
                }
            }
            else {
                tvFaqContent.text = ""
            }
            if (tvFaqContent.text.isNullOrEmpty()) {
                tvFaqContent.visibility = View.GONE
            }
            else {
                tvFaqContent.visibility = View.VISIBLE
            }

            if (item.type == TALK_TO_AGENT) {
                llButtonTalkToAgent.visibility = View.VISIBLE
                llButtonTalkToAgent.setOnClickListener {
                    listener?.onTalkToAgentButtonTapped(item)
                }
            }
            else {
                llButtonTalkToAgent.visibility = View.GONE
                llButtonTalkToAgent.setOnClickListener(null)
            }

            if (bindingAdapterPosition >= itemCount - 1) {
                vBottomDecoration.visibility = View.VISIBLE
            }
            else {
                vBottomDecoration.visibility = View.GONE
            }
        }

        private fun openImageDetailPreview(mediaMap: HashMap<*, *>?) {
            val message = TAPMessageModel()
            val url = mediaMap?.get("url") as String?
            val caption = mediaMap?.get("caption") as String?
            val data = HashMap<String, Any>()
            data[FILE_URL] = url ?: ""
            data[FILE_ID] = ""
            data[CAPTION] = caption ?: ""
            data[MEDIA_TYPE] = IMAGE_JPEG
            message.data = data
            val user = TAPUserModel("", "")
            message.user = user
            message.created = item.createdTime
            TAPImageDetailPreviewActivity.start(
                activity,
                TAPTALK_INSTANCE_KEY,
                message,
                ivImagePreview
            )
        }

        private fun playVideo(url: String) {
            TAPVideoPlayerActivity.start(
                activity,
                TAPTALK_INSTANCE_KEY,
                null,
                url,
                null
            )
        }
    }

    internal inner class FaqChildViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TTLScfPathModel>(parent, itemLayoutId) {

        private var clFaqChildContainer: ConstraintLayout = itemView.findViewById(R.id.cl_faq_child_container)
        private var tvFaqChildTitle: TextView = itemView.findViewById(R.id.tv_faq_child_title)
        private var tvFaqChildContent: TextView = itemView.findViewById(R.id.tv_faq_child_content)
        private var vBottomDecoration: View = itemView.findViewById(R.id.v_bottom_decoration)

        private var scrollListener: View.OnScrollChangeListener? = null

        override fun onBind(item: TTLScfPathModel?, position: Int) {
            if (item == null) {
                return
            }

            tvFaqChildTitle.text = item.title

            if (!item.content.isNullOrEmpty()) {
                tvFaqChildContent.text = item.content
                tvFaqChildContent.visibility = View.VISIBLE
                TTLUtil.setLinkDetection(activity, tvFaqChildContent, item, listener)
            }
            else {
                tvFaqChildContent.visibility = View.GONE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Fix text view scrolling when truncated link is tapped
                if (scrollListener == null) {
                    scrollListener = View.OnScrollChangeListener { p0, p1, p2, p3, p4 ->
                        run {
                            tvFaqChildContent.setOnScrollChangeListener(null)
                            tvFaqChildContent.scrollX = 0
                            tvFaqChildContent.scrollY = 0
                            tvFaqChildContent.setOnScrollChangeListener(scrollListener)
                        }
                    }
                }
                tvFaqChildContent.setOnScrollChangeListener(null)
                tvFaqChildContent.setOnScrollChangeListener(scrollListener)
            }

            if (bindingAdapterPosition >= itemCount - 1) {
                vBottomDecoration.visibility = View.VISIBLE
            }
            else {
                vBottomDecoration.visibility = View.GONE
            }

            clFaqChildContainer.setOnClickListener {
                listener?.onFaqChildTapped(item)
            }
        }
    }

    fun refreshLatestCaseList() {
        TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getRoomList(true, object: TAPDatabaseListener<TAPMessageEntity>() {
            override fun onSelectedRoomList(
                entities: MutableList<TAPMessageEntity>?,
                unreadMap: MutableMap<String, Int>?,
                mentionMap: MutableMap<String, Int>?
            ) {
                if (!entities.isNullOrEmpty()) {
                    val entity = entities[0]
                    val message = TAPMessageModel.fromMessageEntity(entity)
                    val caseList = TTLCaseListModel(message)
                    if (null != unreadMap && null != unreadMap[entity.roomID]) {
                        caseList.unreadCount = unreadMap[entity.roomID]!!
                    }
                    if (null != mentionMap && null != mentionMap[entity.roomID]) {
                        caseList.unreadMentions = mentionMap[entity.roomID]!!
                    }
                    caseListArray?.clear()
                    caseListArray?.add(caseList)
                }

                activity.runOnUiThread {
                    notifyItemChanged(HEADER)
                }
            }
        })
    }

    fun setLastMessage(message: TAPMessageModel) {
        if (caseListArray?.isNotEmpty() == true && caseListArray!![0].lastMessage.room.roomID == message.room.roomID) {
            // Received message in the same room as previous case list
            val previousCaseList = caseListArray!![0]
            val previousMessage = caseListArray!![0].lastMessage
            if (previousMessage.localID == message.localID) {
                // Update case list's last message data
                previousMessage.updateValue(message)
            }
            else if (previousMessage.created < message.created) {
                // Update case list's last message with the new message from socket
                previousCaseList.lastMessage = message

                val activeUser = TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).activeUser
                if (message.user.userID != activeUser.userID) {
                    // Add unread count by 1 if sender is not self
                    previousCaseList.unreadCount++

                    if (TAPUtils.isActiveUserMentioned(message, activeUser)) {
                        // Show mention badge if user is mentioned
                        previousCaseList.unreadMentions++
                    }
                }

            }
            activity.runOnUiThread {
                notifyItemChanged(HEADER)
            }
        }
        else {
            // Received new message in a different case list
            val caseList = TTLCaseListModel(message)
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadCountPerRoom(
                message.room.roomID,
                object: TAPDatabaseListener<TAPMessageEntity>() {
                    override fun onCountedUnreadCount(
                        roomID: String?,
                        unreadCount: Int,
                        mentionCount: Int
                    ) {
                        if (roomID == message.room.roomID) {
                            caseList.unreadCount = unreadCount
                            caseList.unreadMentions = mentionCount
                        }

                        caseListArray?.clear()
                        caseListArray?.add(caseList)

                        activity.runOnUiThread {
                            notifyItemChanged(HEADER)
                        }
                    }
                }
            )
        }
    }
}
