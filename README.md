# CS6360_Project2
Very rudimentary database file and indexing program.

INDEX TREE

SELECT  
Object QueryCellIndeces(String tableName, LeafTableCell leafTableCell)  
returns queried leaf or interior index cell

INSERT  
boolean InsertCellIndeces(String tableName, LeafTableCell leafTableCell)  
returns true if successful insert  
(meaning indexVal doesn't exist yet, and new indexVal was inserted)

DELETE  
boolean DeleteCellIndeces(String tableName, LeafTableCell leafTableCell)  
returns true if successful delete

UPDATE  
boolean UpdateCellIndeces(String tableName, LeafTableCell leafTableCell,Object oldIndexVal)  
returns true if successful update
(meaning the oldIndexVal was found, deleted, and the newIndexVal was inserted/updated)  
