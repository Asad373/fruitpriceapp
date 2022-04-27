package com.android.fruitpriceapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.fruitpriceapp.adapter.FruitAdapter;
import com.android.fruitpriceapp.databinding.ActivityViewBinding;
import com.android.fruitpriceapp.databinding.AddfruitdialougeBinding;
import com.android.fruitpriceapp.model.MyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ViewActivity extends com.android.fruitpriceapp.BaseActivity {
    ActivityViewBinding binding;
    ArrayList<MyModel> mylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.add.setVisibility(View.GONE);
        binding.add.setOnClickListener(view -> {
            //showDialog();
        });
        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOutUser();
            }
        });
        binding.rvUser.setHasFixedSize(true);
        binding.rvUser.setLayoutManager(new LinearLayoutManager(this));
        getdata();
    }

    private void SignOutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
        startActivity(intent);
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        AddfruitdialougeBinding dialoguebinding = AddfruitdialougeBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(dialoguebinding.getRoot());


        dialoguebinding.submit.setOnClickListener(v -> {
            if (checkcondition(dialoguebinding.name, "Enter Fruit Name") && checkcondition(dialoguebinding.dis, "Enter Fruit description")) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyyhhmmss", Locale.ENGLISH);
                String date = simpleDateFormat.format(new Date());
                rootRef.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> UserMap = new HashMap<>();
                        UserMap.put("name", String.valueOf(dialoguebinding.name.getText()));
                        UserMap.put("id", date);
                        UserMap.put("des", String.valueOf(dialoguebinding.dis.getText()));
                        rootRef.getReference().child(uid).child(date).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                    getdata();
                                } else {
                                    Toast.makeText(ViewActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingBar.dismiss();
                    }
                });

            }
            dialog.dismiss();
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void getdata() {
        mylist = new ArrayList<>();
        loadingBar.show();
        rootRef.getReference().child("Fruits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    MyModel myvalue = datas.getValue(MyModel.class);
                    mylist.add(myvalue);

                }
                FruitAdapter adapter = new FruitAdapter(ViewActivity.this, mylist);
                if (adapter.getItemCount() == 0) {
                    binding.main.setVisibility(View.GONE);
                    binding.progressCircularlayout.setVisibility(View.VISIBLE);
                } else {
                    binding.main.setVisibility(View.VISIBLE);
                    binding.progressCircularlayout.setVisibility(View.GONE);
                }
                binding.rvUser.setAdapter(adapter);

                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ViewActivity.this, databaseError + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}