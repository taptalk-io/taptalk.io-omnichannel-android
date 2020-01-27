package io.taptalk.taptalklive.Const

class TTLConstant {
    object MessageType {
        const val TYPE_CLOSE_CASE = 3001
        const val TYPE_REOPEN_CASE = 3002
        const val TYPE_REVIEW = 3003
    }

    object RequestCode {
        const val REVIEW = 1001
    }

    object Extras {
        const val MESSAGE = "kTapTalkLiveExtrasMessage"
        const val SHOW_CLOSE_BUTTON = "kTapTalkLiveExtrasShowCloseButton"
    }

    object PreferenceKey {
        const val AUTH_TICKET = "kTapTalkLiveAuthenticationTicket"
        const val REFRESH_TOKEN = "kTapTalkLiveRefreshToken"
        const val REFRESH_TOKEN_EXPIRY = "kTapTalkLiveRefreshTokenExpiry"
        const val ACCESS_TOKEN = "kTapTalkLiveAccessToken"
        const val ACCESS_TOKEN_EXPIRY = "kTapTalkLiveAccessTokenExpiry"
        const val ACTIVE_USER = "kTapTalkLiveActiveUser"
        const val TAPTALK_API_URL = "kTapTalkLiveTapTalkApiUrl"
        const val TAPTALK_APP_KEY_ID = "kTapTalkLiveTapTalkAppKeyId"
        const val TAPTALK_APP_KEY_SECRET = "kTapTalkLiveTapTalkAppKeySecret"
        const val TAPTALK_AUTH_TICKET = "kTapTalkLiveTapTalkAuthenticationTicket"
        const val CASE_EXISTS = "kTapTalkLiveCaseExists"
    }

    object CustomKeyboard {
        const val MARK_AS_SOLVED = "Mark as solved"
    }
}
