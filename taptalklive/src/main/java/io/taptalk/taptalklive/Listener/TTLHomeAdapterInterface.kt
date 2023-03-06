package io.taptalk.taptalklive.Listener

import io.taptalk.TapTalk.Model.TAPMessageModel

interface TTLHomeAdapterInterface {
    fun onCloseButtonTapped() {

    }

    fun onChannelLinkSelected(position: Int) {

    }

    fun onNewMessageButtonTapped() {

    }

    fun onSeeAllMessagesButtonTapped() {

    }

    fun onChatRoomTapped(lastMessage: TAPMessageModel) {

    }
}
