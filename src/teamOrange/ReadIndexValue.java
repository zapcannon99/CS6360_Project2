package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import static teamOrange.Mapper.*;

public class ReadIndexValue {

    public ArrayList indexVal;

    public ReadIndexValue(){

    }
    
    public ArrayList ReadUnknown(RandomAccessFile table, byte dataType, int bytesPayload, byte numRids){
        try{
            switch(dataType){
                case Mapper.typeCodeNull:
                    //figure this out, need to index null set attributes?
                    break;
                case Mapper.typeCodeTinyInt:
                    indexVal.add(table.readByte());
                    break;
                case Mapper.typeCodeSmallInt:
                    indexVal.add(table.readShort());
                    break;
                case Mapper.typeCodeInt:
                    indexVal.add(table.readInt());
                    break;
                case Mapper.typeCodeBigInt:
                    indexVal.add(table.readLong());
                    break;
                case Mapper.typeCodeDouble:
                    indexVal.add(table.readLong());
                    break;
                case Mapper.typeCodeYear:
                    indexVal.add(table.readByte());
                    break;
                case Mapper.typeCodeTime:
                    indexVal.add(table.readInt());
                    break;
                case Mapper.typeCodeDateTime:
                    indexVal.add(table.readLong());
                    break;
                case Mapper.typeCodeDate:
                    indexVal.add(table.readLong());
                    break;
                case Mapper.typeCodeText:
                    int bytes = bytesPayload-numRids*2-2;
                    byte[] bytesRead = new byte[bytes];
                    indexVal.add(table.read(bytesRead));
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return indexVal;
    }
}
