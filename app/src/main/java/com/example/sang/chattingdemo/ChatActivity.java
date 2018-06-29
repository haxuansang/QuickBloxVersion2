package com.example.sang.chattingdemo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    String username,password;
    FloatingActionButton btnAddChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        Bundle bundleUser= intent.getBundleExtra("PackageUser");
        username=bundleUser.getString("username");
        password=bundleUser.getString("password");
        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
        btnAddChat=(FloatingActionButton)findViewById(R.id.addChatButton);
        createSessionForChat();

    }
    private void createSessionForChat()
    {

    }
}
