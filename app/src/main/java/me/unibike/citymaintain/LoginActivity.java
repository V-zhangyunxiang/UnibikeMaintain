package me.unibike.citymaintain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.unibike.citymaintain.model.LoginModel;
import me.unibike.citymaintain.tool.LoginService;
import me.unibike.citymaintain.util.ToastHelper;
import me.unibike.network.UniNetwork;


/**
 * Created by VULCAN on 2017/6/24.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText login_username_ed;
    private EditText login_pwd_ed;
    private Button activity_login_btn;
    private String value_username, value_pwd;
    private SharedPreferences.Editor ed;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        //自动登录
        if (!sp.getString("name", "").equals("") && !sp.getString("pwd", "").equals("")) {
            login_username_ed.setText(sp.getString("name", ""));
            login_pwd_ed.setText(sp.getString("pwd", ""));
            loginEnable();
            login();
        }
        //监听用户名输入框
        login_username_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //监听密码输入框
        login_pwd_ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //点击登录
        activity_login_btn.setOnClickListener(v -> login());
    }

    //初始化控件
    public void init() {
        login_username_ed = (EditText) findViewById(R.id.login_username_ed);
        login_pwd_ed = (EditText) findViewById(R.id.login_pwd_ed);
        activity_login_btn = (Button) findViewById(R.id.activity_login_btn);
        sp = UniBikeCityMaintainApp.get(getApplicationContext()).getSPInstance();
        ed =sp.edit();
    }

    //登录按钮能否被点击
    public void loginEnable() {
        value_username = login_username_ed.getText().toString();
        value_pwd = login_pwd_ed.getText().toString();
        if (value_username.length() != 0 && value_pwd.length() != 0) {
            activity_login_btn.setEnabled(true);
        } else {
            activity_login_btn.setEnabled(false);
        }
    }

    public void login() {
        ProgressDialog pd = ProgressDialog.show(LoginActivity.this, "正在登录", "请稍后...", false, true);
        LoginService loginService = UniBikeCityMaintainApp.get(getApplicationContext()).provideRetrofit().create(LoginService.class);
        Observable<LoginModel> observable = loginService.getUserMessage(value_username, value_pwd);
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<LoginModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        pd.show();
                    }

                    @Override
                    public void onNext(@NonNull LoginModel loginModel) {
                        if (loginModel.getCode() == 0) {
                            ed.putString("token", loginModel.getPayload().getToken());
                            ed.apply();
                            if (loginModel.getPayload().getToken() != null) {
                                UniNetwork.updateToken(loginModel.getPayload().getToken());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("name", loginModel.getPayload().getUni_account().getName());
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            ToastHelper.showToast(UniBikeCityMaintainApp.get(LoginActivity.this),"用户名或密码错误");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //ToastHelper.showToast(UniBikeCityMaintainApp.get(LoginActivity.this),"用户名或密码错误");
                    }

                    @Override
                    public void onComplete() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        ed.putString("name", value_username);
                        ed.putString("pwd", value_pwd);
                        ed.apply();
                    }
                });
    }
}
