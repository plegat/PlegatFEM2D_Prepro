package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFEM2DGuiPanel;

/**
 * Classe définissant l'objet "racine", composé d'un assemblage d'objets
 * graphiques
 *
 * @author Jean-Michel BORLOT
 */
public class PFEM2DModel implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private List<IPFEM2DDrawableObject> objects = new ArrayList<>();   // liste des objets constituant l'objet "racine"
    private boolean visible = true;                                         // état de visibilité de l'objet
    private boolean meshed = false;

      /**
     * Constructeur
     */
    public PFEM2DModel() {
        this.objects.clear();
    }

    /**
     * Ajoute un objet graphique à l'objet racine
     *
     * @param obj l'objet graphique
     */
    public void addObject(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
    }

    /**
     * Supprime un objet graphique de l'objet racine
     *
     * @param obj l'objet graphique
     */
    public void removeObject(IPFEM2DDrawableObject obj) {
        this.objects.remove(obj);
    }

    /**
     * Dessine l'objet graphique
     *
     * @param g l'objet Graphics sur lequel dessiner
     * @param panel l'objet PFEM2DGuiPanel sur lequel dessiner
     */
    @Override
    public void draw(Graphics g, PFEM2DGuiPanel panel) {

        for (IPFEM2DDrawableObject current : this.objects) {
            if (current.isVisible()) {
                current.draw(g, panel);
            }
        }
    }

    /**
     * Définit la visibilité de l'objet graphique
     *
     * @param flag true si l'objet est visible, false s'il ne l'est pas
     */
    @Override
    public void setVisible(boolean flag) {
        this.visible = flag;
    }

    /**
     * Renvoie l'état de visibilité de l'objet graphique
     *
     * @return true si l'objet est visible, false s'il ne l'est pas
     */
    @Override
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Renvoie l'identité de l'objet graphique
     *
     * @return l'identité de l'objet graphique
     */
    @Override
    public String getId() {
        return "Root";
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        if (!this.isMeshed()) {

            for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
                IPFEM2DDrawableObject iObject = it.next();

                if (iObject instanceof IPFEM2DMeshableObject) {
                    ((IPFEM2DMeshableObject) iObject).mesh(pom);
                }
            }

            this.meshed = true;
        }
    }

    @Override
    public void setMeshMethod(int method) {
    }

    @Override
    public void deleteMesh() {

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) iObject).deleteMesh();
            }
        }

        this.meshed = false;
    }

    @Override
    public boolean isMeshed() {
        return this.meshed;
    }

    @Override
    public PFEM2DNode[] getNodes() {

        ArrayList<PFEM2DNode> listNodes = new ArrayList<>();

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                PFEM2DNode[] objNodes = ((IPFEM2DMeshableObject) iObject).getNodes();

                if (objNodes != null) {
                    for (int i = 0; i < objNodes.length; i++) {
                        if (listNodes.indexOf(objNodes[i]) == -1) {
                            listNodes.add(objNodes[i]);
                        }
                    }
                }
            }
        }

        return listNodes.toArray(new PFEM2DNode[listNodes.size()]);
    }

    @Override
    public IPFEM2DElement[] getElements() {

        ArrayList<IPFEM2DElement> listElements = new ArrayList<>();

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                IPFEM2DElement[] objElements = ((IPFEM2DMeshableObject) iObject).getElements();

                if (objElements != null) {
                    for (int i = 0; i < objElements.length; i++) {
                        if (listElements.indexOf(objElements[i]) == -1) {
                            listElements.add(objElements[i]);
                        }
                    }
                }
            }
        }

        return listElements.toArray(new IPFEM2DElement[listElements.size()]);
    }

    @Override
    public void drawMesh(Graphics g, PFEM2DGuiPanel panel) {
        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) iObject).drawMesh(g, panel);
            }
        }
    }

    /**
     * Renvoie la boite englobante de l'objet graphique
     *
     * @return un tableau de 4 double: Xmin, Xmax, Ymin, Ymax
     */
    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;
        data[1] = Double.NEGATIVE_INFINITY;
        data[2] = Double.POSITIVE_INFINITY;
        data[3] = Double.NEGATIVE_INFINITY;

        for (IPFEM2DDrawableObject current : this.objects) {

            double bb[] = current.getBoundingBox();

            if (bb[0] < data[0]) {
                data[0] = bb[0];
            }
            if (bb[1] > data[1]) {
                data[1] = bb[1];
            }
            if (bb[2] < data[2]) {
                data[2] = bb[2];
            }
            if (bb[3] > data[3]) {
                data[3] = bb[3];
            }
        }

        return data;
    }

    /**
     * Renvoie la liste des points définissant l'objet graphique
     *
     * @return la liste des points
     */
    @Override
    public PFEM2DPoint[] getPoints() {
        ArrayList<PFEM2DPoint> listPts = new ArrayList<>();

        for (IPFEM2DDrawableObject current : this.objects) {
            PFEM2DPoint[] pts = current.getPoints();
            listPts.addAll(Arrays.asList(pts));
        }

        return listPts.toArray(new PFEM2DPoint[listPts.size()]);
    }
}
