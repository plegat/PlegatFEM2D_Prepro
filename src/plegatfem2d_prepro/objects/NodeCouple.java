package plegatfem2d_prepro.objects;

/**
 *
 * @author jmb2
 */
public class NodeCouple {

    PFEM2DNode nd1, nd2;

    public NodeCouple(PFEM2DNode nd1, PFEM2DNode nd2) {
        this.nd1 = nd1;
        this.nd2 = nd2;
    }

    public PFEM2DNode getNd1() {
        return nd1;
    }

    public void setNd1(PFEM2DNode nd1) {
        this.nd1 = nd1;
    }

    public PFEM2DNode getNd2() {
        return nd2;
    }

    public void setNd2(PFEM2DNode nd2) {
        this.nd2 = nd2;
    }

    public boolean isEqual(NodeCouple nc) {

        boolean flag1 = ((nc.nd1.equals(this.nd1)) && (nc.nd2.equals(this.nd2)));
        boolean flag2 = ((nc.nd2.equals(this.nd1)) && (nc.nd1.equals(this.nd2)));

        if (flag1 || flag2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Node couple " + this.nd1.getId() + "/" + this.nd2.getId();
    }
}
