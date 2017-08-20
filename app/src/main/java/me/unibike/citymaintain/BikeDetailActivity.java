package me.unibike.citymaintain;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.unibike.citymaintain.fragment.BikeStatusFragment;
import me.unibike.citymaintain.model.DetailModel;

/**
 * Created by VULCAN on 2017/7/10.
 */

public class BikeDetailActivity extends AppCompatActivity implements BikeStatusFragment.StatusResultListener{
    TextView pwd_tv_one,pwd_tv_two,pwd_tv_three,pwd_tv_four,value_number_tv,value_status_tv,value_battery_tv,
             value_order_number,value_name_user,value_phone_number,value_start_time,value_end_time,value_ride_time,value_ride_cost;
    Button change_bike_status,control_bike;
    DetailModel model;
    ImageView img_back;
    String status;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bike_details);
        init();
        model = getIntent().getParcelableExtra("detail");
        if(model.getPayload().getPassword()!=null) {
            char[] s = model.getPayload().getPassword().toCharArray();
            for (int i = 0; i < s.length; i++) {
                pwd_tv_one.setText(s[0] + "");
                pwd_tv_two.setText(s[1] + "");
                pwd_tv_three.setText(s[2] + "");
                pwd_tv_four.setText(s[3] + "");
            }
        }
        if(model.getPayload().getId()!=0) {
            value_number_tv.setText("\t" + model.getPayload().getId());
        }
        if(model.getPayload().getBike_status()!=null) {
            judgeStatus(model.getPayload().getBike_status());
            value_status_tv.setText("\t"+status);
        }
        if(model.getPayload().getLast_ride()!=null) {
            value_order_number.setText("\t" + model.getPayload().getLast_ride().getOrder_id());
            String startTime = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date((long) model.getPayload().getLast_ride().getCreated_at() * 1000));
            value_start_time.setText("\t" + startTime);
            String endTime = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date((long) model.getPayload().getLast_ride().getFinished_at() * 1000));
            value_end_time.setText("\t" + endTime);
            value_ride_time.setText("\t" + model.getPayload().getLast_ride().getUsed_time() / 60);
            value_ride_cost.setText("\t" + model.getPayload().getLast_ride().getCost() / 100 + "." + model.getPayload().getLast_ride().getCost() % 100 / 10);
        }
       if (model.getPayload().getLock_info()==null){
            value_battery_tv.setText("\t"+"null");
            control_bike.setVisibility(View.GONE);
        }else{
            value_battery_tv.setText("\t"+model.getPayload().getLock_info().getVoltage());
            control_bike.setOnClickListener(v -> {
               //Lock_info非空时,获取锁的ID,传递锁ID到操作车辆界面
                Intent intent = new Intent(this, TestFeatureActivity.class);
                intent.putExtra("lock_id", model.getPayload().getLock_info().getId());
                startActivity(intent);
            });
        }
        //返回
        img_back.setOnClickListener(v -> finish());
        //打开更改状态对话框
        change_bike_status.setOnClickListener(v -> BikeStatusFragment.newInstance(model.getPayload().getId(),model.getPayload().getBike_status()).show(getSupportFragmentManager(), "BikeStatusFragment"));
    }
    private void init(){
        pwd_tv_one= (TextView) findViewById(R.id.pwd_tv_one);
        pwd_tv_two = (TextView) findViewById(R.id.pwd_tv_two);
        pwd_tv_three = (TextView) findViewById(R.id.pwd_tv_three);
        pwd_tv_four = (TextView) findViewById(R.id.pwd_tv_four);
        value_number_tv = (TextView) findViewById(R.id.value_number_tv);
        value_status_tv = (TextView) findViewById(R.id.value_status_tv);
        value_battery_tv = (TextView) findViewById(R.id.value_battery_tv);
        value_order_number = (TextView) findViewById(R.id.value_order_number);
        value_name_user = (TextView) findViewById(R.id.value_name_user);
        value_phone_number = (TextView) findViewById(R.id.value_phone_number);
        value_start_time = (TextView) findViewById(R.id.value_start_time);
        value_end_time = (TextView) findViewById(R.id.value_end_time);
        value_ride_cost = (TextView) findViewById(R.id.value_ride_cost);
        value_ride_time = (TextView) findViewById(R.id.value_ride_time);
        change_bike_status = (Button) findViewById(R.id.change_bike_status);
        control_bike = (Button) findViewById(R.id.control_bike);
        img_back = (ImageView) findViewById(R.id.img_back);
    }

    @Override
    public void onStatusClick(String s) {
        judgeStatus(s);
        value_status_tv.setText("\t"+status);
    }
    //改变状态为中文
    private void judgeStatus(String s){
        if(s.equals("normal")){
            status="正常";
        }else if(s.equals("silent")){
            status="沉默";
        }else if(s.equals("intervened")){
            status="待干预";
        }else if(s.equals("saved")){
            status="暂留";
        }else if(s.equals("broken")){
            status="损坏";
        }
    }
}
