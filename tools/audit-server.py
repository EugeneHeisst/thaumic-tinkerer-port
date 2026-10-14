from pathlib import Path
import shutil,subprocess,time
r=Path('/home/eugheist/thaumcraft4-server');t=Path('/tmp/tt-modern-native-audit');t.mkdir(exist_ok=True);(t/'mods').mkdir(exist_ok=True)
if not (t/'libraries').exists():(t/'libraries').symlink_to(r/'libraries',target_is_directory=True)
for p in (r/'mods').glob('*.jar'):shutil.copy2(p,t/'mods')
jar=Path('/home/eugheist/thaumic-tinkerer-modern/build/libs/thaumic-tinkerer-modern-0.1.0.jar')
source=Path('/home/eugheist/thaumic-tinkerer-modern/src/main')
if any(path.stat().st_mtime_ns>jar.stat().st_mtime_ns for path in source.rglob('*') if path.is_file()):raise SystemExit('Refusing stale audit JAR; run ./build.sh successfully first')
shutil.copy2(jar,t/'mods')
(t/'eula.txt').write_text('eula=true\n');(t/'server.properties').write_text('server-ip=127.0.0.1\nserver-port=25567\nonline-mode=false\nlevel-name=audit-world\nview-distance=2\nsimulation-distance=2\n')
import sys
ccLua='--cc-lua' in sys.argv
kamiDisabled='--kami-disabled' in sys.argv
bedrockDisabled='--bedrock-disabled' in sys.argv
mirrorsDisabled='--mirrors-disabled' in sys.argv
flightDisabled='--flight-disabled' in sys.argv
fireDisabled='--fire-disabled' in sys.argv
if sum((flightDisabled,fireDisabled,mirrorsDisabled,bedrockDisabled,kamiDisabled,ccLua))>1:raise SystemExit('Choose a single configuration audit')
disabled=flightDisabled or fireDisabled or mirrorsDisabled or bedrockDisabled or kamiDisabled
config=t/('config/ttinkerer-startup.toml' if kamiDisabled else 'config/tc4modern-common.toml' if mirrorsDisabled else 'config/ttinkerer-common.toml');config.parent.mkdir(exist_ok=True)
backup=config.read_bytes() if config.exists() else None
if kamiDisabled:config.write_text('[general.kami]\nforceenabled = false\n')
if mirrorsDisabled:
 import re
 if backup is None:raise SystemExit('Run default audit first to generate TC configuration')
 text,count=re.subn(r'(?m)^(\s*allow_mirrors\s*=\s*)true\s*$',r'\g<1>false',backup.decode())
 if count!=1:raise SystemExit('Expected exactly one original allow_mirrors=true config field')
 config.write_text(text)
if bedrockDisabled:config.write_text('bedrockDimensionID = 0\n')
if fireDisabled:config.write_text('enableSurvivalShareTome = true\nenableFire = false\nenableCake = true\n')
if flightDisabled:config.write_text('enableFlight = false\n')
logpath=t/('audit-cc-lua.log' if ccLua else 'audit-kami-disabled.log' if kamiDisabled else 'audit-bedrock-disabled.log' if bedrockDisabled else 'audit-mirrors-disabled.log' if mirrorsDisabled else 'audit-flight-disabled.log' if flightDisabled else 'audit-fire-disabled.log' if fireDisabled else 'audit.log')
success='TT CC LUA AUDIT: ALL PASS' if ccLua else 'TT KAMI STARTUP AUDIT: ALL PASS' if kamiDisabled else 'TT BEDROCK CONFIG AUDIT: ALL PASS' if bedrockDisabled else 'TT MIRRORS CONFIG AUDIT: ALL PASS' if mirrorsDisabled else 'TT FLIGHT CONFIG AUDIT: ALL PASS' if flightDisabled else 'TT FIRE CONFIG AUDIT: ALL PASS' if fireDisabled else 'TT PORT AUDIT: ALL PASS'
try:
 with open(logpath,'w') as log:
  prop='-Dttinkerer.ccLuaAudit=true' if ccLua else '-Dttinkerer.kamiStartupAudit=true' if kamiDisabled else '-Dttinkerer.bedrockConfigAudit=true' if bedrockDisabled else '-Dttinkerer.mirrorsConfigAudit=true' if mirrorsDisabled else '-Dttinkerer.flightConfigAudit=true' if flightDisabled else '-Dttinkerer.fireConfigAudit=true' if fireDisabled else '-Dttinkerer.portAudit=true'
  p=subprocess.Popen(['/usr/lib/jvm/java-21-openjdk/bin/java','-Xmx1500M',prop,'@libraries/net/neoforged/neoforge/21.1.256/unix_args.txt','nogui'],cwd=t,stdin=subprocess.PIPE,stdout=log,stderr=subprocess.STDOUT,text=True)
  try:
   for i in range(90):
    time.sleep(1);s=logpath.read_text()
    if p.poll() is not None:break
    if success in s or 'TT PORT AUDIT FAILED' in s or 'Encountered an unexpected exception' in s:
     p.stdin.write('stop\n');p.stdin.flush();break
   p.wait(timeout=30)
  finally:
   if p.poll() is None:p.stdin.write('stop\n');p.stdin.flush();p.wait(timeout=30)
