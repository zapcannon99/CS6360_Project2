package teamOrange;

import teamOrange.DataElement;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import static teamOrange.Mapper.*;

public class Cell {
    byte typeOfCell;
    ArrayList<DataElement> header;
    ArrayList<DataElement> payload;

    public Cell(){}

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
        }
    }

    public static Cell read(RandomAccessFile table, byte type, int offset){
        try {
            table.seek(offset);
            read(table, type);
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
