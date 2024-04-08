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

    object MessageTypeString {
        const val TEXT = "text"
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val FILE = "file"
        const val DOCUMENT = "document"
        const val LOCATION = "location"
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
        const val QNA_VIA_API = "qna_via_api"
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
        const val JSON_TASK_COMPLETED = "kTapTalkLiveJsonTaskCompleted"
    }

    object Extras {
        const val MESSAGE = "kTapTalkLiveExtrasMessage"
        const val SHOW_CLOSE_BUTTON = "kTapTalkLiveExtrasShowCloseButton"
        const val SCF_PATH = "kTapTalkLiveExtrasScfPath"
        const val CASE_DETAILS = "kTapTalkLiveCaseDetails"
        const val JSON_URL = "kTapTalkLiveJsonUrl"
        const val JSON_STRING = "kTapTalkLiveJsonString"
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

    object ErrorCode {
        const val ERROR_CODE_INVALID_TOPIC_ID = "91001"
        const val ERROR_CODE_MESSAGE_REQUIRED = "91002"
    }

    object ErrorMessage {
        const val ERROR_MESSAGE_TAPTALKLIVE_NOT_INITIALIZED = "TapTalkLive instance is not initialized, please initialize by calling TapTalkLive.init()"
        const val ERROR_MESSAGE_ACTIVE_USER_NOT_FOUND = "Active user not found, please authenticate by calling authenticateUser()"
        const val ERROR_MESSAGE_INVALID_TOPIC_ID = "Invalid Topic ID"
        const val ERROR_MESSAGE_MESSAGE_REQUIRED = "Message may not be empty"
    }
}
