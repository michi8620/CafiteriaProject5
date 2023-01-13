package com.example.cafiteriaproject5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.TapListener;
import com.ablanco.zoomy.Zoomy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAdminFragment extends Fragment implements EventListener<QuerySnapshot>, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context thiscontext;
    private ImageView ivSchedule;
    private StorageReference storageReference;
    private Button btnMinusDialog, btnPlusDialog;
    private FirebaseFirestore firestore;
    private ListView lvUsers;
    private UserAdapter adapter;

    private ArrayList<User> userArrayList = new ArrayList<User>();

    public HomeAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAdminFragment newInstance(String param1, String param2) {
        HomeAdminFragment fragment = new HomeAdminFragment();
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

        firestore = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);
        thiscontext = container.getContext();
        ivSchedule = view.findViewById(R.id.ivSchedule);
        btnPlusDialog = view.findViewById(R.id.btnPlusDialog);
        btnMinusDialog = view.findViewById(R.id.btnMinusDialog);
        btnPlusDialog.setOnClickListener(this);
        btnMinusDialog.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference("images/schedule.jpg");
        try {
            //creating a temporary local file in which we will be storing the image
            //that we will fetch from the firebase storage
            File localFile = File.createTempFile("tempfile", ".jpg");
            storageReference.getFile(localFile)
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
        }

        //code that enables zooming in the image
        Zoomy.Builder builder = new Zoomy.Builder(getActivity())
                .target(ivSchedule)
                .animateZooming(false)
                .enableImmersiveMode(false)
                .tapListener(new TapListener() {
                    @Override
                    public void onTap(View v) {
                        Toast.makeText(thiscontext, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

        builder.register();

        lvUsers = view.findViewById(R.id.lvUsers);

        //הפונקציה הזו גורמת לכך שהרשימה יורדת גם בתוך מסך שיורד
        lvUsers.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapter = new UserAdapter(thiscontext, R.layout.user_row, userArrayList);

        lvUsers.setAdapter(adapter);

        //listener for the firestore, to see updates in the list
        firestore
                .collection("users")
                .addSnapshotListener(this);

        //מועתק מonEvent בגלל שהפונקציה הזו פועלת רק לאחר עדכון ולא על ההתחלה
        firestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                            List<DocumentSnapshot> userDocList = task.getResult().getDocuments();
                            // נותן את כל המסמכים שיש בדוקיומנט.
                            userArrayList.clear();

                            for(DocumentSnapshot doc : userDocList){
                                User user = new User(
                                        doc.getString("firstName"),
                                        doc.getString("lastName"),
                                        doc.getString("gmail"),
                                        doc.getString("grade"),
                                        doc.getDouble("money")
                                );
                                userArrayList.add(user);
                            }

                            adapter.notifyDataSetChanged();

                        } else{
                            Toast.makeText(thiscontext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        //\כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
        List<DocumentSnapshot> userDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        userArrayList.clear();

        for(DocumentSnapshot doc : userDocList){
            User user = new User(
                    doc.getString("firstName"),
                    doc.getString("lastName"),
                    doc.getString("gmail"),
                    doc.getString("grade"),
                    doc.getDouble("money")
            );
            userArrayList.add(user);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if(view == btnPlusDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_plus_money, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            EditText etGmailPlus = dialogView.findViewById(R.id.etGmailPlus);
            EditText etMoneyPlus = dialogView.findViewById(R.id.etMoneyPlus);
            Button btnPlusMoney = dialogView.findViewById(R.id.btnPlusMoney);

            btnPlusMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gmailPlus = etGmailPlus.getText().toString();
                    Double moneyPlus = Double.parseDouble(etMoneyPlus.getText().toString());

                    //update the money of the client.
                    if(gmailPlus.isEmpty() || moneyPlus == null){
                        Toast.makeText(thiscontext, "please fill al fields", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //firstly we get the current money of the user,
                        //and only then we update the money
                        DocumentReference userMoneyRef = firestore.collection("users").document(gmailPlus);
                        userMoneyRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Double currentMoney = document.getDouble("money");
                                        userMoneyRef.update("money", currentMoney+moneyPlus)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(thiscontext, "money updated", Toast.LENGTH_SHORT).show();
                                                    alert.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(thiscontext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(thiscontext, "document does no exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(thiscontext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });

        }
        if(view == btnMinusDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_minus_money, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            EditText etGmailMinus = dialogView.findViewById(R.id.etGmailMinus);
            EditText etMoneyMinus = dialogView.findViewById(R.id.etMoneyMinus);
            Button btnMinusMoney = dialogView.findViewById(R.id.btnMinusMoney);

            btnMinusMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String gmailMinus = etGmailMinus.getText().toString();
                    Double moneyMinus = Double.parseDouble(etMoneyMinus.getText().toString());

                    //update the money of the client.
                    if(gmailMinus.isEmpty() || moneyMinus == null){
                        Toast.makeText(thiscontext, "please fill al fields", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //firstly we get the current money of the user,
                        //and only then we update the money
                        DocumentReference userMoneyRef = firestore.collection("users").document(gmailMinus);
                        userMoneyRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        Double currentMoney = document.getDouble("money");
                                        userMoneyRef.update("money", currentMoney-moneyMinus)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(thiscontext, "money updated", Toast.LENGTH_SHORT).show();
                                                            alert.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(thiscontext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else{
                                        Toast.makeText(thiscontext, "document does no exist", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(thiscontext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
        }
    }
}