package com.mirea.lutchenkoam.mireaproject;

import androidx.lifecycle.ViewModel;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class FileCryptographyViewModel extends ViewModel {

    public void encryptFile(Context context, Uri fileUri, String key) {
        processFile(context, fileUri, key, true);
    }

    public void decryptFile(Context context, Uri fileUri, String key) {
        processFile(context, fileUri, key, false);
    }

    private void processFile(Context context, Uri fileUri, String key, boolean encrypt) {
        try (InputStream fis = context.getContentResolver().openInputStream(fileUri)) {
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);

            byte[] keyBytes = key.getBytes();
            byte[] result = xorEncryptDecrypt(fileBytes, keyBytes);

            saveFile(context, result, (encrypt ? "encrypted_" : "decrypted_") + System.currentTimeMillis() + ".txt");
        } catch (IOException e) {
            Toast.makeText(context, "Error processing file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private byte[] xorEncryptDecrypt(byte[] data, byte[] key) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }

    private void saveFile(Context context, byte[] data, String fileName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            }

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            } else {
                uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            }

            if (uri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    outputStream.write(data);
                    Toast.makeText(context, "File saved successfully: " + fileName, Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(context, "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
