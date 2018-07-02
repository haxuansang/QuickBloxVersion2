package com.example.sang.chattingdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

public class ChatMessageAdapter extends BaseAdapter {
    Context context;
    ArrayList<QBChatMessage> qbChatMessages;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return qbChatMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = view;
        if (view==null){
            if(qbChatMessages.get(i).getSenderId().equals(QBChatService.getInstance().getUser().getId())) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_send_messages,viewGroup,false);
                BubbleTextView bubbleTextView = (BubbleTextView)convertView.findViewById(R.id.idmessend);
                bubbleTextView.setText(qbChatMessages.get(i).getBody());
                Log.e("asd","send");
            }
            else
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_receive_messages,viewGroup,false);
                TextView textView =(TextView)convertView.findViewById(R.id.message_user);
                textView.setText(QBUserHolder.getInstance().getUserById(qbChatMessages.get(i).getSenderId()).getFullName());
                BubbleTextView bubbleTextView = (BubbleTextView)convertView.findViewById(R.id.idmesreceive);
                bubbleTextView.setText(qbChatMessages.get(i).getBody());
                Log.e("asd","receive");
            }
        }
        return convertView;
    }
}
