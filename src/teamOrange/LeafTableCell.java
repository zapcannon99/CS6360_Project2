package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import static teamOrange.Mapper.*;

/**
 * Effectively, this is a Record
 */
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
        super();
        typeOfCell = Mapper.leafTableBTreePage;
        try{
            // Read the header portion
            cellPayloadSize = table.readShort();
            header.add(new DataElement(cellPayloadSize));

            rowid = table.readInt();
            header.add(new DataElement(rowid));

            // Read the payload portions
            numOfColumns = table.readByte();
            payload.add(new DataElement(numOfColumns));

            ArrayList<Integer> types = new ArrayList<Integer>();
            int b;
            for(int k = 0; k < numOfColumns; k++){
                b = table.readUnsignedByte();
                types.add(0xFF & b);
                payload.add(new DataElement(b));
            }

            DataElement e;
            int type;
            for(int k = 0; k < numOfColumns; k++){
                type = types.get(k);
                switch(type){
                    case typeCodeNull:
                        e = new DataElement();
                        break;
                    case typeCodeTinyInt:
                        e = new DataElement(table.readByte());
                        break;
                    case typeCodeSmallInt:
                        e = new DataElement(table.readShort());
                        break;
                    case typeCodeInt:
                        e = new DataElement(table.readInt());
                        break;
                    case typeCodeBigInt:
                        e = new DataElement(table.readLong());
                        break;
                    case typeCodeDouble:
                        e = new DataElement((table.readDouble()));
                        break;
                    case typeCodeDateTime:
                    case typeCodeDate:
                        long milliseconds = table.readLong();
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(milliseconds);
                        e = new DataElement(c);
                        break;
                    case typeCodeYear:
                        b = table.readByte();
                        e = DataElement.DataElementYear(b);
                        break;
                    default:
                        //default take cares of typeCodeText of any length
                        int length = type - typeCodeText;
                        String str = "";
                        for(int j = 0; k < length; k++){
                            str += (char)table.readByte();
                        }
                        e = new DataElement(str);
                        break;
                }
                if(type != typeCodeNull){
                    payload.add(e);
                    columns.add(e);
                }
            }
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
