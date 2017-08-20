package me.unibike.citymaintain.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.unibike.citymaintain.R;
import me.unibike.citymaintain.UniBikeCityMaintainApp;
import me.unibike.citymaintain.model.ChangeStatusModel;
import me.unibike.citymaintain.tool.ChangeStatusService;
import me.unibike.citymaintain.util.ToastHelper;
import me.unibike.lbs.location.LocationHelper;

/**
 * Created by VULCAN on 2017/7/6.
 */

public class BikeStatusFragment extends DialogFragment {
    private Button status_determine;
    private RadioButton normal_btn,delay_btn;
    private RadioGroup testFeaBikeStatus;
    private String s="normal";
    private int bike_id;
    private LocationHelper mLocationHelper;
    String lnglat = null;
    TextView yunYing;
    public interface StatusResultListener {
        void onStatusClick(String s);
    }

    StatusResultListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (StatusResultListener) activity;
    }

    public static BikeStatusFragment newInstance(int bike_id,String bike_status) {
        BikeStatusFragment f = new BikeStatusFragment();
        Bundle b = new Bundle();
        b.putInt("bike_id", bike_id);
        b.putString("bike_status",bike_status);
        f.setArguments(b);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bike_status, null);
        //操作控件
        status_determine = (Button) view.findViewById(R.id.status_determine);
        testFeaBikeStatus = (RadioGroup) view.findViewById(R.id.testFeaBikeStatus);
        normal_btn= (RadioButton) view.findViewById(R.id.opera_normal);
        yunYing = (TextView) view.findViewById(R.id.yunYing);
        delay_btn = (RadioButton) view.findViewById(R.id.no_opera_delay);
        if (getArguments().getInt("bike_id")!=0) {
            bike_id = getArguments().getInt("bike_id");
        }
        if (getArguments().getString("bike_status")!=null && getArguments().getString("bike_status").equals("silent")) {
            normal_btn.setVisibility(View.GONE);
            yunYing.setVisibility(View.GONE);
            delay_btn.setChecked(true);
            s="saved";
        }
        //定位
        mLocationHelper = LocationHelper.getInstance(getActivity());
        mLocationHelper.start();
        lnglat = mLocationHelper.getFormatLocation();
        //监听拥护选择哪一个状态
        testFeaBikeStatus.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.opera_normal:
                    s = "normal";
                    break;
                case R.id.no_opera_delay:
                    s = "saved";
                    break;
                case R.id.no_opera_service:
                    s = "broken";
                    break;
            }
        });
        //得到状态值,调用后台，传递值到Activity,并关闭对话框.
        status_determine.setOnClickListener(v -> {
            //这里调用后台改变车的状态
            ChangeStatusService changeStatusService = UniBikeCityMaintainApp.get(getActivity()).provideRetrofit().create(ChangeStatusService.class);
            Observable<ChangeStatusModel> observable = changeStatusService.changeStatus(UniBikeCityMaintainApp.get(getActivity()).getSPInstance().getString("token", ""), bike_id, lnglat, s);
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Observer<ChangeStatusModel>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ChangeStatusModel changeStatusModel) {
                            if (changeStatusModel.getCode() == 0) {
                                ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"修改成功");
                                mListener.onStatusClick(changeStatusModel.getPayload().getCity_bike().getBike_status());
                            }else{
                                ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"状态未被更改");
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            ToastHelper.showToast(UniBikeCityMaintainApp.get(getActivity()),"发生异常，修改失败");
                        }

                        @Override
                        public void onComplete() {
                            dismiss();
                        }
                    });
        });
        builder.setView(view);
        return builder.create();
    }
}
