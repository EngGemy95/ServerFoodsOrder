package com.fci_zu_eng_gemy_96;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.fci_zu_eng_gemy_96.Common.Common;
import com.fci_zu_eng_gemy_96.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import info.hoang8f.widget.FButton;


public class Login extends AppCompatActivity {

    MaterialEditText edtUserPhone , edtUSerPassword ;
    FButton btnLogin ;
    FirebaseDatabase database ;
    DatabaseReference usersRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserPhone = findViewById(R.id.edtStaffPhone);
        edtUSerPassword = findViewById(R.id.edtStaffPassword);

        btnLogin = findViewById(R.id.btnAccessLogin);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        final String userPhone = edtUserPhone.getText().toString().trim();
        final String userPassword = edtUSerPassword.getText().toString().trim();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(userPhone , userPassword);
            }
        });
    }

    public void signInUser( String phone,  String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        final String localPhone = phone ;
        final String localPassword = password ;

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    User userData = dataSnapshot.child(localPhone).getValue(User.class);
                    userData.setPhone(localPhone);
                    if(Boolean.parseBoolean(userData.getIsStaff())){
                        if(userData.getPassword().equals(localPassword)){
                            progressDialog.dismiss();
                            Common.currentUser = userData;
                            Intent intent = new Intent(Login.this,Home.class);
                            //intent.putExtra("UserName",Common.currentUser.getName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Please login with staff account ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "User not exsit in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
