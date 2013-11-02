/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author JMB
 */
public class PFEM2DBeam implements IPFEM2DDrawableObject,IPFEM2DElement {

    private PFEM2DNode nd1,nd2;
    private long id;
    private double modulus, area, inertia;
    
    private IPFEM2DElement[] connected;

    public PFEM2DBeam(long id, PFEM2DNode nd1, PFEM2DNode nd2, double modulus, double area, double inertia) {
        this.nd1 = nd1;
        this.nd2 = nd2;
        this.id = id;
        this.modulus = modulus;
        this.area = area;
        this.inertia = inertia;
        
        this.connected=new IPFEM2DElement[2];
        this.initConnectedElements();
        
    }
    
    public final void initConnectedElements() {
        this.connected[0]=null;
        this.connected[1]=null;
    }
    
    public boolean areConnectedElementsDefined() {
        return ((this.connected[0]!=null)&&(this.connected[1]!=null));
    }
    
    @Override
    public void draw(Graphics g, PFem2DGuiPanel panel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVisible(boolean flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isVisible() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getId() {
        return "Beam " + this.id;
    }
    
    @Override
    public long getNumId() {
        return this.id;
    }

    @Override
    public PFEM2DNode[] getNodes() {
        
        PFEM2DNode[] tempArray=new PFEM2DNode[2];
        tempArray[0]=this.nd1;
        tempArray[1]=this.nd2;
        
        return tempArray;
    }

    @Override
    public PFEM2DNode getNode(int rank) {
        
        if (rank==0) {
            return this.nd1;
        } else if (rank==1) {
            return this.nd2;
        } else {
            return null;
        }    
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
    
    
    public IPFEM2DElement[] getConnectedElements() {
        return connected;
    }
    
    public void findConnectedElements() {
        
        ArrayList<IPFEM2DElement> connected1=this.nd1.getConnectedElements();
        ArrayList<IPFEM2DElement> connected2=this.nd2.getConnectedElements();
        
        ArrayList<IPFEM2DElement> common=new ArrayList<IPFEM2DElement>();
        
        for (Iterator<IPFEM2DElement> it = connected1.iterator(); it.hasNext();) {
            IPFEM2DElement iPFEM2DElement = it.next();
            
            for (Iterator<IPFEM2DElement> it1 = connected2.iterator(); it1.hasNext();) {
                IPFEM2DElement iPFEM2DElement1 = it1.next();
                
                if (it==it1) {
                    common.add(iPFEM2DElement);
                    break;
                }
                
            }
            
        }
        
        this.connected[0]=common.get(0);
        this.connected[1]=common.get(1);
        
    }

    @Override
    public boolean isElementEdge(PFEM2DEdge edge) {
        
        PFEM2DNode nodEdge1=edge.getNode(0);
        PFEM2DNode nodEdge2=edge.getNode(1);
            
        boolean flag=true;
        
        flag=flag&&((nodEdge1==this.nd1)||(nodEdge1==this.nd2));
        flag=flag&&((nodEdge2==this.nd1)||(nodEdge2==this.nd2));
        
        return flag;
        
        
    }

    @Override
    public double[] getBoundingBox() {
        
        double data[] = new double[4];

        data[0] = Math.min(this.nd1.getX(), this.nd2.getX());
        data[1] = Math.max(this.nd1.getX(), this.nd2.getX());
        data[2] = Math.min(this.nd1.getY(), this.nd2.getY());
        data[3] = Math.max(this.nd1.getY(), this.nd2.getY());

        return data;
    }

    @Override
    public PFEM2DPoint[] getPoints() {
        return null;
    }
    
    
    
}
