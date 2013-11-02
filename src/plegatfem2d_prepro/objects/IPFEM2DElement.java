package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 *
 * @author Jean-Michel BORLOT
 */
public interface IPFEM2DElement {
    
    public String getId();
    public long getNumId();
    
    public PFEM2DNode[] getNodes();
    public PFEM2DNode getNode(int rank);
    
    public boolean isElementEdge(PFEM2DEdge edge);
    
    public void draw(Graphics g, PFEM2DGuiPanel panel);
}
