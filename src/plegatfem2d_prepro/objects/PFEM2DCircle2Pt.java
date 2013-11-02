package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;
import plegatfem2d_prepro.PFEM2DObjectManager;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DCircle2Pt implements IPFEM2DDrawableObject, IPFEM2DMeshableObject, IPFEM2DCurve {

    private PFEM2DPoint centre, pt;
    private int id;
    private boolean visible;
    private PFEM2DPoint[] points;
    private static int NB_PTS = 36;
    private boolean meshed;
    private List<PFEM2DNode> nodes;
    private List<PFEM2DBeam> elements;
    private int nbElements;
    private double modulus, area, inertia;
    private int meshMethod = IPFEM2DMeshableObject.NODE_AND_BEAM;

    public PFEM2DCircle2Pt(int id, PFEM2DPoint centre, PFEM2DPoint pt) {
        this.centre = centre;
        this.pt = pt;
        this.id = id;
        this.visible = true;

        this.meshed = false;
        this.nodes = new ArrayList<>();
        this.elements = new ArrayList<>();

        this.nbElements = 1;

        this.init();
    }

    public final void init() {
        this.points = new PFEM2DPoint[NB_PTS + 1];

        double radius = this.centre.getDistanceTo(this.pt);

        for (int i = 0; i < NB_PTS; i++) {
            this.points[i] = new PFEM2DPoint(0, this.centre.getX() + radius * Math.cos(i / 18. * Math.PI), this.centre.getY() + radius * Math.sin(i / 18. * Math.PI));
        }

        this.points[NB_PTS] = this.points[0];
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

    public PFEM2DPoint getPt() {
        return pt;
    }

    public void setPt(PFEM2DPoint pt) {
        this.pt = pt;
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

        for (int i = 0; i < NB_PTS; i++) {

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
        return "Circle " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {
        if (!this.isMeshed()) {

            double dx = this.pt.getX() - this.centre.getX();
            double dy = this.pt.getY() - this.centre.getY();

            double angle = Math.atan2(dy, dx);

            double step = 2 * Math.PI / this.nbElements;

            if (!this.pt.isMeshed()) {
                this.pt.mesh(pom);
            }

            PFEM2DNode ndStart = this.pt.getNode();

            this.nodes.add(ndStart);
            
            for (int i = 1; i < this.nbElements; i++) {
                this.nodes.add(ndStart.getRotated(pom, this.centre, (i * step) * 180. / Math.PI));
            }

            this.nodes.add(ndStart);

            if (this.meshMethod == IPFEM2DMeshableObject.NODE_AND_BEAM) {
                for (int i = 0; i < this.nbElements; i++) {

                    PFEM2DNode nd1 = this.nodes.get(i);
                    PFEM2DNode nd2;
                    if (i <= this.nbElements - 1) {
                        nd2 = this.nodes.get(i + 1);
                    } else {
                        nd2 = this.nodes.get(0);
                    }
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
        return this.pt;
    }

    @Override
    public PFEM2DPoint getEndPoint() {
        return this.pt;
    }

    @Override
    public ArrayList<PFEM2DEdge> getEdges() {

        ArrayList<PFEM2DEdge> liste = new ArrayList<>();

        int nodeNb = this.nodes.size();

        for (int i = 0; i < nodeNb; i++) {

            PFEM2DEdge edge;

            if (i == nodeNb - 1) {
                edge = new PFEM2DEdge(0, this.nodes.get(i), this.nodes.get(0));
            } else {
                edge = new PFEM2DEdge(0, this.nodes.get(i), this.nodes.get(i + 1));
            }
            
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

        for (int i = 0; i < NB_PTS; i++) {

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
