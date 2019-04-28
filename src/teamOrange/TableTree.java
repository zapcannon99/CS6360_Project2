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
    int rootPage;   // Remember we are indexing by 0
    int pageCount;  // The count of how many pages exist in table
    int rowCount;

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

    public int find(int rowid){
        //check the meta data for table

        //Using table root page, get the root page
        Page pg = Page.getPage(table.rootPage);

        //do binary search through the array of offsets
        binary_search(pg.offsets, rowid);

    }

    /**
     * same thing as find(int rowid)
     * @param rowid
     * @return
     */
    public int search(int rowid){
        return find(rowid);
    }

    /**
     * Insert a record in a table. payload is only the list of record cell body's list of column data values
     * @param table
     * @param rowid
     */
    public void insert(ArrayList<DataElement> payload){
        // Get to the page the record will be inserted in

        Page pg = Page.getPage(table, 0);
        while(pg.rightPageNo != nullPageNo){
            pg = Page.getPage(table, pg.rightPageNo);
        }

        // Now that we have the page to insert, we need to see if it'll fit in the page

    }

    public int delete(){
        //check if underflow
    }

    DataElement[] binary_search(int[] array, int rowid){
        int start = 0;
        int end = array.length;
        return binary_search(array, rowid, start, end);
    }

    DataElement[]  binary_search(int[] array, int rowid, int start, int end){
        int mid = (start + end)/2;
        int mid_rowid_offset = pg.readIntAt(array[mid]);
        if(rowid == mid_rowid_offset) {
            return readCell(mid_rowid_offset);
        } else if(rowid < mid_rowid_offset) {
            return binary_search(array, rowid, start, mid - 1);
        } else if(rowid > mid_rowid_offset) {
            return binary_search(array, rowid, mid + 1, end);
        }
    }

    DataElement[] readCell(int offset){
        if(pg.typeOfPage == Mapper.interiorTableBTreePage){
            //pg.readIntAt(offset + 4);
        } else {
            int seek = offset;
            //
        }
    }

    void overflow_handler(){

    }

    void underflow_handler(){

    }
}
