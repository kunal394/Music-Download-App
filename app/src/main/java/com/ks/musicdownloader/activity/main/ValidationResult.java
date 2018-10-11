package com.ks.musicdownloader.activity.main;

import android.content.Context;

import com.ks.musicdownloader.Utils.ToastUtils;
import com.ks.musicdownloader.activity.common.Constants;

public enum ValidationResult implements ValidationResultService {

    NO_EXTERNAL_STORAGE_PERMISSION(Constants.NO_EXTERNAL_STORAGE_PERMISSION_MESSAGE),
    NO_INTERNET(Constants.NO_INTERNET_MESSAGE),
    NO_URL_PROVIDED(Constants.NO_URL_PROVIDED_MESSAGE),
    INVALID_URL(Constants.INVALID_URL_MESSAGE),
    UNSUPPORTED_SITE(Constants.UNSUPPORTED_SITE_MESSAGE),
    NON_EXISTENT_URL(Constants.NON_EXISTENT_URL_MESSAGE),
    VALID_URL(Constants.VALID_URL_MESSAGE, true),
    PARSING_ERROR(Constants.PARSE_ERROR_MESSAGE);

    private final String message;
    private final boolean validResult;

    ValidationResult(String message) {
        this(message, false);
    }

    ValidationResult(String message, boolean validResult) {
        this.message = message;
        this.validResult = validResult;
    }

    public boolean isValidResult() {
        return validResult;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void displayToast(Context context) {
        ToastUtils.displayLongToast(context, message);
    }
}
