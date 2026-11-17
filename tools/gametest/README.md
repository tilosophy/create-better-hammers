# Runtime regression checks

These tests use Minecraft 1.21.1 / NeoForge 21.1.233 and the full Create 6.0.10 release JAR. They run real server-side item interactions and combat using test players. The Java and structure sources here are excluded from a normal build.

From the repository root with Java 21 and Gradle:

```sh
./gradlew -I tools/gametest/init.gradle -PfullCreateJar=/absolute/path/to/create-6.0.10.jar runGameTestServer
```

Replace the path with your local full Create release JAR. The run directory is `build/gametest-alpha9`.

The three tests cover material progression and cooldowns, ordinary combat and enchantments, and pneumatic behavior. Alpha.9 additionally checks 12 powered damage, no stacking across repeated ticks, boost removal on tank/tool changes and empty tanks, Sturdy Sheet completion text, and failure without false completion when inventory is full.

After any injected test/preview run, use `./gradlew clean build` to produce the distributable JAR. Verify that no `HammerSmokeTest` or `PreviewCapture` classes are packaged.

Alpha.9 verification: all three required tests passed on the development target. Separate Minecraft client captures covered the four held models, pneumatic swing, left-hand view and inventory views. Shader-specific appearance and multiplayer playtesting remain manual checks.
