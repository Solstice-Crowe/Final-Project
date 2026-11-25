#!/bin/bash
# Compila y ejecuta el sistema (Linux / macOS)
cd "$(dirname "$0")" || exit 1
mkdir -p bin
find src -name "*.java" > .fuentes.txt
javac -encoding UTF-8 -d bin @.fuentes.txt && java -cp bin App
rm -f .fuentes.txt
