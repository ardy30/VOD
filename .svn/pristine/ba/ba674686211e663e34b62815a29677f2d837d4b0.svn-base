package com.ppfuns.util;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpf on 2016/12/26.
 */

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        LogUtils.d(TAG, "folderName: " + folderName);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * 创建文件目录
     *
     * @param filePath
     * @return
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * 获取指定文件夹下的所有文件名(不获取子文件夹)
     * @param path
     * @return
     */
    public static List getFileNames(String path) {
        List<String> names = new ArrayList<>();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file != null && file.exists()) {
                File[] array = file.listFiles();
                int size = array.length;
                for (int i = 0; i < size; i++) {
                    File f = array[i];
                    if (f.isFile()) {
                        names.add(f.getName());
                    }
                }
            }
        }
        return names;
    }

    public static List<File> getFiles(String path) {
        List<File> files = new ArrayList<>();
        if (!TextUtils.isEmpty(path)) {
            path = getFolderName(path);
            File dir = new File(path);
            if (dir.exists()) {
                File[] array = dir.listFiles();
                int size = array.length;
                for (int i = 0; i < size; i++) {
                    File f = array[i];
                    if (f.isFile()) {
                        files.add(f);
                    }
                }
            }
        }
        return files;
    }
}
