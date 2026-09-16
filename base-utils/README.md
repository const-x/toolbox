# Base-utils 模块

基础工具库，无外部依赖，提供字符串处理、文件操作、日期时间、数学计算、HTTP调用、RSA加密、Swing扩展组件等通用功能。

## 核心功能

### 1. 字符串工具 (`StringExtUtils`)

#### 中文处理
```java
containChinese(String str)       // 是否包含中文
isChinese(char c)                // 判断字符是否为中文
countChineseCharacters(String)   // 统计中文字符数量
```

#### Emoji/UTF8MB4 处理
```java
hasEmoji(String str)             // 是否包含Emoji
removeEmojis(String str)         // 移除Emoji字符
replaceUtf8mb4(String str)       // 替换UTF8MB4字符为uxxxx格式
regainUtf8mb4(String str)        // 还原uxxxx格式为UTF8MB4字符
removeUtf8Mb4(String text)       // 直接移除UTF8MB4字符
```

#### 字符串填充与格式化
```java
fillStringLen(String value, int len)                     // 填充到指定长度
fillStringLen(String value, int len, char fill, int direction) // 指定填充字符和方向
fillZhLen(String value, int len)                         // 中文按两倍长度填充
sensitive(String data, int begin, int end)               // 敏感数据脱敏（如：张*三）
isNumeric(String str)                                    // 判断是否为数字
isNotBlank / isBlank(String value)                       // 判断非空/空
join(String delimiter, T... values)                      // 连接多个值
```

#### 零宽度字符处理
```java
replaceZeroWidthChars(String text)   // 替换零宽度字符为十六进制表示
```

### 2. 文件工具 (`FileUtils`)

#### 文件操作
```java
createFile(String filePath)         // 创建文件（自动创建父目录）
openFile(File file)                 // 用系统默认程序打开文件
copyFile(String source, String aim, boolean replace)  // 复制文件
delete(String source)               // 递归删除目录或文件
renameFile(File file, String name)  // 重命名文件
readFile(String filePath)           // 读取文件内容
getFileMD5(File file)               // 计算文件MD5值
```

#### 编码识别
```java
GetEncoding(String file)            // 自动识别文件编码（UTF-8/GBK/UTF-16等）
```

#### 行处理器模式
```java
readFile(File file, FileLineHandler handler, String encoding)  // 逐行处理文件
findFiles(String basePath, FileFilter... filters)              // 查找文件（支持递归）
```

### 3. 日期时间工具 (`DateUtils`)

#### 格式化
```java
toDateString(Date date)             // yyyy-MM-dd
toDateTimeString(Date date)         // yyyy-MM-dd HH:mm:ss
toDateTimeZonedString(Date date)    // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
getNowDateTime()                    // 当前日期时间
getNowDate()                        // 当前日期
getShortNowDate()                   // yyMMdd
getShortNowTime()                   // yyMMddHHmmss
```

#### 日期计算
```java
getDaysBetween(Date start, Date end)           // 计算间隔天数
getDateAfter(int days, Date date)              // 获取N天后的日期
getDateAfter(int amount, int field, Date date) // 按字段获取间隔日期
getDateTimeAfter(int days, String datetime)    // 获取N天后的日期字符串
getDateTimeAfterHours(int hours, String datetime) // 获取N小时后的时间
getDelayDate(Date date, Long delaytime, int workBeginHours, ...) // 获取延期时间（考虑工作时间）
```

#### 日期比较
```java
compareWithNowTime(String date)     // 与当前时间比较
compareWithNowDate(String date)     // 与当前日期比较
compareTime(String date1, String date2) // 时间比较
```

#### 周处理
```java
getNowWeekBegin()                   // 本周一零点
getNowWeekEnd()                     // 本周日23点
getWeekBegin(Date date)             // 指定日期所在周一
getWeek(String date)                // 转换为"MM月dd日 周X"格式
```

