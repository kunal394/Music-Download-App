package com.ks.musicdownloader.activity;

import android.content.Context;

import com.ks.musicdownloader.Constants;
import com.ks.musicdownloader.Utils.ToastUtils;

public enum ValidationResult implements ValidationResultService {

    NO_INTERNET(Constants.NO_INTERNET_MESSAGE, false),
    NO_URL_PROVIDED(Constants.NO_URL_PROVIDED_MESSAGE, false),
    INVALID_URL(Constants.INVALID_URL_MESSAGE, false),
    UNSUPPORTED_SITE(Constants.UNSUPPORTED_SITE_MESSAGE, false),
    NON_EXISTENT_URL(Constants.NON_EXISTENT_URL_MESSAGE, false),
    VALID_URL(Constants.VALID_URL_MESSAGE, true);

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
