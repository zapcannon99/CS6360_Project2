package teamOrange;

public class TableTree {

    int find(String table, int rowid){
        //check the meta data for table
        Table table = Table.getTable();

        //Using table root page, get the root page
        Page pg = Page.getPage(table.rootPage);

        //do binary search through the array of offsets
        binary_search(pg.offsets, rowid);

    }

    int search(){
        return find();
    }

    void insert(String table, int rowid){
        Table table = Table.getTable();

        Page pg = Page.getPage();

        if(pg.rightPagePointer != null){

        }
    }

    int delete(){

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
            pg.readIntAt(offset + 4);
        } else {
            int seek = offset;
            pg.read
        }
    }

    void overflow_handler(){

    }

    void underflow_handler(){

    }
}
