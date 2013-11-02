from plegatfem2d_prepro import *
from plegatfem2d_prepro.objects import *
from math import pi, sin, cos

# définition du modèle

pt1 = PFEM2DPoint(1, 0, 0);
pt2 = PFEM2DPoint(2, 100, 0);

circle3 = PFEM2DCircle2Pt(3, pt1, pt2);
circle3.setNbElements(36);



surf = PFEM2DSurface(1);

surf.add(circle3);

for i in range(0,8):
	pt4=PFEM2DPoint(100+i, 30*cos(i*pi/4), 30*sin(i*pi/4));
	pt5=PFEM2DPoint(200+i, 30*cos(i*pi/4)+5, 30*sin(i*pi/4));
	circle6 = PFEM2DCircle2Pt(300+i, pt4, pt5);
	circle6.setNbElements(8);
	surf.add(circle6);

for i in range(0,12):
	pt4=PFEM2DPoint(100+i, 60*cos(i*pi/6), 60*sin(i*pi/6));
	pt5=PFEM2DPoint(200+i, 60*cos(i*pi/6)+10, 60*sin(i*pi/6));
	circle6 = PFEM2DCircle2Pt(300+i, pt4, pt5);
	circle6.setNbElements(12);
	surf.add(circle6);


model = PFEM2DModel();
model.addObject(surf);

pom.addObject(model);
