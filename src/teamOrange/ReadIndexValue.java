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

    public void WriteUnknown(RandomAccessFile table, Object indexVal, byte dataType){
        try{
            switch(dataType){
                case Mapper.typeCodeNull:
                    //figure this out, need to index null set attributes?
                    break;
                case Mapper.typeCodeTinyInt:
                    byte indexValByte = (byte) indexVal;
                    table.writeByte(indexValByte);
                    break;
                case Mapper.typeCodeSmallInt:
                    short indexValShort = (short) indexVal;
                    table.writeShort(indexValShort);
                    break;
                case Mapper.typeCodeInt:
                    int indexValInt = (int) indexVal;
                    table.writeInt(indexValInt);
                    break;
                case Mapper.typeCodeBigInt:
                    long indexValBigInt = (long) indexVal;
                    table.writeLong(indexValBigInt);
                    break;
                case Mapper.typeCodeDouble:
                    long indexValDouble = (long) indexVal;
                    table.writeLong(indexValDouble);
                    break;
                case Mapper.typeCodeYear:
                    byte indexValYear = (byte) indexVal;
                    table.writeByte(indexValYear);
                    break;
                case Mapper.typeCodeTime:
                    int indexValTime = (int) indexVal;
                    table.writeInt(indexValTime);
                    break;
                case Mapper.typeCodeDateTime:
                    long indexValDateTime = (long) indexVal;
                    table.writeLong(indexValDateTime);
                    break;
                case Mapper.typeCodeDate:
                    long indexValDate = (long) indexVal;
                    table.writeLong(indexValDate);
                    break;
                case Mapper.typeCodeText:
                    String indexValStr = (String) indexVal;
                    table.writeBytes(indexValStr);
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

}
