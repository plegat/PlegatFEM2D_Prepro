package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 *
 * @author jmb2
 */
public class PFEM2DNode implements IPFEM2DDrawableObject {

    private double x, y;
    private long id;
    private boolean visible;
    private List<IPFEM2DElement> connectedElements = new ArrayList<>();
    private boolean edgeNode;

    public PFEM2DNode(long id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.visible = true;
        this.edgeNode = false;

        this.initConnectedElements();

    }

    public PFEM2DNode() {
        this.visible = true;
        this.edgeNode = false;

        this.initConnectedElements();
    }

    public final void initConnectedElements() {
        this.connectedElements.clear();
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

        if (this.isVisible()) {
            int xloc = panel.getLocalCoordX(this.x);
            int yloc = panel.getLocalCoordY(this.y);

            int[] xPoints = {xloc + 5, xloc, xloc - 5, xloc, xloc + 5};
            int[] yPoints = {yloc, yloc + 5, yloc, yloc - 5, yloc};

            g.setColor(Color.red);
            g.drawPolyline(xPoints, yPoints, 5);

            //dessin id

            g.drawString("N" + this.id, xloc + 10, yloc - 10);
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
        return "Node " + this.id;
    }

    public long getNumId() {
        return this.id;
    }

    public double getDistanceTo(PFEM2DPoint pt) {
        return Math.sqrt(Math.pow(pt.getX() - this.getX(), 2) + Math.pow(pt.getY() - this.getY(), 2));
    }

    public double getDistanceTo(PFEM2DNode nd) {
        return Math.sqrt(Math.pow(nd.getX() - this.getX(), 2) + Math.pow(nd.getY() - this.getY(), 2));
    }

    public PFEM2DNode getInterpolated(PFEM2DObjectManager pom, PFEM2DNode nd2, double coef) {

        double xInt = this.x + (nd2.x - this.x) * coef;
        double yInt = this.y + (nd2.y - this.y) * coef;

        return new PFEM2DNode(pom.getIdCurrentNode(), xInt, yInt);
    }

    public PFEM2DPoint getInterpolatedPoint(PFEM2DObjectManager pom, PFEM2DNode nd2, double coef) {       
        return this.getInterpolatedPoint(pom.getIdCurrentPoint(), nd2, coef);      
    }

    public PFEM2DPoint getInterpolatedPoint(long id, PFEM2DNode nd2, double coef) {

        double xInt = this.x + (nd2.x - this.x) * coef;
        double yInt = this.y + (nd2.y - this.y) * coef;

        return new PFEM2DPoint(id, xInt, yInt);
    }

    public PFEM2DNode getRotated(PFEM2DObjectManager pom, PFEM2DPoint centre, double angle) {

        double dx = this.getX() - centre.getX();
        double dy = this.getY() - centre.getY();

        double angleBase = Math.atan2(dy, dx);
        double radius = this.getDistanceTo(centre);

        double xRot = centre.getX() + radius * Math.cos(angleBase + angle / 180. * Math.PI);
        double yRot = centre.getY() + radius * Math.sin(angleBase + angle / 180. * Math.PI);

        return new PFEM2DNode(pom.getIdCurrentNode(), xRot, yRot);
    }

    public void addConnectedElement(IPFEM2DElement element) {

        int rank = this.connectedElements.indexOf(element);

        if (rank == -1) {
            this.connectedElements.add(element);
        }
    }

    public void removeConnectedElement(IPFEM2DElement element) {
        this.connectedElements.remove(element);
    }

    public List<IPFEM2DElement> getConnectedElements() {
        return connectedElements;
    }

    public void printConnectedElements() {

        System.out.println("connected elements to node " + this.getId());

        for (Iterator<IPFEM2DElement> it = connectedElements.iterator(); it.hasNext();) {
            IPFEM2DElement iPFEM2DElement = it.next();

            System.out.println("  " + iPFEM2DElement.getId());
        }
    }

    public boolean isEdgeNode() {
        return edgeNode;
    }

    public void setEdgeNode(boolean edgeNode) {
        this.edgeNode = edgeNode;
    }

    @Override
    public double[] getBoundingBox() {
        return new double[]{this.x, this.x, this.y, this.y};
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        return null;
    }
}
