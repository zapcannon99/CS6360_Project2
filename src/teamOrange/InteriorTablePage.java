package teamOrange;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import static java.lang.System.out;
import static teamOrange.Mapper.*;

public class InteriorTablePage extends Page {

    public InteriorTablePage(){
        super();
        typeOfPage = interiorTableBTreePage;
    }

    public InteriorTablePage(String tableFileName, int pageNo){
        super(tableFileName, pageNo, interiorTableBTreePage);

        // Grab the file with table file name
        int pos = 2;
        try{
            tableFile.seek(pos);
            noOfCells = tableFile.readShort();

        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public InteriorTablePage(RandomAccessFile table, int pageOffset){
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
            tableFile.seek(pageOffset + startOfCellContent);
            for(int k = 0; k < noOfCells; k++){
                readCell();
            }

        } catch(Exception e){
            out.println(e.toString());
        }
    }

    public ArrayList<DataElement> readCell(){

    }
}