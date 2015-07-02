package com.example.zip;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;
import java.util.zip.ZipException;

public class Zip4jSp {
    public static void Unzip(final File zipFile, String dest, String passwd,
                             String charset, final Handler handler, final boolean isDeleteZipFile)throws Exception {
        ZipFile zFile = new ZipFile(zipFile);
        if (TextUtils.isEmpty(charset)) {
            charset = "UTF-8";
        }
        zFile.setFileNameCharset(charset);
        if (!zFile.isValidZipFile()) {
            throw new ZipException(
                    "Compressed files are not illegal, may be damaged.");
        }
        File destDir = new File(dest); // Unzip directory
        if (destDir.isDirectory() && !destDir.exists()) {
            destDir.mkdir();
        }
        if (zFile.isEncrypted()) {
            zFile.setPassword(passwd.toCharArray());
        }

        final ProgressMonitor progressMonitor = zFile.getProgressMonitor();

        Thread progressThread = new Thread(new Runnable() {

            @Override
            public void run() {
                Bundle bundle = null;
                Message msg = null;
                try {
                    int percentDone = 0;
                    // long workCompleted=0;
                    // handler.sendEmptyMessage(ProgressMonitor.RESULT_SUCCESS)
                    if (handler == null) {
                        return;
                    }
                    handler.sendEmptyMessage(CompressStatus.START);
                    while (true) {
                        Thread.sleep(1000);

                        percentDone = progressMonitor.getPercentDone();
                        bundle = new Bundle();
                        bundle.putInt(CompressKeys.PERCENT, percentDone);
                        msg = new Message();
                        msg.what = CompressStatus.HANDLING;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        if (percentDone >= 100) {
                            break;
                        }
                    }
                    handler.sendEmptyMessage(CompressStatus.COMPLETED);
                } catch (InterruptedException e) {
                    bundle = new Bundle();
                    bundle.putString(CompressKeys.ERROR, e.getMessage());
                    msg = new Message();
                    msg.what = CompressStatus.ERROR;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    e.printStackTrace();
                } finally {
                    if (isDeleteZipFile) {
                        zipFile.deleteOnExit();//zipFile.delete();
                    }
                }
            }
        });

        progressThread.start();
        zFile.setRunInThread(true);
        zFile.extractAll(dest);
    }
}