package io.taptalk.taptalklive.Const

class TTLConstant {
    object RoomType {
        const val TYPE_TRANSACTION = 4
    }

    object MessageType {
        const val TYPE_REVIEW = 3001
    }

    object RequestCode {
        const val REVIEW = 1001
    }

    object PreferenceKey {
        const val AUTH_TICKET = "kTapTalkLiveAuthenticationTicket"
        const val REFRESH_TOKEN = "kTapTalkLiveRefreshToken"
        const val REFRESH_TOKEN_EXPIRY = "kTapTalkLiveRefreshTokenExpiry"
        const val ACCESS_TOKEN = "kTapTalkLiveAccessToken"
        const val ACCESS_TOKEN_EXPIRY = "kTapTalkLiveAccessTokenExpiry"
        const val ACTIVE_USER = "kTapTalkLiveActiveUser"
    }
}
