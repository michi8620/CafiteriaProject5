package com.example.cafiteriaproject5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String selectedGrade, grade, firstName, lastName, email, money;

    private Button btnEditProfileDialog;
    private CircleImageView ivNewProfileImage;
    private TextView tvNameProfile;
    private TextView tvLastNameProfile;
    private TextView tvGmailProfile;
    private TextView tvGradeProfile;
    private TextView tvMoneyProfile;
    private String gmail = "";
    private CircleImageView ivProfileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private Context context;
    private Uri imageUri;
    StorageReference storageRef;
    ProgressDialog progressDialog;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();
        tvNameProfile = view.findViewById(R.id.tvNameProfile);
        tvLastNameProfile = view.findViewById(R.id.tvLastNameProfile);
        tvGmailProfile = view.findViewById(R.id.tvGmailProfile);
        tvGradeProfile = view.findViewById(R.id.tvGradeProfile);
        tvMoneyProfile = view.findViewById(R.id.tvMoneyProfile);
        btnEditProfileDialog = view.findViewById(R.id.btnEditProfileDialog);
        btnEditProfileDialog.setOnClickListener(this);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);

        TextView tvInstagramHyperLink = view.findViewById(R.id.tvInstgramHyperLink);
        tvInstagramHyperLink.setMovementMethod(LinkMovementMethod.getInstance());

        //get the email from the FirebaseAuth
        user = mAuth.getCurrentUser();
        if(user != null){
            gmail = user.getEmail();
        }

        //set the profile information from firestore.
        firestore.collection("users").document(gmail+"")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                firstName = documentSnapshot.getString("firstName");
                                tvNameProfile.setText(firstName);
                                lastName = documentSnapshot.getString("lastName");
                                tvLastNameProfile.setText(lastName);
                                email = documentSnapshot.getString("gmail");
                                tvGmailProfile.setText(email);
                                grade = documentSnapshot.getString("grade");
                                tvGradeProfile.setText(grade);
                                money = documentSnapshot.getLong("money").toString() + "₪";
                                tvMoneyProfile.setText(money);
                            }else{
                                Log.d("ProfileFragment", "onComplete: DocumentSnapshot doesn't exist");
                            }
                        }else{
                            Log.w("ProfileFragment", "onComplete: ", task.getException());
                        }
                    }
                });

        /*storageRef = FirebaseStorage.getInstance().getReference();

        try {
            //creating a temporary local file in which we will be storing the image
            //that we will fetch from the firebase storage
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            //we'll get the image inside this bitmap variable
                            //using BitmapFactory we could decode the file
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ivSchedule.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(thiscontext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==btnEditProfileDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            builder.create().show();


            Button btnChangeProfileImage = dialogView.findViewById(R.id.btnChangeProfileImage);
            Button btnUpdateProfile = dialogView.findViewById(R.id.btnUpdateProfile);
            ivNewProfileImage = dialogView.findViewById(R.id.ivNewProfileImage);
            EditText etNewName = dialogView.findViewById(R.id.etNewName);
            EditText etNewLastName = dialogView.findViewById(R.id.etNewLastName);
            EditText etNewGmail = dialogView.findViewById(R.id.etNewGmail);

            etNewName.setText(firstName);
            etNewLastName.setText(lastName);
            etNewGmail.setText(email);

            Spinner spinner = dialogView.findViewById(R.id.spinner1);

            //You can use this adapter to provide views for an AdapterView.
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.grade, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //creating spinner for the grade field.
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            //select image
            /*btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 100);
                }
            });*/


            //update profile
            /*btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //מסך טעינה להעלאת קובץ
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setTitle("מעדכן....");
                    progressDialog.show();

                    //update profile picture
                    if(imageUri!=null){
                        storageRef = FirebaseStorage.getInstance().getReference();
                        if(user.getPhotoUrl()!=null) {
                            StorageReference profileImageRef = storageRef.child("profiles/" + user.getEmail() + ".jpg");
                            //upload image
                            scheduleRef.putFile(imageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            imageSchedule.setImageURI(null);
                                            Toast.makeText(thiscontext, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                            if(progressDialog.isShowing())
                                                progressDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            Toast.makeText(thiscontext, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                }
            });*/
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedGrade = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            ivNewProfileImage.setImageURI(imageUri);

        }
    }
}