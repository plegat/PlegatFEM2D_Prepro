/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

import java.util.ArrayList;

/**
 *
 * @author jmb2
 */
public interface IPFEM2DCurve {

    public PFEM2DPoint getStartPoint();

    public PFEM2DPoint getEndPoint();   
    
    public ArrayList<PFEM2DEdge> getEdges();
}
