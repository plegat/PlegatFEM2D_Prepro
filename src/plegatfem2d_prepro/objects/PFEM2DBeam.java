package plegatfem2d_prepro.objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DBeam implements IPFEM2DDrawableObject,IPFEM2DElement {

    private PFEM2DNode nd1,nd2;
    private long id;
    private double modulus, area, inertia;
    private boolean visible;
    
    
    private IPFEM2DElement[] connected;

    public PFEM2DBeam(long id, PFEM2DNode nd1, PFEM2DNode nd2, double modulus, double area, double inertia) {
        this.nd1 = nd1;
        this.nd2 = nd2;
        this.id = id;
        this.modulus = modulus;
        this.area = area;
        this.inertia = inertia;
        
        this.visible = true;
        
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
    public void draw(Graphics g, PFEM2DGuiPanel panel) {
        if (this.isVisible()) {
            int xloc1 = panel.getLocalCoordX(this.nd1.getX());
            int yloc1 = panel.getLocalCoordY(this.nd1.getY());

            int xloc2 = panel.getLocalCoordX(this.nd2.getX());
            int yloc2 = panel.getLocalCoordY(this.nd2.getY());

            int xlocC = (xloc1 + xloc2 ) / 2;
            int ylocC = (yloc1 + yloc2 ) / 2;

            double coef = 0.9;

            xloc1 = (int) Math.round(xlocC + coef * (xloc1 - xlocC));
            yloc1 = (int) Math.round(ylocC + coef * (yloc1 - ylocC));

            xloc2 = (int) Math.round(xlocC + coef * (xloc2 - xlocC));
            yloc2 = (int) Math.round(ylocC + coef * (yloc2 - ylocC));

            
            g.setColor(Color.yellow);
            g.drawLine(xloc1, yloc1, xloc2, yloc2);
            
            //dessin id

            g.drawString("B" + this.id, xlocC, ylocC);
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
        
        List<IPFEM2DElement> connected1=this.nd1.getConnectedElements();
        List<IPFEM2DElement> connected2=this.nd2.getConnectedElements();
        
        List<IPFEM2DElement> common=new ArrayList<>();
        
        for (IPFEM2DElement current : connected1) {
            for (IPFEM2DElement comparedTo : connected2) {
                if (current==comparedTo) {
                    common.add(current);
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
