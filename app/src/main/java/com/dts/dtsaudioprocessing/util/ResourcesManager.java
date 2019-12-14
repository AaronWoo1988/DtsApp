package com.dts.dtsaudioprocessing.util;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// A simple class to manager resources
public class ResourcesManager {
    public static boolean copyFileFromAssetsToInternalStorage(final Context context, final String fileName) {
        boolean copySuccess;

        try {
            InputStream myInput = context.getAssets().open(fileName);
            String outFileName = context.getFilesDir().getPath().toString() + "/" + fileName;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
            copySuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
            copySuccess = false;
        }

        return copySuccess;
    }
}
