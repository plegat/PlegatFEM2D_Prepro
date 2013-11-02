package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;
import plegatfem2d_prepro.PFEM2DObjectManager;

/**
 * Classe définissant un objet graphique de type "ligne", reliant deux points.
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DLine implements IPFEM2DDrawableObject, IPFEM2DMeshableObject, IPFEM2DCurve {

    private PFEM2DPoint pt1, pt2;   // les deux points définissant la ligne 
    private int id;                 // l'identité de la ligne
    private boolean visible;        // l'état de visibilité de la ligne
    private boolean meshed;
    private List<PFEM2DNode> nodes;
    private List<PFEM2DBeam> elements;
    private int nbElements;
    private double modulus, area, inertia;
    private int meshMethod = IPFEM2DMeshableObject.NODE_AND_BEAM;

    /**
     * Constructeur
     *
     * @param id identité
     * @param pt1 point n°1
     * @param pt2 point n°2
     */
    public PFEM2DLine(int id, PFEM2DPoint pt1, PFEM2DPoint pt2) {
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.id = id;
        this.visible = true;

        this.meshed = false;
        this.nodes = new ArrayList<>();
        this.elements = new ArrayList<>();

        this.nbElements = 1;
    }

    /**
     * Définit l'identité de la ligne
     *
     * @param id l'identité de la ligne
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Renvoie le premier point
     *
     * @return le premier point
     */
    public PFEM2DPoint getPt1() {
        return pt1;
    }

    /**
     * Définit le premier point
     *
     * @param pt1 le premier point
     */
    public void setPt1(PFEM2DPoint pt1) {
        this.pt1 = pt1;
    }

    /**
     * Renvoie le second point
     *
     * @return le second point
     */
    public PFEM2DPoint getPt2() {
        return pt2;
    }

    /**
     * Définit le second point
     *
     * @param pt2 le second point
     */
    public void setPt2(PFEM2DPoint pt2) {
        this.pt2 = pt2;
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
            int xloc1 = panel.getLocalCoordX(this.pt1.getX());
            int yloc1 = panel.getLocalCoordY(this.pt1.getY());

            int xloc2 = panel.getLocalCoordX(this.pt2.getX());
            int yloc2 = panel.getLocalCoordY(this.pt2.getY());

            g.setColor(Color.yellow);
            g.drawLine(xloc1, yloc1, xloc2, yloc2);

            if (this.isMeshed()) {
                this.drawMesh(g, panel);
            }
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
        return "Line " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {
        if (!this.isMeshed()) {

            if (!this.pt1.isMeshed()) {
                this.pt1.mesh(pom);
            }
            if (!this.pt2.isMeshed()) {
                this.pt2.mesh(pom);
            }

            PFEM2DNode nd1 = this.pt1.getNode();
            PFEM2DNode nd2 = this.pt2.getNode();

            this.nodes.add(nd1);

            for (int i = 0; i < this.nbElements - 1; i++) {
                this.nodes.add(nd1.getInterpolated(pom, nd2, (i + 1.0) / this.nbElements));
            }

            this.nodes.add(nd2);

            if (this.meshMethod == IPFEM2DMeshableObject.NODE_AND_BEAM) {
                for (int i = 0; i < this.nbElements; i++) {
                    this.elements.add(new PFEM2DBeam(pom.getIdCurrentElement(), this.nodes.get(i), this.nodes.get(i + 1), this.modulus, this.area, this.inertia));
                }
            }

            this.meshed = true;
        }
    }

    @Override
    public void setMeshMethod(int method) {
        this.meshMethod = method;
    }

    @Override
    public void deleteMesh() {
        this.meshed = false;
        this.nodes.clear();
        this.elements.clear();
    }

    @Override
    public boolean isMeshed() {
        return this.meshed;
    }

    @Override
    public PFEM2DNode[] getNodes() {
        PFEM2DNode[] temp = new PFEM2DNode[this.nodes.size()];
        return this.nodes.toArray(temp);
    }

    @Override
    public IPFEM2DElement[] getElements() {
        IPFEM2DElement[] temp = new IPFEM2DElement[this.nodes.size()];
        return this.nodes.toArray(temp);
    }

    public void setNbElements(int nbElements) {
        this.nbElements = nbElements;
    }

    public void setMeshProperties(double modulus, double area, double inertia) {
        this.modulus = modulus;
        this.area = area;
        this.inertia = inertia;
    }

    public double getModulus() {
        return modulus;
    }

    public double getArea() {
        return area;
    }

    public double getInertia() {
        return inertia;
    }

    @Override
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel) {

        for (PFEM2DNode current : this.nodes) {
            current.draw(g, panel);
        }
        
        for (PFEM2DBeam current : this.elements) {
            current.draw(g, panel);
        }        
    }

    /**
     * Renvoie le point de départ de la courbe
     *
     * @return le point de départ de la courbe
     */
    @Override
    public PFEM2DPoint getStartPoint() {
        return this.pt1;
    }

    /**
     * Renvoie le point d'arrivée de la courbe
     *
     * @return le point d'arrivée de la courbe
     */
    @Override
    public PFEM2DPoint getEndPoint() {
        return this.pt2;
    }

    /**
     * Renvoie la boite englobante de l'objet graphique
     *
     * @return un tableau de 4 double: Xmin, Xmax, Ymin, Ymax
     */
    @Override
    public List<PFEM2DEdge> getEdges() {

        ArrayList<PFEM2DEdge> liste = new ArrayList<PFEM2DEdge>();
        int nodeNb = this.nodes.size();

        for (int i = 0; i < nodeNb - 1; i++) {
            PFEM2DEdge edge = new PFEM2DEdge(0, this.nodes.get(i), this.nodes.get(i + 1));
            liste.add(edge);
        }

        liste.trimToSize();
        return liste;
    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Math.min(this.pt1.getX(), this.pt2.getX());
        data[1] = Math.max(this.pt1.getX(), this.pt2.getX());
        data[2] = Math.min(this.pt1.getY(), this.pt2.getY());
        data[3] = Math.max(this.pt1.getY(), this.pt2.getY());

        return data;
    }

    /**
     * Renvoie la liste des points définissant l'objet graphique
     *
     * @return la liste des points
     */
    @Override
    public PFEM2DPoint[] getPoints() {
        return new PFEM2DPoint[]{this.pt1, this.pt2};
    }
}
