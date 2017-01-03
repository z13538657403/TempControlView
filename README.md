# TempControlView
一个可拖拽的动态刻度盘

# 控件简介

该自定义控件是一个可以滑动改变温度值的表盘控件。

# 用法简介

（1）在布局中加入该控件

<com.test.zhangtao.activitytest.TempControlView
    android:id="@+id/temp_view"
    android:layout_width="350dp"
    android:layout_height="350dp"
    android:layout_marginTop="40dp"
    android:layout_centerInParent="true"
    android:background="@android:color/white"/>
    
（2）在代码中设置最低温度，最高温度以及当前温度，并为该控件设置事件监听
tempControlView.setTemp(10 , 70 , 20);
tempControlView.setOnTempChangeListener(new TempControlView.OnTempChangeListener()
{
     @Override
      public void change(int temp)
      {
             Toast.makeText(MainActivity.this , "当前温度：" + temp , Toast.LENGTH_SHORT).show();
      }
});

# 运行截图

