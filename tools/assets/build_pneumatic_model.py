"""Build the pneumatic workshop revision without changing its approved pose."""
from hammer_mesh import box, prism, disc, gear, save
import hammer_mesh as mesh
import math

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

# A narrower opening overlaps the perimeter of a cog recessed behind the rim.
# Keep the brass roof, floor and rear wall below the copper frame's planes.
# The previous coincident y=10.05/14.65 and x=9.65 faces flickered with shaders.
box('gear_housing',[5.25,10.10,6.38],[9.60,14.60,9.62],'brass',.28)
for side,sign in [('near',1),('far',-1)]:
    def zz(a,b): return (a,b) if sign==1 else (16-b,16-a)
    z0,z1=zz(9.62,9.68)
    box(side+'_recess',[5.56,10.37,z0],[9.34,14.33,z1],'charcoal',.15)
    dz0,dz1=zz(9.69,9.80)
    gear(side+'_main_drive',7.48,12.35,dz0,dz1,1.60,'cogwood',12,0)
    hz0,hz1=zz(9.76,9.91)
    disc(side+'_main_hub',7.48,12.35,hz0,hz1,.33,'fastener',8)
    for j,(rx,ry) in enumerate([(5.59,14.28),(9.35,14.28),(5.59,10.43),(9.35,10.43)]):
        rz0,rz1=zz(10.025,10.12)
        box(side+'_fastener_'+str(j),[rx-.12,ry-.12,rz0],[rx+.12,ry+.12,rz1],'fastener',.04)

# Single connected forged C profile with an actual opening through its full depth.
jaw=[(9.40,11.00),(9.40,13.80),(10.12,14.75),(11.15,15.18),
     (12.38,15.18),(13.40,14.68),(14.03,13.91),(14.03,13.30),
     (12.97,13.30),(12.39,13.91),(11.65,13.91),(11.02,13.42),
     (10.68,12.43),(11.02,11.44),(11.65,10.95),(12.39,10.95),
     (12.97,11.56),(14.03,11.56),(14.03,10.95),(13.40,10.18),
     (12.38,9.68),(11.15,9.68),(10.12,10.11)]
prism('open_wrench',jaw,6.77,9.23,'frame')

# One frame with a real aperture and an integrated pivot lug. All cap patches
# share a UV projection and plane; there are no bevels or walls between rails.
outer=[(5.30,10.05),(9.65,10.05),(9.65,11.68),(10.40,11.68),
       (10.65,11.93),(10.65,12.93),(10.40,13.18),(9.65,13.18),
       (9.65,14.65),(5.30,14.65)]
inner=[(6.02,10.85),(8.94,10.85),(8.94,13.86),(6.02,13.86)]
patches=[
    [outer[0],outer[1],inner[1],inner[0]],
    [outer[1],*outer[2:9],inner[2],inner[1]],
    [outer[8],outer[9],inner[3],inner[2]],
    [outer[9],outer[0],inner[0],inner[3]],
]
for side,z0,z1,p0,p1 in [('near',9.18,10.02,10.02,10.16),('far',5.98,6.82,5.84,5.98)]:
    mesh.group(side+'_continuous_frame_and_pivot')
    mesh.group_bounds=([5.30,10.05,z0],[10.65,14.65,z1])
    for patch in patches:
        for a,b,c in mesh.triangulate(patch):
            mesh.face([(*patch[i],z0) for i in (c,b,a)],'copper')
            mesh.face([(*patch[i],z1) for i in (a,b,c)],'copper')
    for boundary in (outer,inner[::-1]):
        for a,b in zip(boundary,boundary[1:]+boundary[:1]):
            mesh.face([(*a,z0),(*b,z0),(*b,z1),(*a,z1)],'copper')
    disc(side+'_pivot_pin',10.0,12.43,p0,p1,.34,'fastener',8)

# A centreline pipe beneath the head joins striker underside to handle socket.
# Both fittings are embedded in the parts they serve; the connection is visible from either side.
box('pipe_front_fitting',[3.97,9.54,7.52],[4.93,10.12,8.48],'copper',.14,'y')
box('pipe_drop',[4.20,8.98,7.75],[4.70,9.72,8.25],'copper',.07,'y')
box('pipe_underside',[4.20,8.85,7.75],[7.12,9.35,8.25],'copper',.07,'x')
box('pipe_socket_fitting',[6.72,8.62,7.52],[7.22,9.58,8.48],'copper',.14,'x')

# Two low rivets secure the top plate; no ambiguous vent protrusion.
for x in (6.1,8.8):
    poly=[(8+.15*math.cos(i*math.pi/4),x+.15*math.sin(i*math.pi/4)) for i in range(8)]
    prism('top_rivet_'+str(x),poly,14.60,14.77,'fastener','y')

save('pneumatic_hammer')
