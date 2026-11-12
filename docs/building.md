# Building and regression tools

Use Java 21 and the included Gradle wrapper. The base Thaumcraft JAR is not bundled.
Put `tc4modern-0.17.179.jar` in the ignored `libs/` directory, or set
`-Ptc4LocalJar=path/to/tc4modern.jar` (`TC4_JAR` environment variable).

```sh
./gradlew build -Ptc4LocalJar=path/to/tc4modern.jar
```

The optional CC: Tweaked, Applied Energistics 2 and Jade APIs are resolved from
Modrinth using the versions declared in `build.gradle`. Add their matching
runtime JARs only to the isolated audit instance when exercising integrations.

The Python tools use paths relative to this checkout, with environment overrides:

| Variable | Default / purpose |
| --- | --- |
| `TT_AUDIT_DIR` | `run-server-audit/`, isolated audit output and world |
| `TT_SERVER_DIR` | `related/server/`, prepared NeoForge server providing `libraries/` and runtime `mods/` |
| `TT_MOD_JAR` | Most recently built addon JAR in `build/libs/` |
| `TT_REFERENCE_JAR` | `related/ThaumicTinkerer_2.5_1.7.10_164.jar`, original reference artifact |
| `MC_REFERENCE_DIR` | `related/reference/`, containing `cfr.jar` and `minecraft-1.7.10-client.jar` |
| `TT_REFERENCE_SOURCE` | `related/build164/`, locally extracted original addon sources |
| `JAVA_HOME` | Optional Java 21 toolchain; otherwise Java tools are found on `PATH` |

A prepared server is read only as a dependency source: the audit copies its
runtime mods into its own directory and links the libraries. The audit output
must differ from the prepared server directory. Use a dedicated reference server,
not an instance currently running a game. No original game JARs or decompiled
sources are redistributed here.

```sh
python3 tools/audit-server.py
python3 tools/audit-original-item-names.py
python3 tools/audit-original-textures.py
python3 tools/migrate-original-locales.py --check
```

Pixel tools additionally require Pillow. Integrated-world wrappers need Linux
`xvfb-run` and `timeout`; audio uses the null OpenAL backend. Existing
`--cc-lua`, `--kami-disabled`, `--bedrock-disabled`, `--mirrors-disabled`,
`--flight-disabled` and `--fire-disabled` audit modes are retained.
