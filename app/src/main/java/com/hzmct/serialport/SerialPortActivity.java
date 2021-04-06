package com.hzmct.serialport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android_serialport_api.Device;
import android_serialport_api.SerialPortManager;

public class SerialPortActivity extends AppCompatActivity {
    private final static String TAG = "SerialPortActivity";

    private Button btnClear;
    private Button btnOpen;
    private Button btnSend;
    private Spinner spinnerPort;
    private Spinner spinnerSpeed;
    private TextView tvRecv;
    private EditText editSend;

    private SerialPortManager serialPortManager;
    private boolean openFlag = false;
    private String selectPort = "/dev/ttyS0";
    private int selectSpeed = 300;
    private String recvStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialport);

        btnClear = findViewById(R.id.btn_clear);
        btnSend = findViewById(R.id.btn_send);
        btnOpen = findViewById(R.id.btn_open);
        spinnerPort = findViewById(R.id.spinner_port);
        spinnerSpeed = findViewById(R.id.spinner_speed);
        tvRecv = findViewById(R.id.tv_recv);
        editSend = findViewById(R.id.edit_send);

        tvRecv.setMovementMethod(ScrollingMovementMethod.getInstance());

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openFlag) {
                    closeSerialPort();
                } else {
                    openSerialPort();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecvInfo("", true);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送串口数据
                if (serialPortManager != null) {
                    String sendStr = editSend.getText().toString();
                    if (sendStr.length() % 2 != 0) {
                        Toast.makeText(SerialPortActivity.this, "发送数据长度必须为2的倍数", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    byte[] bytes = hexString2Bytes(sendStr);
                    serialPortManager.sendPacket(bytes);
                }
            }
        });

        spinnerPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPort = getResources().getStringArray(R.array.port)[position];
                closeSerialPort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpeed = Integer.parseInt(getResources().getStringArray(R.array.speed)[position]);
                closeSerialPort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        closeSerialPort();
        super.onDestroy();
    }

    private void updateRecvInfo(final String recv, final boolean clear) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recvStr = clear ? recv : recvStr + recv + "\n";
                tvRecv.setText(recvStr);

                int scrollAmount = tvRecv.getLayout().getLineTop(tvRecv.getLineCount()) - tvRecv.getHeight();
                if (scrollAmount > 0) {
                    tvRecv.scrollTo(0, scrollAmount);
                } else {
                    tvRecv.scrollTo(0, 0);
                }
            }
        });
    }

    /**
     * 打开串口
     */
    public void openSerialPort() {
        if (serialPortManager == null) {
            btnOpen.setText("关闭");
            openFlag = true;
            Device device = new Device();
            device.path = selectPort;
            device.speed = selectSpeed;
            serialPortManager = new SerialPortManager(device);

            serialPortManager.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
                @Override
                public void onDataReceive(byte[] recvBytes, int i) {
                    if (recvBytes != null && recvBytes.length > 0) {
                        Log.i(TAG, "recvBytes === " + bytesToHexString(recvBytes, recvBytes.length));
                        updateRecvInfo(bytesToHexString(recvBytes, recvBytes.length), false);
                    }
                }
            });
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        btnOpen.setText("开启");
        openFlag = false;

        if (serialPortManager != null) {
            serialPortManager.closeSerialPort();
        }
        serialPortManager = null;
    }


    private String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    private byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}));
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}));
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    private String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            hex += " ";
            ret += hex;
        }
        return ret.toUpperCase();
    }
}
