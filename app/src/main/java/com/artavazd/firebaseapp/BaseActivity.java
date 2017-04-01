package com.artavazd.firebaseapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by artavazdghazaryan on 4/1/17.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.progress_message));
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }



}
