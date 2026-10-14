"""Migrate build164 translations and explicit modern aliases; --check is read-only."""
from pathlib import Path
import json,sys,zipfile
root=Path(__file__).resolve().parent.parent
folder=root/'src/main/resources/assets/ttinkerer/lang'
reference=Path('/home/eugheist/Downloads/ThaumicTinkerer_2.5_1.7.10_164.jar')
mapping_path=root/'tools/original-item-names.json'
mapping=json.loads(mapping_path.read_text())
check='--check' in sys.argv

def output(path,value):
    if check:assert json.loads(path.read_text())==value,('Stale migrated locale',path)
    else:path.write_text(json.dumps(value,ensure_ascii=False,indent=2)+'\n')

with zipfile.ZipFile(reference) as jar:
    files={Path(name).stem.lower():name for name in jar.namelist() if name.startswith('assets/ttinkerer/lang/') and name.endswith('.lang')}
    def original(locale):
        values={}
        for line in jar.read(files[locale]).decode('utf-8-sig').splitlines():
            if '=' in line and not line.startswith('#'):
                key,value=line.split('=',1);values[key]=value
        return values
    for locale in ('de_de','fr_fr','ko_kr','zh_cn'):
        values=original(locale)
        data=dict(values)
        missing=[]
        for key,sources in mapping.items():
            source=next((s for s in (sources['en_us'],sources['ru_ru']) if s in values),sources['en_us'])
            if check:assert sources[locale]==source,(locale,key,'source mapping changed')
            else:sources[locale]=source
            if source in values:data[key]=values[source]
            else:missing.append(key) # Native en_us fallback, no invented translations.
        for key in json.loads((folder/'en_us.json').read_text()):
            if key.startswith('tc.research_name.'):
                suffix=key.removeprefix('tc.research_name.')
                source='ttresearch.name.'+('LEVITATOR' if suffix=='TT_LEVITATOR' else suffix)
                if source in values:data[key]=values[source]
        if 'itemGroup.ThaumicTinkerer' in values:
            data['itemGroup.ttinkerer']=values['itemGroup.ThaumicTinkerer']
            data['tc.research_category.THAUMICTINKERER']=values['itemGroup.ThaumicTinkerer']
        output(folder/f'{locale}.json',data)
        print(f'TT ORIGINAL LOCALE {"AUDIT" if check else "MIGRATION"}: {locale}: {len(values)} original keys retained, {len(data)-len(values)} aliases; {len(missing)} item names use English fallback')
output(mapping_path,mapping)
