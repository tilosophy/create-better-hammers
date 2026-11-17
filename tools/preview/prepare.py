"""Prepare isolated review options and before/after models; never alter production assets."""
import argparse
import json
from pathlib import Path
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--baseline', required=True, help='Commit containing the preceding approved models')
args = parser.parse_args()
repo = Path(__file__).resolve().parents[2]
run = repo / 'build/visual-review'
saves = run / 'saves'
saves.mkdir(parents=True, exist_ok=True)
(saves / 'server.properties').write_text(
    'level-name=preview\nlevel-type=minecraft\\:flat\n'
    'generator-settings={"biome":"minecraft:plains","layers":'
    '[{"block":"minecraft:bedrock","height":1},{"block":"minecraft:dirt","height":2},'
    '{"block":"minecraft:grass_block","height":1}]}\n'
    'online-mode=false\nserver-ip=127.0.0.1\nserver-port=25579\n'
    'view-distance=3\nsimulation-distance=3\nspawn-protection=0\ngamemode=creative\nallow-flight=true\n')
pack = run / 'resourcepacks/refinement-review'
pack.mkdir(parents=True, exist_ok=True)
(pack / 'pack.mcmeta').write_text(json.dumps({'pack': {'pack_format': 34, 'description': 'Temporary before/after review'}}))
base = 'src/main/resources/assets/create_better_hammers/'
files = subprocess.check_output(['git', 'ls-tree', '-r', '--name-only', args.baseline, base], cwd=repo).decode().splitlines()
for path in files:
    if '/textures/item/' not in path and not path.endswith(('.obj', '.mtl')):
        continue
    data = subprocess.check_output(['git', 'show', args.baseline + ':' + path], cwd=repo)
    if path.endswith('.mtl'):
        data = data.replace(b'create_better_hammers:item/', b'hammer_review:item/')
    dest = pack / 'assets/hammer_review' / path.removeprefix(base)
    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_bytes(data)
models = pack / 'assets/create_better_hammers/models/item'
models.mkdir(parents=True, exist_ok=True)
for tier in ('stone', 'iron', 'diamond', 'pneumatic'):
    stem = tier + '_hammer'
    original = json.loads((repo / base / 'models/item' / (stem + '.json')).read_text())
    root = json.loads(json.dumps(original))
    root['overrides'] = [
        {'predicate': {'custom_model_data': i}, 'model': 'create_better_hammers:item/' + tier + '_' + label}
        for i, label in ((1, 'before'), (2, 'after'), (3, 'top'), (4, 'reverse'))]
    (models / (stem + '.json')).write_text(json.dumps(root))
    for label in ('before', 'after', 'top', 'reverse'):
        view = json.loads(json.dumps(original))
        view['display']['gui']['rotation'] = [12, 25, 45]
        view['display']['gui']['scale'] = [.8] * 3
        if label == 'top':
            view['display']['gui']['rotation'] = [65, 25, 45]
        if label == 'reverse':
            view['display']['gui']['rotation'] = [25, 205, 45]
        if label == 'before':
            view['model'] = 'hammer_review:models/item/' + stem + '.obj'
        (models / (tier + '_' + label + '.json')).write_text(json.dumps(view))
(run / 'options.txt').write_text(
    'version:3955\nresourcePacks:["vanilla","mod_resources","file/refinement-review"]\n'
    'renderDistance:4\nsimulationDistance:5\nmaxFps:60\nguiScale:2\nfullscreen:false\npauseOnLostFocus:false\nonboardAccessibility:false\ntutorialStep:none\n')
print('Prepared preview-only assets and options. Existing saves were not modified.')
