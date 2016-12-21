package com.cml.lockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int REQUESTCODE = 0;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取设备管理服务
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, AdminReceiver.class);

        /**
         * 思路:
         *  1.判断是否有权限   有:锁屏后finish    没有:请求权限
         *  2.在请求权限的回调中onActivityResult中判断是否有权限 有:锁屏后finish 没有:直接finish
         */
        if(mDevicePolicyManager.isAdminActive(mComponentName)){
            lock();
        }else {
            requestDevicePermission();
        }

        setContentView(R.layout.activity_main);
    }


    private void requestDevicePermission() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,getString(R.string.activeAfterUse));
        startActivityForResult(intent,REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE && resultCode == RESULT_OK){
            lock();
        }else {
            Toast.makeText(this,getString(R.string.noPermission),Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void lock(){
        mDevicePolicyManager.lockNow();
        finish();
    }
}
