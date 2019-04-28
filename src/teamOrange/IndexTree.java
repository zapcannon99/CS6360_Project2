package teamOrange;

import java.io.*;
import java.util.*;

public class IndexTree {

    Page page;
    Page parentPage;
    int pageNo;
    /*
    *Search method finds index cells that match a given indexVal for query, delete, and update
    * and for insert, returns the position in the cellOffsets arraylist where the given indexVal should be inserted
    * SearchType: 0 = query, 1 = insert, 2 = delete, 3 = update
    */
    public ArrayList<Short> Search(String tableName, Object indexVal, int searchType){
        //DataElement iValDataElement = new DataElement(iVal,dataType);
        ArrayList<Short> result = new ArrayList<Short>();
        pageNo = 0; //root = 0, should really be get pageNo from catalog
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
                        parentPage = page;
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
    * @param searchType, 1 = leaf Insert, 0 = interior insert
    * @return 0 = failure, 1 = success
    */
    public int InsertIndex(String tableName, int offset, Page page, int insertType) throws IOException{
        //create LeafIndexCell, get indexVal, and search for that indexVal
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        LeafIndexCell leafIndexCell = new LeafIndexCell(tableFileName, offset);
        InteriorIndexCell interiorIndexCell = new InteriorIndexCell(tableFileName, offset);
        Object indexVal;
        if(insertType == 1) {
            indexVal = leafIndexCell.getIndexVal();
        }
        else{
            indexVal = interiorIndexCell.getIndexVal();
        }
        ArrayList<Short> searchResult = new ArrayList <Short>();
        searchResult = Search(tableName, indexVal, 1);
        //if search return empty array, DO NOT insert
        //else, it will return the position of the cellsOffset array (where we want to insert)
        if(searchResult.size() == 0) //search found indexVal, CANNOT insert
            return 0;
        else { //INSERT AWAY!
            //get cellcontentstart offset from page and insert there
            //write leaf cell to file
            short cellOffset = writeCellToFile(tableFileName, page, page.startOfCellContent, leafIndexCell, null, 1);
            //update cell ptr array in page- with postion c returned from search

            ArrayList<Short> temp1 = new ArrayList <Short>();
            short position = searchResult.get(0);
            //move cellOffsets indeces right of position c into temp array
            for(int i = position; i<page.cellOffsets.size(); i++) {
                temp1.add(page.cellOffsets.get(i));
                page.cellOffsets.remove(i);
            }
            //add new cell offset to cellOfsets and add old cell offsets
            page.cellOffsets.add(position,cellOffset);
            for(int i = 0; i < temp1.size(); i++){
                page.cellOffsets.add(temp1.get(i));
            }

            ArrayList<Cell> temp2 = new ArrayList <Cell>();
            //move cells right of position c into temp array
            for(int i = position; i<page.cells.size(); i++) {
                temp2.add(page.cells.get(i));
                page.cells.remove(i);
            }
            //add new cell to cells array and add old cells to cells array
            if(insertType == 1)
                page.cells.add(leafIndexCell);
            else
                page.cells.add(interiorIndexCell);
            for(int i = 0; i < temp2.size(); i++){
                page.cells.add(temp2.get(i));
            }

            //write the cellOffsets array to page, increase noOfCells, and update startOfCellContent in page header
            writeCellArrayToFile(tableFileName, page, page.cellOffsets);
            page.incNoOfCells();
            page.updateStartOfCellContent(cellOffset);
            //check if splitting should occur
            if(page.SplitLimit(page.cells,page.cellOffsets))
                Split(tableName, page,1);
            return 1; //SUCCESS
        }
    }

    /*
    * @param tableName
    * @param splitType, 1 = leaf split, 0 = interior split
    */
    public void Split(String tableName, Page page, int splitType) throws IOException{
        if(splitType == 1){ //leaf splitting
            int newLeafPageNo = NewPage(tableName, 1);
            ParentInteriorPage(tableName,page.pageNo,newLeafPageNo);
        }else{ //interior splitting
            int newInteriorPageNo = NewPage(tableName,page.pageNo);
            ParentInteriorPage(tableName,page.pageNo,newInteriorPageNo);
        }
    }

    /*
     * @param tableName
     * @param pageType, 1 = leaf split, 0 = interior split
     */
    public int NewPage(String tableName, int pageType) throws IOException{
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        int midIndex = page.cells.size()/2 + 1; //takes either the center or right middle index
        //CELL OFFSETS ARRAY
        //move cellOffsets indeces right of position & including midIndex into temp array
        ArrayList<Short> temp1 = new ArrayList <Short>();
        for(int i = midIndex+1; i<page.cellOffsets.size(); i++) {
            page.cellOffsets.remove(i);
        }
        //CELLS ARRAY
        //move cells right of position & including midIndex into temp array
        ArrayList<Cell> temp2 = new ArrayList <Cell>();
        for(int i = midIndex+1; i<page.cells.size(); i++) {
            temp2.add(page.cells.get(i));
            page.cells.remove(i);
        }
        //create new Leaf or Interior Page
        Page newPage;
        if(pageType == 1) {
            newPage = new Page(tableName, (pageNo + 2), Mapper.leafIndexBTreePage);
        }
        else{
            newPage = new Page(tableName, (pageNo + 2), Mapper.interiorIndexBTreePage);
        }
        short writeOffset = (short) (page.startOfCellContent - temp2.size());
        //write each cell to the new Leaf page
        for(int i = 0; i < temp2.size(); i++){
            LeafIndexCell newLeafIndexCell = (LeafIndexCell) temp2.get(i);
            //write leaf cell to file
            short cellOffset = writeCellToFile(tableFileName, newPage, writeOffset, newLeafIndexCell, null, 1);
            temp1.add(cellOffset);
        }
        //write the cellOffsets array to the new Leaf or Interior page
        for(int i = 0; i<temp1.size(); i++){
            writeCellArrayToFile(tableFileName,newPage,temp1);
        }
        newPage.noOfCells = (short) temp2.size();
        newPage.startOfCellContent = writeOffset;
        return (pageNo + 2);
    }

    //keep track of parent in search, make a global var for parent page so that you know whether or not to make a new interior page
    public void ParentInteriorPage(String tableName, int oldLeafPageNo, int newLeafPageNo) throws IOException{
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        int midIndex = page.cells.size() / 2 + 1; //takes either the center or right middle index
        //Convert Leaf to Interior
        LeafIndexCell leafIndexCell = (LeafIndexCell) page.cells.get(midIndex); //leaf cell that needs to be converted to interior
        InteriorIndexCell interiorIndexCell =  new InteriorIndexCell();
        InteriorIndexCell newInteriorIndexCell = interiorIndexCell.convertLeaftoInterior(oldLeafPageNo, leafIndexCell); //returns converted interior from leaf, with added left child pageNo
        //if you don't have an existing parent interior page, create one
        if(parentPage == null) {
            Page newInteriorPage = new Page(tableName, (pageNo + 1), Mapper.interiorIndexBTreePage);
            newInteriorPage.rightPageNo = newLeafPageNo;
            //write interior cell to file
            short cellOffset = writeCellToFile(tableFileName, newInteriorPage, newInteriorPage.startOfCellContent, null, newInteriorIndexCell,0);
            newInteriorPage.cellOffsets.add(cellOffset);
        }else { //there is an existing parent interior page
            InsertIndex(tableName,parentPage.startOfCellContent,parentPage,0); //interior index insert
        }
    }

    public void writeCellArrayToFile(RandomAccessFile tableFileName, Page page, ArrayList<Short> cellOffsets){
        short cellArrWriteOffset = (short) (Page.getPageOffset(page.pageNo) + 9);
        try {
            tableFileName.seek(cellArrWriteOffset);
            for(int i=0; i<cellOffsets.size(); i++) {
                tableFileName.write(cellOffsets.get(i));
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    /*
    *Writes Cell (either interior or leaf) to File
    *@param writeType, 1 = leafIndexCell, 0 = interiorIndexCell
    */
    public short writeCellToFile(RandomAccessFile tableFileName, Page page, short startOfCellContent, LeafIndexCell leafIndexCell, InteriorIndexCell interiorIndexCell, int writeType){
        if(writeType == 1){
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
            //change the start of cell content ptr in header
            startOfCellContent = (short) (page.startOfCellContent - leafIndexCellSize);
            page.startOfCellContent = startOfCellContent;
            //increase number of record in header
            page.incNoOfCells();
            return cellWritePageOffset;
        }
        else{
            //find the size of the cell and seek to: page.getOffset()+(startOfCellContent-size)
            short header = (short) 6;
            short interiorIndexCellSize = (short) (interiorIndexCell.bytesPayload + header);
            short cellWritePageOffset = (short) (startOfCellContent - interiorIndexCellSize);
            short cellWriteOffset = (short) Page.getPageOffset(page.pageNo);  //WHY int and NOT short???
            try {
                tableFileName.seek(cellWriteOffset);
                tableFileName.write(interiorIndexCell.leftChildPageNo);
                tableFileName.write(interiorIndexCell.bytesPayload);
                tableFileName.write(interiorIndexCell.numRids);
                tableFileName.write(interiorIndexCell.dataType);
                ReadIndexValue w = new ReadIndexValue();
                w.WriteUnknown(tableFileName,interiorIndexCell.indexVal,interiorIndexCell.dataType);
                for(int i=0; i<interiorIndexCell.rids.size(); i++) {
                    tableFileName.write(interiorIndexCell.rids.get(i));
                }
            }
            catch(Exception e){
                System.out.println(e.toString());
            }
            //change the start of cell content ptr in header
            startOfCellContent = (short) (page.startOfCellContent - interiorIndexCellSize);
            page.startOfCellContent = startOfCellContent;
            //increase number of record in header
            page.incNoOfCells();
            return cellWritePageOffset;
        }
    }

    /*
    *Compare method compares the values of given indexVal to cellIndexVal
    *@return 0 = matching, 1 = indexVal is less, -1 = indexVal is greater
    */
    public int Compare(Object indexVal,Object cellIndexVal){
        if(indexVal instanceof Long || indexVal instanceof Double || indexVal instanceof Byte || indexVal instanceof Short || indexVal instanceof Integer){
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

