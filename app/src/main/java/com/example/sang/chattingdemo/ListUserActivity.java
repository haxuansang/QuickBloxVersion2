package com.example.sang.chattingdemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import com.example.sang.chattingdemo.common.Common;
import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {
    ListView listView;
    Button  btnCreateChat;
    Button btnEditProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        listView=(ListView)findViewById(R.id.lvUsers);
        btnCreateChat=(Button)findViewById(R.id.btnSubmit);
        btnEditProfile=(Button)findViewById(R.id.btnEditprofile);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        retrieveAllUser();
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countChoice=listView.getCount();
                if (listView.getCheckedItemPositions().size()==1){
                    createPrivateChat(listView.getCheckedItemPositions());
                }
                else if(listView.getCheckedItemPositions().size()>1)
                {
                    createGroupChat(listView.getCheckedItemPositions());
                }
                else
                    Toast.makeText(ListUserActivity.this, "Please select friend for chatting!!!", Toast.LENGTH_SHORT).show();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Intent intent = new Intent(ListUserActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });
    }



    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Please waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        int countChoice = listView.getCount();
        for (int i=0;i<countChoice;i++)
        {
            if (checkedItemPositions.get(i))
            {
                QBUser qbUser = (QBUser)listView.getItemAtPosition(i);
                QBChatDialog qbChatPrivate = DialogUtils.buildPrivateDialog(qbUser.getId());
                QBRestChatService.createChatDialog(qbChatPrivate).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            progressDialog.dismiss();

                        Toast.makeText(ListUserActivity.this, "Create Private Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("createPrivateChat",""+e.getMessage());

                    }
                });
            }
        }
    }
    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog progressDialog  = new ProgressDialog(this);
        progressDialog.setMessage("Please waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        int countChoice = listView.getCount();
        ArrayList<Integer> occupandIdsList = new ArrayList<>();
        for (int i=0;i<countChoice;i++)
        {
            if(checkedItemPositions.get(i))
            {
                QBUser qbUser=(QBUser)listView.getItemAtPosition(i);
                occupandIdsList.add(qbUser.getId());

            }
        }
        occupandIdsList.add(QBChatService.getInstance().getUser().getId());
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupandIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupandIdsList);
        dialog.setName("Group"+dialog.getDialogId().toString());
        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(ListUserActivity.this, "Create Group Chat Succesfully!!!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("onError",""+e.getMessage());

            }
        });
    }

    private void retrieveAllUser() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUserHolder.getInstance().putUsers(qbUsers);
                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for (QBUser qbUser : qbUsers)
                {
                    if(!qbUser.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbUserWithoutCurrent.add(qbUser);


                }
                ListUserAdapter listUserAdapter = new ListUserAdapter(getBaseContext(),qbUserWithoutCurrent);
                listView.setAdapter(listUserAdapter);
                listUserAdapter.notifyDataSetChanged();


            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("retrieve",""+e.getMessage());
            }
        });
    }
}
