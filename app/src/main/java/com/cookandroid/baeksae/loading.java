package com.cookandroid.baeksae;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

public class loading {
    private Context context;
    private Dialog dialog;

    public loading(Context context){this.context = context;}

    public void showDialog(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setDimAmount(0.4f);
        dialog.setCancelable(false);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();

    }
    public void closeDialog() {
        dialog.dismiss();
    }
}

