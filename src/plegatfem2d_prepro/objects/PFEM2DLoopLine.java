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
public class PFEM2DLoopLine implements IPFEM2DDrawableObject, IPFEM2DMeshableObject, IPFEM2DCurve {

    IPFEM2DDrawableObject obj;
    private boolean visible;
    private long id;

    public PFEM2DLoopLine(PFEM2DCircle2Pt circle) {
        this.obj = circle;
        this.visible = true;
    }

    public PFEM2DLoopLine(PFEM2DMultiLine multi) throws Exception {

        if ((multi.validate()) && (multi.getStartPoint() == multi.getEndPoint())) {
            this.obj = multi;
            this.visible = true;
        } else {
            throw new Exception();
        }
    }

    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {
        if (this.obj != null) {
            this.obj.draw(g, panel);
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
        return "LoopLine " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {
        if (this.obj != null) {
            if (this.obj instanceof IPFEM2DMeshableObject) {

                if (!((IPFEM2DMeshableObject) this.obj).isMeshed()) {
                    ((IPFEM2DMeshableObject) this.obj).mesh(pom);
                }
            }
        }
    }

    @Override
    public void setMeshMethod(int method) {
        if (this.obj != null) {
            if (this.obj instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) this.obj).setMeshMethod(method);
            }
        }
    }

    @Override
    public void deleteMesh() {
        if (this.obj != null) {
            if (this.obj instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) this.obj).deleteMesh();
            }
        }
    }

    @Override
    public boolean isMeshed() {
        if (this.obj != null) {
            if (this.obj instanceof IPFEM2DMeshableObject) {
                return ((IPFEM2DMeshableObject) this.obj).isMeshed();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public PFEM2DNode[] getNodes() {
        if (this.isMeshed()) {
            return ((IPFEM2DMeshableObject) this.obj).getNodes();
        } else {
            return null;
        }
    }

    @Override
    public IPFEM2DElement[] getElements() {
        if (this.isMeshed()) {
            return ((IPFEM2DMeshableObject) this.obj).getElements();
        } else {
            return null;
        }
    }

    @Override
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel) {
        if (this.obj != null) {
            if (this.obj instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) this.obj).drawMesh(g, panel);
            }
        }
    }

    @Override
    public PFEM2DPoint getStartPoint() {
        return null;
    }

    @Override
    public PFEM2DPoint getEndPoint() {
        return null;
    }

    @Override
    public List<PFEM2DEdge> getEdges() {

        if (this.obj instanceof IPFEM2DCurve) {
            return ((IPFEM2DCurve) obj).getEdges();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public double[] getBoundingBox() {
        return this.obj.getBoundingBox();
    }

    @Override
    public PFEM2DPoint[] getPoints() {

        if (this.obj != null) {
            return this.obj.getPoints();
        } else {
            return null;
        }
    }
}
