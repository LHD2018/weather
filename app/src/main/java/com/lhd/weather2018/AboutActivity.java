package com.lhd.weather2018;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView updateLog;
    private TextView updateApp;
    private TextView openGithub;
    private TextView showStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        updateLog=findViewById(R.id.update_log);
        updateApp=findViewById(R.id.update_app);
        openGithub=findViewById(R.id.open_github);
        showStatement=findViewById(R.id.statement);
        ImageView back=findViewById(R.id.back);
        updateLog.setOnClickListener(this);
        updateApp.setOnClickListener(this);
        openGithub.setOnClickListener(this);
        showStatement.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update_log:
                AlertDialog.Builder updateDialog=new AlertDialog.Builder(AboutActivity.this);
                updateDialog.setTitle("更新日志");
                updateDialog.setMessage("第一个版本");
                updateDialog.setCancelable(true);
                updateDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                updateDialog.show();
                break;
            case R.id.update_app:
                Uri updateUri=Uri.parse("https://github.com/LHD2018/weather/releases");
                Intent intent1=new Intent(Intent.ACTION_VIEW,updateUri);
                startActivity(intent1);
                break;
            case R.id.open_github:
                Uri githubUri=Uri.parse("https://github.com/LHD2018/weather");
                Intent intent2=new Intent(Intent.ACTION_VIEW,githubUri);
                startActivity(intent2);
                break;
            case R.id.statement:
                AlertDialog.Builder statementDialog=new AlertDialog.Builder(AboutActivity.this);
                statementDialog.setTitle("免责声明");
                statementDialog.setMessage("采用和风接口");
                statementDialog.setCancelable(false);
                statementDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                statementDialog.show();
                break;
            case R.id.back:
                finish();
                break;
                default:
                    break;

        }
    }
}
