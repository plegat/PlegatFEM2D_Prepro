package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.mesher.Delaunay2DMesher;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DSurface implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private List<IPFEM2DDrawableObject> objects;
    private boolean visible;
    private long id;
    private boolean meshed;
    private List<PFEM2DNode> nodes;
    private List<IPFEM2DElement> elements;
    private double modulus, thickness;
    private int meshMethod = IPFEM2DMeshableObject.DELAUNAY;
    private int MAX_NUM_LOOP = 10;
    private int NB_LOOP_LISSAGE = 5;
    private double RATIO_SPLIT_EDGE = 1.7;
    private double MIN_EDGE_LENGTH = 10;
    private double MIN_TRIA_ANGLE = 10;

    public PFEM2DSurface(long id) {
        this.objects = new ArrayList<>();

        this.id = id;
        this.visible = true;

        this.meshed = false;
        this.nodes = new ArrayList<>();
        this.elements = new ArrayList<>();
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

    public int getMAX_NUM_LOOP() {
        return MAX_NUM_LOOP;
    }

    public void setMAX_NUM_LOOP(int MAX_NUM_LOOP) {
        this.MAX_NUM_LOOP = MAX_NUM_LOOP;
    }

    public int getNB_LOOP_LISSAGE() {
        return NB_LOOP_LISSAGE;
    }

    public void setNB_LOOP_LISSAGE(int NB_LOOP_LISSAGE) {
        this.NB_LOOP_LISSAGE = NB_LOOP_LISSAGE;
    }

    public double getRATIO_SPLIT_EDGE() {
        return RATIO_SPLIT_EDGE;
    }

    public void setRATIO_SPLIT_EDGE(double RATIO_SPLIT_EDGE) {
        this.RATIO_SPLIT_EDGE = RATIO_SPLIT_EDGE;
    }

    public double getMIN_EDGE_LENGTH() {
        return MIN_EDGE_LENGTH;
    }

    public void setMIN_EDGE_LENGTH(double MIN_EDGE_LENGTH) {
        this.MIN_EDGE_LENGTH = MIN_EDGE_LENGTH;
    }

    public double getMIN_TRIA_ANGLE() {
        return MIN_TRIA_ANGLE;
    }

    public void setMIN_TRIA_ANGLE(double MIN_TRIA_ANGLE) {
        this.MIN_TRIA_ANGLE = MIN_TRIA_ANGLE;
    }

    public void add(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {
        for (IPFEM2DDrawableObject current : this.objects) {
            current.draw(g, panel);
        }

        if (this.isMeshed()) {
            this.drawMesh(g, panel);
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
        return "Surface " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        this.deleteMesh();
        
        if (!this.isMeshed()) {

            for (IPFEM2DDrawableObject obj : this.objects) {
                if (obj instanceof IPFEM2DMeshableObject) {

                    if (!((IPFEM2DMeshableObject) obj).isMeshed()) {
                        ((IPFEM2DMeshableObject) obj).mesh(pom);
                    }
                }
            }

            if (this.meshMethod == IPFEM2DMeshableObject.DELAUNAY) {
                Delaunay2DMesher d2m=new Delaunay2DMesher(this);
                this.meshed=d2m.mesh(pom);
            }
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

    public List<IPFEM2DDrawableObject> getObjects() {
        return objects;
    }
    
    @Override
    public PFEM2DNode[] getNodes() {
        if (this.meshed) {
            return this.nodes.toArray(new PFEM2DNode[this.nodes.size()]);
        } else {
            return null;
        }
    }

    public List<PFEM2DNode> getNodesList() {
        return this.nodes;
    }
    
    @Override
    public IPFEM2DElement[] getElements() {
        if (this.meshed) {
            return this.elements.toArray(new IPFEM2DElement[this.elements.size()]);
        } else {
            return null;
        }
    }

    public List<IPFEM2DElement> getElementsList() {
        return this.elements;
    }
    
    @Override
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel) {

        for (IPFEM2DDrawableObject obj : objects) {
            if (obj instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) obj).drawMesh(g, panel);
            }
        }

        for (int i = 0; i < this.elements.size(); i++) {
            this.elements.get(i).draw(g, panel);
        }

        for (int i = 0; i < this.nodes.size(); i++) {
            this.nodes.get(i).draw(g, panel);
        }
    }

    
    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;
        data[1] = Double.NEGATIVE_INFINITY;
        data[2] = Double.POSITIVE_INFINITY;
        data[3] = Double.NEGATIVE_INFINITY;

        for (IPFEM2DDrawableObject iObject : objects) {
            double bb[] = iObject.getBoundingBox();

            if (bb[0] < data[0]) {
                data[0] = bb[0];
            }
            if (bb[1] > data[1]) {
                data[1] = bb[1];
            }
            if (bb[2] < data[2]) {
                data[2] = bb[2];
            }
            if (bb[3] > data[3]) {
                data[3] = bb[3];
            }
        }

        return data;
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        List<PFEM2DPoint> listPts = new ArrayList<>();

        for (IPFEM2DDrawableObject object : this.objects) {
            PFEM2DPoint[] pts = object.getPoints();
            listPts.addAll(Arrays.asList(pts));
        }

        return listPts.toArray(new PFEM2DPoint[listPts.size()]);
    }
}