#### 文字描述
```java
getDateDetail(Date date, int level) // 转换为"今天 15点30分"等描述
getTimeString(Long t)               // 毫秒转换为"2小时30分15秒"
splitDate(Date begin, Date end, int step, int field, boolean asc) // 日期分段
```

### 4. 数学计算工具

#### BigDecimalWrapper（链式数值计算）
```java
// 链式操作，避免精度丢失
BigDecimalWrapper wrapper = new BigDecimalWrapper("100.5");
wrapper.add("50").multiply(2).divide(3).getString();  // 链式计算

// 支持配置
wrapper.scale(4)              // 设置精度
   .nullAsZero(true)          // null视为0
   .stripTrailingZeros(true)  // 去除尾部零
   .roundingMode(BigDecimal.ROUND_HALF_UP);

// 比较操作
wrapper.isZero()
wrapper.greatThanZero()
wrapper.equals("100")
```

#### CalculationUtils（表达式计算）
```java
evalExp("1+2*3")              // 计算表达式 → 7
evalExp("(1+2)*3")            // 支持括号 → 9
evalExp("5*(-3)")             // 支持负数 → -15
evalExp("100/3", 2)           // 指定精度 → 33.33
```

#### AllocationUtils（按权重分摊）
```java
// 按权重分摊数值（尾差自动处理）
List<TargetWeight<String>> weights = new ArrayList<>();
weights.add(new TargetWeight<>("A", BigDecimal.valueOf(30)));
weights.add(new TargetWeight<>("B", BigDecimal.valueOf(70)));

List<Result<String>> results = AllocationUtils.allocation(
    weights, 2, BigDecimal.valueOf(100)
);
// A: 30.00, B: 70.00（尾差自动分配给最后一项）
```

### 5. HTTP调用工具 (`HttpInvoker`)

#### 基本调用
```java
// GET请求
HttpInvoker.invoke("http://example.com/api", HttpInvoker.GET, handler);

// POST请求
HttpInvoker.invoke("http://example.com/api", HttpInvoker.POST, params, headers, handler);

// 使用参数构建器
HttpInvoker.invoke(new HttpInvokerParams()
    .baseUrl("http://example.com/api")
    .method(HttpInvoker.POST)
    .requestBody("{\"key\":\"value\"}")
    .bodyContentType("application/json")
    .connectTimeout(5000)
    .readTimeout(30000)
    .handler(handler)
);
```

#### 结果处理器
```java
HttpInvokeResultHandler handler = new HttpInvokeResultHandler() {
    @Override
    public void handle(HttpURLConnection connection, int code, BufferedReader reader) {
        // 处理响应（code以20开头为成功）
        if (reader != null) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    @Override
    public void handleException(Exception e) {
        // 处理异常
    }
};
```

### 6. RSA加密工具 (`RSAUtils`)

```java
// 生成密钥对
Map<String, String> keys = RSAUtils.createKeys(1024);
String publicKey = keys.get("publicKey");
String privateKey = keys.get("privateKey");

// 公钥加密、私钥解密
String encrypted = RSAUtils.publicEncrypt("明文", publicKey);
String decrypted = RSAUtils.privateDecrypt(encrypted, privateKey);

// 私钥加密、公钥解密
String encrypted = RSAUtils.privateEncrypt("明文", privateKey);
String decrypted = RSAUtils.publicDecrypt(encrypted, publicKey);

// 私钥签名、公钥验签
String sign = RSAUtils.sign("数据", privateKey);
boolean valid = RSAUtils.verify("数据", sign, publicKey);
```

### 7. 操作系统工具 (`OSUtils`)

```java
// 操作系统判断
OSUtils.isWindows()
OSUtils.isLinux()
OSUtils.isMacOS()
OSUtils.isMacOSX()

// 系统交互
OSUtils.browse(String url)           // 打开浏览器
OSUtils.setSysClipboardText(String) // 设置剪贴板内容
OSUtils.getSysClipboardText()       // 获取剪贴板内容
OSUtils.getDesktopPath()            // 获取桌面路径
```

