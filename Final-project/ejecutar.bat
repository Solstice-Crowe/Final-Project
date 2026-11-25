@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist bin mkdir bin
dir /s /b src\*.java > .fuentes.txt
javac -encoding UTF-8 -d bin @.fuentes.txt && java -cp bin App
del .fuentes.txt
pause
