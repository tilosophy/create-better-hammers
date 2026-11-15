"""Build the pneumatic workshop revision without changing its approved pose."""
from hammer_mesh import box, prism, disc, gear, save
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
box('gear_housing',[5.25,10.05,6.38],[9.65,14.65,9.62],'brass',.28)
for side,sign in [('near',1),('far',-1)]:
    def zz(a,b): return (a,b) if sign==1 else (16-b,16-a)
    z0,z1=zz(9.62,9.68)
    box(side+'_recess',[5.56,10.37,z0],[9.34,14.33,z1],'charcoal',.15)
    dz0,dz1=zz(9.69,9.80)
    gear(side+'_main_drive',7.48,12.35,dz0,dz1,1.60,'cogwood',12,0)
    hz0,hz1=zz(9.76,9.91)
    disc(side+'_main_hub',7.48,12.35,hz0,hz1,.33,'iron',8)
    z0,z1=zz(9.85,10.02)
    for tag,a,b in [
        ('top',[5.3,13.86,z0],[9.65,14.65,z1]),
        ('bottom',[5.3,10.05,z0],[9.65,10.85,z1]),
        ('front',[5.3,10.85,z0],[6.02,13.86,z1]),
        ('rear',[8.94,10.85,z0],[9.65,13.86,z1]),
    ]: box(side+'_'+tag+'_frame',a,b,'copper',.08)
    for j,(rx,ry) in enumerate([(5.59,14.28),(9.35,14.28),(5.59,10.43),(9.35,10.43)]):
        rz0,rz1=zz(10.025,10.12)
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
    box(side+'_pivot_plate',[9.28,11.68,z0],[10.65,13.18,z1],'copper',.25)
    disc(side+'_pivot_pin',10.0,12.43,z0-.04,z1+.04,.34,'iron',8)

# A centreline pipe beneath the head joins striker underside to handle socket.
# Both fittings are embedded in the parts they serve; the connection is visible from either side.
box('pipe_front_fitting',[4.10,9.65,7.66],[4.80,10.10,8.34],'copper',.12,'y')
box('pipe_drop',[4.31,9.05,7.86],[4.59,9.70,8.14],'copper',.04,'y')
box('pipe_underside',[4.31,8.96,7.86],[7.05,9.24,8.14],'copper',.04,'x')
box('pipe_socket_fitting',[6.80,8.75,7.66],[7.18,9.45,8.34],'copper',.12,'x')

# Two low rivets secure the top plate; no ambiguous vent protrusion.
for x in (6.1,8.8):
    poly=[(8+.15*math.cos(i*math.pi/4),x+.15*math.sin(i*math.pi/4)) for i in range(8)]
    prism('top_rivet_'+str(x),poly,14.65,14.77,'iron','y')

save('pneumatic_hammer')
