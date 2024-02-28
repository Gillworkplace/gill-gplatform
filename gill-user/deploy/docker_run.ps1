# 读取 env.properties 文件中的属性，并生成对应的 -e 参数
$var = ""
Get-Content -Path "env-self.properties" | ForEach-Object {

    # 如果行不以 '#' 开头且不是空行
    if ($_ -notmatch '^\s*#' -and $_ -ne '') {
        # 提取键值对
        $key, $value = $_ -split '=', 2
        # 输出提取的键值对
        # 生成 -e 参数并保存到数组中
        $var += " -e" + " $key=$value"
    }
}

Write-Host "$var"

# 将生成的参数数组添加到 docker run 命令中
Invoke-Expression "docker run -d --name gill-user -p 9000:9000 -p 19000:19000 $var gill-user:1.0.0"