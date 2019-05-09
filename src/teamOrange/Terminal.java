package teamOrange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import static java.lang.System.out;

public class Terminal {
	public Terminal() {
	}
	
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	static boolean isExit=false;
	
	public static void parseUserCommand(String userCommand) {
		List<String> commandList = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));
		switch (commandList.get(0)) {
		case "show":
			if(commandList.get(1).equals("tables")) {
				System.out.println("CASE: SHOW TABLES");
				showTables();
			}
			else 
				System.out.println("I didn't understand the command");			
			break;
		case "create":
			if(commandList.get(1).equals("table")) {
				System.out.println("CASE: CREATE TABLE");
				createTable(commandList);
			}
			else if(commandList.get(1).equals("index")){
				System.out.println("CASE: CREATE INDEX");
				createIndex(commandList);
			}
			else 
				System.out.println("I didn't understand the command");	
			break;
		case "drop":
			if(commandList.get(1).equals("table") && commandList.size()==3) {
				out.println("CASE: DROP");
				dropTable(commandList.get(2));
			}
			else 
				out.println("I didn't understand the command");
			break;
		case "insert":
			out.println("CASE: INSERT");
			insertInto(commandList);
			break;
		case "delete":
			out.println("CASE: DELETE");
			deleteFrom(commandList);
			break;
		case "update":
			out.println("CASE: UPDATE");
			UpdateRecord(commandList);
			break;
		case "select":
			out.println("CASE: SELECT");
			parseQuery(commandList);
			break;	
		case "exit":
			isExit = true;
			break;
		default:
			System.out.println("I didn't understand the command");
			break;
	}
	}
	
	private static void UpdateRecord(List<String> commandList) {
		
		int index_set = commandList.indexOf("set");
		int index_where = commandList.indexOf("where");

		if ((index_where == -1) || (index_set == -1)) {
			out.println("I didn't understand the query");
			return;
		}
		
		String tname = commandList.get(1);
		ArrayList<String> set_cols = new ArrayList<String>();
		ArrayList<String> set_vals = new ArrayList<String>();
		
		List<String> condition = commandList.subList(index_where + 1, commandList.size());
		List<String> set = commandList.subList(index_set + 1, index_where);
		

		// Add all columns and values
		ArrayList<String> buffer = new ArrayList<String>();
		
		for (int i = 0; i < set.size(); i++) {
			buffer.addAll(Arrays.asList(set.get(i).split(",|=")));	
		}
		
		buffer.removeAll(Arrays.asList(""));
	
		int i = 0;
		while (i + 1 < buffer.size()) {
			set_cols.add(buffer.get(i));
			i++;
			set_vals.add(buffer.get(i));
			i++;
		}
		
		out.println(tname);
		out.println(set_cols);
		out.println(set_vals);
		out.println(condition);
		updateHelper(tname, set_cols, set_vals,condition);
		
	}

	private static void updateHelper(String tablename, ArrayList<String> set_cols, ArrayList<String> set_vals,
			List<String> condition) {
		// TODO Auto-generated method stub
		
	}

	private static void deleteFrom(List<String> commandList) {
		if (!commandList.get(1).equals("from") || !commandList.get(2).equals("table")
				|| !commandList.get(4).equals("where")) {
			out.println("I didn't understand the command");
			return;
		}
		
		List<String> condition = commandList.subList(5, commandList.size());

		deleteHelper(commandList.get(3),condition);
		
	}

	private static void deleteHelper(String tablename, List<String> condition) {
		// TODO Auto-generated method stub
		
	}

	private static void insertInto(List<String> commandList) {
		ArrayList<String> col_values = new ArrayList<String>();
		ArrayList<String> col_names = new ArrayList<String>();
		
		int index_values = commandList.indexOf("values");

		if ((index_values == -1) || !commandList.get(1).equals("into") || !commandList.get(2).equals("table")) {
			out.println("I didn't understand the query");
			return;
		}

		String tname ;
		List<String> cols = commandList.subList(3, index_values);
		List<String> vals = commandList.subList(index_values + 1, commandList.size());

		// Add all columns name to col_names
		for (int i = 0; i < cols.size(); i++) {
			col_names.addAll(Arrays.asList(cols.get(i).split(",|\\(|\\)")));	
		}
		
		tname = col_names.get(0);
		col_names.remove(0);
		
		for (int i = 0; i < vals.size(); i++) {
			col_values.addAll(Arrays.asList(vals.get(i).split(",|\\(|\\)")));	
		}
		
		col_names.removeAll(Arrays.asList(""));
		col_values.removeAll(Arrays.asList(""));

		out.println(tname);
		out.println(col_names);
		out.println(col_values);
		
		insertHelper(tname, col_names, col_values);
		return;
		
	}

	private static void insertHelper(String tablename, ArrayList<String> col_names, ArrayList<String> col_values) {
		// TODO Auto-generated method stub
		
	}

	private static void dropTable(String tablename) {
		// TODO Auto-generated method stub
		
	}

	private static void createIndex(List<String> commandList) {
		// TODO Auto-generated method stub
		String tableName = commandList.get(3);
		//get all column names
		ArrayList<String> indexList = new ArrayList<String>();
		String indexColumnName = "indexColumnName";
		While(indexColumnName.compareTo("")!=0){
			indexColumnName = parse(commandList.get(4));
			indexList.add(indexColumnName);
		}
		//create Catalog object
		File catalogFile = new File("c:/DavisBase/catalog.txt");
		Catalog catalog = new Catalog(catalogFile);
		String tableIndex = tableName + "Index.txt";
		File tableIndexFile = new File("c:/DavisBase/"+tableIndex);
		//get record count of table
		Catalog TableInfo = catalog.getTableInfo(tableName);
		int numRowids = TableInfo.recordCount;
		ArrayList indexVals = new ArrayList();
		TableTree tableTree = new TableTree();
		LeafTableCell record = null;
		if(tableIndexFile.exists()){
			System.out.println("Index File already exists");
		}
		//if there are already records inserted in the database, but not in the indexFile
		//add all record to indexFile
		else if(numRowids!=0) {
			for(int i=1; i<=numRowids; i++) {
				record = (LeafTableCell) tableTree.search(i);
				IndexTree indexTree = new IndexTree();
				indexTree.InsertCellIndeces(tableName,record);
			}
		}
		//if there are no records inserted into the database yet
		//just create a indexFile
		else if(numRowids==0){
			tableIndexFile.createNewFile();
		}
	}

	private static void createTable(List<String> commandList) {
		
	}

	private static void showTables() {
		List<String> tmp = new ArrayList<>();
		tmp.add("table_name");
		queryHelper(tmp,"davisbase_tables",new ArrayList<String>());
	}

	private static void parseQuery(List<String> commandList) {
		int index_where = commandList.indexOf("where");
		int index_from = commandList.indexOf("from");

		if ((index_from == -1) || (index_where - index_from) != 2) {
			System.out.println("I didn't understand the query");
			return;
		}
		String table_name = commandList.get(index_from+1);
		List<String> condition = commandList.subList(index_where + 1, commandList.size());
		List<String> cols = commandList.subList(1, index_from);
		System.out.println("table_name: "+table_name);
		System.out.println("cols: "+cols);
		System.out.println("condition: "+condition);
		System.out.println("condition length: "+condition.size());
		
		queryHelper(cols,table_name,condition);
	}

	private static void queryHelper(List<String> cols, String table_name, List<String> condition) {
		// TODO Auto-generated method stub
		//find rowId of record with IndexTree and then get the record from the TableTree
		IndexTree indexTree = new IndexTree();
		TableTree tableTree = new TableTree();
		ArrayList records = new ArrayList();
		for(int i=0;i<cols.size();i++) {
			int rowId = indexTree.QueryCellIndeces(table_name, cols.get(i));
			LeafTableCell record = (LeafTableCell) tableTree.search(rowId);
			System.out.println(record);
			records.add(record);
		}
	}

	public static void main(String[] args) {
		String userCommand="";
		while(!isExit) {
			userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
	}

}
