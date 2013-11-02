/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plegatfem2d_prepro.objects;

/**
 *
 * @author jmb2
 */
public class PFEM2DVector {
    
    private PFEM2DPoint start;
    private double x,y;
    private long id;

    public PFEM2DVector(long id, PFEM2DPoint start, PFEM2DPoint end) {
        this.id = id;
        this.start = start;
        this.x=end.getX()-start.getX();
        this.y=end.getY()-start.getY();
    }

    public PFEM2DVector(long id, PFEM2DNode start, PFEM2DNode end) {
        this.id = id;
        this.start = new PFEM2DPoint(0, start.getX(), start.getY());
        this.x=end.getX()-start.getX();
        this.y=end.getY()-start.getY();
    }

    public PFEM2DVector(long id, PFEM2DPoint start, double x, double y) {
        this.id = id;
        this.start = start;
        this.x=x;
        this.y=y;
    }

    public PFEM2DVector(long id, PFEM2DNode start, double x, double y) {
        this.id = id;
        this.start = new PFEM2DPoint(0, start.getX(), start.getY());
        this.x=x;
        this.y=y;
    }

    
    
    
    public PFEM2DPoint getStart() {
        return start;
    }

    public void setStart(PFEM2DPoint start) {
        this.start = start;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public double getLength() {
        
        return Math.sqrt(x*x+y*y);
        
    }
    
    public void normalize() {
        
        double l=this.getLength();
        
        this.x/=l;
        this.y/=l;
                
    }
    
    
    public double scalar(PFEM2DVector vect2) {
    
        return this.x*vect2.x+this.y*vect2.y;
        
    }
    
    public double product(PFEM2DVector vect2) {
        
        return this.x*vect2.y-this.y*vect2.x;
        
    }
    
    
    
}
