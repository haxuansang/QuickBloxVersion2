package com.example.sang.chattingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ListUserAdapter extends BaseAdapter {
    Context context;
    ArrayList<QBUser> qbUserArrayList;

    public ListUserAdapter(Context context, ArrayList<QBUser> qbUserArrayList) {
        this.context = context;
        this.qbUserArrayList = qbUserArrayList;
    }



    @Override
    public int getCount() {
        return qbUserArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return qbUserArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;

        if (convertView==null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_multiple_choice,viewGroup,false);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(qbUserArrayList.get(i).getLogin());

        }
        return view;
    }
}
