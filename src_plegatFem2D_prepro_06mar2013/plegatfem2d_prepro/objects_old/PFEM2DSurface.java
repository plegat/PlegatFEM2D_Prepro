package plegatfem2d_prepro.objects;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.PFem2DGuiPanel;

/**
 *
 * @author jmb2
 */
public class PFEM2DSurface implements IPFEM2DDrawableObject, IPFEM2DMeshableObject {

    private ArrayList<IPFEM2DDrawableObject> objects;
    private boolean visible;
    private long id;
    private boolean meshed;
    private ArrayList<PFEM2DNode> nodes;
    private ArrayList<IPFEM2DElement> elements;
    private double modulus, thickness;
    private int meshMethod = IPFEM2DMeshableObject.DELAUNAY;
    private int MAX_NUM_LOOP = 10;
    private int NB_LOOP_LISSAGE = 5;
    private double RATIO_SPLIT_EDGE = 1.7;
    private double MIN_EDGE_LENGTH = 10;
    private double MIN_TRIA_ANGLE = 10;

    public PFEM2DSurface(long id) {
        this.objects = new ArrayList<IPFEM2DDrawableObject>();
        
        this.id=id;
        this.visible=true;

        this.meshed = false;
        this.nodes = new ArrayList<PFEM2DNode>();
        this.elements = new ArrayList<IPFEM2DElement>();

    }

    public void setMeshProperties(double modulus, double thickness) {
        this.modulus = modulus;
        this.thickness = thickness;
    }

    public double getModulus() {
        return modulus;
    }

    public double getThickness() {
        return thickness;
    }

    public int getMAX_NUM_LOOP() {
        return MAX_NUM_LOOP;
    }

    public void setMAX_NUM_LOOP(int MAX_NUM_LOOP) {
        this.MAX_NUM_LOOP = MAX_NUM_LOOP;
    }

    public int getNB_LOOP_LISSAGE() {
        return NB_LOOP_LISSAGE;
    }

    public void setNB_LOOP_LISSAGE(int NB_LOOP_LISSAGE) {
        this.NB_LOOP_LISSAGE = NB_LOOP_LISSAGE;
    }

    public double getRATIO_SPLIT_EDGE() {
        return RATIO_SPLIT_EDGE;
    }

    public void setRATIO_SPLIT_EDGE(double RATIO_SPLIT_EDGE) {
        this.RATIO_SPLIT_EDGE = RATIO_SPLIT_EDGE;
    }

    public double getMIN_EDGE_LENGTH() {
        return MIN_EDGE_LENGTH;
    }

    public void setMIN_EDGE_LENGTH(double MIN_EDGE_LENGTH) {
        this.MIN_EDGE_LENGTH = MIN_EDGE_LENGTH;
    }

    public double getMIN_TRIA_ANGLE() {
        return MIN_TRIA_ANGLE;
    }

    public void setMIN_TRIA_ANGLE(double MIN_TRIA_ANGLE) {
        this.MIN_TRIA_ANGLE = MIN_TRIA_ANGLE;
    }

    public void add(IPFEM2DDrawableObject obj) {
        this.objects.add(obj);
    }

