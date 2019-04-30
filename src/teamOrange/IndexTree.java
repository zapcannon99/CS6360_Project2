package teamOrange;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.*;

public class IndexTree {

    Page page;
    Page parentPage;
    int pageNo;

    /*
    * I will be given a cell and I will go through the cell to find the row id and the indeces that have been indexed
    * I will send the indexed values into DataElement to give it a java datatype, it will return an Object indexVal which then I can use instanceOf when necessary
    * return success or not
    */
    public Object QueryCellIndeces(String tableName, LeafTableCell leafTableCell){
        int rowid = leafTableCell.rowid; //get row id from given cell
        ArrayList<DataElement> columnTypes = leafTableCell.expandCell();
        Object datatype = columnTypes.get(0);
        byte dataType = (byte) datatype; //get dataType of that value
        Object indexVal = leafTableCell.columns.get(0); //get indexed values from given cell
        ArrayList searchResult = Search(tableName,indexVal,0);
        return searchResult.get(0); //return the leaf or interior cell
    }

    public boolean InsertCellIndeces(String tableName, LeafTableCell leafTableCell) throws IOException {
        int rowid = leafTableCell.rowid; //get row id from given cell
        ArrayList<DataElement> columnTypes = leafTableCell.expandCell();
        Object datatype = columnTypes.get(0);
        byte dataType = (byte) datatype; //get dataType of that value
        Object indexVal = leafTableCell.columns.get(0); //get indexed values from given cell
        if(InsertIndex(tableName,rowid,dataType,indexVal) == 1)
            return true;
        else return false;
    }

    public boolean DeleteCellIndeces(String tableName, LeafTableCell leafTableCell) throws IOException {
        int rowid = leafTableCell.rowid; //get row id from given cell
        ArrayList<DataElement> columnTypes = leafTableCell.expandCell();
        Object datatype = columnTypes.get(0);
        byte dataType = (byte) datatype; //get dataType of that value
        Object indexVal = leafTableCell.columns.get(0); //get indexed values from given cell
        if(DeleteIndex(tableName,rowid,dataType,indexVal) == 1)
            return true;
        else return false;
    }

    public boolean UpdateCellIndeces(String tableName, LeafTableCell leafTableCell,Object oldIndexVal) throws IOException{
        int rowid = leafTableCell.rowid; //get row id from given cell
        ArrayList<DataElement> columnTypes = leafTableCell.expandCell();
        Object datatype = columnTypes.get(0);
        byte dataType = (byte) datatype; //get dataType of that value
        Object indexVal = leafTableCell.columns.get(0); //get indexed values from given cell
        if(UpdateIndex(tableName,rowid,dataType,indexVal,oldIndexVal) == 1)
            return true;
        else return false;
    }

