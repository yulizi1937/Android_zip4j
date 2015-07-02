package com.example.zip;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends Activity {
    private TextView _info_textView;
    private ProgressBar progressBar;
    private Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CompressStatus.START: {
                    _info_textView.setText("Start...");
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
                case CompressStatus.HANDLING: {
                    Bundle bundle = msg.getData();
                    int percent = bundle.getInt(CompressKeys.PERCENT);
                    _info_textView.setText(percent + "%");
                    progressBar.setProgress(percent);
                    break;
                }
                case CompressStatus.ERROR: {
                    Bundle bundle = msg.getData();
                    String error = bundle.getString(CompressKeys.ERROR);
                    _info_textView.setText(error);
                    break;
                }
                case CompressStatus.COMPLETED: {
                    _info_textView.setText("Completed");
                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _info_textView = (TextView) findViewById(R.id.show);
        progressBar = (ProgressBar) findViewById(R.id.Progress);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //在sd卡的根目录放一个test.zip文件
        File out = new File(path, "test.zip");
        try {
            Zip4jSp.Unzip(out, path+"/yasuo", null, null, _handler, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
