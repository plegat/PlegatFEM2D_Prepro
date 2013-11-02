# définition du modèle

        PFEM2DPoint pt1 = new PFEM2DPoint(1, 20, 20);
        PFEM2DPoint pt2 = new PFEM2DPoint(2, 120, 20);
        PFEM2DPoint pt3 = new PFEM2DPoint(3, 120, 120);
        PFEM2DPoint pt4 = new PFEM2DPoint(4, 20, 120);

        PFEM2DPoint pt101 = new PFEM2DPoint(101, 20, 30);
        PFEM2DPoint pt102 = new PFEM2DPoint(102, 120, 30);
        PFEM2DPoint pt103 = new PFEM2DPoint(103, 120, 110);
        PFEM2DPoint pt104 = new PFEM2DPoint(104, 20, 110);

        PFEM2DPoint pt5 = pt1.getRotated(pt101, -90);
        pt5.setId(5);
        PFEM2DPoint pt6 = pt2.getRotated(pt102, 90);
        pt6.setId(6);
        PFEM2DPoint pt7 = pt3.getRotated(pt103, -90);
        pt7.setId(7);
        PFEM2DPoint pt8 = pt4.getRotated(pt104, 90);
        pt8.setId(8);

        PFEM2DLine line1 = new PFEM2DLine(1, pt1, pt2);
        PFEM2DLine line2 = new PFEM2DLine(2, pt6, pt7);
        PFEM2DLine line3 = new PFEM2DLine(3, pt3, pt4);
        PFEM2DLine line4 = new PFEM2DLine(4, pt8, pt5);

        line1.setNbElements(4);
        line2.setNbElements(4);
        line3.setNbElements(1);
        line4.setNbElements(4);

        PFEM2DPoint pt9 = new PFEM2DPoint(9, 70, 90);
        PFEM2DPoint pt10 = new PFEM2DPoint(10, 90, 90);

        PFEM2DCircle2Pt circle5 = new PFEM2DCircle2Pt(5, pt9, pt10);
        circle5.setNbElements(12);

        PFEM2DArcCircle3Pt arc6 = new PFEM2DArcCircle3Pt(6, pt101, pt5, pt1);
        PFEM2DArcCircle3Pt arc7 = new PFEM2DArcCircle3Pt(7, pt102, pt2, pt6);
        PFEM2DArcCircle3Pt arc8 = new PFEM2DArcCircle3Pt(8, pt103, pt7, pt3);
        PFEM2DArcCircle3Pt arc9 = new PFEM2DArcCircle3Pt(9, pt104, pt4, pt8);

        arc6.setNbElements(3);
        arc7.setNbElements(3);
        arc8.setNbElements(3);
        arc9.setNbElements(3);

        PFEM2DMultiLine loop = new PFEM2DMultiLine();

        loop.add(line1);
        loop.add(arc6);

        loop.add(line2);
        loop.add(arc7);

        loop.add(line3);
        loop.add(arc8);

        loop.add(line4);
        loop.add(arc9);

        loop.validate();

        PFEM2DLoopLine loopExt;
        
            loopExt = new PFEM2DLoopLine(loop);

            #rectangle interne

            PFEM2DPoint pt11 = new PFEM2DPoint(11, 20, 30);
            PFEM2DPoint pt12 = new PFEM2DPoint(12, 120, 30);
            PFEM2DPoint pt13 = new PFEM2DPoint(13, 120, 60);
            PFEM2DPoint pt14 = new PFEM2DPoint(14, 20, 60);

            PFEM2DLine line5 = new PFEM2DLine(5, pt11, pt12);
            PFEM2DLine line6 = new PFEM2DLine(6, pt12, pt13);
            PFEM2DLine line7 = new PFEM2DLine(7, pt13, pt14);
            PFEM2DLine line8 = new PFEM2DLine(8, pt14, pt11);

            PFEM2DMultiLine multi2 = new PFEM2DMultiLine();
            multi2.add(line5);
            multi2.add(line6);
            multi2.add(line7);
            multi2.add(line8);

            PFEM2DLoopLine loopInt = new PFEM2DLoopLine(multi2);

            PFEM2DSurface surf = new PFEM2DSurface(1);

            surf.add(loopExt);
            surf.add(circle5);
            surf.add(loopInt);

            #surf.mesh(pom);

            PFEM2DModel model = new PFEM2DModel();
            model.addObject(surf);

            pom.addObject(model);
