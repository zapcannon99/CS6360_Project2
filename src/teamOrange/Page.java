package teamOrange;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import static java.lang.System.out;
import static teamOrange.Mapper.*;
import static teamOrange.Schema.*;

public class Page {
    // Starting positions of certain headers
    public static final short noOfCellOffset = 0x01;
    public static final short startOfCellContentOffset = 0x03;
    public static final short rightPageNoOffset = 0x05;
    public static final short cellOffsetsOffset = 0x09;

    public static final String path = "data/tables/";

    byte typeOfPage;
    short noOfCells = 0;
    short startOfCellContent = pageSize;
    int rightPageNo = -1;           // -1 means no page right?
    ArrayList<Short> cellOffsets;   // This is the arraylist of the offsets pointing to the cells
    ArrayList<Cell> cells;          // The idea is that this arraylist, when being read in, will be in the same order as cellOffsets
    int pageNo;
    int parentPageNo;
    int pageOffset;                 // Where the page starts in the file
    RandomAccessFile tableFile;

    Page(){}

    Page(String tableFileName,int pageNo,byte typeOfPage) {
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

            // Evil Joel is indexing starting 0, He changed it cuz files systems start indexing at 0 too
            tableFile = new RandomAccessFile(path+tableFileName, "rw");
            pageOffset = getPageOffset(pageNo);
            if(tableFile.length() > pageOffset + pageSize)
                tableFile.setLength((pageNo + 1)*pageSize);
            tableFile.seek(pageOffset);

            tableFile.writeByte(typeOfPage);
            tableFile.seek(tableFile.getFilePointer() + 2); // skip 2 bytes to write the current Cell Start
            // If we are making a brand new page, this is something that needs to be handled by getting metadata
            this.pageNo=pageNo;
        }
        catch (Exception e){
            System.out.println("ERROR: Problem creating page");
        }
    }

    public int remainingBytes(){
        return startOfCellContent-(9 + cellOffsets.size()*2);
    }

    public void setPageNo(int newIndex){
        pageNo = newIndex;
        pageOffset = pageNo * pageSize;
    }

    /**
     * Gets a page from specified tableFileName and page no. Uses the pages 1st byte to get the
     * proper subclass to return
     * @param table RandomAccessFile
     * @param pageNo
     * @return a subclass of Page
     */
    public static Page getPage(RandomAccessFile table, int pageNo){
        int pageOffset = getPageOffset(pageNo);
        Page page = null;
        try{
            table.seek(pageOffset);
            Byte pagetype = table.readByte();
            switch(pagetype){
                case interiorIndexBTreePage:
                    // Nancy's stuff goes here
                    page = new InteriorIndexPage(table, pageOffset);
                    break;
                case interiorTableBTreePage:
                    page = new InteriorTablePage(table, pageOffset);
                case leafIndexBTreePage:
                    // Nancy's stuff goes here
                    page = new LeafIndexPage(table, pageOffset);
                    break;
                case leafTableBTreePage:
                    break;
                default:
                    throw new Exception("ERROR: Page type not recognized in the page header.");
            }
        } catch(Exception e){
            out.println(e.toString());
            return null;
        }
        return page;
    }

    /**
     * Gets a page from specified tableFileName string and page no. Uses the pages 1st byte to get the
     * proper subclass to return
     *
     * WARNING: because we create a new RandomAccessFile object, we may run into a problem such as the seek location
     * not being where we expect it to be
     * @param tableFileName
     * @param pageNo
     * @return a subclass of Page
     */
    public static Page getPage(String tableFileName, int pageNo){
        int pageOffset = getPageOffset(pageNo);
        Page page = null;
        try{
            RandomAccessFile table = new RandomAccessFile(path + tableFileName, "rw");
            table.seek(pageOffset);
            Byte pagetype = table.readByte();
            switch(pagetype){
                case interiorIndexBTreePage:
                    // Nancy's stuff goes here
                    page = new InteriorIndexPage(table, pageOffset);
                    break;
                case interiorTableBTreePage:
                    page = new InteriorTablePage(table, pageOffset);
                case leafIndexBTreePage:
                    // Nancy's stuff goes here
                    page = new LeafIndexPage(table, pageOffset);
                    break;
                case leafTableBTreePage:
                    break;
                default:
                    throw new Exception("ERROR: Page type not recognized in the page header.");
            }
        } catch(Exception e){
            out.println(e.toString());
            return null;
        }
        return page;
    }

    /**
     * Just returns the page offset
     * @param pageNo page number indexing from 0
     * @return
     */
    public static int getPageOffset(int pageNo){
        return pageNo * pageSize;
    }

    public void incNoOfCells(){
        noOfCells++;
    }

    public void decNoOfCells(){
        noOfCells--;
    }

    public void updateStartOfCellContent(short offset){
        startOfCellContent = offset;
    }

    /**
     * Removes the element with the offset from the arrayList
     * @param index
     */
    public void remove(int index){
        noOfCells--;
        cellOffsets.remove(index);
        cells.remove(index);
    }

    public void addCell(Cell cell){
        int cell_size = 0;
        switch(cell.typeOfCell){
            case interiorIndexBTreePage:
                // Nancy's stuff goes here
                //cell = new InteriorIndexCell(table);
                break;
            case interiorTableBTreePage:
                cell_size = ((InteriorTableCell)cell).totalSize();
                break;
            case leafIndexBTreePage:
                // Nancy's stuff goes here
                //cell = new LeafIndexCell(table);
                break;
            case leafTableBTreePage:
                cell_size = ((LeafTableCell)cell).totalSize();
                break;
            default:
                System.out.println("ERROR: Page type not recognized in the page header.");
        }
        int startAt = startOfCellContent - cell_size;
        cellOffsets.add((short)startAt);
        cells.add(cell);
        noOfCells++;
        startOfCellContent = (short)startAt;
    }

    public void write(){
        switch(typeOfPage){
            case leafTableBTreePage:
                ((LeafTablePage)this).write();
                break;
            case interiorTableBTreePage:
                ((InteriorTablePage)this).write();
                break;
        }
    }

    public boolean SplitLimit(ArrayList<Cell> cells, ArrayList<Short> cellOffsets){
        int bytesUsed = cells.size() + cellOffsets.size() + 9;
        int bytesUsedPercent = bytesUsed / pageSize;
        if(bytesUsedPercent >= 0.9)
            return true;
        else return false;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Other Auxillary Functions
    //------------------------------------------------------------------------------------------------------------------
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