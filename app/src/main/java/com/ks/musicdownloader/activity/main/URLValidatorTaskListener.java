package com.ks.musicdownloader.activity.main;

interface URLValidatorTaskListener {

    void handleValidatorResult(ValidationResult validationResult, String url, String siteName);
}
