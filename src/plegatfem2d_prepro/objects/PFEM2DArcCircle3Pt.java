package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;
import plegatfem2d_prepro.PFEM2DObjectManager;

/**
 *
 * @author jmb2
 */
public class PFEM2DArcCircle3Pt implements IPFEM2DDrawableObject, IPFEM2DMeshableObject, IPFEM2DCurve {

    private PFEM2DPoint centre, pt1, pt2;
    private long id;
    private boolean visible;
    private PFEM2DPoint[] points;
    private int nbPts;
    private boolean meshed;
    private List<PFEM2DNode> nodes;
    private List<PFEM2DBeam> elements;
    private int nbElements;
    private double modulus, area, inertia;
    private int meshMethod = IPFEM2DMeshableObject.NODE_AND_BEAM;

    public PFEM2DArcCircle3Pt(long id, PFEM2DPoint centre, PFEM2DPoint pt1, PFEM2DPoint pt2) {
        this.centre = centre;
        this.pt1 = pt1;
        this.pt2 = pt2;
        this.id = id;
        this.visible = true;


        this.meshed = false;
        this.nodes = new ArrayList<>();
        this.elements = new ArrayList<>();

        this.nbElements = 1;

        this.init();
    }

    public final void init() {

        double radius1 = this.centre.getDistanceTo(this.pt1);
        double radius2 = this.centre.getDistanceTo(this.pt2);

        if (Math.abs(radius1 - radius2) > 1e-5) {
            double x2 = this.centre.getX() + (this.pt2.getX() - this.centre.getX()) / radius2 * radius1;
            double y2 = this.centre.getY() + (this.pt2.getY() - this.centre.getY()) / radius2 * radius1;

            this.pt2.setX(x2);
            this.pt2.setY(y2);
        }

        double dx1 = this.pt1.getX() - this.centre.getX();
        double dy1 = this.pt1.getY() - this.centre.getY();

        double dx2 = this.pt2.getX() - this.centre.getX();
        double dy2 = this.pt2.getY() - this.centre.getY();

        double angle1 = Math.atan2(dy1, dx1);
        double angle2 = Math.atan2(dy2, dx2);

        while (angle2 <= angle1) {
            angle2 = angle2 + 2 * Math.PI;
        }

        this.nbPts = (int) Math.floor(Math.abs(angle2 - angle1) / (10. / 180 * Math.PI)) + 1;
        double step = (angle2 - angle1) / (this.nbPts - 1);

        this.points = new PFEM2DPoint[this.nbPts];

        for (int i = 0; i < this.nbPts; i++) {
            this.points[i] = new PFEM2DPoint(0, this.centre.getX() + radius1 * Math.cos(angle1 + i * step), this.centre.getY() + radius1 * Math.sin(angle1 + i * step));
        }
    }

    public PFEM2DPoint getCentre() {
        return centre;
    }

    public void setCentre(PFEM2DPoint centre) {
        this.centre = centre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PFEM2DPoint getPt1() {
        return pt1;
    }

    public void setPt1(PFEM2DPoint pt) {
        this.pt1 = pt;
    }

    public PFEM2DPoint getPt2() {
        return pt2;
    }

    public void setPt2(PFEM2DPoint pt) {
        this.pt2 = pt;
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

        for (int i = 0; i < this.nbPts - 1; i++) {

            g.setColor(Color.yellow);

            int xloc1 = panel.getLocalCoordX(this.points[i].getX());
            int yloc1 = panel.getLocalCoordY(this.points[i].getY());

            int xloc2 = panel.getLocalCoordX(this.points[i + 1].getX());
            int yloc2 = panel.getLocalCoordY(this.points[i + 1].getY());

            g.drawLine(xloc1, yloc1, xloc2, yloc2);

            if (this.isMeshed()) {
                this.drawMesh(g, panel);
            }
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
        return "Arc_Circle " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {
        if (!this.isMeshed()) {

            double dx1 = this.pt1.getX() - this.centre.getX();
            double dy1 = this.pt1.getY() - this.centre.getY();

            double dx2 = this.pt2.getX() - this.centre.getX();
            double dy2 = this.pt2.getY() - this.centre.getY();

            double angle1 = Math.atan2(dy1, dx1);
            double angle2 = Math.atan2(dy2, dx2);

            while (angle2 < angle1) {
                angle2 = angle2 + 2 * Math.PI;
            }

            double step = (angle2 - angle1) / this.nbElements;

            if (!this.pt1.isMeshed()) {
                this.pt1.mesh(pom);
            }
            if (!this.pt2.isMeshed()) {
                this.pt2.mesh(pom);
            }

            PFEM2DNode ndStart = this.pt1.getNode();
            PFEM2DNode ndEnd = this.pt2.getNode();

            this.nodes.add(ndStart);

            for (int i = 1; i < this.nbElements; i++) {
                this.nodes.add(ndStart.getRotated(pom, this.centre, (i * step) * 180.0 / Math.PI));
            }

            this.nodes.add(ndEnd);

            if (this.meshMethod == IPFEM2DMeshableObject.NODE_AND_BEAM) {
                for (int i = 0; i < this.nbElements; i++) {

                    PFEM2DNode nd1 = this.nodes.get(i);
                    PFEM2DNode nd2 = this.nodes.get(i + 1);
                    PFEM2DBeam beam = new PFEM2DBeam(pom.getIdCurrentElement(), nd1, nd2, modulus, area, inertia);

                    this.elements.add(beam);
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

    @Override
    public PFEM2DPoint getStartPoint() {
        return this.pt1;
    }

    @Override
    public PFEM2DPoint getEndPoint() {
        return this.pt2;
    }

    @Override
    public List<PFEM2DEdge> getEdges() {

        ArrayList<PFEM2DEdge> liste = new ArrayList<>();

        int nodeNb = this.nodes.size();

        for (int i = 0; i < nodeNb - 1; i++) {
            PFEM2DEdge edge;
            edge = new PFEM2DEdge(0, this.nodes.get(i), this.nodes.get(i + 1));
            liste.add(edge);
        }

        liste.trimToSize();
        return liste;
    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;
        data[1] = Double.NEGATIVE_INFINITY;
        data[2] = Double.POSITIVE_INFINITY;
        data[3] = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < this.nbPts; i++) {

            double x = this.points[i].getX();
            double y = this.points[i].getY();

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
        return this.points;
    }
}
