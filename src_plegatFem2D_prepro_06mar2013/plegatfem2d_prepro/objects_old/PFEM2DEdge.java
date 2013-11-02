/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author JMB
 */
public class PFEM2DEdge implements IPFEM2DDrawableObject, IPFEM2DElement {

    private PFEM2DNode nd1, nd2;
    private long id;
    private IPFEM2DElement[] connected;

    public PFEM2DEdge(long id, PFEM2DNode nd1, PFEM2DNode nd2) {

        if (nd1.getId().compareTo(nd2.getId()) < 0) {
            this.nd1 = nd1;
            this.nd2 = nd2;
        } else {
            this.nd1 = nd2;
            this.nd2 = nd1;
        }

        this.id = id;


        this.connected = new IPFEM2DElement[2];
        this.initConnectedElements();

    }

    public final void initConnectedElements() {
        this.connected[0] = null;
        this.connected[1] = null;
    }

    public boolean areConnectedElementsDefined() {
        return ((this.connected[0] != null) && (this.connected[1] != null));
    }

    @Override
    public void draw(Graphics g, PFem2DGuiPanel panel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVisible(boolean flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isVisible() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getId() {
        return "Edge " + this.id;
    }

    @Override
    public long getNumId() {
        return this.id;
    }

    @Override
    public PFEM2DNode[] getNodes() {

        PFEM2DNode[] tempArray = new PFEM2DNode[2];
        tempArray[0] = this.nd1;
        tempArray[1] = this.nd2;

        return tempArray;
    }

    @Override
    public PFEM2DNode getNode(int rank) {

        if (rank == 0) {
            return this.nd1;
        } else if (rank == 1) {
            return this.nd2;
        } else {
            return null;
        }


    }

    public IPFEM2DElement[] getConnectedElements() {
        return connected;
    }

    public void findConnectedElements() {

        ArrayList<IPFEM2DElement> connected1 = this.nd1.getConnectedElements();
        ArrayList<IPFEM2DElement> connected2 = this.nd2.getConnectedElements();


        ArrayList<IPFEM2DElement> common = new ArrayList<IPFEM2DElement>();

        for (Iterator<IPFEM2DElement> it = connected1.iterator(); it.hasNext();) {
            IPFEM2DElement iPFEM2DElement = it.next();
            //System.out.println("testing element " + iPFEM2DElement.getId());

            for (Iterator<IPFEM2DElement> it1 = connected2.iterator(); it1.hasNext();) {
                IPFEM2DElement iPFEM2DElement1 = it1.next();
                //System.out.println("   with element " + iPFEM2DElement1.getId());

                if (iPFEM2DElement == iPFEM2DElement1) {
                    common.add(iPFEM2DElement);
                    //System.out.println("adding element " + iPFEM2DElement.getId() + " to edge " + this.nd1.getId() + "/" + this.nd2.getId());
                    break;
                }

            }

        }



        this.connected[0] = common.get(0);
        if (common.size() > 1) {
            this.connected[1] = common.get(1);
        } else {
            this.connected[1] = null;
        }



    }

    @Override
    public boolean isElementEdge(PFEM2DEdge edge) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return "Edge_" + this.nd1.getId() + "_" + this.nd2.getId();
    }

    public PFEM2DPoint getIntersection(PFEM2DEdge edge, boolean flagEnds) {

        return this.getIntersection(edge.nd1, edge.nd2, flagEnds);

    }

    public PFEM2DPoint getIntersection(PFEM2DNode node1, PFEM2DNode node2, boolean flagEnds) {

        //System.out.println(" ");
        //System.out.println("checking intersection " + node1.getId() + "-" + node2.getId());

        double epsilon = 1e-5;

        double ex1 = this.nd1.getX();
        double ey1 = this.nd1.getY();

        double ex2 = this.nd2.getX();
        double ey2 = this.nd2.getY();

        double nx1 = node1.getX();
        double ny1 = node1.getY();

        double nx2 = node2.getX();
        double ny2 = node2.getY();

        /*
         System.out.println("E1: "+ex1+"/"+ey1);
         System.out.println("E2: "+ex2+"/"+ey2);
         System.out.println("N1: "+nx1+"/"+ny1);
         System.out.println("N2: "+nx2+"/"+ny2);
         */

        double a, b;

        if (Math.abs(ex2 - ex1) < epsilon) {
            if (Math.abs((nx2 - nx1) * (ey2 - ey1)) < epsilon) {
                //System.out.println("condition 1");
                return null;
            } else {
                //System.out.println("formule 1");
                b = (ex1 - nx1) / (nx2 - nx1);
                a = ((ny1 - ey1) + b * (ny2 - ny1)) / (ey2 - ey1);
            }
        } else {

            double denom = (nx2 - nx1) * (ey2 - ey1) - (ny2 - ny1) * (ex2 - ex1);

            if (Math.abs(denom) < epsilon) {
                //System.out.println("condition 3");
                return null;
            } else {
                //System.out.println("formule 2");
                b = ((ny1 - ey1) * (ex2 - ex1) - (nx1 - ex1) * (ey2 - ey1)) / denom;
                a = (nx1 - ex1 + b * (nx2 - nx1)) / (ex2 - ex1);
            }




        }

        //System.out.println("a= " + a + ", b= " + b);

        if ((a > 0) && (a < 1) && (b > 0) && (b < 1)) {
            return this.nd1.getInterpolatedPoint(this.nd2, a);
        } else if ((flagEnds) && ((Math.abs(a) < epsilon) || (Math.abs(a - 1) < epsilon)) && ((Math.abs(b) < epsilon) || (Math.abs(b - 1) < epsilon))) {
            return this.nd1.getInterpolatedPoint(this.nd2, a);
        } else {
            //System.out.println("a= " + a + ", b= " + b);
            //System.out.println("condition 5");
            return null;
        }


    }

    public boolean isSame(PFEM2DEdge edge2) {

        return (this.toString().compareTo(edge2.toString()) == 0);

    }

    public double getLength() {

        return this.nd1.getDistanceTo(this.nd2);

    }

    public PFEM2DPoint getMidPoint() {

        return this.nd1.getInterpolatedPoint(nd2, 0.5);


    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Math.min(this.nd1.getX(), this.nd2.getX());
        data[1] = Math.max(this.nd1.getX(), this.nd2.getX());
        data[2] = Math.min(this.nd1.getY(), this.nd2.getY());
        data[3] = Math.max(this.nd1.getY(), this.nd2.getY());

        return data;
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        return null;
    }
}
