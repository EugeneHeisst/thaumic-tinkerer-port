"""Pixel equality for copied/renamed original textures; excludes split effect sprites."""
from pathlib import Path
from zipfile import ZipFile
from PIL import Image
import io
root=Path(__file__).resolve().parent.parent
prefix='assets/ttinkerer/textures/'
with ZipFile('/home/eugheist/Downloads/ThaumicTinkerer_2.5_1.7.10_164.jar') as jar:
    normalized=lambda value:value.lower().replace('_','').replace(' ','')
    originals={normalized(name):name for name in jar.namelist() if name.startswith(prefix) and name.endswith('.png')}
    checked=0
    for path in (root/'src/main/resources'/prefix).rglob('*.png'):
        relative=path.relative_to(root/'src/main/resources').as_posix()
        if '/mob_effect/' in relative:continue # Atlas extraction is a separate renderer adaptation.
        assert normalized(relative) in originals,('No original texture mapping',relative)
        source=originals[normalized(relative)]
        with Image.open(path) as actual,Image.open(io.BytesIO(jar.read(source))) as expected:
            assert actual.size==expected.size and actual.convert('RGBA').tobytes()==expected.convert('RGBA').tobytes(),('Texture changed',relative,source)
        checked+=1
    print(f'TT ORIGINAL TEXTURE PIXEL AUDIT: ALL PASS ({checked} copied/renamed textures; split effect sprites excluded)')
