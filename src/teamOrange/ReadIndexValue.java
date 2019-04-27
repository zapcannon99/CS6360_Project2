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

    public void WriteUnknown(RandomAccessFile table, ArrayList indexVal, byte dataType){
        try{
            switch(dataType){
                case Mapper.typeCodeNull:
                    //figure this out, need to index null set attributes?
                    break;
                case Mapper.typeCodeTinyInt:
                    byte indexValByte = (byte) indexVal.get(0);
                    table.writeByte(indexValByte);
                    break;
                case Mapper.typeCodeSmallInt:
                    short indexValShort = (short) indexVal.get(0);
                    table.writeShort(indexValShort);
                    break;
                case Mapper.typeCodeInt:
                    int indexValInt = (int) indexVal.get(0);
                    table.writeInt(indexValInt);
                    break;
                case Mapper.typeCodeBigInt:
                    long indexValBigInt = (long) indexVal.get(0);
                    table.writeLong(indexValBigInt);
                    break;
                case Mapper.typeCodeDouble:
                    long indexValDouble = (long) indexVal.get(0);
                    table.writeLong(indexValDouble);
                    break;
                case Mapper.typeCodeYear:
                    byte indexValYear = (byte) indexVal.get(0);
                    table.writeByte(indexValYear);
                    break;
                case Mapper.typeCodeTime:
                    int indexValTime = (int) indexVal.get(0);
                    table.writeInt(indexValTime);
                    break;
                case Mapper.typeCodeDateTime:
                    long indexValDateTime = (long) indexVal.get(0);
                    table.writeLong(indexValDateTime);
                    break;
                case Mapper.typeCodeDate:
                    long indexValDate = (long) indexVal.get(0);
                    table.writeLong(indexValDate);
                    break;
                case Mapper.typeCodeText:
                    String indexValStr = (String) indexVal.get(0);
                    table.writeBytes(indexValStr);
                    break;
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

}
