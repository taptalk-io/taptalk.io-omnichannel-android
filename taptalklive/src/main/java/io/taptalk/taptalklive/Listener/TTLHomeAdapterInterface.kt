package io.taptalk.taptalklive.Listener

import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel

interface TTLHomeAdapterInterface {
    fun onCloseButtonTapped() {

    }

    fun onChannelLinkSelected(channelLink: TTLChannelLinkModel?, position: Int) {

    }

    fun onNewMessageButtonTapped() {

    }

    fun onSeeAllMessagesButtonTapped() {

    }

    fun onChatRoomTapped(lastMessage: TAPMessageModel) {

    }
}
