package io.taptalk.taptalklive.Const

class TTLConstant {
    object Api {
        const val API_VERSION = "v1"
    }

    object TapTalkInstanceKey {
        const val TAPTALK_INSTANCE_KEY = "TapTalkOmniChannelInstanceKey"
    }

    object MessageType {
        const val TYPE_CLOSE_CASE = 3001
        const val TYPE_REOPEN_CASE = 3002
        const val TYPE_REVIEW = 3003
        const val TYPE_REVIEW_SUBMITTED = 3004
        const val TYPE_CASE_CREATED = 3005
        const val TYPE_CASE_AGENT_CHANGED = 3006
        const val TYPE_CASE_DETAILS_UPDATED = 3007
        const val TYPE_BROADCAST_TEXT_MESSAGE = 3011 // Also used for WABA broadcast template
        const val TYPE_BROADCAST_IMAGE_MESSAGE = 3012
        const val TYPE_BROADCAST_VIDEO_MESSAGE = 3013
        const val TYPE_BROADCAST_FILE_MESSAGE = 3014
        const val TYPE_WABA_TEMPLATE_TEXT_MESSAGE = 3021
        const val TYPE_WABA_TEMPLATE_IMAGE_MESSAGE = 3022
        const val TYPE_WABA_TEMPLATE_VIDEO_MESSAGE = 3023
        const val TYPE_WABA_TEMPLATE_FILE_MESSAGE = 3024
    }

    object CaseMedium {
        const val LAUNCHER = "launcher"
        const val WHATSAPP_SME = "whatsapp"
        const val WHATSAPP_BA = "whatsappba"
        const val TELEGRAM = "telegram"
        const val LINE = "line"
        const val TWITTER = "twitter"
        const val FACEBOOK = "facebook"
        const val INSTAGRAM = "instagram"
        const val GOOGLE_BUSINESS = "google_business"
        const val LINKEDIN = "linkedin"
    }

    object ScfPathType {
        const val QNA = "qna"
        const val TALK_TO_AGENT = "talk_to_agent"
    }

    object RequestCode {
        const val REVIEW = 1001
    }

    object Form {
        const val REVIEW_CHARACTER_LIMIT = 1000
    }

    object Broadcast {
        const val SCF_PATH_UPDATED = "kTapTalkLiveScfPathUpdated"
        const val NEW_CASE_CREATED = "kTapTalkLiveNewCaseCreated"
    }

    object Extras {
        const val MESSAGE = "kTapTalkLiveExtrasMessage"
        const val SHOW_CLOSE_BUTTON = "kTapTalkLiveExtrasShowCloseButton"
        const val SCF_PATH = "kTapTalkLiveExtrasScfPath"
        const val CASE_DETAILS = "kTapTalkLiveCaseDetails"
    }

    object PreferenceKey {
        const val APP_KEY_SECRET = "kTapTalkLiveApplicationKeySecret"
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
        const val CHANNEL_LINKS = "kTapTalkLiveChannelLinks"
        const val SCF_PATH = "kTapTalkLiveScfPath"
        const val TOPICS = "kTapTalkLiveTopics"
        const val CASE_EXISTS = "kTapTalkLiveCaseExists"
    }
}
