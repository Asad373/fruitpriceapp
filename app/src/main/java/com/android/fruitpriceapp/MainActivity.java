package com.android.fruitpriceapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.fruitpriceapp.Utils.DataUtils;
import com.android.fruitpriceapp.adapter.FruitAdapter;
import com.android.fruitpriceapp.databinding.ActivityMainBinding;
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

public class MainActivity extends BaseActivity {
    ArrayList<MyModel> mylist;
    Dialog dialog;
    ActivityMainBinding binding;
    AddfruitdialougeBinding dialoguebinding;
    boolean updateReocrd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateReocrd = false;
        dialog = new Dialog(this);
        //dialog.requestWindowFeature(Window.);
        //dialog.setTitle("Add Fruit");
        dialog.setCancelable(true);
        dialoguebinding = AddfruitdialougeBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialoguebinding.getRoot());

        binding.addFruite.setOnClickListener(view -> {
                showDialog(null, 0, false);
        });

        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOutUser();
            }
        });
        //data
        binding.adminRecycle.setHasFixedSize(true);
        binding.adminRecycle.setLayoutManager(new LinearLayoutManager(this));
        getdata();

    }

    public  void showDialog(MyModel model, int Position, boolean falg) {

        dialoguebinding.submit.setOnClickListener(v -> {
            if(falg){
                dialoguebinding.dialogTitle.setText("Update Fruit");
                updateRecord(model);
                updateReocrd = false;
            }else{
                dialoguebinding.dialogTitle.setText("Add Fruit");
                addFruit();
            }

        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void SignOutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
        startActivity(intent);
    }

    public void getdata() {
        mylist = new ArrayList<>();
        //loadingBar.show();
        rootRef.getReference().child("Fruits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    MyModel myvalue = datas.getValue(MyModel.class);
                    mylist.add(myvalue);

                }
                setAdapter();
                loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this, databaseError + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        FruitAdapter adapter = new FruitAdapter(MainActivity.this, mylist);
        if (adapter.getItemCount() == 0) {
            binding.adminRecycle.setVisibility(View.GONE);
            binding.record.setVisibility(View.VISIBLE);
        } else {
            binding.adminRecycle.setVisibility(View.VISIBLE);
            binding.record.setVisibility(View.GONE);
        }

        adapter.setUpdate(new FruitAdapter.updateItem() {
            @Override
            public void editClickListner(int position) {
            MyModel model = mylist.get(position);
            updateReocrd  = true;
            dialoguebinding.name.setText(model.getName());
            dialoguebinding.price.setText(model.getPrice());
            dialoguebinding.dis.setText(model.getDes());
            showDialog(model, position, updateReocrd);
            //updateRecord(model);
            }
        });
        adapter.setDelete(new FruitAdapter.deleteItem() {
            @Override
            public void deleteClickListner(int position) {
                showDialogForDelete(position);

            }
        });
        binding.adminRecycle.setAdapter(adapter);
    }

    public void deleteRecord(MyModel model){
        loadingBar.show();
         rootRef.getReference().child("Fruits").orderByChild("id").equalTo(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 for (DataSnapshot ds : snapshot.getChildren())
                 {
                     ds.getRef().removeValue();
                     getdata();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }

    public void updateRecord(MyModel model){

        String desp =  dialoguebinding.dis.getText().toString();
        String name = dialoguebinding.name.getText().toString();
        String price = dialoguebinding.price.getText().toString();

        if(desp.equals("")){
         customAlertDialogue("please Enetr Description");
        }else{
            if(name.equals("")){
                customAlertDialogue("Please Enetr Fruit Name");
            }else{
                if(price.equals("")){
                    customAlertDialogue("Please Eneter Price");
                }else{
                    loadingBar.show();
                    rootRef.getReference().child("Fruits").child(model.getId()).child("name").setValue(name);
                    rootRef.getReference().child("Fruits").child(model.getId()).child("price").setValue(price);
                    rootRef.getReference().child("Fruits").child(model.getId()).child("des").setValue(desp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getdata();
                            dialoguebinding.name.setText("");
                            dialoguebinding.dis.setText("");
                            dialoguebinding.price.setText("");
                            dialog.dismiss();
                        }
                    });
                }
            }
        }

    }

    public  void addFruit(){
        if (checkcondition(dialoguebinding.name, "Enter Fruit Name") && checkcondition(dialoguebinding.dis, "Enter Fruit description") && checkcondition(dialoguebinding.price, "Enter price")) {
            loadingBar.show();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyyhhmmss", Locale.ENGLISH);
            String date = simpleDateFormat.format(new Date());
            rootRef.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String, Object> UserMap = new HashMap<>();
                    UserMap.put("name", String.valueOf(dialoguebinding.name.getText()));
                    UserMap.put("id", date);
                    UserMap.put("price", String.valueOf(dialoguebinding.price.getText()));
                    UserMap.put("des", String.valueOf(dialoguebinding.dis.getText()));
                    rootRef.getReference().child("Fruits").child(date).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                getdata();
                                //loadingBar.dismiss();
                                dialoguebinding.name.setText("");
                                dialoguebinding.dis.setText("");
                                dialoguebinding.price.setText("");
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                loadingBar.dismiss();
                                Toast.makeText(MainActivity.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingBar.dismiss();
                    dialog.dismiss();
                }
            });

        }

    }

    private void showDialogForDelete(int position) throws Resources.NotFoundException {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure to delete this fruit record?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(
                       "Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //Do Something Here
                                MyModel model  = mylist.get(position);
                                deleteRecord(model);
                            }
                        })
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }
}
