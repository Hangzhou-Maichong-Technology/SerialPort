package com.hzmct.serialport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.hzmct.serialport.permission.PermissionUtil;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android_serialport_api.Device;
import android_serialport_api.SerialPortManager;

public class SerialPortActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "SerialPortActivity";

    private Button btnClear;
    private Button btnOpen, btnOpen2, btnOpen3, btnOpen4;
    private Button btnSend, btnSend2, btnSend3, btnSend4;
    private Spinner spinnerPort, spinnerPort2, spinnerPort3, spinnerPort4;
    private Spinner spinnerSpeed, spinnerSpeed2, spinnerSpeed3, spinnerSpeed4;
    private TextView tvRecv;
    private EditText editSend, editSend2, editSend3, editSend4;
    private CheckBox cbLoopTime1, cbLoopTime2, cbLoopTime3, cbLoopTime4;
    private EditText etLoopTime1, etLoopTime2, etLoopTime3, etLoopTime4;

    private SerialPortManager serialPortManager, serialPortManager2, serialPortManager3, serialPortManager4;
    private boolean openFlag = false;
    private boolean openFlag2 = false;
    private boolean openFlag3 = false;
    private boolean openFlag4 = false;
    private String selectPort = "/dev/ttyS0";
    private String selectPort2 = "/dev/ttyS0";
    private String selectPort3 = "/dev/ttyS0";
    private String selectPort4 = "/dev/ttyS0";
    private int selectSpeed = 300;
    private int selectSpeed2 = 300;
    private int selectSpeed3 = 300;
    private int selectSpeed4 = 300;
    private long loopTime = 500;
    private long loopTime2 = 500;
    private long loopTime3 = 500;
    private long loopTime4 = 500;
    private String recvStr = "";
    private RelativeLayout rl_port2, rl_port3, rl_port4;
    private LinearLayout ll_port2;
    private ScheduledFuture sendFuture1, sendFuture2, sendFuture3, sendFuture4;
    private PermissionUtil permissionUtil = new PermissionUtil();
    private AtomicInteger recvLine = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serialport);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasPermission(this)) {
            permissionUtil.requestPermission(this);
        }

        findId();
        tvRecv.setMovementMethod(ScrollingMovementMethod.getInstance());
        initSpinner();
        initCheckBox();
        initData();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initSpinner() {
        spinnerPort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPort = getResources().getStringArray(R.array.port)[position];
                closeSerialPort(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpeed = Integer.parseInt(getResources().getStringArray(R.array.speed)[position]);
                closeSerialPort(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerPort2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPort2 = getResources().getStringArray(R.array.port)[position];
                closeSerialPort(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeed2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpeed2 = Integer.parseInt(getResources().getStringArray(R.array.speed)[position]);
                closeSerialPort(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerPort3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPort3 = getResources().getStringArray(R.array.port)[position];
                closeSerialPort(2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeed3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpeed3 = Integer.parseInt(getResources().getStringArray(R.array.speed)[position]);
                closeSerialPort(2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerPort4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPort4 = getResources().getStringArray(R.array.port)[position];
                closeSerialPort(3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSpeed4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpeed4 = Integer.parseInt(getResources().getStringArray(R.array.speed)[position]);
                closeSerialPort(3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCheckBox() {
        cbLoopTime1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (openFlag) {
                        try {
                            loopTime = Long.parseLong(etLoopTime1.getText().toString());
                        } catch (Exception e) {
                            loopTime = 500;
                            etLoopTime1.setText(500);
                        }

                        if (sendFuture1 != null) {
                            sendFuture1.cancel(true);
                            sendFuture1 = null;
                        }

                        sendFuture1 = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "LoopSendThread1");
                            }
                        }).scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                serialSend1();
                            }
                        }, 0, loopTime, TimeUnit.MILLISECONDS);
                    } else {
                        cbLoopTime1.setChecked(false);
                    }
                } else {
                    if (sendFuture1 != null) {
                        sendFuture1.cancel(true);
                        sendFuture1 = null;
                    }
                }
            }
        });

        cbLoopTime2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (openFlag2) {
                        try {
                            loopTime2 = Long.parseLong(etLoopTime2.getText().toString());
                        } catch (Exception e) {
                            loopTime2 = 500;
                            etLoopTime2.setText(500);
                        }

                        if (sendFuture2 != null) {
                            sendFuture2.cancel(true);
                            sendFuture2 = null;
                        }

                        sendFuture2 = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "LoopSendThread2");
                            }
                        }).scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                serialSend2();
                            }
                        }, 0, loopTime2, TimeUnit.MILLISECONDS);
                    } else {
                        cbLoopTime2.setChecked(false);
                    }
                } else {
                    if (sendFuture2 != null) {
                        sendFuture2.cancel(true);
                        sendFuture2 = null;
                    }
                }
            }
        });

        cbLoopTime3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (openFlag3) {
                        try {
                            loopTime3 = Long.parseLong(etLoopTime3.getText().toString());
                        } catch (Exception e) {
                            loopTime3 = 500;
                            etLoopTime3.setText(500);
                        }

                        if (sendFuture3 != null) {
                            sendFuture3.cancel(true);
                            sendFuture3 = null;
                        }

                        sendFuture3 = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "LoopSendThread3");
                            }
                        }).scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                serialSend3();
                            }
                        }, 0, loopTime3, TimeUnit.MILLISECONDS);
                    } else {
                        cbLoopTime3.setChecked(false);
                    }
                } else {
                    if (sendFuture3 != null) {
                        sendFuture3.cancel(true);
                        sendFuture3 = null;
                    }
                }
            }
        });

        cbLoopTime4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (openFlag4) {
                        try {
                            loopTime4 = Long.parseLong(etLoopTime4.getText().toString());
                        } catch (Exception e) {
                            loopTime4 = 500;
                            etLoopTime4.setText(500);
                        }

                        if (sendFuture4 != null) {
                            sendFuture4.cancel(true);
                            sendFuture4 = null;
                        }

                        sendFuture4 = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                return new Thread(r, "LoopSendThread4");
                            }
                        }).scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                serialSend4();
                            }
                        }, 0, loopTime4, TimeUnit.MILLISECONDS);
                    } else {
                        cbLoopTime4.setChecked(false);
                    }
                } else {
                    if (sendFuture4 != null) {
                        sendFuture4.cancel(true);
                        sendFuture4 = null;
                    }
                }
            }
        });
    }

    private void initData() {
        LogUtils.getConfig().setDir("/sdcard").setLog2FileSwitch(true);
    }

    @Override
    protected void onDestroy() {
        closeSerialPort(0);
        closeSerialPort(1);
        closeSerialPort(2);
        closeSerialPort(3);
        super.onDestroy();
    }

    private synchronized void updateRecvInfo(final String recv, final boolean clear) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean realClear = clear;

                if (recvLine.getAndIncrement() > 100) {
                    recvLine.set(0);
                    realClear = true;
                }

                recvStr = realClear ? recv : recvStr + recv + "\n";
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
    public void openSerialPort(int port) {
        switch (port) {
            case 0:
                if (serialPortManager == null) {
                    btnOpen.setText("关闭");
                    openFlag = true;
                    final Device device = new Device();
                    device.path = selectPort;
                    device.speed = selectSpeed;
                    serialPortManager = new SerialPortManager(device);

                    serialPortManager.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
                        @Override
                        public void onDataReceive(byte[] recvBytes, int i) {
                            if (recvBytes != null && recvBytes.length > 0) {
                                String recvInfo = TimeUtils.getNowString() + " | " + device.path +
                                        "| recvBytes === " + bytesToHexString(recvBytes, recvBytes.length);
                                LogUtils.i(recvInfo);
                                updateRecvInfo(recvInfo, false);
                            }
                        }
                    });
                }
                break;

            case 1:
                if (serialPortManager2 == null) {
                    btnOpen2.setText("关闭");
                    openFlag2 = true;
                    final Device device = new Device();
                    device.path = selectPort2;
                    device.speed = selectSpeed2;
                    serialPortManager2 = new SerialPortManager(device);

                    serialPortManager2.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
                        @Override
                        public void onDataReceive(byte[] recvBytes, int i) {
                            if (recvBytes != null && recvBytes.length > 0) {
                                String recvInfo = TimeUtils.getNowString() + " | " + device.path +
                                        "| recvBytes === " + bytesToHexString(recvBytes, recvBytes.length);
                                LogUtils.i(recvInfo);
                                updateRecvInfo(recvInfo, false);
                            }
                        }
                    });
                }
                break;
            case 2:
                if (serialPortManager3 == null) {
                    btnOpen3.setText("关闭");
                    openFlag3 = true;
                    final Device device = new Device();
                    device.path = selectPort3;
                    device.speed = selectSpeed3;
                    serialPortManager3 = new SerialPortManager(device);

                    serialPortManager3.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
                        @Override
                        public void onDataReceive(byte[] recvBytes, int i) {
                            if (recvBytes != null && recvBytes.length > 0) {
                                String recvInfo = TimeUtils.getNowString() + " | " + device.path +
                                        "| recvBytes === " + bytesToHexString(recvBytes, recvBytes.length);
                                LogUtils.i(recvInfo);
                                updateRecvInfo(recvInfo, false);
                            }
                        }
                    });
                }
                break;
            case 3:
                if (serialPortManager4 == null) {
                    btnOpen4.setText("关闭");
                    openFlag4 = true;
                    final Device device = new Device();
                    device.path = selectPort4;
                    device.speed = selectSpeed4;
                    serialPortManager4 = new SerialPortManager(device);

                    serialPortManager4.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
                        @Override
                        public void onDataReceive(byte[] recvBytes, int i) {
                            if (recvBytes != null && recvBytes.length > 0) {
                                String recvInfo = TimeUtils.getNowString() + " | " + device.path +
                                        "| recvBytes === " + bytesToHexString(recvBytes, recvBytes.length);
                                LogUtils.i(recvInfo);
                                updateRecvInfo(recvInfo, false);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    private static final String ACTION_FUNCTION_SWITCH_CLOSE = "com.hzmact.cloudcontrol.ACTION_FUNCTION_SWITCH_CLOSE";
    private static final String ACTION_FUNCTION_SWITCH_OPEN = "com.hzmact.cloudcontrol.ACTION_FUNCTION_SWITCH_OPEN";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_one:
                ll_port2.setVisibility(View.GONE);
                rl_port2.setVisibility(View.GONE);
                break;
            case R.id.action_two:
                ll_port2.setVisibility(View.GONE);
                rl_port2.setVisibility(View.VISIBLE);
                break;
            case R.id.action_three:
                ll_port2.setVisibility(View.VISIBLE);
                rl_port2.setVisibility(View.VISIBLE);
                rl_port3.setVisibility(View.VISIBLE);
                rl_port4.setVisibility(View.GONE);
                break;
            case R.id.action_four:
                ll_port2.setVisibility(View.VISIBLE);
                rl_port2.setVisibility(View.VISIBLE);
                rl_port3.setVisibility(View.VISIBLE);
                rl_port4.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(int port) {
        switch (port) {
            case 0:
                btnOpen.setText("开启");
                openFlag = false;
                cbLoopTime1.setChecked(false);

                if (serialPortManager != null) {
                    serialPortManager.closeSerialPort();
                }
                serialPortManager = null;
                break;
            case 1:
                btnOpen2.setText("开启");
                openFlag2 = false;
                cbLoopTime2.setChecked(false);

                if (serialPortManager2 != null) {
                    serialPortManager2.closeSerialPort();
                }
                serialPortManager2 = null;
                break;
            case 2:
                btnOpen3.setText("开启");
                openFlag3 = false;
                cbLoopTime3.setChecked(false);

                if (serialPortManager3 != null) {
                    serialPortManager3.closeSerialPort();
                }
                serialPortManager3 = null;
                break;
            case 3:
                btnOpen4.setText("开启");
                openFlag4 = false;
                cbLoopTime4.setChecked(false);

                if (serialPortManager4 != null) {
                    serialPortManager4.closeSerialPort();
                }
                serialPortManager4 = null;
                break;
            default:
                break;
        }

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

    public void findId() {
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        tvRecv = findViewById(R.id.tv_recv);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        btnOpen = findViewById(R.id.btn_open);
        btnOpen.setOnClickListener(this);
        spinnerPort = findViewById(R.id.spinner_port);
        spinnerSpeed = findViewById(R.id.spinner_speed);
        editSend = findViewById(R.id.edit_send);
        cbLoopTime1 = findViewById(R.id.cb_loop_time);
        etLoopTime1 = findViewById(R.id.et_loop_time);

        rl_port2 = findViewById(R.id.rl_port2);
        btnSend2 = findViewById(R.id.btn_send2);
        btnSend2.setOnClickListener(this);
        btnOpen2 = findViewById(R.id.btn_open2);
        btnOpen2.setOnClickListener(this);
        spinnerPort2 = findViewById(R.id.spinner_port2);
        spinnerSpeed2 = findViewById(R.id.spinner_speed2);
        editSend2 = findViewById(R.id.edit_send2);
        cbLoopTime2 = findViewById(R.id.cb_loop_time2);
        etLoopTime2 = findViewById(R.id.et_loop_time2);

        ll_port2 = findViewById(R.id.ll_port2);
        rl_port3 = findViewById(R.id.rl_port3);
        btnSend3 = findViewById(R.id.btn_send3);
        btnSend3.setOnClickListener(this);
        btnOpen3 = findViewById(R.id.btn_open3);
        btnOpen3.setOnClickListener(this);
        spinnerPort3 = findViewById(R.id.spinner_port3);
        spinnerSpeed3 = findViewById(R.id.spinner_speed3);
        editSend3 = findViewById(R.id.edit_send3);
        cbLoopTime3 = findViewById(R.id.cb_loop_time3);
        etLoopTime3 = findViewById(R.id.et_loop_time3);

        rl_port4 = findViewById(R.id.rl_port4);
        btnSend4 = findViewById(R.id.btn_send4);
        btnSend4.setOnClickListener(this);
        btnOpen4 = findViewById(R.id.btn_open4);
        btnOpen4.setOnClickListener(this);
        spinnerPort4 = findViewById(R.id.spinner_port4);
        spinnerSpeed4 = findViewById(R.id.spinner_speed4);
        editSend4 = findViewById(R.id.edit_send4);
        cbLoopTime4 = findViewById(R.id.cb_loop_time4);
        etLoopTime4 = findViewById(R.id.et_loop_time4);
    }

    private void serialSend1() {
        if (serialPortManager != null) {
            String sendStr = editSend.getText().toString();
            if (sendStr.length() % 2 != 0) {
                Toast.makeText(SerialPortActivity.this, "发送数据长度必须为2的倍数", Toast.LENGTH_SHORT).show();
                return;
            }

            LogUtils.i(TimeUtils.getNowString() + " | Device1 | sendBytes === " + sendStr);

            byte[] bytes = hexString2Bytes(sendStr);
            serialPortManager.sendPacket(bytes);
        }
    }

    private void serialSend2() {
        if (serialPortManager2 != null) {
            String sendStr = editSend2.getText().toString();
            if (sendStr.length() % 2 != 0) {
                Toast.makeText(SerialPortActivity.this, "发送数据长度必须为2的倍数", Toast.LENGTH_SHORT).show();
                return;
            }

            LogUtils.i(TimeUtils.getNowString() + " | Device2 | sendBytes === " + sendStr);

            byte[] bytes = hexString2Bytes(sendStr);
            serialPortManager2.sendPacket(bytes);
        }
    }

    private void serialSend3() {
        if (serialPortManager3 != null) {
            String sendStr = editSend3.getText().toString();
            if (sendStr.length() % 2 != 0) {
                Toast.makeText(SerialPortActivity.this, "发送数据长度必须为2的倍数", Toast.LENGTH_SHORT).show();
                return;
            }

            LogUtils.i(TimeUtils.getNowString() + " | Device3 | sendBytes === " + sendStr);

            byte[] bytes = hexString2Bytes(sendStr);
            serialPortManager3.sendPacket(bytes);
        }
    }

    private void serialSend4() {
        if (serialPortManager4 != null) {
            String sendStr = editSend4.getText().toString();
            if (sendStr.length() % 2 != 0) {
                Toast.makeText(SerialPortActivity.this, "发送数据长度必须为2的倍数", Toast.LENGTH_SHORT).show();
                return;
            }

            LogUtils.i(TimeUtils.getNowString() + " | Device4 | sendBytes === " + sendStr);

            byte[] bytes = hexString2Bytes(sendStr);
            serialPortManager4.sendPacket(bytes);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                updateRecvInfo("", true);
                break;
            case R.id.btn_send:
                serialSend1();
                break;
            case R.id.btn_send2:
                serialSend2();
                break;
            case R.id.btn_send3:
                serialSend3();
                break;
            case R.id.btn_send4:
                serialSend4();
                break;
            case R.id.btn_open:
                if (openFlag) {
                    closeSerialPort(0);
                } else {
                    openSerialPort(0);
                }
                break;
            case R.id.btn_open2:
                if (openFlag2) {
                    closeSerialPort(1);
                } else {
                    openSerialPort(1);
                }
                break;
            case R.id.btn_open3:
                if (openFlag3) {
                    closeSerialPort(2);
                } else {
                    openSerialPort(2);
                }
                break;
            case R.id.btn_open4:
                if (openFlag4) {
                    closeSerialPort(3);
                } else {
                    openSerialPort(3);
                }
                break;
            default:
                break;
        }

    }
}
