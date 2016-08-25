package com.lichfaker.apksignature;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lichfaker.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText packageNameField;

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageNameField = (EditText) findViewById(R.id.packageField);

        resultTextView = (TextView) findViewById(R.id.resultText);

        findViewById(R.id.selectBtn).setOnClickListener(this);
        findViewById(R.id.genSignatureBtn).setOnClickListener(this);
        findViewById(R.id.copyBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectBtn:
                onSelect();
                break;
            case R.id.genSignatureBtn:
                String sign = getSign();
                Logger.d("应用签名:" + sign);
                resultTextView.setText(sign);
                break;
            case R.id.copyBtn:
                copy();
                break;
            default:
                break;
        }
    }

    // 显示选择列表
    void onSelect() {
        Intent intent = new Intent(MainActivity.this, PackageListActivity.class);
        startActivityForResult(intent, 1);
    }

    // 获取签名
    String getSign() {
        String name = packageNameField.getText().toString().trim();
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "包名不能为空", Toast.LENGTH_LONG).show();
            return "";
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(name, PackageManager.GET_SIGNATURES);
            if (info.signatures != null && info.signatures.length > 0) {
                Signature signature = info.signatures[0];
                MessageDigest md5 = null;
                try {
                    md5 = MessageDigest.getInstance("MD5");
                    byte[] digest = md5.digest(signature.toByteArray());
                    return toHexString(digest);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    // Should not occur
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "包名不存在", Toast.LENGTH_LONG).show();
        }
        return "";
    }

    // 复制到剪切板
    void copy() {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(resultTextView.getText().toString());
        Toast.makeText(this, "已复制到剪切板", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String packageName = data.getStringExtra("packageName");
            packageNameField.setText(packageName);
        } catch (Exception e) {

        }
    }

    private static final char[] HEX_CHAR = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /** 将字节数组转化为对应的十六进制字符串 */
    private String toHexString(byte[] rawByteArray) {
        char[] chars = new char[rawByteArray.length * 2];
        for (int i = 0; i < rawByteArray.length; ++i) {
            byte b = rawByteArray[i];
            chars[i*2] = HEX_CHAR[(b >>> 4 & 0x0F)];
            chars[i*2+1] = HEX_CHAR[(b & 0x0F)];
        }
        return new String(chars);
    }
}
