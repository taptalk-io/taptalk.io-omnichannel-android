package io.taptalk.taptalklive.Listener

import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.model.TTLCaseListModel

interface TTLHomeAdapterInterface {
    fun onCloseButtonTapped() {

    }

    fun onChannelLinkSelected(channelLink: TTLChannelLinkModel?, position: Int) {

    }

    fun onNewMessageButtonTapped() {

    }

    fun onSeeAllMessagesButtonTapped() {

    }

    fun onCaseListTapped(caseList: TTLCaseListModel) {

    }
}
