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

    public InteriorTablePage(RandomAccessFile table, int pageNo){
        super();
        tableFile = table;
        this.pageOffset = pageNo * pageSize;
        // When using this constructor, we assume that the first byte has been read already
        typeOfPage = interiorTableBTreePage;
        try{
            table.seek(pageOffset);
            noOfCells = tableFile.readShort();
            startOfCellContent = tableFile.readShort();
            rightPageNo = tableFile.readInt();
            for(int k = 0; k < noOfCells; k++){
                cellOffsets.add(table.readShort());
            }

            // Then proceed to read the cells in an interior page
            tableFile.seek(pageOffset + startOfCellContent);
            for(int k = 0; k < noOfCells; k++){
                //readCell();
                short offset = cellOffsets.get(k);
                InteriorTableCell cell = InteriorTableCell.read(table, pageOffset + offset);
            }

        } catch(Exception e){
            out.println(e.toString());
        }
    }

    public static InteriorTablePage getPage(RandomAccessFile table, int pageNo){
        return new InteriorTablePage(table, pageNo);

    }

    public void addCell(int leftPage, int rowid){
        InteriorTableCell cell = InteriorTableCell.createCell(leftPage, rowid);
        if(remainingBytes() < cell.totalSize()){
            startOfCellContent -= 8;
            cellOffsets.add(startOfCellContent);
            cells.add(0, cell);
        }
        noOfCells++;
    }

    public void write(){
        try{
            tableFile.seek(pageOffset);

            // header info
            tableFile.writeByte(typeOfPage);
            tableFile.writeShort(noOfCells);
            tableFile.writeShort(startOfCellContent);
            tableFile.writeInt(rightPageNo);

            // write offsets to cells
            for(Short offset : cellOffsets){
                tableFile.writeShort(offset);
            }

            tableFile.seek(pageOffset + startOfCellContent);
            for(int k = cellOffsets.size() - 1; k >= 0; k--){
                LeafTableCell cell = (LeafTableCell)cells.get(k);
                cell.write(tableFile);
            }
        } catch(Exception e){
            System.out.println("ERROR: cannot write: " + e);
        }
    }
}