### 8. 反射工具 (`ReflectUtils`)

```java
// 获取字段
Field field = ReflectUtils.getFieldByFieldName(obj, "fieldName");

// 获取/设置字段值
Object value = ReflectUtils.getValueByFieldName(obj, "fieldName");
ReflectUtils.setValueByFieldName(obj, "fieldName", value);

// 设置静态final字段
ReflectUtils.setStaticFinalValueByFieldName(Class, "fieldName", value);

// 获取所有字段
List<Field> fields = ReflectUtils.getFields(obj, true);  // includeSupers=true

// 扫描包下所有类
Set<Class<?>> classes = ReflectUtils.getClzFromPkg("com.example");
```

### 9. ANSI颜色工具 (`ColoredStringUtils`)

```java
// 文本颜色
ColoredStringUtils.colorString("hello", ColoredStringUtils.COLOR_RED);

// 文本+背景颜色
ColoredStringUtils.colorStringWithBackgroud("hello",
    ColoredStringUtils.COLOR_RED,
    ColoredStringUtils.COLOR_WHITE);

// 高亮显示
ColoredStringUtils.ligntColorString("hello", ColoredStringUtils.COLOR_GREEN);

// 高亮关键词
ColoredStringUtils.lightUpWords("hello world keyword", "keyword");  // 自动分配颜色
ColoredStringUtils.lightUpWords("hello world", COLOR_RED, "hello", "world");  // 指定颜色

// 显示选项
SHOW_OPPTION_UNDER_LINE   // 下划线
SHOW_OPPTION_TEXT_BLING   // 闪烁
SHOW_OPPTION_INVERSE      // 反白
```

### 10. Swing扩展组件

#### VFlowLayout（垂直流式布局）
```java
JPanel panel = new JPanel(new VFlowLayout());
panel.add(new JButton("按钮1"));
panel.add(new JButton("按钮2"));
// 按钮按垂直方向排列，支持自动换列

// 配置选项
new VFlowLayout(VFlowLayout.TOP, 5, 5, true, false);
// align: TOP/MIDDLE/BOTTOM
// hgap/vgap: 水平/垂直间距
// hfill: 水平填充
// vfill: 垂直填充
```

#### LineNumberHeaderView（行号视图）
```java
JTextArea textArea = new JTextArea();
JScrollPane scrollPane = new JScrollPane(textArea);
scrollPane.setRowHeaderView(new LineNumberHeaderView(textArea));
// 自动显示行号，跟随滚动
```

#### JSplitPaneExt（可调整分割面板）
```java
JSplitPaneExt splitPane = new JSplitPaneExt();
splitPane.addComponent(leftPanel, rightPanel, JSplitPane.HORIZONTAL_SPLIT);
splitPane.reSetDividerLocation();  // 窗口调整时保持比例
```

#### LimitativeDocument（长度限制文档）
```java
JTextArea textArea = new JTextArea();
textArea.setDocument(new LimitativeDocument(textArea));
// 限制输入长度
```

#### JOptionPaneExt（扩展对话框）
```java
JOptionPaneExt.showTextMessageDialog("标题", "长文本内容");
// 支持显示长文本的对话框
```

### 11. 文件行处理器 (`FileLineReader` + `FileLineHandler`)

```java
FileLineReader reader = new FileLineReader();
reader.setEncoding("UTF-8");

// 选择文件并处理
reader.ChooseFiles(new FileLineHandler() {
    @Override
    public boolean startScaleFile(File file) {
        System.out.println("开始处理: " + file.getName());
        return true;  // 返回false跳过此文件
    }

    @Override
    public boolean handleLine(File file, String preLine, String curLine,
                              String nextLine, int lineIndex) {
        // 处理当前行，返回false停止处理
        System.out.println("第" + lineIndex + "行: " + curLine);
        return true;
    }

    @Override
    public void handleException(File file, String preLine, String curLine,
                                String nextLine, int lineIndex, Exception e) {
        // 处理异常
    }

    @Override
    public void endScaleFile(File file) {
        // 文件处理完成
    }
});
```

