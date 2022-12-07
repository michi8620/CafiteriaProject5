package com.example.cafiteriaproject5;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements EventListener<QuerySnapshot> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context thiscontext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore firestore;
    private TextView tvTitleClient, tvTextClient;

    private ListView productListViewClient;
    private ProductAdapter adapter;

    private ArrayList<Product> productArrayListClient = new ArrayList<Product>();;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        DocumentReference docRef = firestore.collection("events").document("event");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tvTitleClient.setText(document.getString("title"));
                        tvTextClient.setText(document.getString("text"));
                    }
                } else {
                    Toast.makeText(thiscontext, "get failed with " + task.getException(), Toast.LENGTH_LONG);
                }
            }
        });
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        thiscontext = container.getContext();
        tvTextClient = view.findViewById(R.id.tvTextClient);
        tvTitleClient = view.findViewById(R.id.tvTitleClient);

        productListViewClient = view.findViewById(R.id.listViewProductClient);

        //הפונקציה הזו גורמת לכך שהרשימה יורדת גם בתוך מסך שיורד
        productListViewClient.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapter = new ProductAdapter(thiscontext, R.layout.product_row, productArrayListClient);

        productListViewClient.setAdapter(adapter);

        //listener for the firestore, to see updates in the list
        firestore
                .collection("products")
                .addSnapshotListener(this);

        //מועתק מonEvent בגלל שהפונקציה הזו פועלת רק לאחר עדכון ולא על ההתחלה
        firestore.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                            List<DocumentSnapshot> productDocList = task.getResult().getDocuments();
                            // נותן את כל המסמכים שיש בדוקיומנט.
                            productArrayListClient.clear();

                            for(DocumentSnapshot doc : productDocList){
                                Product product = new Product(
                                        doc.getString("name"),
                                        doc.getDouble("price"),
                                        doc.getLong("code").intValue()
                                );
                                productArrayListClient.add(product);
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
        List<DocumentSnapshot> productDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        productArrayListClient.clear();

        for(DocumentSnapshot doc : productDocList){
            Product product = new Product(
                    doc.getString("name"),
                    doc.getDouble("price"),
                    doc.getLong("code").intValue()
            );
            productArrayListClient.add(product);
        }

        adapter.notifyDataSetChanged();
    }
}