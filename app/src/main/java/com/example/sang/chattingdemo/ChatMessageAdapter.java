package com.example.sang.chattingdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sang.chattingdemo.common.holder.QBFileHolder;
import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<QBChatMessage> mMessageList;
    public  List<Bitmap> arrayUserImage;
    Integer userID;


    public ChatMessageAdapter(Context context, List<QBChatMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
        userID = QBChatService.getInstance().getUser().getId();
        //getURLArrays();

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        BubbleTextView bubbleTextView;
        CircleImageView userImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            bubbleTextView = (BubbleTextView) itemView.findViewById(R.id.idmesreceive);
            userImage = (CircleImageView) itemView.findViewById(R.id.user_image);

        }

        void bind(final QBChatMessage message) {
            bubbleTextView.setText(message.getBody());
            if (QBFileHolder.getInstance().getFileUserById(message.getSenderId())!=null)
            {
                userImage.setImageBitmap(QBFileHolder.getInstance().getFileUserById(message.getSenderId()));
            }
            else
            {
            QBUsers.getUser(message.getSenderId()).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(final QBUser qbUser, Bundle bundle) {
                    if(qbUser.getFileId()!=null)
                    {
                        QBContent.getFile(qbUser.getFileId()).performAsync(new QBEntityCallback<QBFile>() {
                            @Override
                            public void onSuccess(QBFile qbFile, Bundle bundle) {
                                String URL = qbFile.getPublicUrl();
                                Picasso.with(mContext).load(URL).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        QBFileHolder.getInstance().putQBFileUser(qbUser.getId(),bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(mContext, "Cant load image of user" + message.getSenderId(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });

            }
        }

    }

    private void getURLArrays() {
        arrayUserImage = new ArrayList<>();
        List<QBUser> qbUserList = new ArrayList<>();
        qbUserList = QBUserHolder.getInstance().getUsers();
        Toast.makeText(mContext, ""+qbUserList.size(), Toast.LENGTH_SHORT).show();


        for(QBUser user : qbUserList)
        {
            QBUsers.getUser(user.getId()).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(final QBUser qbUser, Bundle bundle) {
                    if(qbUser.getFileId()!=null)
                    {
                        QBContent.getFile(qbUser.getFileId()).performAsync(new QBEntityCallback<QBFile>() {
                            @Override
                            public void onSuccess(final QBFile qbFile, Bundle bundle) {

                                Picasso.with(mContext).load(qbFile.getPublicUrl()).into(new Target() {
                                 @Override
                                 public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                     Log.e("load bitmap" ,"thanh cong" +qbFile.getId());
                                 }

                                 @Override
                                 public void onBitmapFailed(Drawable errorDrawable) {

                                 }

                                 @Override
                                 public void onPrepareLoad(Drawable placeHolderDrawable) {

                                 }
                             });

                            }

                            @Override
                            public void onError(QBResponseException e) {

                            }
                        });
                    }
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
        Toast.makeText(mContext, ""+arrayUserImage.size(), Toast.LENGTH_SHORT).show();

    }
}