### 12. 其他工具

#### ListSpliter（列表分割器）
```java
List<String> list = Arrays.asList("a", "b", "c", "d", "e");
ListSpliter<String> spliter = new ListSpliter<>(list, 2);  // 每次取2个

while (spliter.hasNext()) {
    List<String> part = spliter.next();
    System.out.println(part);  // [a,b], [c,d], [e]
}

// 内置死循环检测，hasNext()连续调用5次未调用next()会抛异常
```

#### FileOutWriter（文件输出）
```java
FileOutWriter writer = new FileOutWriter("/path/to/file.txt", false);  // append模式
writer.write("内容");
writer.writeLine("带换行");
writer.write(inputStream);  // 从输入流写入
writer.close();
```

#### ActiveCacheMap（活动缓存）
```java
// 带过期机制的缓存Map
```

#### CookiesUtils（Cookie工具）
```java
// Cookie解析与处理
```

#### CloneUtils（克隆工具）
```java
// 对象深拷贝
```

#### StackTracesUtils（堆栈工具）
```java
// 异常堆栈处理
```

## 模块结构

```
base-utils/
├── pom.xml                                    # Maven配置（无外部依赖）
└── src/main/java/idv/const_x/
    ├── utils/                                  # 工具类
    │   ├── StringExtUtils.java                # 字符串扩展工具
    │   ├── FileUtils.java                     # 文件工具
    │   ├── DateUtils.java                     # 日期时间工具
    │   ├── ReflectUtils.java                  # 反射工具
    │   ├── OSUtils.java                       # 操作系统工具
    │   ├── ColoredStringUtils.java            # ANSI颜色工具
    │   ├── RSAUtils.java                      # RSA加密工具
    │   ├── CalculationUtils.java              # 表达式计算
    │   ├── ListSpliter.java                   # 列表分割器
    │   ├── CookiesUtils.java                  # Cookie工具
    │   ├── CloneUtils.java                    # 克隆工具
    │   ├── StackTracesUtils.java              # 堆栈工具
    │   ├── RadomUtils.java                    # 随机工具
    │   ├── PubUtils.java                      # 公共工具
    │   ├── AnsiUtils.java                     # ANSI工具
    │   ├── ActiveCacheMap.java                # 活动缓存Map
    │   ├── CharsetRestorer.java               # 编码恢复
    │   └── MathUtils.java                     # 数学工具
    ├── math/                                   # 数学计算
    │   ├── BigDecimalWrapper.java             # BigDecimal链式包装器
    │   ├── MathUtils.java                     # 数学工具
    │   └ AllocationUtils.java                 # 权重分摊工具
    ├── file/                                   # 文件操作
    │   ├── FileOutWriter.java                 # 文件输出写入器
    │   └ line/
    │       ├── FileLineReader.java           # 文件行读取器
    │       └ handler/
    │           ├── FileLineHandler.java      # 行处理器接口
    │           └ FileLineSearchHandler.java  # 行搜索处理器
    ├── http/                                   # HTTP调用
    │   ├── HttpInvoker.java                   # HTTP调用器
    │   ├── HttpInvokerParams.java             # 参数构建器
    │   ├── HttpInvokeResultHandler.java       # 结果处理器接口
    │   ├── AbsInvokeResultHandler.java        # 结果处理器抽象类
    │   └ DefaultInvokeHandler.java           # 默认处理器
    ├── swing/                                  # Swing扩展组件
    │   ├── VFlowLayout.java                   # 垂直流式布局
    │   ├── LineNumberHeaderView.java          # 行号视图
    │   ├── JSplitPaneExt.java                 # 可调整分割面板
    │   ├── LimitativeDocument.java            # 长度限制文档
    │   ├── JOptionPaneExt.java                # 扩展对话框
    │   └ ProgressBarHandler.java             # 进度条处理器
    └── LogBuilder.java                        # 日志构建器
```

