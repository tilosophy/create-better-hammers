# Minecraft visual capture helper

Development-only sources, excluded from normal builds. Target: Minecraft 1.21.1, NeoForge 21.1.233, Create 6.0.10, Java 21.

`init.gradle` adds a `visualPreview` client run and a `previewWorld` server run. Pass `-PfullCreateJar=/absolute/path/to/create-6.0.10.jar` to use the full release and its bundled dependencies.

The client opens a local save named `preview` in `build/visual-review/saves`. First run `python3 tools/preview/prepare.py --baseline COMMIT_SHA` with the preceding model commit. This creates local-only flat-world server settings, review options and the comparison resource pack. After accepting Minecraft's EULA for the development server in that directory, run:

```sh
./gradlew -I tools/preview/init.gradle -PfullCreateJar=/absolute/path/to/create-6.0.10.jar runPreviewWorld
./gradlew -I tools/preview/init.gradle -PfullCreateJar=/absolute/path/to/create-6.0.10.jar runVisualPreview
```

The server helper places a smooth-stone platform and stops. The client helper takes eleven captures in `build/visual-review/screenshots`: held Pneumatic, held Diamond, both comparison screens the four-tier lineup, and an elevated view of the three basic handle tops. It then exits. The comparison screens expect a temporary resource pack supplying custom-model-data 1 (previous) and 2 (refined) variants for the items; both variants must use the same GUI transform. Custom-model-data 3 supplies the elevated top view; 4 supplies the reverse angle. The normal held views use custom-model-data 0 and the actual item transforms.

Use a working OpenGL display; these are Minecraft-rendered captures, not generated mockups. Do not distribute the preview resource pack or helper classes. Run a clean normal build before packaging a candidate, then check the JAR excludes `PreviewCapture` and `PreviewWorld`.
