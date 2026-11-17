"""Check beta packaging against the owner-approved alpha.11 runtime JAR.

Usage: python3 tools/verify_release.py /path/to/alpha.11.jar /path/to/beta.1.jar
Does not launch Minecraft or substitute for gameplay tests.
"""
import hashlib
import json
from pathlib import Path
import struct
import sys
import tomllib
import zipfile


def sha(data):
    return hashlib.sha256(data).hexdigest()


def verify(baseline_path, release_path):
    root = Path(__file__).resolve().parents[1]
    with zipfile.ZipFile(baseline_path) as old, zipfile.ZipFile(release_path) as new:
        assert old.testzip() is None and new.testzip() is None
        names = set(new.namelist())
        files = {n for n in names if not n.endswith('/')}
        forbidden = [n for n in files if any(s in n.lower() for s in (
            'hammersmoketest', 'previewcapture', 'gametest', 'preview/', 'test_structure'))]
        assert not forbidden, forbidden
        classes = [n for n in files if n.endswith('.class')]
        assert classes
        assert set(classes) == {n for n in old.namelist() if n.endswith('.class')}
        for name in classes:
            assert struct.unpack('>H', new.read(name)[6:8])[0] == 65, name
            assert new.read(name) == old.read(name), f'Changed compiled gameplay: {name}'
        resources = [n for n in files if n.startswith(('assets/', 'data/'))]
        assert set(resources) == {n for n in old.namelist() if n.startswith(('assets/', 'data/')) and not n.endswith('/')}
        for name in resources:
            assert new.read(name) == old.read(name), f'Changed gameplay resource: {name}'
            assert new.read(name) == (root / 'src/main/resources' / name).read_bytes(), name
        json_count = 0
        for name in files:
            if name.endswith('.json'):
                json.loads(new.read(name))
                json_count += 1
        metadata = tomllib.loads(new.read('META-INF/neoforge.mods.toml').decode())
        mod = metadata['mods'][0]
        assert mod['modId'] == 'create_better_hammers'
        assert mod['version'] == '0.1.0-beta.1'
        assert mod['logoFile'] == 'logo.png'
        assert metadata['license'] == 'MIT'
        assert new.read('logo.png') == (root / 'art/listing/icon.png').read_bytes()
        assert new.read('LICENSE') == (root / 'LICENSE').read_bytes()
        assert new.read('THIRD_PARTY_NOTICES.md') == (root / 'THIRD_PARTY_NOTICES.md').read_bytes()
        assert not any(n.endswith('.jar') for n in files), 'Unexpected bundled dependency'
        allowed_changes = {'META-INF/neoforge.mods.toml', 'META-INF/MANIFEST.MF', 'logo.png', 'THIRD_PARTY_NOTICES.md'}
        old_files = {n for n in old.namelist() if not n.endswith('/')}
        changed = sorted(n for n in files | old_files if n not in files or n not in old_files or new.read(n) != old.read(n))
        assert set(changed) <= allowed_changes, changed
        return {
            'baseline': str(baseline_path), 'baseline_sha256': sha(Path(baseline_path).read_bytes()),
            'release': str(release_path), 'release_sha256': sha(Path(release_path).read_bytes()),
            'size_bytes': Path(release_path).stat().st_size,
            'identical_gameplay_classes': len(classes), 'identical_gameplay_resources': len(resources),
            'valid_json_files': json_count, 'changed_entries': changed,
            'status': 'passed',
        }


if __name__ == '__main__':
    print(json.dumps(verify(Path(sys.argv[1]), Path(sys.argv[2])), indent=2))
