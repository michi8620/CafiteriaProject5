package com.example.cafiteriaproject5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;


/*
action: contains the sign up and login buttons. checks if the user is shminist or client.
it's the first page.
xml file: activity_main.xml
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // declare variables.
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private Button btnRegDialog, btnLogDialog;
    String grade;
    String firstName;
    String lastName;
    String regPassword;
    String regGmail;
    String logGmail;
    String logPassword;
    String type = "client";

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //always, in every activity and fragment:
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        btnRegDialog = findViewById(R.id.btnRegDialog);
        btnLogDialog = findViewById(R.id.btnLogDialog);
        btnRegDialog.setOnClickListener(this);
        btnLogDialog.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null).
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //when the user comes back after going out the app, we identify him and
        //sending him to his main activity.
        if(currentUser != null){
            String gmailForStart = currentUser.getEmail().toString();
            DocumentReference docRef = firestore.collection("users").document(gmailForStart);
            Intent iClient = new Intent(MainActivity.this, ClientMainActivity.class);
            Intent iAdmin = new Intent(MainActivity.this, ShministMainActivity.class);
            iClient.putExtra("doc", gmailForStart.toString());
            iAdmin.putExtra("doc", gmailForStart.toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String typeLog = document.getString("type");
                            if(typeLog.equals("client")){
                                startActivity(iClient);
                            }
                            else{
                                startActivity(iAdmin);
                            }
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "task failed", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
        //doing this so the user can't go back to his main page after logout.
        else{
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
        //because I don't want anything to happen when the user clicks 'back'
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
            EditText etFirstName, etLastName, etRegPassword, etGmailReg;
            etFirstName = dialogView.findViewById(R.id.etFirstName);
            etLastName = dialogView.findViewById(R.id.etLastName);
            etGmailReg = dialogView.findViewById(R.id.etGmailReg);
            etRegPassword = dialogView.findViewById(R.id.etRegPassword);
            Spinner spinner = dialogView.findViewById(R.id.spinner1);

            //You can use this adapter to provide views for an AdapterView.
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //creating spinner for the grade field.
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            //שליחת הנתונים
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstName = etFirstName.getText().toString();
                    lastName = etLastName.getText().toString();
                    regGmail = etGmailReg.getText().toString();
                    regPassword = etRegPassword.getText().toString();

                    //מסך טעינה להעלאת קובץ ושינוי הפרטים
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();

                    //check if the user filled all the fields.
                    if(firstName.isEmpty() || lastName.isEmpty() || regPassword.isEmpty() || regGmail.isEmpty() || grade.isEmpty()){
                        Toast.makeText(dialogView.getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    }
                    else if(!checkPassword(regPassword)){
                        Toast.makeText(MainActivity.this, "סיסמא חייבת להכיל ספרה אחת לפחות ו6 תווים לפחות", Toast.LENGTH_LONG).show();
                    }
                    else{
                        //creating the user with type = "client"
                        User user = new User(firstName, lastName, regGmail, grade, regPassword, type);
                        //קודם כל auth ואז ליצור משתמש, משום שבauth יש תקינות הקלט (חלקית)
                        mAuth.createUserWithEmailAndPassword(regGmail, regPassword)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            firestore.collection("users")
                                                    .document(regGmail + "")
                                                    .set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                if(progressDialog.isShowing())
                                                                    progressDialog.dismiss();
                                                                Toast.makeText(dialogView.getContext(), "נרשמת בהצלחה", Toast.LENGTH_SHORT).show();
                                                                //intent automatically to clientMain because when signin up the user is automatically a client, unless changed in the firestore by hand.
                                                                Intent i = new Intent(MainActivity.this, ClientMainActivity.class);
                                                                i.putExtra("doc", regGmail.toString());
                                                                startActivity(i);
                                                            }
                                                            else{
                                                                Toast.makeText(dialogView.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        StorageReference defaultImageRef = storageRef.child("profiles/"+regGmail+".jpg");
                        // Get the drawable resource ID of the image you want to upload
                        int drawableResource = R.drawable.profile_person;

                        // Get the InputStream of the image from the drawable resource
                        InputStream stream = getResources().openRawResource(+ drawableResource);

                        // Create a byte array to hold the data from the InputStream
                        byte[] data = new byte[0];
                        try {
                            data = new byte[stream.available()];
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            stream.read(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Create the upload task and upload the data to Firebase Storage
                        UploadTask uploadTask = defaultImageRef.putBytes(data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Image uploaded successfully
                                // Get the download URL of the uploaded image
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.w("MainActivity", "onFailure: image upload failed", exception);
                            }
                        });
                    }
                }
            });

        }
        if(v == btnLogDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_login, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            builder.create().show();
            EditText etGmailLog, etPasslog;
            Button btnLogin;
            etGmailLog = dialogView.findViewById(R.id.etGmailLog);
            etPasslog = dialogView.findViewById(R.id.etPassLog);
            btnLogin = dialogView.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logGmail = etGmailLog.getText().toString();
                    logPassword = etPasslog.getText().toString();

                    //מסך טעינה להעלאת קובץ ושינוי הפרטים
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.show();

                    if(logGmail.equals("") || logPassword.equals("")){
                        Toast.makeText(dialogView.getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mAuth.signInWithEmailAndPassword(logGmail, logPassword)
                                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //check if the user is a client or an admin
                                            Intent iClient = new Intent(MainActivity.this, ClientMainActivity.class);
                                            Intent iAdmin = new Intent(MainActivity.this, ShministMainActivity.class);
                                            iClient.putExtra("doc", logGmail);
                                            iAdmin.putExtra("doc", logGmail);
                                            DocumentReference docRef = firestore.collection("users").document(logGmail);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            if(progressDialog.isShowing())
                                                                progressDialog.dismiss();
                                                            Toast.makeText(dialogView.getContext(), "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                                                            String typeLog = document.getString("type");
                                                            if(typeLog.equals("client")){
                                                                startActivity(iClient);
                                                            }
                                                            else{
                                                                startActivity(iAdmin);
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(dialogView.getContext(), "task failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(dialogView.getContext(), "אימייל או סיסמא אינם נכונים",
                                                    Toast.LENGTH_SHORT).show();
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
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null, false);
        Toast.makeText(dialogView.getContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
    }

    /*
    * this password function checks if a given password
    * has at least one number in it.
    *
    * @param password (String)
    * @return true or false (boolean)
     */
    public boolean checkPassword(String password){
        boolean num=false;
        for (int i=0; i<password.length(); i++){
            char ch = password.charAt(i);
            if(Character.isDigit(ch))
                num = true;
        }
        if(num && password.length() >= 6){
            return true;
        }
        return false;
    }

}