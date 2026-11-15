"""Construct three workshop hammers from head, socket and tapered handle geometry."""
from pathlib import Path
import json
import hammer_mesh as m

ROOT = Path(__file__).resolve().parents[2]
MODELS = ROOT / 'src/main/resources/assets/create_better_hammers/models/item'
# Reuse the established handheld poses; the inventory angle shows the new construction.
display = json.loads((MODELS/'pneumatic_hammer.json').read_text())['display']

def handle():
    m.group('tapered_wood_handle')
    sections=[(-2.5,.91),(0.0,.85),(6.8,.67),(10.7,.72)]
    rings=[]
    for y,w in sections:
        rings.append([(x,y,z) for x,z in m.rect(8-w,8-w,8+w,8+w,.17)])
    m.group_bounds=([7.09,-2.5,7.09],[8.91,10.7,8.91])
    m.face(rings[0],'wood');m.face(rings[-1][::-1],'wood')
    for a,b in zip(rings,rings[1:]):
        for i in range(8):
            j=(i+1)%8
            m.face([a[j],a[i],b[i],b[j]],'wood')

for tier in ('stone','iron','diamond'):
    m.reset()
    handle()
    if tier=='stone':
        # Dressed stone with a few large irregularities, not a pixel-shaped outline.
        outline=[(3.15,10.65),(3.65,10.10),(7.2,10.25),(10.9,10.05),
                 (12.65,10.50),(13.02,11.10),(12.85,14.12),(12.30,14.73),
                 (8.9,14.58),(5.3,14.82),(3.55,14.51),(3.02,13.94)]
        m.prism('dressed_stone_head',outline,6.05,9.95,'stone')
        top=14.68
    else:
        # A forged body between two broad striking ends, with chamfered edges.
        m.box('head_body',[4.12,10.48,6.35],[11.88,14.47,9.65],tier,.22,'x')
        for name,x0,x1 in [('forward_face',3.02,4.45),('rear_face',11.55,12.98)]:
            m.box(name,[x0,10.20,6.10],[x1,14.75,9.90],tier,.43 if tier=='iron' else .58,'x')
        if tier=='diamond':
            # Iron socket and wraparound band retain the gemstone head.
            # Both diamond striking ends stay exposed; no decorative fasteners.
            m.box('iron_handle_collar',[7.02,8.90,7.02],[8.98,10.60,8.98],'iron',.18,'y')
            # Extend 10% of the original band width toward each striking end.
            # Keep the handle eye unchanged so iron remains visible around it.
            m.box('iron_head_cuff',[6.824,10.24,6.14],[9.176,14.71,9.86],'iron',.32,'x')
        else:
            m.box('handle_socket',[7.02,9.65,7.02],[8.98,10.54,8.98],tier,.18,'y')
        top=14.47
        if tier=='diamond': top=14.71
    # The seated wooden tenon and wedge establish how the head is attached.
    m.box('handle_eye',[7.02,top-.04,7.06],[8.98,top+.045,8.94],'charcoal',.18,'y')
    m.box('wood_tenon',[7.31,top+.025,7.32],[8.69,top+.17,8.68],'wood',.13,'y')
    m.box('wood_wedge',[7.43,top+.16,7.88],[8.57,top+.24,8.12],'cogwood',.03,'y')
    stem=tier+'_hammer'
    m.save(stem)
    model={'loader':'neoforge:obj','model':'create_better_hammers:models/item/'+stem+'.obj',
           'automatic_culling':False,'shade_quads':True,'emissive_ambient':False,
           'gui_light':'side','flip_v':True,
           'textures':{'particle':'create_better_hammers:item/'+('diamond_hammer_gem' if tier=='diamond' else 'pneumatic_'+tier)},'display':display}
    (MODELS/(stem+'.json')).write_text(json.dumps(model,indent=2)+'\n')
