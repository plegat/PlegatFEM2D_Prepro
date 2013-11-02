package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;
import plegatfem2d_prepro.PFEM2DObjectManager;

/**
 *
 * @author Jean-Michel BORLOT
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
    public void draw(Graphics g, PFEM2DGuiPanel panel) {
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

    public boolean findConnectedElements() {

        List<IPFEM2DElement> connected1 = this.nd1.getConnectedElements();
        List<IPFEM2DElement> connected2 = this.nd2.getConnectedElements();


        List<IPFEM2DElement> common = new ArrayList<>();

        for (IPFEM2DElement iPFEM2DElement : connected1) {
            for (IPFEM2DElement iPFEM2DElement1 : connected2) {
                if (iPFEM2DElement == iPFEM2DElement1) {
                    common.add(iPFEM2DElement);
                    break;
                }
            }
        }

        if (common.isEmpty()) {
            return false;
        } else {
            this.connected[0] = common.get(0);
            if (common.size() > 1) {
                this.connected[1] = common.get(1);
            } else {
                this.connected[1] = null;
            }

            return true;
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

    public PFEM2DPoint getIntersection(PFEM2DObjectManager pom, PFEM2DEdge edge, boolean flagEnds) {
        return this.getIntersection(pom.getIdCurrentPoint(), edge.nd1, edge.nd2, flagEnds);
    }

    public PFEM2DPoint getIntersection(long id, PFEM2DEdge edge, boolean flagEnds) {
        return this.getIntersection(id, edge.nd1, edge.nd2, flagEnds);
    }

    public PFEM2DPoint getIntersection(PFEM2DObjectManager pom, PFEM2DNode node1, PFEM2DNode node2, boolean flagEnds) {
        return this.getIntersection(pom.getIdCurrentPoint(), node1, node2, flagEnds);
    }

    public PFEM2DPoint getIntersection(long id, PFEM2DNode node1, PFEM2DNode node2, boolean flagEnds) {

        double epsilon = 1e-5;

        double ex1 = this.nd1.getX();
        double ey1 = this.nd1.getY();

        double ex2 = this.nd2.getX();
        double ey2 = this.nd2.getY();

        double nx1 = node1.getX();
        double ny1 = node1.getY();

        double nx2 = node2.getX();
        double ny2 = node2.getY();

        double a, b;

        if (Math.abs(ex2 - ex1) < epsilon) {
            if (Math.abs((nx2 - nx1) * (ey2 - ey1)) < epsilon) {
                return null;
            } else {
                b = (ex1 - nx1) / (nx2 - nx1);
                a = ((ny1 - ey1) + b * (ny2 - ny1)) / (ey2 - ey1);
            }
        } else {

            double denom = (nx2 - nx1) * (ey2 - ey1) - (ny2 - ny1) * (ex2 - ex1);

            if (Math.abs(denom) < epsilon) {
                return null;
            } else {
                b = ((ny1 - ey1) * (ex2 - ex1) - (nx1 - ex1) * (ey2 - ey1)) / denom;
                a = (nx1 - ex1 + b * (nx2 - nx1)) / (ex2 - ex1);
            }
        }

        if ((a > 0) && (a < 1) && (b > 0) && (b < 1)) {
            return this.nd1.getInterpolatedPoint(id, this.nd2, a);
        } else if ((flagEnds) && ((Math.abs(a) < epsilon) || (Math.abs(a - 1) < epsilon)) && ((Math.abs(b) < epsilon) || (Math.abs(b - 1) < epsilon))) {
            return this.nd1.getInterpolatedPoint(id, this.nd2, a);
        } else {
            return null;
        }
    }

    public boolean isSame(PFEM2DEdge edge2) {
        return (this.toString().compareTo(edge2.toString()) == 0);
    }

    public double getLength() {
        return this.nd1.getDistanceTo(this.nd2);
    }

    public PFEM2DPoint getMidPoint(PFEM2DObjectManager pom) {
        return this.getMidPoint(pom.getIdCurrentPoint());
    }

    public PFEM2DPoint getMidPoint(long id) {
        return this.nd1.getInterpolatedPoint(id, nd2, 0.5);
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

    public double getAngle(PFEM2DPoint pt) {

        double l12 = this.getLength();
        double l23 = this.getNode(0).getDistanceTo(pt);
        double l31 = this.getNode(1).getDistanceTo(pt);

        double angle = Math.acos((Math.pow(l12, 2) - Math.pow(l23, 2) - Math.pow(l31, 2)) / (-2 * l23 * l31));

        return angle;
    }

    public double getAngle(PFEM2DNode node) {

        double l12 = this.getLength();
        double l23 = this.getNode(0).getDistanceTo(node);
        double l31 = this.getNode(1).getDistanceTo(node);

        double angle = Math.acos((Math.pow(l12, 2) - Math.pow(l23, 2) - Math.pow(l31, 2)) / (-2 * l23 * l31));

        return angle;
    }
}
