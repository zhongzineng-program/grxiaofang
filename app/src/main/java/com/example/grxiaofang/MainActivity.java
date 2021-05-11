package com.example.grxiaofang;
/*登录代码*/
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public Button bt_login;
    public ImageButton clear_text1,clear_text2;
    public TextView btn_find,btn_register,btn_phone;
    public EditText edit_username;
    public EditText edit_password;
    public String user;
    public String pwd;
    public String user1;
    public String pwd1;
    private int flag;
    /*实现按两次才退出程序*/
    long eTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - eTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
                eTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*实现按两次才退出程序*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        bt_login = findViewById(R.id.bt_login);
        edit_username = findViewById(R.id.et_login_username);
        edit_password = findViewById(R.id.et_login_pwd);
        clear_text1 = findViewById(R.id.clear_text1);
        clear_text2 = findViewById(R.id.clear_text2);
        btn_find = findViewById(R.id.btn_find);
        btn_register =findViewById(R.id.btn_register);
        btn_phone =findViewById(R.id.btn_phone);
        flag=0;
        /*调用清楚文本框的方法*/
        EditTextUtils.clearButtonListener(edit_username,clear_text1);
        EditTextUtils.clearButtonListener(edit_password,clear_text2);

        /*验证码登录监听器*/
        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,phoneLogin.class);
                startActivity(intent);
            }
        });

        /*注册监听器*/
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,register.class);
                startActivity(intent);
            }
        });

        /*忘记监听器*/
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,alter.class);
                startActivity(intent);
            }
        });
        /*登录监听器*/
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    user1 = edit_username.getText().toString();
                    pwd1 = edit_password.getText().toString();
                    new Thread(new Runnable() {
                    @Override
                    public void run() {
                            try {
                                Class.forName(Constant.FORNAME);
                                Connection cn= DriverManager.getConnection(Constant.MYSQLBASE_URL,"grxiaofang","Qq6611678@");
                                String sql="SELECT * from admin WHERE `username`= '"+user1+"'  AND `password`= '"+pwd1+"' ";
                                Statement st=(Statement)cn.createStatement();
                                ResultSet rs=st.executeQuery(sql);
                                while(rs.next()){
                                           user=rs.getString("username");
                                           pwd=rs.getString("password");
                                }
                                cn.close();
                                st.close();
                                rs.close();
                                /*判断账号密码是否正确*/
                                if (user1.equals(user)  &&  pwd1.equals(pwd)) {
                                    Intent intent =new Intent(MainActivity.this,bluetooth.class);
                                    startActivity(intent);
                                    /*finish();*/ /*注释finsh()就可以按返回回到这个界面*/
                                    flag = 1;
                                    Log.i("Mainactivity", String.valueOf("正确的"+flag));
                                } else {
                                    flag = 0;
                                    Log.i("Mainactivity", String.valueOf("错误的"+flag));
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                try {
                    sleep(1000);
                    if(flag == 1) {
                        Toast.makeText(MainActivity.this, "欢迎使用智能消防栓系统！", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "请检查您的账户密码！", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("Mainactivity", String.valueOf("外部的"+flag));
            }
        });


    }
}




