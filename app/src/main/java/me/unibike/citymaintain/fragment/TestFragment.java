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

import me.unibike.citymaintain.R;
import me.unibike.citymaintain.TestFeatureActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends DialogFragment {
    private EditText editText;
    private Button cancel,determine;
    private String value_ed;

    public TestFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view=getActivity().getLayoutInflater().inflate(R.layout.test,null);
        editText = (EditText) view.findViewById(R.id.ed_lock_id);
        cancel = (Button) view.findViewById(R.id.cancel);
        determine = (Button) view.findViewById(R.id.determine);

        loginEnable();

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
            Intent intent=new Intent(getActivity(),TestFeatureActivity.class);
            intent.putExtra("lock_id",value_ed);
            startActivity(intent);
            dismiss();
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
