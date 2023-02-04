package com.example.cafiteriaproject5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAdminFragment extends Fragment implements View.OnClickListener, EventListener<QuerySnapshot> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context thiscontext;

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore firestore;
    private Button btnAddEventDialog, btnAddProductDialog;
    private TextView tvTitle, tvText;
    private static int productCode = 99;

    private ListView productListView;
    private ProductAdapter adapter;

    private ArrayList<Product> productArrayList = new ArrayList<Product>();

    public EditAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditAdminFragment.
     */
    public static EditAdminFragment newInstance(String param1, String param2) {
        EditAdminFragment fragment = new EditAdminFragment();
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
                        tvTitle.setText(document.getString("title"));
                        tvText.setText(document.getString("text"));
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thiscontext = container.getContext();
        //productCode is set to be 99 everytime the app runs.
        //to prevent that we save productCode to sharedPreferences
        //which is like file in the application. 
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(thiscontext);
        productCode = sp.getInt("product_code", 0);

        View view= inflater.inflate(R.layout.fragment_edit_admin, container, false);
        tvText = view.findViewById(R.id.tvText);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnAddEventDialog = view.findViewById(R.id.btnAddEventDialog);
        btnAddProductDialog = view.findViewById(R.id.btnAddProductDialog);
        btnAddEventDialog.setOnClickListener(this);
        btnAddProductDialog.setOnClickListener(this);

        productListView = view.findViewById(R.id.listViewProduct);
        //הפונקציה הזו גורמת לכך שהרשימה יורדת גם בתוך מסך שיורד
        productListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapter = new ProductAdapter(thiscontext, R.layout.product_row, productArrayList);

        productListView.setAdapter(adapter);

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
                            productArrayList.clear();

                            for(DocumentSnapshot doc : productDocList){
                                Product product = new Product(
                                        doc.getString("name"),
                                        doc.getDouble("price"),
                                        doc.getLong("code").intValue()
                                );
                                productArrayList.add(product);
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
    public void onClick(View view) {
        if(view == btnAddEventDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            Button btnAddEvent = dialogView.findViewById(R.id.btnAddEvent);
            EditText etTitle = dialogView.findViewById(R.id.etTitle);
            EditText etText = dialogView.findViewById(R.id.etText);

            btnAddEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = etTitle.getText().toString();
                    String text = etText.getText().toString();

                    if(title.isEmpty() || text.isEmpty()){
                        Toast.makeText(dialogView.getContext(), "אנא כתוב גם כותרת וגם טקסט לאירוע", Toast.LENGTH_LONG);
                    }
                    else{
                        Event event = new Event(title, text);
                        firestore
                                .collection("events")
                                .document("event")
                                .set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(thiscontext,"הוספת אירוע בהצלחה", Toast.LENGTH_SHORT).show();
                                        alert.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(thiscontext, e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });

        }
        if (view == btnAddProductDialog) {
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            Button btnAddProduct = dialogView.findViewById(R.id.btnAddProduct);
            EditText etProductName = dialogView.findViewById(R.id.etProductName);
            EditText etPrice = dialogView.findViewById(R.id.etPrice);

            btnAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String productName = etProductName.getText().toString();
                    Double price = Double.parseDouble(etPrice.getText().toString());
                    productCode++;
                    Product product = new Product(productName, price, productCode);
                    String code = Integer.toString(productCode);
                    firestore.collection("products")
                            .document(code)
                            .set(product)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(dialogView.getContext(), "product was added", Toast.LENGTH_SHORT).show();
                                        alert.dismiss();

                                    }
                                    else{
                                        Toast.makeText(dialogView.getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(thiscontext);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("product_code", productCode);
            editor.apply();  /* Edit the value here*/
        }
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        //\כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
        List<DocumentSnapshot> productDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        productArrayList.clear();

        for(DocumentSnapshot doc : productDocList){
            Product product = new Product(
                    doc.getString("name"),
                    doc.getDouble("price"),
                    doc.getLong("code").intValue()
            );
            productArrayList.add(product);
        }

        adapter.notifyDataSetChanged();
    }
}