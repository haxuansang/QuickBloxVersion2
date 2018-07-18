package com.example.sang.chattingdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.sang.chattingdemo.common.Common;
import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {
    String username,password;
    FloatingActionButton btnAddChat;
    ListView lvChatting;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final Intent intent = getIntent();
        Bundle bundleUser= intent.getBundleExtra("PackageUser");
        username=bundleUser.getString("username");
        password=bundleUser.getString("password");
        initConnectionTimeOut();
        btnAddChat=(FloatingActionButton)findViewById(R.id.addChatButton);
        lvChatting=(ListView)findViewById(R.id.lvChatting);
        loadBitmapUsers();
        createSessionForChat();
        loadChatDialogs();
        btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent= new Intent(ChatActivity.this,ListUserActivity.class);
                startActivity(newIntent);
            }
        });
        lvChatting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                QBChatDialog qbChatDialog = (QBChatDialog)lvChatting.getAdapter().getItem(i);
                Intent intent1 = new Intent(ChatActivity.this,ChatMessageActivity.class);
                intent1.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                startActivity(intent1);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadChatDialogs();

    }
    private void initConnectionTimeOut()
    {
        QBChatService.getInstance();
        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(10000);
        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(600000); //Sets chat socket's read timeout in seconds
        chatServiceConfigurationBuilder.setKeepAlive(true); //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.setUseTls(true); //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);
        QBChatService.getInstance().setReconnectionAllowed(true);
    }
    private void createSessionForChat()
    {

        progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final QBUser qbUser = new QBUser(username,password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
                public void onSuccess(QBSession qbSession, Bundle bundle) {

                    qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                        @Override
                        public void onSuccess(Object o, Bundle bundle) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Log.e("Error",""+e.getMessage());
                            Toast.makeText(ChatActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }
    private  void loadBitmapUsers()
    {

                QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                        QBUserHolder.getInstance().putUsers(qbUsers);

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });






    }


    private  void loadChatDialogs()
    {
        QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
        requestGetBuilder.setLimit(500);
        QBRestChatService.getChatDialogs(null,requestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                        ChatDialogsAdapter chatDialogsAdapter = new ChatDialogsAdapter(qbChatDialogs,ChatActivity.this);
                        lvChatting.setAdapter(chatDialogsAdapter);
                        chatDialogsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("onError", "onError: "+e.getMessage() );
            }
        });
    }
}
