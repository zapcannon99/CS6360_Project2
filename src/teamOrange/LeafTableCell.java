package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class LeafTableCell extends Cell {
    // Stuff in the header
    short cellPayloadSize;
    int rowid;

    // Stuff in the payload
    byte numOfColumns;
    ArrayList<DataElement> columns;



    /**
     * This constructor assumes that the file pointer offset is already at the location of the cell to be read. If this
     * is not the case, please use InteriorTableCell(RandomAccessFile table, int offset) to seek first;
     * @param table table file descriptor with file pointer already seeked to the location of the cell to read
     */
    public LeafTableCell(RandomAccessFile table){
        typeOfCell = Mapper.leafTableBTreePage;
        try{
            // Read the header portion
            short data = table.readShort();
            header.add(new DataElement(data));
            cellPayloadSize = data;

            int data = table.readInt();
            header.add(new DataElement(data));
            rowid = data;


            // Read the payload portions
            int data = table.readInt();
            header.add(new DataElement(data));
            leftChildPageNo = data;
            data = table.readInt();
            header.add(new DataElement(data));
            rowid = data;
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    /**
     * This constructor seeks the absolute offset given in the arguments and then reads the cell
     * @param table
     * @param offset
     */
    public InteriorTableCell(RandomAccessFile table, int offset){

    }
}
