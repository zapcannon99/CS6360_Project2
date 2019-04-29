package teamOrange;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

import static teamOrange.Mapper.pageSize;

public class Page {

    public static class Schema {
        static Scanner scanner;
        static FileWriter fileWriter;


        public static void pris(){
            System.out.println("This is pris");
        }
        public static boolean insert(String tableName,String[] columnArray)
        {
            System.out.println("Calling insert");
                File directory = new File("catalog");
                Boolean alreadyPresent = false;
                int row_id = -1;
                int x = 0;
                if (!directory.exists()) {
                    directory.mkdir();
                    System.out.println("Creating a directory");

                } else {
                    try {
                        scanner = new Scanner(new File("catalog/davisbase_tables"));
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Directory already exist");
                    scanner.useDelimiter("[,\n]");
                    tableName = tableName.substring(0, tableName.length() - 4);
                    //Getting the last row_id  x
                    while (scanner.hasNext()) {
                        x = Integer.parseInt(scanner.next());
                        String name = scanner.next();
                        System.out.println(name + "");
                        if (tableName.equals(name.trim())) {
                            alreadyPresent = true;
                            row_id = x;
                        }
                    }
                }
                //Gettting the next id by auto increment
                x++;
                if (!alreadyPresent)
                {
                    System.out.println(x);
                    System.out.println(tableName);
                    try {
                        fileWriter = new FileWriter("catalog/davisbase_tables", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    PrintWriter printWriter = new PrintWriter(bufferedWriter);
                    System.out.println("yes");
                    printWriter.println(x + "," + tableName);
                    System.out.println("yo");
                    printWriter.flush();
                    printWriter.close();


                    // Adding Columns

                    try {
                        scanner = new Scanner(new File("catalog/davisbase_columns"));
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    scanner.useDelimiter("[,\n]");
                    //Getting the last row_id  y
                    int y=0;
                    while (scanner.hasNext()) {
                        y = Integer.parseInt(scanner.next());//row_id
                        scanner.next(); //TableName
                        scanner.next(); //colName
                        scanner.next(); //DataType
                        scanner.next(); //Ordinal
                        scanner.next(); //IsNullable
                        scanner.next(); //IsIndexed
                    }

                    try {
                        fileWriter = new FileWriter("catalog/davisbase_columns", true);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                    bufferedWriter = new BufferedWriter(fileWriter);
                    printWriter = new PrintWriter(bufferedWriter);

                    for(int i=0;i<columnArray.length;i++)
                    {
                        if((i%5)==0)
                        {
                            y=y+1;
                            //ColumName
                            printWriter.print(y+","+tableName+","+columnArray[i]);
                        }
                        else if((i%5)==1 || (i%5)==3 )
                        {
                            //DateType
                            //IsNullable

                            printWriter.print(","+columnArray[i]);

                        }
                        else if ((i%5)==4)
                        {
                            printWriter.println(","+columnArray[i]);
                        }

                        else if((i%5)==2)
                        {
                            //ordinal Value
                            printWriter.print(","+Integer.parseInt(columnArray[i]));

                        }
                    }

                    printWriter.flush();
                    printWriter.close();




                    //Adding Columns




                }


            return  true;
        }
    }


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
            //   String columnName;
            //        String dataType;
            //        int ordinalPosition;
            //        boolean isNullable;
            //        boolean isIndexed;

            String[] StringArray=new String[20];
            StringArray[0]="col1"; //CloummnName
            StringArray[1]="TEXT"; //DataType
            StringArray[2]="1";   // Ordinal Value
            StringArray[3]="NO";  // Isnullable
            StringArray[4]="NO";   // IsIndexed

            StringArray[5]="col2";
            StringArray[6]="TEXT";
            StringArray[7]="2";
            StringArray[8]="NO";
            StringArray[9]="NO";

            StringArray[10]="col3";
            StringArray[11]="TEXT";
            StringArray[12]="3";
            StringArray[13]="NO";
            StringArray[14]="NO";

            StringArray[15]="col4";
            StringArray[16]="TEXT";
            StringArray[17]="4";
            StringArray[18]="NO";
            StringArray[19]="NO";

            Schema.insert(tableFileName,StringArray);

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
