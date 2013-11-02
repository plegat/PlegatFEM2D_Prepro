from plegatfem2d_prepro import *
from plegatfem2d_prepro.objects import *
from math import pi, sin, cos

# définition du modèle

pt1 = PFEM2DPoint(1, -100, -100);
pt2 = PFEM2DPoint(2, -100, 100);
pt3 = PFEM2DPoint(3, 100, 100);
pt4 = PFEM2DPoint(4, 100, -100);

line1 = PFEM2DLine(5, pt1, pt2);
line2 = PFEM2DLine(6, pt2, pt3);
line3 = PFEM2DLine(7, pt3, pt4);
line4 = PFEM2DLine(8, pt4, pt1);

line1.setNbElements(10);
line2.setNbElements(10);
line3.setNbElements(10);
line4.setNbElements(10);

multi2 = PFEM2DMultiLine();
multi2.add(line1);
multi2.add(line2);
multi2.add(line3);
multi2.add(line4);



surf = PFEM2DSurface(1);

surf.add(multi2);

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
