package com.android.fruitpriceapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.android.fruitpriceapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity {
    ActivitySignupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        String[] securityArray = {"Gate", "Security"};


        binding.login.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
        binding.submit.setOnClickListener(view -> {

            if (checkcondition(binding.firstname, "Enter your FirstName") && checkcondition(binding.lastname, "Enter your LastName") && checkcondition(binding.email, "Enter your Email") && isValidEmail(binding.email) && checkcondition(binding.password, "Enter your Password") && passwordmatch(binding.password, binding.confirmPassword) && checkcondition(binding.contacNumber, "Enter your Phone Number") && checkcondition(binding.address, "Enter your Phone Number") /*&& checkcondition_spinner(binding.type, "please select type")*/) {
                loadingBar.show();
                mAuth.createUserWithEmailAndPassword(String.valueOf(binding.email.getText()), String.valueOf(binding.password.getText()))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //loadingBar.dismiss();
                                String UID = task.getResult().getUser().getUid();
                                String name = binding.firstname.getText().toString() + " " + binding.lastname.getText().toString();
                                String contact = binding.contacNumber.getText().toString();
                                String email = binding.email.getText().toString();
                                String Address = binding.address.getText().toString();
                                //String Type = binding.type.getSelectedItem().toString();
                                String Type = "Public";
                                addUserType(UID, Type, name,email,contact,Address);
                                //startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                //finish();
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
        binding.showPassword.setOnClickListener(v -> {
            show(binding.password);
            show(binding.confirmPassword);
        });
    }

    private void addUserType(String id,  String type, String name, String email, String contactNumber, String Address) {
        //loadingBar.show();
        rootRef.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> UserMap = new HashMap<>();
                UserMap.put("UID", id);
                UserMap.put("userType", type);
                UserMap.put("userName",name);
                UserMap.put("Email", email);
                UserMap.put("ContactNumber", contactNumber);
                UserMap.put("Address",Address);

                rootRef.getReference().child("UserType").child(id).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                            //dialog.dismiss();
                        } else {
                            //dialog.dismiss();
                            loadingBar.dismiss();
                            Toast.makeText(SignupActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                //dialog.dismiss();
            }
        });
    }

    // This fun Make sure that the the input for signup is not Empty


    //check the password is valid or not
    public boolean isValidPassword(@NonNull EditText editText) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(editText.getText().toString());
        if (!matcher.matches()) {
            editText.setError("Your Password Must have Uppercase letters: A-Z. Lowercase letters: a-z. Numbers: 0-9");
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    //Another Validation for Password
    public boolean passwordmatch(@NonNull EditText editText1, @NonNull EditText editText2) {
        if (editText1.getText().toString().equals(editText2.getText().toString())) {
            return true;

        } else {
            editText2.setError("Your password is not matched");
            editText2.requestFocus();
            return false;
        }

    }

    //check the email is valid or not
    public boolean isValidEmail(@NonNull EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
            editText.setError("Please Enter Valid Email");
            editText.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    //for show password
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


}