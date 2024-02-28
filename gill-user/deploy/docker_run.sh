#!/bin/bash

ARGS=""
pattern="^\s*#"

# 读取 env.properties 文件中的属性，并生成对应的 -e 参数
while IFS='=' read -r key value; do
    # 如果行不以 '#' 开头且不是空行
    if [[ ! $key =~ $pattern && ${#key} -gt 1 ]]; then
        value=$(echo "$value" | tr -d '\n' | tr -d '\r')
        # 生成 -e 参数并保存到数组中
        ARGS="$ARGS -e \"$key=$value\""
    fi
done < env-self.properties


# 将生成的参数数组添加到 docker run 命令中
eval "docker run -d --name gill-user -p 9000:9000 -p 19000:19000 $ARGS gill-user:1.0.0"