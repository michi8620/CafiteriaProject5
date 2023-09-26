package com.example.cafiteriaproject5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
side: shminist
action: see the wanted products (which are in recyclerView) and delete.
xml file: fragment_see_wanted.xml
 */

public class SeeWantedFragment extends Fragment implements EventListener<QuerySnapshot> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String itemId = "";

    private Context context;

    private FirebaseFirestore firestore;

    private RecyclerView wantedRecyclerView;
    private WantedAdapter wantedAdapter;
    private ArrayList<WantedProduct> wantedProductArrayList = new ArrayList<WantedProduct>();

    public SeeWantedFragment() {
        // Required empty public constructor
    }


    public static SeeWantedFragment newInstance(String param1, String param2) {
        SeeWantedFragment fragment = new SeeWantedFragment();
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
        View view = inflater.inflate(R.layout.fragment_see_wanted, container, false);
        context = view.getContext();

        wantedRecyclerView = view.findViewById(R.id.wantedRecyclerView);

        //for the view holder
        wantedRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        wantedAdapter = new WantedAdapter(container.getContext(), wantedProductArrayList);
        wantedRecyclerView.setAdapter(wantedAdapter);

        //delete the wantedProduct
        wantedAdapter.setOnItemClickListener(new WantedAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //delete from firebase
                androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(context);
                adb.setTitle("האם את/ה בטוח/ה שאת/ה רוצה למחוק את המוצר " + wantedProductArrayList.get(position).getName() + "?");
                adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete from firestore
                        firestore.collection("wantedProducts")
                                .document(wantedProductArrayList.get(position).getCode() + "")
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("SeeWanted", "item deleted successfully");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("SeeWanted", "onFailure: item still in firebase, ", e);
                                    }
                                });
                    }
                });
                adb.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adb.create().dismiss();
                    }
                });
                adb.create().show();
            }
        });

        //listener for the firestore, to see updates in the list
        firestore
                .collection("wantedProducts")
                .addSnapshotListener(this);

        //מועתק מonEvent בגלל שהפונקציה הזו פועלת רק לאחר עדכון ולא על ההתחלה
        firestore.collection("wantedProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                            List<DocumentSnapshot> wantedProductDocList = task.getResult().getDocuments();
                            // נותן את כל המסמכים שיש בדוקיומנט.
                            wantedProductArrayList.clear();

                            for(DocumentSnapshot doc : wantedProductDocList){
                                WantedProduct wantedProduct = new WantedProduct(
                                        doc.getString("name"),
                                        doc.getLong("code").intValue()
                                );
                                wantedProductArrayList.add(wantedProduct);
                            }
                            wantedAdapter.notifyDataSetChanged();

                        } else{
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        //\כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
        List<DocumentSnapshot> wantedProductDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        wantedProductArrayList.clear();

        for(DocumentSnapshot doc : wantedProductDocList){
            WantedProduct wantedProduct = new WantedProduct(
                    doc.getString("name"),
                    doc.getLong("code").intValue()
            );
            wantedProductArrayList.add(wantedProduct);
        }

        wantedAdapter.notifyDataSetChanged();
    }
}