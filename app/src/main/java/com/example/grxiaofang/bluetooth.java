package com.example.grxiaofang;
/*蓝牙主程序*/
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class bluetooth extends AppCompatActivity {
    private Toast mToast;
    //创建广播接收者的对象
    MyReceiver myReceiver=new MyReceiver();
    //获取蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////
    public ArrayAdapter arrayAdapter;  //这个适配器列表是用来加载到列表的数据
    public ArrayList<BluetoothDevice> deviceAdress = new ArrayList<>();  //存放蓝牙设备（这里Adress我忘了改过来了，这是存放设备不是设备地址）
    public ArrayList<String> deviceName = new ArrayList<>();  //存放蓝牙名称和地址
    public ListView listView;   //定义展示列表
    //手机连接的UUID
    //设备连接的UUID由厂商决定。            00001101-0000-1000-8000-00805F9B34FB
    private final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //蓝牙通信的UUID，必须为这个，如果换成其他的UUID会无法通信
    private BluetoothSocket bluetoothSocket = null;
    private EditText editText_send;
    private TextView textView_receive;
    private Button button_send,button;
    private Switch kaiguan;

    private SoundPool soundPool;//声明一个SoundPool
    private int soundID;//创建某个声音对应的音频ID

    // 用来发数据
    OutputStream outputStream;
    String s;//存储接收的数据
    int state;

    //"11","22","33","44","55","66","77","88","99"
    //##########################################################################################################################//
    public class MyReceiver extends BroadcastReceiver {
        //private static final String action1="com.mingrisoft";	  //声明第一个动作
        //private static final String action2="mingrisoft";      //声明第二个动作

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(context, "开始搜索设备", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////
                for (int i = 0; i < deviceAdress.size(); i++) {
                    if (deviceAdress.get(i).getAddress().equals(device.getAddress())) return;
                    //上面if语句就是去除已经获取的蓝牙设备
                }
                // 不是重复的就添加到列表中(获取未配对的蓝牙设备)
                deviceAdress.add(device);  //添加地址到列表中   用于鉴别是否已经添加列表和点击事件用的
                deviceName.add("地址："+device.getAddress()+"\n"+"名称："+device.getName());  //存放蓝牙名称和地址用于显示到列表上的
                arrayAdapter.notifyDataSetChanged();  //更新列表
                Connect_BT(deviceAdress);
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            } else if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            }
            //if (intent.getAction().equals(action1)){
            //    Toast.makeText(context, "MyReceiver收到:com.mingrisoft的广播", Toast.LENGTH_SHORT).show(); 	           //回复该动作收到广播
            //} else if (intent.getAction().equals(action2)){
            //    Toast.makeText(context, "MyReceiver收到:mingrisoft的广播", Toast.LENGTH_SHORT).show();          //回复该动作收到广播
            //}
        }
    }
//##########################################################################################################################//


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //////////////////////////////////////////////创建搜索蓝牙列表的///////////////////////////////////////////////////////////////////并把
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,deviceName);  //实例化ArrayAdapter对象deviceName集合数据放入arrayAdapter适配器集合内
        listView = (ListView) findViewById(R.id.list);  //获取列表框的
        listView.setAdapter(arrayAdapter);  //将arrayAdapter集合内的数据加载到列表框 就是适配器对象与ListView关联
        initSound();