## 使用示例

### 1. 字符串处理
```java
// 中文统计
String text = "Hello世界";
int count = StringExtUtils.countChineseCharacters(text);  // 2

// 去除Emoji
String clean = StringExtUtils.removeEmojis("Hello😊World");

// 脱敏处理
String masked = StringExtUtils.sensitive("张三丰", 1, 1);  // 张*丰

// 字符串填充
String padded = StringExtUtils.fillStringLen("abc", 10, '-', 1);  // -------abc
String zhPadded = StringExtUtils.fillZhLen("中文", 6);  // 中文  （中文算2个宽度）
```

### 2. 文件处理
```java
// 自动识别编码读取文件
String encoding = FileUtils.GetEncoding("/path/to/file.txt");
FileUtils.readFile(file, handler, encoding);

// 计算MD5
String md5 = FileUtils.getFileMD5(new File("/path/to/file"));

// 查找文件
List<File> files = FileUtils.findFiles("/path/to/dir",
    new FileNameExtensionFilter("Java", "java"));
```

### 3. 日期处理
```java
// 获取间隔日期
String date = DateUtils.getDateTimeAfter(7, "2024-01-01 00:00:00");  // 2024-01-08 00:00:00

// 周处理
Date monday = DateUtils.getNowWeekBegin();

// 文字描述
String desc = DateUtils.getDateDetail(new Date(), 2);  // 今天 15点
String time = DateUtils.getTimeString(3661000L);       // 1小时1分1.000秒
```

### 4. 数学计算
```java
// 链式计算
BigDecimal result = new BigDecimalWrapper("100")
    .add(50).multiply(1.5).divide(3).get();  // 75

// 表达式计算
BigDecimal value = CalculationUtils.evalExp("(1+2)*3-4/2");  // 7

// 权重分摊
List<TargetWeight<User>> weights = Arrays.asList(
    new TargetWeight<>(user1, BigDecimal.valueOf(30)),
    new TargetWeight<>(user2, BigDecimal.valueOf(70))
);
List<Result<User>> results = AllocationUtils.allocation(weights, 2, BigDecimal.valueOf(100));
```

### 5. HTTP调用
```java
HttpInvoker.invoke(new HttpInvokerParams()
    .baseUrl("https://api.example.com/data")
    .method(HttpInvoker.GET)
    .encoding("UTF-8")
    .connectTimeout(10000)
    .handler(new DefaultInvokeHandler() {
        @Override
        public void handleSuccess(HttpURLConnection conn, BufferedReader reader) {
            System.out.println(reader.lines().collect(Collectors.joining("\n")));
        }
    })
);
```

### 6. RSA加密
```java
Map<String, String> keys = RSAUtils.createKeys(2048);
String encrypted = RSAUtils.publicEncrypt("敏感数据", keys.get("publicKey"));
String decrypted = RSAUtils.privateDecrypt(encrypted, keys.get("privateKey"));
```

### 7. Swing组件
```java
// 垂直布局
JPanel panel = new JPanel(new VFlowLayout(VFlowLayout.TOP, 5, 5, true, false));

// 带行号的文本区
JTextArea textArea = new JTextArea();
JScrollPane scroll = new JScrollPane(textArea);
scroll.setRowHeaderView(new LineNumberHeaderView(textArea));

// 可调整分割面板
JSplitPaneExt splitPane = new JSplitPaneExt();
splitPane.addComponent(leftPanel, rightPanel, JSplitPane.HORIZONTAL_SPLIT);
```

## 依赖说明

- **无外部依赖**：纯 Java 8 实现，仅使用 JDK 内置 API
