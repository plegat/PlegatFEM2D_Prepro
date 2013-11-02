from plegatfem2d_prepro import *
from plegatfem2d_prepro.objects import *

# définition du modèle

pt1 = PFEM2DPoint(1, 20, 20);
pt2 = PFEM2DPoint(2, 120, 20);
pt3 = PFEM2DPoint(3, 120, 120);
pt4 = PFEM2DPoint(4, 20, 120);

pt101 = PFEM2DPoint(101, 20, 30);
pt102 = PFEM2DPoint(102, 120, 30);
pt103 = PFEM2DPoint(103, 120, 110);
pt104 = PFEM2DPoint(104, 20, 110);

pt5 = pt1.getRotated(pt101, -90);
pt5.setId(5);
pt6 = pt2.getRotated(pt102, 90);
pt6.setId(6);
pt7 = pt3.getRotated(pt103, -90);
pt7.setId(7);
pt8 = pt4.getRotated(pt104, 90);
pt8.setId(8);

line1 = PFEM2DLine(1, pt1, pt2);
line2 = PFEM2DLine(2, pt6, pt7);
line3 = PFEM2DLine(3, pt3, pt4);
line4 = PFEM2DLine(4, pt8, pt5);

line1.setNbElements(4);
line2.setNbElements(4);
line3.setNbElements(1);
line4.setNbElements(4);

pt9 = PFEM2DPoint(9, 70, 90);
pt10 = PFEM2DPoint(10, 90, 90);

circle5 = PFEM2DCircle2Pt(5, pt9, pt10);
circle5.setNbElements(12);

arc6 = PFEM2DArcCircle3Pt(6, pt101, pt5, pt1);
arc7 = PFEM2DArcCircle3Pt(7, pt102, pt2, pt6);
arc8 = PFEM2DArcCircle3Pt(8, pt103, pt7, pt3);
arc9 = PFEM2DArcCircle3Pt(9, pt104, pt4, pt8);

arc6.setNbElements(3);
arc7.setNbElements(3);
arc8.setNbElements(3);
arc9.setNbElements(3);

loop = PFEM2DMultiLine();

loop.add(line1);
loop.add(arc6);

loop.add(line2);
loop.add(arc7);

loop.add(line3);
loop.add(arc8);

loop.add(line4);
loop.add(arc9);

loop.validate();

loopExt = PFEM2DLoopLine(loop);

#rectangle interne

pt11 = PFEM2DPoint(11, 20, 30);
pt12 = PFEM2DPoint(12, 120, 30);
pt13 = PFEM2DPoint(13, 120, 60);
pt14 = PFEM2DPoint(14, 20, 60);

line5 = PFEM2DLine(5, pt11, pt12);
line6 = PFEM2DLine(6, pt12, pt13);
line7 = PFEM2DLine(7, pt13, pt14);
line8 = PFEM2DLine(8, pt14, pt11);

multi2 = PFEM2DMultiLine();
multi2.add(line5);
multi2.add(line6);
multi2.add(line7);
multi2.add(line8);

loopInt = PFEM2DLoopLine(multi2);

surf = PFEM2DSurface(1);

surf.add(loopExt);
surf.add(circle5);
surf.add(loopInt);

#surf.mesh(pom);

model = PFEM2DModel();
model.addObject(surf);

pom.addObject(model);
