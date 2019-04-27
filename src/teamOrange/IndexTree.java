package teamOrange;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class IndexTree {

    Page page;

    /*
    *Search method finds index cells that match a given indexVal for query, delete, and update
    * and for insert, returns the position in the cellOffsets arraylist where the given indexVal should be inserted
    * SearchType: 0 = query, 1 = insert, 2 = delete, 3 = update
    */
    public ArrayList<Short> Search(String tableName, Object indexVal, int searchType){
        ArrayList<Short> result = new ArrayList<Short>();
        int pageNo = 0; //root = 0
        page = Page.getPage(tableName,pageNo);
        InteriorIndexCell interiorIndexCell;
        LeafIndexCell leafIndexCell;
        short c = 0; //position of cells arraylist
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
                        if(searchType == 0) { //query
                            result = interiorIndexCell.getRids(); //return associated row ids
                            return result;
                        }else if(searchType == 2){ //delete
                            //delete pointer to cell
                            //result.add(page.cellOffsets.get(c)); //return current address
                            return result;
                        }else if(searchType == 3){ //update
                            //add to rids array and reinsert
                            //result.add(page.cellOffsets.get(c)); //return current address
                            return result;
                        }
                        else return result; //already exists- CANNOT insert, returns empty arraylist
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
                leafIndexCell = (LeafIndexCell) page.cells.get(0); //gets first leaf cell
                cellIndexVal = leafIndexCell.getIndexVal(); //gets index value from cell
                int compare = Compare(indexVal, cellIndexVal);
                int count = 0;
                while (compare != 0 || count <= page.cells.size()) { //look through cells array until indexVal found or end of cells array
                    compare = Compare(indexVal, cellIndexVal);
                    if (compare == 0) { //cell with given indexVal is found! return array of associated row ids
                        if(searchType == 0) { //query
                            result = leafIndexCell.getRids(); //return associated row ids
                            return result;
                        }else if(searchType == 2){ //delete
                            //delete pointer to cell
                            //result.add(page.cellOffsets.get(c)); //return current address
                            return result;
                        }else if(searchType == 3){ //update
                            //add to rids array and reinsert
                            //result.add(page.cellOffsets.get(c)); //return current address
                            return result;
                        }
                        else return result; //already exists- CANNOT insert, returns empty arraylist
                    }else if (compare == 1) { //indexVal is less, return index of cells array
                        //insert
                        if(searchType == 1){ //c position is where we want to insert, return empty arraylist
                            result.add(c);
                            return result;
                        } //query,delete,update
                        else return result; //ONLY looking for matching indexVal, returns empty arraylist
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
                found = false;
            }
        }
        return result;
    }

    /*
    *Insert method inserts new index cell into page, updates the cell offsets array, and header info
    * @param tableName
    * @param offset, of recently inserted record
    * @return 0 = failure, 1 = success
    */
    public int InsertIndex(String tableName, int offset) throws IOException{
        //create LeafIndexCell, get indexVal, and search for that indexVal
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        LeafIndexCell leafIndexCell = new LeafIndexCell(tableFileName,offset);
        Object indexVal = leafIndexCell.getIndexVal();
        ArrayList<Short> searchResult = new ArrayList <Short>();
        searchResult = Search(tableName, indexVal, 1);
        //if search return empty array, DO NOT insert
        //else, it will return the position of the cellsOffset array (where we want to insert)
        if(searchResult.size() == 0) //search found indexVal, CANNOT insert
            return 0;
        else { //INSERT AWAY!
            //get cellcontentstart offset from page(global) and insert there
            short cellOffset = writeCellToFile(tableFileName, page.startOfCellContent, leafIndexCell);
            //update cell ptr array in page- with postion c returned from search
            ArrayList<Short> temp = new ArrayList <Short>();
            short position = searchResult.get(0);
            //move cellOffsets indeces right of position c into temp array
            for(int i = position; i<page.cellOffsets.size(); i++) {
                temp.add(page.cellOffsets.get(i));
                page.cellOffsets.remove(i);
            }
            //add new cell offset to cellOfsets and add old cell offsets
            page.cellOffsets.add(position,cellOffset);
            for(int i = 0; i < temp.size(); i++){
                page.cellOffsets.add(temp.get(i));
            }
            //write the cellOffsets array to page, increase noOfCells, and update startOfCellContent in page header
            writeCellArrayToFile(tableFileName);
            page.incNoOfCells();
            page.updateStartOfCellContent(cellOffset);
            return 1; //SUCCESS
        }
    }

    public void writeCellArrayToFile(RandomAccessFile tableFileName){
        short cellArrWriteOffset = (short) (Page.getPageOffset(page.pageNo) + 9);
        try {
            tableFileName.seek(cellArrWriteOffset);
            for(int i=0; i<page.cellOffsets.size(); i++) {
                tableFileName.write(page.cellOffsets.get(i));
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public short writeCellToFile(RandomAccessFile tableFileName, short startOfCellContent, LeafIndexCell leafIndexCell){
        //find the size of the cell and seek to: page.getOffset()+(startOfCellContent-size)
        short header = (short) 2;
        short leafIndexCellSize = (short) (leafIndexCell.bytesPayload + header);
        short cellWritePageOffset = (short) (startOfCellContent - leafIndexCellSize);
        short cellWriteOffset = (short) Page.getPageOffset(page.pageNo);  //WHY int and NOT short???
        try {
            tableFileName.seek(cellWriteOffset);
            tableFileName.write(leafIndexCell.bytesPayload);
            tableFileName.write(leafIndexCell.numRids);
            tableFileName.write(leafIndexCell.dataType);
            ReadIndexValue w = new ReadIndexValue();
            w.WriteUnknown(tableFileName,leafIndexCell.indexVal,leafIndexCell.dataType);
            for(int i=0; i<leafIndexCell.rids.size(); i++) {
                tableFileName.write(leafIndexCell.rids.get(i));
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return cellWritePageOffset;
    }

    /*
    *Compare method compares the values of given indexVal to cellIndexVal
    *@return 0 = matching, 1 = indexVal is less, -1 = indexVal is greater
    */
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
