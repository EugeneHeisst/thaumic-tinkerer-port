#!/usr/bin/env python3
"""Position only this checkout's audit, including native Wayland windows."""
from pathlib import Path
import subprocess,time,tempfile
root=Path(__file__).resolve().parents[1]
for attempt in range(55):
    pid=None
    for proc in Path('/proc').iterdir():
        if not proc.name.isdigit():continue
        try:
            if (proc/'cwd').resolve()!=root/'run-client-audit':continue
            args=(proc/'cmdline').read_bytes().decode().split('\0')
            if args and args[0].endswith('/java'):pid=int(proc.name);break
        except (OSError,UnicodeError):continue
    if pid:
        script=('function place(w) { if (w.pid === '+str(pid)+') { const g=w.frameGeometry; '
                'w.frameGeometry={x:540,y:140,width:g.width,height:g.height}; } } '
                'workspace.windowAdded.connect(place); for (const w of workspace.windowList()) place(w);')
        with tempfile.NamedTemporaryFile(mode='w',suffix='.js',prefix='tt-audit-position-',delete=False) as file:
            file.write(script);path=Path(file.name)
        try:
            subprocess.run(['qdbus6','org.kde.KWin','/Scripting','org.kde.kwin.Scripting.unloadScript','tt-audit-position'],stdout=subprocess.DEVNULL)
            sid=subprocess.check_output(['qdbus6','org.kde.KWin','/Scripting','org.kde.kwin.Scripting.loadScript',str(path),'tt-audit-position'],text=True).strip()
            subprocess.run(['qdbus6','org.kde.KWin','/Scripting/Script'+sid,'org.kde.kwin.Script.run'],check=True)
            print(f'Position watcher installed for own audit PID {pid}: native KWin x540 y140',flush=True)
        finally:path.unlink(missing_ok=True)
        break
    time.sleep(1)
else:raise SystemExit('Own audit process did not appear within 55 seconds')
