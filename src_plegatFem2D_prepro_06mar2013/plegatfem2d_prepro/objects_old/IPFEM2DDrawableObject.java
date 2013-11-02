/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author jmb2
 */
public interface IPFEM2DDrawableObject {

    public void draw(Graphics g, PFem2DGuiPanel panel);

    public void setVisible(boolean flag);

    public boolean isVisible();
    
    public PFEM2DPoint[] getPoints();
    
    public String getId();
    
    public double[] getBoundingBox();
    
}
