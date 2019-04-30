package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import static teamOrange.Mapper.*;

public class LeafIndexCell extends Cell {
    short bytesPayload;
    byte numRids;
    byte dataType;
    Object indexVal;
    ArrayList<Integer> rids;

    public LeafIndexCell(){

    }

    /*
    * creates leaf index cell from given info in leaf table cell
    */
    public LeafIndexCell(int rowid, byte dataTaype, Object indexVal){
        this.numRids = 1;
        this.dataType = dataTaype;
        DataElement dataElement = new DataElement(indexVal,dataType);
        this.indexVal = dataElement.getValue();
        this.bytesPayload = (short) (6 + dataElement.sizeof());
    }

    /**
     * This constructor seeks the absolute offset given in the arguments and then reads the cell
     * @param table
     * @param offset
     */
    public LeafIndexCell(RandomAccessFile table, int offset){
        try {
            bytesPayload = table.readShort();
            header.add(new DataElement(bytesPayload));

            numRids = table.readByte();
            payload.add(new DataElement(numRids));

            dataType = table.readByte();
            payload.add(new DataElement(dataType));

            ReadIndexValue r = new ReadIndexValue();
            r.ReadUnknown(table,dataType,bytesPayload,numRids); //looks up dataType serial code and reads based on size
            payload.add(new DataElement(indexVal,dataType));

            for(int i=0; i<numRids; i++)
                rids.add(table.readInt());
            payload.add(new DataElement(rids));
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public short getBytesPayload(){
        return bytesPayload;
    }
    public byte getNumRids(){
        return numRids;
    }
    public byte getDataType(){
        return dataType;
    }
    public Object getIndexVal(){
        return indexVal;
    }
    public ArrayList<Integer> getRids(){
        return rids;
    }
}
