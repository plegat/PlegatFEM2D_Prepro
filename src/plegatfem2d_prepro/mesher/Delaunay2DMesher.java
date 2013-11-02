package plegatfem2d_prepro.mesher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import plegatfem2d_prepro.PFEM2DObjectManager;
import plegatfem2d_prepro.objects.IPFEM2DCurve;
import plegatfem2d_prepro.objects.IPFEM2DDrawableObject;
import plegatfem2d_prepro.objects.IPFEM2DElement;
import plegatfem2d_prepro.objects.IPFEM2DMeshableObject;
import plegatfem2d_prepro.objects.NodeCouple;
import plegatfem2d_prepro.objects.PFEM2DEdge;
import plegatfem2d_prepro.objects.PFEM2DNode;
import plegatfem2d_prepro.objects.PFEM2DPoint;
import plegatfem2d_prepro.objects.PFEM2DSurface;
import plegatfem2d_prepro.objects.PFEM2DTria;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class Delaunay2DMesher extends Abstract2DMesher {

    public Delaunay2DMesher(PFEM2DSurface surface) {
        super(surface);

        this.nodes = this.surface.getNodesList();
        this.elements = this.surface.getElementsList();

    }
    private int MAX_LOOP_SWAP = 10;
    private int MAX_NUM_LOOP_REFINE = 10;
    private int MAX_LOOP_SMOOTH = 3;
    private double ratioLimitDiagonalSwapping = 1.05;
    private int NB_LOOP_LISSAGE = 1;
    private double RATIO_SPLIT_EDGE = 2.0;
    private double MIN_EDGE_LENGTH = 5;
    private double MIN_TRIA_ANGLE = 10;
    private double MAX_TRIA_ANGLE = 110;
    private double MIN_SMOOTH_DISTANCE = 1;
    private int DELTA_BOX = 20;
    private List<PFEM2DNode> nodes;
    private List<IPFEM2DElement> elements;

    @Override
    public boolean mesh(PFEM2DObjectManager pom) {

        if (this.surface == null) {
            return false;
        }

        long idFirstNode = pom.getIdCurrentNode();
        long idFirstElement = pom.getIdCurrentElement();
        List<PFEM2DNode> boundaryNodes = new ArrayList<>();

        // recuperation des noeuds de la frontiere

        for (IPFEM2DDrawableObject obj : this.surface.getObjects()) {
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

        // initialisation des connexions des noeuds

        for (PFEM2DNode pFEM2DNode : boundaryNodes) {
            pFEM2DNode.initConnectedElements();
        }

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

        PFEM2DNode nd1 = new PFEM2DNode(pom.getIdCurrentNode(), xmin - DELTA_BOX, ymin - DELTA_BOX);
        PFEM2DNode nd2 = new PFEM2DNode(pom.getIdCurrentNode(), xmax + DELTA_BOX, ymin - DELTA_BOX);
        PFEM2DNode nd3 = new PFEM2DNode(pom.getIdCurrentNode(), xmax + DELTA_BOX, ymax + DELTA_BOX);
        PFEM2DNode nd4 = new PFEM2DNode(pom.getIdCurrentNode(), xmin - DELTA_BOX, ymax + DELTA_BOX);

        this.nodes.add(nd1);
        this.nodes.add(nd2);
        this.nodes.add(nd3);
        this.nodes.add(nd4);

        PFEM2DTria tria1 = new PFEM2DTria(pom.getIdCurrentElement(), nd1, nd2, nd3, 1, 1);
        PFEM2DTria tria2 = new PFEM2DTria(pom.getIdCurrentElement(), nd1, nd3, nd4, 1, 1);


        this.elements.add(tria1);
        this.elements.add(tria2);

        nd1.addConnectedElement(tria1);
        nd1.addConnectedElement(tria2);
        nd2.addConnectedElement(tria1);
        nd3.addConnectedElement(tria1);
        nd3.addConnectedElement(tria2);
        nd4.addConnectedElement(tria2);

        tria1.findNeighbours();
        tria2.findNeighbours();

        // insertion des noeuds suivant algo de delaunay

        for (PFEM2DNode currentNode : boundaryNodes) {
            this.insertNode(currentNode, pom, null);
        }

        // récupération des edges de la frontiere
        // et definition de la longueur mini de maillage

        System.out.println("récupération des edges de la frontière...");

        ArrayList<PFEM2DEdge> listEdge = new ArrayList<>();
        MIN_EDGE_LENGTH = Double.POSITIVE_INFINITY;

        for (Iterator<IPFEM2DDrawableObject> it = this.surface.getObjects().iterator(); it.hasNext();) {
            IPFEM2DDrawableObject pFEM2DEdge = it.next();

            if (pFEM2DEdge instanceof IPFEM2DCurve) {

                List<PFEM2DEdge> edgeList = ((IPFEM2DCurve) pFEM2DEdge).getEdges();
                listEdge.addAll(edgeList);

                for (PFEM2DEdge edgeCurrent : edgeList) {
                    double edgeLength = edgeCurrent.getLength();

                    if ((edgeLength > 0) && (edgeLength < MIN_EDGE_LENGTH)) {
                        MIN_EDGE_LENGTH = edgeLength;
                    }
                }
            }
        }

        System.out.println("min edge length: " + MIN_EDGE_LENGTH);
        listEdge.trimToSize();

        // respect de la frontiere

        System.out.println("delaunay frontier research started");
        this.frontierDefinition(listEdge);
        System.out.println("delaunay frontier research finished");

        // suppression des éléments hors surface

        this.removeOuterElements(nd1, listEdge);
        System.out.println("outside elements removed");

        // marquage des noeuds comme étant noeuds frontière

        for (PFEM2DNode current : nodes) {
            current.setEdgeNode(true);
        }

        // raffinage du maillage

        this.meshRefinementByDelaunay(listEdge, pom);

        // réinitialisation des connexions

        this.initMeshConnectivities();

        //suppression des noeuds non connectes

        for (int i = this.nodes.size() - 1; i >= 0; i--) {
            PFEM2DNode nodeCurrent = this.nodes.get(i);

            List<IPFEM2DElement> connectedElements = nodeCurrent.getConnectedElements();

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
            //long nodeId = this.nodes.get(0).getNumId();
            long nodeId = idFirstNode;
            for (int i = 0; i < this.nodes.size(); i++) {
                PFEM2DNode node = this.nodes.get(i);
                node.setId(nodeId);
                nodeId++;
            }
            pom.setIdCurrentNode(nodeId);
        } else {
            pom.setIdCurrentNode(idFirstNode);
        }

        // renumerotation des éléments

        if (!this.elements.isEmpty()) {
            //long elementId = this.elements.get(0).getNumId();
            long elementId = idFirstElement;
            for (int i = 0; i < this.elements.size(); i++) {
                PFEM2DTria element = (PFEM2DTria) this.elements.get(i);
                element.setId(elementId);
                elementId++;
            }
            pom.setIdCurrentElement(elementId);
        } else {
            pom.setIdCurrentElement(idFirstElement);
        }

        // boucle de swapping des diagonales 

        System.out.println("boucle de swapping finale");
        this.swapDiagonals();

        // fin du maillage

        System.out.println("delaunay meshing finished");

        return true;
    }

    private boolean swapDiagonals() {

        // boucle de swapping des diagonales 

        int nbSwap = 1;
        int nbLoop = 0;

        while ((nbSwap > 0) && (nbLoop < MAX_LOOP_SWAP)) {
            nbLoop++;
            nbSwap = 0;

            System.out.println("Boucle de swapping de diagonales #" + nbLoop);

            for (IPFEM2DElement element : this.elements) {

                PFEM2DTria tria = (PFEM2DTria) element;

                PFEM2DEdge[] edge = new PFEM2DEdge[3];

                double qualPost[] = new double[3];


                for (int i = 0; i < 3; i++) {
                    edge[i] = tria.getEdgeOppositeToNode(i);

                    tria.findNeighbours();
                    PFEM2DTria tria2 = (PFEM2DTria) tria.getNeighbours(i);

                    if (tria2 != null) {

                        tria2.findNeighbours();

                        PFEM2DEdge edgeTemp = tria.getDiag(tria2);

                        if (edgeTemp == null) {
                            System.out.println(" edgeTemp=null");
                            tria.printNodes();
                            tria.printNeighbours();

                            tria2.printNodes();
                            tria2.printNeighbours();
                        }

                        //évaluation des qualités des triangles
                        double qual1init = tria.getQuality();
                        double qual2init = tria2.getQuality();

                        PFEM2DTria triaTemp1 = new PFEM2DTria(0, edgeTemp.getNode(0), edgeTemp.getNode(1), edge[i].getNode(0), 0, 0);
                        PFEM2DTria triaTemp2 = new PFEM2DTria(0, edgeTemp.getNode(0), edgeTemp.getNode(1), edge[i].getNode(1), 0, 0);

                        double qual1post = triaTemp1.getQuality();
                        double qual2post = triaTemp2.getQuality();

                        qualPost[i] = Math.min(qual1post, qual2post);

                        if (qualPost[i] < Math.min(qual1init, qual2init)) {
                            qualPost[i] = 0;
                        }
                    }
                }

                int rank;

                double[] qualPostSorted = new double[3];
                System.arraycopy(qualPost, 0, qualPostSorted, 0, 3);
                Arrays.sort(qualPostSorted);

                boolean flag = false;
                rank = 2;
                int rank2 = -1;

                do {
                    double value = qualPostSorted[rank];

                    if (value > 0) {
                        for (int i = 0; (i < 3 && (rank2 == -1)); i++) {
                            if (value == qualPost[i]) {
                                rank2 = i;
                            }
                        }

                        if (rank2 > -1) {
                            PFEM2DTria tria2 = (PFEM2DTria) tria.getNeighbours(rank2);
                            flag = tria.swapDiagonal(tria2);
                        }
                    }

                    rank--;

                } while ((!flag) && (rank >= 0));
            }

            System.out.println("Nombre de swap: " + nbSwap);
        }

        return true;
    }

    private boolean initMeshConnectivities() {

        // réinitialisation des connexions

        for (PFEM2DNode node : this.nodes) {
            node.initConnectedElements();
        }

        for (IPFEM2DElement element : this.elements) {
            PFEM2DTria tria = (PFEM2DTria) element;
            tria.initNeighbours();

            for (int i = 0; i < 3; i++) {
                PFEM2DNode node = tria.getNode(i);
                node.addConnectedElement(tria);
            }
        }

        for (IPFEM2DElement element : this.elements) {
            PFEM2DTria tria = (PFEM2DTria) element;
            tria.findNeighbours();
        }

        return true;
    }

    private List<PFEM2DTria> findTria2Remove(PFEM2DNode node, PFEM2DTria seed) {

        List<PFEM2DTria> trias2remove = new ArrayList<>(20);
        trias2remove.clear();

        boolean flag = true;

        if (seed == null) {
            for (int j = 0; (j < this.elements.size()) && (flag); j++) {

                PFEM2DTria tria = (PFEM2DTria) this.elements.get(j);

                if (tria.isInCircle(node)) {
                    trias2remove.add(tria);
                    flag = false;
                }
            }
        } else {
            trias2remove.add(seed);
        }

        int rank = 0;

        while (rank < trias2remove.size()) {
            PFEM2DTria tria = trias2remove.get(rank);

            for (int i = 0; i < 3; i++) {
                PFEM2DTria neighbour = (PFEM2DTria) tria.getNeighbours(i);

                if (neighbour != null) {
                    if ((trias2remove.indexOf(neighbour) == -1) && (neighbour.isInCircle(node))) {
                        trias2remove.add(neighbour);
                    }
                }
            }
            rank++;
        }

        return trias2remove;

    }

    private void removeTria(PFEM2DTria tria) {

        this.elements.remove(tria);

        PFEM2DNode[] triaNodes = tria.getNodes();

        triaNodes[0].removeConnectedElement(tria);
        triaNodes[1].removeConnectedElement(tria);
        triaNodes[2].removeConnectedElement(tria);

        for (int i = 0; i < 3; i++) {
            PFEM2DTria neighbour = (PFEM2DTria) tria.getNeighbours(i);

            if (neighbour != null) {
                neighbour.findNeighbours();
            }
        }
    }

    private boolean insertNode(PFEM2DNode node, PFEM2DObjectManager pom, PFEM2DTria seed) {

        //System.out.println("insert node " + node.getId());

        List<PFEM2DTria> trias2remove = new ArrayList<>(20);
        trias2remove.clear();

        trias2remove = this.findTria2Remove(node, seed);

        ArrayList<NodeCouple> couples = new ArrayList<>(20);

        for (int j = 0; j < trias2remove.size(); j++) {
            PFEM2DTria triaCurrent = trias2remove.get(j);

            this.removeTria(triaCurrent);

            PFEM2DNode[] triaNodes = triaCurrent.getNodes();

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
        List<PFEM2DTria> createdTria = new ArrayList<>(10);

        for (int j = 0; j < couples.size(); j++) {

            NodeCouple nc = couples.get(j);

            PFEM2DTria newTria = new PFEM2DTria(pom.getIdCurrentElement(), node, nc.getNd1(), nc.getNd2(), 0, 0);

            node.addConnectedElement(newTria);
            nc.getNd1().addConnectedElement(newTria);
            nc.getNd2().addConnectedElement(newTria);

            this.elements.add(newTria);
            createdTria.add(newTria);
        }

        for (PFEM2DTria currentTria : createdTria) {
            currentTria.updateNeighbours();

            for (int i = 0; i < 3; i++) {
                PFEM2DTria nei = (PFEM2DTria) currentTria.getNeighbours(i);

                if (nei != null) {
                    nei.updateNeighbours();
                }
            }
        }

        return true;
    }

    private double nodeSmoothing(PFEM2DNode node) {

        List<PFEM2DNode> listConnectedNodes = new ArrayList<>();

        double delta = 0;

        for (int i = 0; i < NB_LOOP_LISSAGE; i++) {

            listConnectedNodes.clear();

            if (!node.isEdgeNode()) {

                List<IPFEM2DElement> connectedElements = node.getConnectedElements();

                for (IPFEM2DElement iPFEM2DElement : connectedElements) {
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

                    for (PFEM2DNode currentNode : listConnectedNodes) {
                        xAverage += currentNode.getX();
                        yAverage += currentNode.getY();
                    }

                    xAverage /= nbConnectedNodes;
                    yAverage /= nbConnectedNodes;

                    // lissage barycentrique

                    node.setX(xAverage);
                    node.setY(yAverage);
                }
            }

        }

        return delta;
    }

    private boolean removeOuterElements(PFEM2DNode first, ArrayList<PFEM2DEdge> listEdge) {

        List<IPFEM2DElement> liste = first.getConnectedElements();
        List<PFEM2DTria> listToExplore = new ArrayList<>();

        for (IPFEM2DElement current : liste) {

            ((PFEM2DTria) current).setLocationOutside();
            listToExplore.add((PFEM2DTria) current);
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

                        for (PFEM2DEdge pFEM2DEdge1 : listEdge) {
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

                for (int j = 0; j < 3; j++) {
                    PFEM2DTria nei = (PFEM2DTria) tria.getNeighbours(j);
                    if (nei != null) {
                        nei.updateNeighbours();
                    }
                }
            }
        }

        return true;
    }

    private boolean frontierDefinition(ArrayList<PFEM2DEdge> listEdge) {

        for (Iterator<PFEM2DEdge> it = listEdge.iterator(); it.hasNext();) {
            PFEM2DEdge edge = it.next();
            PFEM2DNode nodEdge1 = edge.getNode(0);
            PFEM2DNode nodEdge2 = edge.getNode(1);

            List<IPFEM2DElement> trias1 = nodEdge1.getConnectedElements();
            List<IPFEM2DElement> trias2 = nodEdge2.getConnectedElements();

            // vérification edge élément

            boolean flag_edge_element = false;

            for (IPFEM2DElement iPFEM2DElement : trias2) {
                flag_edge_element = flag_edge_element || iPFEM2DElement.isElementEdge(edge);
            }

            // recherche des éléments intersectés par l'edge

            if (!flag_edge_element) {
                List<PFEM2DTria> listeElements = new ArrayList<>();
                List<PFEM2DEdge> listeEdges = new ArrayList<>();

                for (Iterator<IPFEM2DElement> it1 = trias1.iterator(); it1.hasNext();) {
                    PFEM2DTria pFEM2DTria = (PFEM2DTria) it1.next();

                    int edgeIntersection = pFEM2DTria.isEdgeIntersecting(edge, false);

                    if (edgeIntersection > 0) {
                        listeElements.add(pFEM2DTria);
                        List<PFEM2DEdge> liste = pFEM2DTria.getAllEdges(edgeIntersection);
                        listeEdges.addAll(liste);
                    }
                }


                for (Iterator<IPFEM2DElement> it1 = trias2.iterator(); it1.hasNext();) {
                    PFEM2DTria pFEM2DTria = (PFEM2DTria) it1.next();

                    int edgeIntersection = pFEM2DTria.isEdgeIntersecting(edge, false);

                    if (edgeIntersection > 0) {

                        int rank = listeElements.indexOf(pFEM2DTria);

                        if (rank == -1) {
                            listeElements.add(pFEM2DTria);

                            List<PFEM2DEdge> listIntersectingEdges = pFEM2DTria.getAllEdges(edgeIntersection);

                            for (PFEM2DEdge pFEM2DEdge : listIntersectingEdges) {

                                boolean flag = false;

                                for (PFEM2DEdge pFEM2DEdge1 : listeEdges) {
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

                                List<PFEM2DEdge> listIntersectingEdges = neig.getAllEdges(edgeIntersection);

                                for (PFEM2DEdge pFEM2DEdge : listIntersectingEdges) {

                                    boolean flag = false;

                                    for (PFEM2DEdge pFEM2DEdge1 : listeEdges) {
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

                while (listeEdges.size() > 0) {

                    for (int i = listeEdges.size() - 1; i >= 0; i--) {

                        PFEM2DEdge pFEM2DEdge = listeEdges.get(i);

                        pFEM2DEdge.findConnectedElements();
                        IPFEM2DElement[] connectedElements = pFEM2DEdge.getConnectedElements();

                        PFEM2DTria tria1 = (PFEM2DTria) connectedElements[0];
                        PFEM2DTria tria2 = (PFEM2DTria) connectedElements[1];

                        PFEM2DEdge diag = tria1.getDiag(tria2);

                        if (diag != null) {

                            PFEM2DPoint intersectionPoint = edge.getIntersection(0, diag, false);

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
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean meshRefinementByDelaunay(ArrayList<PFEM2DEdge> listEdge, PFEM2DObjectManager pom) {

        List<PFEM2DEdge> edgeTooLong = new ArrayList<>();    // liste des edge trop longs

        boolean flagLoop = true;
        int numLoop = 0;

        while (flagLoop) {

            numLoop++;

            System.out.println("* Refinement loop #" + numLoop);

            //recherche des edges trop longs
            //on ne garde que les edges trop longs supportant des triangles de mauvaise qualité

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

                edgeOppositeAngle[0] = tria.getAngle(0);
                edgeOppositeAngle[1] = tria.getAngle(1);
                edgeOppositeAngle[2] = tria.getAngle(2);

                for (int i = 0; i < 3; i++) {

                    boolean flag = false;

                    for (PFEM2DEdge pFEM2DEdge1 : listEdge) {
                        flag = flag || (pFEM2DEdge1.isSame(edgeTria[i]));
                    }

                    if (!flag) {

                        double ratio = edgeTriaLength[i] / minEdgeLength;
                        double diagLength = tria.getOppositeNode(edgeTria[i]).getDistanceTo(edgeTria[i].getMidPoint(0));

                        if ((ratio > RATIO_SPLIT_EDGE)
                                && (edgeTriaLength[i] > 2 * MIN_EDGE_LENGTH)
                                && (diagLength > 2 * MIN_EDGE_LENGTH)
                                && (edgeOppositeAngle[(i % 3)] > 2 * MIN_TRIA_ANGLE)) {

                            boolean flagEdge = false;

                            for (PFEM2DEdge edgeTemp : edgeTooLong) {
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

            // division des edges trop longs par insertion d'un noeud intermédiaire
            // par la méthode de Delaunay

            while (edgeTooLong.size() > 0) {

                boolean flagEdgeModified = false;
                PFEM2DEdge edgeCurrent = edgeTooLong.get(0);

                boolean flag = edgeCurrent.findConnectedElements();

                if (flag) {
                    PFEM2DNode midNode = edgeCurrent.getNode(0).getInterpolated(pom, edgeCurrent.getNode(1), 0.5);
                    midNode.initConnectedElements();
                    midNode.setVisible(true);

                    this.nodes.add(midNode);

                    flagEdgeModified = this.insertNode(midNode, pom, (PFEM2DTria) edgeCurrent.getConnectedElements()[0]);

                    if (flagEdgeModified) {
                        edgeModified++;
                    }
                }

                edgeTooLong.remove(0);
            }

            // lissage des points interne

            if ((edgeModified == 0) || (numLoop >= MAX_NUM_LOOP_REFINE)) {
                flagLoop = false;
            }
        }

        return true;
    }
}