//        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        //创建广播接收者的对象
//        MyReceiver myReceiver=new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED );
        //上面是添加动作事件
        //过滤器
        intentFilter.addAction("com.mingrisoft");
        //注册广播接收者的对象
        registerReceiver(myReceiver,intentFilter);


        button= (Button) findViewById(R.id.Broadcast);  //获取布局文件中的广播按钮


        button.setOnClickListener(new View.OnClickListener() {
            //为按钮设置单击事件
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.Broadcast:
                        playSound();   //调用方法
                        break;
                }
                //Intent intent=new Intent();                    //创建Intent对象
                //intent.setAction("com.mingrisoft");            //为Intent添加动作com.mingrisoft
                //sendBroadcast(intent);                         //发送广播
                //////////蓝牙刷新///////////////
                deviceAdress.clear();         //
                deviceName.clear();           //
                startDiscovery();             //
                ////////////////////////////////
//                listView.setAdapter(arrayAdapter);  //将arrayAdapter集合内的数据加载到列表框 就是适配器对象与ListView关联

            }

        });

        //按钮搜寻蓝牙
        Button button_discovery = (Button) findViewById(R.id.DiscoveryBT);
        button_discovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });


    }

    @SuppressLint("NewApi")
    private void initSound() {
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(this, R.raw.testsong, 1); //配置提示音音源
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //判断是否支持蓝牙
    public boolean isSupportBlueTooth() {
        if(mBluetoothAdapter != null) {
            return true;
        }
        else {
            return false;
        }
    }
    //获取蓝牙状态
    public  boolean BlueToothState() {
        assert (mBluetoothAdapter != null);   //若不支持该蓝牙设备会有个断言
        return mBluetoothAdapter.isEnabled();
    }
    //打开蓝牙
    public void OpenBlueTooth(View view) {
        if(isSupportBlueTooth() == true) {
            if(!BlueToothState()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,0);
                showToast("亲,打开了噢！需要什么帮助吗？");
            }
            else {
                showToast("亲,已经打开了噢，无需重复打开。");
            }
        }
        else {
            showToast("亲,您不支持此蓝牙设备!");
        }
    }
    //关闭蓝牙
    public void CloseBlueTooth(View view) {
        mBluetoothAdapter.disable();
        showToast("亲,我们会再见面的!");
    }

    // 开始搜索
    public void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {mBluetoothAdapter.cancelDiscovery();  Toast.makeText(this,"搜索器打开",Toast.LENGTH_SHORT).show();}
        mBluetoothAdapter.startDiscovery();
        if (!mBluetoothAdapter.isDiscovering()) {Toast.makeText(this,"搜索器没打开",Toast.LENGTH_SHORT).show();}
    }

    //取消搜索
    public void cancelScanBule() {
        mBluetoothAdapter.cancelDiscovery();
    }

    //在activity结束时要注销；
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //解除注册
        unregisterReceiver(myReceiver);
    }


    //连接蓝牙
    public void Connect_BT(ArrayList<BluetoothDevice> deviceAdress) {
        //MainActivity 实现OnItemClickListener 然后重写方法
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //final BluetoothDevice[] romoteDevice = new BluetoothDevice[1];
                if(mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice clickDevice = (BluetoothDevice)deviceAdress.get(position);
                String s1 = String.valueOf(position);  //编号
                Toast.makeText(bluetooth.this, s1 + "--" + clickDevice.getName() + "--" + clickDevice.getAddress(), Toast.LENGTH_SHORT).show();
                //在连接前需要先关闭搜索
                //点击列表，去请求服务器
                if (clickDevice != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//////////////////////////////////////////需要配对码进行配对连接////////////////////////////////////////////////////////
//                                Method method = BluetoothDevice.class.getMethod("createBond");
////                                Log.e(getPackageName(), "开始配对");
//                                method.invoke(deviceAdress.get(position));
//                                deviceAdress.get(position).createBond();
//////////////////////////////这里如果没有配对过的设备是会弹出窗口输入配对码的////////////////////////////////////////////////////////////
                                bluetoothSocket = clickDevice.createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
                                bluetoothSocket.connect();
                                state=0;
                                //textView_receive.setText("连接成功");
//                                showToast("连接成功");
                                readThread mreadThread = new readThread();
                                mreadThread.start();
////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                //下面提示框没有回调效果显示，不知道为什么
//                                Looper.prepare();
//                                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT);
//                                Looper.loop();
                                //错误原因是自己想在网络请求成功后，弹出一个Toast提醒，但由于程序在主线程中创建handler后会创建一个looper对象，而子线程却不会，那什么时候需要looper？
                                //
                                //　　Looper用于封装了android线程中的消息循环，默认情况下一个线程是不存在消息循环（message loop）的，需要调用Looper.prepare()来给线程创建一个消息循环，调用Looper.loop()来使消息循环起作用，使用Looper.prepare()和Looper.loop()创建了消息队列就可以让消息处理在该线程中完成。
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });
        /*发送功能*/
        button_send = (Button) findViewById(R.id.button_send);
        textView_receive = findViewById(R.id.receive);
        textView_receive.setMovementMethod(ScrollingMovementMethod.getInstance()); //文本框滑动效果
        editText_send = findViewById(R.id.send);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_receive.setText(" 发送到数据：" + editText_send.getText());
                state=1;
                String text = editText_send.getText().toString();
                String ch1 ="\r\n";
                String end_ch = text+ch1;
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(end_ch.getBytes());
                    //outputStream.write(text.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /*开关灯功能*/
        kaiguan = findViewById(R.id.kaiguan);  //获取布局文件中的开关按钮
        kaiguan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String led_open = "led(0)"; //usmart的led函数功能
                    String ch1 ="\r\n"; //加\r\n是因为stm32接收的串口必须以这个结尾
                    String end_ch = led_open+ch1; //两个拼成
                    try {
                        outputStream = bluetoothSocket.getOutputStream();
                        outputStream.write(end_ch.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String led_close = "led(1)"; //usmart的led函数功能
                    String ch1 ="\r\n";
                    String end_ch = led_close+ch1;
                    try {
                        outputStream = bluetoothSocket.getOutputStream();
                        outputStream.write(end_ch.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    /**
     * 读取数据
     */
    private class readThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = bluetoothSocket.getInputStream();
                textView_receive.setText("客户端:获得输入流");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    if ((bytes = is.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        s = new String(buf_data);
                        //textView_receive.setText("客户端:读取数据了");
                        if(state==1) {
                            readThread.sleep(3000);
                            textView_receive.setText("读取的数据：\n" + s);
                        }
                    }
                } catch (IOException e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    //在完成BluetoothSocket的处理后，始终记得调用close()方法来进行清理


    //数据传输

///////////////////////////////////////////
//        强制打开蓝牙
//        mBluetoothAdapter.enable();
//////////////////////////////////////////
// 客户端线程

    //显示函数
    public void showToast(String text) {
        //Toast显示的时间有限，过一定的时间就会自动消失
        //因此这里要判断是否消失了
        //Toast.LENGTH_SHORT这个是设置显示两秒，LONG是3秒
        if( mToast == null ) {  //检查mToast文本空了没
            //下面这行程序是一般是只执行一次的，就是刚开始空文本时初始化一次里面显示设置
            mToast = Toast.makeText(this,text,Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);//这里是倒数两秒，也就是浮框消失后重新设置文本内容
            //达到事件不同回显文本不同的切换效果
        }
        mToast.show();
    }
    /*提示音方法*/
    private void playSound() {
        soundPool.play(
                soundID,
                0.1f,      //左耳道音量【0~1】
                0.5f,      //右耳道音量【0~1】
                0,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }
    /*提示音方法*/
}

//强制转化字符串三种方法
/*
    int a = 123;
1、 String s1 = String.valueOf(a);
2、 String s2 = Iteger.toString(a);
3、 String s3 = "" + a;
 */



////显示单选列表项的对话框
//AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//创建AlertDialog.Builder对象
//                builder.setIcon(R.drawable.discovery);
//                        builder.setTitle("搜索设备");
//                        //第一个是参数为列表数组，第二个为从0开始选择，第三个单击内部监听器       对话框单选->添加项
//                        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
//@Override
//public void onClick(DialogInterface dialog, int which) {
//        Toast.makeText(MainActivity.this,"---"+items[which]+"---",Toast.LENGTH_SHORT).show();
//        }
//        });
//        //这里的按钮名字设置为确定，后面那个单击内部监听器没有设置
//        //设置确定按钮
//        builder.setNegativeButton("扫描", null);
//        builder.setPositiveButton("连接",null);
//        builder.create().show();
