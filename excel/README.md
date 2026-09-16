# Excel 模块

基于 Apache POI 3.x 的 Excel 文件处理工具，提供 Excel 文件的读取、创建、合并等功能，支持样式定制、单元格渲染、数据校验等高级特性。

## 核心功能

### 1. Excel 文件创建 (`ExcelCreater`)

#### 支持的文件格式
- **XLS**：Excel 97-2003 格式（`HSSFWorkbook`）
- **XLSX**：Excel 2007+ 格式（`XSSFWorkbook`）
- **SXSSF**：流式 XLSX（大数据量场景，`SXSSFWorkbook`）

#### 核心功能
- **多表单管理**：创建、切换、保护、隐藏表单
- **表头定义**：自动创建表头行、冻结首行
- **数据写入**：行写入、批量写入、公式写入
- **样式控制**：普通样式、锁定样式、高亮样式
- **数据校验**：下拉列表约束（单元格下拉选择）
- **单元格注释**：支持为单元格添加批注
- **自动分页**：超过指定行数自动创建新表单
- **动态表单**：根据某列值自动创建对应表单
- **隐藏属性**：存储不可见的元数据属性
- **自适应列宽**：根据内容自动调整列宽

### 2. Excel 文件读取 (`ExcelReader`)

#### 核心功能
- **迭代读取**：`hasNext()` + `nextRow()` 逐行读取
- **表单切换**：按名称或索引切换表单
- **类型识别**：自动识别字符串、数值、布尔、日期类型
- **单元格更新**：支持修改单元格内容和样式
- **属性读取**：读取隐藏的元数据属性

### 3. Excel 文件合并 (`ExcelMerger`)

基于匹配规则将多个源 Excel 数据合并到目标 Excel 文件。

#### 核心概念
- **目标文件**：被更新的 Excel 文件
- **源文件**：提供数据的 Excel 文件
- **匹配器**：决定源行与目标行是否匹配的规则
- **源列映射**：定义源列 → 目标列的数据传递

## 元数据体系

### 列元数据 (`ExcelColumnMeta`)

定义单列的属性：
```java
ExcelColumnMeta field = new ExcelColumnMeta("列名", ExcelTypeEnum.TEXT);
field.setTips("列说明/提示");             // 鼠标悬停提示
field.setSelects(Arrays.asList("A","B")); // 下拉可选值
field.setCellRenderer(new MyRenderer());  // 单元格渲染器
field.setType(ExcelTypeEnum.NUMBER);      // 数据类型
```

**支持的数据类型** (`ExcelTypeEnum`)：
| 类型 | 说明 |
|------|------|
| TEXT | 文本（默认） |
| INTEGER | 整数 |
| NUMBER | 数值 |
| DATE | 日期 |
| BOOLEAN | 布尔 |
| FORMULA | 公式 |

### 表单元数据 (`ExcelSheetMeta`)

定义表单的属性：
```java
ExcelSheetMeta sheetMeta = new ExcelSheetMeta("表单名");
sheetMeta.setMaxRowPreSheet(50000);       // 单表单最大行数
sheetMeta.setSheetExcelColumnMeta(field); // 动态表单分割列
sheetMeta.addField(field1).addField(field2); // 添加列定义
```

## 单元格渲染器 (`AbsCellRenderer`)

可插拔的单元格值、注释、样式渲染机制。

### 抽象基类
```java
public abstract class AbsCellRenderer<T> {
    // 内容渲染（值转换）
    public abstract Object contentRender(T entity, Object value, int row);
    // 注释渲染（批注内容）
    public String commentRender(T entity, Object value, int row);
    // 样式渲染（自定义样式）
    public CellStyle cellStyleRender(T entity, Object value, Workbook book, IStyleGenerator style, int row);
}
```

### 内置渲染器
| 渲染器 | 功能 |
|--------|------|
| `BooleanValueRenderer` | 布尔值 → "是"/"否" |
| `NumberRenderer` | 数值精度控制（指定小数位数） |
| `ConstRenderer` | 返回常量值 |
| `RefValueRenderer` | 取对象的指定字段值 |

### 自定义渲染器示例
```java
ExcelColumnMeta price = new ExcelColumnMeta("售价", ExcelTypeEnum.TEXT);
price.setCellRenderer(new AbsCellRenderer<Object[]>() {
    @Override
    public Object contentRender(Object[] values, Object value, int row) {
        return "$" + value;  // 前缀美元符号
    }

    @Override
    public String commentRender(Object[] values, Object value, int row) {
        Double d = Double.valueOf(value.toString());
        if (d < 4.5) {
            return "售价过低";  // 低价格添加批注警告
        }
        return null;
    }

    @Override
    public CellStyle cellStyleRender(Object[] values, Object value, Workbook book, IStyleGenerator style, int row) {
        Double d = Double.valueOf(value.toString());
        if (d < 4.5) {
            return style.getHighlightCellStyle(price, row);  // 低价格高亮显示
        }
        return null;
    }
});
```

