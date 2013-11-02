package plegatfem2d_prepro.objects;

/**
 * Interface de définition des objets de type "courbe"
 * @author Jean-Michel BORLOT
 */
import java.util.List;

/**
     * Renvoie le point de départ de la courbe
     * @return le point de départ de la courbe
 */
public interface IPFEM2DCurve {

    public PFEM2DPoint getStartPoint();

    /**
     * Renvoie le point d'arrivée de la courbe
     * @return le point d'arrivée de la courbe
     */
    public PFEM2DPoint getEndPoint();   
    
    public List<PFEM2DEdge> getEdges();
}
