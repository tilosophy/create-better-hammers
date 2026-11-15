"""Rebuild the original pneumatic OBJ mesh; run from any working directory."""
from pathlib import Path
import math

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'src/main/resources/assets/create_better_hammers'
MODELS = ASSETS / 'models/item'
MODELS.mkdir(parents=True, exist_ok=True)

materials = ['brass','steel','copper','wood','diamond','iron','brass_trim','charcoal']
(MODELS/'pneumatic_hammer.mtl').write_text('\n'.join(
    f'newmtl {n}\nKd 1 1 1\nKa 0 0 0\nmap_Kd create_better_hammers:item/pneumatic_{n}\n'
    for n in materials))

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
        if 'fastener' in current_group or 'rivet' in current_group or 'pin' in current_group:
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

# Same diagonal grip centreline as the approved diamond sprite.
box('grip',[7.15,-2.5,7.15],[8.85,8.5,8.85],'grip',.18,'y')
box('butt_collar',[6.97,-2.65,6.97],[9.03,-2.15,9.03],'frame',.24,'y')
box('upper_collar',[6.98,7.45,6.98],[9.02,7.98,9.02],'frame',.22,'y')
box('socket',[7.0,7.98,7.0],[9.0,10.7,9.0],'recess',.15,'y')

# Solid head and stepped backer. Cyan exists on the actual end plate only.
box('striker_body',[3.25,10.0,6.1],[5.35,14.85,9.9],'body',.28,'x')
box('striker_gasket',[3.05,10.06,6.17],[3.3,14.79,9.83],'recess',.25,'x')
box('plate_surround',[2.65,9.88,5.98],[3.07,14.97,10.02],'frame',.32,'x')
box('diamond_end_plate',[2.42,10.23,6.33],[2.68,14.62,9.67],'plate',.20,'x')

# Raised frame around the recessed steel cog.
box('gear_housing',[5.25,10.05,6.38],[9.65,14.65,9.62],'body',.28)
for side,z0,z1 in [('near',9.62,9.84),('far',6.16,6.38)]:
    box(side+'_recess',[5.56,10.37,z0],[9.34,14.33,z1],'recess',.15)
    for tag,a,b in [
        ('top',[5.4,14.13,z0-.04],[9.48,14.61,z1+.12]),
        ('bottom',[5.4,10.10,z0-.04],[9.48,10.58,z1+.12]),
        ('front',[5.30,10.5,z0-.04],[5.78,14.2,z1+.12]),
        ('rear',[9.13,10.5,z0-.04],[9.61,14.2,z1+.12]),
    ]: box(side+'_'+tag+'_frame',a,b,'frame')
    # One steel cog sits behind the frame and partly behind a lower cover.
    dz0,dz1=(9.84,9.95) if side=='near' else (6.05,6.16)
    gear(side+'_main_drive',7.48,12.55,dz0,dz1,1.38,'iron',10,0)
    disc(side+'_main_hub',7.48,12.55,dz0-.025,dz1+.025,.33,'iron',8)
    cz0,cz1=(9.97,10.04) if side=='near' else (5.96,6.03)
    box(side+'_lower_cover',[5.75,10.48,cz0],[9.15,11.45,cz1],'steel',.12)

    for j,(rx,ry) in enumerate([(5.54,14.34),(9.37,14.34),(5.54,10.35),(9.37,10.35)]):
        rz0,rz1=(9.965,10.095) if side=='near' else (6.025,6.155)
        box(side+'_fastener_'+str(j),[rx-.12,ry-.12,rz0],[rx+.12,ry+.12,rz1],'iron',.04)

# Single connected forged C profile with an actual opening through its full depth.
jaw=[(9.40,11.00),(9.40,13.80),(10.12,14.75),(11.15,15.18),
     (12.38,15.18),(13.40,14.68),(14.03,13.91),(14.03,13.30),
     (12.97,13.30),(12.39,13.91),(11.65,13.91),(11.02,13.42),
     (10.68,12.43),(11.02,11.44),(11.65,10.95),(12.39,10.95),
     (12.97,11.56),(14.03,11.56),(14.03,10.95),(13.40,10.18),
     (12.38,9.68),(11.15,9.68),(10.12,10.11)]
prism('open_wrench',jaw,6.77,9.23,'frame')

# Pivot plates overlap the wrench root, leaving the jaw silhouette/opening intact.
for side,z0,z1 in [('near',9.24,9.48),('far',6.52,6.76)]:
    box(side+'_pivot_plate',[9.28,11.68,z0],[10.65,13.18,z1],'steel',.25)
    disc(side+'_pivot_pin',10.0,12.43,z0-.04,z1+.04,.34,'iron',8)

# Short air connection with a fitting seated into each of the two connected parts.
for x in (4.6,6.05):
    disc('pipe_fitting_'+str(x),x,11.02,9.80,10.27,.29,'copper',8)
box('pipe_link',[4.6,10.90,10.13],[6.05,11.14,10.37],'copper',.04,'x')

# Two low rivets secure the top plate; no ambiguous vent protrusion.
for x in (6.1,8.8):
    poly=[(8+.15*math.cos(i*math.pi/4),x+.15*math.sin(i*math.pi/4)) for i in range(8)]
    prism('top_rivet_'+str(x),poly,14.65,14.77,'iron','y')

(MODELS/'pneumatic_hammer.obj').write_text('\n'.join(lines)+'\n')
print(f'{len(parts)} parts; {face_count} faces; pose kept in pneumatic_hammer.json.')
