package com.example.erailadmin.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.erailadmin.Model.CommonModel;
import com.example.erailadmin.R;
import com.example.erailadmin.RestApi.ApiClientClass;
import com.example.erailadmin.RestApi.ApiInterface;
import com.example.erailadmin.Utils.Connectivity;
import com.example.erailadmin.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCollageFragment extends Fragment {

    private View mFragmentView;
    private Activity activity;
    private Utils utils;
    private ApiInterface apiInterface;
    private EditText et_name, et_mobile, et_password, et_address;
    private Button btn_add;
    private String name, mobile, password, address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_add_collage, null);
            init(mFragmentView);
        }
        return mFragmentView;
    }

    public void init(View view) {

        activity = getActivity();
        utils = new Utils(activity);
        apiInterface = ApiClientClass.getClient().create(ApiInterface.class);

        btn_add = view.findViewById(R.id.btn_add);
        et_name = view.findViewById(R.id.et_name);
        et_mobile = view.findViewById(R.id.et_mobile);
        et_password = view.findViewById(R.id.et_password);
        et_address = view.findViewById(R.id.et_address);

        btn_add.setOnClickListener(v -> {

            if (Connectivity.getInstance(activity).isOnline()) {

                name = et_name.getText().toString().trim();
                mobile = et_mobile.getText().toString().trim();
                address = et_address.getText().toString().trim();
                password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    et_name.setError("Please Enter Name");
                    et_name.requestFocus();

                } else if (TextUtils.isEmpty(mobile)) {
                    et_mobile.setError("Please Enter Mobile");
                    et_mobile.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    et_password.setError("Please Enter Password");
                    et_password.requestFocus();

                } else if (TextUtils.isEmpty(address)) {
                    et_address.setError("Please Enter Address");
                    et_address.requestFocus();

                } else {
                    if (Connectivity.getInstance(activity).isOnline()) {
                        AddCollage(name, mobile, password, address);
                    } else {
                        Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddCollage(String name, String contact, String password, String address) {

        utils.startProgressDialog();

        apiInterface.addCollage(name, contact, password, address).enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {

                utils.stopProgressDialog();

                assert response.body() != null;
                if (response.body().getResult().equals("200")) {
                    Toast.makeText(activity, "Successfully Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                utils.stopProgressDialog();
                call.cancel();
            }
        });
    }
}
