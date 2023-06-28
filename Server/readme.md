**compilazione:**

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java
```

**esecuzione:**

```bash
java -cp ".:./bin:./lib/*" Main
```

**compilazione ed esecuzione:**

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java && java -cp ".:./bin:./lib/*" Main
```
