package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.JSON_TASK_COMPLETED
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.NEW_CASE_CREATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_STRING
import io.taptalk.taptalklive.Const.TTLConstant.Extras.JSON_URL
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SCF_PATH
import io.taptalk.taptalklive.Const.TTLConstant.ScfPathType.QNA_VIA_API
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.adapter.TTLHomeFaqAdapter
import io.taptalk.taptalklive.helper.JsonTask
import io.taptalk.taptalklive.helper.TTLUtil
import kotlinx.android.synthetic.main.ttl_activity_home.*

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
    private lateinit var adapterItems: ArrayList<TTLScfPathModel>
    private lateinit var scfMap: HashMap<String /*apiURL*/, TTLScfPathModel>
    private var contentResponseMap: HashMap<String /*apiURL*/, String /*contentResponse*/> = HashMap()
    private var contentResponse: String? = null

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

    private fun initView() {
        adapter = TTLHomeFaqAdapter(this, generateAdapterItems(), adapterListener, false)
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
                        if (adapterItems.isNotEmpty() && adapterItems[0].apiURL == jsonUrl) {
                            adapterItems[0].contentResponse = jsonString
                            adapter.items = adapterItems
                        }
                        else {
                            val child = scfMap[jsonUrl]
                            if (child != null) {
                                child.contentResponse = jsonString
                            }
                        }
                        if (!jsonUrl.isNullOrEmpty()) {
                            contentResponseMap[jsonUrl] = jsonString
                        }
//                        contentResponse = jsonString
//                        adapter.items = generateAdapterItems()
                        Log.e(">>>>>>>>>>>>>", "JSON_TASK_COMPLETED $jsonUrl: $jsonString");
                    }
                }
            }
        }
    }

    private fun generateAdapterItems(): ArrayList<TTLScfPathModel> {
        val itemList = ArrayList<TTLScfPathModel>()
        val scfPath = intent.getParcelableExtra<TTLScfPathModel>(SCF_PATH)
        if (scfPath != null) {
            Log.e(">>>>>>>>>>", "generateAdapterItems parent: ${scfPath.apiURL} - ${scfPath.contentResponse}")
            if (scfPath.contentResponse.isNullOrEmpty() &&
                !scfPath.apiURL.isNullOrEmpty() &&
                !contentResponseMap[scfPath.apiURL].isNullOrEmpty()
            ) {
                scfPath.contentResponse = contentResponseMap[scfPath.apiURL]!!
            }
            if (scfPath.childItems.isEmpty()) {
                itemList.add(scfPath)
            }
            else {
                val parentScf = scfPath.copy()
                parentScf.childItems = ArrayList()
                itemList.add(parentScf)
                for (child in scfPath.childItems) {
                    Log.e(">>>>>>>>>>", "generateAdapterItems child: ${child.apiURL} - ${child.contentResponse}")
                    if (child.contentResponse.isNullOrEmpty() &&
                        !child.apiURL.isNullOrEmpty() &&
                        !contentResponseMap[child.apiURL].isNullOrEmpty()
                    ) {
                        child.contentResponse = contentResponseMap[child.apiURL]!!
                    }
                }
                itemList.addAll(scfPath.childItems)
            }
            scfMap = TTLUtil.fetchScfPathContentResponse(scfPath, true)
        }
        if (itemList.isEmpty()) {
            onBackPressed()
        }
        adapterItems = itemList
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
    }
}
