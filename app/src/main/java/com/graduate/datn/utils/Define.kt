package com.graduate.datn.utils

open class Define {

    open class Api {
        val CONTENT_TYPE = "Content-Type: application/json"
        val LOGIN_URL = "app_api/v1/auth/user"

        companion object {
            const val ERROR_CODE = "error_code"
            const val ERROR_MESSAGE = "error_message"
            const val NO_NETWORK_CONNECTION = "NO_NETWORK_CONNECTION"
            const val TIME_OUT = "TIME_OUT"
            const val UNKNOWN = "UNKNOWN"
        }

        object BaseResponse {
            val SUCCESS = "success"
            val DATA = "data"
            val PAGE = "page"
            val ERROR = "error"

        }


        object Key {
            val AUTHORIZATION = "Authorization"
            val LOGIN_NAME = "login_name"
            val PASSWORD = "password"
            val TOKEN = "token"
        }

        object Http {
            val RESPONSE_CODE_ACCESS_TOKEN_EXPIRED = 403
        }
    }

    object Bus {
        val ACCESS_TOKEN_EXPIRED = "ACCESS_TOKEN_EXPIRED"
    }

    open class ResponseStatus {
        companion object {
            const val LOADING = 1
            const val SUCCESS = 2
            const val ERROR = 0
        }
    }

    class Database {

        object User {
            val TABLE_NAME = "dtb_user"
            val ID = "id"
            val LOG_IN = "login"
            val AVATAR_URL = "avatar_url"
            val NAME = "name"
            val COMPANY = "company"
            val EMAIL = "email"
            val LOCATION = "location"
        }

        object Repo {
            val TABLE_NAME = "dtb_repo"
            val ID = "id"
            val NAME = "name"
            val FULL_NAME = "full_name"
            val DESCRIPTION = "description"
            val CONTRIBUTORS_URL = "contributors_url"
        }

        companion object {
            val DATABASE_VERSION = 1
            val DATABASE_NAME = "app_database"
        }
    }

    object Intent {
        val REPO_OWNER = "repo_owner"
        val REPO_NAME = "repo_name"
    }

    object PreferenceKey {
        val IS_FIRST_LAUNCH = "isHaveMasterData"
        val USER_INFO = "userInfo"
        val MASTER_DATA = "masterData"
    }

    companion object {

        val PREF_FILE_NAME = "oab_pref"
        val REALM_NAME = "oab_database"

        val DEFAULT_TIMEOUT = 60L
        val CLICK_TIME_INTERVAL = 300L
        val IMAGE_IDENTIFY_PATH = "knd_image"
    }

}
