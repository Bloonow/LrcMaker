package com.excelbloonow.android.lrcmaker.FileWidget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static boolean fileOutput(String filepath, String content) {
        File file = new File(filepath);
        if (file.isDirectory()) {
            return false;
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static StringBuilder fileInputBasic(String filepath) {
        File file = new File(filepath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String content;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((content = bufferedReader.readLine()) != null) {
                stringBuilder.append(content).append("\n");
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder;
    }

    public static String fileInput(String filepath) {
        StringBuilder stringBuilder = fileInputBasic(filepath);
        if (stringBuilder != null) {
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String fileInputWithTrimmed(String filepath) {
        StringBuilder stringBuilder = fileInputBasic(filepath);
        if (stringBuilder == null || stringBuilder.length() == 0) {
            return null;
        }
        while (stringBuilder.toString().startsWith("\n")) {
            stringBuilder.deleteCharAt(0);
        }
        while (stringBuilder.toString().endsWith("\n")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
