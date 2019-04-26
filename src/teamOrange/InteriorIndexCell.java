package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;

public class InteriorIndexCell extends Cell{
    int leftChildPageNo;
    int bytesPayload;
    byte numRids;
    byte dataType;
    ArrayList indexVal;
    ArrayList<Short> rids;

    /**
     * This constructor seeks the absolute offset given in the arguments and then reads the cell
     * @param table
     * @param offset
     */
    public InteriorIndexCell(RandomAccessFile table, int offset){
        try {
            leftChildPageNo = table.readInt();
            header.add(new DataElement(leftChildPageNo));

            bytesPayload = table.readShort();
            header.add(new DataElement(bytesPayload));

            numRids = table.readByte();
            payload.add(new DataElement(numRids));

            dataType = table.readByte();
            payload.add(new DataElement(dataType));

            ReadIndexValue r = new ReadIndexValue();
            r.ReadUnknown(table,dataType,bytesPayload,numRids); //looks up dataType serial code and reads based on size
            payload.add(new DataElement(indexVal.get(0),dataType));

            for(int i=0; i<numRids; i++)
                rids.add(table.readShort());
            payload.add(new DataElement(rids));

        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}
