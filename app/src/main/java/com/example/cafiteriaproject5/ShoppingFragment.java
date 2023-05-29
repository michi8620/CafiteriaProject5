package com.example.cafiteriaproject5;

import static it.sephiroth.android.library.imagezoom.ImageViewTouchBase.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.stream.Collectors;

/*
side: client
action: the delivery fragment. contains "add product" and "delete product" buttons,
רשימה של המוצרים שנכנסו למשלוח וכפתור שליחה לאימייל השכבתי.
the add dialog contains also the product list so the user can see the codes.
מחיקת מוצר נעשת על ידי כתיבת המספר שלו שנמצא ברשימה של המוצרים שנמצאים במשלוח עד כה.
xml file: fragment_shopping.xml
 */
public class ShoppingFragment extends Fragment implements View.OnClickListener, EventListener<QuerySnapshot> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Context thisContext;
    private FirebaseFirestore firestore;
    private TextView tvSum;
    private Button btnDeliveryDialog, btnSendDelivery;
    private EditText etCodeDelivery, etQuantityDelivery;
    private RecyclerView deliveryRecyclerView;
    private ProductDeliveryAdapter adapter;
    private String fullName = "", grade = "", message = "", roomNum = "", comment = "";
    private double sum=0;
    private static int index=0;
    private int roomNumInt;

    private ArrayList<ProductDelivery> deliveryArrayList = new ArrayList<>();

    private RecyclerView productDialogRecyclerView;
    private ProductAdapter adapterDialog;

    private ArrayList<Product> productDialogArrayList = new ArrayList<>();

    public ShoppingFragment() {
        // Required empty public constructor
    }

    public static ShoppingFragment newInstance(String param1, String param2) {
        ShoppingFragment fragment = new ShoppingFragment();
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
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        thisContext = view.getContext();
        btnDeliveryDialog = view.findViewById(R.id.btnDeliveryDialog);
        btnSendDelivery = view.findViewById(R.id.btnSendDelivery);
        deliveryRecyclerView = view.findViewById(R.id.recyclerViewDelivery);
        tvSum = view.findViewById(R.id.tvSum);

        adapter = new ProductDeliveryAdapter(thisContext, deliveryArrayList);
        deliveryRecyclerView.setAdapter(adapter);
        deliveryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        btnDeliveryDialog.setOnClickListener(this);
        btnSendDelivery.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnDeliveryDialog){
            //הבונה של הדיאלוג
            AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
            // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_delivery, null, false);
            //Sets a custom view to be the contents of the alert dialog.
            builder.setView(dialogView);
            //Creates an AlertDialog with the arguments supplied to this builder.
            AlertDialog alert = builder.create();
            alert.show();

            etCodeDelivery = dialogView.findViewById(R.id.etCodeDelivery);
            etQuantityDelivery = dialogView.findViewById(R.id.etQuantityDelivery);
            Button btnAddDelivery = dialogView.findViewById(R.id.btnAddDelivery);
            productDialogRecyclerView = dialogView.findViewById(R.id.productDialogRecyclerView);
            adapterDialog = new ProductAdapter(thisContext, productDialogArrayList, getLayoutInflater(), this);
            productDialogRecyclerView.setAdapter(adapterDialog);
            productDialogRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            adapterDialog.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(thisContext);
                    adb.setTitle("האם את/ה בטוח/ה שאת/ה רוצה למחוק את המוצר " + productDialogArrayList.get(position).getName() + "?");
                    adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String productCode = productDialogArrayList.get(position).getCode() + "";
                            firestore.collection("products").document(productCode)
                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(thisContext, "נמחק בהצלחה", Toast.LENGTH_SHORT).show();
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

            adapter.setOnItemClickListener(new ProductDeliveryAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(thisContext);
                    adb.setTitle("האם את/ה בטוח/ה שאת/ה רוצה למחוק את המוצר " + deliveryArrayList.get(position).getName() + " מהרשימה?");
                    adb.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(position < deliveryArrayList.size()){
                                sum-=deliveryArrayList.get(position).getTotal();
                                tvSum.setText(sum + "");
                                deleteDelivery(deliveryArrayList, position);
                                index--;
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(thisContext, "מספר לא קיים", Toast.LENGTH_SHORT).show();
                            }
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

            //בחלק זה אנו בונים את הרשימה שבתוך הדיאלוג על מנת שהמשתמש יראה את הקודים של המוצרים
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
                                productDialogArrayList.clear();

                                for(DocumentSnapshot doc : productDocList){
                                    Product product = new Product(
                                            doc.getString("name"),
                                            doc.getDouble("price"),
                                            doc.getLong("code").intValue()
                                    );
                                    productDialogArrayList.add(product);
                                }

                                adapter.notifyDataSetChanged();

                            } else{
                                Toast.makeText(thisContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            //הוספה של המוצר לרשימת המשלוח
            btnAddDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String codeDelivery = etCodeDelivery.getText().toString();
                    String stringQuantityDelivery = etQuantityDelivery.getText().toString();
                    //check if the user wrote something in the fields.
                    if(codeDelivery.isEmpty() || stringQuantityDelivery.isEmpty()){
                        Toast.makeText(thisContext, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    }
                    //if yes:
                    if(!codeDelivery.isEmpty() && !stringQuantityDelivery.isEmpty()){
                        int quantityDelivery = Integer.parseInt(stringQuantityDelivery);
                        if(quantityDelivery > 10){
                            Toast.makeText(thisContext, "לא ניתן להזמין מוצר אחד יותר מ-10 פעמים", Toast.LENGTH_SHORT).show();
                        }
                        else if(quantityDelivery < 1){
                            Toast.makeText(thisContext, "מספר לא תקין", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            DocumentReference deliveryProductRef = firestore.collection("products").document(codeDelivery);
                            deliveryProductRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists()){
                                            String productName = document.getString("name");
                                            Double productPrice = document.getDouble("price");
                                            //check if there is already the same product in the arrayList
                                            //if not then:
                                            if(findProduct(deliveryArrayList, productName) == -1){
                                                double total = productPrice*quantityDelivery;
                                                index++;
                                                ProductDelivery productDelivery = new ProductDelivery(index, productName, quantityDelivery, total);
                                                deliveryArrayList.add(productDelivery);
                                                adapter.notifyDataSetChanged();
                                                sum += total;
                                                tvSum.setText(sum + "");
                                                alert.dismiss();
                                            }
                                            //if yes then change the quantity of the already existing product,
                                            //change the total and change the sum to be accurate.
                                            else{
                                                ProductDelivery productDelivery = deliveryArrayList.get(findProduct(deliveryArrayList, productName));
                                                productDelivery.setQuantity(productDelivery.getQuantity()+1);
                                                productDelivery.setTotal(productDelivery.getTotal()+productPrice);
                                                deliveryArrayList.set(findProduct(deliveryArrayList, productName), productDelivery);
                                                adapter.notifyDataSetChanged();
                                                sum += productPrice;
                                                tvSum.setText(sum + "");
                                                alert.dismiss();
                                            }
                                        }
                                        else{
                                            Toast.makeText(thisContext, "מוצר זה אינו קיים", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(thisContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        if (view == btnSendDelivery) {
            //check if there's products in the delivery
            if(deliveryArrayList.isEmpty()){
                Toast.makeText(thisContext, "נא הוסף מוצרים למשלוח", Toast.LENGTH_SHORT).show();
            }
            else{
                //הבונה של הדיאלוג
                AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                // יצירת הוויאו של הדיאלוג על ידי קריאת קובץ האקסמל
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_room_delivery, null, false);
                //Sets a custom view to be the contents of the alert dialog.
                builder.setView(dialogView);
                //Creates an AlertDialog with the arguments supplied to this builder.
                AlertDialog alert = builder.create();
                alert.show();

                EditText etRoomNum = dialogView.findViewById(R.id.etRoomNum);
                EditText etComment = dialogView.findViewById(R.id.etComment);
                Button btnSendDelivery2 = dialogView.findViewById(R.id.btnSendDelivery2);
                btnSendDelivery2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        roomNum = etRoomNum.getText().toString();
                        if(!roomNum.equals(""))
                            roomNumInt = Integer.parseInt(roomNum);
                        comment = etComment.getText().toString();
                        if(roomNumInt > 100){
                            Toast.makeText(thisContext, "מספר חדר לא תקין", Toast.LENGTH_SHORT).show();
                        }
                        else if(roomNum.equals("")){
                            Toast.makeText(thisContext, "אנא כתוב מספר חדר", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String email = user.getEmail();
                                DocumentReference docRef = firestore.collection("users").document(email);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                fullName = document.getString("firstName") + " " + document.getString("lastName");
                                                grade = document.getString("grade");


                                                String emailReceiver = "shministboyarreciever@gmail.com";
                                                String subject = "משלוח חדש!";
                                                message = "מבקש המשלוח: " + fullName + "\n שכבה: " + grade + "\n חדר: " + roomNum + "\n";
                                                message += comment + "\n" + "רשימת המוצרים:" + "\n";
                                                //makes the list to string
                                                if(deliveryArrayList != null){
                                                    //מסדר את ההודעה של המשלוח
                                                    String listString = deliveryArrayList.stream().map(Object::toString)
                                                            .collect(Collectors.joining("\n"));
                                                    message += listString;
                                                    //המבנה של ההודעה הוא כזה:
                                                    //מבקש המשלוח: [שם ושם משפחה]
                                                    //שכבה: [שכבה]
                                                    //חדר: [חדר]
                                                    //הערות: [הערות]
                                                    //רשימת המוצרים:
                                                    //1. 2 ביסלי
                                                    // 2. 1 תפוצ'יפס

                                                    String[] addresses = emailReceiver.split(",");

                                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                    intent.setData(Uri.parse("mailto:"));
                                                    intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                                                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                    intent.putExtra(Intent.EXTRA_TEXT, message);

                                                    try{
                                                        startActivity(intent);
                                                        alert.dismiss();
                                                        Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(thisContext, "הזמנתך נשלחה בהצלחה", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }, 7000);
                                                    }
                                                    catch(Exception e){
                                                        Toast.makeText(thisContext, "No email app is installed" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                else{
                                                    Toast.makeText(thisContext, "הוסף מוצרים למשלוח", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Log.d(TAG, "onComplete: document does not exist");
                                            }
                                        } else {
                                            Log.d(TAG, "onComplete: task is not successful");
                                        }
                                    }
                                });
                            }
                            else{
                                Log.d(TAG, "btnSendDelivery: no user is registered");
                            }
                        }

                    }
                });
            }

        }


    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        //\כאן אנחנו יכולים לראות אם יש שינוי כלשהו ברשימה
        List<DocumentSnapshot> productDocList = value.getDocuments();
        //הווליו נותן את כל המסמכים שיש בדוקיומנט.
        //אנחנו מוחקים את כל הפריטים ברשימה על מנת לעדכן אותה אם הפריטים שונו או שנוספו חדשים.
        productDialogArrayList.clear();

        for(DocumentSnapshot doc : productDocList){
            Product product = new Product(
                    doc.getString("name"),
                    doc.getDouble("price"),
                    doc.getLong("code").intValue()
            );
            productDialogArrayList.add(product);
        }

        adapter.notifyDataSetChanged();
    }

    /*
    * this delivery function deletes a product from
    * the deliveryList by the given index of it.
    * Also, the productDelivery class have an index attribute, which the
    * function changes so the index attribute will be accurate to its index position.
    *
    * @param deliveryList - the list of products in the delivery, indexToDelete
     */
    private static void deleteDelivery(List<ProductDelivery> deliveryList, int indexToDelete) {
        deliveryList.remove(indexToDelete);
        for (int i = 0; i < deliveryList.size(); i++) {
            deliveryList.get(i).setIndex(i+1);
        }
    }

    /*
    * this findProduct function finds a product in a given list of products
    * by its name.
    *
    * @param deliveryList - the product list, name - the name of the product
    * @return if found it, return the index, if not return -1. (an int)
     */
    private static int findProduct(List<ProductDelivery> deliveryList, String name) {
        for (int i = 0; i < deliveryList.size(); i++) {
            if (deliveryList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
