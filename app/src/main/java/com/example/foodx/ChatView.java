package com.example.foodx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatView extends AppCompatActivity {
    private RecyclerView recView;
    private Button backbtn;

    private UserAdapter useradapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private  List<String> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        backbtn = findViewById(R.id.backBtnchview);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatView.this,MainMenuActivity.class));
            }
        });

        recView = findViewById(R.id.Chats_recycler);
        recView.setHasFixedSize(true);
        recView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Chat chat= snapshot1.getValue(Chat.class);
                    if (chat.getSender().equals(fuser.getUid()) && !userList.contains(chat.getReceiver()) )
                    {
                        userList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid()) && !userList.contains(chat.getSender()))
                    {
                        userList.add(chat.getSender());
                    }

                }
                for( String i : userList)
                {
                    Log.d("THE USER LIST FOR CHAT VIEW :", "user :"+i);
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void readChats()
    {
        mUsers = new ArrayList<>();
        useradapter = new UserAdapter(getApplicationContext(),mUsers);
        recView.setAdapter(useradapter);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    User user =snapshot1.getValue(User.class);
                    for (String id :userList)
                    {
                        Log.d("THE USER LIST FOR CHAT VIEW 3CURRENT ID:", "user :"+ id);
                        if(user.getId().equals(id)){
                            Log.d("THE USER LIST FOR CHAT VIEW 22:", "user :"+ mUsers.toString());
                            if (mUsers.size()!=0){
                                for (User user1:mUsers)
                                {

                                    if(!user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                        Log.d("THE USER LIST FOR CHAT VIEW IF CURRENT USER NOT ALREADY THERE IN MUSER ADD HIM", "user :"+ mUsers.toString());
                                    }
                                }
                            } else {
                                mUsers.add(user);
                                Log.d("THE USER LIST FOR CHAT VIEW 3 mUSER WAS NULL SO ADDED USER :", "user :"+ mUsers.toString());
                            }
                        }
                    }
                }
               useradapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}