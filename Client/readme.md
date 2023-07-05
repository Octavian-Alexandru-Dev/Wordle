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
javac -cp lib/* -d bin/ src/* && java -cp ".:./bin:./lib/*" Main

```

// =======================================================================

##### Creazione del file jar:

```bash
jar cvfm Client.jar META-INF/MANIFEST.MF -C bin/ .
```

##### compilazione e creazione del file jar:

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java &&
jar cvfm Client.jar META-INF/MANIFEST.MF -C bin/ . -C lib/ .
```

##### Esecuzione del file jar:

```bash
java -jar Client.jar
```

##### compilazione, creazione del file jar ed esecuzione:

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java &&
jar -cfm Client.jar META-INF/MANIFEST.MF -C bin/ .  -C lib/ . &&
java -jar Client.jar
```
