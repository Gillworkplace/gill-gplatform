# coturn服务器安装

## 依赖准备

```shell
yum install openssl-devel
yum install libevent-devel
```



## 安装包准备

```shell
mkdir -p /usr/local/coturn
cd /usr/local/coturn
wget https://github.com/coturn/coturn/archive/4.5.1.1.tar.gz
```



## 安装安装包

```shell
tar -zxvf 4.5.1.1.tar.gz
cd coturn-4.5.1.1
./configure
make
make install
```



## 准备TLS文件

```shell
cd /usr/local/etc
openssl req -x509 -newkey rsa:2048 -keyout /usr/local/etc/turn_server_pkey.pem -out /usr/local/etc/turn_server_cert.pem -days 99999 -nodes
# common name 填ip 或 域名
```



## 准备dh文件

```shell
openssl dhparam -out dhparam4096.pem 4096
```



## 配置coturn配置文件

```shell
# 获取内网IP
ifconfig | grep -A1 eth0 | grep inet | awk '{print $2}'

# 编写conf文件
vim turnserver.conf
```



```shell
# 内网IP
listening-ip=<内网IP>
# 监听端口
listening-port=<通讯端口>
# 带安全认证的 监听端口
tls-listening-port=<TLS认证的通讯端口>
# 外网IP
external-ip=<外网IP>
# dh 文件
dh-file=/usr/local/etc/dhparam4096.pem
fingerprint
lt-cred-mech
# turn 账号密码
user=<username>:<password>
realm=<外网IP>
mobility
verbose
```



## 云服务器开放端口

<通讯端口>

<TLS认证的通讯端口>



## 运行coturn

```shell
turnserver -c /usr/local/etc/turnserver.conf
```

