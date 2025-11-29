@echo off
setlocal enabledelayedexpansion

REM Set Java 21 explicitly
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Verify Java version
echo Setting JAVA_HOME to: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -version

if "%1"=="rebuild" (
    echo.
    echo Stopping Java processes...
    taskkill /F /IM java.exe /T >nul 2>&1
    timeout /t 2 /nobreak
    
    echo Cleaning and rebuilding...
    call .\mvnw clean -q
    call .\mvnw compile -q -DskipTests
    
    echo Build complete! Starting application...
    call .\mvnw spring-boot:run
) else if "%1"=="build" (
    echo Building...
    call .\mvnw clean compile -q -DskipTests
    echo Build complete!
) else if "%1"=="run" (
    echo Starting Spring Boot...
    call .\mvnw spring-boot:run
) else if "%1"=="stop" (
    echo Stopping Java processes...
    taskkill /F /IM java.exe /T >nul 2>&1
    echo Stopped!
) else (
    echo Usage: build.bat [command]
    echo Commands:
    echo   rebuild - Clean, compile, and run
    echo   build   - Clean and compile only
    echo   run     - Run the application
    echo   stop    - Stop all Java processes
)
endlocal
