"""Export runtime swatches from the approved 64x48 atlas (requires ImageMagick)."""
from pathlib import Path
import shutil
import subprocess

HERE = Path(__file__).resolve().parent
DEST = HERE.parents[1] / 'src/main/resources/assets/create_better_hammers/textures/item'
NAMES = ['brass', 'copper', 'steel', 'fastener',
         'wood', 'diamond', 'stone', 'iron',
         'cogwood', 'charcoal', 'endgrain', 'wedge']

for i, name in enumerate(NAMES):
    # Half the former horizontal grain resolution; miniature details get their
    # own four-pixel artwork instead of a complete sixteen-pixel material tile.
    size = '8x16!' if name == 'wood' else '4x4!' if name in ('fastener', 'endgrain', 'wedge') else '16x16!'
    subprocess.run(['magick', str(HERE / 'workshop-worn-materials.png'),
                    '-crop', f'16x16+{i % 4 * 16}+{i // 4 * 16}', '+repage',
                    '-filter', 'point', '-resize', size, '-strip',
                    str(DEST / f'pneumatic_{name}.png')], check=True)

shutil.copyfile(DEST / 'pneumatic_brass.png', DEST / 'pneumatic_brass_trim.png')
shutil.copyfile(DEST / 'pneumatic_diamond.png', DEST / 'diamond_hammer_gem.png')
