package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 *
 * @author Jean-Michel BORLOT
 */
public interface IPFEM2DMeshableObject {
    
    public void mesh(PFEM2DObjectManager pom);
    
    public void setMeshMethod(int method);

    public void deleteMesh();
    
    public boolean isMeshed();
    
    public PFEM2DNode[] getNodes();
    public IPFEM2DElement[] getElements();
    
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel);
    
    public static int DELAUNAY=1;
    public static int FRONT=2;
   
    public static int NODE_ONLY=3;
    public static int NODE_AND_BEAM=4;
}
