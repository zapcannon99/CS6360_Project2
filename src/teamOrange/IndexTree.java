package teamOrange;

public class IndexTree {

    public void Search(String tableName){
        Page page = Page.getPage(tableName,0); //root = 0
        Cell interiorIndexCell;
        Cell leafIndexCell;
        if(page.typeOfPage == Mapper.interiorIndexBTreePage)
            interiorIndexCell = (Cell) page.cells.get(0);
        else
            leafIndexCell = (Cell) page.cells.get(0);
    }
}
