package com.example.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BondedItemAdapter extends BaseAdapter  {
    private Context mContext;

    private ArrayList<BluetoothDevice> bondedList;
    public BondedItemAdapter(Context context){
        this.mContext=context;
        bondedList=new ArrayList<>();
    }

    public void addBondedDeviceAndReferesh(BluetoothDevice bluetoothDevice){
        bondedList.add(bluetoothDevice);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return bondedList.size();
    }

    @Override
    public Object getItem(int i) {
        return bondedList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("TAG","getView");
        View v=null;
        if(view!=null){
            v=view;
        }else{
            v=View.inflate(this.mContext,R.layout.bonded_item,null);
        }
        TextView item_bt=(TextView)v.findViewById(R.id.btoned_bt_item);

        Button btn_cancle_bond=(Button)v.findViewById(R.id.btn_cancle_bond);
        final int position=i;
        btn_cancle_bond.setOnClickListener(new View.OnClickListener() {
            //将取消配对的点击事件处理写在这
            @Override
            public void onClick(View view) {
                Log.d("TAG","the click position="+position);
                BluetoothDevice bluetoothDevice=bondedList.get(position);
                try {
                    Method method=BluetoothDevice.class.getMethod("removeBond");
                    method.invoke(bluetoothDevice);
                    bondedList.remove(position);
                    notifyDataSetChanged();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        String name=bondedList.get(i).getName()!=null?bondedList.get(i).getName():bondedList.get(i).getAddress();
        item_bt.setText(name);
        return v;
    }


}
