# CS6360_Project2
## Schema

Schema is a Static Class inside Page class, Since, java doesn't allow standalone static classes.
Schema basically just has two functions: (More can be created as neeed arises)
1. insert(String tableName,Stringp[] columnData])
2. isIndexed(String tableFileName,String columnName)

### insert
It is currently called in the parameterized constructor of Page.
It is used to insert the table data in two files namely "davisbase_tables" and "davisbase_columns", which will be created inside a folder named catalog, which would be sibling to our data folder (Where tables are stored).
Moreover, if the tableName is already inserted than it won't make any changes to any of the files.
Inside the page constructor, we use Schema.insert as follows:
```
  String[] StringArray=new String[10];
            StringArray[0]="col1"; //CloummnName
            StringArray[1]="TEXT"; //DataType
            StringArray[2]="1";   // Ordinal Value
            StringArray[3]="NO";  // Isnullable
            StringArray[4]="NO";   // IsIndexed

            StringArray[5]="col2";
            StringArray[6]="TEXT";
            StringArray[7]="2";
            StringArray[8]="NO";
            StringArray[9]="YES";
            
            Schema.insert(tableFileName,StringArray);
```

### isIndexed
isIndexed would return true if column in the table specified is Indexed, else it would return false
It would look for "davisbase_columns" and search for the column.
Inside the page constructor, we can use Schema.isIndexed as follows:

```
System.out.println(Schema.isIndexed(tableFileName,"col1"));
            System.out.println(Schema.isIndexed(tableFileName,"col2"));
            System.out.println(Schema.isIndexed(tableFileName,"col3"));
            System.out.println(Schema.isIndexed(tableFileName,"col4"));
```

Note: tableFileName does contains ".tbl" extension at the end.

## Page

In my implementation a Page.java is a class. Every time we need a page we can create or call the previously created object of this class.
Each page acts has an object of Page.java class. 

### Creating A New Page (Page Object)
```
   Page Page1=new Page(tableFileName,1,interiorIndexBTreePage);
```
There are 3 mandatory Parameters which are required while creating an instance of page,
They are:
1) tableFileName (String): Name of the table along with the extension ".tbl"
2) pageNo (int): Page Number
3) typeofPage (int): Type of the page, interior/leaf/index/table page.

This will create a file under data/tables/ directly after you type following as an input while the program is running.
``` 
create table tableName;
```
tableName can be replaced by any name you want to give to table without the ".tbl" extension.

A detailed usage of various functions supported by the page class can be found in Terminal.java under parseCreateTable function 
```
Page Page1=new Page(tableFileName,1,interiorIndexBTreePage);
            Page1.writeByteAt(5, (byte) 0xFC);
            Page1.writeIntAt(7,43);
            Page1.writeFloatAt(12, (float) 3.14);
            Page1.writeStringAt(20,"Hello");

            System.out.println(Integer.toHexString(Page1.readByteAt(5)));
            System.out.println(Page1.readIntAt(7));
            System.out.println(Page1.readFloatAt(12));
            System.out.println(Page1.readStringAt(20,5));
```

### Functions for Reading and Writing the Page
Reading :
* boolean writeByteAt(int offset, byte byteToBeWritten) : To write a byte to a page.
* boolean writeIntAt(int offset, int intToBeWritten) : To write an int to a page.
* boolean writeFloatAt(int offset, float floatToBeWritten) : To write a float to a page.
* boolean writeStringAt(int offset, Sring StringToBeWritten) : To write a String to a page.

Writing :
* int readByteAt(int offset) : To read a byte from a page (Must convert it to string if you want to display it in Hex Format by Using Integer.toHexString(Page1.readByteAt(int offset)) )
* int readIntAt(int offset) : To read a int from a page
* float readFloatAt(int offset) : To read a Float from a page
* int readStringAt(int offset) : To read a String from a page

## Mapper Class
This class stores all the constants, which can be used throughout any java classes by 
```
import static teamOrange.Mapper.*;
```
