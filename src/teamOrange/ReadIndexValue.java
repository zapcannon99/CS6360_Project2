package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import static teamOrange.Mapper.*;

public class ReadIndexValue {

    public Object indexVal;

    public ReadIndexValue(){

    }
    
    public Object ReadUnknown(RandomAccessFile table, byte dataType, int bytesPayload, byte numRids){
        try{
            switch(dataType){
                case Mapper.typeCodeNull:
                    //figure this out, need to index null set attributes?
                    break;
                case Mapper.typeCodeTinyInt:
                    indexVal = table.readByte();
                    break;
                case Mapper.typeCodeSmallInt:
                    indexVal = table.readShort();
                    break;
                case Mapper.typeCodeInt:
                    indexVal = table.readInt();
                    break;
                case Mapper.typeCodeBigInt:
                    indexVal = table.readLong();
                    break;
                case Mapper.typeCodeDouble:
                    indexVal = table.readLong();
                    break;
                case Mapper.typeCodeYear:
                    indexVal = table.readByte();
                    break;
                case Mapper.typeCodeTime:
                    indexVal = table.readInt();
                    break;
                case Mapper.typeCodeDateTime:
                    indexVal = table.readLong();
                    break;
                case Mapper.typeCodeDate:
                    indexVal = table.readLong();
                    break;
                case Mapper.typeCodeText:
                    int bytes = bytesPayload-numRids*2-2;
                    byte[] bytesRead = new byte[bytes];
                    indexVal = table.read(bytesRead);
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return indexVal;
    }
}
