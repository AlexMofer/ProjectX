## 1 前言
本文档参考并翻译自[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)。Google风格官方使用的是2个空格的缩进，国内常用的缩进为4个空格，因此此文档改为4个空格。同理，自动换行Google是+4个，此处为+8个，switch块缩进Google是2个，此处为4个。

本文档作为Java™编程语言中源代码的Google编码标准的**完整定义**。 当且仅当它遵守本文中的规则时，Java源文件才被称为遵循*Google Style*。

与其他编程风格指南一样，所涵盖的问题不仅包括格式化的审美问题，也包括其他类型的约定或编码标准。 但是，本文档主要关注我们普遍遵循的快速规则，并避免提供不可明确强制执行的建议（无论是通过人工还是工具）。

### 1.1 术语说明
在本文档中，除非另有说明：
 1. 术语 *class* 可表示一个普通类、枚举类、接口或是注解类型（`@interface`）。
 2. 术语 *member* （类的）可表示内部类、变量、方法或是构造函数；即除了初始化代码块与注释以外的类的所有顶级内容。
 3. 术语 *comment* 通常用于表示实现的注释（implementation comments），我们不使用“documentation comments”，而是用“Javadoc”。
其他的“术语说明”则会在后面的文档偶尔出现。

### 1.2 指南说明
本文档中的示例代码是**非规范**的。也就是说，虽然示例遵循Google Style，但其并不代表这就是体现代码优雅的*唯一*方式。示例中所做的可选格式的选择不应视为规则强制执行。

## 2 源文件基础
### 2.1 文件名
源文件以其顶级类的类名来命名，大小写敏感，文件扩展名为`.java`。

### 2.2 文件编码：UTF-8
源文件编码格式为**UTF-8**。

### 2.3 特殊字符
#### 2.3.1 空白字符
除了行结束符序列，**ASCII水平空格字符(0x20，即空格)**是源文件中唯一允许出现的空白字符，这意味着：
 1. 所有其它字符串中的空白字符都要进行转义。
 2. 制表符**不**用于缩进。

