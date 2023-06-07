package com.mtsahakis.mediaprojectiondemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.ContentValues.TAG;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
public class ScreenCaptureService extends Service {

    private static final String TAG = "ScreenCaptureService";
    private static final String RESULT_CODE = "RESULT_CODE";
    private static final String DATA = "DATA";
    private static final String ACTION = "ACTION";
    private static final String START = "START";
    private static final String STOP = "STOP";
    private static final String SCREENCAP_NAME = "screencap";
    FirebaseStorage storage;
    StorageReference storageReference;
    private static int IMAGES_PRODUCED;
    private MediaProjection mMediaProjection;
    private String mStoreDir;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;

    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, STOP);
        return intent;
    }

    private static boolean isStartCommand(Intent intent) {
        return intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
    }

    private static boolean isStopCommand(Intent intent) {
        return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP);
    }

    private static int getVirtualDisplayFlags() {
        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {

            FileOutputStream fos = null;
            Bitmap bitmap = null;
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    // write bitmap to a file
                    fos = new FileOutputStream(mStoreDir + "/myscreen_" + IMAGES_PRODUCED + ".png");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                    IMAGES_PRODUCED++;
                //    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    //String imagee = mStoreDir + "/myscreen_" + IMAGES_PRODUCED + ".png";
                  //  Log.e(TAG, "onImageAvailable: imageeimagee-- "+imagee );
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                    String imagess = getRealPathFromURI(tempUri);
                    uploadImage(tempUri);



                    Thread.sleep(1000*120);
                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {


                    bitmap.recycle();
                }

            }
        }
    }
    // UploadImage method
    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            String request = SharedPreferenceUtility.getInstance(getApplicationContext())
                    .getString("request");
            if (request.equalsIgnoreCase("1")){
                SharedPreferenceUtility.getInstance(getApplicationContext()).putString("request","0");
                String parent_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id");
                String child_id =  SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id");
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                StorageReference ref
                        = storageReference
                        .child("image/"+parent_id+"/"+child_id+"/"+"Requested/" + UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                taskSnapshot -> {
                                    Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uri.isComplete());
                                    Uri url = uri.getResult();
                                    Log.e("FBApp1 URL ", url.toString());
                                    String sendImg = url.toString();
                                    if (!sendImg.equals("")) {
                                        updateCoverPhoto2(sendImg);
                                    }

                                })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // Error, Image not uploaded
                                Toast
                                        .makeText(getApplicationContext(),
                                                "Failed " + e.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .addOnProgressListener(taskSnapshot -> {


                        });
            }else {
                String parent_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id");
                String child_id =  SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id");
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                StorageReference ref
                        = storageReference
                        .child("image/"+parent_id+"/"+child_id+"/" + UUID.randomUUID().toString());
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                taskSnapshot -> {
                                    Toast.makeText(getApplicationContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                    while(!uri.isComplete());
                                    Uri url = uri.getResult();
                                    Log.e("FBApp1 URL ", url.toString());
                                    String sendImg = url.toString();
                                    if (!sendImg.equals("")) {
                                        updateCoverPhoto(sendImg);
                                    }

                                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                // Error, Image not uploaded
                                Toast
                                        .makeText(getApplicationContext(),
                                                "Failed " + e.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .addOnProgressListener(taskSnapshot -> {


                        });
            }



        }
    }

    private void updateCoverPhoto2(String sendImg) {
        try {
            VibrasInterface apiInterface = ApiClient.getClient().create(VibrasInterface.class);
            String parent_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id");
            String child_id =  SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id");
          /*  MultipartBody.Part filePart;
            if (!str_image_path.equalsIgnoreCase("")) {
                File file = saveBitmapToFile(new File(str_image_path));
                if (file != null) {
                    filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                } else {
                    filePart = null;
                }
            } else {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
                filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
            }*/
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), parent_id);
            RequestBody chld_id = RequestBody.create(MediaType.parse("text/plain"), child_id);
            RequestBody image = RequestBody.create(MediaType.parse("text/plain"), sendImg);

            Call<ResponseBody> loginCall = apiInterface.take_child_screenshot(userId, chld_id, image);
            loginCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }catch (Exception e){

        }
    }
    public void updateCoverPhoto(String str_image_path )
    {
        try {
            VibrasInterface apiInterface = ApiClient.getClient().create(VibrasInterface.class);
            String parent_id = SharedPreferenceUtility.getInstance(getApplicationContext()).getString("parent_id");
            String child_id =  SharedPreferenceUtility.getInstance(getApplicationContext()).getString("child_id");
          /*  MultipartBody.Part filePart;
            if (!str_image_path.equalsIgnoreCase("")) {
                File file = saveBitmapToFile(new File(str_image_path));
                if (file != null) {
                    filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                } else {
                    filePart = null;
                }
            } else {
                RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
                filePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
            }*/
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), parent_id);
            RequestBody chld_id = RequestBody.create(MediaType.parse("text/plain"), child_id);
            RequestBody image = RequestBody.create(MediaType.parse("text/plain"), str_image_path);

            Call<ResponseBody> loginCall = apiInterface.uploadSelfie(userId, chld_id, image);
            loginCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();
                }
            });
        }catch (Exception e){

        }
    }
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }
    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e(TAG, "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create store dir
        File externalFilesDir = getCacheDir();
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
            File storeDirectory = new File(mStoreDir);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                    stopSelf();
                }
            }
        } else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
            stopSelf();
        }

        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStartCommand(intent)) {
            // create notification
            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
            startForeground(notification.first, notification.second);
            // start projection
            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(DATA);
            Log.e(TAG, "onStartCommand: data  "+ resultCode);
            Log.e(TAG, "onStartCommand: data"+ data);
            startProjection(resultCode, data);
        } else if (isStopCommand(intent)) {
            stopProjection();
            stopSelf();
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startProjection(int resultCode, Intent data) {
        MediaProjectionManager mpManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection == null) {
            mMediaProjection = mpManager.getMediaProjection(resultCode, data);
            if (mMediaProjection != null) {
                // display metrics
                mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                mDisplay = windowManager.getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();
                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }
                // register media projection stop callback
                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMediaProjection != null) {
                        mMediaProjection.stop();
                    }
                }
            });
        }
    }

    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {
        // get width and height
        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }
}