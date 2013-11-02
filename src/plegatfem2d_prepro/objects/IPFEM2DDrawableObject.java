package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 * Interface définissant les objets graphiques
 * @author Jean-Michel BORLOT
 */
public interface IPFEM2DDrawableObject {

    /**
     * Dessine l'objet graphique
     * @param g l'objet Graphics sur lequel dessiner
     * @param panel l'objet PFEM2DGuiPanel sur lequel dessiner
     */
    public void draw(Graphics g, PFEM2DGuiPanel panel);

    /**
     * Définit la visibilité de l'objet graphique
     * @param flag true si l'objet est visible, false s'il ne l'est pas
     */
    public void setVisible(boolean flag);

    /**
     * Renvoie l'état de visibilité de l'objet graphique
     * @return true si l'objet est visible, false s'il ne l'est pas
     */
    public boolean isVisible();
    
    /**
     * Renvoie la liste des points définissant l'objet graphique
     * @return la liste des points
     */
    public PFEM2DPoint[] getPoints();
    
    /**
     * Renvoie l'identité de l'objet graphique
     * @return l'identité de l'objet graphique
     */
    public String getId();
    
    /**
     * Renvoie la boite englobante de l'objet graphique
     * @return un tableau de 4 double: Xmin, Xmax, Ymin, Ymax
     */
    public double[] getBoundingBox();
    
}
