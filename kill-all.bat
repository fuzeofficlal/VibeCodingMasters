@echo off
echo =======================================================
echo          VibeCodingMaster Multi-Service Terminator
echo =======================================================
echo.

echo [1/3] Hunting Port 8090 (Go API Gateway)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8090') do (
    echo Terminating PID: %%a
    taskkill /F /T /PID %%a 2>nul
)

echo.
echo [2/3] Hunting Port 8000 (Python Market Data)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8000') do (
    echo Terminating PID: %%a
    taskkill /F /T /PID %%a 2>nul
)

echo.
echo [3/3] Hunting Port 8080 (Java Spring Boot)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Terminating PID: %%a
    taskkill /F /T /PID %%a 2>nul
)

echo.
echo [SUCCESS] System Terminated.
timeout /t 3
