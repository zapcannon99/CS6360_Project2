package teamOrange;

import java.io.RandomAccessFile;
import java.util.Random;

public class InteriorTableCell extends Cell {
    int leftChildPageNo;
    int rowid;


    /**
     * This constructor assumes that the file pointer offset is already at the location of the cell to be read. If this
     * is not the case, please use InteriorTableCell(RandomAccessFile table, int offset) to seek first;
     * @param table table file descriptor with file pointer already seeked to the location of the cell to read
     */
    public InteriorTableCell(RandomAccessFile table){
        typeOfCell = Mapper.interiorTableBTreePage;
        try{
            int i = table.readInt();
            header.add(new DataElement(i));
            leftChildPageNo = i;
            i = table.readInt();
            header.add(new DataElement(i));
            rowid = i;
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
