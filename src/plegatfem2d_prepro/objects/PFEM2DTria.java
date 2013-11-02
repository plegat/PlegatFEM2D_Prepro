package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DTria implements IPFEM2DDrawableObject, IPFEM2DElement {

    private PFEM2DNode nd1, nd2, nd3;
    private long id;
    private double modulus, thickness;
    private boolean visible;
    private PFEM2DPoint centre;
    private double radius;
    private IPFEM2DElement[] neighbours = new IPFEM2DElement[3];
    private int location;
    public static int NOT_LOCALIZED = 0;
    public static int OUTSIDE = 1;
    public static int INSIDE = 2;

    public PFEM2DTria(long id, PFEM2DNode nd1, PFEM2DNode nd2, PFEM2DNode nd3, double modulus, double thickness) {
        this.nd1 = nd1;
        this.nd2 = nd2;
        this.nd3 = nd3;
        this.id = id;
        this.modulus = modulus;
        this.thickness = thickness;

        this.visible = true;
        this.location = NOT_LOCALIZED;

        this.initNeighbours();

        this.defCentre();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMeshProperties(double modulus, double thickness) {
        this.modulus = modulus;
        this.thickness = thickness;
    }

    public double getModulus() {
        return modulus;
    }

    public double getThickness() {
        return thickness;
    }

    public final void initNeighbours() {
        for (int i = 0; i < neighbours.length; i++) {
            this.neighbours[i] = null;
        }
    }

    public final void defCentre() {

        double xab = nd2.getX() - nd1.getX();
        double xca = nd1.getX() - nd3.getX();

        double yab = nd2.getY() - nd1.getY();
        double yca = nd1.getY() - nd3.getY();

        double normeAB = xab * xab + yab * yab;
        double normeCA = xca * xca + yca * yca;

        xab /= normeAB;
        yab /= normeAB;
        xca /= normeCA;
        yca /= normeCA;

        double xd = (nd1.getX() + nd2.getX()) / 2.0;
        double yd = (nd1.getY() + nd2.getY()) / 2.0;
        double xf = (nd1.getX() + nd3.getX()) / 2.0;
        double yf = (nd1.getY() + nd3.getY()) / 2.0;

        double alpha, beta;

        if (Math.abs(yab) < 1e-5) {
            beta = (xd - xf) / yca;
            alpha = (yd - yf + beta * xca) / xab;
        } else {
            beta = (yd - yf - (xf - xd) * xab / yab) / (yca * xab / yab - xca);
            alpha = (xf - xd + beta * yca) / yab;
        }

        this.centre = new PFEM2DPoint(0, xd + alpha * yab, yd - alpha * xab);
        this.radius = this.nd1.getDistanceTo(this.centre);
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

        if (this.isVisible()) {
            int xloc1 = panel.getLocalCoordX(this.nd1.getX());
            int yloc1 = panel.getLocalCoordY(this.nd1.getY());

            int xloc2 = panel.getLocalCoordX(this.nd2.getX());
            int yloc2 = panel.getLocalCoordY(this.nd2.getY());

            int xloc3 = panel.getLocalCoordX(this.nd3.getX());
            int yloc3 = panel.getLocalCoordY(this.nd3.getY());

            int xlocC = (xloc1 + xloc2 + xloc3) / 3;
            int ylocC = (yloc1 + yloc2 + yloc3) / 3;

            double coef = 0.9;

            xloc1 = (int) Math.round(xlocC + coef * (xloc1 - xlocC));
            yloc1 = (int) Math.round(ylocC + coef * (yloc1 - ylocC));

            xloc2 = (int) Math.round(xlocC + coef * (xloc2 - xlocC));
            yloc2 = (int) Math.round(ylocC + coef * (yloc2 - ylocC));

            xloc3 = (int) Math.round(xlocC + coef * (xloc3 - xlocC));
            yloc3 = (int) Math.round(ylocC + coef * (yloc3 - ylocC));

            g.setColor(Color.cyan);
            g.drawLine(xloc1, yloc1, xloc2, yloc2);
            g.drawLine(xloc2, yloc2, xloc3, yloc3);
            g.drawLine(xloc3, yloc3, xloc1, yloc1);

            //dessin id

            g.drawString("T" + this.id, xlocC, ylocC);
        }
    }

    @Override
    public void setVisible(boolean flag) {
        this.visible = flag;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public String getId() {
        return "Tria " + this.id;
    }

    @Override
    public long getNumId() {
        return this.id;
    }

    @Override
    public PFEM2DNode[] getNodes() {

        PFEM2DNode[] tempArray = new PFEM2DNode[3];
        tempArray[0] = this.nd1;
        tempArray[1] = this.nd2;
        tempArray[2] = this.nd3;

        return tempArray;
    }

    @Override
    public PFEM2DNode getNode(int rank) {

        if (rank == 0) {
            return this.nd1;
        } else if (rank == 1) {
            return this.nd2;
        } else if (rank == 2) {
            return this.nd3;
        } else {
            return null;
        }
    }

    public void setNode(int rank, PFEM2DNode node) {
        if (rank == 0) {
            this.nd1 = node;
        } else if (rank == 1) {
            this.nd2 = node;
        } else if (rank == 2) {
            this.nd3 = node;
        }
    }

    public void setNodes(PFEM2DNode node1, PFEM2DNode node2, PFEM2DNode node3) {
        this.nd1 = node1;
        this.nd2 = node2;
        this.nd3 = node3;
    }

    public boolean isInCircle(PFEM2DNode node) {
        if (node.getDistanceTo(this.centre) <= (this.radius + 1e-8)) {
            return true;
        } else {
            return false;
        }
    }

    public double[] getLocalCoordinate(PFEM2DNode node) {
        return getLocalCoordinate(this.nd1, this.nd2, this.nd3, node);
    }

    public static double[] getLocalCoordinate(PFEM2DNode origin, PFEM2DNode xAxis, PFEM2DNode yAxis, PFEM2DNode node) {

        double epsilon = 1e-5;

        double xa = origin.getX();
        double ya = origin.getY();

        double xb = xAxis.getX();
        double yb = xAxis.getY();

        double xc = yAxis.getX();
        double yc = yAxis.getY();

        double xm = node.getX();
        double ym = node.getY();

        double[] lambda = new double[2];

        if (Math.abs(xb - xa) < epsilon) {
            if ((Math.abs(xc - xa) < epsilon) || (Math.abs(yb - ya) < epsilon)) {
                return null;
            } else {
                lambda[1] = (xm - xa) / (xc - xa);
                lambda[0] = ((ym - ya) - lambda[1] * (yc - ya)) / (yb - ya);
            }
        } else if (Math.abs(yc - ya) < epsilon) {
            if ((Math.abs(yb - ya) < epsilon) || (Math.abs(xc - xa) < epsilon)) {
                return null;
            } else {
                lambda[0] = (ym - ya) / (yb - ya);
                lambda[1] = ((xm - xa) - lambda[0] * (xb - xa)) / (xc - xa);
            }
        } else {
            double a = (yc - ya) * (xb - xa) - (xc - xa) * (yb - ya);

            if (Math.abs(a) < epsilon) {
                return null;
            } else {
                double b = (ym - ya) * (xb - xa) - (xm - xa) * (yb - ya);
                lambda[1] = b / a;
                lambda[0] = ((xm - xa) - lambda[1] * (xc - xa)) / (xb - xa);
            }
        }

        return lambda;
    }

    public static int getNodeRelativePosition(PFEM2DNode origin, PFEM2DNode xAxis, PFEM2DNode yAxis, PFEM2DNode node) {

        double epsilon = 1e-5;

        double[] lambda = getLocalCoordinate(origin, xAxis, yAxis, node);

        int local = 0;

        if (Math.abs(lambda[0]) < epsilon) {
            if (lambda[1] > 0) {
                if (lambda[0] + lambda[1] > 1) {
                    local = 13;
                } else {
                    local = 10;
                }
            } else if (lambda[1] < 0) {
                local = 16;
            }
        } else if (lambda[0] > 0) {
            if (Math.abs(lambda[1]) < epsilon) {
                if (lambda[0] + lambda[1] > 1) {
                    local = 12;
                } else {
                    local = 8;
                }
            } else if (lambda[1] > 0) {
                if (Math.abs(lambda[0] + lambda[1] - 1) < epsilon) {
                    local = 9;
                } else if (lambda[0] + lambda[1] > 1) {
                    local = 4;
                } else {
                    local = 1;
                }
            } else if (lambda[1] < 0) {
                if (Math.abs(lambda[0] + lambda[1] - 1) < epsilon) {
                    local = 11;
                } else if (lambda[0] + lambda[1] > 1) {
                    local = 3;
                } else {
                    local = 2;
                }
            }
        } else if (lambda[0] < 0) {
            if (Math.abs(lambda[1]) < epsilon) {

                local = 15;

            } else if (lambda[1] > 0) {
                if (Math.abs(lambda[0] + lambda[1] - 1) < epsilon) {
                    local = 14;
                } else if (lambda[0] + lambda[1] > 1) {
                    local = 5;
                } else {
                    local = 6;
                }
            } else if (lambda[1] < 0) {

                local = 7;

            }
        }

        return local;
    }

    public int getNodeRelativePosition(PFEM2DNode node) {
        return getNodeRelativePosition(this.nd1, this.nd2, this.nd3, node);
    }

    public boolean isNodeIntersectingOppositeEdge(PFEM2DNode nodeTria, PFEM2DNode node) {

        int local = this.getNodeRelativePosition(node);

        if ((nodeTria == nd1) && (local == 4)) {
            return true;
        }
        if ((nodeTria == nd2) && (local == 6)) {
            return true;
        }
        if ((nodeTria == nd3) && (local == 2)) {
            return true;
        }

        return false;
    }

    public int isEdgeIntersecting(PFEM2DEdge edge, boolean flagEnds) {

        PFEM2DPoint pt1 = edge.getIntersection(0, nd1, nd2, flagEnds);
        PFEM2DPoint pt2 = edge.getIntersection(0, nd2, nd3, flagEnds);
        PFEM2DPoint pt3 = edge.getIntersection(0, nd3, nd1, flagEnds);

        int val = 0;

        if (pt1 != null) {
            val = val + 1;
        }
        if (pt2 != null) {
            val = val + 2;
        }
        if (pt3 != null) {
            val = val + 4;
        }

        return val;
    }

    public PFEM2DEdge getEdgeOppositeToNode(int nodeIndex) {

        switch (nodeIndex) {
            case 0:
                return this.getEdge(2);

            case 1:
                return this.getEdge(4);

            case 2:
                return this.getEdge(1);

            default:
                return null;
        }
    }

    public PFEM2DEdge getEdge(int edge) {

        switch (edge) {
            case 1:
                return new PFEM2DEdge(0, nd1, nd2);

            case 2:
                return new PFEM2DEdge(0, nd2, nd3);

            case 4:
                return new PFEM2DEdge(0, nd3, nd1);

            default:
                return null;
        }
    }

    public List<PFEM2DEdge> getAllEdges(int edge) {

        List<PFEM2DEdge> temp = new ArrayList<>();

        if ((edge & 1) == 1) {
            temp.add(new PFEM2DEdge(0, nd1, nd2));
        }
        if ((edge & 2) == 2) {
            temp.add(new PFEM2DEdge(0, nd2, nd3));
        }
        if ((edge & 4) == 4) {
            temp.add(new PFEM2DEdge(0, nd3, nd1));
        }

        return temp;
    }

    public void findNeighbours() {

        for (int i = 0; i < 3; i++) {

            PFEM2DEdge edge;

            if (i == 0) {
                edge = new PFEM2DEdge(0, nd2, nd3);
            } else if (i == 1) {
                edge = new PFEM2DEdge(0, nd3, nd1);
            } else {
                edge = new PFEM2DEdge(0, nd1, nd2);
            }

            edge.findConnectedElements();
            IPFEM2DElement[] connected = edge.getConnectedElements();

            if (connected[0] == this) {
                this.neighbours[i] = connected[1];
            } else if (connected[1] == this) {
                this.neighbours[i] = connected[0];
            } else {
                this.neighbours[i] = null;
            }
        }
    }

    public void updateNeighbours() {

        this.findNeighbours();

        for (int i = 0; i < 3; i++) {
            IPFEM2DElement iPFEM2DElement = neighbours[i];

            if (iPFEM2DElement instanceof PFEM2DTria) {
                ((PFEM2DTria) iPFEM2DElement).findNeighbours();
            }
        }
    }

    public IPFEM2DElement getNeighbours(int node) {
        return this.neighbours[node];
    }

    public IPFEM2DElement getNeighbours(PFEM2DNode node) {

        if (node == this.nd1) {
            return this.neighbours[0];
        } else if (node == this.nd2) {
            return this.neighbours[1];
        } else if (node == this.nd3) {
            return this.neighbours[2];
        } else {
            return null;
        }
    }

    @Override
    public boolean isElementEdge(PFEM2DEdge edge) {

        PFEM2DNode nodEdge1 = edge.getNode(0);
        PFEM2DNode nodEdge2 = edge.getNode(1);

        boolean flag = true;

        flag = flag && ((nodEdge1 == this.nd1) || (nodEdge1 == this.nd2) || (nodEdge1 == this.nd3));
        flag = flag && ((nodEdge2 == this.nd1) || (nodEdge2 == this.nd2) || (nodEdge2 == this.nd3));

        return flag;
    }

    public int getNodeVertexRank(PFEM2DNode node) {

        if (node == this.nd1) {
            return 0;
        } else if (node == this.nd2) {
            return 1;
        } else if (node == this.nd3) {
            return 2;
        } else {
            return -1;
        }
    }

    public boolean swapDiagonal(PFEM2DTria tria) {

        PFEM2DNode[] thisNodes = this.getNodes();
        PFEM2DNode[] triaNodes = tria.getNodes();

        int diag1 = -1;
        int diag2 = -1;

        for (int i = 0; i < 3; i++) {
            if (tria == this.neighbours[i]) {
                diag1 = i;
            }
            if (this == tria.neighbours[i]) {
                diag2 = i;
            }
        }

        if ((diag1 != -1) && (diag2 != -1)) {

            int rank2 = getNodeRelativePosition(thisNodes[diag1],
                    thisNodes[(diag1 + 1) % 3], thisNodes[(diag1 + 2) % 3],
                    triaNodes[diag2]);

            if (rank2 == 4) {

                PFEM2DNode diagNode1 = thisNodes[diag1];
                PFEM2DNode diagNode2 = triaNodes[diag2];
                PFEM2DNode remain1 = thisNodes[(diag1 + 1) % 3];
                PFEM2DNode remain2 = thisNodes[(diag1 + 2) % 3];

                // mise à jour des connectivités

                this.nd1 = diagNode1;
                this.nd2 = remain1;
                this.nd3 = diagNode2;

                tria.nd1 = diagNode2;
                tria.nd2 = remain2;
                tria.nd3 = diagNode1;

                // mise à jour du centre et du rayon

                this.defCentre();
                tria.defCentre();

                // mise à jour des voisins

                this.nd1.removeConnectedElement(tria);
                this.nd2.removeConnectedElement(tria);
                this.nd3.removeConnectedElement(tria);
                this.nd1.removeConnectedElement(this);
                this.nd2.removeConnectedElement(this);
                this.nd3.removeConnectedElement(this);

                tria.nd1.removeConnectedElement(tria);
                tria.nd2.removeConnectedElement(tria);
                tria.nd3.removeConnectedElement(tria);
                tria.nd1.removeConnectedElement(this);
                tria.nd2.removeConnectedElement(this);
                tria.nd3.removeConnectedElement(this);

                this.nd1.addConnectedElement(this);
                this.nd2.addConnectedElement(this);
                this.nd3.addConnectedElement(this);
                tria.nd1.addConnectedElement(tria);
                tria.nd2.addConnectedElement(tria);
                tria.nd3.addConnectedElement(tria);

                // mise à jour des éléments voisins

                this.updateNeighbours();
                tria.updateNeighbours();

                // mise à jour des voisins des voisins

                for (int i = 0; i < 3; i++) {
                    IPFEM2DElement temp = this.neighbours[i];
                    if (temp != null) {
                        ((PFEM2DTria) temp).updateNeighbours();
                    }
                }

                for (int i = 0; i < 3; i++) {
                    IPFEM2DElement temp = tria.neighbours[i];
                    if (temp != null) {
                        ((PFEM2DTria) temp).updateNeighbours();
                    }
                }

                return true;
            }
        }

        return false;
    }

    public PFEM2DEdge getDiag(PFEM2DTria tria2) {

        PFEM2DNode diagNode1 = null;
        PFEM2DNode diagNode2 = null;

        if (this.neighbours[0] == tria2) {
            diagNode1 = this.nd1;
        } else if (this.neighbours[1] == tria2) {
            diagNode1 = this.nd2;
        } else if (this.neighbours[2] == tria2) {
            diagNode1 = this.nd3;
        }

        if (tria2.neighbours[0] == this) {
            diagNode2 = tria2.nd1;
        } else if (tria2.neighbours[1] == this) {
            diagNode2 = tria2.nd2;
        } else if (tria2.neighbours[2] == this) {
            diagNode2 = tria2.nd3;
        }

        if ((diagNode1 == null) || (diagNode2 == null)) {
            if (diagNode1 == null) {
                System.out.println("diagnode1=null");
                for (int i = 0; i < neighbours.length; i++) {
                    IPFEM2DElement iPFEM2DElement = neighbours[i];
                    if (iPFEM2DElement != null) {
                        System.out.println("  neighbour[" + i + "]=" + iPFEM2DElement.getId());
                    } else {
                        System.out.println("  neighbour[" + i + "]=null");
                    }
                }
            }
            if (diagNode2 == null) {
                System.out.println("diagnode2=null");
                for (int i = 0; i < tria2.neighbours.length; i++) {
                    IPFEM2DElement iPFEM2DElement = tria2.neighbours[i];
                    if (iPFEM2DElement != null) {
                        System.out.println("  neighbour[" + i + "]=" + iPFEM2DElement.getId());
                    } else {
                        System.out.println("  neighbour[" + i + "]=null");
                    }
                }
            }
            return null;
        } else {
            return new PFEM2DEdge(0, diagNode1, diagNode2);
        }
    }

    public void swapOrientation() {
        PFEM2DNode tempNode = this.nd2;
        this.nd2 = this.nd3;
        this.nd3 = tempNode;
    }

    public int getLocation() {
        return location;
    }

    public int getLocationInverse() {
        if (this.location == OUTSIDE) {
            return INSIDE;
        } else if (this.location == INSIDE) {
            return OUTSIDE;
        } else {
            return NOT_LOCALIZED;
        }
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public void setLocationOutside() {
        this.location = OUTSIDE;
    }

    public void setLocationInside() {
        this.location = INSIDE;
    }

    public double getAngle(int nodeRank) {

        double l12 = this.nd1.getDistanceTo(this.nd2);
        double l23 = this.nd2.getDistanceTo(this.nd3);
        double l31 = this.nd3.getDistanceTo(this.nd1);
        double angle;

        switch (nodeRank) {

            case 0:
                angle = Math.acos((Math.pow(l12, 2) - Math.pow(l23, 2) - Math.pow(l31, 2)) / (-2 * l23 * l31));
                break;

            case 1:
                angle = Math.acos((Math.pow(l23, 2) - Math.pow(l31, 2) - Math.pow(l12, 2)) / (-2 * l31 * l12));
                break;

            case 2:
                angle = Math.acos((Math.pow(l31, 2) - Math.pow(l12, 2) - Math.pow(l23, 2)) / (-2 * l12 * l23));
                break;

            default:
                angle = 0;
        }

        if (angle < 0) {
            angle += Math.PI;
        }

        return angle / Math.PI * 180.;
    }

    public double getQuality() {

        
        // méthode basée sur ratio radius/min edge

        double l = this.nd1.getDistanceTo(this.nd2);
        l = Math.min(l, this.nd2.getDistanceTo(this.nd3));
        l = Math.min(l, this.nd3.getDistanceTo(this.nd1));

        return (l / this.radius) / Math.sqrt(3);
    }

    public int getOppositeNodeRank(PFEM2DEdge edge) {

        PFEM2DNode nodeEdge1 = edge.getNode(0);
        PFEM2DNode nodeEdge2 = edge.getNode(1);

        if ((nodeEdge1 != this.nd1) && (nodeEdge2 != this.nd1)) {
            return 0;
        } else if ((nodeEdge1 != this.nd2) && (nodeEdge2 != this.nd2)) {
            return 1;
        } else if ((nodeEdge1 != this.nd3) && (nodeEdge2 != this.nd3)) {
            return 2;
        } else {
            return 9;
        }
    }

    public PFEM2DNode getOppositeNode(PFEM2DEdge edge) {

        int rank = this.getOppositeNodeRank(edge);

        switch (rank) {

            case 0:
                return this.nd1;

            case 1:
                return this.nd2;

            case 2:
                return this.nd3;

            default:
                return null;
        }
    }

    public double getOrientation() {

        PFEM2DVector vect1 = new PFEM2DVector(0, this.nd1, this.nd2);
        PFEM2DVector vect2 = new PFEM2DVector(0, this.nd1, this.nd3);

        return vect1.product(vect2);
    }

    public void printNodes() {

        System.out.println("tria " + this.getId() + ":");
        System.out.println("  node 1: " + this.nd1.getId());
        System.out.println("  node 2: " + this.nd2.getId());
        System.out.println("  node 3: " + this.nd3.getId());
    }

    public void printNeighbours() {

        System.out.println("tria " + this.getId() + ":");
        for (int i = 0; i < neighbours.length; i++) {
            IPFEM2DElement iPFEM2DElement = neighbours[i];
            String neiId = "null";
            if ((iPFEM2DElement) != null) {
                neiId = iPFEM2DElement.getId();
            }
            System.out.println("  neighbour " + (i + 1) + ": " + neiId);
        }
    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = this.nd1.getX();
        data[1] = this.nd1.getX();
        data[2] = this.nd1.getY();
        data[3] = this.nd1.getY();

        double x, y;

        for (int i = 0; i < 2; i++) {

            if (i == 0) {
                x = this.nd2.getX();
                y = this.nd2.getY();
            } else {
                x = this.nd3.getX();
                y = this.nd3.getY();
            }

            if (x < data[0]) {
                data[0] = x;
            } else if (x > data[1]) {
                data[1] = x;
            }

            if (y < data[2]) {
                data[2] = y;
            } else if (y > data[3]) {
                data[3] = y;
            }
        }

        return data;
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        return null;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {

        StringBuilder temp = new StringBuilder(this.getId());
        temp.append(", nodes: ");
        temp.append(this.getNode(0).getId());
        temp.append("/");
        temp.append(this.getNode(1).getId());
        temp.append("/");
        temp.append(this.getNode(2).getId());
        temp.append(", neighbours: ");

        for (int i = 0; i < 3; i++) {
            IPFEM2DElement iPFEM2DElement = neighbours[i];

            if (iPFEM2DElement == null) {
                temp.append("---");
            } else {
                temp.append(iPFEM2DElement.getId());
            }

            if (i < 2) {
                temp.append("/");
            }
        }

        return temp.toString();
    }
}
