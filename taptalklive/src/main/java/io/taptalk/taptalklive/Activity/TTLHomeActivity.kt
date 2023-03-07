package io.taptalk.taptalklive.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY
import io.taptalk.taptalklive.Listener.TTLHomeAdapterInterface
import io.taptalk.taptalklive.R
import io.taptalk.taptalklive.adapter.TTLHomeAdapter
import io.taptalk.taptalklive.model.TTLCaseListModel
import kotlinx.android.synthetic.main.ttl_activity_home.*

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

    private lateinit var adapter: TTLHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ttl_activity_home)

        instanceKey = TAPTALK_INSTANCE_KEY
        initView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down)
    }

    private fun initView() {
        val homeItemList = ArrayList<TTLScfPathModel>()
        homeItemList.add(TTLScfPathModel())
        adapter = TTLHomeAdapter(this, homeItemList, adapterListener)
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

    private val adapterListener = object: TTLHomeAdapterInterface {
        override fun onCloseButtonTapped() {
            onBackPressed()
        }

        override fun onChannelLinkSelected(channelLink: TTLChannelLinkModel, position: Int) {
            openChannelUrl(channelLink)
        }

        override fun onNewMessageButtonTapped() {
            openCreateCaseForm()
        }

        override fun onSeeAllMessagesButtonTapped() {
            openCaseList()
        }

        override fun onCaseListTapped(caseList: TTLCaseListModel) {
            openChatRoom(caseList)
        }
    }

    private fun openCreateCaseForm() {
        TTLCreateCaseFormActivity.start(this, true)
    }

    private fun openCaseList() {
        TTLCaseListActivity.start(this)
    }

    private fun openChatRoom(caseList: TTLCaseListModel) {
        val room: TAPRoomModel = caseList.lastMessage.room
        TapUIChatActivity.start(
            this@TTLHomeActivity,
            TAPTALK_INSTANCE_KEY,
            room.roomID,
            room.name,
            room.imageURL,
            room.type,
            room.color
        )
    }

    private fun openChannelUrl(channelLink: TTLChannelLinkModel) {
        if (channelLink.url.isEmpty() || !Patterns.WEB_URL.matcher(channelLink.url).matches()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(channelLink.url)
        startActivity(intent)
    }
}
