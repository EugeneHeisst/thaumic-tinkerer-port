"""Build-164 ModelMagnet cuboids, including its quarter-turn transparent planes."""
import json,math,itertools
from pathlib import Path
root=Path('src/main/resources/assets/ttinkerer')
parts=[((0,0),(0,0,0),(14,2,14),(-7,22,-7),0),((0,16),(0,0,0),(4,13,4),(-2,9,-2),0),((28,19),(0,-2,0),(6,14,0),(3,10,-3),-90),((28,33),(0,0,0),(6,14,0),(-3,8,3),90),((40,19),(0,0,0),(6,14,0),(3,8,3),180),((40,33),(0,0,0),(6,14,0),(-3,8,-3),0),((28,49),(0,0,0),(6,0,6),(-3,8,-3),0)]
elements=[]
for (u,v),lo,(dx,dy,dz),origin,angle in parts:
 c=round(math.cos(math.radians(angle)));s=round(math.sin(math.radians(angle)))
 def transform(x,y,z):return [8+origin[0]+c*x+s*z,24-origin[1]-y,8-origin[2]+s*x-c*z]
 corners=[transform(*p) for p in itertools.product(*[(a,a+b) for a,b in zip(lo,(dx,dy,dz))])]
 start=[min(p[i] for p in corners) for i in range(3)];end=[max(p[i] for p in corners) for i in range(3)]
 uv={'up':[u+dz,v,u+dz+dx,v+dz],'down':[u+dz+dx,v,u+dz+2*dx,v+dz],'east':[u,v+dz,u+dz,v+dz+dy],'north':[u+2*dz+dx,v+dz,u+2*dz+2*dx,v+dz+dy],'west':[u+dz+dx,v+dz,u+2*dz+dx,v+dz+dy],'south':[u+dz,v+dz,u+dz+dx,v+dz+dy]}
 dirs=['north','east','south','west'];faces={}
 for face,coords in uv.items():
  if coords[0]==coords[2] or coords[1]==coords[3]:continue
  target=dirs[(dirs.index(face)+angle//90)%4] if face in dirs else face
  faces[target]={'texture':'#all','uv':[n/4 for n in coords]}
  if face in ('up','down') and angle:faces[target]['rotation']=angle%360
 elements.append({'from':start,'to':end,'faces':faces})
for block,stem in [('magnet','magnet'),('mob_magnet','mobmagnet')]:
 for pulling,suffix in [(True,'s'),(False,'n')]:
  name=block+('_pull' if pulling else '_push');tex='ttinkerer:model/'+stem+suffix
  (root/'models/block'/f'{name}.json').write_text(json.dumps({'parent':'minecraft:block/block','render_type':'minecraft:translucent','textures':{'particle':tex,'all':tex},'elements':elements},indent=2))
 (root/'blockstates'/f'{block}.json').write_text(json.dumps({'variants':{f'pulling={str(p).lower()}':{'model':f'ttinkerer:block/{block}'+('_pull' if p else '_push')} for p in [True,False]}},indent=2))
 (root/'models/item'/f'{block}.json').write_text(json.dumps({'parent':f'ttinkerer:block/{block}_pull'}))
(root/'models/item/soul_mould.json').write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':'ttinkerer:items/soulmould'}}))
p=Path('src/main/resources/assets/minecraft/atlases/blocks.json');data=json.loads(p.read_text())
for tex in ['magnets','magnetn','mobmagnets','mobmagnetn']:
 entry={'type':'minecraft:single','resource':'ttinkerer:model/'+tex,'sprite':'ttinkerer:model/'+tex}
 if entry not in data['sources']:data['sources'].append(entry)
p.write_text(json.dumps(data,indent=2))
for lang,names in [('en_us',('Kinetic Attractor','Corporeal Attractor','Soul Mould')),('ru_ru',('Кинетический притяжатель','Материальный притяжатель','Матрица души'))]:
 p=root/'lang'/f'{lang}.json';data=json.loads(p.read_text());data.update(dict(zip(['block.ttinkerer.magnet','block.ttinkerer.mob_magnet','item.ttinkerer.soul_mould'],names)));p.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n')
