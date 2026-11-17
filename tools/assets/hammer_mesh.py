"""Shared mesh construction and material mapping for the workshop hammer family."""
from pathlib import Path
import math

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'src/main/resources/assets/create_better_hammers'
MODELS = ASSETS / 'models/item'
MODELS.mkdir(parents=True, exist_ok=True)

materials = ['brass','steel','copper','wood','diamond','iron','brass_trim','charcoal','stone','cogwood','fastener','endgrain','wedge']
lines=['# Pneumatic hammer; logical head faces -X, rear wrench +X.',
       '# Geometry rotated -45 deg around the original sprite pivot, before applying unchanged handheld transforms.',
       'mtllib pneumatic_hammer.mtl','vt 0.5 0.5']
vertex_count = 0
face_count = 0
parts=[]

def transform(p):
    x,y,z=p; x-=8; y-=8
    q=math.sqrt(.5)
    return ((8+(x+y)*q)/16, (8+(y-x)*q)/16, z/16)

current_group = ''
group_bounds = None
uv_count=0

def material_for(name, mat):
    if mat in materials: return mat
    if name=='grip': return 'wood'
    if name=='plate_surround': return 'copper'
    if name=='diamond_end_plate': return 'diamond'
    if name=='gear_housing' or name=='open_wrench': return 'brass'
    if name.startswith('pipe_'): return 'copper'
    if 'main_drive' in name or 'main_hub' in name: return 'iron'
    return {'body':'steel','frame':'brass_trim','recess':'charcoal','grip':'wood',
            'mechanism':'iron','plate':'diamond'}[mat]

def face(points,mat):
    global vertex_count,face_count,uv_count
    mat=material_for(current_group,mat)
    lines.append('usemtl '+mat)
    a,b,c=points[:3]
    ab=[b[i]-a[i] for i in range(3)]; ac=[c[i]-a[i] for i in range(3)]
    normal=[ab[1]*ac[2]-ab[2]*ac[1],ab[2]*ac[0]-ab[0]*ac[2],ab[0]*ac[1]-ab[1]*ac[0]]
    axis=max(range(3),key=lambda i:abs(normal[i]))
    u,v={0:(2,1),1:(0,2),2:(0,1)}[axis]
    lo,hi=group_bounds
    for p in points:
        lines.append('v '+' '.join(f'{n:.7f}' for n in transform(p)))
        # Planar mapping over each part keeps edge wear on the physical edges.
        # Mesh coordinates carry depth; the bitmap supplies only material treatment.
        uu=.025+.95*(p[u]-lo[u])/max(.01,hi[u]-lo[u])
        vv=.025+.95*(p[v]-lo[v])/max(.01,hi[v]-lo[v])
        if mat=='wood':
            uu=.15+.7*(p[u]-lo[u])/max(.01,hi[u]-lo[u])
        if mat in ('fastener','endgrain','wedge'):
            # Dedicated four-pixel swatches, with a uniform planar scale.
            # Thin wedge faces sample a strip instead of crushing an entire tile.
            span=max(.01,hi[u]-lo[u],hi[v]-lo[v])
            uu=.125+.75*(p[u]-lo[u])/span
            vv=.125+.75*(p[v]-lo[v])/span
        elif 'fastener' in current_group or 'rivet' in current_group or 'pin' in current_group:
            uu=.3+.4*(p[u]-lo[u])/max(.01,hi[u]-lo[u])
            vv=.3+.4*(p[v]-lo[v])/max(.01,hi[v]-lo[v])
        if 'main_drive' in current_group or 'main_hub' in current_group:
            uu=.1+.8*(p[u]-lo[u])/max(.01,hi[u]-lo[u])
            vv=.1+.8*(p[v]-lo[v])/max(.01,hi[v]-lo[v])
        assert 0<=uu<=1 and 0<=vv<=1,(current_group,uu,vv)
        lines.append(f'vt {uu:.7f} {vv:.7f}')
    lines.append('f '+' '.join(f'{vertex_count+i+1}/{uv_count+i+2}' for i in range(len(points))))
    vertex_count+=len(points); uv_count+=len(points); face_count+=1

def group(name):
    global current_group
    current_group=name
    lines.append('o '+name); parts.append(name)

def area(poly):
    return sum(a[0]*b[1]-b[0]*a[1] for a,b in zip(poly,poly[1:]+poly[:1]))/2

