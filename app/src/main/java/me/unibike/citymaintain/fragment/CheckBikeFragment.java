package me.unibike.citymaintain.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.unibike.citymaintain.BikeDetailActivity;
import me.unibike.citymaintain.R;
import me.unibike.citymaintain.UniBikeCityMaintainApp;
import me.unibike.citymaintain.model.DetailModel;
import me.unibike.citymaintain.tool.DetailService;
import me.unibike.citymaintain.util.ToastHelper;
import me.unibike.lbs.location.LocationHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckBikeFragment extends DialogFragment{
    private EditText  editText;
    private Button cancel,determine;
    private String value_ed;
    private LocationHelper mLocationHelper;
    private String lnglat = null;
    public CheckBikeFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view=getActivity().getLayoutInflater().inflate(R.layout.code_check_car,null);
        editText = (EditText) view.findViewById(R.id.ed_bike_id);
        cancel = (Button) view.findViewById(R.id.cancel);
        determine = (Button) view.findViewById(R.id.determine);

        loginEnable();

        mLocationHelper = LocationHelper.getInstance(getActivity());
        mLocationHelper.start();
        lnglat = mLocationHelper.getFormatLocation();

        editText.addTextChangedListener(new TextWatcher() {
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
        cancel.setOnClickListener(v -> dismiss());

        determine.setOnClickListener(v -> {
            //拿到输入的车id,请求车辆详情
            DetailService detailService = UniBikeCityMaintainApp.get(getActivity()).provideRetrofit().create(DetailService.class);
            Observable<DetailModel> observable = detailService.detailMessage(UniBikeCityMaintainApp.get(getActivity()).getSPInstance().getString("token", ""), Integer.parseInt(value_ed),lnglat);
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Observer<DetailModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(@NonNull DetailModel detailModel) {
                            if (detailModel.getCode() == 0) {
                                Intent intent = new Intent(getActivity(), BikeDetailActivity.class);
                                intent.putExtra("detail", detailModel);
                                startActivity(intent);
                                dismiss();
                            }else{
                                ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"该编号车辆不存在");
                            }

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"请求数据异常");
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        });
        builder.setView(view);
        return builder.create();
    }

    public void loginEnable() {
        value_ed = editText.getText().toString().trim();
        if (value_ed.length() != 0) {
            determine.setEnabled(true);
        } else {
            determine.setEnabled(false);
        }
    }
}
