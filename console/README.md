# Console 模块

基于 JLine 3 和 Jansi 的交互式命令行 Shell 框架，提供丰富的终端交互能力，包括命令解析、Tab 补全、彩色输出、进度条显示等功能。

## 核心功能

### 1. 交互式 Shell 框架 (`JlineShell`)

- **命令注册机制**：通过 `registryHandler()` 注册自定义命令处理器
- **Tab 补全**：自动收集已注册命令，支持参数和选项的智能补全
- **命令解析**：自动解析参数和选项（支持 `-option:value` 格式）
- **参数智能分割**：支持引号包裹的参数（如 `'hello world'`）
- **内置命令**：`clear`（清屏）、`debug`（调试模式切换）、`exit`（退出）

### 2. 命令处理器体系 (`InputLineHandler`)

#### 接口定义
```java
public interface InputLineHandler {
    String getCmd();                           // 命令名称
    void handleLine(ConsoleIOProxy console, String[] args);  // 处理逻辑
    String getHelp();                          // 帮助信息
    FirstLimitedCompleter getCompleter();      // 补全器
    boolean enable();                          // 是否启用
}
```

#### 抽象基类 `AbsInputLineHandler`
- 自动解析参数（`params`）和选项（`options`）
- 提供参数/选项获取方法：`getParam()`, `getOptions()`, `hasOptions()`
- 支持必填参数校验
- 自动生成帮助信息（基于 `HelpBuilder`）

### 3. 补全器 (`Completer`)

#### `FirstLimitedCompleter`
- 抽象基类，限定仅当用户输入指定 root 命令后才触发补全

#### `StringTreeCompleter`
- 支持树状结构的命令补全
- 按位置索引添加候选值
- 支持自由选项（全局可用，不重复提示）
- 可配置是否忽略 `-` 开头的选项 token

### 4. 控制台 I/O 代理 (`ConsoleIOProxy`)

#### 日志级别输出
```java
console.debug("调试信息");    // DEBUG 级别（蓝色）
console.info("普通信息");     // INFO 级别（绿色）
console.warning("警告信息");  // WARN 级别（黄色）
console.error("错误信息");    // ERROR 级别（红色）
console.important("重要信息"); // 红底白字高亮
```

#### 彩色输出
- 支持前景色和背景色设置
- 支持 ANSI 渲染语法：`@|red Hello|@`
- 预定义颜色枚举：`ConsoleColorEnum`（BLACK/BLUE/CYAN/GREEN/MAGENTA/RED/YELLOW/WHITE，及其亮色版本）

#### 表格打印
```java
List<String> headers = Arrays.asList("列1", "列2");
List<List<Object>> rows = ...;
console.printTable(headers, rows);              // 带表头表格
console.printTable(rows);                       // 无表头表格
console.printTable(headers, rows, "keyword");   // 高亮关键词
```

#### 进度条
```java
ProgressBarHandler bar = console.createProgressBar();
bar.progress("处理中...");                      // 动态滚动进度条
bar.progress(step, total, "进度描述");          // 精确进度条（显示百分比、剩余时间）
bar.completed("完成");                          // 完成状态
bar.shutdown("中断");                           // 中断状态
```

#### 交互式输入
```java
String input = console.waittingInput("请输入");                    // 等待输入
String input = console.waittingInput("请选择", "Y", "N");          // 限定选项
boolean confirmed = console.waittingComfirm("确认吗？");           // 确认对话框
T choice = console.waittingChoose(values, "请选择");              // 选择列表
```

### 5. 命令帮助构建器 (`HelpBuilder`)

链式构建命令帮助信息：
```java
new HelpBuilder("cmd", "命令描述")
    .appendParamDesc("param1", "参数描述", true)       // 必填参数
    .appendParamDesc("param2", "参数描述", false)      // 可选参数
    .appendOptionDesc("v", "详细模式")                  // 无值选项
    .appendOptionDesc("f", "文件路径", true)           // 需值选项
    .appendOptionDesc("type", "类型", "A", "B", "C")   // 带可选值选项
    .build();
```