    /* add num of pages in file + 1 or 2
    * Search method finds index cells that match a given indexVal for query, delete, and update
    * and for insert, returns the position in the cellOffsets arraylist where the given indexVal should be inserted
    * SearchType: 0 = query, 1 = insert, 2 = delete, 3 = update
    */
    public ArrayList Search(String tableName, Object indexVal, int searchType){
        //DataElement iValDataElement = new DataElement(iVal,dataType);
        ArrayList result = new ArrayList();
        pageNo = 0; //root = 0, should really be get pageNo from catalog
        page = Page.getPage(tableName,pageNo);
        InteriorIndexCell interiorIndexCell;
        LeafIndexCell leafIndexCell;
        int c = 0; //position of cells arraylist
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
                            result.add(interiorIndexCell); //return cell with queried indexVal and associated row ids
                            return result;
                        }else if(searchType == 2){ //delete
                            //returns position of pointer to cell that we wish to delete, can also be used with cellsarray
                            result.add(c);
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
                        page.parentPageNo = parentPage.pageNo; //keep track of the parentPageNo
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
                            result.add(leafIndexCell); //return cell with queried indexVal and associated row ids
                            return result;
                        }else if(searchType == 2){ //delete
                            //returns position of pointer to cell that we wish to delete, can also be used with cellsarray
                            result.add(c);
                            return result;
                        }else if(searchType == 3){ //update
                            //add to rids array and reinsert
                            //result.add(page.cellOffsets.get(c)); //return current address
                            return result;
                        }
                        else return result; //already exists- CANNOT insert, returns empty arraylist
                    }else if (compare == 1) { //indexVal is less, return index of cells array
                        //insert
                        if(searchType == 1){ //return c: position in cellOffsets where we want to insert
                            result.add(c);
                            return result;
                        } //query,delete,update
                        else return result; //ONLY looking for matching indexVal, returns empty arraylist
                    }else { //indexVal is more, check next cell
                        c++;
                        if(count == page.cells.size()){ //end of cells array, indexVal NOT found, return null array
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

    //Insert
    /*******************************************************************************************************************************************************/
    /*
    *Insert method inserts new index cell into page, updates the cell offsets array, and header info
    * @param tableName
    * @param offset, of recently inserted record
    * @return 0 = failure, 1 = success, give indexVal not offset- looking for indexVal?
    */
    public int InsertIndex(String tableName, int rowid, byte dataType, Object iVal) throws IOException{
        //create LeafIndexCell from leaf table cell, get indexVal, and search for that indexVal
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        LeafIndexCell leafIndexCell = new LeafIndexCell(rowid,dataType,iVal);
        Object indexVal = leafIndexCell.getIndexVal();
        ArrayList<Integer> searchResult = new ArrayList <Integer>();
        //searchType: 1 = insert
        searchResult = Search(tableName, indexVal, 1);
        //if search return empty array, DO NOT insert
        //else, it will return the position of the cellsOffset array (where we want to insert)
        if(searchResult.size() == 0) //search found indexVal, CANNOT insert
            return 0;
        else { //INSERT AWAY!
            int position = searchResult.get(0);
            //insertType: 1 = leaf Insert, 0 = interior insert
            int success = Insert(tableName,page,leafIndexCell,null,position,Mapper.leafIndexBTreePage);
            return success;
        }
    }

    public int Insert(String tableName, Page page, LeafIndexCell leafIndexCell, InteriorIndexCell interiorIndexCell, int position, byte insertType) throws IOException{
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        //get cellcontentstart offset from page and insert there
        //write leaf cell to file
        short cellOffset = writeCellToFile(tableFileName, page, page.startOfCellContent, leafIndexCell, null);
        //update cell ptr array in page- with postion c returned from search

        ArrayList<Short> temp1 = new ArrayList <Short>();
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
        if(insertType == Mapper.leafIndexBTreePage) {
            page.cells.add(leafIndexCell);
        }
        else {
            page.cells.add(interiorIndexCell);
        }
        for(int i = 0; i < temp2.size(); i++){
            page.cells.add(temp2.get(i));
        }

        //write the cellOffsets array to page, increase noOfCells, and update startOfCellContent in page header
        writeCellArrayToFile(tableFileName, page, page.cellOffsets);
        page.incNoOfCells();
        page.updateStartOfCellContent(cellOffset);
        //check if splitting should occur
        if(page.SplitLimit(page.cells,page.cellOffsets))
            Split(tableName, page,Mapper.leafIndexBTreePage);
        return 1; //SUCCESS
    }
    /*
    * @param tableName
    * @param splitType, 1 = leaf split, 0 = interior split
    */
    public void Split(String tableName, Page page, byte splitType) throws IOException{
        if(splitType == Mapper.leafIndexBTreePage){ //leaf splitting
            int newLeafPageNo = NewPage(tableName, Mapper.leafIndexBTreePage);
            ParentInteriorPage(tableName,page,page.pageNo,newLeafPageNo);
        }else{ //interior splitting
            int newInteriorPageNo = NewPage(tableName,Mapper.interiorIndexBTreePage);
            ParentInteriorPage(tableName,page,page.pageNo,newInteriorPageNo);
        }
    }

    /*
     * @param tableName
     * @param pageType, 1 = leaf split, 0 = interior split
     */
    public int NewPage(String tableName, byte pageType) throws IOException{
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
        if(pageType == Mapper.leafIndexBTreePage) {
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
            short cellOffset = writeCellToFile(tableFileName, newPage, writeOffset, newLeafIndexCell, null);
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
    public void ParentInteriorPage(String tableName, Page page, int oldLeafPageNo, int newLeafPageNo) throws IOException{
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        int midIndex = page.cells.size() / 2 + 1; //takes either the center or right middle index
        //Convert Leaf to Interior
        LeafIndexCell leafIndexCell = (LeafIndexCell) page.cells.get(midIndex); //leaf cell that needs to be converted to interior
        InteriorIndexCell interiorIndexCell =  new InteriorIndexCell();
        InteriorIndexCell newInteriorIndexCell = interiorIndexCell.convertLeaftoInterior(oldLeafPageNo, leafIndexCell); //returns converted interior from leaf, with added left child pageNo
        //if you don't have an existing parent interior page, create one
        if(parentPage == null) {
            Page newInteriorPage = new Page(tableName, (page.pageNo + 1), Mapper.interiorIndexBTreePage);
            newInteriorPage.rightPageNo = newLeafPageNo;
            //write interior cell to file
            short cellOffset = writeCellToFile(tableFileName, newInteriorPage, newInteriorPage.startOfCellContent, null, newInteriorIndexCell);
            newInteriorPage.cellOffsets.add(cellOffset);
        }else { //there is an existing parent interior page
            //move cell to parent page and check for splitting, call split if full
            //find position within the parentpage that it needs to be inserted in- cellOffsets array
            Page parentPage = Page.getPage(tableName,page.parentPageNo);
            short position = 0;
            while(Compare(leafIndexCell.indexVal,parentPage.cells.get(position)) == 1){ //while moved indexVal is less than the parentPage cell indexVal
                position++;
            }
            Insert(tableName,parentPage,null, newInteriorIndexCell, position, Mapper.interiorIndexBTreePage);
        }
    }
    /********************************************************************************************************************************************************/

    //Delete
    /*******************************************************************************************************************************************************/
    //get back the position instead of the rids? I can delete that position from cellsArray But how do I tell if its the last one or if i just need to update?
    // get the position from Search and in here just see if the rids is equal to 1- this would be the last one to delete
    public int DeleteIndex(String tableName, int rowid, byte dataType, Object indexVal) throws IOException{
        //create LeafIndexCell from leaf table cell, get indexVal, and search for that indexVal
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        //LeafIndexCell leafIndexCell = new LeafIndexCell(rowid,dataType,iVal); //just to make iVal actually have a java dataType (for instanceof)
        //Object indexVal = leafIndexCell.getIndexVal();
        ArrayList<Integer> searchResult = new ArrayList <Integer>();
        //searchType: 2 = delete
        searchResult = Search(tableName, indexVal, 2);
        int position = searchResult.get(0); //position of both cellOffsets and cellsArray
        LeafIndexCell leafIndexCell = (LeafIndexCell) page.cells.get(position);
        InteriorIndexCell interiorIndexCell = (InteriorIndexCell) page.cells.get(position);
        //if search return empty array, DO NOT delete
        //else, it will return c: the position of the cellsOffset array (that we want to delete) or rids: the associated row ids (we will delete 1 of them)
        if(searchResult.size() == 0) //search did NOT found indexVal, CANNOT delete
            return 0;
        else { //DELETE AWAY!
            if(leafIndexCell.rids.size() == 1) { //last rowid of that indexval, just delete the ptr at the top of the page
                page.cellOffsets.remove(position); //delete at this position of cellOffsets, rearrange cellOffsets,
                /*this would be implementing underflow, not sure how this would work yet
                if(page.cellOffsets.size() == 0){//no more cells exist in this page

                }
                */
                writeCellArrayToFile(tableFileName,page,page.cellOffsets); //decrease page noOfCells-1, and write to file
                return 1;
            } else { //delete only 1 row id
                //find the row id to delete from rids, delete it, decrease numRids-1, and decrease bytesPayload-4
                if(page.typeOfPage == Mapper.leafIndexBTreePage) {
                    leafIndexCell.rids.remove(rowid);
                    leafIndexCell.numRids = (byte) (leafIndexCell.numRids - 1);
                    leafIndexCell.bytesPayload = (short) (leafIndexCell.bytesPayload - 4);
                    short endOfCellContent = (short) (page.cellOffsets.get(position) + leafIndexCell.bytesPayload + 2); //header = 2
                    writeCellToFile(tableFileName,page,endOfCellContent,leafIndexCell,null);
                } else{
                    interiorIndexCell.rids.remove(rowid);
                    interiorIndexCell.numRids = (byte) (leafIndexCell.numRids - 1);
                    interiorIndexCell.bytesPayload = (short) (leafIndexCell.bytesPayload - 4);
                    short endOfCellContent = (short) (page.cellOffsets.get(position) + leafIndexCell.bytesPayload + 6); //header = 6
                    writeCellToFile(tableFileName,page,endOfCellContent,null,interiorIndexCell);
                }
            }
        }
        return 1;
    }
    /*******************************************************************************************************************************************************/

    //Update
    /*******************************************************************************************************************************************************/
    //if search doesn't find indexVal, don't update and return 0
    //update: search for old indexVal and delete then search for new indexVal and update
    public int UpdateIndex(String tableName,int rowid,byte dataType,Object nIndexVal,Object oIndexVal) throws IOException{
        RandomAccessFile tableFileName = new RandomAccessFile(tableName, "rw");
        ArrayList<Integer> searchResult = Search(tableName,oIndexVal,3);
        if(searchResult.size() == 0){ //old indexVal was not found, no record was found that could be updated
            return 0;
        }else { //old indexVal found, delete it
            DeleteIndex(tableName, rowid, dataType, oIndexVal);
            searchResult = Search(tableName,nIndexVal,3);
            if(searchResult.size() == 0){ //new indexVal NOT found, insert it
                InsertIndex(tableName, rowid, dataType, nIndexVal);
            }else{ //indexVal found, update it
                int position = searchResult.get(0); //position of both cellOffsets and cellsArray
                LeafIndexCell leafIndexCell = (LeafIndexCell) page.cells.get(position);
                InteriorIndexCell interiorIndexCell = (InteriorIndexCell) page.cells.get(position);
                //find the row id to add to rids, add to it, increase numRids-1, and increase bytesPayload-4
                if(page.typeOfPage == Mapper.leafIndexBTreePage) {
                    leafIndexCell.rids.add(rowid);
                    leafIndexCell.numRids = (byte) (leafIndexCell.numRids + 1);
                    leafIndexCell.bytesPayload = (short) (leafIndexCell.bytesPayload + 4);
                    writeCellToFile(tableFileName,page,page.startOfCellContent,leafIndexCell,null);
                    if(page.SplitLimit(page.cells,page.cellOffsets))
                        Split(tableName, page,Mapper.leafIndexBTreePage);
                } else{
                    interiorIndexCell.rids.add(rowid);
                    interiorIndexCell.numRids = (byte) (leafIndexCell.numRids + 1);
                    interiorIndexCell.bytesPayload = (short) (leafIndexCell.bytesPayload + 4);
                    writeCellToFile(tableFileName,page,page.startOfCellContent,null,interiorIndexCell);
                    //if(page.SplitLimit(page.cells,page.cellOffsets)) this only work if it happens at the leaf...
                        //Split(tableName, page, Mapper.interiorIndexBTreePage);
                }
            }
            return 1;
        }
    }
    /*******************************************************************************************************************************************************/
    public void writeCellArrayToFile(RandomAccessFile tableFileName, Page page, ArrayList<Short> cellOffsets){
        short cellArrWriteOffset = (short) (Page.getPageOffset(page.pageNo) + 9);
        try {
            //write the new noOfCells on page
            tableFileName.seek(Page.getPageOffset(page.pageNo) + 1);
            tableFileName.write(cellOffsets.size());
            //write array
            tableFileName.seek(cellArrWriteOffset);
            for(int i=0; i<cellOffsets.size(); i++) {
                tableFileName.write(cellOffsets.get(i)); //gotta do writeShort
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
    public short writeCellToFile(RandomAccessFile tableFileName, Page page, short startOfCellContent, LeafIndexCell leafIndexCell, InteriorIndexCell interiorIndexCell){
        if(leafIndexCell != null){
            //find the size of the cell and seek to: page.getOffset()+(startOfCellContent-size)
            short header = (short) 2;
            short leafIndexCellSize = (short) (leafIndexCell.bytesPayload + header);
            short cellWritePageOffset = (short) (startOfCellContent - leafIndexCellSize);
            short cellWriteOffset = (short) (Page.getPageOffset(page.pageNo) + cellWritePageOffset);
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
            //increase number of record in header, done in writeArrayToFile
            //page.incNoOfCells();
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
            //increase number of record in header, done in writeArrayToFile
            //page.incNoOfCells();
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
