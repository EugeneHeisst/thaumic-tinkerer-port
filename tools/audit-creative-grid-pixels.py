"""Check actual GUI sprite frames against original assets through verified model mappings.
3D/custom/animated override models require their separate visual audits.
"""
import csv,json
from pathlib import Path
from PIL import Image,ImageChops
root=Path(__file__).resolve().parent.parent
folder=root/'run-client-audit/enchanter-audit'
for locale in ('en_us','ru_ru'):
    with Image.open(folder/f'creative-{locale}.png') as frame:
        frame=frame.convert('RGB')
        assert frame.size==(1080,900),'Unexpected framebuffer/layout'
        plain=foil=0
        rows=list(csv.DictReader((folder/f'creative-{locale}.tsv').open(),delimiter='\t'))
        for row in rows:
            namespace,name=row['item'].split(':',1)
            if namespace!='ttinkerer':continue
            model_path=root/f'src/main/resources/assets/ttinkerer/models/item/{name}.json'
            model=json.loads(model_path.read_text())
            texture=model.get('textures',{}).get('layer0','')
            if model.get('parent') not in ('minecraft:item/generated','minecraft:item/handheld') or 'overrides' in model or len(model.get('textures',{}))!=1 or not texture.startswith('ttinkerer:'):continue
            with Image.open(root/f'src/main/resources/assets/ttinkerer/textures/{texture.split(":",1)[1]}.png') as sprite:
                if sprite.width!=sprite.height:continue
                sprite=sprite.convert('RGBA').resize((48,48),Image.Resampling.NEAREST)
            tint=int(row['tint'],16)
            assert tint>>24==255,(locale,name,'transparent native item tint',row['tint'])
            sprite=ImageChops.multiply(sprite,Image.new('RGBA',sprite.size,((tint>>16)&255,(tint>>8)&255,tint&255,255)))
            index=int(row['index']);x=33+index%18*54;y=81+index//18*69
            actual=frame.crop((x,y,x+48,y+48))
            expected=Image.new('RGBA',(48,48),(139,139,139,255));expected.alpha_composite(sprite)
            if row['foil']=='true':
                # Native glint can fill transparent holes inside baked front-face quads.
                # Validate the outer sprite bounds, not those interior alpha holes.
                left,top,right,bottom=sprite.getchannel('A').getbbox()
                outside=sum(max(abs(c-139) for c in actual.getpixel((x,y)))>8
                            for y in range(48) for x in range(48)
                            if not (left-1<=x<=right and top-1<=y<=bottom))
                assert outside==0,(locale,name,'glint outside original bounds',outside)
                foil+=1
            else:
                changed=sum(max(pixel)>8 for pixel in ImageChops.difference(actual,expected.convert('RGB')).get_flattened_data())
                assert changed<=30,(locale,name,'GUI sprite mismatch',changed)
                plain+=1
        assert plain>=60 and foil>=20,'Unexpected sprite coverage'
        print(f'TT CREATIVE GRID PIXEL AUDIT: ALL PASS ({locale}: {plain} sprite stacks, {foil} glint bounds; custom/3D/override models excluded)')
