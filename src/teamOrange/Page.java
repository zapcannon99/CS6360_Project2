package teamOrange;

import java.io.File;
import java.io.RandomAccessFile;
import static teamOrange.Mapper.pageSize;

public class Page {
    byte noOfCells=0;
    byte typeOfPage;
    byte startOfCellContent;
    int pagePointer;
    int array;
    int pageNo;
    String path="data/tables/";
    RandomAccessFile tableFile;

    Page(String tableFileName,int pageNo,byte typeOfPage)
    {
        try{
            File directory = new File("data");
            if (! directory.exists()){
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
                File subDirectory = new File("data/tables");
                if (! subDirectory.exists()){
                    subDirectory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }
            }
            else
            {
                File subDirectory = new File("data/tables");
                if (! subDirectory.exists()){
                    subDirectory.mkdir();
                    // If you require it to make the entire directory path including parents,
                    // use directory.mkdirs(); here instead.
                }

            }

            tableFile = new RandomAccessFile(path+tableFileName, "rw");
            tableFile.setLength((pageNo)*pageSize);
            tableFile.seek((pageNo-1)*pageSize);
            tableFile.writeByte(typeOfPage);
            this.pageNo=pageNo;




//            tableFile.writeBytes("Hello");
//            tableFile.writeFloat((float)3.14);
//            tableFile.writeInt(45);
//            tableFile.writeByte(0xff);
//            tableFile.seek(0);
//            byte[] b=new byte[10];
//            tableFile.read(b,0,5);
//            System.out.println(new String(b));
//            //tableFile.seek(5);
//            System.out.println(tableFile.readFloat());
//            System.out.println(tableFile.readInt());
//            System.out.println(Integer.toHexString(tableFile.read()));
//            tableFile.seek(0);


        }
        catch (Exception e)
        {

        }
    }

    int readByteAt(int offset){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
        }
        else
        {
            try{
                tableFile.seek(offset);
                return tableFile.read();
            }
            catch (Exception e)
            {
                System.out.println("Exception Occcured while reading the byte");
            }
        }
        return 0;
    }

    int readIntAt(int offset){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
        }
        else
        {
            try{
                tableFile.seek(offset);
                return tableFile.readInt();
            }
            catch (Exception e)
            {
                System.out.println("Exception Occcured while reading the byte");
            }
        }
        return 0;
    }

    float readFloatAt(int offset){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
        }
        else
        {
            try{
                tableFile.seek(offset);
                return tableFile.readFloat();
            }
            catch (Exception e)
            {
                System.out.println("Exception Occcured while reading the byte");
            }
        }
        return 0;
    }

    String readStringAt(int offset,int length){
        if(offset>=pageSize)
        {

            System.out.println("Offset More than the Page Size");
        }
        else
        {
            try{
                tableFile.seek(offset);
                byte[] bytesArray=new byte[length];
                tableFile.read(bytesArray,0,length);
                return new String(bytesArray);
            }
            catch (Exception e)
            {
                System.out.println("Exception Occcured while reading the byte");
            }
        }
        return "";
    }


    boolean writeByteAt(int offset,byte byteToBeWritten){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
            return false;
        }
        else
        {
            try{
                tableFile.seek(offset);
                tableFile.writeByte(byteToBeWritten);
            }
            catch (Exception e) {
                System.out.println("Exception Occcured while write the byte");
                return false;
            }
        }
        return true;
    }

    boolean writeIntAt(int offset,int intToBeWritten){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
            return false;
        }
        else
        {
            try{
                tableFile.seek(offset);
                tableFile.writeInt(intToBeWritten);
            }
            catch (Exception e) {
                System.out.println("Exception Occcured while write the byte");
                return false;
            }
        }
        return true;
    }

    boolean writeFloatAt(int offset,float floatToBeWritten){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
            return false;
        }
        else
        {
            try{
                tableFile.seek(offset);
                tableFile.writeFloat(floatToBeWritten);
            }
            catch (Exception e) {
                System.out.println("Exception Occcured while write the byte");
                return false;
            }
        }
        return true;
    }

    boolean writeStringAt(int offset,String StringToBeWritten){
        if(offset>=pageSize)
        {
            System.out.println("Offset More than the Page Size");
            return false;
        }
        else
        {
            try{
                tableFile.seek(offset);
                tableFile.writeBytes(StringToBeWritten);
            }
            catch (Exception e) {
                System.out.println("Exception Occcured while write the byte");
                return false;
            }
        }
        return true;
    }


}
