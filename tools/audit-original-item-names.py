"""Compare every port display-name mapping with the user's unmodified build164 JAR."""
from pathlib import Path
import json, zipfile
root=Path(__file__).resolve().parent.parent
reference=Path('/home/eugheist/Downloads/ThaumicTinkerer_2.5_1.7.10_164.jar')
mapping=json.loads((root/'tools/original-item-names.json').read_text())
with zipfile.ZipFile(reference) as jar:
    for locale,original in [('en_us','en_US'),('ru_ru','ru_RU')]:
        values={line.split('=',1)[0]:line.split('=',1)[1] for line in jar.read(f'assets/ttinkerer/lang/{original}.lang').decode('utf-8-sig').splitlines() if '=' in line and not line.startswith('#')}
        modern=json.loads((root/f'src/main/resources/assets/ttinkerer/lang/{locale}.json').read_text())
        keys={key for key in modern if key.startswith(('item.ttinkerer.','block.ttinkerer.'))}
        assert keys-set(mapping)=={'item.ttinkerer.legacy_crop_egg'},'Unmapped item names (legacy crop egg is intentionally hidden)'
        for key,sources in mapping.items():
            expected=values[sources[locale]]
            assert modern[key]==expected,(locale,key,modern[key],expected)
            assert modern[sources[locale]]==expected,('original key changed',locale,sources[locale])
        print(f'TT ORIGINAL ITEM NAME AUDIT: ALL PASS ({locale}, {len(mapping)} mapped names)')
