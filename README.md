# SerialPort
## 迈冲科技串口库

串口通信由上位机（Android 开发板）和下位机（硬件设备）联合制定一套协议。

协议由 byte 数组构成，一般包括起始位、数据位、校验位和结束位，通过对数组数据的解析来达到通信目的。

## 一、创建 AndroidStudio 项目，导入库文件

将 mcSerialPort.aar 文件拷贝到 libs 目录下。

在```app```目录中的```build.gradle```文件中添加如下依赖：

```groovy
    implementation files('libs/mcSerialPort.aar')
```

## 二、使用接口

### 1，打开串口

串口通信前首先需要打开串口。

```java
/**
* @param device - 串口节点数据
* public class Device {
*    public String path;        节点路径
*    public int speed = 9600;   波特率
*    public int dataBits = 8;   数据位
*    public int stopBits = 1;   停止位
*    public char parity = 'n';  校验位, 'n': 无校验 'e': 偶校验 'o': 奇校验
*    public boolean block = false; 是否为阻塞模式打开串口节点
* }
**/
Device device = new Device
device.path = selectPort;
device.speed = selectSpeed;
serialPortManager = new SerialPortManager(device);
```

### 2，发送数据

串口打开成功后，使用 ```sendPacket``` 方法向下位机硬件设备发送协议数据。

```java
/**
* @parms bytes - 数据
**/
serialPortManager.sendPacket(bytes);

```

### 3，接收数据

```java
/**
* 设置数据接收回调 （在线程中运行）
**/
serialPortManager.setOnDataReceiveListener(new SerialPortManager.OnDataReceiveListener() {
     @Override
     public void onDataReceive(byte[] recvBytes, int i) {
         if (recvBytes != null && recvBytes.length > 0) {
              Log.i(TAG, "recvBytes === " + bytesToHexString(recvBytes, recvBytes.length));
         }
     }
});
```

### 4，关闭串口

串口使用结束后需要关闭串口。

```java
/**
* 关闭串口
**/
serialPortManager.closeSerialPort();
```