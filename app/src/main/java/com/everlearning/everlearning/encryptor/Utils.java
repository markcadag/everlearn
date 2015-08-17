package com.everlearning.everlearning.encryptor;

import android.util.Log;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by mark on 8/8/15.
 */
public class Utils {

    private File fileEncoding;
    private OnEncode onEncode;
    private final String TAG = Utils.class.getSimpleName();

    public interface OnDecode {
        void onFailDecode(File file);
        void onSuccessDecode(InputStream inputStream);
        void onDecoding();
    }

    public interface OnEncode {
        void onFailEncode(File file);
        void onSuccessEncode(File fromFile,File encoded);
        void onEncoding();
    }

    private byte[] fileToByte(File file) {
        int size = (int)file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            Log.i(TAG, "sucess file to byte");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "fail file to byte");
        }
        return bytes;
    }

    //TODO add entity
    String generateName ="generate";

    public void encode(File file, File output, Crypto crypto){
        try {
            String generateName = System.currentTimeMillis()+"";
            OutputStream fileStream=new BufferedOutputStream(new FileOutputStream(output));
            OutputStream ouStream=crypto.getCipherOutputStream(fileStream, new Entity(generateName));
            ouStream.write(fileToByte(file));
            ouStream.flush();
            ouStream.close();
            Log.i(TAG,"success  " + output.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG,"failed");
            e.printStackTrace();
        }
    }

    public void decode(File file, File output, Crypto crypto) {
        if (!file.exists()) {
            Log.e(TAG, "cannot decode file does not exist");
            return;
        }
        try {
            FileInputStream fileStream = new FileInputStream(file);
            InputStream inputStream = crypto.getCipherInputStream(fileStream, new Entity(generateName));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(output));
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            int read;
            byte [] buff = new byte[1024];
            while((read = inputStream.read(buff)) != -1){
                outputStream.write(buff,0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
