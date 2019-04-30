package teamOrange;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import static teamOrange.Mapper.*;

/**
 * This is a static class that will treat a given .tbl file as a B+ tree
 */
public class TableTree {
    public static final String path = "data/tables/";

    RandomAccessFile table;
    int rootPage = 0;   // Remember we are indexing by 0
    int pageCount = 0;  // The count of how many pages exist in table
    int rowCount = 0;

    public TableTree(){}

    public TableTree(String tablename){
        //Creates table if there is none already
        try {
            // This section creates the directory if it hasn't already have one. -JY Maybe we should move this outside
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
                File subDirectory = new File("data/tables");
                if (!subDirectory.exists()) {
                    subDirectory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }
            } else {
                File subDirectory = new File("data/tables");
                if (!subDirectory.exists()) {
                    subDirectory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }

            }
            this.table = new RandomAccessFile(path + tablename, "rw");
            Page pg = new LeafTablePage(this.table, 0);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public Page addNewLeafPage(){
        Page p = new LeafTablePage(table, pageCount++);
        return p;
    }

    public Page addNewInterPage(){
        Page p = new InteriorTablePage(table, pageCount++);
        return p;
    }

    public TableTree(RandomAccessFile table){
        this.table = table;
        // get the root page
    }

    public Cell find(int rowid){
        //check the meta data for table
        //Root page should be pageIndex 0, if i did overflow right

        //do binary search through the array of offsets
        Page pg = InteriorTablePage.getPage(table, 0);
        int nextPageNo = 0;
        while(pg.typeOfPage == interiorTableBTreePage){
            InteriorTablePage p = InteriorTablePage.getPage(table, nextPageNo);
            InteriorTableCell cell = (InteriorTableCell)binary_search(p.cells, rowid);
            if(cell == null){
                nextPageNo = p.rightPageNo;
            } else {
                nextPageNo = cell.leftChildPageNo;
            }
            Page.getPage(table, nextPageNo);
        }

        LeafTablePage p = LeafTablePage.getPage(table, nextPageNo);
        LeafTableCell cell = (LeafTableCell)binary_search(p.cells, rowid);

        return cell;
    }

    /**
     * same thing as find(int rowid)
     * @param rowid
     * @return
     */
    public Cell search(int rowid){
        return find(rowid);
    }

    /**
     *
     * @param payload
     */
    public void insert(ArrayList<DataElement> payload){
        // Page access history
        ArrayList<InteriorTablePage> pageHistory = new ArrayList<InteriorTablePage>();

        // Get to the page the record will be inserted in

        Page pg = Page.getPage(table, 0);
        while(pg.rightPageNo != nullPageNo){
            pageHistory.add((InteriorTablePage)pg);
            pg = Page.getPage(table, pg.rightPageNo);
        }

        // Now that we have the page to insert in, we need to see if it'll fit in the page
        LeafTableCell cell = LeafTableCell.createCell(rowCount++, payload);
        if(cell.totalSize() <= pg.remainingBytes()){
            // since the cell's total size is less than the remaining bytes, insert it
            pg.addCell(cell);
        } else {
            // otherwise call overflow handler
            LeafTablePage sibling = overflow_handler(pg, pageHistory);
            sibling.addCell(cell);
        }

        for(InteriorTablePage p : pageHistory){
            int lastIndex = p.cells.size()-1;
            InteriorTableCell c = (InteriorTableCell)p.cells.get(lastIndex);
            c.rowid = cell.rowid;
            p.cells.set(lastIndex, c);
            p.write();
        }
    }

    public Cell delete(int rowid){
        // forget about overflow I guess
        try{
            // Get root page
            int pageIndex = 0;
            Page pg = Page.getPage(table, pageIndex);
            InteriorTablePage parent;
            InteriorTableCell parent_cell;
            while(pg.typeOfPage == interiorTableBTreePage){
                parent = (InteriorTablePage)pg;
                parent_cell = (InteriorTableCell)binary_search(pg.cells, rowid);
                if(parent_cell == null){
                    pageIndex = parent.rightPageNo;
                } else{
                    pageIndex = parent_cell.leftChildPageNo;
                }
            }

            pg = new LeafTablePage(table, pageIndex);
            if(pg.typeOfPage == leafTableBTreePage){
                int foundIndex = binary_search_index(pg.cells, rowid);
                if(foundIndex == -1){
                    System.out.println("ERROR: Cannot find the record");
                    return null;
                } else {
                    pg.cellOffsets.remove(foundIndex);
                    ((LeafTablePage)pg).write();
                }
            } else{
                throw new Exception("Cannot find page");
            }

        } catch(Exception e){
            System.out.println("ERROR: " + e);
        } finally{
            return null;
        }
    }

    Cell binary_search(ArrayList<Cell> array, int rowid){
        int start = 0;
        int end = array.size()-1;
        return binary_search( array, rowid, start, end);
    }

    Cell binary_search(ArrayList<Cell> array, int rowid, int start, int end){
        int mid = (start + end)/2;
        int mid_rowid;
        int mid_rowid_prev = -1;
        Cell cell = array.get(mid);
        if(start > end){
            return null;
        }
        if(cell.typeOfCell == leafTableBTreePage){
            mid_rowid = ((LeafTableCell)cell).rowid;
        } else {
            mid_rowid = ((InteriorTableCell)cell).rowid;
            if(mid > 0){
                InteriorTableCell cell_prev = (InteriorTableCell)array.get(mid-1);
                mid_rowid_prev = ((InteriorTableCell)cell_prev).rowid;
            } else {
                mid_rowid_prev = -1;
            }
        }

        if(rowid <= mid_rowid && rowid > mid_rowid_prev) {
            return cell;
        } else if(rowid < mid_rowid) {
            return binary_search(array, rowid, start, mid - 1);
        } else { // (rowid > mid_rowid)
            return binary_search(array, rowid, mid + 1, end);
        }
    }

    int binary_search_index(ArrayList<Cell> array, int rowid){
        int start = 0;
        int end = array.size()-1;
        return binary_search_index( array, rowid, start, end);
    }

    int binary_search_index(ArrayList<Cell> array, int rowid, int start, int end){
        int mid = (start + end)/2;
        int mid_rowid;
        int mid_rowid_prev = -1;
        if(start > end){
            return -1;
        }
        Cell cell = array.get(mid);
        if(cell.typeOfCell == leafTableBTreePage){
            mid_rowid = ((LeafTableCell)cell).rowid;
        } else {
            mid_rowid = ((InteriorTableCell)cell).rowid;
            if(mid > 0){
                InteriorTableCell cell_prev = (InteriorTableCell)array.get(mid-1);
                mid_rowid_prev = ((InteriorTableCell)cell_prev).rowid;
            } else {
                mid_rowid_prev = -1;
            }

        }

        if(rowid <= mid_rowid && rowid > mid_rowid_prev) {
            return mid_rowid;
        } else if(rowid < mid_rowid) {
            return binary_search_index(array, rowid, start, mid - 1);
        } else { // (rowid > mid_rowid)
            return binary_search_index(array, rowid, mid + 1, end);
        }
    }


    /**
     *
     * @param pg
     * @param pageHistory
     * @return The sibling page that was created
     */
     LeafTablePage overflow_handler(Page pg, ArrayList<InteriorTablePage> pageHistory){
        ArrayList<Page> pages = new ArrayList<Page>();
        int backIndex = 1;
        InteriorTablePage parent;
        if(pageHistory.isEmpty()){
            parent = new InteriorTablePage(table, 0);
            pg.setPageNo(pageCount++);
        } else {
            parent = pageHistory.get(pageHistory.size() - backIndex);
        }
        LeafTablePage sibling = new LeafTablePage(table, pageCount++);
        pg.rightPageNo = sibling.pageNo;
        parent.rightPageNo = sibling.pageNo;
        parent.addCell(pg.pageNo, ((InteriorTableCell)pg.cells.get(pg.cells.size() - 1)).rowid);

        return sibling;
    }

//    not really needed merp
//    void underflow_handler(){
//
//    }
}
