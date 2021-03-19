package com.example.erailadmin.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.erailadmin.Model.CommonModel;
import com.example.erailadmin.R;
import com.example.erailadmin.RestApi.ApiClientClass;
import com.example.erailadmin.RestApi.ApiInterface;
import com.example.erailadmin.Utils.Connectivity;
import com.example.erailadmin.Utils.Const;
import com.example.erailadmin.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddFoodShopFragment extends Fragment {

    private View mFragmentView;
    private Activity activity;
    private Utils utils;
    private ApiInterface apiInterface;
    private EditText et_name, et_mobile, et_password, et_address, et_station;
    private CircleImageView img_icon;
    private Button btn_add;
    private String name, mobile, password, address, station;
    private String selectedPhoto = "";
    private Uri imageUri;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_add_food_shop, null);
            init(mFragmentView);
        }
        return mFragmentView;
    }

    public void init(View view) {

        activity = getActivity();
        utils = new Utils(activity);
        apiInterface = ApiClientClass.getClient().create(ApiInterface.class);

        img_icon = view.findViewById(R.id.img_icon);
        btn_add = view.findViewById(R.id.btn_add);
        et_name = view.findViewById(R.id.et_name);
        et_mobile = view.findViewById(R.id.et_mobile);
        et_password = view.findViewById(R.id.et_password);
        et_address = view.findViewById(R.id.et_address);
        et_station = view.findViewById(R.id.et_station);

        img_icon.setOnClickListener(v -> {
            if (isPermissionGranted()) {
                openImagePicker();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                }
            }
        });

        btn_add.setOnClickListener(v -> {

            if (Connectivity.getInstance(activity).isOnline()) {

                name = et_name.getText().toString().trim();
                mobile = et_mobile.getText().toString().trim();
                address = et_address.getText().toString().trim();
                station = et_station.getText().toString().trim();
                password = et_password.getText().toString().trim();

                RequestBody shopImage = null;
                if (!selectedPhoto.equals("")) {
                    File file = new File(selectedPhoto);
                    shopImage = RequestBody.create(file, MediaType.parse("image/*"));
                } else {
                    Toast.makeText(activity, "Select Shop Image", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                } else if (TextUtils.isEmpty(station)) {
                    et_station.setError("Please Enter Station Name");
                    et_station.requestFocus();

                } else {

                    RequestBody shopName = RequestBody.create(name, MediaType.parse("text/plain"));
                    RequestBody shopMobile = RequestBody.create(mobile, MediaType.parse("text/plain"));
                    RequestBody shopAddress = RequestBody.create(address, MediaType.parse("text/plain"));
                    RequestBody shopPassword = RequestBody.create(password, MediaType.parse("text/plain"));
                    RequestBody shopStation = RequestBody.create(station, MediaType.parse("text/plain"));

                    if (Connectivity.getInstance(activity).isOnline()) {
                        AddFoodShop(shopName, shopMobile, shopPassword, shopAddress, shopStation, shopImage);
                    } else {
                        Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AddFoodShop(RequestBody name, RequestBody contact, RequestBody password, RequestBody address, RequestBody station, RequestBody photo) {

        utils.startProgressDialog();

        apiInterface.addFoodShop(name, contact, password, address, station, photo).enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {

                utils.stopProgressDialog();

                assert response.body() != null;
                if (response.body().getResult().equals("200")) {
                    Toast.makeText(activity, "Successfully Added", Toast.LENGTH_SHORT).show();
                    clearDataFromUI();
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

    private void clearDataFromUI() {
        et_name.setText("");
        et_mobile.setText("");
        et_password.setText("");
        et_station.setText("");
        et_address.setText("");
        img_icon.setImageResource(R.drawable.restaurant);
        selectedPhoto = "";
    }

    private void openImagePicker() {

        TextView textView = new TextView(activity);
        textView.setText("Select Shop Image");
        textView.setPadding(40, 30, 40, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCustomTitle(textView);
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        dialogBuilder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Take Photo")) {
                imageUri = utils.getPhotoFileUri(activity);
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePicture, Const.CAPTURE_IMAGE_INTENT);
            } else if (options[which].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Const.CHOOSE_PHOTO_INTENT);
            } else if (options[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        dialogBuilder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Const.CAPTURE_IMAGE_INTENT:
                if (resultCode == RESULT_OK) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(imageUri);
                    activity.sendBroadcast(mediaScanIntent);
                    utils.performCrop(activity, imageUri, false, this);
                } else {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            case Const.CHOOSE_PHOTO_INTENT:
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    utils.performCrop(activity, imageUri, false, this);
                } else {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            case Const.CROP_IMAGE_INTENT:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    selectedPhoto = utils.getRealPathFromUri(imageUri);
                    bitmap = BitmapFactory.decodeFile(selectedPhoto);
                    img_icon.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            }
        }
    }
}
