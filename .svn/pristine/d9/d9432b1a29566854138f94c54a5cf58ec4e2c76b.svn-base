package com.ppfuns.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.jakewharton.disklrucache.DiskLruCache;
import com.ppfuns.util.DownloadFiles;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.MD5Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;



/**
 * 网络图片下载到本地磁盘缓存实现，对比本地已下载的文件，只下载未下载的图片，
 * <p/>
 * 以URL为唯一标识，用第三方库DiskLruCache作为缓存
 * Created by FlyZebra on 2016/6/21.
 */
public class DiskCache implements IDiskCache {
    private final int max_size = 100 * 1024 * 1024;
    private DiskLruCache mDiskLruCache;
    private String savePath;
    private String TEMPFILE = ".TEMP";
    private Context context;

    public DiskCache init(Context context) {
        return this.init(context, max_size);
    }

    public DiskCache init(Context context, int max_size) {
        this.context = context;
        return this.init(context, max_size, getSavePath());
    }

    @Override
    public DiskCache init(Context context, int size, String cachePath) {
        if (context == null) {
            this.context = context;
        }
        try {
            savePath = cachePath;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(file, getAppVersion(context), 1, max_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Bitmap getBitmap(String imgUrl) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream is = null;
        try {
            snapShot = mDiskLruCache.get(MD5Utils.md5ForString(imgUrl));
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (snapShot != null) {
                    snapShot.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("<DiskCache>从磁盘缓存生成Bitmap对象，Bitmap=" + bitmap);
        return bitmap;
    }

    @Override
    public boolean saveBitmapFromBitmap(String url, Bitmap bitmap) {
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(MD5Utils.md5ForString(url));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("<DiskCache>保存Bitmap对像到缓存，是否成功" + flag);
        return flag;
    }

    /**
     * 为避免重复下载，考虑各种复杂的网络情况，
     * 先以临时文件名下载，下载完成后改为能正确取用的文件名
     */
    @Override
    public boolean saveBitmapFromImgurl(String imgUrl) {
        if (checkFileExist(imgUrl)) {
            LogUtils.i("<DiskCache>图片文件已经存在：" + imgUrl);
            return true;
        }
        LogUtils.i("<DiskCache>开始下载图片文件：" + imgUrl);
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            String key = MD5Utils.md5ForString(imgUrl);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key + TEMPFILE);
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            if (DownloadFiles.downloadImgUrlToStream(imgUrl, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
            File file = getFile(key + TEMPFILE);
            if (file.exists()) {
                LogUtils.i(imgUrl + ",文件下载完成，临时文件名为：" + file.getAbsolutePath());
                File saveFile = getFile(key);
                file.renameTo(saveFile);
                LogUtils.i(imgUrl + "文件改名为：" + saveFile.getAbsolutePath());
                context.deleteFile(key);
                flag = true;
            } else {
                flag = false;
            }
        } catch (IOException e) {
            flag = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public boolean delFileUrl(String url) {
        boolean flag = false;
        try {
            flag = mDiskLruCache.remove(MD5Utils.md5ForString(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean delFileKey(String key) {
        boolean flag = false;
        try {
            flag = mDiskLruCache.remove(MD5Utils.md5ForString(key));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public String getBitmapPath(String imgUrl) {
        String path = "file://" + savePath + File.separator + MD5Utils.md5ForString(imgUrl) + ".0";
        return path;
    }

    @Override
    public String getString(String key) {
        LogUtils.i("<DiskCache>读取磁盘缓存数据:" + key);
        String jsonStr = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream in = null;
        try {
            snapShot = mDiskLruCache.get(MD5Utils.md5ForString(key));
            if (snapShot != null) {
                in = snapShot.getInputStream(0);
                byte b[] = new byte[8 * 1024 * 1024];
                int len = 0;
                int temp = 0;          //所有读取的内容都使用temp接收
                while ((temp = in.read()) != -1) {    //当没有读取完时，继续读取
                    b[len] = (byte) temp;
                    len++;
                }
                jsonStr = new String(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (snapShot != null) {
                    snapShot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("<DiskCache>读取磁盘缓存数据:" + key + ",json=" + jsonStr);
        return jsonStr;
    }

    @Override
    public boolean saveString(String str, String key) {
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(MD5Utils.md5ForString(key));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            byte[] bytes = str.getBytes();
            outputStream.write(bytes);
            editor.commit();
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.i("<DiskCache>保存磁盘缓存数据key=" + key + ",成功=" + flag + ",jsonStr" + str);
        return flag;
    }

    @Override
    public boolean saveObj(Object object, String key) {
        boolean flag = false;
        OutputStream outputStream = null;

//        ByteArrayOutputStream bo = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(MD5Utils.md5ForString(key));
            if (editor == null) return false;
            outputStream = editor.newOutputStream(0);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            editor.commit();
            mDiskLruCache.flush();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    @Override
    public Object getObj(String key) {
        Object obj = null;
        DiskLruCache.Snapshot snapShot = null;
        InputStream in = null;
        try {
            snapShot = mDiskLruCache.get(MD5Utils.md5ForString(key));
            if (snapShot != null) {
                in = snapShot.getInputStream(0);
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                obj = objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (snapShot != null) {
                    snapShot.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    @Override
    public boolean checkFileExist(String imgUrl) {

        String fileName = savePath + File.separator + MD5Utils.md5ForString(imgUrl) + ".0";
        File file = new File(fileName);
        return file.exists();
    }

    private File getFile(String key) {
        String fileName = savePath + File.separator + key + ".0";
        File file = new File(fileName);
        return file;
    }

    @Override
    public String getSavePath() {
        File str = context.getFilesDir();
        savePath = str.getAbsolutePath() + File.separator + "ppfuns";
        return savePath;
    }
}
