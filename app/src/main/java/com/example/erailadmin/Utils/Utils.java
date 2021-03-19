package com.example.erailadmin.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.erailadmin.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Utils {

    private ProgressDialog progressDialog;
    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public void startProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading....");
        progressDialog.setTitle(context.getResources().getString(R.string.app_name));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void stopProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public String getRealPathFromUri(Uri tempUri) {
        try (Cursor cursor = context.getContentResolver().query(tempUri, new String[]{"_data"}, null, null, null)) {
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    public Uri getPhotoFileUri(Activity activity) {
        Uri uri = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String fileName = "IMG_" + timeStamp + ".jpg";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = activity.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera/");
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        } else {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File photoFile = new File(storageDir, "/Camera/" + fileName);
            if (!Objects.requireNonNull(photoFile.getParentFile()).exists()) {
                photoFile.getParentFile().mkdir();
            }
            uri = FileProvider.getUriForFile(activity, "com.example.erailadmin.provider", photoFile);
        }
        return uri;
    }

    public void performCrop(Activity activity, Uri uri, boolean isFreeCrop, Fragment fragment) {
        try {
            Uri photoUri = getPhotoFileUri(activity);
            activity.grantUriPermission("com.android.camera", photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropIntent.setDataAndType(uri, "image/*");
            cropIntent.putExtra("crop", "true");
            if (!isFreeCrop) {
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 1000);
                cropIntent.putExtra("outputY", 1000);
            }
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            if (fragment == null) {
                activity.startActivityForResult(cropIntent, Const.CROP_IMAGE_INTENT);
            } else {
                fragment.startActivityForResult(cropIntent, Const.CROP_IMAGE_INTENT);
            }
        } catch (ActivityNotFoundException ex) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