    @Override
    public void draw(Graphics g, PFem2DGuiPanel panel) {
        for (int i = 0; i < this.objects.size(); i++) {
            IPFEM2DDrawableObject obj = this.objects.get(i);
            obj.draw(g, panel);
        }

        if (this.isMeshed()) {
            this.drawMesh(g, panel);
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
        return "Surface " + this.id;
    }

    @Override
    public void mesh(PFEM2DObjectManager pom) {

        this.deleteMesh();
        pom.updateId();

        if (!this.isMeshed()) {

            for (int i = 0; i < this.objects.size(); i++) {

                IPFEM2DDrawableObject obj = this.objects.get(i);

                if (obj instanceof IPFEM2DMeshableObject) {

                    if (!((IPFEM2DMeshableObject) obj).isMeshed()) {
                        ((IPFEM2DMeshableObject) obj).mesh(pom);
                    }
                }
            }

            if (this.meshMethod == IPFEM2DMeshableObject.DELAUNAY) {
                this.meshDelaunay(pom);
            }
        }
    }

    @Override
    public void setMeshMethod(int method) {
        this.meshMethod = method;
    }

    @Override
    public void deleteMesh() {
        this.meshed = false;
        this.nodes.clear();
        this.elements.clear();
    }

    @Override
    public boolean isMeshed() {
        return this.meshed;
    }

    @Override
    public PFEM2DNode[] getNodes() {
        if (this.meshed) {
            return this.nodes.toArray(new PFEM2DNode[this.nodes.size()]);
        } else {
            return null;
        }
    }

    @Override
    public IPFEM2DElement[] getElements() {
        if (this.meshed) {
            return this.elements.toArray(new IPFEM2DElement[this.elements.size()]);
        } else {
            return null;
        }
    }

    @Override
    public void drawMesh(Graphics g, PFem2DGuiPanel panel) {

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {
                ((IPFEM2DMeshableObject) obj).drawMesh(g, panel);
            }
        }

        for (int i = 0; i < this.elements.size(); i++) {
            this.elements.get(i).draw(g, panel);
        }

        for (int i = 0; i < this.nodes.size(); i++) {
            this.nodes.get(i).draw(g, panel);
        }
    }