## 模块结构

```
console/
├── pom.xml                                    # Maven 配置（依赖 JLine 3、Jansi、base-utils）
└── src/main/java/idv/const_x/
    ├── ansi/                                   # ANSI 终端支持
    │   ├── ConsoleColorEnum.java              # 颜色枚举
    │   ├── ConsoleIOProxy.java                # 控制台 I/O 代理（核心输出类）
    │   └── Inputer.java                       # 输入接口
    └── console/
        ├── JlineShell.java                    # Shell 主框架（单例）
        ├── completer/                          # 补全器
        │   ├── FirstLimitedCompleter.java     # 补全器基类
        │   ├── StringTreeCompleter.java       # 树状补全器
        │   └── TestCompleter.java             # 测试补全器（调试用）
        └── handler/                            # 命令处理器
            ├── InputLineHandler.java          # 处理器接口
            ├── AbsInputLineHandler.java       # 处理器抽象基类
            ├── HelpBuilder.java               # 帮助信息构建器
            ├── DemoHandler.java               # 功能演示处理器
            ├── CalculateHandler.java          # 数学计算处理器
            ├── JsonFormatterHandler.java      # JSON 格式化处理器
            └── CmdHandler.java                # 系统命令处理器
```

## 使用示例

### 1. 启动 Shell
```java
JlineShell shell = JlineShell.getInstance();
shell.registryHandler(new MyHandler());
shell.run(args);  // args 可作为初始命令直接执行
```

### 2. 创建自定义处理器
```java
public class MyHandler extends AbsInputLineHandler {

    @Override
    public String getCmd() {
        return "mycmd";
    }

    @Override
    public void handleLine(String line, ConsoleIOProxy console,
                           Map<String, String> options, String... params) {
        String arg1 = getParam(params, 0);  // 获取第一个参数
        String opt = getOptions(options, "v");  // 获取 -v 选项值
        console.println("执行结果: " + arg1);
    }

    @Override
    public HelpBuilder getHelpBuilder() {
        return new HelpBuilder(getCmd(), "我的命令")
                .appendParamDesc("arg1", "参数说明")
                .appendOptionDesc("v", "详细模式")
                .build();
    }

    @Override
    public FirstLimitedCompleter getCompleter() {
        return new StringTreeCompleter(getCmd())
                .addSubStrings(1, "option1", "option2");  // 第一参数补全
    }
}
```

### 3. 运行测试
```java
// console/src/test/java/idv/const_x/console/JlineShellTest.java
public static void main(String[] args) {
    JlineShell shell = JlineShell.getInstance();
    shell.registryHandler(new DemoHandler());
    shell.registryHandler(new CalculateHandler());
    shell.registryHandler(new JsonFormatterHandler());
    shell.run(args);
}
```

## 内置处理器说明

| 命令 | 功能 | 示例 |
|------|------|------|
| `hello` | 功能演示（交互、进度条、表格等） | `hello world` |
| `cal` | 数学表达式计算 | `cal (1+2)*3` |
| `json` | 打开 JSON 格式化页面 | `json -c`（使用剪贴板内容） |
| `cmd` | 系统 Shell 命令（Windows: cmd, Linux: sh） | `cmd dir` |
| `clear` | 清屏 | `clear` |
| `debug` | 切换调试模式 | `debug on` / `debug off` |
| `exit` | 退出 Shell | `exit` |
| `?` | 显示帮助 | `?` 或 `命令 ?` |

更多处理器可直接引入
<dependency>
    <groupId>idv.constx</groupId>
    <artifactId>console.ext</artifactId>
</dependency>

## 依赖说明

- **JLine 3.9.0**：终端输入处理、命令行编辑、Tab 补全
- **Jansi 1.11**：ANSI 转义序列支持（彩色输出）
- **base-utils**：基础工具类（字符串处理、颜色工具等）

