@echo off
echo ==========================================
echo  Starting Go API Gateway (port 8090)
echo ==========================================
cd /d "%~dp0gateway"
go run .
