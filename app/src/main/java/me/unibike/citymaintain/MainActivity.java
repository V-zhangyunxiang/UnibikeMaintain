package me.unibike.citymaintain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import me.unibike.citymaintain.fragment.WorkBenchFragment;
import me.unibike.citymaintain.util.ToastHelper;

public class MainActivity extends FragmentActivity {
    private RadioGroup activity_rg;
    private FrameLayout frameLayout;
    private FragmentStatePagerAdapter statePagerAdapter;
    private String name;
    private long clickTime = 0; // 第一次点击的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getStringExtra("name") != null) {
            name = getIntent().getStringExtra("name");
        }
        initFragmentAdapter();
        initFragment();
        //System.out.println("sha1-----"+sHA1(this));

    }

    /**
     * 初始化fragment的适配器
     */
    private void initFragmentAdapter() {
        frameLayout = (FrameLayout) findViewById(R.id.activity_main_frame);
        statePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            public int getCount() {
                return 1;
            }

            public Fragment getItem(int arg0) {
                switch (arg0) {
                    case 0:
                        return WorkBenchFragment.newInstance(name);
//                    case 1:
//                        return new MyFragment();

                    default:
                        return null;
                }
            }
        };
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {

        //首次进入默认设置显示第一个fragment

        Fragment fragment = (Fragment) statePagerAdapter.instantiateItem(frameLayout, 0);
        statePagerAdapter.setPrimaryItem(frameLayout, 0, fragment);
        statePagerAdapter.finishUpdate(frameLayout);

        activity_rg = (RadioGroup) findViewById(R.id.activity_main_radioGroup);
        activity_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //index 为将要显示的fragment的下标
                int index = 0;
                switch (checkedId) {
                    case R.id.radioButton_workBench:
                        index = 0;
                        break;
//                    case R.id.radioButton_My:
//                        index = 1;
//                        break;
                }
                Fragment fragment = (Fragment) statePagerAdapter.instantiateItem(frameLayout, index);
                //告诉适配器，你需要显示哪个碎片
                statePagerAdapter.setPrimaryItem(frameLayout, 0, fragment);
                //刷新  需要在fragment里面重新setMenuVisibility
                statePagerAdapter.finishUpdate(frameLayout);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor ed = UniBikeCityMaintainApp.get(getApplicationContext()).getSPInstance().edit();
        ed.remove("token");
        ed.apply();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        exit();
//    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            ToastHelper.showToast(UniBikeCityMaintainApp.get(this),"再按一次退出程序");
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
               exit();
            return true;
        } else {
            // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result=hexString.toString();
            return result.substring(0, result.length()-1);
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
