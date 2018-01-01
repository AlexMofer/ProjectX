FtpServer
=========

<img src="icon.png" alt="Icon"/>

用于Android设备的FTP服务器。

介绍
---

用于Android设备的FTP服务器，可用于WIFI传输文件。

先决条件
----

无

入门
---

**引用:**
```java
dependencies {
    ...
    compile 'am.util:ftpserver:1.1.1'
    ...
}
```


**实现:**

开启FTP服务器：

```java
    final int port = 8585;
    final String home = Environment.getExternalStorageDirectory().getAbsolutePath();
    FtpServer server = FTPHelper.createServer(port, 10, 5000, true, home);
    server.start();
```

关闭FTP服务器：

```java
    server.stop();
```

**在build.gradle文件中增加**：

```java
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }
```

增加屏蔽混淆提示：

```java
    #
```

注意
---

- 不要忘记修改build.gradle文件
- 不要忘记增加屏蔽混淆提示

支持
---

- Google+: https://plus.google.com/114728839435421501183
- Gmail: moferalex@gmail.com

如果发现错误，请在此处提出:
https://github.com/AlexMofer/ProjectX/issues

许可
---

Copyright (C) 2015 AlexMofer

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.