def cross(a,b,c):
    return (b[0]-a[0])*(c[1]-a[1])-(b[1]-a[1])*(c[0]-a[0])

def triangulate(poly):
    indices=list(range(len(poly))); result=[]
    while len(indices)>3:
        for k,b in enumerate(indices):
            a,c=indices[k-1],indices[(k+1)%len(indices)]
            if cross(poly[a],poly[b],poly[c])<=1e-8: continue
            inside=any(all(cross(poly[u],poly[v],poly[p])>=-1e-8 for u,v in [(a,b),(b,c),(c,a)]) for p in indices if p not in (a,b,c))
            if inside: continue
            result.append((a,b,c)); indices.pop(k); break
        else: raise ValueError('Non-simple polygon')
    result.append(tuple(indices)); return result

def prism(name,poly,low,high,mat,axis='z',caps=None):
    group(name)
    if area(poly)<0: poly=poly[::-1]
    def point(uv,z):
        u,v=uv
        return (u,v,z) if axis=='z' else (z,u,v) if axis=='x' else (v,z,u)
    global group_bounds
    all_points=[point(uv,z) for uv in poly for z in (low,high)]
    group_bounds=([min(p[i] for p in all_points) for i in range(3)],
                  [max(p[i] for p in all_points) for i in range(3)])
    for a,b,c in triangulate(poly):
        face([point(poly[i],low) for i in (c,b,a)],(caps or {}).get('low',mat))
        face([point(poly[i],high) for i in (a,b,c)],(caps or {}).get('high',mat))
    for a,b in zip(poly,poly[1:]+poly[:1]):
        face([point(a,low),point(b,low),point(b,high),point(a,high)],mat)

def rect(x0,y0,x1,y1,c=0):
    if not c: return [(x0,y0),(x1,y0),(x1,y1),(x0,y1)]
    return [(x0+c,y0),(x1-c,y0),(x1,y0+c),(x1,y1-c),
            (x1-c,y1),(x0+c,y1),(x0,y1-c),(x0,y0+c)]

def box(name,a,b,mat,c=0,axis='z',caps=None):
    if axis=='z': poly,lo,hi=rect(a[0],a[1],b[0],b[1],c),a[2],b[2]
    elif axis=='x': poly,lo,hi=rect(a[1],a[2],b[1],b[2],c),a[0],b[0]
    else: poly,lo,hi=rect(a[2],a[0],b[2],b[0],c),a[1],b[1]
    prism(name,poly,lo,hi,mat,axis,caps)

def disc(name,cx,cy,z0,z1,r,mat,n=12):
    p=[(cx+r*math.cos(i*2*math.pi/n),cy+r*math.sin(i*2*math.pi/n)) for i in range(n)]
    prism(name,p,z0,z1,mat)

def gear(name,cx,cy,z0,z1,r,mat,n,phase):
    p=[]
    for i in range(n):
        for a,rr in [(-.5,r*.78),(-.30,r),(.30,r),(.5,r*.78)]:
            angle=phase+(i+a)*2*math.pi/n
            point=(cx+rr*math.cos(angle),cy+rr*math.sin(angle))
            if not p or math.dist(point,p[-1])>1e-7: p.append(point)
    if math.dist(p[-1],p[0])<1e-7:p.pop()
    prism(name,p,z0,z1,mat)


def save(stem):
    lines[2]='mtllib '+stem+'.mtl'
    (MODELS/(stem+'.obj')).write_text('\n'.join(lines)+'\n')
    (MODELS/(stem+'.mtl')).write_text('\n'.join(
        f'newmtl {n}\nKd 1 1 1\nKa 0 0 0\nmap_Kd create_better_hammers:item/'+
        ('diamond_hammer_gem' if stem=='diamond_hammer' and n=='diamond' else 'pneumatic_'+n)+'\n'
        for n in materials))
    print(f'{stem}: {len(parts)} parts, {face_count} faces')

def reset():
    global lines,vertex_count,face_count,parts,current_group,group_bounds,uv_count
    lines=['# Original workshop hammer mesh; rotated -45 degrees around sprite pivot.',
           '# Handheld display transforms are fixed separately.', 'mtllib placeholder.mtl','vt 0.5 0.5']
    vertex_count=face_count=uv_count=0
    parts=[]
    current_group=''
    group_bounds=None
