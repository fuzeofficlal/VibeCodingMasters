@echo off
echo ==========================================
echo  Starting Spring Boot Backend (port 8080)
echo ==========================================
cd /d "%~dp0"
call mvnw.cmd spring-boot:run
