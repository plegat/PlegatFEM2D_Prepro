package plegatfem2d_prepro;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import plegatfem2d_prepro.objects.IPFEM2DDrawableObject;
import plegatfem2d_prepro.objects.IPFEM2DElement;
import plegatfem2d_prepro.objects.IPFEM2DMeshableObject;
import plegatfem2d_prepro.objects.PFEM2DNode;
import plegatfem2d_prepro.objects.PFEM2DPoint;

/**
 *
 * @author jmb2
 */
public class PFEM2DObjectManager {

    public PFEM2DObjectManager() {
        this.objects = new ArrayList<IPFEM2DDrawableObject>();
    }

    public void init() {
        this.objects.clear();
    }

    public void addObject(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
    }

    public void removeObject(int rank) {
        this.objects.remove(rank);
    }

    public void removeObject(IPFEM2DDrawableObject obj) {
        this.objects.remove(obj);
    }

    public void draw(Graphics g, PFem2DGuiPanel panel) {

        Iterator<IPFEM2DDrawableObject> it = this.objects.iterator();

        while (it.hasNext()) {
            IPFEM2DDrawableObject obj = it.next();
            obj.draw(g, panel);
        }
    }

    public void updateId() {

        this.idCurrentPoint = 1;
        this.idCurrentNode = 1;
        this.idCurrentElement = 1;

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {

                PFEM2DNode[] nodes = ((IPFEM2DMeshableObject) obj).getNodes();

                if (nodes != null) {

                    for (int j = 0; j < nodes.length; j++) {
                        if (nodes[j].getNumId() > this.idCurrentNode) {
                            this.idCurrentNode = nodes[j].getNumId();
                        }
                    }
                }

                IPFEM2DElement[] elements = ((IPFEM2DMeshableObject) obj).getElements();

                if (elements != null) {

                    for (int j = 0; j < elements.length; j++) {

                        if (elements[j].getNumId() > this.idCurrentElement) {
                            this.idCurrentElement = elements[j].getNumId();
                        }
                    }
                }
            }

            if (obj instanceof IPFEM2DDrawableObject) {

                PFEM2DPoint[] pts = obj.getPoints();

                if (pts != null) {

                    for (int j = 0; j < pts.length; j++) {
                        long id = pts[j].getNumId();

                        if (id > this.idCurrentPoint) {
                            idCurrentPoint = id;
                        }
                    }
                }
            }
        }
    }
    private ArrayList<IPFEM2DDrawableObject> objects;
    private long idCurrentNode;
    private long idCurrentPoint;
    private long idCurrentElement;

    public long getIdCurrentElement() {
        idCurrentElement++;
        return idCurrentElement;
    }

    public long getIdCurrentNode() {
        idCurrentNode++;
        return idCurrentNode;
    }

    public long getIdCurrentPoint() {
        idCurrentPoint++;
        return idCurrentPoint;
    }

    public void incrementIdCurrentElement() {
        idCurrentElement++;
    }

    public void incrementIdCurrentNode() {
        idCurrentNode++;
    }

    public void incrementIdCurrentPoint() {
        idCurrentPoint++;
    }

    public void setIdCurrentNode(long idCurrentNode) {
        this.idCurrentNode = idCurrentNode;
    }

    public void setIdCurrentPoint(long idCurrentPoint) {
        this.idCurrentPoint = idCurrentPoint;
    }

    public void setIdCurrentElement(long idCurrentElement) {
        this.idCurrentElement = idCurrentElement;
    }

    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;     // x min
        data[1] = Double.NEGATIVE_INFINITY;     // x max
        data[2] = Double.POSITIVE_INFINITY;     // y min
        data[3] = Double.NEGATIVE_INFINITY;     // y max

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            double[] bb = iObject.getBoundingBox();

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

    public void mesh() {

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();

            if (iObject instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) iObject).mesh(this);
            }
        }
    }

    public boolean openFile(File fichier) {

        this.init();
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine moteur = manager.getEngineByName("jython");

        if (moteur == null) {
            System.out.println("Impossible de trouver le moteur de scripting recherché.");
            return false;
        } else {
            try {
                Bindings bindings = moteur.getBindings(ScriptContext.ENGINE_SCOPE);
                bindings.clear();
                bindings.put("pom", this);

                moteur.eval(new BufferedReader(new FileReader(fichier)));
                
                System.out.println("end of file importation");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return false;
            } catch (ScriptException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
