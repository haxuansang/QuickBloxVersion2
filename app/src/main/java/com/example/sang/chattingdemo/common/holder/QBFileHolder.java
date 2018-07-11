package com.example.sang.chattingdemo.common.holder;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.quickblox.content.model.QBFile;

import java.util.HashMap;

public class  QBFileHolder {
    private static QBFileHolder instance=null;
    public HashMap<Integer,Bitmap> arrayImageUser;
    public static synchronized QBFileHolder getInstance()
    {
        if(instance==null)
            instance = new QBFileHolder();
        return instance;
    }
    private QBFileHolder()
    {
        arrayImageUser = new HashMap<>();
    }
    public void putQBFileUser(int idUser,Bitmap bitmap)
    {
        arrayImageUser.put(idUser,bitmap);
    }
    public Bitmap getFileUserById(int i)
    {
       return arrayImageUser.get(i);
    }

}
