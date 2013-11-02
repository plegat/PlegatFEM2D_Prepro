/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author JMB
 */
public interface IPFEM2DElement {
    
    public String getId();
    public long getNumId();
    
    public PFEM2DNode[] getNodes();
    public PFEM2DNode getNode(int rank);
    
    public boolean isElementEdge(PFEM2DEdge edge);
    
    public void draw(Graphics g, PFem2DGuiPanel panel);

    
}
