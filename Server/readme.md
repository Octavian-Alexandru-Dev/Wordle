##### compilazione:

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java
```

##### esecuzione:

```bash
java -cp ".:./bin:./lib/*" Main
```

##### compilazione ed esecuzione:

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java && java -cp ".:./bin:./lib/*" Main
```

##### Creazione del file jar:

```bash
jar cvfm bin/Server.jar META-INF/MANIFEST.MF -C bin/ .
```

##### compilazione e creazione del file jar:

```bash
javac -cp ".:./lib/*" -d ./bin ./src/*.java && jar cvfm bin/Server.jar META-INF/MANIFEST.MF -C bin/ . -C lib/ .
```

##### Esecuzione del file jar:

```bash
java -jar bin/Server.jar
```

##### compilazione, creazione del file jar ed esecuzione:

```bash
javac -cp ".:./lib/*:./gson-2.2.1.jar" -d ./bin ./src/*.java &&
jar -cfvm bin/Server.jar META-INF/MANIFEST.MF -C bin/ .  -C lib/ . &&
java -jar bin/Server.jar
```
