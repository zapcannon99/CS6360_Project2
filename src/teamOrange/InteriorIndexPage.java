package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import static java.lang.System.out;
import static teamOrange.Mapper.*;

public class InteriorIndexPage extends Page{

    public InteriorIndexPage(){
        super();
        typeOfPage = interiorIndexBTreePage;
    }

    /*public InteriorIndexPage(String tableFileName, int pageNo){
        super(tableFileName, pageNo, interiorIndexBTreePage);

        // Grab the file with table file name
        int pos = 2;
        try{
            tableFile.seek(pos);
            noOfCells = tableFile.readShort();

        } catch(Exception e){
            System.out.println(e.toString());
        }
    }*/

    public InteriorIndexPage(RandomAccessFile table, int pageOffset){
        super();
        tableFile = table;
        this.pageOffset = pageOffset;
        // When using this constructor, we assume that the first byte has been read already
        typeOfPage = interiorTableBTreePage;
        try{
            noOfCells = tableFile.readShort();
            startOfCellContent = tableFile.readShort();
            rightPageNo = tableFile.readInt();
            for(int k = 0; k < noOfCells; k++){
                cellOffsets.add(table.readShort());
            }

            // Then proceed to read the cells in an interior page
            tableFile.seek(pageOffset + startOfCellContent); //get rid pageOffset
            for(int k = 0; k < noOfCells; k++){
                InteriorIndexCell interiorIndexCell = new InteriorIndexCell(tableFile, (pageOffset + cellOffsets.get(k)));
                cells.add(interiorIndexCell);
            }

        } catch(Exception e){
            out.println(e.toString());
        }
    }
}
