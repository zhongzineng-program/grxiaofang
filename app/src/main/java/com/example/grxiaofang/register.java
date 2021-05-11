package com.example.grxiaofang;
/*注册类*/
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class register extends AppCompatActivity {

    public Button bt_register;
    public ImageButton clear_text1,clear_text2,clear_text3;
    public EditText edit_username; //用户名
    public EditText edit_password1; //密码
    public EditText edit_password2; //再一次输入密码

    public String user; //用来存编辑框的转换的字符串
    public String pwd1; //用来存编辑框的转换的字符串
    public String pwd2; //用来存编辑框的转换的字符串
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        edit_username = findViewById(R.id.et_register_username);
        edit_password1 = findViewById(R.id.et_register_pwd1);
        edit_password2 = findViewById(R.id.et_register_pwd2);
        clear_text1 = findViewById(R.id.clear_text1);   //绑定xml的按钮
        clear_text2 = findViewById(R.id.clear_text2);   //绑定xml的按钮
        clear_text3 = findViewById(R.id.clear_text3);   //绑定xml的按钮
        bt_register =findViewById(R.id.bt_register);    //绑定xml的按钮

            /*调用清楚文本框的方法*/
        EditTextUtils.clearButtonListener(edit_username,clear_text1);
        EditTextUtils.clearButtonListener(edit_password1,clear_text2);
        EditTextUtils.clearButtonListener(edit_password2,clear_text3);


        /*注册监听器*/
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = edit_username.getText().toString();  //把编辑框的数据转成字符串到这个变量
                pwd1 = edit_password1.getText().toString();  //把编辑框的数据转成字符串到这个变量
                pwd2 = edit_password2.getText().toString();  //把编辑框的数据转成字符串到这个变量

                if (pwd1.equals("")||pwd2.equals("")){	//判断两次密码是否为空
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                }else if(pwd1.equals(pwd2)){ //判断正确写入数据库
                    /*把Editext里面的密码上传到数据库*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Class.forName(Constant.FORNAME);
                                Connection cn= DriverManager.getConnection(Constant.MYSQLBASE_URL,"grxiaofang","Qq6611678@");
                                String sql="insert into admin(username,password) values('"+user+"','"+pwd1+"')";
                                Statement st=(Statement)cn.createStatement();
                                st.executeUpdate(sql);
                                cn.close();
                                st.close();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    Toast.makeText(getApplication(),"注册成功",Toast.LENGTH_SHORT).show();
                    //注册成功后进入提前写好的登录页面
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    //intent.putExtra(,);//可以填入用户信息，如ID等
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplication(),"密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
                }
            }
        });


/*登录监听器*/

/*        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = edit_username.getText().toString();  //把编辑框的数据转成字符串到这个变量
                pwd1 = edit_password1.getText().toString();  //把编辑框的数据转成字符串到这个变量
                pwd2 = edit_password2.getText().toString();  //把编辑框的数据转成字符串到这个变量
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
*//*判断账号密码是否正确*//*


                            if (user1.equals(user)  &&  pwd1.equals(pwd)) {
                                Intent intent =new Intent(MainActivity.this,bluetooth.class);
                                startActivity(intent);
                                finish();
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
                    sleep(900);
                    if(flag == 1) {
                        Toast.makeText(MainActivity.this, "欢迎使用智能消防栓系统！", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "请检查您的账户密码！", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("Mainactivity", String.valueOf("外部的"+flag));
            }
        });*/


    }
}
