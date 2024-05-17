package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SimpleItemAnimator
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TapCommonListener
import io.taptalk.TapTalk.Listener.TapCoreFileDownloadListener
import io.taptalk.TapTalk.Listener.TapCoreMessageListener
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Manager.TapCoreRoomListManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.JSON_TASK_COMPLETED
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.SCF_PATH_UPDATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_STRING
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_URL
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Interface.TTLHomeAdapterInterface
import io.taptalk.taptalklive.Manager.TTLDataManager
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.adapter.TTLHomeFaqAdapter
import io.taptalk.taptalklive.helper.TTLUtil
import io.taptalk.taptalklive.model.TTLCaseListModel
import kotlinx.android.synthetic.main.ttl_activity_home.rv_home
import java.io.File

class TTLHomeActivity : TAPBaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TTLHomeActivity::class.java)
            if (context !is Activity) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay)
            }
        }
    }

    private lateinit var adapter: TTLHomeFaqAdapter
    private var pendingDownloadMessage: TAPMessageModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_home)

        instanceKey = TAPTALK_INSTANCE_KEY
        initView()
        initListeners()
        fetchNewMessages()
    }

    override fun onResume() {
        super.onResume()
        fetchNewMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
        TapTalkLive.getInstance()?.tapTalkLiveListener?.onCloseButtonInHomePageTapped(this)
    }

    private fun initView() {
        adapter = TTLHomeFaqAdapter(this, generateAdapterItems(), adapterListener, true)
        rv_home.adapter = adapter
        rv_home.layoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                }
                catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        val messageAnimator = rv_home.itemAnimator as SimpleItemAnimator?
        messageAnimator?.supportsChangeAnimations = false
    }

    private fun generateAdapterItems(): ArrayList<TTLScfPathModel> {
        val homeItemList = ArrayList<TTLScfPathModel>()

        homeItemList.add(TTLScfPathModel()) // Empty item for header

        val scfPath = TTLDataManager.getInstance().scfPath
        if (scfPath != null) {
            if (scfPath.contentResponse.isNullOrEmpty() &&
                !scfPath.apiURL.isNullOrEmpty() &&
                !TapTalkLive.getContentResponseMap()[scfPath.apiURL].isNullOrEmpty()
            ) {
                scfPath.contentResponse = TapTalkLive.getContentResponseMap()[scfPath.apiURL]!!
            }
            if (scfPath.childItems.isEmpty()) {
                homeItemList.add(scfPath)
            }
            else {
                val parentScf = scfPath.copy()
                parentScf.childItems = ArrayList()
                homeItemList.add(parentScf)
                homeItemList.addAll(scfPath.childItems)
            }
            TTLUtil.fetchScfPathContentResponse(scfPath, true)
        }
        return homeItemList
    }

    private val adapterListener = object: TTLHomeAdapterInterface {
        override fun onCloseButtonTapped() {
            onBackPressed()
        }

        override fun onChannelLinkSelected(channelLink: TTLChannelLinkModel, position: Int) {
            openChannelUrl(channelLink)
        }

        override fun onNewMessageButtonTapped() {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onCreateNewMessageButtonTapped(this@TTLHomeActivity)
        }

        override fun onSeeAllMessagesButtonTapped() {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onSeeAllMessagesButtonTapped(this@TTLHomeActivity)
        }

        override fun onCaseListTapped(caseList: TTLCaseListModel) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onCaseListItemTapped(this@TTLHomeActivity, caseList.lastMessage)
        }

        override fun onFaqChildTapped(scfPath: TTLScfPathModel) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqChildTapped(this@TTLHomeActivity, scfPath)
        }

        override fun onTalkToAgentButtonTapped(scfPath: TTLScfPathModel) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onTalkToAgentButtonTapped(this@TTLHomeActivity, scfPath)
        }

        override fun onDownloadFileButtonTapped(fileMessage: TAPMessageModel) {
            downloadFile(fileMessage)
        }

        override fun onOpenFileButtonTapped(fileUri: Uri) {
            val mimeType = TAPFileUtils.getMimeTypeFromUri(this@TTLHomeActivity, fileUri)
            if (!mimeType.isNullOrEmpty()) {
                if (!TAPUtils.openFile(TAPTALK_INSTANCE_KEY, this@TTLHomeActivity, fileUri, mimeType)) {
                    showDownloadFileDialog()
                }
            }
        }

        override fun onFaqContentUrlTapped(scfPath: TTLScfPathModel, url: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentUrlTapped(this@TTLHomeActivity, scfPath, url)
        }

        override fun onFaqContentUrlLongPressed(scfPath: TTLScfPathModel, url: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentUrlLongPressed(this@TTLHomeActivity, scfPath, url)
        }

        override fun onFaqContentEmailTapped(scfPath: TTLScfPathModel, email: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentEmailAddressTapped(this@TTLHomeActivity, scfPath, email)
        }

        override fun onFaqContentEmailLongPressed(scfPath: TTLScfPathModel, email: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentEmailAddressLongPressed(this@TTLHomeActivity, scfPath, email)
        }

        override fun onFaqContentPhoneTapped(scfPath: TTLScfPathModel, phone: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentPhoneNumberTapped(this@TTLHomeActivity, scfPath, phone)
        }

        override fun onFaqContentPhoneLongPressed(scfPath: TTLScfPathModel, phone: String) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqContentPhoneNumberLongPressed(this@TTLHomeActivity, scfPath, phone)
        }
    }

    private fun openChannelUrl(channelLink: TTLChannelLinkModel) {
        if (channelLink.url.isEmpty()) {
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(channelLink.url)
            startActivity(intent)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initListeners() {
        TAPBroadcastManager.register(
            this,
            broadcastReceiver,
            SCF_PATH_UPDATED,
            JSON_TASK_COMPLETED
        )
        TapCoreMessageManager.getInstance(instanceKey).addMessageListener(messageListener)
    }

    private fun removeListeners() {
        TAPBroadcastManager.unregister(this, broadcastReceiver)
        TapCoreMessageManager.getInstance(instanceKey).removeMessageListener(messageListener)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                SCF_PATH_UPDATED -> {
                    adapter.items = generateAdapterItems()
                }
                JSON_TASK_COMPLETED -> {
                    val jsonUrl = intent.getStringExtra(JSON_URL)
                    val jsonString = intent.getStringExtra(JSON_STRING)
                    if (!jsonString.isNullOrEmpty()) {
                        if (adapter.items.isNotEmpty() && adapter.items.size > 1 && adapter.items[1].apiURL == jsonUrl) {
                            adapter.items[1].contentResponse = jsonString
                            adapter.notifyItemChanged(1)
                        }
                    }
                }
            }
        }
    }

    private fun fetchNewMessages() {
        if (!TTLDataManager.getInstance().checkActiveUserExists()) {
            return
        }
        TapCoreRoomListManager.getInstance(instanceKey).fetchNewMessageToDatabase(object : TapCommonListener() {
            override fun onSuccess(successMessage: String?) {
                adapter.refreshLatestCaseList()
            }
        })
    }

    private val messageListener = object : TapCoreMessageListener() {
        override fun onReceiveNewMessage(message: TAPMessageModel?) {
            processNewMessageFromSocket(message)
        }

        override fun onReceiveUpdatedMessage(message: TAPMessageModel?) {
            processNewMessageFromSocket(message)
        }

        override fun onMessageDeleted(message: TAPMessageModel?) {
            processNewMessageFromSocket(message)
        }
    }

    private fun processNewMessageFromSocket(message: TAPMessageModel?) {
        if (message != null && message.isHidden == false) {
            adapter.setLastMessage(message)
        }
    }

    private fun showDownloadFileDialog() {
        TapTalkDialog.Builder(this)
            .setTitle(getString(R.string.tap_error_could_not_find_file))
            .setMessage(getString(R.string.tap_error_redownload_file))
            .setCancelable(true)
            .setPrimaryButtonTitle(getString(R.string.tap_ok))
            .setSecondaryButtonTitle(getString(R.string.tap_cancel))
            .setPrimaryButtonListener { v: View? ->
                pendingDownloadMessage?.let { downloadFile(it) }
            }
            .show()
    }

    private fun downloadFile(fileMessage: TAPMessageModel) {
        if (!TAPUtils.hasPermissions(this, *TAPUtils.getStoragePermissions(true))) {
            // Request storage permission
            pendingDownloadMessage = fileMessage
            ActivityCompat.requestPermissions(
                this,
                TAPUtils.getStoragePermissions(true),
                PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
            return
        }
        pendingDownloadMessage = null
        TapCoreMessageManager.getInstance(TAPTALK_INSTANCE_KEY).downloadMessageFile(fileMessage, object : TapCoreFileDownloadListener() {
            override fun onProgress(message: TAPMessageModel?, percentage: Int, bytes: Long) {
                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = percentage
                    adapter.notifyItemChanged(1)
                }
            }

            override fun onSuccess(message: TAPMessageModel?, file: File?) {
                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = 100
                    adapter.notifyItemChanged(1)
                }
            }

            override fun onError(message: TAPMessageModel?, errorCode: String?, errorMessage: String?) {
                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = 0
                    adapter.notifyItemChanged(1)
                }
            }
        })
    }
}
