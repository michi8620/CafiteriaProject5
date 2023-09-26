package com.example.cafiteriaproject5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/*
side: Admin
action: adds the schedule picture from the gallery to HomeAdminFragment
xml file: fragment_admin_only.xml
 */
public class AdminOnlyFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button btnSelectImage;
    private Button btnUploadImage;

    private ImageView imageSchedule;
    Uri imageUri;
    StorageReference storageRef;

    Context thiscontext;

    ProgressDialog progressDialog;



    public AdminOnlyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminOnlyFragment.
     */
    public static AdminOnlyFragment newInstance(String param1, String param2) {
        AdminOnlyFragment fragment = new AdminOnlyFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_only, container, false);

        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageSchedule = view.findViewById(R.id.imageSchedule);

        thiscontext = container.getContext();

        //select the image from gallery using intent
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        //upload image to firebase storage
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri!=null){
                    uploadImage();
                }
                else{
                    Toast.makeText(getContext(), "יש לבחור תמונה", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    private void uploadImage() {

        //מסך טעינה להעלאת קובץ
        progressDialog = new ProgressDialog(thiscontext);
        progressDialog.setTitle("Uploading file....");
        progressDialog.show();

        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference scheduleRef = storageRef.child("images/schedule.jpg");

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

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
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
                imageSchedule.setImageURI(imageUri);
            } else {
                // The selected file is not an image
                Toast.makeText(getContext(), "אנא בחר בקובץ מסוג תמונה", Toast.LENGTH_SHORT).show();
            }

        }
    }
}