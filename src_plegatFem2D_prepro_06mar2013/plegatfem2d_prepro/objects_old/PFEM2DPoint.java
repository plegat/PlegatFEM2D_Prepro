package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author jmb2
 */
public class PFEM2DPoint implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private double x, y;
    private long id;
    private boolean visible;
    private PFEM2DNode node;
    private boolean meshed;

    public PFEM2DPoint(long id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.visible = true;

        this.node = null;
        this.meshed = false;

    }

    public PFEM2DPoint() {
        this.id = 0;
        this.x = 0;
        this.y = 0;
        this.visible = false;

        this.node = null;
        this.meshed = false;


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
    public void draw(Graphics g, PFem2DGuiPanel panel) {

        if (this.isVisible()) {
            int xloc = panel.getLocalCoordX(this.x);
            int yloc = panel.getLocalCoordY(this.y);

            int[] xPoints = {xloc + 5, xloc - 5, xloc - 5, xloc + 5, xloc + 5};
            int[] yPoints = {yloc + 5, yloc + 5, yloc - 5, yloc - 5, yloc + 5};

            g.setColor(Color.cyan);
            g.drawPolyline(xPoints, yPoints, 5);

            if (this.isMeshed()) {
                this.drawMesh(g, panel);
            }

            // dessin id
            
            g.drawString("P" + this.id, xloc + 10, yloc + 10);
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
        return "Point " + this.id;
    }

    public long getNumId() {
        return this.id;
    }

    public double getDistanceTo(PFEM2DPoint pt) {
        return Math.sqrt(Math.pow(pt.getX() - this.getX(), 2) + Math.pow(pt.getY() - this.getY(), 2));
    }

    public PFEM2DPoint getRotated(PFEM2DPoint centre, double angle) {

        double dx = this.getX() - centre.getX();
        double dy = this.getY() - centre.getY();

        double angleBase = Math.atan2(dy, dx);
        double radius = centre.getDistanceTo(this);

        double xRot = centre.getX() + radius * Math.cos(angleBase + angle / 180. * Math.PI);
        double yRot = centre.getY() + radius * Math.sin(angleBase + angle / 180. * Math.PI);

        return new PFEM2DPoint(0, xRot, yRot);


    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        if (!this.meshed) {

            this.node = new PFEM2DNode(pom.getIdCurrentNode(), this.x, this.y);
            
            this.meshed = true;
        }

    }

    @Override
    public void setMeshMethod(int method) {
    }

    @Override
    public void deleteMesh() {
        this.meshed = false;
        this.node = null;
    }

    @Override
    public boolean isMeshed() {
        return this.meshed;
    }

    @Override
    public PFEM2DNode[] getNodes() {
        PFEM2DNode[] temp = new PFEM2DNode[1];
        temp[0] = this.node;
        return temp;
    }

    public PFEM2DNode getNode() {
        return this.node;
    }

    @Override
    public IPFEM2DElement[] getElements() {
        return null;
    }

    @Override
    public void drawMesh(Graphics g, PFem2DGuiPanel panel) {

        this.node.draw(g, panel);

    }
    
    @Override
    public String toString() {
        
        return "Point #"+this.id+", x/y="+this.x+"/"+this.y;
        
    }

    @Override
    public double[] getBoundingBox() {
        return new double[]{this.x,this.x,this.y,this.y};
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        return new PFEM2DPoint[]{this};
    }
    
}
