package com.android.fruitpriceapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.fruitpriceapp.model.MyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BaseActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    FirebaseDatabase rootRef;
    String uid;

    ProgressDialog loadingBar;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance();
        ;
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Processing");
        loadingBar.setMessage("please wait for while");
        loadingBar.setCanceledOnTouchOutside(false);
    }

    public Boolean checkcondition(@NonNull EditText editText, String toast) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError(toast);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    public Boolean checkcondition_spinner(@NonNull Spinner spinner, String toast) {
        if (spinner.getSelectedItem().equals("Select user type")) {
            Toast.makeText(this,toast, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }

    }


    public void customAlertDialogue(String message) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                dialog.dismiss();

                            }
                        }).show();
    }
}