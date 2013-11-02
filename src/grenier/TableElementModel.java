/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grenier;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import plegatfem2d_prepro.objects.IPFEM2DElement;
import plegatfem2d_prepro.objects.PFEM2DTria;

/**
 *
 * @author Jean-Michel BORLOT
 */
public class TableElementModel extends AbstractTableModel {

    public TableElementModel(List<IPFEM2DElement> elements) {
        this.elements = elements;
    }

    public void setElements(List<IPFEM2DElement> elements) {
        this.elements = elements;
    }

    
    private List<IPFEM2DElement> elements;
    private String[] names={"ID","Node 1","Node 2","Node 3", "Neighbour 1","Neighbour 2","Neighbour 3"};
    

    
    @Override
    public int getRowCount() {
       if (this.elements==null) {
            return 0;
        } else {
           return this.elements.size();
       } 
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        if (this.elements==null) {
            return 0;
        }
        
        PFEM2DTria tria=(PFEM2DTria)this.elements.get(rowIndex);
        
        
        if (columnIndex==0) {
            return tria.getId();
        } else if (columnIndex<4) {
            return tria.getNode(columnIndex-1).getId();
        } else {
            
            IPFEM2DElement neig=tria.getNeighbours(columnIndex-4);
            
            if (neig==null) {
                return "---";
            } else {
                return neig.getId();
            }
        }
        
        
        
        
        
    }
    
    @Override
    public String getColumnName(int col) {
        
        
        return this.names[col];
        
    }
}
