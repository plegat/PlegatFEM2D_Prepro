/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grenier;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import plegatfem2d_prepro.objects.PFEM2DNode;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class TableNodeModel extends AbstractTableModel {

    public TableNodeModel(List<PFEM2DNode> nodes) {
        this.nodes = nodes;
    }

    public void setNodes(List<PFEM2DNode> nodes) {
        this.nodes = nodes;
    }

    
    private List<PFEM2DNode> nodes;
    private String[] names={"ID","x","y"};
    
    @Override
    public int getRowCount() {
        if (this.nodes==null) {
            return 0;
        } else {
            return this.nodes.size();
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        if (this.nodes==null) {
            return 0;
        } 
        
        PFEM2DNode node=this.nodes.get(rowIndex);
        
        if (columnIndex==0) {
            return node.getId();
        } else if (columnIndex==1){
            return node.getX();
        } else {
            return node.getY();
        }
        
        
        
    }
    
    @Override
    public String getColumnName(int col) {
        
        
        return this.names[col];
        
    }
    
}
