package com.example.sang.chattingdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sang.chattingdemo.common.Common;
import com.example.sang.chattingdemo.common.holder.QBChatMessageHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageActivity extends AppCompatActivity implements QBChatDialogMessageListener {
    QBChatDialog qbChatDialog;
    RecyclerView lvChatting;
    ImageButton btnsendMessage;
    TextView contentMessage;
    ChatMessageAdapter adapter;
    List<QBChatMessage> qbChatMessagesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        initView();
        qbChatMessagesArray=new ArrayList<QBChatMessage>();
        initChatDilalog();
        retrieveMessages();

        btnsendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(contentMessage.getText().toString());
                chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                chatMessage.setSaveToHistory(true);
                try {
                    qbChatDialog.sendMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                if (qbChatDialog.getType().equals(QBDialogType.PRIVATE)) {
                    qbChatMessagesArray.add(chatMessage);
                    adapter.notifyDataSetChanged();
                }
                contentMessage.setText("");
                contentMessage.setFocusable(true);
                scroolSmooth();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }

    private void retrieveMessages() {

        QBMessageGetBuilder qbMessageGetBuilder = new QBMessageGetBuilder();
        qbMessageGetBuilder.setLimit(100);
        if(qbChatDialog!=null)
        {
            QBRestChatService.getDialogMessages(qbChatDialog,qbMessageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    for (QBChatMessage  qbChatMessage: qbChatMessages
                         ) {
                        qbChatMessagesArray.add(qbChatMessage);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ChatMessageActivity.this);
                    adapter = new ChatMessageAdapter(ChatMessageActivity.this,qbChatMessagesArray);
                    lvChatting.setLayoutManager(layoutManager);
                    lvChatting.setAdapter(adapter);
                    scroolSmooth();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }
        else
            Toast.makeText(this, "null roi", Toast.LENGTH_SHORT).show();


    }
    private void scroolSmooth()
    {
        if(adapter.getItemCount()>0)
        lvChatting.smoothScrollToPosition(adapter.getItemCount()-1);
    }
    private void initChatDilalog() {
        qbChatDialog=(QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        Toast.makeText(this, ""+qbChatDialog.getDialogId(), Toast.LENGTH_SHORT).show();
        qbChatDialog.initForChat(QBChatService.getInstance());
        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });
        qbChatDialog.addMessageListener(this);

        if (!qbChatDialog.getType().equals(QBDialogType.PRIVATE))
        {

            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);
            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    Toast.makeText(ChatMessageActivity.this, "Join Group Succesfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(ChatMessageActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initView() {
        lvChatting = (RecyclerView) findViewById(R.id.list_chat_messages);
        btnsendMessage = (ImageButton) findViewById(R.id.sendMessage);
        contentMessage =(EditText)findViewById(R.id.content_message);

    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        qbChatMessagesArray.add(qbChatMessage);
        adapter.notifyDataSetChanged();
        scroolSmooth();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("ErrorChatMessage",""+e.getMessage());
    }
}
