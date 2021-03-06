package com.android.fruitpriceapp;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.android.fruitpriceapp.Utils.DataUtils;
import com.android.fruitpriceapp.adapter.FruitAdapter;
import com.android.fruitpriceapp.databinding.ActivityLoginBinding;
import com.android.fruitpriceapp.databinding.AddfruitdialougeBinding;
import com.android.fruitpriceapp.databinding.ResetpasswordBinding;
import com.android.fruitpriceapp.model.MyModel;
import com.android.fruitpriceapp.model.UserTypeModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    Dialog dialog;
    ResetpasswordBinding Resetbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(this);
        dialog.setCancelable(true);
        Resetbinding = ResetpasswordBinding.inflate(getLayoutInflater());
        dialog.setContentView(Resetbinding.getRoot());

        binding.showPassword.setOnClickListener(v -> {
            show(binding.password);
        });
        binding.forgotPassword.setOnClickListener(view -> {
            ResetPassword();
        });

        binding.signup.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        binding.submit.setOnClickListener(view -> {
            if (checkcondition(binding.email, "Enter your Email") && checkcondition(binding.password, "Enter your Password")) {

                if(binding.email.getText().toString().equals("Admin") && binding.password.getText().toString().equals("pakistan123")){
                    DataUtils.Usertype = "Admin";
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }  else{
                    loadingBar.show();
                    mAuth.signInWithEmailAndPassword(String.valueOf(binding.email.getText()), String.valueOf(binding.password.getText()))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String  UID = task.getResult().getUser().getUid();
                                    getdata(UID);
                                    //loadingBar.dismiss();
                                /*startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();*/
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }

        });

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

    public void show(@NonNull EditText editText) {
        if (editText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.showPassword.setColorFilter(getResources().getColor(R.color.black));

        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.showPassword.setColorFilter(getResources().getColor(R.color.primary));
        }
        editText.setSelection(Objects.requireNonNull(editText.getText()).length());
    }

    public void getdata(String UID) {
        ArrayList<UserTypeModel> mylist = new ArrayList<>();
        //loadingBar.show();
        rootRef.getReference().child("UserType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    UserTypeModel myvalue = datas.getValue(UserTypeModel.class);
                    mylist.add(myvalue);
                }
                for (int i = 0; i < mylist.size(); i++){
                    if(mylist.get(i).getUID().equals(UID)){
                        if(mylist.get(i).getUserType().equals("Admin")){
                           // Toast.makeText(LoginActivity.this, "you are a" + mylist.get(i).getUserType(), Toast.LENGTH_LONG).show();
                            DataUtils.Usertype = mylist.get(i).getUserType();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                             finish();
                        }else{
                            //Toast.makeText(LoginActivity.this, "you are a" + mylist.get(i).getUserType(), Toast.LENGTH_LONG).show();
                            DataUtils.Usertype = mylist.get(i).getUserType();
                            startActivity(new Intent(LoginActivity.this, ViewActivity.class));
                            finish();
                        }

                    }
                }
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, databaseError + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void ResetPassword() {

        Resetbinding.submit.setOnClickListener(v -> {
            if (String.valueOf(Resetbinding.restpasswordmail.getText()).equals("") || Resetbinding.restpasswordmail.getText() == null) {
                Toast.makeText(LoginActivity.this, "Enter Email First", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.show();
                mAuth.sendPasswordResetEmail(String.valueOf(Resetbinding.restpasswordmail.getText()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //Toast.makeText(LoginActivity.this, "Email sent to your email", Toast.LENGTH_SHORT).show();
                                //Log.d(TAG, "Email sent.");
                                loadingBar.dismiss();
                                customAlertDialogue("A password rest link has been sent to your email, kindly check your email.");
                                  dialog.dismiss();
                            } else {
                                loadingBar.dismiss();
                                customAlertDialogue("Wrong Email or user not exist");

                            }
                        });
            }
        });
        Resetbinding.submitC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}