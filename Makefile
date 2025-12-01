.PHONY: help rebuild build run stop clean test docs

# Default shell
SHELL := /bin/zsh

# Java version requirement
JAVA_VERSION := 21

# Colors for output
BLUE := \033[0;34m
GREEN := \033[0;32m
RED := \033[0;31m
NC := \033[0m # No Color

help:
	@echo "$(BLUE)MecManager Backend - Build Commands$(NC)"
	@echo ""
	@echo "Usage: make [command]"
	@echo ""
	@echo "Commands:"
	@echo "  $(GREEN)rebuild$(NC)     - Clean, compile, and run the application"
	@echo "  $(GREEN)build$(NC)       - Clean and compile only (skip tests)"
	@echo "  $(GREEN)run$(NC)         - Run the application"
	@echo "  $(GREEN)clean$(NC)       - Clean build artifacts"
	@echo "  $(GREEN)test$(NC)        - Run tests"
	@echo "  $(GREEN)stop$(NC)        - Stop running Java processes"
	@echo "  $(GREEN)docs$(NC)        - Generate API documentation"
	@echo "  $(GREEN)help$(NC)        - Show this help message"
	@echo ""
	@echo "Examples:"
	@echo "  make rebuild    # Recommended for fresh start"
	@echo "  make run        # Just run the app"
	@echo "  make test       # Run unit tests"

verify-java:
	@echo "$(BLUE)Verifying Java installation...$(NC)"
	@java -version
	@echo "$(GREEN)✓ Java verified$(NC)"

rebuild: verify-java clean
	@echo "$(BLUE)Cleaning and rebuilding...$(NC)"
	@./mvnw clean -q
	@./mvnw compile -q -DskipTests
	@echo "$(GREEN)✓ Build complete!$(NC)"
	@echo "$(BLUE)Starting application...$(NC)"
	@./mvnw spring-boot:run

build: verify-java
	@echo "$(BLUE)Building...$(NC)"
	@./mvnw clean compile -q -DskipTests
	@echo "$(GREEN)✓ Build complete!$(NC)"

run:
	@echo "$(BLUE)Starting Spring Boot application...$(NC)"
	@echo "$(BLUE)Access at: http://localhost:8080$(NC)"
	@echo "$(BLUE)Swagger UI: http://localhost:8080/swagger-ui/index.html$(NC)"
	@./mvnw spring-boot:run

clean:
	@echo "$(BLUE)Cleaning build artifacts...$(NC)"
	@./mvnw clean -q
	@rm -rf target/
	@echo "$(GREEN)✓ Clean complete!$(NC)"

test:
	@echo "$(BLUE)Running tests...$(NC)"
	@./mvnw test
	@echo "$(GREEN)✓ Tests complete!$(NC)"

docs:
	@echo "$(BLUE)Generating API documentation...$(NC)"
	@./mvnw clean compile -q -DskipTests
	@echo "$(GREEN)✓ API docs generated!$(NC)"
	@echo "$(BLUE)Access Swagger UI at: http://localhost:8080/swagger-ui/index.html$(NC)"

stop:
	@echo "$(BLUE)Stopping Java processes...$(NC)"
	@pkill -f "java.*spring-boot" || echo "No running Java processes found"
	@sleep 1
	@echo "$(GREEN)✓ Stopped!$(NC)"

install-deps:
	@echo "$(BLUE)Installing Maven wrapper...$(NC)"
	@chmod +x ./mvnw
	@echo "$(GREEN)✓ Dependencies ready!$(NC)"

format:
	@echo "$(BLUE)Formatting code...$(NC)"
	@./mvnw spotless:apply -q
	@echo "$(GREEN)✓ Code formatted!$(NC)"

compile-only:
	@echo "$(BLUE)Compiling only (no tests)...$(NC)"
	@./mvnw compile -q -DskipTests
	@echo "$(GREEN)✓ Compilation complete!$(NC)"

package:
	@echo "$(BLUE)Building JAR package...$(NC)"
	@./mvnw package -q -DskipTests
	@echo "$(GREEN)✓ JAR built: target/mecManager-0.0.1-SNAPSHOT.jar$(NC)"

debug:
	@echo "$(BLUE)Running in debug mode (port 5005)...$(NC)"
	@./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Default target
.DEFAULT_GOAL := help
