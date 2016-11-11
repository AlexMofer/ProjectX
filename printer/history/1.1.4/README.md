# Printer
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/printer/icon.png)

标准ES-POS命令打印，固定IP或蓝牙打印，支持黑白图片打印
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/printer/screenshot.png)
![打印样例](https://github.com/AlexMofer/ProjectX/blob/master/printer/printer_example.jpg)
## 要求
- minSdkVersion 5
- ```<uses-permission android:name="android.permission.INTERNET" />```
- ```<uses-permission android:name="android.permission.BLUETOOTH" />```

## 引用
```java
dependencies {
    ⋯
    compile 'am.util:printer:1.1.3'
    ⋯
}
```
## 详情
- 继承PrintTask来实现打印任务
- 继承PrinterWriter来实现更多纸张类型的打印
- PrinterUtils包含了众多打印指令

##使用
1.添加蓝牙权限```<uses-permission android:name="android.permission.BLUETOOTH" />```或者网络请求权限```<uses-permission android:name="android.permission.INTERNET" />```

2.继承PrintTask类，实现具体打印任务：
```java
private class TestPrintTask extends PrintTask {

    public TestPrintTask(BluetoothDevice device, int type) {
        super(device, type);
    }

    public TestPrintTask(String ip, int port, int type) {
        super(ip, port, type);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //打印前的准备工作，比如显示对话框
    }

    @Override
    protected byte[] getPrintData(int type) throws Exception {
        //实现打印数据的排版，生成字节流
        //根据不同的type创建不同的PrinterWriter，包内提供PrinterWriter80mm和PrinterWriter58mm，对应的是80mm小票打印机及58mm小票打印机。也可以自己创建PrinterWriter
        PrinterWriter80mm printer = new PrinterWriter80mm();
        printer.setAlignCenter();
        printer.printDrawable(res, R.drawable.ic_printer_logo);
        printer.setAlignLeft();
        printer.printLine();
        printer.printLineFeed();
        printer.setLineHeight(80);
        printer.print("最时尚的明星餐厅");
        printer.printLineFeed();
        printer.print("客服电话：400-8008800");
        printer.printLineFeed();
        ...
        printer.feedPaperCutPartial();
        return printer.getData();
    }

    @Override
    protected void onPrinterStateChanged(int state) {
        super.onPrinterStateChanged(state);
        //这些状态的变更是在主线程内
        switch (state) {
            case PrintRequest.STATE_0:
                //生成打印页面数据...
                break;
            case PrintRequest.STATE_1:
                ///生成数据成功，开始创建Socket连接...
                break;
            case PrintRequest.STATE_2:
                //创建Socket成功，开始发送测试数据...
                break;
            case PrintRequest.STATE_3:
                //获取输出流成功，开始写入打印页面数据...
                break;
            case PrintRequest.STATE_4:
                //写入打印页面数据成功，正在完成打印...
                break;
        }
    }

    @Override
    protected void onResult(int errorCode) {
        super.onResult(errorCode);
        switch (errorCode) {
            case PrintRequest.ERROR_0:
                //打印成功完成！
                break;
            case PrintRequest.ERROR_1:
                //生成打印页面数据失败！
                break;
            case PrintRequest.ERROR_2:
                //创建Socket失败！
                break;
            case PrintRequest.ERROR_3:
                //获取输出流失败！
                break;
            case PrintRequest.ERROR_4:
                //写入打印页面数据失败！
                break;
            case PrintRequest.ERROR_5:
                //必要的参数不能为空！
                break;
        }
    }
}
```

3.执行打印：
```java
new TestPrintTask(device, type).execute();
new TestPrintTask(ip, port, type).execute();
```

如果你要实现自己的打印机PrinterWriter，那么你需要继承
```java
public class PrinterWriter80mm extends PrinterWriter{

    public static final int TYPE_80 = 80;// 纸宽80mm

    public PrinterWriter80mm() throws IOException {
    }

    @Override
    protected int getLineWidth() {
        //一行能够放下多少个“-”
        return 24;
    }

    @Override
    protected int getLineStringWidth(int textSize) {
        //根据字体的大小，一行可以放下多少个英文字符
        switch (textSize) {
            default:
            case 0:
                return 47;
            case 1:
                return 23;
        }
    }

    @Override
    protected int getDrawableMaxWidth() {
        //图片能够全部打印在纸上的最大宽度
        return 500;
    }
}
```

## 注意
- 仅提供建立蓝牙连接打印，不包括蓝牙搜索及配对功能
- 不包含二维码生成功能

## 历史
- [**1.1.2**](https://bintray.com/alexmofer/maven/Printer/1.1.2)
- [**1.1.1**](https://bintray.com/alexmofer/maven/Printer/1.1.1)
- [**1.1.0**](https://bintray.com/alexmofer/maven/Printer/1.1.0)
- [**1.0.0**](https://bintray.com/alexmofer/maven/Printer/1.0.0)