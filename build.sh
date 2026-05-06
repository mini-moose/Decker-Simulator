find ./src/ -type f -name "*.java" > sources.txt
javac -d bin @sources.txt
java -cp bin main.Main