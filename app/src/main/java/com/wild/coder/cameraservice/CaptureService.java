package com.wild.coder.cameraservice;

import android.app.Service;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wildcoder on 21/11/16.
 */

public abstract class CaptureService extends Service {

    public abstract void onCaptured(File f);

    @SuppressWarnings("deprecation")

    protected void takePhoto() {
        final SurfaceView preview = new SurfaceView(this);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                Camera camera = null;
                try {
                    camera = Camera.open();
                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    camera.startPreview();
                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            savePicture(data);
                            camera.release();
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

    }

    protected void takePic() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = 1;
        params.height = 1;
        params.x = 0;
        params.y = 0;
        SurfaceView sv = new SurfaceView(this);

        windowManager.addView(sv, params);
        SurfaceHolder sHolder = sv.getHolder();
        sHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                Camera camera = null;
                try {
                    camera = Camera.open();
                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    camera.startPreview();
                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            savePicture(data);
                            camera.release();
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        // tells Android that this surface will have its data constantly
        // replaced
        if (Build.VERSION.SDK_INT < 11)
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    protected void savePicture(byte[] data) {
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "ImageToSync");
            if (!f.exists())
                f.mkdir();
            File image = new File(f, System.currentTimeMillis() + ".jpg");
            if (!image.exists())
                image.createNewFile();
            FileOutputStream out = new FileOutputStream(image);
            out.write(data);
            out.flush();
            out.close();
            onCaptured(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

