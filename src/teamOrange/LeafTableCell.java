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

    public LeafTableCell(){
        super();
    }

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

    public int totalSize(){
        return 6 + cellPayloadSize;
    }


    public ArrayList<DataElement> expandCell(){
        ArrayList<DataElement> columnTypes = new ArrayList<DataElement>();
        for(DataElement e : payload){
            columnTypes.add(new DataElement((byte)e.getDatatye()));
        }
        ArrayList<DataElement> cell = new ArrayList<DataElement>();
        cell.add(new DataElement(cellPayloadSize));
        cell.add(new DataElement(rowid));
        cell.add(new DataElement(numOfColumns));
        cell.addAll(columnTypes);
        cell.addAll(payload);
        return cell;
    }

    /**
     * Write cell to the ABSOLUTE offset in table
     * @param table RandomAccessFile
     * @param offset ABSOLUTE offset within file
     */
    void Write(RandomAccessFile table, int offset){
        try{
            table.seek(offset);

            table.writeShort(cellPayloadSize);
            table.writeInt(rowid);

            table.writeByte(numOfColumns);
            for(DataElement e : payload){
                table.writeByte(e.getDatatye());
            }

            for(DataElement e : payload){
                // First chekc if it's a string/text cuz it needs to be handled a little differently
                if(e.getDatatye() >= typeCodeText){
                    table.writeBytes(e.value_string);
                } else {
                    // Don't worry if it's not a string
                    switch(e.sizeof()){
                        case 0;
                            //basically, the type code is a null or an empty string, skip it
                            break;
                        case 1:
                            table.writeByte(e.value_long.byteValue());
                        case 2:
                            table.writeShort(e.value_long.shortValue());
                        case 4:

                        case 8:
                        default:
                            throw new Exception("Unexpected size for a DataElement");
                    }
                }
            }
        } catch(Exception e){
            System.out.println("ERROR: failed to write record. Reason: " + e);
        }
    }

    /*************************************************************
     * STATIC FUNCTIONS
     **************************************************************/

    /**
     * Formats payload which only contains the column of data values and pre-appends the the cell header and body headers
     * @param payload
     * @return formated cell to be inserted
     */
    public static LeafTableCell createCell(int rowid, ArrayList<DataElement> payload){
        LeafTableCell cell = new LeafTableCell();
        cell.rowid = rowid;
        cell.payload = payload;
        cell.numOfColumns = (byte)payload.size();
        cell.cellPayloadSize = calculatePayloadSize(payload);
    }


    public static short calculatePayloadSize(ArrayList<DataElement> payload){
        short size = 0;
        for(DataElement e : payload){
            size += e.sizeof();
        }
        size += payload.size() + 1; // 1 byte for every column and the byte for number of columns

        return size;
    }
}
}