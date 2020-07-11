package com.example.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<BluetoothDevice> arrayList;
    public MyAdapter(Context context){

        this.context=context;
        arrayList=new ArrayList<>();

    }


    public void addBluetoothAndReferesh(BluetoothDevice bluetoothDevice){
        arrayList.add(bluetoothDevice);
        this.notifyDataSetChanged();
        //每当有一个item添加进来的时候，就通知listview更新数据
    }

    @Override
    public int getCount() {
        Log.d("TAG","getCount");
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        Log.d("TAG","getItem i="+i);
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.d("TAG","getItemId i="+i);
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Log.d("TAG","getView");
        View v=null;
        if(view!=null){
            v=view;
        }else{
            v=View.inflate(this.context,R.layout.list_item,null);
        }
        TextView item_bt=(TextView)v.findViewById(R.id.btn_BT_item);

        String name=arrayList.get(i).getName()!=null?arrayList.get(i).getName():arrayList.get(i).getAddress();
        item_bt.setText(name);
        return v;
    }





}
