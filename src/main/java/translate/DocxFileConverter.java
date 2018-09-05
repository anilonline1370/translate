package translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class DocxFileConverter {

	public void docxFileConverter(File file, List<Attribute> attributeList, List<Rule> ruleList, Map<String, List<Util>> utilMap) throws Exception {
		XWPFDocument docx = openDocxFile(file);
		if (docx != null) {
			List<XWPFTable> tables = docx.getTables();
			for(XWPFTable table : tables){
				XWPFTable tableFound = null;
				List<XWPFTableRow> rows =  table.getRows();
				
				// Add Atrribute List
				for(XWPFTableRow row : rows){
					if(row.getCell(0).getText().equalsIgnoreCase("Attribute Name") 
							&& row.getCell(1).getText().equalsIgnoreCase("Variable Name")
							&& row.getCell(2).getText().equalsIgnoreCase("Data Type")){
						tableFound = table;
						break;
					}
				}
				if(tableFound != null){
					XWPFTableRow oldRow = tableFound.getRow(1);
					for (Attribute attribute : attributeList) {
						CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
						XWPFTableRow newRow = new XWPFTableRow(ctrow, tableFound);
						newRow.getCell(0).setText(attribute.getName());
						newRow.getCell(1).setText(attribute.getVariableName());
						newRow.getCell(2).setText(attribute.getDataType());
						tableFound.addRow(newRow);
					}
					tableFound.removeRow(1);
					tableFound = null;
				}
				
				// Add Rule List
				for(XWPFTableRow row : rows){
					if(row.getCell(0).getText().equalsIgnoreCase("Rule Name") 
							&& row.getCell(1).getText().equalsIgnoreCase("Variable Name")
							&& row.getCell(2).getText().equalsIgnoreCase("Data Type")){
						tableFound = table;
					}
				}
				if(tableFound != null){
					XWPFTableRow oldRow = tableFound.getRow(1);
					for (Rule rule : ruleList) {
						CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
						XWPFTableRow newRow = new XWPFTableRow(ctrow, tableFound);
						newRow.getCell(0).setText(rule.getName());
						newRow.getCell(1).setText(rule.getVariableName());
						newRow.getCell(2).setText(rule.getRuleType());
						tableFound.addRow(newRow);
					}
					tableFound.removeRow(1);
					tableFound = null;
				}
				
				// Add Data Table List
	            
	            // Add Util Libraries map
				for(XWPFTableRow row : rows){
					if(row.getCell(0).getText().equalsIgnoreCase("Util Name") 
							&& row.getCell(1).getText().equalsIgnoreCase("Variable Name")
							&& row.getCell(2).getText().equalsIgnoreCase("Script Text")){
						tableFound = table;
						break;
					}
				}
				if(tableFound != null){
					XWPFTableRow oldRow = tableFound.getRow(1);
					for(String utilName : utilMap.keySet()){
			            for (Util util : utilMap.get(utilName)) {
			            	CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
							XWPFTableRow newRow = new XWPFTableRow(ctrow, tableFound);
							newRow.getCell(0).setText(util.getName());
							newRow.getCell(1).setText(util.getVariableName());
							newRow.getCell(2).setText(util.getScriptText());
							tableFound.addRow(newRow);
			            }
					}
					tableFound.removeRow(1);
					tableFound = null;
					break;
				}
			}
			
			saveDocxFile(docx, Constants.SOURCE_FILE.replace("input", "output"));
		}
	}

	private XWPFDocument openDocxFile(File file) throws Exception {
		XWPFDocument document = null;
		if (file != null && file.exists()) {
			document = new XWPFDocument(new FileInputStream(file));
		}
		return document;
	}

	private void saveDocxFile(XWPFDocument doc, String file) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			doc.write(out);
			doc.close();
			System.out.println("Successfully created new file at location : " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