#### 2.3.2 特殊转义序列
对于具有[特殊转义序列](http://docs.oracle.com/javase/tutorial/java/data/characters.html)的任何字符（`\b`、 `\t`、`\n`、`\f`、`\r`、`\"`、`\'`及`\\`），我们使用它的转义序列，而不是相应的八进制(比如`\012`)或Unicode(比如`\u000a`)转义。

#### 2.3.3 非ASCII字符
对于剩余的非ASCII字符，是使用实际的Unicode字符(比如`∞`)，还是使用等价的Unicode转义符(比如`\u221e`)，取决于哪个能让代码**更易于阅读和理解**。
>  **提示：**在使用Unicode转义符或是一些实际的Unicode字符时，建议做些注释给出解释，这有助于别人阅读和理解。

例如：
示例|结论
---|---
`String unitAbbrev = "μs";`|最佳，即使没有注释也非常清晰。
`String unitAbbrev = "\u03bcs"; // "μs"`| 允许，但没有理由要这样做。
`String unitAbbrev = "\u03bcs"; // Greek letter mu, "s"`|允许，但这样做显得笨拙还容易出错。
`String unitAbbrev = "\u03bcs";`|很糟，读者根本看不出这是什么。
`return '\ufeff' + content; // byte order mark`|很好，对于非打印字符，使用转义，并在必要时写上注释。

>  **提示：**永远不要由于害怕某些程序可能无法正确处理非ASCII字符而让你的代码可读性变差。当程序无法正确处理非ASCII字符时，它自然**无法正确运行**，也就需要**修复**了。

## <span id="3">3 源文件结构</span>
一个源文件**按顺序**包含：
 1. 许可证或版权信息（如需）
 2. package语句
 3. import语句
 4. 有且仅有一个的顶级类
以上每个部分之间用**一个空行**隔开。

### 3.1 许可证或版权信息（如需）
如果一个文件包含许可证或版权信息，那么它应当被放在文件最前面。

### <span id="3.2">3.2 package语句</span>
package语句**不换行**，列限制（[章节4.4](#4.4)）并不适用于package语句。

### <span id="3.3">3.3 import语句</span>
#### 3.3.1 import不要使用通配符
**不**使用**静态**或其他方式的**通配符导入**。

#### 3.3.2 不要换行
import语句**不换行**，列限制([章节4.4](#4.4))并不适用于import语句。

#### 3.3.3 顺序和间距
导入顺序如下：
 1. 所有的静态导入独立成组。
 2. 所有的非静态导入独立成组。
如果存在静态和非静态导入，则单个空白行分隔两个块。 import语句之间没有其他空行。

在每个块中，导入的名称以ASCII排序顺序显示。 （注意：这与以ASCII排序顺序的import语句不同，因为“.”排在“;”前面。）

#### 3.3.4 没有静态导入的类
静态导入不能用于静态内部类。它们以普通导入方式导入。

### 3.4 类声明
#### 3.4.1 有且仅有一个顶级类
每个顶级类都驻留在自己的源文件中。

#### 3.4.2 类成员顺序
类的成员顺序对易学性有很大的影响，但这也不存在唯一的通用法则。不同的类对成员的排序可能是不同的。

最重要的一点，每个类应该以***某种*逻辑去排序**它的成员，维护者应该要能解释这种排序逻辑。比如，新的方法不能总是习惯性地添加到类的结尾，因为这样就是按时间顺序而非某种逻辑来排序的。

##### 3.4.2.1 区块划分
当一个类有多个构造函数或者多个同名的方法时，它们顺序出现，并且中间没有其他代码（包括私有member）。

## 4 格式化
**术语说明**：*块状结构（block-like construct）*指的是一个类，方法或构造函数的主体。需要注意的是([章节4.8.3.1](#4.8.3.1))，数组初始化中的初始值*可*被选择性地视为块状结构。

### 4.1 大括号
#### 4.1.1 使用大括号（即使是可选的）
大括号与`if`、`else`、`for`、`do`及`while`语句一起使用，即使只有一条语句或是空，也应该把大括号写上。

#### <span id="4.1.2">4.1.2 非空块：K & R 风格</span>
对于非空块和块状结构，大括号遵循 Kernighan 和 Ritchie 风格（[Egyptian brackets](http://www.codinghorror.com/blog/2012/07/new-programming-jargon.html)）：
 + 左大括号前不换行
 + 左大括号后换行
 + 右大括号前换行
 + 右大括号后换行，*只有*右大括号是一个语句、函数体或类的终止，则右大括号后换行。例如，如果后面跟着`else`或逗号，那么右大括号后面不换行。

示例：
```java
return () -> {
    while (condition()) {
        method();
    }
};

return new MyClass() {
    @Override public void method() {
        if (condition()) {
            try {
                something();
            } catch (ProblemException e) {
                recover();
            }
        } else if (otherCondition()) {
            somethingElse();
        } else {
            lastThing();
        }
    }
};
```
[章节4.8.1](#4.8.1)给出了enum类的一些例外。

#### 4.1.3 空块：可以用简洁版本
空块或块状构造可以是K＆R样式（如[章节4.1.2](#4.1.2)所述）。 或者，它可以在打开之后立即关闭，在（`{}`）之间没有字符或换行符，除非它是多块语句的一部分（直接包含多个块：`if/else`或`try/catch/finally`），即使大括号内没内容，右大括号也要换行。

示例：
```java
// 这是可以接受的
void doNothing() {}

// 这是同样可以接受的
void doNothingElse() {
}
```
```java
// 这是不可接受的: 多块语句中没有简明的空块
try {
    doSomething();
} catch (Exception e) {}
```

### 4.2 块缩进：4个空格
每次打开新的块或块状构造时，缩进增加4个空格。当块结束时，缩进返回到上一缩进级别。缩进级别适用于整个块中的代码和注释。(详见[章节4.1.2](#4.1.2)中的代码示例)

### 4.3 一行一个语句
每个语句后要换行。

### <span id="4.4">4.4 列限制：100</span>
Java代码的列限制为100个字符。除非如下所述，否则超过此限制的任何行都必须被换行，详见[章节4.5](#4.5)所述。
**例外：**
 1. 不可能满足列限制的行(例如，Javadoc中的一个长URL，或是一个长的JSNI方法参考)。
 2. `package`和`import`语句(见[章节3.2](#3.2)和[章节3.3](#3.3))。
 3. 注释中那些可能被剪切并粘贴到shell中的命令行。

### <span id="4.5">4.5 自动换行</span>
**术语说明：**当合法占用单行的代码被分为多行时，我们称之为自动换行(line-wrapping)。

我们并没有全面，确定性的准则来决定在每一种情况下如何自动换行。很多时候，对于同一段代码会有好几种有效的自动换行方式。
 > **说明：**虽然自动换行的典型原因是为了避免溢出列限制，但事实上符合列限制的代码也可以自动换行，这由代码作者自行决定。

　
 > **提示：**通过提取方法或局部变量可以解决不得不自动换行的问题。
 
#### 4.5.1 从哪里断开
自动换行的基本准则是：更倾向于在**更高的语法级别**处断开。如：
 1. 如果在非赋值运算符处断开，那么在该符号前断开。（注意：这一点与 Google 其它语言的编程风格不同，如 C++ 和 JavaScript。）
　 - 这条规则也适用于以下"类运算符"符号：
　　　- 点分隔符（`.`）
　　　- 一个方法引用的两个冒号（`::`）
　　　- 类型界限中的 &（`<T extends Foo & Bar>`）
　　　- 捕获块中的管道符号（`catch (FooException | BarException e)`）。
 2. 如果在赋值运算符处断开，通常在该符号后断开，但是在符号之前也是可以接受的。
　 - 这条规则也适用于foreach语句中的冒号。
 3. 方法名或构造函数名与左括号（`(`）留在同一行。
 4. 逗号（`,`）与其前面的内容留在同一行。
 5. 在Lambda表达式中，一行从不会被断开，除了如果Lambda表达式的主体由单个非支持表达式组成，则可能会在箭头之后立即出现断开。示例：
```java
MyLambda<String, Long, Object> lambda =
    (String label, Long value, Object obj) -> {
        ...
    };

Predicate<String> predicate = str ->
    longExpressionInvolving(str);
```
 > **说明：**自动换行的主要目的是为了使代码更为清晰，但对代码要求行数越少越好，则自动换行就*没有必要*了。

#### 4.5.2自动换行时缩进至少+8个空格
自动换行时，第一行后面的每一行（每个连续行）从原始行缩进至少增加8个空格。

当存在连续自动换行时，缩进可能会多缩进不只8个空格(语法元素存在多级时)。一般而言，两个连续行使用相同的缩进当且仅当它们开始于同级语法元素。

[章节4.6.3水平对齐](#4.6.3)一节中指出，不鼓励使用可变数目的空格来对齐前面行的符号。

### 4.6 空白
#### 4.6.1 垂直空白
以下情况需要使用一个空行：
 1. 类内连续的成员之间：字段，构造函数，方法，嵌套类，静态初始化块，实例初始化块。
　 - **例外：**两个连续字段之间的空行（在它们之间没有其他代码）是可选的。这样的空白行根据需要用于创建字段的*逻辑分组*。
　 - **例外：**枚举常量之间的空行，[章节4.8.1](#4.8.1)将介绍。
 2. 在函数体内，语句的逻辑分组间使用空行。
 3. 类内的第一个成员前或最后一个成员后的空行是可选的。（既不鼓励也不反对这样做，视个人喜好而定。）
 4. 要满足本文档中其他节的空行要求。（比如[章节3：源文件结构](#3)及[章节3.3：import语句](#3.3)）

多个连续的空行是允许的，但没有必要这样做(也不鼓励这样做)。

#### 4.6.2 水平空白
除了语言需求和其它规则，并且除了文字，注释和Javadoc用到单个空格，单个ASCII空格仅出现在以下几个地方：
 1. 分隔任何保留字（如：`if`、`for`及`catch`）与紧随其后的左括号（`(`）。
 2. 分隔任何保留字（如：`else`或`catch`）与其前面的右大括号（`}`）。
 3. 在任何左大括号前（`{`），两个例外：
　 - `@SomeAnnotation({a, b})`（不使用空格）
　 - `String[][] x = {{"foo"}};`（两个大括号之间无空格）
 4. 在任何二元或三元运算符的两侧。这也适用于以下"类运算符"符号：
　 - 类型界限中的&符：`<T extends Foo & Bar>`
　 - catch块中的管道符号：`catch (FooException | BarException e)`
　 -  foreach语句中的冒号（`:`）。
 5. 在`, : ;`及右括号（`)`）后
 6. 如果在一条语句后做注释，则双斜杠（`//`）两边都要空格。这里可以允许多个空格，但没有必要。
 7. 类型和变量之间：`List<String> list`
 8. 在数组初始值设定符的两个大括号内可选
　 - `new int[] {5, 6}`与`new int[] { 5, 6 }`都可行

此规则不应理解为在行的开始或结束处要求或禁止额外的空格; 它只涉及行的内部空格。

#### <span id="4.6.3">4.6.3 水平对齐：不做要求</span>
**术语说明：***水平对齐*指的是通过增加可变数量的空格来使某一行的字符与上一行的相应字符对齐。

这是允许的，但Google编程风格对此**不做要求**。即使对于已经使用水平对齐的代码，我们也不需要去*保持*这种风格。

以下示例先展示未对齐的代码，然后是对齐的代码：
```java
private int x; // this is fine
private Color color; // this too

private int   x;      // permitted, but future edits
private Color color;  // may leave it unaligned
```
 >  **说明：**对齐可增加代码可读性，但它为日后的维护带来问题。考虑未来某个时候，我们需要修改一堆对齐的代码中的一行。
这可能导致原本很漂亮的对齐代码变得错位。很可能它会提示你调整周围代码的空白来使这一堆代码重新水平对齐(比如程序员想保持这种水平对齐的风格)。这就会让你做许多的无用功，增加了reviewer的工作并且可能导致更多的合并冲突。

### 4.7 用小括号来限定组：推荐
除非作者和reviewer都认为去掉小括号也不会使代码被误解，或是去掉小括号能让代码更易于阅读，否则我们不应该去掉小括号。我们没有理由假设读者能记住整个Java运算符优先级表。

### 4.8 具体结构
#### <span id="4.8.1">4.8.1 枚举类</span>
枚举常量间用逗号隔开，换行可选。 还允许附加空行（通常只有一个）。 这是一个样例：
```java
private enum Answer {
    YES {
        @Override public String toString() {
            return "yes";
        }
    },

    NO,
    MAYBE
}
```
没有方法和文档的枚举类可写成数组初始化的格式（详见[章节4.8.3.1](#4.8.3.1)）。
```java
private enum Suit { CLUBS, HEARTS, SPADES, DIAMONDS }
```
由于枚举类也是一个*类*，因此所有适用于其它类的格式规则也适用于枚举类。

#### 4.8.2 变量声明
##### 4.8.2.1 每次只声明一个变量
不要使用组合声明，如`int a, b;`。

##### 4.8.2.2 需要时才声明，并尽快进行初始化
不要在一个代码块的开头把局部变量一次性都声明了，而是在第一次需要使用它时才声明。局部变量在声明时最好就进行初始化，或者声明后尽快进行初始化。

#### 4.8.3 数组
##### <span id="4.8.3.1">4.8.3.1 数组初始化：可写成块状结构</span>
数组初始化可以写成块状结构，比如，下面的写法都是可行的：
```java
new int[] {
    0, 1, 2, 3 
}

new int[] {
    0,
    1,
    2,
    3
}

new int[] {
    0, 1,
    2, 3
}

new int[]
    {0, 1, 2, 3}
```

##### 4.8.3.2 非C风格的数组声明
中括号是类型的一部分：`String[] args`， 而非 `String args[]`。

#### 4.8.4 switch语句
**术语说明：**switch块的大括号内是一个或多个语句组。每个语句组包含一个或多个switch标签(`case FOO:`或`default:`)，后面跟着一条或多条语句（最后一个语句组后面没有语句或者跟着多条语句）。

##### 4.8.4.1 缩进
与其它块状结构一致，switch块中的内容缩进为4个空格。每个switch标签后新起一行，再缩进4个空格，写下一条或多条语句。

##### 4.8.4.2 Fall-through：注释
在一个switch块内，每个语句组要么通过`break`、`continue`、`return`或抛出异常来终止，要么通过一条注释来说明程序将继续执行到下一个语句组， 任何能表达这个意思的注释都是可行的(典型的是用`// fall through`)。这个特殊的注释并不需要在最后一个语句组中出现。示例：
```java
switch (input) {
    case 1:
    case 2:
        prepareOneOrTwo();
        // fall through
    case 3:
        handleOneTwoOrThree();
        break;
    default:
        handleLargeNumber(input);
}
```
注意，在`case 1`后面不需要注释，只有在语句组的末尾需要。

##### 4.8.4.3 `default`的情况要写出来
每个switch语句包括一个`default`语句组，即使它不包含代码。

**例外：**`enum`类型的switch语句可以省略`default`语句组，*如果*它包括覆盖该类型的*所有*可能值的显式情况。这使IDE或其他静态分析工具能够在错过任何情况时发出警告。

#### 4.8.5 注解(Annotations)
注解紧跟在文档块后面，应用于类、方法和构造函数，一个注解独占一行。这些换行不属于自动换行（[章节4.5，自动换行](#4.5)），因此缩进级别不变。示例：
```java
@Override
@Nullable
public String getNameIfPresent() { ... }
```
**例外：**单个的注解可以和签名的第一行出现在同一行。如：
```java
@Override public int hashCode() { ... }
```
应用于字段的注解紧随文档块出现，应用于字段的多个注解允许与字段出现在同一行。如：
```java
@Partial @Mock DataLoader loader;
```
参数和局部变量注解没有特定规则。

#### 4.8.6 注释
本节讨论实现注释。 Javadoc在[章节7 Javadoc](#7)中单独讲解。

任何换行符之前可以有任意空格，然后是实现注释。这样的注释使该行非空白。

##### 4.8.6.1 块注释风格
块注释与其周围的代码在同一缩进级别。它们可以是`/* ... */`风格，也可以是`// ...`风格。对于多行的`/* ... */`注释，后续行必须以`*`开始， 并且与前一行的`*`对齐。
```java
/*
 * This is
 * okay.
 */
 
 // And so
 // is this.
 
 /* Or you can
  * even do this. */
```
注释不要封闭在由星号或其它字符绘制的框架里。
 > **提示：**当编写多行注释时，如果您希望自动代码格式化程序在必要时重新换行（段落样式），请使用`/* ... */`样式。大多数格式化程序不会在`// ...`样式注释块中重新换行。

#### 4.8.7 修饰符
类和成员的修饰符如果存在，则按Java语言规范中推荐的顺序出现。
```java
public protected private abstract default static final transient volatile synchronized native strictfp
```

#### 4.8.8 数字字面量
`long`数值使用大写`L`后缀，而非小写（以避免与数字`1`混淆）。例如，`3000000000L`而不是`3000000000l`。

## 5 命名约定
### 5.1 对所有标识符都通用的规则
标识符只能使用ASCII字母和数字，因此每个有效的标识符名称都能匹配正则表达式`\w+`。

在Google Style中，特殊的前缀或后缀不使用在示例中看到的如`name_`、`mName`、`s_name`和`kName`。

### 5.2 标识符类型的规则
#### 5.2.1 包名
包名称都是小写，连续的单词连接在一起（无下划线）。如`com.example.deepspace`而非`com.example.deepSpace`或`com.example.deep_space`。

#### <span id="5.2.2">5.2.2 类名</span>
类名都以UpperCamelCase风格编写。

类名通常是名词或名词短语（如：`Character`或`ImmutableList`），接口名称通常也是名词或名词短语（如`List`），但有时可能是形容词或形容词短语（如`Readable`）。

现在还没有特定的规则或行之有效的约定来命名注解类型。

测试类从它们正在测试的类的名称开始命名，并以`Test`结束。例如，`HashTest`或`HashIntegrationTest`。

#### 5.2.3 方法名
方法名都以lowerCamelCase风格编写。

方法名称通常是动词或动词短语。 例如，`sendMessage`或`stop`。

下划线可能出现在JUnit测试方法名称中，用以分隔名称的逻辑。一个典型的模式是`test<MethodUnderTest>_<state>`，例如`testPop_emptyStack`。尚未出现给测试方法命名的标准命名准则。

#### 5.2.4 常量名
常量名命名模式为`CONSTANT_CASE`，全部字母大写，用下划线分隔单词。那，到底什么算是一个常量？

常数是静态final字段，其内容是不可变的，并且其方法没有可检测的函数副作用。这包括基元，字符串，不可变类型和不可变类型的不可变集合。如果任何实例的观测状态可以改变，它就不是一个常量。静态final字段不一定都是常量。例如：
```java
// Constants
static final int NUMBER = 5;
static final ImmutableList<String> NAMES = ImmutableList.of("Ed", "Ann");
static final ImmutableMap<String, Integer> AGES = ImmutableMap.of("Ed", 35, "Ann", 32);
static final Joiner COMMA_JOINER = Joiner.on(','); // because Joiner is immutable
static final SomeMutableType[] EMPTY_ARRAY = {};
enum SomeEnum { ENUM_CONSTANT }

// Not constants
static String nonFinal = "non-final";
final String nonStatic = "non-static";
static final Set<String> mutableCollection = new HashSet<String>();
static final ImmutableSet<SomeMutableType> mutableElements = ImmutableSet.of(mutable);
static final ImmutableMap<String, SomeMutableType> mutableValues =
    ImmutableMap.of("Ed", mutableInstance, "Ann", mutableInstance2);
static final Logger logger = Logger.getLogger(MyClass.getName());
static final String[] nonEmptyArray = {"these", "can", "change"};
```
这些名字通常是名词或名词短语。

#### 5.2.5 非常量字段名
非常量字段名（静态或其他）以lowerCamelCase风格编写。
这些名称通常是名词或名词短语。 例如，`computedValues`或`index`。

#### 5.2.6 参数名
参数名以lowerCamelCase风格编写。

应该避免公共方法中的单字符参数名称。

#### 5.2.7 局部变量名
局部变量名以lowerCamelCase风格编写。

即使局部变量是final和不可改变的，也不应该把它示为常量，自然也不能用常量的规则去命名它。

#### 5.2.8 类型变量名
类型变量可用以下两种风格之一进行命名：
+ 单个的大写字母，后面可以跟一个数字（如：`E`、`T`、`X`、`T2`）。
+ 以类命名方式（[章节5.2.2](#5.2.2)），后面加个大写的`T`（如：`RequestT`、`FooBarT`）。

### 5.3 CamelCase：定义
有时不止一种合理的方式可以将短语转换为CamelCase模式，例如首字母缩略词或不寻常的结构，如“IPv6”或“iOS”。为了提高可预测性，Google Style指定（尽量）使用以下确定性方案。

从名称的文本形式开始：
 1. 将短语转换为纯ASCII并删除任何省略号。例如：“Müller's algorithm”可改为“Muellers algorithm”。
 2. 以空格和任何剩余的标点符号（通常为连字符）将结果划分为单词。
　 - *推荐：*如果有任何词在常用的情况下已经具有常规的CamelCase外观，将其拆分为其组成部分（例如，“AdWords”成为“ad words”）。 注意，诸如“iOS”这样的词本身不是真正的骆驼情况；它违反任何惯例，因此本建议不适用。
 3. 将单词（包括首字母缩略词）第一个字符大写其他字符全小写：
　 - ...将所有字符全大写
　 - ...除第一个字符外，将所有字符全小写
 4. 最后，将所有单词连接成一个词。
注意，原始单词样式几乎完全被忽略。示例：
文本形式|正确|错误
---|---|---
"XML HTTP request"|`XmlHttpRequest`|`XMLHTTPRequest`
"new customer ID"|`newCustomerId`|`newCustomerID`
"inner stopwatch"|`innerStopwatch`|`innerStopWatch`
"supports IPv6 on iOS?"|`supportsIpv6OnIos`|`supportsIPv6OnIOS`
"YouTube importer"|`YouTubeImporter`
||`YoutubeImporter`*
*可接受，但不推荐。
 > **提示：**一些单词在英语中有不明确的连字符：例如“nonempty”和“non-empty”都是正确的，所以方法名称`checkNonempty`和`checkNonEmpty`也都是正确的。

## 6 编程实践
### 6.1 `@Override`：能用则用
只要是合法的，就把`@Override`注解给用上。这包括重写超类方法的类方法，实现接口方法的类方法，以及重定义超接口方法的接口方法。

**例外：**当父方法为`@Deprecated`时，可以省略`@Override`。

### 6.2 捕获的异常：不能忽视
除下面的例子，对捕获的异常不做响应极少是正确的。（典型的响应方式是打印日志，如果不行，则把它当作一个`AssertionError`重新抛出。）

如果它确实是不需要在catch块中做任何响应，需要做注释加以说明（如下面的例子）。
```java
try {
    int i = Integer.parseInt(response);
    return handleNumericResponse(i);
} catch (NumberFormatException ok) {
    // it's not numeric; that's fine, just continue
}
return handleTextResponse(response);
```
**例外：**在测试中，如果一个捕获的异常被命名为`expected`，则它可以被不加注释地忽略。下面是一种非常常见的情形，用以确保所测试的方法会抛出一个期望中的异常，因此在这里就没有必要加注释。
```java
try {
    emptyStack.pop();
    fail();
} catch (NoSuchElementException expected) {
}
```

### 6.3 静态成员：使用类进行调用
使用类名调用静态的类成员，而不是具体某个对象或表达式。
```java
Foo aFoo = ...;
Foo.aStaticMethod(); // good
aFoo.aStaticMethod(); // bad
somethingThatYieldsAFoo().aStaticMethod(); // very bad
```

### 6.4 Finalizers: 禁用
**极少**会去重载`Object.finalize`。
 > **提示：**不要这么做，不得已非要这么做的话，请先仔细阅读并理解[Effective Java Item 7: "Avoid Finalizers"](http://books.google.com/books?isbn=8131726592)，非常小心，最后还是不要这么做。

## <span id="7">7 Javadoc</span>
### 7.1 格式
#### 7.1.1 一般形式
Javadoc块的基本格式如下所示：
```java
/**
 * Multiple lines of Javadoc text are written here,
 * wrapped normally...
 */
public int method(String p1) { ... }
```
或者是以下单行形式：
```java
/** An especially short bit of Javadoc. */
```
基本形式总是可以接受的。当整个Javadoc块（包括注释标记）可以放在单个行上时，可以将其替换为单行形式。注意，这只适用于没有块标签的情形，如`@return`。

#### <span id="7.1.2">7.1.2 段落</span>
空行（即仅包含对齐的前导星号（`*`）的行）出现在段落之间，并在Javadoc标记（如果存在）之前。除了第一个段落，每个段落第一个单词前都有标签`<p>`，并且它和第一个单词间没有空格。

#### <span id="7.1.3">7.1.3 Javadoc标记</span>
标准的Javadoc标记按以下顺序出现：`@param`、`@return`、 `@throws`、`@deprecated`，且这四种类型不能出项空描述。当描述无法在一行中容纳时，连续行需要至少在`@`缩进的基础上再缩进至少4个空格。

### <span id="7.2">7.2 摘要片段</span>
每个类或成员的Javadoc以一个简短的摘要片段开始。这个片段是非常重要的，在某些情况下，它是唯一出现的文本，比如在类和方法索引中。

这只是一个小片段，可以是一个名词短语或动词短语，但不是一个完整的句子。它不会以`A {@code Foo} is a...`或`This method returns...`开头,它也不会是一个完整的祈使句，如`Save the record.`。然而，由于开头大写及被加了标点，它看起来就像是个完整的句子。
 > **提示：**一个常见的错误是把简单的Javadoc写成 `/** @return the customer ID */`，这是不正确的。它应该写成`/** Returns the customer ID. */`。

### 7.3 何处需要使用Javadoc
至少在每个public类及它的每个public和protected成员处使用Javadoc，以下是一些例外。

也可能存在其他Javadoc内容，如[章节7.3.3](#7.3.3)所述。

#### 7.3.1 例外：不言自明的方法
对于简单明显的方法如`getFoo`，Javadoc是可选的。这种情况下除了写"Returns the foo"，确实也没有什么值得写了。
 > **提示：**如果有一些相关信息是需要读者了解的，那么以上的示例不应作为忽视这些信息的理由。例如，对于名为`getCanonicalName`的方法，不要省略其文档（文档可以仅仅是`/** Returns the canonical name. */`），可能读者并不明白“canonical name”具体指什么

#### 7.3.2 例外：重载
如果一个方法重载了超类中的方法，那么Javadoc并非必需的。

#### <span id="7.3.3">7.3.3 非必需Javadoc</span>
对于包外不可见的类和方法，如有需要，也是要使用Javadoc的。

如果一个注释是用来定义一个类，方法，字段的整体目的或行为，那么这个注释应该写成Javadoc（使用`/**`）。

不一定要求非必需的Javadoc遵循[章节7.1.2](#7.1.2)、[章节7.1.3](#7.1.3)和[章节7.2](#7.2)的格式化规则，当然也是建议遵循的。