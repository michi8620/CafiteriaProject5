package com.example.cafiteriaproject5;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
side: client
action: adds wanted product
xml file: fragment_wanted_products.xml
 */
public class WantedProductsFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore firestore;
    private EditText etWantedProduct;
    private Button btnSendWanted;

    private int highestCode = 0;

    private ArrayList<WantedProduct> wantedProductArrayList = new ArrayList<WantedProduct>();

    public WantedProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add.
     */
    public static WantedProductsFragment newInstance(String param1, String param2) {
        WantedProductsFragment fragment = new WantedProductsFragment();
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

        /*
        This method is used to get the highestCode from the WantedProducts collection
        so when I add a wantedProduct its id will not be the same as the others.
        It's in the OnCreate method because if I put it in the Onclick it will run at the end.
         */
        firestore.collection("wantedProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                            List<DocumentSnapshot> wantedProductDocList = task.getResult().getDocuments();
                            // נותן את כל המסמכים שיש בדוקיומנט.

                            for(DocumentSnapshot doc : wantedProductDocList){
                                int docCode = doc.getLong("code").intValue();
                                if(highestCode < docCode){
                                    highestCode = docCode;
                                }
                            }

                        } else{
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wanted_products, container, false);
        etWantedProduct = view.findViewById(R.id.etWantedProduct);
        btnSendWanted = view.findViewById(R.id.btnSendWanted);
        btnSendWanted.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == btnSendWanted){
            //now checking...
            String wantedProductName = etWantedProduct.getText().toString();
            WantedProduct wantedProduct1 = new WantedProduct(wantedProductName, highestCode+1);

            firestore.collection("wantedProducts")
                    .document((highestCode+1)+"")
                    .set(wantedProduct1)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "השליחה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.w("WantedProducts", "onCompleteFailure: " + task.getException().toString());
                            }
                        }
                    });
            /*
            In this code I get the highestCode from the collection
            AFTER I added a wantedProduct because the method that
            runs before the add is in the OnCreate function.
            */
            firestore.collection("wantedProducts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                                List<DocumentSnapshot> wantedProductDocList = task.getResult().getDocuments();
                                // נותן את כל המסמכים שיש בדוקיומנט.

                                for(DocumentSnapshot doc : wantedProductDocList){
                                    int docCode = doc.getLong("code").intValue();
                                    if(highestCode < docCode){
                                        highestCode = docCode;
                                    }
                                }
                                Log.d("WantedProducts", "Onclick1: HighestCode: " + highestCode);

                            } else{
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}