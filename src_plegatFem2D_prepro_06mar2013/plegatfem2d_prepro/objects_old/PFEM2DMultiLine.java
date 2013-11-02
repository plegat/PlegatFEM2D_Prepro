package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author JMB
 */
public class PFEM2DMultiLine implements IPFEM2DDrawableObject, IPFEM2DMeshableObject, IPFEM2DCurve {

    private ArrayList<IPFEM2DDrawableObject> objects;
    private ArrayList<Boolean> orient;
    private boolean visible;
    private long id;

    public PFEM2DMultiLine() {
        this.objects = new ArrayList<IPFEM2DDrawableObject>();
        this.orient = new ArrayList<Boolean>();

    }

    public void add(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
        this.orient.add(true);
    }

    public boolean validate() {

        //this.printData();


        int nbObj = this.objects.size();

        for (int i = 0; i < nbObj; i++) {
            this.orient.set(i, true);
        }



        for (int i = 1; i < nbObj; i++) {

            IPFEM2DDrawableObject objStart = this.objects.get(0);
            PFEM2DPoint ptStart = ((IPFEM2DCurve) objStart).getStartPoint();

            IPFEM2DDrawableObject objEnd = this.objects.get(i - 1);
            PFEM2DPoint ptEnd;
            if (this.orient.get(i - 1) == true) {
                ptEnd = ((IPFEM2DCurve) objEnd).getEndPoint();
            } else {
                ptEnd = ((IPFEM2DCurve) objEnd).getStartPoint();
            }

            int rank = i;

            boolean flag = true;

            while ((flag) && (rank < nbObj)) {
                IPFEM2DDrawableObject objCurrent = this.objects.get(rank);
                PFEM2DPoint ptStartCurrent = ((IPFEM2DCurve) objCurrent).getStartPoint();
                PFEM2DPoint ptEndCurrent = ((IPFEM2DCurve) objCurrent).getEndPoint();

                if (ptStartCurrent == ptStart) {
                    this.objects.remove(rank);
                    this.orient.remove(rank);

                    this.objects.add(0, objCurrent);
                    this.orient.add(0, false);

                    flag = false;
                    //System.out.println("insertion tete reverse #"+(rank+1));
                } else if (ptStartCurrent == ptEnd) {
                    this.objects.remove(rank);
                    this.orient.remove(rank);

                    this.objects.add(i, objCurrent);
                    this.orient.add(i, true);

                    flag = false;
                    //System.out.println("insertion queue #"+(rank+1));
                } else if (ptEndCurrent == ptStart) {
                    this.objects.remove(rank);
                    this.orient.remove(rank);

                    this.objects.add(0, objCurrent);
                    this.orient.add(0, true);

                    flag = false;
                    //System.out.println("insertion tete normale #"+(rank+1));
                } else if (ptEndCurrent == ptEnd) {
                    this.objects.remove(rank);
                    this.orient.remove(rank);

                    this.objects.add(i, objCurrent);
                    this.orient.add(i, false);

                    flag = false;
                    //System.out.println("insertion queue reverse #"+(rank+1));
                }


                rank++;
            }

            if (flag) {
                System.out.println("erreur validation multi-ligne " + this.getId());
                System.out.println("pb positionnement sur objet #" + (i + 1));
                return false;
            }


        }

        //this.printData();

        return true;

    }

