package com.example.sang.chattingdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

public class ChatMessageActivity extends AppCompatActivity {
    QBChatDialog qbChatDialog;
    ListView lvChatting;
    ImageButton btnsendMessage;
    TextView contentMessage;
    ChatMessageAdapter adapter;
    ArrayList<QBChatMessage> qbChatMessagesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        lvChatting=(ListView)findViewById(R.id.lvChatting);
        initView();

        retrieveMessages();
       // initChatDilalog();

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
                qbChatMessagesArray.add(chatMessage);
                adapter.notifyDataSetChanged();
                contentMessage.setText("");
                contentMessage.setFocusable(true);

            }
        });
    }

    private void retrieveMessages() {
        qbChatDialog=(QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());
        QBMessageGetBuilder qbMessageGetBuilder = new QBMessageGetBuilder();
        qbMessageGetBuilder.setLimit(500);
        if(qbChatDialog!=null)
        {
            QBRestChatService.getDialogMessages(qbChatDialog,qbMessageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                   qbChatMessagesArray=qbChatMessages;
                    adapter = new ChatMessageAdapter(ChatMessageActivity.this,qbChatMessagesArray);
                    lvChatting.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("Error",e.getMessage());
                }
            });
        }
        else
            Toast.makeText(this, "nulll roi", Toast.LENGTH_SHORT).show();
    }

    private void initChatDilalog() {


        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });
        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                qbChatMessagesArray.add(qbChatMessage);
                adapter.notifyDataSetChanged();



            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("Error",e.getMessage());
            }
        });
    }

    private void initView() {
        lvChatting = (ListView)findViewById(R.id.lvChatMain);
        btnsendMessage = (ImageButton) findViewById(R.id.sendMessage);
        contentMessage =(EditText)findViewById(R.id.content_message);

    }
}
