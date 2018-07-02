package com.example.sang.chattingdemo.common;

import com.example.sang.chattingdemo.common.holder.QBUserHolder;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Common {
     public static final String DIALOG_EXTRA="Dialogs";
        public static String createChatDialogName(ArrayList<Integer> arrayList)
        {
            List<QBUser> userList = QBUserHolder.getInstance().getUsersById(arrayList);
            StringBuilder name =new StringBuilder();
            for (QBUser qbUser :userList)
                name.append(qbUser.getFullName()).append(" ");
            if (name.length()>30)
                name.replace(30,name.length()-1,"...");

            return name.toString();

        }
}
