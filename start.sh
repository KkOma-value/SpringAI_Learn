#!/bin/bash

# Spring AI Learn 项目启动脚本
# 作者: Spring AI Learn Team
# 用途: 快速启动 Spring AI 学习项目

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Java 版本
check_java() {
    print_info "检查 Java 环境..."
    
    if ! command -v java &> /dev/null; then
        print_error "未找到 Java 环境，请先安装 Java 21+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d. -f1)
    
    if [ "$JAVA_MAJOR_VERSION" -lt 21 ]; then
        print_error "Java 版本过低: $JAVA_VERSION，需要 Java 21+"
        exit 1
    fi
    
    print_success "Java 版本检查通过: $JAVA_VERSION"
}

# 检查 Maven
check_maven() {
    print_info "检查 Maven 环境..."
    
    if ! command -v mvn &> /dev/null; then
        print_error "未找到 Maven，请先安装 Maven 3.6+"
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven 版本检查通过: $MVN_VERSION"
}

# 检查配置文件
check_config() {
    print_info "检查配置文件..."
    
    if [ ! -f "src/main/resources/application-local.yml" ]; then
        print_warning "未找到 application-local.yml 配置文件"
        
        if [ -f "src/main/resources/application-local.yml.example" ]; then
            print_info "复制示例配置文件..."
            cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
            print_warning "请编辑 src/main/resources/application-local.yml 文件，填入您的配置信息"
            print_warning "特别注意配置以下内容："
            print_warning "  - 数据库连接信息"
            print_warning "  - 阿里云通义千问 API Key"
            print_warning "  - 邮件服务配置（如需要）"
            print_warning "  - 搜索 API Key（如需要）"
            
            read -p "配置完成后，按回车键继续..." -r
        else
            print_error "未找到配置文件模板"
            exit 1
        fi
    else
        print_success "配置文件检查通过"
    fi
}

# 检查 PostgreSQL（可选）
check_postgresql() {
    print_info "检查 PostgreSQL 连接（如果配置了的话）..."
    
    # 这里可以添加 PostgreSQL 连接检查逻辑
    print_warning "请确保 PostgreSQL 数据库正在运行（如果您配置了向量存储功能）"
}

# 清理和编译
build_project() {
    print_info "清理并编译项目..."
    
    mvn clean compile -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "项目编译成功"
    else
        print_error "项目编译失败"
        exit 1
    fi
}

# 运行测试（可选）
run_tests() {
    if [ "$1" = "--with-tests" ]; then
        print_info "运行测试..."
        mvn test
        
        if [ $? -eq 0 ]; then
            print_success "测试通过"
        else
            print_warning "测试失败，但继续启动..."
        fi
    fi
}

# 启动应用
start_application() {
    print_info "启动 Spring AI Learn 应用..."
    print_info "应用将在 http://localhost:8088/api 启动"
    print_info "API 文档地址: http://localhost:8088/api/swagger-ui.html"
    print_info "健康检查地址: http://localhost:8088/api/health"
    print_info ""
    print_info "按 Ctrl+C 停止应用"
    print_info ""
    
    # 启动应用，传递所有环境变量
    mvn spring-boot:run -Dspring-boot.run.profiles=local
}

# 帮助信息
show_help() {
    echo "Spring AI Learn 项目启动脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --help              显示此帮助信息"
    echo "  --with-tests        启动前运行测试"
    echo "  --check-only        仅检查环境，不启动应用"
    echo "  --build-only        仅编译项目，不启动应用"
    echo ""
    echo "环境变量:"
    echo "  DASHSCOPE_API_KEY   阿里云通义千问 API 密钥"
    echo "  SEARCH_API_KEY      搜索服务 API 密钥"
    echo "  DATABASE_URL        数据库连接 URL"
    echo "  DATABASE_USERNAME   数据库用户名"
    echo "  DATABASE_PASSWORD   数据库密码"
    echo ""
    echo "示例:"
    echo "  $0                  # 正常启动"
    echo "  $0 --with-tests     # 运行测试后启动"
    echo "  $0 --check-only     # 仅检查环境"
    echo "  DASHSCOPE_API_KEY=your-key $0  # 设置环境变量并启动"
}

# 主函数
main() {
    echo "========================================"
    echo "     Spring AI Learn 项目启动脚本"
    echo "========================================"
    echo ""
    
    # 解析命令行参数
    case "$1" in
        --help)
            show_help
            exit 0
            ;;
        --check-only)
            check_java
            check_maven
            check_config
            check_postgresql
            print_success "所有检查完成"
            exit 0
            ;;
        --build-only)
            check_java
            check_maven
            build_project
            print_success "项目编译完成"
            exit 0
            ;;
        --with-tests)
            WITH_TESTS="--with-tests"
            ;;
        *)
            if [ -n "$1" ]; then
                print_error "未知选项: $1"
                show_help
                exit 1
            fi
            ;;
    esac
    
    # 执行检查和启动流程
    check_java
    check_maven
    check_config
    check_postgresql
    build_project
    run_tests $WITH_TESTS
    
    print_success "所有检查完成，正在启动应用..."
    echo ""
    
    start_application
}

# 捕获 Ctrl+C 信号
trap 'print_info "正在停止应用..."; exit 0' INT

# 运行主函数
main "$@" 