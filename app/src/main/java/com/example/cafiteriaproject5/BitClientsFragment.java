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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BitClientsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BitClientsFragment extends Fragment implements EventListener<QuerySnapshot> {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;

    private FirebaseFirestore firestore;

    private RecyclerView bitClientsRecyclerView;
    private BitClientAdapter bitClientAdapter;
    private ArrayList<BitClient> bitArrayList = new ArrayList<>();

    public BitClientsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BitClientsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BitClientsFragment newInstance(String param1, String param2) {
        BitClientsFragment fragment = new BitClientsFragment();
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
        View view = inflater.inflate(R.layout.fragment_bit_clients, container, false);
        context = view.getContext();

        bitClientsRecyclerView = view.findViewById(R.id.bitClientsRecyclerView);

        //for the view holder
        bitClientsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bitClientAdapter = new BitClientAdapter(container.getContext(), bitArrayList);
        bitClientsRecyclerView.setAdapter(bitClientAdapter);

        //delete the bit client
        bitClientAdapter.setOnItemClickListener(new BitClientAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //delete from firebase
                androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(context);
                adb.setTitle("האם את/ה בטוח/ה שאת/ה רוצה למחוק את המוצר?");
                adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firestore.collection("bit")
                                .document(bitArrayList.get(position).getCode() + "")
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
                .collection("bit")
                .addSnapshotListener(this);
        //מועתק מonEvent בגלל שהפונקציה הזו פועלת רק לאחר עדכון ולא על ההתחלה
        firestore.collection("bit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
                            List<DocumentSnapshot> bitDocList = task.getResult().getDocuments();
                            // נותן את כל המסמכים שיש בדוקיומנט.
                            bitArrayList.clear();

                            for(DocumentSnapshot doc : bitDocList){
                                BitClient bitClient = new BitClient(
                                        doc.getString("code"),
                                        doc.getString("name"),
                                        doc.getString("lastName"),
                                        doc.getString("product"),
                                        doc.getString("quantity")
                                );
                                bitArrayList.add(bitClient);
                            }
                            bitClientAdapter.notifyDataSetChanged();

                        } else{
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return view;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        //\כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
        List<DocumentSnapshot> bitDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        bitArrayList.clear();

        for(DocumentSnapshot doc : bitDocList){
            BitClient bitClient = new BitClient(
                    doc.getString("code"),
                    doc.getString("name"),
                    doc.getString("lastName"),
                    doc.getString("product"),
                    doc.getString("quantity")
            );
            bitArrayList.add(bitClient);
        }

        bitClientAdapter.notifyDataSetChanged();
    }
}