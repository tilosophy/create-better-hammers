"""Give the three approved sprites physical depth without repainting their artwork.

Requires Pillow. Run after the normal Gradle build has supplied Minecraft resources.
"""
from pathlib import Path
from zipfile import ZipFile
from PIL import Image
import json

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'src/main/resources/assets/create_better_hammers'
with ZipFile(ROOT / 'build/moddev/artifacts/neoforge-21.1.233-client-extra-aka-minecraft-resources.jar') as archive:
    generated = json.loads(archive.read('assets/minecraft/models/item/generated.json'))
    handheld = json.loads(archive.read('assets/minecraft/models/item/handheld.json'))
display = generated['display'] | handheld['display']
display['gui'] = {'rotation': [10, 25, 0], 'translation': [0, 0, 0], 'scale': [.9, .9, .9]}

for tier in ('stone', 'iron', 'diamond'):
    sprite = Image.open(ASSETS / f'textures/item/{tier}_hammer.png').convert('RGBA')
    assert sprite.size == (16, 16)
    depths = {}
    for y in range(16):
        for x in range(16):
            r, g, b, a = sprite.getpixel((x, y))
            if not a:
                continue
            wooden = r > g and max(r, g, b) - min(r, g, b) > 15
            # The original silhouette already supplies stepped head edges.
            depths[x, y] = 1.5 if wooden else 3.5
    elements = []
    for (x, y), depth in depths.items():
        faces = {
            'south': {'uv': [x, y, x+1, y+1], 'texture': '#layer0'},
            'north': {'uv': [x+1, y, x, y+1], 'texture': '#layer0'},
        }
        for side, dx, dy in [('west', -1, 0), ('east', 1, 0), ('up', 0, -1), ('down', 0, 1)]:
            if depths.get((x+dx, y+dy), 0) < depth:
                faces[side] = {'uv': [x+.1, y+.1, x+.9, y+.9], 'texture': '#layer0'}
        elements.append({'from': [x, 15-y, 8-depth/2], 'to': [x+1, 16-y, 8+depth/2], 'faces': faces})
    model = {'gui_light': 'side', 'ambientocclusion': False,
             'textures': {'layer0': f'create_better_hammers:item/{tier}_hammer', 'particle': '#layer0'},
             'display': display, 'elements': elements}
    (ASSETS / f'models/item/{tier}_hammer.json').write_text(json.dumps(model, indent=2) + '\n')
    print(f'{tier}: {len(elements)} occupied pixels; solid 3.5-unit head, 1.5-unit handle; original handheld transforms.')
