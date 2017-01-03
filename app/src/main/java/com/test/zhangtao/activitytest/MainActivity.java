package com.test.zhangtao.activitytest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by zhangtao on 17/1/2.
 */

public class MainActivity extends Activity
{
    private TempControlView tempControlView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempControlView = (TempControlView) findViewById(R.id.temp_view);
        tempControlView.setTemp(10 , 70 , 20);
        tempControlView.setOnTempChangeListener(new TempControlView.OnTempChangeListener()
        {
            @Override
            public void change(int temp)
            {
                Toast.makeText(MainActivity.this , "当前温度：" + temp , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
