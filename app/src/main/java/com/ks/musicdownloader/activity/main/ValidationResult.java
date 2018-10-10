package com.ks.musicdownloader.activity.main;

import android.content.Context;

import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.common.Constants;

public enum ValidationResult implements ValidationResultService {

    NO_EXTERNAL_STORAGE_PERMISSION(Constants.NO_EXTERNAL_STORAGE_PERMISSION_MESSAGE, false),
    NO_INTERNET(Constants.NO_INTERNET_MESSAGE, false),
    NO_URL_PROVIDED(Constants.NO_URL_PROVIDED_MESSAGE, false),
    INVALID_URL(Constants.INVALID_URL_MESSAGE, false),
    UNSUPPORTED_SITE(Constants.UNSUPPORTED_SITE_MESSAGE, false),
    NON_EXISTENT_URL(Constants.NON_EXISTENT_URL_MESSAGE, false),
    VALID_URL(Constants.VALID_URL_MESSAGE, true),
    PARSING_ERROR(Constants.PARSE_ERROR_MESSAGE, false);

    private final String message;
    private final boolean validResult;

    ValidationResult(String message, boolean validResult) {
        this.message = message;
        this.validResult = validResult;
    }

    public boolean isValidResult() {
        return validResult;
    }

    @Override
    public void displayToast(Context context) {
        ToastUtils.displayLongToast(context, message);
    }
}
