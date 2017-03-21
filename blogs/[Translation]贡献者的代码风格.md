# 贡献者的代码风格

下面的代码样式是严格的规则，而不是准则或建议。不符合这些规则的Android应用通常不会被接受。我们认识到，并非所有现有代码都遵循这些规则，但我们期望所有新代码都符合。

## Java语言规则

---

Android遵循标准的Java编码规则以及下面描述的附加规则。

### 不要忽略异常

可能很容易编写完全忽略异常的代码，例如：
```java
void setServerPort(String value) {
    try {
        serverPort = Integer.parseInt(value);
    } catch (NumberFormatException e) { }
}
```
切勿如此。虽然你可能认为你的代码永远不会出现这个错误或者处理这个错误并不重要，但这样忽略异常将埋下一颗地雷，其他人某一天可能会触发它。你必须以合理的方式在代码中处理每一个异常，方式上具体情况具体处理。

*你应当为你写出一个空的catch语句块时感到惊恐。即便有时就得这么处理，但也不应心安理得。写Java代码时，你不应当逃避惊恐感。*——[James Gosling](http://www.artima.com/intv/solid4.html)

可接受的替代方案（按优先顺序）是：

 + 将异常抛出给方法的调用者。
```java
    void setServerPort(String value) throws NumberFormatException {
        serverPort = Integer.parseInt(value);
    }
```
 + 抛出一个适合你的抽象层次的新异常。
```java
    void setServerPort(String value) throws ConfigurationException {
        try {
            serverPort = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Port " + value + " is not valid.");
        }
    }
 ```
 + 优雅地处理错误并在catch {}块中替换一个合适的值。
```java
    /** Set port. If value is not a valid number, 80 is substituted. */

    void setServerPort(String value) {
        try {
            serverPort = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            serverPort = 80;  // default port for server
        }
    }
 ```
 + 捕获异常并抛出一个新的`RuntimeException`。这是比较危险的，除非你确定导致这个错误的事件属于程序崩溃。
```java
    /** Set port. If value is not a valid number, die. */

    void setServerPort(String value) {
        try {
            serverPort = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("port " + value " is invalid, ", e);
        }
    }
```
     > **注意** 将原异常作为参数创建RuntimeException。如果代码必须在Java 1.3上编译，那么你不得不忽略作为原因的原始异常。
 + 作为最后的手段，如果确定忽略异常是恰当的，那么可以忽略它，但必须注释一个很好的理由为什么忽略：
```java
    /** If value is not a valid number, original port number is used. */
    void setServerPort(String value) {
        try {
            serverPort = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Method is documented to just ignore invalid user input.
            // serverPort will just be unchanged.
        }
    }
```

### 不要捕捉基类异常

有时候很容易懒惰到这样捕捉异常：
```java
try {
    someComplicatedIOFunction();        // may throw IOException
    someComplicatedParsingFunction();   // may throw ParsingException
    someComplicatedSecurityFunction();  // may throw SecurityException
    // phew, made it all the way
} catch (Exception e) {                 // I'll just catch all exceptions
    handleError();                      // with one generic handler!
}
```
切勿如此。在几乎所有情况下，捕获基类异常或Throwable（最好不是Throwable，因为它包括错误异常）是不合适的。这非常危险，因为这意味着你会捕捉那些本应在应用级被处理的非预期的异常（包括像ClassCastException这样的运行时异常）。它忽略了处理异常时的异常类型，也就是如果别人在你调用的代码中加入了一种新的类型的异常，编译器就无法帮你区别处理这个新的类型的异常，而在大多数情况下，不应以同样的方式处理不同类型的异常。

这条规则也有个罕见的例外，在你希望捕获各种错误（防止它们出现在UI中，或者保持后续的工作继续运行）的测试代码和顶层代码中，你可以捕捉并恰当地处理基类异常（或Throwable）。慎重而为，并注解出为何此处这么做是安全的。

捕获泛型异常的替代方案：

 + 每个try语句之后分类型捕获每一个异常，方法虽笨，但是却要优于捕获基类异常，避免在catch块中代码过度重复。
 + 重构代码，拆分单个try块的内容，换成多个try块来捕获异常。通过拆分输入输出与解析来区别处理捕获的异常。
 + 重新抛出异常。多数时候你并不需要捕获这个级别的异常，就让方法抛出来即可。
记住：把异常当朋友吧！当编译器提示你没有捕获异常时，不用皱眉。微笑：编译器不过是辅助你捕获代码中的运行错误。

### 不要使用Finalizers
Finalizers可以让你在对象被垃圾回收时执行一段代码。虽然他非常便于进行资源清理（尤其是外部资源），但是无法保证什么时候会调用它（或者甚至马上就会调用）。

Android并不使用Finalizers。大多数情况下，你可在一个拥有较好的异常捕获的Finalizers中执行你想要的操作。如果你的确需要它，定义一个close()（或类似的）方法并注释出这个方法需要在什么时候被调用（可参见InputStream）。这种情况下，并不要求但可以适当地打印一些简短的日志消息，当然也不能日志洪泛。

### 完整的导入
当你想要使用foo包的Bar类时，以下是两种可能的导入方式：

 + `import foo.*;`

    潜在地减少import语句的数量。
 + `import foo.Bar;`

    能确切地指出什么类被使用了并且对维护者来说可读性更高。

Android代码中全部使用`import foo.Bar;`的导入方式。另一种方案在Java标准库（`java.util.*`、`java.io.*`等）与单元测试代码（`junit.framework.*`）上存在显式异常。

## Java库规则

---
在使用Android的Java库和工具上存在一些约定。在某些情况下，这些约定出现了重大的改变，较早的代码可能仍然使用一些已弃用的模式或库。对于现成的代码可以继续现有的风格，但对于新创建的组件时，就不要再使用已弃用的库了。

## Java风格规则

---

### 使用标准的Javadoc注释
每个文件应在顶部有一个版权声明，其后是package和import语句（每个块由空行分隔），最后是类或接口声明。在Javadoc注释中，需描述类或接口的作用。
```java
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.foo;

import android.os.Blah;
import android.view.Yada;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Does X and Y and provides an abstraction for Z.
 */

public class Foo {
    ...
}
```
你写的每一个类和重要的公共方法必须包含一个Javadoc注释，至少需要一句话的描述。句式以动词开始。

例如：
```java
/** Returns the correctly rounded positive square root of a double value. */
static double sqrt(double a) {
    ...
}
```
或
```java
/**
 * Constructs a new String by converting the specified array of
 * bytes using the platform's default character encoding.
 */
public String(byte[] bytes) {
    ...
}
```
对于像`setFoo()`这样普通的get与set方法就不需要写Javadoc了，能写的也不过是“获取 Foo”。如果方法执行了一些复杂的操作（如强制约束条件或有重要的函数副作用），那么就需要注释。如果读者不清楚“Foo”属性具体内容时，也需要注释。

你写的每个方法，无论是公共的还是其他的，都将从Javadoc中受益。公共方法是API的一部分，因此需要Javadoc。Android目前没有强制执行一个特定的风格来编写Javadoc注释，但你可以参照这个说明：[如何为Javadoc工具编写文档注释](http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html)。

### 短小精炼的方法
尽可能地保持方法的短小精炼。但有时方法内容长又是恰当的，所以并不硬性限制方法的内容长度。如果一个方法超过40行左右，可以考虑在不破坏程序结构的前提下对其拆解。

### 固定位置中定义字段
在文件的顶部或在使用它们的方法之前定义字段。

### 限制变量范围
将局部变量的范围保持为最小。通过这样做，我们可以提高代码的可读性和可维护性，并减少出错的可能性。每个变量应该在包含变量的所有使用的最内部块中声明。

局部变量应该在它们第一次使用的点被声明。几乎每个局部变量声明都应该包含一个初始化器。在你还没有足够的初始化条件时就不要声明局部变量，推迟到条件充足时是更为明智的选择。

在try-catch语句块中，如果变量通过一个会抛出异常的方法的返回值来初始化，则必须在try块中进行。如果值必须在try块之外使用，那么它就得在try块之前声明，此时还不能明确地初始化：
```java
// Instantiate class cl, which represents some sort of Set
Set s = null;
try {
    s = (Set) cl.newInstance();
} catch(IllegalAccessException e) {
    throw new IllegalArgumentException(cl + " not accessible");
} catch(InstantiationException e) {
    throw new IllegalArgumentException(cl + " not instantiable");
}

// Exercise the set
s.addAll(Arrays.asList(args));
```
然而，即使这种情况也可以通过在一个方法中封装try-catch块来避免：
```java
Set createSet(Class cl) {
    // Instantiate class cl, which represents some sort of Set
    try {
        return (Set) cl.newInstance();
    } catch(IllegalAccessException e) {
        throw new IllegalArgumentException(cl + " not accessible");
    } catch(InstantiationException e) {
        throw new IllegalArgumentException(cl + " not instantiable");
    }
}

...

// Exercise the set
Set s = createSet(cl);
s.addAll(Arrays.asList(args));
```
若不是有逼不得已的原因，循环变量应该在for语句体内声明：
```java
for (int i = 0; i < n; i++) {
    doSomething(i);
}
```
以及
```java
for (Iterator i = c.iterator(); i.hasNext(); ) {
    doSomethingElse(i.next());
}
```
### 排序导入语句
import语句的顺序：

 1. Android包
 2. 第三方包（`com`、`junit`、`net`、`org`）
 3. `java `与`javax`

要完全匹配IDE设置，导入顺序应为：

 + 每个分组中按字母顺序排列，大写字母在小写之前（如：Z在a之前）
 + 在每个主要分组（`android`、`com`、`junit`、`net`、`org`、`java`、`javax`）之间用空行分隔。

最初，对排序没有样式要求，这意味着IDE可能会经常改变顺序，或者开发人员须禁用自动导入来进行手动维护导入。这很糟糕。当Java风格提出时，首选样式经过一段多变而有混乱的过程之后归结为简单地“选择一个兼容的排序方案”。所以我们选择了一种风格，更新了风格指南，并让IDE遵守它。以后IDE将按照此方案自行维护包的导入，而我们则无需再做额外操作。

风格是这样选取的：

 + 想最先看到的导入应放置于顶部（`android`）。
 + 最不想看到的导入应放置于底部（`java`）。
 + 大家更易于遵循的风格。
 + IDE可以遵循的风格。

静态导入的使用及位置仍存在一些争议。有些人倾向于静态导入穿插在其他导入之间，而有些人则更乐意静态导入在所有导入顶部或底部。加之，我们也没有确定如何使所有IDE使用相同的顺序。由于许多人认为这是个低优先级的问题，所以保持兼容性的前提下自行判断即可。

### 空格缩进
使用4个空格而非制表符进行块缩进。存在疑问时，保持与周围代码一致即可。

使用8个空格进行自动换行，包括函数调用及赋值运算。正确示例：
```java
Instrument i =
        someLongExpression(that, wouldNotFit, on, one, line);
```
错误示例：
```java
Instrument i =
    someLongExpression(that, wouldNotFit, on, one, line);
```

### 遵循字段命名规则

 + 非公共，非静态字段名以m开头。
 + 静态字段名称以s开头。
 + 其他字段以小写字母开头。
 + 公共静态final字段（常量）为ALL_CAPS_WITH_UNDERSCORES。

示例：
```java
public class MyClass {
    public static final int SOME_CONSTANT = 42;
    public int publicField;
    private static MyClass sSingleton;
    int mPackagePrivate;
    private int mPrivate;
    protected int mProtected;
}
```

### 使用标准括号风格
左大括号不要独立成行; 与其之前的代码在同一行：
```java
class MyClass {
    int func() {
        if (something) {
            // ...
        } else if (somethingElse) {
            // ...
        } else {
            // ...
        }
    }
}
```
我们需要在条件语句周围添加括号。例外：如果整个条件（条件和主体）适合一行，你可以（非必须）把它全部放在一行上。 例如，这是可行的：
```java
if (condition) {
    body();
}
```
这也是可行的：
```java
if (condition) body();
```
但这是不好的：
```java
if (condition)
    body();  // bad!
```

### 列长限制
代码中的每行文字长度最多为100个字符。虽然关于这个规则存在很多争论，但最终决定仍旧是100个字符。*以下例外：*

 + 如果注释行包含示例命令或长度超过100个字符的URL文本，则该行可能长于100个字符，以便于剪切和粘贴。
 + 导入行可以超过限制，因为人们很少看到它们（这也简化了工具写入）。

### 使用标准Java注解
注解应位于同一语句元素的其他修饰符之前。简单的标记注解（如`@Override`）可以与语句元素列在同一行。如果有多个注解或参数化注解，它们则应按字母顺序逐行列出。

Java中三种预定义注解的Android标准用法是：

 + `@Deprecated`：不再鼓励使用的过时的语句元素必须使用@Deprecated注解。如果使用@Deprecated注解，则还必须具有Javadoc的@deprecated标记，并且指出替代方案。此外，请记住，@Deprecated方法仍然应该工作。 如果您看到旧代码带有@deprecated Javadoc标记，请添加@Deprecated注释。
 + `@Override`：只要方法从超类覆盖声明或实现，就必须使用@Override注解。例如，如果使用Javadoc的@inheritdocs标记，并从类（而不是接口）派生，则还必须@Overrides注解该方法，表明其覆盖父类的方法。
 + `@SuppressWarnings`：在无法消除警告的情况下才可以使用@SuppressWarnings注解。如果警告通过了“不可消除”测试，则必须使用@SuppressWarnings注解，以确保所有警告都反映代码存在实际问题。
    当需要使用@SuppressWarnings注解时，就必须以TODO注释出为何出现“不可消除”的情形。通常会定义出是哪个类提供了一个糟糕的接口。例如：
```java
   // TODO: The third-party class com.third.useful.Utility.rotate() needs generics
   @SuppressWarnings("generic-cast")
   List<String> blix = Utility.rotate(blax);
```
    当必须使用@SuppressWarnings注解时，应该重构代码以分离出需要使用该注解的语句元素。

### 运用首字母缩略词
使用首字母缩略词及简写来命名变量、方法和类以提高可读性：

好|坏
---|---
XmlHttpRequest|XMLHTTPRequest
getCustomerId|getCustomerID
class Html|class HTML
String url|String URL
long id|long ID
由于JDK和Android代码库在首字母缩略词之间非常不一致，因此几乎不可能与周围的代码一致。所以，始终将首字母缩写作为词。

### 使用TODO注释
使用TODO来注释临时的、解决方案短暂的或者足够好但并不完美的代码。TODO需全大写后跟冒号：
```java
// TODO: Remove this code after the UrlTable2 has been checked in.
```
以及
```java
// TODO: Change this to use a flag instead of a constant.
```
如果您的TODO的形式是“在未来的日期做某事”，请确保您包括一个非常具体的日期（“在2005年11月修复”）或一个非常具体的事件（“在所有产品版本提升到V7之后删除此代码”）。

### 谨慎打印
虽然日志是有必要的，但是它对性能具有显著的负面影响，并且随着日志的长度变大，其有效性损失越多。日志打印器提供了五种不同级别的日志：

 + `ERROR`：致命错误出现时使用，即导致不可逆的用户可见效果，像隐式地删除数据、卸载应用程序、擦数数据分区或刷设备（或更糟）。这个级别始终会被打印出来。通过ERROR级别的日志来收集异常并发送给统计服务器是个不错的方案。
 + `WARNING`：严重的意外事件发生时使用，即导致可恢复的用户可见效果，像并不会导致数据丢失的失误操作、一直等待或重启应用、重新下载一个新版本或重启设备。这个级别始终会被打印出来。通过WARNING级别的日志来收集异常并发送给统计服务器也是可以考虑的方案。
 + `INFORMATIVE`：大多数人所关注的重要事件发生时使用，即检测到一些具备广泛影响但并不是错误的情况。其应该由领域中最具权威性的模块来打印（以避免非授权的组件来重复打印日志）。这个级别始终会被打印出来。
 + `DEBUG`：用于进一步打印设备上可能与调试意外行为有关的内容。你应该只打印你的组件上发生的那些你所需要收集的足够信息。如果调试日志占了你的日志主导，那么你应该使用详细日志记录。

    这个级别始终会被打印出来。即使是正式版本，需要添加条件判断来禁用所有的调试日志（如：`if (LOCAL_LOG)`、`if (LOCAL_LOGD)`）。if的判断条件最好不要是活动逻辑。打印所需的字符串的创建最好也包含在if语句里面。如果打印字符串在if语句外部构建，则不应在方法调用中重新映射日志调用。

    有些代码坚持使用非标准命名的方案`if (localLOGV)`，其尚可接受。
 + `VERBOSE`：以外的所有打印都可以使用这个。只在调试版本中打印此级别的日志，并需要if语句包裹，才能默认被编译出来。if语句中的字符串构建将在正式版中删除。

*注意：*

 + 在给定的模块中，除了在VERBOSE级别，如果可以，一个错误只应报告一次。在模块内的单个函数调用链中，只有最内层函数应该返回错误，如果明显有助于隔离问题，那么同一模块中的调用者应该只添加一些日志。
 + 在一个模块链当中，除VERBOSE级别，当低级模块检测到来自高级模块的无效数据时，低级模块只应将此情况记录到DEBUG日志中，并且仅当日志提供的信息对调用者来说不可用。具体来说，不需要打印抛出异常的位置（异常应包含所有相关信息），或者日志的所有信息包含在错误代码中。这在框架和应用程序之间的交互中尤其重要，由框架处理的第三方应用引起的情形，所触发的日志级别不应高于DEBUG级别。只有当模块或应用程序检测到来自其自身级别或较低级别的错误时，才能触发INFORMATIVE或更高级别的日志打印。
 + 有些日志打印会多次用到相同（或非常相似）的数据时，可以提取公共数据用于打印日志，以便更好地节约资源。
 + 网络连接的丢失完全是常见而又预期内的，不应该打印日志。网络连接丢失的日志应该为DEBUG或VERBOSE级别（在正式版中是否打印日志取决于后果与非预期的严重程度）。
 + 在第三方应用程序提供的文件系统中具备一个完整的文件系统的，不应打印级别高于INFORMATIVE的日志。
 + 来自任何不受信任的源（包括共享存储上的任何文件，或通过任何网络获取的数据）的无效数据被视为预期的，并且当检测数据无效时，不应触发高于DEBUG的级别的日志打印（甚至对高级别的日志打印也应受限制）。
 + 记住，对字符串使用+运算符时，会隐式创建一个带有缓冲区（16字符）及其他临时String对象的`StringBuilder`。显式地创建StringBuilder并不比直接使用“+”更耗费资源（实际上更高效）。记住，即便日志并不会被用于阅读，在正式版中`Log.v()`方法照样会被编译执行，包括打印的字符串。
 + 所有高于DEBUG级别的日志，用于给他人阅读或在正式版也启用，则必须简洁且易于理解。
 + 必要的时候，如果可能，记录应该保持在一行。线长度最多可达80或100个字符是完全可以接受的，如果可能，应避免长度大于130或160个字符（包括标签的长度）。
 + 不应使用高于VERBOSE的级别记录报告成功的日志。
 + 用于追踪难以重现的问题的临时日志应当使用DEBUG或者VERBOSE级别，并且包裹在if语句中，以便完成问题追踪后完全禁用它。
 + 小心日志中的安全漏洞。避免打印私人信息。毫无疑问地，也应避免打印保护的内容。这在编写框架代码时尤其重要，因为事先不容易知道什么是私人信息或受保护的内容。
 + 禁用`System.out.println()`（或native的`printf()`）。System.out和System.err被重定向到/dev/null，所以你的打印语句将没有可见的效果。然而，对于这些打印所需要的字符串仍然被构建了。
 + *日志的黄金规则：你的日志不必要的情况下不会将其他日志推出缓冲区，其他日志也不会对你的日志这么做。*

### 风格一致
我们一致认为：保持代码风格一致。如果您正在编辑代码，请花几分钟时间查看周围的代码并确定其样式。如果代码在if条件左右使用了空格，那么你也该这么做。如果代码的注释用星星框起来，那么你也保证你的注释用星星框起来。

编码风格指导的要点是有一个通用的编码词汇，所以人们可以专注于你在说什么，而不是你如何说。我们在这里提出全球化的风格规则，所以人们知道编码词汇，但本土化的风格也很重要。如果你添加到一个文件的代码看起来与现有的代码有很大的不同，当读者阅读它时，它会使读者脱离他们的节奏。所有尽量避免这一点。

## Javatests风格规则

---

遵循测试方法命名规则，使用下划线将要测试的内容与要测试的特定案例分开。这种风格让阅读者更容易看懂正在被测试的案例。例如：
```java
testMethod_specificCase1 testMethod_specificCase2

void testIsDistinguishable_protanopia() {
    ColorMatcher colorMatcher = new ColorMatcher(PROTANOPIA)
    assertFalse(colorMatcher.isDistinguishable(Color.RED, Color.BLACK))
    assertTrue(colorMatcher.isDistinguishable(Color.X, Color.Y))
}
```

