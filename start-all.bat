@echo off
setlocal
echo =======================================================
echo          VibeCodingMaster Multi-Service Launcher
echo =======================================================

REM 1. Start Java Spring Boot Application
echo.
echo [1/3] Starting Spring Boot Backend (Port 8080)...
start "Spring Boot Backend" cmd /k "mvnw.cmd spring-boot:run"
REM Give Java some time to initialize
timeout /t 8 /nobreak > nul

REM 2. Start Go API Gateway
echo.
echo [2/3] Starting Go API Gateway (Port 8090)...
cd gateway
start "Go API Gateway" cmd /k "go run main.go"
cd ..
timeout /t 2 /nobreak > nul

REM 3. Start Python Market Data
echo.
echo [3/3] Starting Python FastAPI Market Service (Port 8000)...
cd market-service
start "Python Market Microservice" cmd /k ".\venv\Scripts\python.exe -m uvicorn main:app --port 8000"
cd ..

echo.
echo =======================================================
echo [SUCCESS] All services have been launched!
echo.
echo Endpoints:
echo    - Core API Gateway:     http://localhost:8090
echo    - Java Direct Backend:  http://localhost:8080
echo    - Python Market Data:   http://localhost:8000
echo    - Frontend UI:          http://localhost:8090/
echo =======================================================
pause