## 样式生成器 (`IStyleGenerator`)

可定制的表单样式策略。

### 接口定义
```java
public interface IStyleGenerator {
    CellStyle getHeadCellStyle(ExcelColumnMeta field);      // 表头样式
    CellStyle getCellStyle(ExcelColumnMeta field, int row); // 普通单元格样式
    CellStyle getLockedCellStyle(ExcelColumnMeta field, int row);  // 锁定样式
    CellStyle getHighlightCellStyle(ExcelColumnMeta field, int row); // 高亮样式
    boolean isDisplayGridlines();  // 是否显示网格线
    boolean isPrintGridlines();    // 打印是否显示网格线
}
```

### 内置样式生成器
| 样式生成器 | 特点 |
|------------|------|
| `DefaultStyleGenerator` | 默认样式（黄色表头、边框、居中对齐） |
| `GreenStyleGenerator` | 绿色主题（奇偶行不同颜色、绿表头、隐藏网格线） |

### 自定义样式示例
```java
creater.setStyle(GreenStyleGenerator.getInstance(creater));
```

## 行匹配器 (`ILineMatcher`)

用于 Excel 合并时判断源行与目标行是否匹配。

### 接口定义
```java
public interface ILineMatcher {
    boolean match(Object[] source, int sRowIdx, Object[] aim, int aRowIdx);
}
```

### 内置匹配器
| 匹配器 | 功能 |
|--------|------|
| `EqualToMatcher` | 指定列值相等即匹配 |
| `LineIndexMatcher` | 行索引相等即匹配 |
| `LineGroupMatcher` | 组合多个匹配器（AND/OR 逻辑） |

### 匹配器使用示例
```java
// 单列相等匹配
ILineMatcher matcher = new EqualToMatcher("E", "A");  // 源E列与目标A列比较

// 行索引匹配
ILineMatcher matcher = new LineIndexMatcher();  // 相同行号匹配

// 组合匹配
ILineMatcher matcher = new LineGroupMatcher(
    LineGroupMatcher.GROUP_LOGIC_AND,
    new EqualToMatcher(0, 0),
    new EqualToMatcher(1, 1)
);
```

## 模块结构

```
excel/
├── pom.xml                                    # Maven 配置（POI 3.10、commons-lang3）
└── src/main/java/idv/const_x/file/excel/
    ├── ExcelReader.java                       # Excel 读取器
    ├── ExcelCreater.java                      # Excel 创建器
    ├── ExcelMerger.java                       # Excel 合并器
    ├── ExcelColumnMeta.java                   # 列元数据定义
    ├── ExcelSheetMeta.java                    # 表单元数据定义
    ├── ExcelTypeEnum.java                     # 数据类型枚举
    ├── ExcelSign.java                         # 常量标识枚举
    ├── renderer/                              # 单元格渲染器
    │   ├── AbsCellRenderer.java              # 渲染器抽象基类
    │   ├── BooleanValueRenderer.java         # 布尔值渲染器
    │   ├── NumberRenderer.java               # 数值渲染器
    │   ├── ConstRenderer.java                # 常量渲染器
    │   └── RefValueRenderer.java             # 引用字段渲染器
    ├── matcher/                               # 行匹配器
    │   ├── ILineMatcher.java                 # 匹配器接口
    │   ├── EqualToMatcher.java               # 列值相等匹配器
    │   ├── LineIndexMatcher.java             # 行索引匹配器
    │   └── LineGroupMatcher.java             # 组合匹配器
    ├── style/                                 # 样式生成器
    │   ├── IStyleGenerator.java              # 样式生成器接口
    │   ├── DefaultStyleGenerator.java        # 默认样式生成器
    │   └── GreenStyleGenerator.java          # 绿色主题样式生成器
    └── utils/
        └── ExcelUtils.java                    # 工具类（列号转换）
```

## 使用示例

