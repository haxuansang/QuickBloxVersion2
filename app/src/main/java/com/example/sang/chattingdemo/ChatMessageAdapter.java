package com.example.sang.chattingdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<QBChatMessage> mMessageList;
    Integer userID;

    public ChatMessageAdapter(Context context, List<QBChatMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
        userID = QBChatService.getInstance().getUser().getId();

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {


        if (mMessageList.get(position).getSenderId().equals(userID)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_send_messages, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_receive_messages, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(mMessageList.get(position));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(mMessageList.get(position));
               if (position>0) ((ReceivedMessageHolder) holder).check(position);
        }
    }



    private class SentMessageHolder extends RecyclerView.ViewHolder {
        BubbleTextView bubbleTextView;

        SentMessageHolder(View itemView) {
            super(itemView);

            bubbleTextView = (BubbleTextView) itemView.findViewById(R.id.idmessend);
        }

        void bind(QBChatMessage message) {
            bubbleTextView.setText(message.getBody());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        BubbleTextView bubbleTextView;
        CircleImageView userImage;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            bubbleTextView = (BubbleTextView) itemView.findViewById(R.id.idmesreceive);
            userImage=(CircleImageView)itemView.findViewById(R.id.user_image);
        }

        void bind(QBChatMessage message) {
            bubbleTextView.setText(message.getBody());

        }
         void check(int position) {
            if(position==0)
                userImage.setVisibility(View.VISIBLE);
            else
            if (mMessageList.get(position-1).getSenderId().equals(userID))
                    userImage.setVisibility(View.VISIBLE);

        }
    }
}