    @Override
    public void draw(Graphics g, PFem2DGuiPanel panel) {
        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            obj.draw(g, panel);
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
        return "MultiLine " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        boolean flag = this.validate();

        if (!flag) {
            System.out.println("multiline " + this.getId() + " not validated!!!");
            return;
        }

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {

                if (!((IPFEM2DMeshableObject) obj).isMeshed()) {
                    ((IPFEM2DMeshableObject) obj).mesh(pom);
                }


            }
        }
    }

    @Override
    public void setMeshMethod(int method) {

        for (int i = 0; i < this.objects.size(); i++) {

            if (this.objects.get(i) instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) this.objects.get(i)).setMeshMethod(method);
            }
        }

    }

    @Override
    public void deleteMesh() {
        for (int i = 0; i < this.objects.size(); i++) {

            if (this.objects.get(i) instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) this.objects.get(i)).deleteMesh();
            }
        }
    }

    @Override
    public boolean isMeshed() {

        boolean flag = true;

        for (int i = 0; i < this.objects.size(); i++) {

            if (this.objects.get(i) instanceof IPFEM2DMeshableObject) {
                flag = flag && ((IPFEM2DMeshableObject) this.objects.get(i)).isMeshed();
            }
        }

        return flag;
    }

    @Override
    public PFEM2DNode[] getNodes() {

        ArrayList<PFEM2DNode> listNodes = new ArrayList<PFEM2DNode>();


        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {

                PFEM2DNode[] objNodes = ((IPFEM2DMeshableObject) obj).getNodes();

                int startRank = 1;

                if (listNodes.isEmpty()) {
                    startRank = 0;
                }


                for (int j = 1; j < objNodes.length; j++) {
                    if (this.orient.get(i)) {
                        listNodes.add(objNodes[j]);
                    } else {
                        listNodes.add(objNodes[objNodes.length - j - 1]);
                    }
                }


            }
        }

        PFEM2DNode[] tempArray = new PFEM2DNode[listNodes.size()];
        return listNodes.toArray(tempArray);

    }

    @Override
    public IPFEM2DElement[] getElements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawMesh(Graphics g, PFem2DGuiPanel panel) {

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {

                ((IPFEM2DMeshableObject) obj).drawMesh(g, panel);

            }
        }

    }

    public void printData() {

        System.out.println("Multi-ligne #" + this.getId());

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);
            PFEM2DPoint ptS, ptE;

            if (this.orient.get(i)) {
                ptS = ((IPFEM2DCurve) obj).getStartPoint();
                ptE = ((IPFEM2DCurve) obj).getEndPoint();
            } else {
                ptS = ((IPFEM2DCurve) obj).getEndPoint();
                ptE = ((IPFEM2DCurve) obj).getStartPoint();
            }

            System.out.print("object #" + (i + 1) + ": " + obj.getId() + ", nodes: " + ptS.getId() + "/" + ptE.getId());
            System.out.println("");

        }


    }

    @Override
    public PFEM2DPoint getStartPoint() {
        return ((IPFEM2DCurve) this.objects.get(0)).getStartPoint();
    }

    @Override
    public PFEM2DPoint getEndPoint() {

        int nb = this.objects.size();

        if (this.orient.get(nb - 1)) {
            return ((IPFEM2DCurve) this.objects.get(nb - 1)).getEndPoint();
        } else {
            return ((IPFEM2DCurve) this.objects.get(nb - 1)).getStartPoint();

        }
    }

    @Override
    public ArrayList<PFEM2DEdge> getEdges() {

        ArrayList<PFEM2DEdge> liste = new ArrayList<PFEM2DEdge>();

        for (Iterator<IPFEM2DDrawableObject> it = this.objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject pFEM2DEdge = it.next();

            if (pFEM2DEdge instanceof IPFEM2DCurve) {
                liste.addAll(((IPFEM2DCurve) pFEM2DEdge).getEdges());
            }
        }

        liste.trimToSize();
        return liste;

    }

    @Override
    public double[] getBoundingBox() {

        double data[] = new double[4];

        data[0] = Double.POSITIVE_INFINITY;
        data[1] = Double.NEGATIVE_INFINITY;
        data[2] = Double.POSITIVE_INFINITY;
        data[3] = Double.NEGATIVE_INFINITY;

        for (Iterator<IPFEM2DDrawableObject> it = objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject iObject = it.next();
            double bb[] = iObject.getBoundingBox();

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

    @Override
    public PFEM2DPoint[] getPoints() {
        ArrayList<PFEM2DPoint> listPts=new ArrayList<PFEM2DPoint>();
        
        for (Iterator<IPFEM2DDrawableObject> it = this.objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject object = it.next();
            PFEM2DPoint[] pts=object.getPoints();
            listPts.addAll(Arrays.asList(pts));
        }
        
        return listPts.toArray(new PFEM2DPoint[listPts.size()]);
    }
}
