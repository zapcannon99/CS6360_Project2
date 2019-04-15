# CS6360_Project2
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

## Mapper CLass
This class stores all the constants, which can be used throughout any java classes by 
```
import static teamOrange.Mapper.*;
```
