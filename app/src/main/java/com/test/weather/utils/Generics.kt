package com.test.weather.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Show snack bar
 */
 fun showSnackBar(msg: String, view: View) {
    Snackbar
        .make(view, msg, 5000)
        .setActionTextColor(Color.MAGENTA)
        .show();
}