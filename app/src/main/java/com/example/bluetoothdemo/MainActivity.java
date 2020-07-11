package com.example.bluetoothdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;



    private IntentFilter intentFilter;

    private ListView listView;

    private ListView bondedListView;

    private BondedItemAdapter bondedItemAdapter;

    private MyAdapter myAdapter;


    private final BroadcastReceiver btBroadcaseReceiver=new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondstate = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    Log.d("TAG","bondstate::::= "+bondstate);

                    if(bondstate==BluetoothDevice.BOND_BONDED){//如果新配对了一个设备

                        //就将它更新到listview中
                        BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        bondedItemAdapter.addBondedDeviceAndReferesh(bluetoothDevice);


                    }

                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("TAG","found bluetooth device bluetooth name="+bluetoothDevice.getName()+" bluetooth address="+bluetoothDevice.getAddress());

                    myAdapter.addBluetoothAndReferesh(bluetoothDevice);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.d("TAG","bluetooth enable status changed");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d("TAG","bluetooth discovery finished");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d("TAG","start bluetooth discovery");
                    break;
                case  BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.d("TAG","action_acl_connected");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d("TAG","action_acl_disconnected");
                    break;

            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission();

        initWidget();
        init();






    }



    private void requestLocationPermission(){
        /*
         * 在这里需要获取位置的权限，不然搜索设备时候搜不到
         * 需要在manifest文件中将这两个权限也加进去
         * */
        int checkAccessFineLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(checkAccessFineLocationPermission!= PackageManager.PERMISSION_GRANTED){

            Log.d("TAG","request access_fine_loacation");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
        }



        int checkAccessCoarseLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(checkAccessCoarseLocationPermission!= PackageManager.PERMISSION_GRANTED){

            Log.d("TAG","request access_fine_loacation");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},3);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //注册蓝牙enable状态改变的广播
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //注册搜索完成的广播
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //注册蓝牙找到设备的广播
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //注册蓝牙开始扫描的广播
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //注册蓝牙配对变化的广播
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //注册acl连接的广播
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //注册acl断开的广播
        this.registerReceiver(btBroadcaseReceiver,intentFilter);
        /*
        这里将btBroadcastReceiver和intentFilter绑定，并注册广播
        * */



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //这里会看到permission请求是否成功的消息
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i=0;i<permissions.length;i++){
            Log.d("TAG","permission="+permissions[i]+" grantResults="+grantResults[i]);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(btBroadcaseReceiver);
        //在activity stop之前需要ungister receiver，否则会引起内存泄露
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void init(){//初始化好蓝牙

        bluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
        if(bluetoothAdapter==null){
            Log.d("TAG","设备不支持蓝牙");
        }


        if(!bluetoothAdapter.isEnabled()){//如果蓝牙没有enable。就去enable蓝牙
            if(!bluetoothAdapter.enable()){
                Log.d("TAG","enable bluetooth failed");
            }
        }


        //将配对过的设备添加到listview中
        Set<BluetoothDevice> bondedDevices=bluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator=bondedDevices.iterator();
        while (iterator.hasNext()){
            bondedItemAdapter.addBondedDeviceAndReferesh(iterator.next());
        }


    }

    private void initWidget(){
        Button button=(Button)findViewById(R.id.btn_searchBT);
        button.setOnClickListener(this);

        myAdapter=new MyAdapter(this);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG","the click position = "+ i
                +"the click item="+adapterView.getAdapter().getItem(i).toString());

                BluetoothDevice bluetoothDevice= (BluetoothDevice) myAdapter.getItem(i);
                bluetoothAdapter.cancelDiscovery();
                if(!bluetoothDevice.createBond()){
                    Log.d("TAG","create bond failed");
                }
            }
        });

        bondedListView=(ListView)findViewById(R.id.bondedDevice_list_view);
        bondedItemAdapter=new BondedItemAdapter(this);
        bondedListView.setAdapter(bondedItemAdapter);
        bondedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_searchBT:
                bluetoothAdapter.startDiscovery();
                break;
        }
    }
}