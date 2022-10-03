package com.example.cafiteriaproject5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // declare variables.
    private FirebaseFirestore firestore;
    private Button btnRegDialog, btnLogDialog;
    String grade, firstName, lastName, regPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        btnRegDialog = findViewById(R.id.btnRegDialog);
        btnLogDialog = findViewById(R.id.btnLogDialog);
        btnRegDialog.setOnClickListener(this);
        btnLogDialog.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if(v == btnRegDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            builder.create().show();
            Button btnRegister = dialogView.findViewById(R.id.btnRegister);
            EditText etFirstName, etLastName, etRegPassword;
            etFirstName = dialogView.findViewById(R.id.etFirstName);
            etLastName = dialogView.findViewById(R.id.etLastName);
            etRegPassword = dialogView.findViewById(R.id.etRegPassword);
            Spinner spinner = dialogView.findViewById(R.id.spinner1);

            //You can use this adapter to provide views for an AdapterView.
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstName = etFirstName.getText().toString();
                    lastName = etLastName.getText().toString();
                    regPassword = etRegPassword.getText().toString();

                    //check if the user fill all the fields.
                    if(firstName.isEmpty() || lastName.isEmpty() || regPassword.isEmpty() || grade.isEmpty()){
                        Toast.makeText(dialogView.getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        User user = new User(firstName, lastName, grade, regPassword);
                        firestore
                                .collection("users")
                                //create an id for the user with the time he created it
                                .document(System.currentTimeMillis() + "")
                                .set(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(dialogView.getContext(), "user was added", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(dialogView.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            });

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        grade = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}