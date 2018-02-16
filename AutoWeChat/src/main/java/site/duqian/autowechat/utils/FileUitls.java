package site.duqian.autowechat.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by duqian on 2017/2/20.
 */

public class FileUitls {

    private static final String TAG = FileUitls.class.getSimpleName();

    public static void saveBitmap2File(Bitmap bm, String fileName)  {
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dirFile = new File(path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
            File myCaptureFile = new File(path + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (Exception e){
            LogUtils.debug(TAG,"error "+e.toString());
        }
    }

    public static String makeVideoThumbFile(String videoFile) {
        File f = new File(videoFile + ".jpg");
        if (f.exists()) {
            return f.toString();
        }
        Bitmap tmp = getVideoThumbnail(videoFile);
        if (tmp != null) {
            File thumbfile = saveImage(tmp, videoFile + ".jpg");
            if (thumbfile != null) {
                return thumbfile.toString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static File saveImage(Bitmap bmp, String fileName) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        if (fileName == null || fileName.equals("")) {
            fileName = System.currentTimeMillis() + ".jpg";
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();

            if (bitmap.getWidth() > 320 || bitmap.getHeight() > 200) {
                float scale = 1;
                if (bitmap.getWidth() > 320) {
                    scale = (float) 320 / (float) bitmap.getWidth();
                } else {
                    scale = (float) 200 / (float) bitmap.getHeight();
                }
                Matrix matrix = new Matrix();

                matrix.postScale(scale, scale);

                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap = resizeBmp;

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
