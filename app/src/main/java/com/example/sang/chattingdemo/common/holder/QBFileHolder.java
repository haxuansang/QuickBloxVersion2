package com.example.sang.chattingdemo.common.holder;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.quickblox.content.model.QBFile;

import java.util.HashMap;
import java.util.Hashtable;

public class  QBFileHolder {
    private static QBFileHolder instance=null;
    public Hashtable<Integer,Bitmap> arrayImageUser;
    public int count=0;
    public static synchronized QBFileHolder getInstance()
    {
        if(instance==null)
            instance = new QBFileHolder();
        return instance;
    }
    private QBFileHolder()
    {
        arrayImageUser = new Hashtable<>();

    }
    public void putQBFileUser(int idUser,Bitmap bitmap)
    {
        arrayImageUser.put(idUser,bitmap);
        count++;
    }

    public Bitmap getFileUserById(int i)
    {
       return arrayImageUser.get(i);
    }
    public int sizeOfImages()
    {
        return arrayImageUser.size();
    }


}
