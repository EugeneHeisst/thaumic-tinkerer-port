"""Portable paths shared by the standalone regression tools."""
from pathlib import Path
import os
import shutil

PROJECT_ROOT = Path(__file__).resolve().parent.parent

def configured_path(variable, default):
    return Path(os.environ.get(variable, str(default))).expanduser().resolve()

def required_file(variable, default):
    path = configured_path(variable, default)
    if not path.is_file():
        raise SystemExit(f"Missing reference file {path.name}; set {variable} to its location (see docs/building.md).")
    return path

def java_tool(name):
    home = os.environ.get("JAVA_HOME")
    executable = Path(home) / "bin" / name if home else None
    if executable and executable.is_file():
        return str(executable)
    executable = shutil.which(name)
    if not executable:
        raise SystemExit(f"Java 21 tool {name} is unavailable; set JAVA_HOME or PATH.")
    return executable

AUDIT_DIR = configured_path("TT_AUDIT_DIR", PROJECT_ROOT / "run-server-audit")
SERVER_DIR = configured_path("TT_SERVER_DIR", PROJECT_ROOT / "related/server")
REFERENCE_DIR = configured_path("MC_REFERENCE_DIR", PROJECT_ROOT / "related/reference")
TT_REFERENCE_SOURCE = configured_path("TT_REFERENCE_SOURCE", PROJECT_ROOT / "related/build164")

def reference_jar():
    return required_file("TT_REFERENCE_JAR", PROJECT_ROOT / "related/ThaumicTinkerer_2.5_1.7.10_164.jar")

def built_mod_jar():
    override = os.environ.get("TT_MOD_JAR")
    candidates = [Path(override).expanduser().resolve()] if override else list((PROJECT_ROOT / "build/libs").glob("thaumic-tinkerer-modern-*.jar"))
    candidates = [p for p in candidates if p.is_file() and not p.name.endswith(("-sources.jar", "-javadoc.jar"))]
    if not candidates:
        raise SystemExit("Build the addon first or set TT_MOD_JAR to its compiled JAR.")
    return max(candidates, key=lambda p: p.stat().st_mtime_ns)