### 1. 创建 Excel 文件
```java
// 创建 XLSX 格式
ExcelCreater creater = new ExcelCreater(ExcelSign.EXCEL_TYPE_XLSX);

// 定义列
ExcelColumnMeta name = new ExcelColumnMeta("商品名称", ExcelTypeEnum.TEXT);
ExcelColumnMeta category = new ExcelColumnMeta("商品类别", ExcelTypeEnum.TEXT);
category.setSelects(Arrays.asList("日化", "食品"));  // 下拉选项

ExcelColumnMeta price = new ExcelColumnMeta("售价", ExcelTypeEnum.NUMBER);
price.setCellRenderer(new NumberRenderer(2));  // 保留2位小数

ExcelColumnMeta stock = new ExcelColumnMeta("库存", ExcelTypeEnum.INTEGER);
ExcelColumnMeta active = new ExcelColumnMeta("是否上架", ExcelTypeEnum.TEXT);
active.setCellRenderer(new BooleanValueRenderer());

// 定义表单
ExcelSheetMeta sheetMeta = new ExcelSheetMeta("商品列表");
sheetMeta.addField(name).addField(category).addField(price).addField(stock).addField(active);
sheetMeta.setMaxRowPreSheet(50000);  // 单表单最大行数

// 创建表单
creater.createSheet(sheetMeta);

// 设置样式
creater.setStyle(GreenStyleGenerator.getInstance(creater));

// 写入数据
creater.writeRow(new Object[]{"洗衣液", "日化", 14.45, 100, true});
creater.writeRow(new Object[]{"大米", "食品", 45.0, 50, false});

// 批量写入
Object[][] data = {
    new Object[]{"洗发水", "日化", 25.0, 200, true},
    new Object[]{"饼干", "食品", 8.5, 300, true}
};
creater.writeRows(data);

// 写入锁定单元格
creater.lockCurrentSheet("password123");
creater.writeLocked(creater.getCurrentIndex(), 2, "45.0", "不可修改");

// 写入高亮单元格
creater.writeHighlight(creater.getCurrentIndex(), 3, "0", "库存不足");

// 写入公式
String colName = ExcelUtils.columnNumberToName(2);
creater.writeFormula(creater.getCurrentIndex(), 5, "SUM(" + colName + "2)", "行公式");

// 保存文件
creater.write2File("/path/to/output.xlsx");
```

### 2. 读取 Excel 文件
```java
ExcelReader reader = new ExcelReader("/path/to/file.xlsx");

// 切换表单
reader.loadSheet("Sheet1");
reader.loadSheet(0);  // 按索引
reader.loadSheet("Sheet1", 1);  // 从第2行开始

// 逐行读取
while (reader.hasNext()) {
    Object[] row = reader.nextRow();
    String name = (String) row[0];
    Double price = (Double) row[1];
    // 处理数据...
    System.out.println("第" + reader.getCurrentIndex() + "行: " + name);
}

// 更新单元格
reader.updateCell(0, 0, "新值");
reader.updateCell(0, 1, "高亮值", HSSFColor.RED.index, HSSFColor.WHITE.index, true);

// 保存修改
reader.flush();
```

### 3. 合并 Excel 文件
```java
// 目标文件
ExcelMerger merger = new ExcelMerger("/path/to/aim.xlsx");

// 匹配器：源文件E列与目标文件A列相等时匹配
ILineMatcher matcher = new EqualToMatcher("E", "A");

// 源列映射：源F列 → 目标B列，源G列 → 目标C列
ExcelMerger.SourceColumn sc1 = new ExcelMerger.SourceColumn("F", "B");
ExcelMerger.SourceColumn sc2 = new ExcelMerger.SourceColumn(6, 2);  // 索引方式

// 添加源文件
merger.addSource("/path/to/source.xlsx", "Sheet1", matcher, sc1, sc2);

// 执行合并
merger.parse();
```

### 4. 动态分表
```java
// 根据类别列值自动创建对应表单
ExcelColumnMeta category = new ExcelColumnMeta("商品类别");
ExcelSheetMeta sheetMeta = new ExcelSheetMeta(category);  // 分表列
sheetMeta.addField(new ExcelColumnMeta("商品名称"));
sheetMeta.addField(category);
sheetMeta.addField(new ExcelColumnMeta("售价"));

creater.createSheet(sheetMeta);

// 写入数据时会自动创建"日化"、"食品"等表单
creater.writeRow(new Object[]{"洗衣液", "日化", 14.45});
creater.writeRow(new Object[]{"大米", "食品", 45.0});
```

## 工具类

### ExcelUtils
```java
// 列名转列号（A → 0, B → 1, AA → 26）
int colNum = ExcelUtils.columnNameToNumber("A");

// 列号转列名（0 → A, 1 → B, 26 → AA）
String colName = ExcelUtils.columnNumberToName(0);
```

## 依赖说明

- **Apache POI 3.10-FINAL**：Excel 文件操作核心库
  - `poi`：HSSF（XLS 格式）
  - `poi-ooxml`：XSSF（XLSX 格式）、SXSSF（流式）
- **Apache Commons Lang3 3.3.1**：字符串工具
- **base-utils**：文件操作工具

## 设计模式

- **策略模式**：`IStyleGenerator` 可切换不同样式策略
- **模板方法**：`AbsCellRenderer` 定义渲染流程，子类实现具体逻辑
- **建造者模式**：`ExcelSheetMeta.addField().addField()` 链式构建
- **工厂模式**：`DefaultStyleGenerator.getInstance()` 单例工厂
- **迭代器模式**：`ExcelReader.hasNext()/nextRow()` 逐行迭代

## 注意事项

1. **行数限制**：XLS 格式最大 65536 行，XLSX 格式建议不超过 150000 行（可配置自动分表）
2. **SXSSF 模式**：大数据量时使用流式写入，需指定内存保留行数
3. **样式缓存**：样式对象创建开销大，内置样式生成器已做缓存优化
4. **公式刷新**：包含公式的文件写入后会强制刷新公式计算