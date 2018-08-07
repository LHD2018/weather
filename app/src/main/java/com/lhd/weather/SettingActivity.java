package com.lhd.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lhd.weather.util.Utility;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText userId;
    private EditText userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        userId=findViewById(R.id.user_id);
        userKey=findViewById(R.id.user_key);
        Button setKey=findViewById(R.id.set_key);
        Button resetKey=findViewById(R.id.reset_key);
        ImageView back=findViewById(R.id.back);
        setKey.setOnClickListener(this);
        resetKey.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_key:
                String apiKey=userId.getText().toString()+"/"+userKey.getText().toString();
                if (apiKey.equals("/")){
                    Toast.makeText(SettingActivity.this,"请输入id和key",Toast.LENGTH_SHORT).show();
                }else{
                    Utility.setApiKey(SettingActivity.this,apiKey);
                    Toast.makeText(SettingActivity.this,"请重启应用",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset_key:
                Utility.setApiKey(SettingActivity.this,"HE1807171032311823/24a2d899122b4526b7299924f133c599");
                Toast.makeText(SettingActivity.this,"请重启应用",Toast.LENGTH_SHORT).show();
                break;
            case R.id.back:
                finish();
                break;
                default:
                    break;
        }
    }
}
