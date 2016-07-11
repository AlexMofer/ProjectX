# Printer
![ICON](https://github.com/AlexMofer/ProjectX/blob/master/printer/icon.png)

标准ES-POS命令打印，固定IP或蓝牙打印，支持黑白图片打印
## 预览
![Screenshots](https://github.com/AlexMofer/ProjectX/blob/master/printer/screenshot.png)
## 要求
- minSdkVersion 5
- ```<uses-permission android:name="android.permission.BLUETOOTH" />```

## 引用
```
dependencies {
    ⋯
    compile 'am.util:printer:1.0.0'
    ⋯
}
```
## 详情
- 继承PrintTask来实现打印任务
- 继承PrinterWriter来实现更多纸张类型的打印
- PrinterUtils包含了众多打印指令

## 注意
- 仅提供建立蓝牙连接打印，不包括蓝牙搜索及配对功能
- 不包含二维码生成功能