package io.taptalk.taptalklive.Interface

import android.net.Uri
import io.taptalk.TapTalk.Model.TAPMessageModel
import io.taptalk.taptalklive.API.Model.TTLChannelLinkModel
import io.taptalk.taptalklive.API.Model.TTLScfPathModel
import io.taptalk.taptalklive.model.TTLCaseListModel

interface TTLHomeAdapterInterface {
    fun onCloseButtonTapped() {

    }

    fun onChannelLinkSelected(channelLink: TTLChannelLinkModel, position: Int) {

    }

    fun onNewMessageButtonTapped() {

    }

    fun onSeeAllMessagesButtonTapped() {

    }

    fun onCaseListTapped(caseList: TTLCaseListModel) {

    }

    fun onFaqChildTapped(scfPath: TTLScfPathModel) {

    }

    fun onTalkToAgentButtonTapped(scfPath: TTLScfPathModel) {

    }

    fun onDownloadFileButtonTapped(fileMessage: TAPMessageModel) {

    }

    fun onOpenFileButtonTapped(fileUri: Uri) {

    }

    fun onFaqContentUrlTapped(scfPath: TTLScfPathModel, url: String) {

    }

    fun onFaqContentUrlLongPressed(scfPath: TTLScfPathModel, url: String) {

    }

    fun onFaqContentEmailTapped(scfPath: TTLScfPathModel, email: String) {

    }

    fun onFaqContentEmailLongPressed(scfPath: TTLScfPathModel, email: String) {

    }

    fun onFaqContentPhoneTapped(scfPath: TTLScfPathModel, phone: String) {

    }

    fun onFaqContentPhoneLongPressed(scfPath: TTLScfPathModel, phone: String) {

    }
}
