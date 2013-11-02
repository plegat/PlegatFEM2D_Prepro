package plegatfem2d_prepro.mesher;

import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.objects.PFEM2DSurface;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class Abstract2DMesher {

    protected PFEM2DSurface surface;

    public Abstract2DMesher(PFEM2DSurface surface) {
        this.surface = surface;
    }

    public PFEM2DSurface getSurface() {
        return surface;
    }

    public void setSurface(PFEM2DSurface surface) {
        this.surface = surface;
    }
    
    public boolean mesh(PFEM2DObjectManager pom) {        
        return true;
    }   
}