    public void meshDelaunay(PFEM2DObjectManager pom) {

        ArrayList<PFEM2DNode> boundaryNodes = new ArrayList<PFEM2DNode>();

        // recuperation des noeuds de la frontiere

        for (int i = 0; i < this.objects.size(); i++) {

            IPFEM2DDrawableObject obj = this.objects.get(i);

            if (obj instanceof IPFEM2DMeshableObject) {

                PFEM2DNode[] temp = ((IPFEM2DMeshableObject) obj).getNodes();

                boundaryNodes.addAll(Arrays.asList(temp));
            }
        }

        double xmin, xmax, ymin, ymax;
        xmin = Double.MAX_VALUE;
        ymin = Double.MAX_VALUE;
        xmax = Double.MIN_VALUE;
        ymax = Double.MIN_VALUE;

        // definition de la bounding box

        for (int i = 0; i < boundaryNodes.size(); i++) {

            PFEM2DNode node = boundaryNodes.get(i);

            double x = node.getX();
            double y = node.getY();

            if (x > xmax) {
                xmax = x;
            } else if (x < xmin) {
                xmin = x;
            }

            if (y > ymax) {
                ymax = y;
            } else if (y < ymin) {
                ymin = y;
            }
        }

        int deltaBox = 20;

        long idNode = pom.getIdCurrentNode();

        PFEM2DNode nd1 = new PFEM2DNode(idNode, xmin - deltaBox, ymin - deltaBox);
        PFEM2DNode nd2 = new PFEM2DNode(idNode + 1, xmax + deltaBox, ymin - deltaBox);
        PFEM2DNode nd3 = new PFEM2DNode(idNode + 2, xmax + deltaBox, ymax + deltaBox);
        PFEM2DNode nd4 = new PFEM2DNode(idNode + 3, xmin - deltaBox, ymax + deltaBox);

        this.nodes.add(nd1);
        this.nodes.add(nd2);
        this.nodes.add(nd3);
        this.nodes.add(nd4);

        long idElement = pom.getIdCurrentElement();

        this.elements.add(new PFEM2DTria(idElement, nd1, nd2, nd3, 1, 1));
        this.elements.add(new PFEM2DTria(idElement + 1, nd1, nd3, nd4, 1, 1));

        // insertion des noeuds suivant algo de delaunay

        ArrayList<PFEM2DTria> trias2remove = new ArrayList<PFEM2DTria>();

        for (int i = 0; i < boundaryNodes.size(); i++) {
            PFEM2DNode node = boundaryNodes.get(i);

            trias2remove.clear();

            for (int j = 0; j < this.elements.size(); j++) {

                PFEM2DTria tria = (PFEM2DTria) this.elements.get(j);

                if (tria.isInCircle(node)) {
                    trias2remove.add(tria);
                }
            }

            ArrayList<NodeCouple> couples = new ArrayList<NodeCouple>();

            for (int j = 0; j < trias2remove.size(); j++) {
                this.elements.remove(trias2remove.get(j));

                PFEM2DNode[] triaNodes = trias2remove.get(j).getNodes();

                couples.add(new NodeCouple(triaNodes[0], triaNodes[1]));
                couples.add(new NodeCouple(triaNodes[1], triaNodes[2]));
                couples.add(new NodeCouple(triaNodes[2], triaNodes[0]));
            }

            //nettoyage des couples

            for (int j = couples.size() - 1; j >= 0; j--) {

                NodeCouple nc = couples.get(j);

                for (int k = couples.size() - 1; k > j; k--) {

                    NodeCouple nc2 = couples.get(k);

                    if (nc2.isEqual(nc)) {
                        couples.remove(k);
                        couples.remove(j);
                        break;
                    }
                }
            }

            //creation des nouveaux trias

            couples.trimToSize();

            for (int j = 0; j < couples.size(); j++) {

                NodeCouple nc = couples.get(j);

                this.elements.add(new PFEM2DTria(pom.getIdCurrentElement(), node, nc.getNd1(), nc.getNd2(), 1, 1));
            }
        }

        // connectivités noeuds/edges/elements

        System.out.println("initialisation des connectivités des noeuds...");

        for (Iterator<IPFEM2DElement> it = this.elements.iterator(); it.hasNext();) {
            PFEM2DTria element = (PFEM2DTria) it.next();

            PFEM2DNode[] elementNodes = element.getNodes();

            for (PFEM2DNode pFEM2DNode : elementNodes) {
                pFEM2DNode.addConnectedElement(element);
            }
        }

        System.out.println("initialisation des connectivités des trias...");

        for (Iterator<IPFEM2DElement> it = this.elements.iterator(); it.hasNext();) {
            PFEM2DTria element = (PFEM2DTria) it.next();
            element.findNeighbours();
        }

        // recuperation des edges de la frontiere

        System.out.println("récupération des edges de la frontière...");

        ArrayList<PFEM2DEdge> listEdge = new ArrayList<PFEM2DEdge>();

        for (Iterator<IPFEM2DDrawableObject> it = this.objects.iterator(); it.hasNext();) {
            IPFEM2DDrawableObject pFEM2DEdge = it.next();

            if (pFEM2DEdge instanceof IPFEM2DCurve) {
                listEdge.addAll(((IPFEM2DCurve) pFEM2DEdge).getEdges());
            }
        }

        listEdge.trimToSize();

        // respect de la frontiere

        System.out.println("lancement respect de la frontière...");

        for (Iterator<PFEM2DEdge> it = listEdge.iterator(); it.hasNext();) {
            PFEM2DEdge edge = it.next();
            PFEM2DNode nodEdge1 = edge.getNode(0);
            PFEM2DNode nodEdge2 = edge.getNode(1);

            ArrayList<IPFEM2DElement> trias1 = nodEdge1.getConnectedElements();
            ArrayList<IPFEM2DElement> trias2 = nodEdge2.getConnectedElements();

            // vérification edge élément

            boolean flag_edge_element = false;

            for (Iterator<IPFEM2DElement> it1 = trias1.iterator(); it1.hasNext() && !flag_edge_element;) {
                PFEM2DTria iPFEM2DElement = (PFEM2DTria) it1.next();

                flag_edge_element = flag_edge_element || iPFEM2DElement.isElementEdge(edge);
            }

            // recherche des éléments intersectés par l'edge

            if (!flag_edge_element) {
                ArrayList<PFEM2DTria> listeElements = new ArrayList<PFEM2DTria>();
                ArrayList<PFEM2DEdge> listeEdges = new ArrayList<PFEM2DEdge>();

                for (Iterator<IPFEM2DElement> it1 = trias1.iterator(); it1.hasNext();) {
                    PFEM2DTria pFEM2DTria = (PFEM2DTria) it1.next();

                    int edgeIntersection = pFEM2DTria.isEdgeIntersecting(edge, false);

                    if (edgeIntersection > 0) {
                        listeElements.add(pFEM2DTria);

                        ArrayList<PFEM2DEdge> liste = pFEM2DTria.getAllEdges(edgeIntersection);

                        listeEdges.addAll(liste);

                        for (Iterator<PFEM2DEdge> it2 = liste.iterator(); it2.hasNext();) {
                            PFEM2DEdge pFEM2DEdge = it2.next();
                        }
                    }
                }

                for (Iterator<IPFEM2DElement> it1 = trias2.iterator(); it1.hasNext();) {
                    PFEM2DTria pFEM2DTria = (PFEM2DTria) it1.next();

                    int edgeIntersection = pFEM2DTria.isEdgeIntersecting(edge, false);

                    if (edgeIntersection > 0) {

                        int rank = listeElements.indexOf(pFEM2DTria);

                        if (rank == -1) {
                            listeElements.add(pFEM2DTria);

                            ArrayList<PFEM2DEdge> listIntersectingEdges = pFEM2DTria.getAllEdges(edgeIntersection);

                            for (Iterator<PFEM2DEdge> it2 = listIntersectingEdges.iterator(); it2.hasNext();) {
                                PFEM2DEdge pFEM2DEdge = it2.next();

                                boolean flag = false;
                                for (Iterator<PFEM2DEdge> it3 = listeEdges.iterator(); it3.hasNext() && !flag;) {
                                    PFEM2DEdge pFEM2DEdge1 = it3.next();

                                    flag = flag || (pFEM2DEdge1.isSame(pFEM2DEdge));
                                }

                                if (!flag) {
                                    listeEdges.add(pFEM2DEdge);
                                }
                            }
                        }
                    }
                }

                int rank = 0;

                while (rank < listeElements.size()) {

                    PFEM2DTria tria = listeElements.get(rank);

                    for (int i = 0; i < 3; i++) {
                        PFEM2DTria neig = (PFEM2DTria) tria.getNeighbours(i);

                        if ((neig != null) && (listeElements.indexOf(neig) == -1)) {

                            int edgeIntersection = neig.isEdgeIntersecting(edge, false);

                            if (edgeIntersection > 0) {

                                listeElements.add(neig);

                                ArrayList<PFEM2DEdge> listIntersectingEdges = neig.getAllEdges(edgeIntersection);

                                for (Iterator<PFEM2DEdge> it2 = listIntersectingEdges.iterator(); it2.hasNext();) {
                                    PFEM2DEdge pFEM2DEdge = it2.next();

                                    boolean flag = false;
                                    for (Iterator<PFEM2DEdge> it1 = listeEdges.iterator(); it1.hasNext() && !flag;) {
                                        PFEM2DEdge pFEM2DEdge1 = it1.next();

                                        flag = flag || (pFEM2DEdge1.isSame(pFEM2DEdge));
                                    }

                                    if (!flag) {
                                        listeEdges.add(pFEM2DEdge);
                                    }
                                }
                            }
                        }
                    }
                    rank++;
                }

                // traitement de la liste des edges intersectés

                int loop = 1;

                while (listeEdges.size() > 0) {

                    loop++;

                    for (Iterator<PFEM2DEdge> it1 = listeEdges.iterator(); it1.hasNext();) {
                        PFEM2DEdge pFEM2DEdge = it1.next();
                        pFEM2DEdge.findConnectedElements();
                    }

                    for (int i = listeEdges.size() - 1; i >= 0; i--) {

                        PFEM2DEdge pFEM2DEdge = listeEdges.get(i);

                        IPFEM2DElement[] connectedElements = pFEM2DEdge.getConnectedElements();

                        PFEM2DTria tria1 = (PFEM2DTria) connectedElements[0];
                        PFEM2DTria tria2 = (PFEM2DTria) connectedElements[1];

                        PFEM2DEdge diag = tria1.getDiag(tria2);

                        if (diag != null) {

                            PFEM2DPoint intersectionPoint = edge.getIntersection(diag, false);

                            if (intersectionPoint == null) {
                                boolean swapOK = tria1.swapDiagonal(tria2);

                                if (swapOK) {
                                    if (tria1.isEdgeIntersecting(edge, false) == 0) {
                                        listeElements.remove(tria1);
                                    }
                                    if (tria2.isEdgeIntersecting(edge, false) == 0) {
                                        listeElements.remove(tria2);
                                    }

                                    listeEdges.remove(pFEM2DEdge);
                                }
                            }
                        } else {
                            //System.out.println("diag=null");
                        }
                    }
                }
            }
        }

        System.out.println("delaunay frontier research finished");

        // suppression des éléments hors surface

        PFEM2DNode first = nodes.get(0);

        ArrayList<IPFEM2DElement> liste = first.getConnectedElements();
        ArrayList<PFEM2DTria> listToExplore = new ArrayList<PFEM2DTria>();

        for (Iterator<IPFEM2DElement> it = liste.iterator(); it.hasNext();) {
            PFEM2DTria tria = (PFEM2DTria) it.next();
            tria.setLocationOutside();
            listToExplore.add(tria);
        }

        System.out.println("outside elements seeding ok...");

        while (listToExplore.size() > 0) {

            PFEM2DTria tria = (PFEM2DTria) listToExplore.get(0);

            for (int i = 0; i < 3; i++) {
                PFEM2DTria nei = (PFEM2DTria) tria.getNeighbours(i);

                if ((nei != null) && (nei.getLocation() == PFEM2DTria.NOT_LOCALIZED) && (listToExplore.indexOf(nei) < 0)) {
                    PFEM2DEdge edge = tria.getEdgeOppositeToNode(i);

                    if (edge != null) {
                        boolean flag = false;
                        for (Iterator<PFEM2DEdge> it = listEdge.iterator(); it.hasNext() && !flag;) {
                            PFEM2DEdge pFEM2DEdge1 = it.next();
                            flag = flag || (pFEM2DEdge1.isSame(edge));
                        }

                        if (!flag) {
                            nei.setLocation(tria.getLocation());
                        } else {
                            nei.setLocation(tria.getLocationInverse());
                        }

                        listToExplore.add(nei);
                    }
                }
            }

            listToExplore.remove(0);
        }

        for (int i = elements.size() - 1; i >= 0; i--) {

            PFEM2DTria tria = (PFEM2DTria) elements.get(i);

            if (tria.getLocation() == PFEM2DTria.NOT_LOCALIZED) {
                System.out.println("location NOT DEFINED for element " + tria.getId());
            } else if (tria.getLocation() == PFEM2DTria.OUTSIDE) {
                for (int j = 0; j < 3; j++) {
                    tria.getNode(j).removeConnectedElement(tria);
                }

                elements.remove(i);
            }
        }

        System.out.println("outside elements removed");

        // raffinage du maillage

        // marquage des noeuds comme étant noeuds frontière

        for (Iterator<PFEM2DNode> it = nodes.iterator(); it.hasNext();) {
            PFEM2DNode nodeTemp = it.next();

            nodeTemp.setEdgeNode(true);
        }

        // raffinage du maillage

        ArrayList<PFEM2DEdge> edgeTooLong = new ArrayList<PFEM2DEdge>();    // liste des edge trop longs

        boolean flagLoop = true;
        int numLoop = 0;

        while (flagLoop) {

            numLoop++;

            System.out.println("* Refinement loop #" + numLoop);

            for (Iterator<IPFEM2DElement> it = elements.iterator(); it.hasNext();) {
                PFEM2DTria tria = (PFEM2DTria) it.next();

                PFEM2DEdge[] edgeTria = new PFEM2DEdge[3];
                edgeTria[0] = tria.getEdge(1);
                edgeTria[1] = tria.getEdge(2);
                edgeTria[2] = tria.getEdge(4);

                double[] edgeTriaLength = new double[3];
                for (int i = 0; i < 3; i++) {
                    edgeTriaLength[i] = edgeTria[i].getLength();
                }

                double minEdgeLength = Math.min(edgeTriaLength[0], Math.min(edgeTriaLength[1], edgeTriaLength[2]));

                double[] edgeOppositeAngle = new double[3];
                edgeOppositeAngle[0] = tria.getAngle(2);
                edgeOppositeAngle[1] = tria.getAngle(0);
                edgeOppositeAngle[2] = tria.getAngle(1);

                for (int i = 0; i < 3; i++) {

                    boolean flag = false;

                    for (Iterator<PFEM2DEdge> it2 = listEdge.iterator(); it2.hasNext() && !flag;) {
                        PFEM2DEdge pFEM2DEdge1 = it2.next();
                        flag = flag || (pFEM2DEdge1.isSame(edgeTria[i]));
                    }

                    if (!flag) {

                        double ratio = edgeTriaLength[i] / minEdgeLength;
                        double diagLength = tria.getOppositeNode(edgeTria[i]).getDistanceTo(edgeTria[i].getMidPoint());

                        if ((ratio > RATIO_SPLIT_EDGE)
                                && (edgeTriaLength[i] > 2 * MIN_EDGE_LENGTH)
                                && (diagLength > 2 * MIN_EDGE_LENGTH)
                                && (edgeOppositeAngle[i] > 2 * MIN_TRIA_ANGLE)
                                && (edgeOppositeAngle[(i + 1) % 3] > MIN_TRIA_ANGLE)
                                && (edgeOppositeAngle[(i + 2) % 3] > MIN_TRIA_ANGLE)) {

                            boolean flagEdge = false;
                            for (Iterator<PFEM2DEdge> it1 = edgeTooLong.iterator(); (it1.hasNext()) && (!flagEdge);) {
                                PFEM2DEdge edgeTemp = it1.next();

                                if (edgeTemp.toString().equals(edgeTria[i].toString())) {
                                    flagEdge = true;
                                }
                            }

                            if (!flagEdge) {
                                edgeTooLong.add(edgeTria[i]);
                            }
                        }
                    }
                }
            }

            int edgeModified = 0;

            while ((edgeTooLong.size() > 0) && (numLoop < MAX_NUM_LOOP)) {

                boolean flagEdgeModified = false;

                PFEM2DEdge edgeCurrent = edgeTooLong.get(0);

                PFEM2DNode midNode = edgeCurrent.getNode(0).getInterpolated(pom, edgeCurrent.getNode(1), 0.5);
                midNode.initConnectedElements();
                midNode.setVisible(true);

                this.nodes.add(midNode);

                edgeCurrent.findConnectedElements();

                IPFEM2DElement[] connectedElement = edgeCurrent.getConnectedElements();

                if ((connectedElement[0] != null) && (connectedElement[0] instanceof PFEM2DTria)) {

                    PFEM2DTria tria = (PFEM2DTria) connectedElement[0];

                    int oppositeNodeRank = tria.getOppositeNodeRank(edgeCurrent);
                    PFEM2DNode oppositeNode = tria.getNode(oppositeNodeRank);

                    tria.setNodes(oppositeNode, edgeCurrent.getNode(0), midNode);
                    PFEM2DTria tria2 = new PFEM2DTria(pom.getIdCurrentElement(), oppositeNode, edgeCurrent.getNode(1), midNode, tria.getModulus(), tria.getThickness());
                    this.elements.add(tria2);

                    oppositeNode.addConnectedElement(tria2);
                    edgeCurrent.getNode(1).removeConnectedElement(tria);
                    edgeCurrent.getNode(1).addConnectedElement(tria2);
                    midNode.addConnectedElement(tria);
                    midNode.addConnectedElement(tria2);

                    tria.findNeighbours();
                    tria2.findNeighbours();

                    flagEdgeModified = true;

                }

                if ((connectedElement[1] != null) && (connectedElement[1] instanceof PFEM2DTria)) {

                    PFEM2DTria tria = (PFEM2DTria) connectedElement[1];

                    int oppositeNodeRank = tria.getOppositeNodeRank(edgeCurrent);
                    PFEM2DNode oppositeNode = tria.getNode(oppositeNodeRank);

                    tria.setNodes(oppositeNode, edgeCurrent.getNode(0), midNode);
                    PFEM2DTria tria2 = new PFEM2DTria(pom.getIdCurrentElement(), oppositeNode, edgeCurrent.getNode(1), midNode, tria.getModulus(), tria.getThickness());
                    this.elements.add(tria2);

                    oppositeNode.addConnectedElement(tria2);
                    edgeCurrent.getNode(1).removeConnectedElement(tria);
                    edgeCurrent.getNode(1).addConnectedElement(tria2);
                    midNode.addConnectedElement(tria);
                    midNode.addConnectedElement(tria2);

                    tria.findNeighbours();
                    tria2.findNeighbours();

                    flagEdgeModified = true;

                }

                if (flagEdgeModified) {
                    edgeModified++;
                }

                edgeTooLong.remove(0);
            }

            // lissage des points interne

            ArrayList<PFEM2DNode> listConnectedNodes = new ArrayList<PFEM2DNode>();

            for (int i = 0; i < NB_LOOP_LISSAGE; i++) {

                for (Iterator<PFEM2DNode> it = this.nodes.iterator(); it.hasNext();) {
                    PFEM2DNode nodeCurrent = it.next();

                    listConnectedNodes.clear();

                    if (!nodeCurrent.isEdgeNode()) {

                        ArrayList<IPFEM2DElement> connectedElements = nodeCurrent.getConnectedElements();

                        for (Iterator<IPFEM2DElement> it1 = connectedElements.iterator(); it1.hasNext();) {
                            IPFEM2DElement iPFEM2DElement = it1.next();

                            if (iPFEM2DElement instanceof PFEM2DTria) {

                                PFEM2DNode[] triaNodes = ((PFEM2DTria) iPFEM2DElement).getNodes();

                                for (int j = 0; j < 3; j++) {

                                    if (listConnectedNodes.indexOf(triaNodes[j]) == -1) {
                                        listConnectedNodes.add(triaNodes[j]);
                                    }
                                }
                            }
                        }

                        double xAverage = 0;
                        double yAverage = 0;
                        int nbConnectedNodes = listConnectedNodes.size();

                        if (nbConnectedNodes > 0) {
                            for (Iterator<PFEM2DNode> it1 = listConnectedNodes.iterator(); it1.hasNext();) {
                                PFEM2DNode currentNode = it1.next();

                                xAverage += currentNode.getX();
                                yAverage += currentNode.getY();
                            }

                            xAverage /= nbConnectedNodes;
                            yAverage /= nbConnectedNodes;

                            double ratioAveraging = 0.3; // entre 0 et 1

                            nodeCurrent.setX(nodeCurrent.getX() + ratioAveraging * (xAverage - nodeCurrent.getX()));
                            nodeCurrent.setY(nodeCurrent.getY() + ratioAveraging * (yAverage - nodeCurrent.getY()));
                        }
                    }
                }
            }

            if ((edgeModified == 0) || (numLoop >= MAX_NUM_LOOP)) {
                flagLoop = false;
            }
        }

        //suppression des noeuds non connectes

        for (int i = this.nodes.size() - 1; i >= 0; i--) {
            PFEM2DNode nodeCurrent = this.nodes.get(i);

            ArrayList<IPFEM2DElement> connectedElements = nodeCurrent.getConnectedElements();

            if (connectedElements.isEmpty()) {
                this.nodes.remove(nodeCurrent);
            }
        }

        // orientation de la normale des éléments

        for (int i = 0; i < this.elements.size(); i++) {
            PFEM2DTria element = (PFEM2DTria) this.elements.get(i);

            if (element.getOrientation() < 0) {
                element.swapOrientation();
            }
        }

        // renumerotation des noeuds

        if (!this.nodes.isEmpty()) {
            long nodeId = this.nodes.get(0).getNumId();
            for (int i = 0; i < this.nodes.size(); i++) {
                PFEM2DNode node = this.nodes.get(i);
                node.setId(nodeId);
                nodeId++;
            }
            pom.setIdCurrentNode(nodeId);
        }

        if (!this.elements.isEmpty()) {
            long elementId = this.elements.get(0).getNumId();

            for (int i = 0; i < this.elements.size(); i++) {
                PFEM2DTria element = (PFEM2DTria) this.elements.get(i);
                element.setId(elementId);
                elementId++;
            }
            pom.setIdCurrentElement(elementId);
        }

        // fin du maillage

        System.out.println("delaunay meshing finished");
        this.meshed = true;
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
