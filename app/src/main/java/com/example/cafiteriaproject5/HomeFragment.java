package com.example.cafiteriaproject5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements EventListener<QuerySnapshot>, ShakeDetector.OnShakeListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context thiscontext;

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String gmail = "";
    private Double currentMoney=0.0;
    private boolean gift;

    private TextView tvTitleClient, tvTextClient;

    private RecyclerView productRecyclerViewClient;
    private ProductAdapter adapter;

    private ArrayList<Product> productArrayListClient = new ArrayList<Product>();

    private ShakeDetector shakeDetector;
    private KonfettiView konfettiView;
    private Shape.DrawableShape drawableShape;
    private EmitterConfig emitterConfig;

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
        mAuth = FirebaseAuth.getInstance();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        thiscontext = container.getContext();
        tvTextClient = view.findViewById(R.id.tvTextClient);
        tvTitleClient = view.findViewById(R.id.tvTitleClient);

        shakeDetector = new ShakeDetector(thiscontext);
        shakeDetector.setOnShakeListener(this);
        konfettiView = view.findViewById(R.id.konfettiView);
        drawableShape = new Shape.DrawableShape(AppCompatResources.getDrawable(thiscontext, R.drawable.ic_baseline_android), true);

        productRecyclerViewClient = view.findViewById(R.id.recyclerViewProductClient);
        productRecyclerViewClient.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //הפונקציה הזו גורמת לכך שהרשימה יורדת גם בתוך מסך שיורד
        productRecyclerViewClient.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        adapter = new ProductAdapter(thiscontext, productArrayListClient, inflater, this);

        productRecyclerViewClient.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(thiscontext);
                adb.setTitle("האם את/ה בטוח/ה שאת/ה רוצה למחוק את המוצר " + productArrayListClient.get(position).getName() + "?");
                adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String productCode = productArrayListClient.get(position).getCode() + "";
                        firestore.collection("products").document(productCode)
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(thiscontext, "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("EditAdminFragment", "onFailure: Error deleting document", e);
                                    }
                                });
                        adapter.notifyDataSetChanged();
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

    @Override
    public void onResume() {
        super.onResume();
        shakeDetector.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    @Override
    public void onShake() {
        // The konfetti doesn't work anymore and idk why
        emitterConfig = new Emitter(300, TimeUnit.MILLISECONDS).max(300);
        Log.d("HomeFragment", "onShake: konfetti");
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .shapes(Shape.Circle.INSTANCE, Shape.Square.INSTANCE, drawableShape)
                        .spread(360)
                        .position(0.5, 0.25, 1, 1)
                        .sizes(new Size(8,50,10))
                        .timeToLive(3000)
                        .fadeOutEnabled(true)
                        .build()
        );
        //get the email from the FirebaseAuth
        user = mAuth.getCurrentUser();
        if(user != null){
            gmail = user.getEmail();
        }
        //get the current money
        firestore.collection("users").document(gmail+"")
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()) {
                                currentMoney = documentSnapshot.getDouble("money");
                                gift = documentSnapshot.getBoolean("gift");
                                if(!gift){
                                    //add 10 shekels
                                    firestore.collection("users").document(gmail+"")
                                            .update("money", currentMoney+10)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    firestore.collection("users").document(gmail+"")
                                                                    .update("gift", true)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(thiscontext, "קיבלת 10 שקל מתנה מאיתנו!", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("HomeFragment", "onFailure: ", e);
                                                }
                                            });
                                } else{
                                    Toast.makeText(thiscontext, "לא ניתן לקבל את המתנה יותר מפעם אחת", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }
}