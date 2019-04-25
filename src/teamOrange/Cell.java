package teamOrange;

import teamOrange.DataElement;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import static teamOrange.Mapper.*;

public class Cell {
    byte typeOfCell;    // specifies the cell type. Must match the page, but we will have to work on how that is guaranteed
    ArrayList<DataElement> header;  // Has all the parts of the header of a cell, regardless of type of cell
    ArrayList<DataElement> payload; // Has all the parts of the payload, regardless of type of cell

    // needed for inheritance
    public Cell(){}

    /**
     * This constructor will create a cell for a given location that's already been seeked to before calling this constructor
     * @param table table flie descriptor
     * @param type type of page the cell will be in
     * @return
     */
    public static Cell read(RandomAccessFile table, byte type){
        switch(type){
            case interiorIndexBTreePage:
                // Nancy's stuff goes here
                break;
            case interiorTableBTreePage:
                return new InteriorTableCell(table, type);
            case leafIndexBTreePage:
                // Nancy's stuff goes here
                break;
            case leafTableBTreePage:
                break;
            default:
                throw new Exception("ERROR: Page type not recognized in the page header.");
                )
        }
    }

    /**
     * Constructor reads record, but must provide an absolute location. Don't include just the offset.
     * @param table table file descriptor
     * @param type type of page
     * @param offset ABSOLUTE offset that the record lies at
     * @return
     */
    public static Cell read(RandomAccessFile table, byte type, int offset){
        try {
            table.seek(offset);
            read(table, type);
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
