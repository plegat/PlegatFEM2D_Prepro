package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author jmb2
 */
public class PFEM2DModel implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private ArrayList<IPFEM2DDrawableObject> objects = new ArrayList<IPFEM2DDrawableObject>();
    private boolean visible = true;
    private boolean meshed = false;

    public PFEM2DModel() {
        this.objects.clear();
    }

    public void addObject(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
    }

    public void removeObject(IPFEM2DDrawableObject obj) {
        this.objects.remove(obj);
    }

    @Override
    public void draw(Graphics g, PFem2DGuiPanel panel) {

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iPFEM2DDrawableObject = it.next();

            if (iPFEM2DDrawableObject.isVisible()) {
                iPFEM2DDrawableObject.draw(g, panel);
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
        return "Root";
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        if (!this.isMeshed()) {

            for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
                IPFEM2DDrawableObject iObject = it.next();

                if (iObject instanceof IPFEM2DMeshableObject) {
                    ((IPFEM2DMeshableObject) iObject).mesh(pom);
                }
            }

            this.meshed = true;
        }
    }

    @Override
    public void setMeshMethod(int method) {
        
    }

    @Override
    public void deleteMesh() {

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) iObject).deleteMesh();
            }
        }
        
        this.meshed=false;
    }

    @Override
    public boolean isMeshed() {
        return this.meshed;
    }

    @Override
    public PFEM2DNode[] getNodes() {

        ArrayList<PFEM2DNode> listNodes = new ArrayList<PFEM2DNode>();

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                PFEM2DNode[] objNodes = ((IPFEM2DMeshableObject) iObject).getNodes();

                if (objNodes != null) {
                    for (int i = 0; i < objNodes.length; i++) {
                        if (listNodes.indexOf(objNodes[i]) == -1) {
                            listNodes.add(objNodes[i]);
                        }
                    }
                }
            }
        }

        return listNodes.toArray(new PFEM2DNode[listNodes.size()]);
    }

    @Override
    public IPFEM2DElement[] getElements() {

        ArrayList<IPFEM2DElement> listElements = new ArrayList<IPFEM2DElement>();

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                IPFEM2DElement[] objElements = ((IPFEM2DMeshableObject) iObject).getElements();

                if (objElements != null) {
                    for (int i = 0; i < objElements.length; i++) {
                        if (listElements.indexOf(objElements[i]) == -1) {
                            listElements.add(objElements[i]);
                        }
                    }
                }
            }
        }

        return listElements.toArray(new IPFEM2DElement[listElements.size()]);
    }

    @Override
    public void drawMesh(Graphics g, PFem2DGuiPanel panel) {
        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) iObject).drawMesh(g, panel);
            }
        }
    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;
        data[1] = Double.NEGATIVE_INFINITY;
        data[2] = Double.POSITIVE_INFINITY;
        data[3] = Double.NEGATIVE_INFINITY;

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();
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
        ArrayList<PFEM2DPoint> listPts = new ArrayList<PFEM2DPoint>();

        for (Iterator<IPFEM2DDrawableObject> it = this.objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject object = it.next();
            PFEM2DPoint[] pts = object.getPoints();
            listPts.addAll(Arrays.asList(pts));
        }

        return listPts.toArray(new PFEM2DPoint[listPts.size()]);
    }
}
