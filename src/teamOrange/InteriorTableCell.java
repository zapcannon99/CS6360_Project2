package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class InteriorTableCell extends Cell {
    int leftChildPageNo;
    int rowid;

    public InteriorTableCell(){
        super();
        typeOfCell = Mapper.interiorTableBTreePage;
    }

    /**
     * This constructor assumes that the file pointer offset is already at the location of the cell to be read. If this
     * is not the case, please use InteriorTableCell(RandomAccessFile table, int offset) to seek first;
     * @param table table file descriptor with file pointer already seeked to the location of the cell to read
     */
    public InteriorTableCell(RandomAccessFile table){
        super();
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
        super();
        typeOfCell = Mapper.interiorTableBTreePage;
        try{
            table.seek(offset);
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

    public int totalSize(){
        return 8;
    }

    public static InteriorTableCell createCell(int leftChildPageNo, int rowid){
        InteriorTableCell cell = new InteriorTableCell();
        cell.leftChildPageNo = leftChildPageNo;
        cell.rowid = rowid;
        ArrayList<DataElement> header = new ArrayList<DataElement>();
        header.add(new DataElement(cell.leftChildPageNo));
        header.add(new DataElement(cell.rowid));
        cell.header = header;
        return cell;
    }

    /**
     * Read from a fd table that's already been seeked
     * @param table
     * @return
     */
    public static InteriorTableCell read(RandomAccessFile table){
        try{
            return new InteriorTableCell(table);
        } catch(Exception e){
            System.out.println("ERROR @InteriorTableCell.read(): " + e);
            return null;
        }
    }

    /**
     * Read InteriorTableCell at absolute offset
     * @param table
     * @param offset
     * @return
     */
    public static InteriorTableCell read(RandomAccessFile table, int offset){
        try{
            table.seek(offset);
            return read(table);
        } catch(Exception e){
            System.out.println("ERROR: " + e);
            return null;
        }
    }

    /**
     * pre seeked
     * @param table
     */
    public void write(RandomAccessFile table){
        try{
            table.writeInt(leftChildPageNo);
            table.writeInt(rowid);
        } catch(Exception e){
            System.out.println("ERROR: " + e);
        }
    }

}