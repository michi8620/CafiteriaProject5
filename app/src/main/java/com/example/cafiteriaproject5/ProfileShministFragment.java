package com.example.cafiteriaproject5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileShministFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileShministFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String firstName;
    private String lastName;
    private String email;
    private String money;

    //buttons
    private Button btnEditProfileShministDialog;
    private Button btnChangeIGDialog;

    private CircleImageView ivNewProfileImage;
    private CircleImageView ivProfileImageShminist;

    private TextView tvNameShministProfile;
    private TextView tvLastNameShministProfile;
    private TextView tvGmailShministProfile;
    private TextView tvMoneyShministProfile;

    private String gmail = "";

    //flags that indicate that the profile info has been updated
    public boolean flagImage;
    private Boolean flagFirstName;
    private Boolean flagLastName;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    private Context context;

    private Uri imageUri;
    private StorageReference storageRef;

    private StorageReference profileImageRef;

    ProgressDialog progressDialog;

    public ProfileShministFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileAdminFragment.
     */
    public static ProfileShministFragment newInstance(String param1, String param2) {
        ProfileShministFragment fragment = new ProfileShministFragment();
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
        View view =  inflater.inflate(R.layout.fragment_profile_shminist, container, false);
        context = view.getContext();

        tvNameShministProfile = view.findViewById(R.id.tvNameShministProfile);
        tvLastNameShministProfile = view.findViewById(R.id.tvLastNameShministProfile);
        tvGmailShministProfile = view.findViewById(R.id.tvGmailShministProfile);
        tvMoneyShministProfile = view.findViewById(R.id.tvMoneyShministProfile);

        btnEditProfileShministDialog = view.findViewById(R.id.btnEditProfileShministDialog);
        btnEditProfileShministDialog.setOnClickListener(this);
        btnChangeIGDialog = view.findViewById(R.id.btnChangeIGDialog);
        btnChangeIGDialog.setOnClickListener(this);

        ivProfileImageShminist = view.findViewById(R.id.ivProfileImageShminist);

        //get the email from the FirebaseAuth
        user = mAuth.getCurrentUser();
        if(user != null){
            gmail = user.getEmail();
        }

        //get the string texts of the profile info
        updateStringFields();
        //get the profile image from storage
        updateImage(ivProfileImageShminist);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==btnEditProfileShministDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile_shminist, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();


            Button btnChangeProfileImage = dialogView.findViewById(R.id.btnChangeProfileImage);
            Button btnUpdateProfile = dialogView.findViewById(R.id.btnUpdateProfile);

            ivNewProfileImage = dialogView.findViewById(R.id.ivNewProfileImage);

            EditText etNewName = dialogView.findViewById(R.id.etNewName);
            EditText etNewLastName = dialogView.findViewById(R.id.etNewLastName);

            etNewName.setText(firstName);
            etNewLastName.setText(lastName);

            updateImage(ivNewProfileImage);

            //select image
            btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 100);
                }
            });


            //update profile
            btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //make sure everything is changed;
                    flagImage = false;
                    flagFirstName = false;
                    flagLastName = false;

                    //מסך טעינה להעלאת קובץ ושינוי הפרטים
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setTitle("מעדכן....");
                    progressDialog.show();

                    //update profile picture
                    if(imageUri!=null){
                        storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference profileImageRef = storageRef.child("profiles/" + user.getEmail() + ".jpg");
                        //upload image
                        profileImageRef.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        updateImage(ivNewProfileImage);
                                        flagImage = true;
                                        //checking it here becuase it's the last function that runs.
                                        if(flagFirstName && flagLastName){
                                            if(progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            alert.dismiss();
                                            Toast.makeText(context, "פרופיל התעדכן בהצלחה", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else{
                        flagImage = true;
                    }

                    String newFirstName = etNewName.getText().toString();
                    String newLastName = etNewLastName.getText().toString();

                    DocumentReference userRef = firestore.collection("users").document(gmail);

                    //update everything else
                    if(!newFirstName.equals(firstName)){
                        // Set the name field to the new name
                        userRef
                                .update("firstName", newFirstName)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ProfileFragment", "DocumentSnapshot successfully updated!");
                                        flagFirstName = true;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ProfileFragment", "Error updating document", e);
                                    }
                                });
                    }
                    else{
                        flagFirstName = true;
                    }

                    if(!newLastName.equals(lastName)){
                        // Set the lastName field to the new last name
                        userRef
                                .update("lastName", newLastName)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("ProfileFragment", "DocumentSnapshot successfully updated!");
                                        flagLastName = true;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ProfileFragment", "Error updating document", e);
                                    }
                                });
                    }
                    else{
                        flagLastName = true;
                    }

                    updateImage(ivProfileImageShminist);
                    updateStringFields();
                }
            });
        }
        //update the instagram link
        if(btnChangeIGDialog == v){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_ig, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            EditText etIGLink = dialogView.findViewById(R.id.etIGLink);

            Button btnChangeIG = dialogView.findViewById(R.id.btnChangeIG);
            btnChangeIG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String IGlink = etIGLink.getText().toString();
                    try {
                        //check if it's a proper url
                        URL url = new URL(IGlink);

                        firestore.collection("IGlink").document("link").update("linkText", IGlink)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        alert.dismiss();
                                        Toast.makeText(context, "קישור עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("ProfileShminist", "onFailure: ", e);
                                    }
                                });
                    }
                    catch (MalformedURLException e){
                        Toast.makeText(context, "לינק לא תקין", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null){

            Uri selectedUri = data.getData();

            // Check the file type using the MIME type
            String mimeType = getActivity().getContentResolver().getType(selectedUri);
            if (mimeType != null && mimeType.startsWith("image/")) {
                // The selected file is an image
                imageUri = selectedUri;
                ivNewProfileImage.setImageURI(imageUri);
            } else {
                // The selected file is not an image
                Toast.makeText(getContext(), "אנא בחר בקובץ מסוג תמונה", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void updateStringFields(){

        //set the profile information from firestore.
        firestore.collection("users").document(gmail+"")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                firstName = documentSnapshot.getString("firstName");
                                tvNameShministProfile.setText(firstName);
                                lastName = documentSnapshot.getString("lastName");
                                tvLastNameShministProfile.setText(lastName);
                                email = documentSnapshot.getString("gmail");
                                tvGmailShministProfile.setText(email);
                                money = documentSnapshot.getLong("money").toString() + "₪";
                                tvMoneyShministProfile.setText(money);
                            }else{
                                Log.d("ProfileFragment", "onComplete: DocumentSnapshot doesn't exist");
                            }
                        }else{
                            Log.w("ProfileFragment", "onComplete: ", task.getException());
                        }
                    }
                });
    }
    public void updateImage(ImageView ivImageToUpdate){
        //get the profile from firebase
        storageRef = FirebaseStorage.getInstance().getReference();
        profileImageRef = FirebaseStorage.getInstance().getReference("profiles/" + gmail + ".jpg");
        try {
            //creating a temporary local file in which we will be storing the image
            //that we will fetch from the firebase storage
            File localFile = File.createTempFile("tempfile", ".jpg");
            profileImageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            //we'll get the image inside this bitmap variable
                            //using BitmapFactory we could decode the file
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ivImageToUpdate.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}