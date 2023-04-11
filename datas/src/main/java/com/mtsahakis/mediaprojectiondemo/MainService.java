package com.mtsahakis.mediaprojectiondemo;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainService extends Service implements View.OnTouchListener {

    private static final String TAG = MainService.class.getSimpleName();

    private WindowManager windowManager;

    private View floatyView;
    int c = 0;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        addOverlayView();
    }

    private void addOverlayView() {
        try {
            Log.e(TAG, "addOverlayView: JAva Code Workig");
            final LayoutParams params;
            int layoutParamsType;
            layoutParamsType = LayoutParams.TYPE_APPLICATION_OVERLAY;
            params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    layoutParamsType,
                    0,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.CENTER | Gravity.START;
            params.x = 0;
            params.y = 0;

            FrameLayout interceptorLayout = new FrameLayout(this) {

                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {

                    // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        // Check if the HOME button is pressed
                        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                            Log.v(TAG, "BACK Button Pressed");

                            // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                            return true;
                        }
                    }

                    // Otherwise don't intercept the event
                    return super.dispatchKeyEvent(event);
                }
            };

            LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

            if (inflater != null) {

                floatyView = inflater.inflate(R.layout.floating_view, interceptorLayout);
                ImageView img = floatyView.findViewById(R.id.img);
                Button request_btn = floatyView.findViewById(R.id.request_btn);
                TextView text = floatyView.findViewById(R.id.text);
                request_btn.setOnClickListener(v -> {
                    requestForUnlock(this, "Please Unlock My phone");

                });
          /*  img.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return false;
                }
            });*/
                windowManager.addView(floatyView, params);
            } else {
                Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SAW-example", e.getLocalizedMessage());
            Log.e("SAW-example", e.getMessage());

        }
    }

    private void requestForUnlock(Context context, String message) {
        try {

            VibrasInterface apiInterface = ApiClient.getClient().create(VibrasInterface.class);
            String parent_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id");
            String child_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id");
            HashMap<String, String> map = new HashMap<>();
            map.put("parent_id", parent_id);
            map.put("child_id", child_id);
            map.put("message", message);
            Call<ResponseBody> loginCall = apiInterface.request_remove_lockdown(map);
            loginCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        startChecking(context);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        } catch (Exception e) {

        }

    }

    private void startChecking(Context context) {
        Toast.makeText(context, R.string.checking_please_wait, Toast.LENGTH_SHORT).show();
        //showProgressMessage(context,"");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (floatyView != null) {
            windowManager.removeView(floatyView);
            floatyView = null;

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();

        Log.v(TAG, "onTouch...");

        // Kill service
        //

        return true;
    }


    private Dialog mDialog;
    private boolean isProgressDialogRunning = false;

    public void showProgressMessage(Context context, String msg) {
        try {
            if (isProgressDialogRunning) {
                hideProgressMessage();
            }
            isProgressDialogRunning = true;
            mDialog = new Dialog(context);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_loading);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         //   TextView textView = mDialog.findViewById(R.id.textView);
           // textView.setText(msg);
            WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            mDialog.getWindow().setAttributes(lp);
            mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hideProgressMessage() {
        isProgressDialogRunning = true;
        try {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
