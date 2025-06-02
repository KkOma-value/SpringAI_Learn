@echo off
REM Spring AI Learn 项目启动脚本 (Windows)
REM 作者: Spring AI Learn Team
REM 用途: 快速启动 Spring AI 学习项目

setlocal enabledelayedexpansion

REM 设置颜色代码
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM 标题
echo ========================================
echo      Spring AI Learn 项目启动脚本
echo ========================================
echo.

REM 解析命令行参数
if "%1"=="--help" goto :show_help
if "%1"=="--check-only" goto :check_only
if "%1"=="--build-only" goto :build_only
if "%1"=="--with-tests" set WITH_TESTS=true

REM 执行主流程
call :check_java
if errorlevel 1 exit /b 1

call :check_maven
if errorlevel 1 exit /b 1

call :check_config
if errorlevel 1 exit /b 1

call :check_postgresql

call :build_project
if errorlevel 1 exit /b 1

if "%WITH_TESTS%"=="true" call :run_tests

call :start_application
goto :eof

REM ========================================
REM 函数定义
REM ========================================

:print_info
echo %BLUE%[INFO]%NC% %~1
goto :eof

:print_success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

:check_java
call :print_info "检查 Java 环境..."

java -version >nul 2>&1
if errorlevel 1 (
    call :print_error "未找到 Java 环境，请先安装 Java 21+"
    exit /b 1
)

REM 检查Java版本
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do (
    set JAVA_VERSION=%%i
    set JAVA_VERSION=!JAVA_VERSION:"=!
)

REM 提取主版本号
for /f "tokens=1 delims=." %%a in ("!JAVA_VERSION!") do set JAVA_MAJOR=%%a

if !JAVA_MAJOR! LSS 21 (
    call :print_error "Java 版本过低: !JAVA_VERSION!，需要 Java 21+"
    exit /b 1
)

call :print_success "Java 版本检查通过: !JAVA_VERSION!"
goto :eof

:check_maven
call :print_info "检查 Maven 环境..."

mvn -version >nul 2>&1
if errorlevel 1 (
    call :print_error "未找到 Maven，请先安装 Maven 3.6+"
    exit /b 1
)

call :print_success "Maven 版本检查通过"
goto :eof

:check_config
call :print_info "检查配置文件..."

if not exist "src\main\resources\application-local.yml" (
    call :print_warning "未找到 application-local.yml 配置文件"
    
    if exist "src\main\resources\application-local.yml.example" (
        call :print_info "复制示例配置文件..."
        copy "src\main\resources\application-local.yml.example" "src\main\resources\application-local.yml" >nul
        call :print_warning "请编辑 src\main\resources\application-local.yml 文件，填入您的配置信息"
        call :print_warning "特别注意配置以下内容："
        call :print_warning "  - 数据库连接信息"
        call :print_warning "  - 阿里云通义千问 API Key"
        call :print_warning "  - 邮件服务配置（如需要）"
        call :print_warning "  - 搜索 API Key（如需要）"
        echo.
        pause
    ) else (
        call :print_error "未找到配置文件模板"
        exit /b 1
    )
) else (
    call :print_success "配置文件检查通过"
)
goto :eof

:check_postgresql
call :print_info "检查 PostgreSQL 连接（如果配置了的话）..."
call :print_warning "请确保 PostgreSQL 数据库正在运行（如果您配置了向量存储功能）"
goto :eof

:build_project
call :print_info "清理并编译项目..."

mvn clean compile -DskipTests
if errorlevel 1 (
    call :print_error "项目编译失败"
    exit /b 1
)

call :print_success "项目编译成功"
goto :eof

:run_tests
call :print_info "运行测试..."
mvn test
if errorlevel 1 (
    call :print_warning "测试失败，但继续启动..."
) else (
    call :print_success "测试通过"
)
goto :eof

:start_application
call :print_info "启动 Spring AI Learn 应用..."
call :print_info "应用将在 http://localhost:8088/api 启动"
call :print_info "API 文档地址: http://localhost:8088/api/swagger-ui.html"
call :print_info "健康检查地址: http://localhost:8088/api/health"
echo.
call :print_info "按 Ctrl+C 停止应用"
echo.

REM 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=local
goto :eof

:check_only
call :check_java
if errorlevel 1 exit /b 1

call :check_maven
if errorlevel 1 exit /b 1

call :check_config
if errorlevel 1 exit /b 1

call :check_postgresql

call :print_success "所有检查完成"
exit /b 0

:build_only
call :check_java
if errorlevel 1 exit /b 1

call :check_maven
if errorlevel 1 exit /b 1

call :build_project
if errorlevel 1 exit /b 1

call :print_success "项目编译完成"
exit /b 0

:show_help
echo Spring AI Learn 项目启动脚本 (Windows)
echo.
echo 用法: %~nx0 [选项]
echo.
echo 选项:
echo   --help              显示此帮助信息
echo   --with-tests        启动前运行测试
echo   --check-only        仅检查环境，不启动应用
echo   --build-only        仅编译项目，不启动应用
echo.
echo 环境变量:
echo   DASHSCOPE_API_KEY   阿里云通义千问 API 密钥
echo   SEARCH_API_KEY      搜索服务 API 密钥
echo   DATABASE_URL        数据库连接 URL
echo   DATABASE_USERNAME   数据库用户名
echo   DATABASE_PASSWORD   数据库密码
echo.
echo 示例:
echo   %~nx0                     # 正常启动
echo   %~nx0 --with-tests        # 运行测试后启动
echo   %~nx0 --check-only        # 仅检查环境
echo   set DASHSCOPE_API_KEY=your-key ^& %~nx0   # 设置环境变量并启动
echo.
echo 配置说明:
echo   1. 首次运行会自动创建 application-local.yml 配置文件
echo   2. 请根据提示编辑配置文件，填入必要的 API 密钥和数据库信息
echo   3. 确保已安装 Java 21+ 和 Maven 3.6+
echo   4. 如需使用向量存储功能，请确保 PostgreSQL 数据库正在运行
exit /b 0 