package com.example.sang.chattingdemo.common.holder;

import android.util.SparseArray;

import com.quickblox.content.model.QBFile;
import com.quickblox.users.model.QBUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QBUserHolder {
    private static QBUserHolder instance;
    public SparseArray<QBUser> qbUserSparseArray;
    public SparseArray<String> qbURLImagesUser;


    public static synchronized QBUserHolder getInstance() {
        if (instance == null)
            instance = new QBUserHolder();
        return instance;

    }

    private QBUserHolder() {
        qbUserSparseArray = new SparseArray<>();
        qbURLImagesUser=new SparseArray<>();

    }
    public void putURL(int i,String URL)
    {
        qbURLImagesUser.put(i,URL);
    }
    public List<Integer> getFileIDs()
    {
        List<Integer> fileIdsOfUsers = new ArrayList<>();
        for(int i=0;i<qbUserSparseArray.size();i++)
        {
            fileIdsOfUsers.add(qbUserSparseArray.keyAt(i));

        }
        return fileIdsOfUsers;
    }
    public String getURLById(int i)
    {
        return qbURLImagesUser.get(i);
    }
    public void putUsers(List<QBUser> qbUsers) {
        for (QBUser qbUser : qbUsers)
            putUser(qbUser);

    }


    public void putUser(QBUser qbUser) {
        qbUserSparseArray.put(qbUser.getId(), qbUser);

    }


    public QBUser getUserById(int i) {
        return qbUserSparseArray.get(i);

    }

    public List<QBUser> getUsersById(List<Integer> ids) {
        List<QBUser> qbUserList = new ArrayList<>();
        for (Integer i : ids) {
            QBUser user = getUserById(i);
            if (user != null) {
                qbUserList.add(user);
            }

        }
        return qbUserList;
    }



}
