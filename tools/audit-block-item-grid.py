"""Actual GUI pixel regressions for repaired 3D block-item transforms."""
from pathlib import Path
from PIL import Image
import csv
root=Path(__file__).resolve().parent.parent/'run-client-audit/enchanter-audit'
for locale in ('en_us','ru_ru'):
    rows=list(csv.DictReader((root/f'creative-{locale}.tsv').open(),delimiter='\t'))
    with Image.open(root/f'creative-{locale}.png') as frame:
        frame=frame.convert('RGB')
        for name,minimum,maximum in [('funnel',18,30),('magnet',30,43),('mob_magnet',30,43),('repairer',35,47),('dislocator',35,47)]:
            index=int(next(row['index'] for row in rows if row['item']=='ttinkerer:'+name))
            x=33+index%18*54;y=81+index//18*69
            crop=frame.crop((x,y,x+48,y+48))
            points=[(x,y) for y in range(48) for x in range(48) if max(abs(channel-139) for channel in crop.getpixel((x,y)))>8]
            assert points,(locale,name,'invisible block icon')
            height=max(y for x,y in points)-min(y for x,y in points)+1
            assert minimum<=height<=maximum,(locale,name,'edge-on or oversized icon',height)
            assert min(x for x,y in points)>0 and max(x for x,y in points)<47,(locale,name,'horizontal clipping')
    print(f'TT BLOCK ITEM GRID PIXEL AUDIT: ALL PASS ({locale}: five visible, bounded 3D icons)')
