package teamOrange;

import java.math.BigInteger;
import java.util.ArrayList;

public class IndexTree {

    public ArrayList<Short> Search(String tableName, Object indexVal, byte dataType){
        ArrayList<Short> result = new ArrayList<Short>();
        int pageNo = 0; //root = 0
        Page page = Page.getPage(tableName,pageNo);
        InteriorIndexCell interiorIndexCell;
        LeafIndexCell leafIndexCell;
        int c = 0;
        Object cellIndexVal;
        boolean found = true;
        while(found) {
            if (page.typeOfPage == Mapper.interiorIndexBTreePage) { //page is an interior index page
                interiorIndexCell = (InteriorIndexCell) page.cells.get(c); //gets first interior cell
                cellIndexVal = interiorIndexCell.getIndexVal(); //gets index value from cell
                int compare = Compare(indexVal, cellIndexVal);
                int count = 1;
                while (compare != 0 || count <= page.cells.size()) { //look through cells array until indexVal found or end of cells array
                    compare = Compare(indexVal, cellIndexVal);
                    if (compare == 0) { //cell with given indexVal is found!
                        result = interiorIndexCell.getRids();
                        return result;
                    } else if (compare == 1) { //indexVal is less, go to left child
                        pageNo = interiorIndexCell.getLeftChildPageNo();
                        page = Page.getPage(tableName, pageNo);
                    } else { //indexVal is more, check next cell
                        c++;
                        if(count == page.cells.size()){ //end of cells array, go to the right child
                            pageNo = page.rightPageNo;
                            c = 0;
                        }
                        else {
                            interiorIndexCell = (InteriorIndexCell) page.cells.get(c); //gets next interior cell
                            cellIndexVal = interiorIndexCell.getIndexVal(); //gets index value from cell
                        }
                    }
                    count++;
                }
            } else { //page is a leaf index page
                found = false;

                leafIndexCell = (LeafIndexCell) page.cells.get(0); //gets first leaf cell
                cellIndexVal = leafIndexCell.getIndexVal(); //gets index value from cell
                int compare = Compare(indexVal, cellIndexVal);
                int count = 0;
                while (compare != 0 || count <= page.cells.size()) { //look through cells array until indexVal found or end of cells array
                    compare = Compare(indexVal, cellIndexVal);
                    if (compare == 0) { //cell with given indexVal is found! return array of associated row ids
                        return leafIndexCell.getRids();
                    }else { //indexVal is more, check next cell
                        c++;
                        if(count == page.cells.size()){ //end of cells array, indexVal NOT found, return null array
                            result.add(null);
                            return result;
                        }
                        else {
                            leafIndexCell = (LeafIndexCell) page.cells.get(c); //gets next leaf cell
                            cellIndexVal = leafIndexCell.getIndexVal(); //gets index value from cell
                        }
                    }
                    count++;
                }
            }
        }
        return result;
    }

    public int Compare(Object indexVal,Object cellIndexVal){
        if(indexVal instanceof Long || indexVal instanceof Double){
            double indexValNum = (Double) indexVal;
            double cellIndexValNum = (Double) cellIndexVal;
            if(indexValNum == cellIndexValNum)
                return 0;
            else if(indexValNum < cellIndexValNum)
                return 1;
            else return -1;
        }
        if(indexVal instanceof String){
            String indexValStr = (String) indexVal;
            String cellIndexValStr = (String) cellIndexVal;
            if(indexValStr.compareTo(cellIndexValStr) < 0)
                return 0;
            else if(indexValStr.compareTo(cellIndexValStr) < 0)
                return 1;
            else return -1;
        }
        return 0;
    }
}
