package com.panjq.opencv.alg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.panjq.opencv.opencvdemo.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.opencv.core.Mat;
import org.opencv.android.Utils;

/**
 * Created by panjq1 on 2017/10/22.
 */

public class ImagePro {
    private Context context;
    private static final String    TAG = "ImagePro:";
    static {
       // System.loadLibrary("imagePro-lib");
        //System.loadLibrary("opencv_java3");
    }
    public native int[] jniImagePro1(int[] pixels, int w, int h);
    public native ImageData jniImagePro2(ImageData image_data);
    public native void jniImagePro3(long matAddrSrcImage, long matAddrDestImage);
    public ImagePro(Context context){
        this.context = context;
    }
    /**
     *
     */
    public Bitmap ImageProcess1(Bitmap origImage) {
        int w = origImage.getWidth();
        int h = origImage.getHeight();
        int[] pixels = new int[w * h];
        origImage.getPixels(pixels, 0, w, 0, 0, w, h);
        int[] image= jniImagePro1(pixels,w,h);
        Log.i(TAG, "jniImagePro1 called successfully");
        //最后将返回的int数组转为bitmap类型。
        Bitmap desImage=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        //faceall为返回的int数组
        desImage.setPixels(image,0,w,0,0,w,h);
        return desImage;
    }

    /**
     *
     */
    public Bitmap ImageProcess2(Bitmap origImage) {
       ImageData imageData=new ImageData(origImage);
//        ImageData imageData=new ImageData();
//        imageData.getImageData(origImage);
        Log.i(TAG, "input image size:"+imageData.w+","+imageData.h);
        ImageData out_image= jniImagePro2(imageData);
        Log.i(TAG, "return image size:"+out_image.w+","+out_image.h);
        Bitmap desImage=out_image.getBitmap();
        //saveImage(desImage,"desImage.jpg");
        Log.i(TAG, "jniImagePro2 called successfully");
        return desImage;
    }



    /**
     *
     */
    public Bitmap ImageProcess3(Bitmap origImage) {
        Log.i(TAG, "called JNI:jniImagePro3 ");
        int w=origImage.getWidth();
        int h=origImage.getHeight();
        Mat origMat = new Mat();
        Mat destMat = new Mat();
        Utils.bitmapToMat(origImage, origMat);
        jniImagePro3(origMat.getNativeObjAddr(), destMat.getNativeObjAddr());
        Bitmap bitImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(destMat, bitImage);
        Log.i(TAG, "jniImagePro3 called successfully");
        return bitImage;
    }


    //图片保存
    public void saveBitmap(Bitmap b){
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/3DLUT/";
        String path =Environment.getExternalStorageDirectory().getPath()+"/DCIM/3DLUT/";

        File folder=new File(path);
        if(!folder.exists()&&!folder.mkdirs()){
            LogUtil.e("无法保存图片");
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName=path+ dataTake +".jpg";
        LogUtil.e("jpegName = "+jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + jpegName)));
    }

}
