package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 * Classe définissant un objet graphique de type "point"
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DPoint implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private double x, y;
    private long id;
    private boolean visible;
    private PFEM2DNode node;
    private boolean meshed;

    /**
     * Constructeur
     *
     * @param id l'identité du point
     * @param x l'abscisse du point
     * @param y l'ordonnée du point
     */
    public PFEM2DPoint(long id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.visible = true;

        this.node = null;
        this.meshed = false;
    }

    /**
     * Constructeur par défaut
     */
    public PFEM2DPoint() {
        this.id = 0;
        this.x = 0;
        this.y = 0;
        this.visible = false;

        this.node = null;
        this.meshed = false;
    }

    /**
     * Définit l'identité du point
     *
     * @param id l'identité du point
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Renvoie l'abscisse du point
     *
     * @return l'abscisse du point
     */
    public double getX() {
        return x;
    }

    /**
     * Définit l'abscisse du point
     *
     * @param x l'abscisse du point
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Renvoie l'ordonnée du point
     *
     * @return l'ordonnée du point
     */
    public double getY() {
        return y;
    }

    /**
     * Définit l'ordonnée du point
     *
     * @param y l'ordonnée du point
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Dessine l'objet graphique
     *
     * @param g l'objet Graphics sur lequel dessiner
     * @param panel l'objet PFEM2DGuiPanel sur lequel dessiner
     */
    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

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

    /**
     * Définit la visibilité de l'objet graphique
     *
     * @param flag true si l'objet est visible, false s'il ne l'est pas
     */
    @Override
    public void setVisible(boolean flag) {
        this.visible = flag;
    }

    /**
     * Renvoie l'état de visibilité de l'objet graphique
     *
     * @return true si l'objet est visible, false s'il ne l'est pas
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Renvoie l'identité de l'objet graphique
     *
     * @return l'identité de l'objet graphique
     */
    @Override
    public String getId() {
        return "Point " + this.id;
    }

    /**
     * Renvoie l'identité sous format numérique de l'objet graphique
     *
     * @return l'identité sous format numérique de l'objet graphique
     */
    public long getNumId() {
        return this.id;
    }

    /**
     * Renvoie la distance du point à un autre point
     *
     * @param pt l'autre point
     * @return la distance
     */
    public double getDistanceTo(PFEM2DPoint pt) {
        return Math.sqrt(Math.pow(pt.getX() - this.getX(), 2) + Math.pow(pt.getY() - this.getY(), 2));
    }

    /**
     * Constructeur par rotation du point courant
     *
     * @param centre le centre de la rotation
     * @param angle l'angle de la rotation
     * @return le nouveau point créé
     */
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
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel) {
        this.node.draw(g, panel);
    }

    // méthode toString
    @Override
    public String toString() {
        return "Point #"+this.id+", x/y="+this.x+"/"+this.y;
    }

    /**
     * Renvoie la boite englobante de l'objet graphique
     * @return un tableau de 4 double: Xmin, Xmax, Ymin, Ymax
     */
    @Override
    public double[] getBoundingBox() {
        return new double[]{this.x,this.x,this.y,this.y};
    }

    /**
     * Renvoie la liste des points définissant l'objet graphique
     * @return la liste des points
     */
    @Override
    public PFEM2DPoint[] getPoints() {
        return new PFEM2DPoint[]{this};
    }
}
