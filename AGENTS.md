# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

BMD RegKassenTestTool is a JavaFX 17 desktop application for testing Austrian cash register (Registrierkasse) data compliance (RKSV). It has GUI mode and a CLI batch mode.

### System dependencies

- **JDK 21** (pre-installed; project targets Java 17 source/target, runs fine on 21)
- **Maven 3.8+** (install via `sudo apt-get install -y maven` if missing)
- **Xvfb** (install via `sudo apt-get install -y xvfb` for headless JavaFX; start with `Xvfb :99 -screen 0 1920x1080x24 &` and `export DISPLAY=:99`)

### Build and run

- **Build:** `mvn clean package -DskipTests` (no test sources exist in `src/test/`)
- **Run GUI (headless):** `java -cp "target/BMD_RegKassenTestTool-1.0-SNAPSHOT.jar:lib/bcprov-jdk15on-1.52.jar:lib/bcpkix-jdk15on-1.52.jar" com.bmd_regkassentesttool.MainLauncher`
- **Run batch mode:** same command with CLI arguments (e.g. `--dep <file> --key <file>`)
- **Lint/compile check:** `mvn compile` (there are no separate lint or test tasks)

### Known caveats

- The `javafx-maven-plugin` config in `pom.xml` references an incorrect module/class name (`com.bmd_regkassentesttool.bmd_regkassentesttool/com.bmd_regkassentesttool.HelloApplication`). Use `java -cp` to run the shaded JAR directly instead of `mvn javafx:run`.
- The `config.properties` file has hardcoded Windows paths (`D:\BMD\...`). These are loaded at runtime for file history; the app works on Linux but stored paths will be invalid.
- BouncyCastle runtime JARs in `lib/` are version 1.52 while the Maven dependency was updated to 1.70 for JPMS compilation. The shaded JAR excludes BouncyCastle (`maven-shade-plugin` config), so `lib/bcprov-jdk15on-1.52.jar` and `lib/bcpkix-jdk15on-1.52.jar` must be on the classpath at runtime.
- The build produces deprecation and unchecked warnings in `__Coding.java` and `DecryptionLogic.java`; these are pre-existing and non-blocking.
- The `guava-r05.jar` filename-based automodule warning during compilation is harmless.