finally:
 if disabled:
  if backup is None:config.unlink(missing_ok=True)
  else:config.write_bytes(backup)
result=logpath.read_text();print(result[-10000:])
if disabled or ccLua:
 if success not in result or 'Exception caught' in result:raise SystemExit(1)
 raise SystemExit(0)
if any(marker not in result for marker in ('TT SWORD BLOCKING AUDIT: ALL PASS','TT RECALL FOCUS AUDIT: ALL PASS','TT WARP GATE AUDIT: ALL PASS','TT SKY PEARL DATA AUDIT: ALL PASS','TT SHADOWBEAM PATH AUDIT: ALL PASS','TT XP DRAIN AUDIT: ALL PASS','TT PLACEMENT MIRROR AUDIT: ALL PASS','TT BLOCK TALISMAN AUDIT: ALL PASS','TT CAT AMULET AUDIT: ALL PASS','TT ICHOR POUCH AUDIT: ALL PASS','TT BEDROCK PORTAL AUDIT: ALL PASS','TT BEDROCK GENERATION AUDIT: ALL PASS','TT AWAKENED SWORD AUDIT: ALL PASS','TT AWAKENED AXE AUDIT: ALL PASS','TT PROTOCLAY AUDIT: ALL PASS','TT AWAKENED SHOVEL AUDIT: ALL PASS','TT AWAKENED PICK AUDIT: ALL PASS','TT ADVANCED MINING HOOK AUDIT: ALL PASS','TT ADVANCED TOOL MINING AUDIT: ALL PASS','TT ADVANCED TOOL MODES AUDIT: ALL PASS','TT AWAKENED BOOTS AUDIT: ALL PASS','TT AWAKENED LEGS AUDIT: ALL PASS','TT AWAKENED CHEST AUDIT: ALL PASS','TT AWAKENED HELM AUDIT: ALL PASS','TT KAMI ARMOR AUDIT: ALL PASS','TT KAMI TOOLS AUDIT: ALL PASS','TT KAMI WAND AUDIT: ALL PASS','TT KAMI MATERIAL AUDIT: ALL PASS','TT KAMI RESOURCES AUDIT: ALL PASS')):raise SystemExit(1)
if 'TT TABLET AUDIT: ALL PASS' not in result or 'TT LOCOMOTIVE AUDIT: ALL PASS' not in result or 'TT SUMMON AUDIT: ALL PASS' not in result or 'TT BLOOD SWORD AUDIT: ALL PASS' not in result or 'TT SHARE TOME AUDIT: ALL PASS' not in result or 'TT DISLOCATOR AUDIT: ALL PASS' not in result or 'TT MAGNET AUDIT: ALL PASS' not in result or 'TT FOCUS AUDIT: ALL PASS' not in result or 'TT REPAIRER AUDIT: ALL PASS' not in result or 'TT FUNNEL AUDIT: ALL PASS' not in result or 'TT XP TALISMAN AUDIT: ALL PASS' not in result or 'TT TRANSVECTOR AUDIT: ALL PASS' not in result or 'TT TALISMAN AUDIT: ALL PASS' not in result or 'TT PORT AUDIT: ALL PASS' not in result or 'Exception caught' in result: raise SystemExit(1)
