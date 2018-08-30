package com.ks.musicdownloader.activity;

interface URLValidatorTaskListener {

    void handleValidatorResult(ValidationResult validationResult, String url, String siteName);
}
