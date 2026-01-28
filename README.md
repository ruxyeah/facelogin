# 人脸识别登录系统

基于 Spring Boot + 百度 AI 的人脸识别登录系统，支持人脸登录、口罩识别、账号密码登录及人脸信息管理。

## 功能特性

- **人脸识别登录** - 通过摄像头实时采集人脸进行身份验证
- **口罩识别支持** - 自动检测口罩并调整识别阈值
- **账号密码登录** - 传统登录方式作为备选
- **人脸信息管理** - 支持人脸注册、更新、删除、查询
![ScreenShot_2026-01-28_092657_561.png](docs%2FScreenShot_2026-01-28_092657_561.png)
![ScreenShot_2026-01-28_092738_932.png](docs%2FScreenShot_2026-01-28_092738_932.png)
![ScreenShot_2026-01-28_092805_864.png](docs%2FScreenShot_2026-01-28_092805_864.png)
## 技术栈

| 类别 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.2.5 |
| 模板引擎 | Thymeleaf 3.0 |
| 人脸识别 | 百度 AI 人脸识别 API |
| 前端 | Bootstrap + jQuery |
| JSON处理 | Gson + FastJSON |
| JDK版本 | Java 1.8+ |

## 项目结构

```
src/main/java/com/example/facelogin/
├── Controller/
│   ├── LoginController.java      # 登录控制器
│   └── FaceController.java       # 人脸管理控制器
├── Service/
│   ├── LoginService.java         # 登录业务逻辑
│   └── BaiduTokenService.java    # 百度API令牌服务
├── Face/
│   └── BaiduAIFace.java          # 百度人脸识别封装
├── config/
│   └── BaiduFaceProperties.java  # 配置属性类
├── Model/
│   └── Setingmodel.java          # 人脸操作参数模型
├── SetingModel/
│   └── Setingmodel.java          # 人脸搜索参数模型
├── Utils/
│   ├── HttpUtil.java             # HTTP请求工具
│   ├── GsonUtils.java            # JSON工具
│   └── Base64Util.java           # Base64编码工具
└── FaceloginApplication.java     # 启动类

src/main/resources/
├── templates/
│   ├── index.html                # 登录页面
│   ├── getface.html              # 人脸识别页面
│   └── succeed.html              # 管理中心页面
├── static/asserts/               # 静态资源
└── application.properties        # 应用配置
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.x
- 现代浏览器（Chrome/Firefox/Edge）
- 摄像头设备

### 2. 配置百度AI

1. 访问 [百度AI开放平台](https://ai.baidu.com/) 注册账号
2. 创建人脸识别应用，获取 API Key 和 Secret Key
3. 在人脸库中创建分组（记录 Group ID）

### 3. 修改配置

编辑 `src/main/resources/application.properties`：

```properties
# 百度API密钥（替换为你的密钥）
baidu.face.api-key=你的API_KEY
baidu.face.secret-key=你的SECRET_KEY
baidu.face.group-id=你的GROUP_ID

# 识别阈值
baidu.face.mask-threshold=70      # 口罩模式阈值
baidu.face.normal-threshold=80    # 正常模式阈值
```

### 4. 启动项目

```bash
# 克隆项目
git clone <repository-url>
cd Face-recognition-login

# 编译运行
mvn spring-boot:run
```

### 5. 访问系统

打开浏览器访问：`http://localhost:8888`

> **重要提示**：摄像头功能需要通过 HTTPS 或 localhost 访问。
> 如需通过 IP 访问，请参考下方"常见问题"章节。

## 使用说明

### 登录方式

| 方式 | 说明 |
|------|------|
| 人脸登录 | 点击"开始人脸识别"，正视摄像头自动识别 |
| 账号登录 | 默认账号：`admin`，密码：`123123` |

### 管理功能

登录成功后进入管理中心，可使用以下功能：

| 功能 | 说明 |
|------|------|
| 人脸录入 | 输入用户ID，拍照注册新用户人脸 |
| 人脸更新 | 更新已注册用户的人脸数据 |
| 用户列表 | 查看当前分组中所有已注册用户 |
| 删除人脸 | 删除指定用户的人脸数据 |

### 口罩识别

系统会自动检测用户是否佩戴口罩：
- **未戴口罩**：识别阈值 80 分，最多重试 3 次
- **佩戴口罩**：识别阈值 70 分，最多重试 5 次

## API 接口

### 登录接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/login/password` | POST | 账号密码登录 |
| `/login/searchface` | POST | 人脸搜索识别 |
| `/login/jumpGetface` | GET | 跳转人脸识别页 |
| `/login/facesucceed` | GET | 登录成功页 |

### 人脸管理接口

| 接口 | 方法 | 参数 | 说明 |
|------|------|------|------|
| `/face/register` | POST | userId, imageBase64 | 注册人脸 |
| `/face/update` | POST | userId, imageBase64 | 更新人脸 |
| `/face/delete` | POST | userId | 删除人脸 |
| `/face/userlist` | GET | - | 获取用户列表 |

## 配置说明

### 完整配置项

```properties
# ===================================
# 服务器配置
# ===================================
server.port=8888
server.max-http-header-size=1000KB

# ===================================
# 百度人脸识别API配置
# ===================================
# API密钥
baidu.face.api-key=your_api_key
baidu.face.secret-key=your_secret_key
baidu.face.group-id=your_group_id

# API地址配置
baidu.face.base-url=https://aip.baidubce.com
baidu.face.face-api-prefix=/rest/2.0/face/v3
baidu.face.token-path=/oauth/2.0/token

# API端点路径
baidu.face.detect-path=/detect
baidu.face.search-path=/search
baidu.face.register-path=/faceset/user/add
baidu.face.update-path=/faceset/user/update
baidu.face.delete-path=/faceset/face/delete
baidu.face.face-list-path=/faceset/face/getlist
baidu.face.group-users-path=/faceset/group/getusers

# 识别阈值
baidu.face.mask-threshold=70
baidu.face.normal-threshold=80
```

## 常见问题

### 1. 摄像头无法打开

**原因**：浏览器的 `getUserMedia` API 仅在安全上下文（HTTPS 或 localhost）中可用。

**解决方案**：

方法一：使用 localhost 访问
```
http://localhost:8888
```

方法二：Chrome 添加不安全源白名单
1. 访问 `chrome://flags/#unsafely-treat-insecure-origin-as-secure`
2. 添加你的 IP 地址，如：`http://192.168.1.100:8888`
3. 重启浏览器

方法三：配置 HTTPS（生产环境推荐）

### 2. 人脸识别失败

- 确保光线充足
- 正视摄像头，保持面部在识别框内
- 检查百度AI控制台人脸库是否有数据
- 查看后台日志确认API响应

### 3. 注册提示用户已存在

该用户ID已注册过人脸，请使用"人脸更新"功能。

## 技术细节

### 识别流程

```
1. 前端采集图像 → Base64编码
2. 发送到后端 /login/searchface
3. 调用百度人脸检测API → 判断口罩状态
4. 调用百度人脸搜索API → 获取匹配结果
5. 根据阈值判断 → 返回识别结果
6. 前端根据结果 → 跳转或重试
```

### 安全说明

- 默认账号密码仅供演示，生产环境请接入数据库
- API密钥请妥善保管，建议使用环境变量
- 建议生产环境启用HTTPS

## License

MIT License

## 致谢

- [百度AI开放平台](https://ai.baidu.com/) - 人脸识别API
- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [Bootstrap](https://getbootstrap.com/) - 前端框架
