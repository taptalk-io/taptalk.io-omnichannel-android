package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import io.taptalk.TapTalk.Helper.TAPBroadcastManager
import io.taptalk.TapTalk.Manager.TapCoreMessageManager
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant
import io.taptalk.taptalklive.Const.TTLConstant.Broadcast.NEW_CASE_CREATED
import io.taptalk.taptalklive.Const.TTLConstant.Extras.SCF_PATH
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.TapTalkLive
import io.taptalk.taptalklive.adapter.TTLHomeFaqAdapter
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
        TAPBroadcastManager.register(this, broadcastReceiver, NEW_CASE_CREATED)
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
            }
        }
    }

    private fun generateAdapterItems(): ArrayList<TTLScfPathModel> {
        val itemList = ArrayList<TTLScfPathModel>()
        val scfPath = intent.getParcelableExtra<TTLScfPathModel>(SCF_PATH)
        if (scfPath != null) {
            if (scfPath.childItems.isEmpty()) {
                itemList.add(scfPath)
            }
            else {
                val parentScf = scfPath.copy()
                parentScf.childItems = ArrayList()
                itemList.add(parentScf)
                itemList.addAll(scfPath.childItems)
            }
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
    }
}
