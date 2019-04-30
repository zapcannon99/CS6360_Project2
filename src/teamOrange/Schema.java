package teamOrange;

import java.io.File;
import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

import static teamOrange.Mapper.pageSize;

final public class Schema {
    static Scanner scanner;
    static FileWriter fileWriter;

    public static boolean isIndexed(String tableName, String columnName){
        try {
            scanner = new Scanner(new File("catalog/davisbase_columns"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        scanner.useDelimiter("[,\n]");
        int y=0;
        tableName = tableName.substring(0, tableName.length() - 4);
        Boolean isIndexed=false;
        while (scanner.hasNext()) {
            y = Integer.parseInt(scanner.next());//row_id
            String tabName=scanner.next(); //TableName
            String colName=scanner.next(); //colName

            scanner.next(); //DataType
            scanner.next(); //Ordinal
            scanner.next(); //IsNullable
            String isIndex=scanner.next(); //IsIndexed
            if(tableName.equals(tabName.trim()) && columnName.equals(colName.trim()))
            {
                if(isIndex.trim().equals("YES"))
                {
                    isIndexed=true;
                }
            }
        }
        return isIndexed;
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
        }
        return  true;
    }
    public static int test() {
        // Adding Columns
        // String columnName;
        // String dataType;
        // int ordinalPosition;
        // boolean isNullable;
        // boolean isIndexed;

        String[] StringArray = new String[20];
        StringArray[0] = "col1"; //CloummnName
        StringArray[1] = "TEXT"; //DataType
        StringArray[2] = "1";   // Ordinal Value
        StringArray[3] = "NO";  // Isnullable
        StringArray[4] = "NO";   // IsIndexed

        StringArray[5] = "col2";
        StringArray[6] = "TEXT";
        StringArray[7] = "2";
        StringArray[8] = "NO";
        StringArray[9] = "YES";

        StringArray[10] = "col3";
        StringArray[11] = "TEXT";
        StringArray[12] = "3";
        StringArray[13] = "NO";
        StringArray[14] = "NO";

        StringArray[15] = "col4";
        StringArray[16] = "TEXT";
        StringArray[17] = "4";
        StringArray[18] = "NO";
        StringArray[19] = "YES";

        String tableFileName = "testtest.tbl";
        Schema.insert(tableFileName, StringArray);

        System.out.println(Schema.isIndexed(tableFileName, "col1"));
        System.out.println(Schema.isIndexed(tableFileName, "col2"));
        System.out.println(Schema.isIndexed(tableFileName, "col3"));
        System.out.println(Schema.isIndexed(tableFileName, "col4"));

        int pageNo = 0;
        try{
            RandomAccessFile tableFile= new RandomAccessFile(tableFileName, "rw");
            tableFile.setLength((0+1) * pageSize);
            tableFile.seek((pageNo) * pageSize);
        } catch(Exception e){
            System.out.println("ERROR: " + e);
        }
        return 0;
    }
}
