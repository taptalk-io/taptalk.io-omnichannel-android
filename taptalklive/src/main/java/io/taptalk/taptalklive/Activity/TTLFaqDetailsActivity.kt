package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.SimpleItemAnimator
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Helper.TAPFileUtils
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalkDialog
import io.taptalk.TapTalk.Listener.TapCoreFileDownloadListener
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.JSON_TASK_COMPLETED
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.NEW_CASE_CREATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_STRING
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_URL
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SCF_PATH
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.adapter.TTLHomeFaqAdapter
import io.taptalk.taptalklive.helper.TTLUtil
import kotlinx.android.synthetic.main.ttl_activity_home.*
import java.io.File

class TTLFaqDetailsActivity : TAPBaseActivity() {

    companion object {
        fun start(context: Context, scfPath: TTLScfPathModel) {
            val intent = Intent(context, TTLFaqDetailsActivity::class.java)
            intent.putExtra(SCF_PATH, scfPath)
            context.startActivity(intent)
            if (context is Activity) {
                context.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay)
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
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterBroadcastReceiver()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right)
        TapTalkLive.getInstance()?.tapTalkLiveListener?.onCloseButtonInFaqDetailsTapped(this, intent.getParcelableExtra(SCF_PATH))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (TAPUtils.allPermissionsGranted(grantResults)) {
            when (requestCode) {
                PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE -> {
                    Log.e(">>>>>>>>>", "PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE: downloadFile")
                    pendingDownloadMessage?.let { downloadFile(it) }
                }
            }
        }
    }

    private fun initView() {
        adapter = TTLHomeFaqAdapter(this, generateAdapterItems(), adapterListener, false)
        rv_home?.adapter = adapter
        rv_home?.layoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
            override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                }
                catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
        val messageAnimator = rv_home?.itemAnimator as SimpleItemAnimator?
        messageAnimator?.supportsChangeAnimations = false
    }

    private fun registerBroadcastReceiver() {
        TAPBroadcastManager.register(
            this,
            broadcastReceiver,
            NEW_CASE_CREATED,
            JSON_TASK_COMPLETED
        )
    }

    private fun unregisterBroadcastReceiver() {
        TAPBroadcastManager.unregister(this, broadcastReceiver)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NEW_CASE_CREATED -> {
                    finish()
                    overridePendingTransition(R.anim.tap_stay, R.anim.tap_stay)
                }
                JSON_TASK_COMPLETED -> {
                    val jsonUrl = intent.getStringExtra(JSON_URL)
                    val jsonString = intent.getStringExtra(JSON_STRING)
                    if (!jsonString.isNullOrEmpty()) {
                        if (adapter.items.isNotEmpty() && adapter.items[0].apiURL == jsonUrl) {
                            adapter.items[0].contentResponse = jsonString
                            adapter.notifyItemChanged(0)
                        }
//                        Log.e(">>>>>>>>>>>>>", "JSON_TASK_COMPLETED $jsonUrl: $jsonString");
                    }
                }
            }
        }
    }

    private fun generateAdapterItems(): ArrayList<TTLScfPathModel> {
        val itemList = ArrayList<TTLScfPathModel>()
        val scfPath = intent.getParcelableExtra<TTLScfPathModel>(SCF_PATH)
        if (scfPath != null) {
            if (scfPath.contentResponse.isNullOrEmpty() &&
                !scfPath.apiURL.isNullOrEmpty() &&
                !TapTalkLive.getContentResponseMap()[scfPath.apiURL].isNullOrEmpty()
            ) {
                scfPath.contentResponse = TapTalkLive.getContentResponseMap()[scfPath.apiURL]!!
            }
            Log.e(">>>>>>>>>>", "generateAdapterItems parent: ${scfPath.apiURL} - ${scfPath.contentResponse}")
            if (scfPath.childItems.isEmpty()) {
                itemList.add(scfPath)
            }
            else {
                val parentScf = scfPath.copy()
                parentScf.childItems = ArrayList()
                itemList.add(parentScf)
                itemList.addAll(scfPath.childItems)
            }
            TTLUtil.fetchScfPathContentResponse(scfPath, true)
        }
        if (itemList.isEmpty()) {
            onBackPressed()
        }
        return itemList
    }

    private val adapterListener = object: TTLHomeAdapterInterface {
        override fun onCloseButtonTapped() {
            onBackPressed()
        }

        override fun onFaqChildTapped(scfPath: TTLScfPathModel) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onFaqChildTapped(this@TTLFaqDetailsActivity, scfPath)
        }

        override fun onTalkToAgentButtonTapped(scfPath: TTLScfPathModel) {
            TapTalkLive.getInstance()?.tapTalkLiveListener?.onTalkToAgentButtonTapped(this@TTLFaqDetailsActivity, scfPath)
        }

        override fun onDownloadFileButtonTapped(fileMessage: TAPMessageModel) {
            downloadFile(fileMessage)
        }

        override fun onOpenFileButtonTapped(fileUri: Uri) {
            Log.e(">>>>>>>>>>>>>>>", "onOpenFileButtonTapped: $fileUri")
            val mimeType = TAPFileUtils.getMimeTypeFromUri(this@TTLFaqDetailsActivity, fileUri)
            Log.e(">>>>>>>>>>>>>>>", "onOpenFileButtonTapped mimeType: $mimeType")
            if (!mimeType.isNullOrEmpty()) {
                if (!TAPUtils.openFile(TAPTALK_INSTANCE_KEY, this@TTLFaqDetailsActivity, fileUri, mimeType)) {
                    showDownloadFileDialog()
                }
            }
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
            Log.e(">>>>>>>>>", "downloadFile : request permission")
            // Request storage permission
            pendingDownloadMessage = fileMessage
            ActivityCompat.requestPermissions(
                this,
                TAPUtils.getStoragePermissions(true),
                PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE
            )
            return
        }
//        Log.e(">>>>>>>>>", "downloadFile: ${TAPUtils.toJsonString(fileMessage.data)}")
        Log.e(">>>>>>>>>", "downloadFile: ${fileMessage.localID}")
        pendingDownloadMessage = null
        TapCoreMessageManager.getInstance(TAPTALK_INSTANCE_KEY).downloadMessageFile(fileMessage, object : TapCoreFileDownloadListener() {
            override fun onProgress(message: TAPMessageModel?, percentage: Int, bytes: Long) {
                Log.e(">>>>>>>>>", "downloadFile onProgress: ${message?.localID} - $percentage")
//                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = percentage
                    adapter.notifyItemChanged(0)
//                }
            }

            override fun onSuccess(message: TAPMessageModel?, file: File?) {
                Log.e(">>>>>>>>>", "downloadFile onSuccess: ${message?.localID} - ${file?.absolutePath}")
//                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = 100
                    adapter.notifyItemChanged(0)
//                }
            }

            override fun onError(message: TAPMessageModel?, errorCode: String?, errorMessage: String?) {
                Log.e(">>>>>>>>>", "downloadFile onError: ${message?.localID} - $errorMessage")
//                if (message?.localID == fileMessage.localID) {
                    adapter.fileDownloadProgress = 0
                    adapter.notifyItemChanged(0)
//                }
            }
        })
    }
}
