package com.app.mndalakanm.utils

import java.util.*

object Constant {
    var URL = "https://technorizen.com/restaurant/"

    //    var URL = "https://lamavie.ml/restaurant/"
    var BASE_URL = URL + "webservice/"
    var BASE_URL_IMAGE = URL + "uploads/images/"
    var PRIVACY = URL + "Privacy.html"
    var TERMS = URL + "terms.html"
    const val GOOGLE_API_KEY = "AIzaSyCPtKABcau-mG3VFY3ZEKGE2FHZOCrnD84"
    var STRIPE_TEST_KEY =
        "pk_test_51JhQFvCpH8njLNMpMBDi4lZmmbWelluhn0cyK9ClhumFHjJou909aYQXoSCH1Leq2hOqPsOSYhMviSIYKDgGI7pn009nXkSGIl"
    var SHARED_PREFERENCES_NAME = "VILBORG_TOWER_USER"
    var TYPE = "1"//USER
    var REGISTER_ID = "REGISTER_ID"
    var IS_LOGIN = "IS_LOGIN"
    var USER_ID = "USER_ID"
    var USER_TYPE = "USER_TYPE"
    var CHILD_ID = "CHILD_ID"
    var CHILD_Data = "CHILD_DATA"
    var CHILD_NAME = "CHILD_NAME"
    var LANGUAGE = "LANGUAGE"
    var PAIRINGCODE = "pairing_code"
    var LATITUDE = "latitude"
    var LONGITUDE = "longitude"
    var LOCK = "lock"
    var CODE = "code"
    var ISMEMBERSHIP = "false"
    var FIREBASETOKEN = "firebase_token"
    var CURRENCY = Currency.getInstance(Locale.GERMANY).symbol

    var search = "search"
    var rating = "rating"
    var distance = "distance"

}