#!/bin/bash

# 获取脚本所在目录
script_dir="$(cd "$(dirname "$0")" && pwd)"
echo "Script directory: $script_dir"

# 设置 Java 类路径和扩展目录
java_ext_dirs="${script_dir}/../../target"
echo "Java ext dirs: $java_ext_dirs"

# 设置类路径
classpath="${java_ext_dirs}:${java_ext_dirs}/test-classes:${java_ext_dirs}/classes"

# 打印类路径以便调试
echo "Classpath: $classpath"

# 设置文件编码
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# 启动 Java 程序
if ! java -cp "$classpath" idv.const_x.console.JlineShellTest "$@"; then
    echo "Java command failed" >&2
    exit 1
fi
