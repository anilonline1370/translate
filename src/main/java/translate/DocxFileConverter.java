package translate;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
//import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

import translate.commerce.ApprovalSequence;
import translate.commerce.CommerceAction;
import translate.commerce.CommerceComponents;
import translate.commerce.PrinterDocument;

public class DocxFileConverter {
	XWPFRun heading1Run = null;
	XWPFRun heading2Run = null;
	XWPFRun heading3Run = null;

	public void docxFileConverter(File file, List<UserStories> userStories, List<CommerceComponents> commerceComponents, List<DataTable> dataTableList , List<Users> usersList , List<Groups> groupList) throws Exception {
		File outputFile = new File("files/output/Sample TDD_temp.docx");
		FileOutputStream fos = new FileOutputStream(outputFile);
		/*XWPFDocument docx = new XWPFDocument(new FileInputStream("files/input/template.docx"));
		for (XWPFParagraph p : docx.getParagraphs()) {
		    List<XWPFRun> runs = p.getRuns();
		    if (runs != null) {
		        for (XWPFRun r : runs) {
		            String text = r.getText(0);
		            if (text != null && text.contains("Heading1")) {
		            	heading1Run = r;
		            	p.removeRun(r.getTextPosition());
		            }
		            if (text != null && text.contains("Heading2")) {
		            	heading2Run = r;
		            	p.removeRun(r.getTextPosition());
		            }
		            if (text != null && text.contains("Heading3")) {
		            	heading3Run = r;
		            	p.removeRun(r.getTextPosition());
		            }
		        }
		    }
		}*/
		XWPFDocument docx = new XWPFDocument();
		if (docx != null) {
			int storyNum = 1;
				for(UserStories stories : userStories){
					int storySubNum = 1;
					
					addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "  " + stories.getUserStoryNum());
					
					List<Attribute> attributeList= stories.getAttributeList();
					if(attributeList != null && attributeList.size() > 0){
						
						addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum++ + "  " + "Configuration Attributes");
						
//						XWPFParagraph paragraph = docx.createParagraph();
//						XWPFRun run = paragraph.createRun();
//						run.setBold(true);
//						run.setColor("800000");
//						run.setUnderline(UnderlinePatterns.SINGLE);
//						run.setText(storyNum + "." + storySubNum++ + "  " + "Configuration Attributes");
						//run.setText("Configuration Attributes");

						int numOfColumns = 4;
						XWPFTable utilTable = docx.createTable(attributeList.size()+1,numOfColumns);

						XWPFTableRow headerRow = utilTable.getRow(0);
						
						String[] headerNames = {"Attribute Name", "Variable Name", "Description", "Data Type"};
						
						addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);
						
//						headerRow.getCell(0).setColor("4bacc6");
//						headerRow.getCell(1).setColor("4bacc6");
//						headerRow.getCell(2).setColor("4bacc6");
//						headerRow.getCell(3).setColor("4bacc6");
//						headerRow.getCell(0).setText("Attribute Name");
//						headerRow.getCell(1).setText("Variable Name");
//						headerRow.getCell(2).setText("Description");
//						headerRow.getCell(3).setText("Data Type");
						setTableSize(utilTable, 1600, 2500, 4100, 1000);

						int count = 1;
						for(Attribute attribute : attributeList){
							if(attribute != null){
								XWPFTableRow newRow = utilTable.getRow(count++);
								newRow.getCell(0).setText(attribute.getName());
								newRow.getCell(1).setText(attribute.getVariableName());
								newRow.getCell(2).setText(attribute.getDescription());
								newRow.getCell(3).setText(Constants.getDataTypes().get(attribute.getDataType()));
							}
						}
					}

					docx.createParagraph();
					
					List<Rule> ruleList = stories.getRuleList();
					if(ruleList != null && ruleList.size() > 0){
						
						List<Rule> recommendationRuleList = new ArrayList<Rule>();
						List<Rule> constraintRuleList = new ArrayList<Rule>();
						List<Rule> hidingRuleList = new ArrayList<Rule>();
						List<Rule> recommendationItemRuleList = new ArrayList<Rule>();
						List<Rule> configurationRuleList = new ArrayList<Rule>();
						List<Rule> pricingRuleList = new ArrayList<Rule>();
						for(Rule rule : ruleList){
							if(rule.getRuleType().equalsIgnoreCase("1")){
								recommendationRuleList.add(rule);
							}else if(rule.getRuleType().equalsIgnoreCase("2")){
								constraintRuleList.add(rule);
							}else if(rule.getRuleType().equalsIgnoreCase("11")){
								hidingRuleList.add(rule);
							}else if(rule.getRuleType().equalsIgnoreCase("9")){
								recommendationItemRuleList.add(rule);
							}else if(rule.getRuleType().equalsIgnoreCase("6")){
								configurationRuleList.add(rule);
							}else if(rule.getRuleType().equalsIgnoreCase("4")){
								pricingRuleList.add(rule);
							}
						}

						if(recommendationRuleList != null && recommendationRuleList.size() > 0){
							createRuleTable(docx, recommendationRuleList, "Recommendation Rule", storyNum, storySubNum++);
						}
						if(constraintRuleList != null && constraintRuleList.size() > 0){
							createRuleTable(docx, constraintRuleList, "Constraint Rule", storyNum, storySubNum++);
						}
						if(hidingRuleList != null && hidingRuleList.size() > 0){
							createRuleTable(docx, hidingRuleList, "Hiding Rule", storyNum, storySubNum++);
						}
						if(recommendationItemRuleList != null && recommendationItemRuleList.size() > 0){
							createRuleTable(docx, recommendationItemRuleList, "Recommended Items rule", storyNum, storySubNum++);
						}
						if(configurationRuleList != null && configurationRuleList.size() > 0){
							createRuleTable(docx, configurationRuleList, "Configuration Flow rule", storyNum, storySubNum++);
						}
						if(pricingRuleList != null && pricingRuleList.size() > 0){
							createRuleTable(docx, pricingRuleList, "Pricing rule", storyNum, storySubNum++);
						}
					}

					docx.createParagraph();
					
					List<Util> utilList = stories.getUtilList();
					if(utilList != null && utilList.size() > 0){
						for(Util util : utilList){
							if(util != null){
								
								addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum++ + "  " + "BML Util Libraries");
								
//								XWPFRun run = docx.createParagraph().createRun();
//								run.setBold(true);
//								run.setColor("800000");
//								run.setUnderline(UnderlinePatterns.SINGLE);
//								run.setText(storyNum + "." + storySubNum++ + "  " + "BML Util Libraries");
								//run.setText("BML Util Libraries");

								int numOfColumns = 3;
								XWPFTable utilTable = docx.createTable(2, numOfColumns);
								XWPFTableRow headerRow = utilTable.getRow(0);

								String[] headerNames = {"Util Name", "Variable Name", "Description"};
								
								addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);
								
//								headerRow.getCell(0).setColor("4bacc6");
//								headerRow.getCell(1).setColor("4bacc6");
//								headerRow.getCell(2).setColor("4bacc6");
//
//								headerRow.getCell(0).setText("Util Name");
//								headerRow.getCell(1).setText("Variable Name");
//								headerRow.getCell(2).setText("Description");

								XWPFTableRow newRow = utilTable.getRow(1);
								newRow.getCell(0).setText(util.getName());
								newRow.getCell(1).setText(util.getVariableName());
								newRow.getCell(2).setText(util.getDescription());
								
								setTableSize(utilTable, 2600, 3000, 3600, 0);
								
								addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, "Script Text");
								
//								XWPFRun scriptRunHeader = docx.createParagraph().createRun();
//								scriptRunHeader.addBreak();
//								scriptRunHeader.setBold(true);
//								scriptRunHeader.setColor("800000");
//								scriptRunHeader.setUnderline(UnderlinePatterns.SINGLE);
//								scriptRunHeader.setText("Script Text");
								
								XWPFRun scriptRun  = docx.createParagraph().createRun();
								for(String scriptText : util.getScriptText().split(";")){
									scriptRun.setText(scriptText + ";");
									scriptRun.addBreak();
								}
							
								
							}
						}
					}
					
					// Writing Commerce Actions
					List<CommerceAction> commerceActionList = stories.getCommerceActionList();
					if(commerceActionList != null && commerceActionList.size() > 0){
						docx.createParagraph();
						addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum++ + "  " + "Commerce Actions");
						int numOfColumns = 3;
						XWPFTable utilTable = docx.createTable(commerceActionList.size()+1, numOfColumns);
						XWPFTableRow headerRow = utilTable.getRow(0);
						String[] headerNames = {"Action", "Action Variable Name", "Description"};
						addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);
						int count = 1;
						for(CommerceAction commerceAction : commerceActionList){
							if(commerceAction != null){
								XWPFTableRow newRow = utilTable.getRow(count++);
								newRow.getCell(0).setText(commerceAction.getLabel());
								newRow.getCell(1).setText(commerceAction.getVariableName());
								newRow.getCell(2).setText(commerceAction.getDescription());
								setTableSize(utilTable, 2600, 3000, 3600, 0);
								
							}
						}
					}
					
					// Writing Approval Sequence 
					List<ApprovalSequence> approvalSequenceList = stories.getApprovalSequenceList();
					if(approvalSequenceList != null && approvalSequenceList.size() > 0){
						docx.createParagraph();
						addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum++ + "  " + "Approval Sequence");

						for(ApprovalSequence approvalSequence : approvalSequenceList){
							if(approvalSequence != null){
								int numOfColumns = 5;
								XWPFTable utilTable = docx.createTable(2, numOfColumns);
								XWPFTableRow headerRow = utilTable.getRow(0);
								String[] headerNames = {"Approval Reason Name", "Approval Reason Variable Name", "Approval Reason Description",
										"Approver", "Approval Template"};
								addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);

								XWPFTableRow newRow = utilTable.getRow(1);
								newRow.getCell(0).setText(approvalSequence.getLabel());
								newRow.getCell(1).setText(approvalSequence.getVariableName());
								newRow.getCell(2).setText(approvalSequence.getDescription());
								newRow.getCell(3).setText(approvalSequence.getApprover());
								newRow.getCell(4).setText(approvalSequence.getApprovalTemplate());

								setTableSize(utilTable, 2600, 3000, 3600, 0);
								
								if(StringUtils.isNotBlank(approvalSequence.getScriptText())){
									addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, "Script Text");
									XWPFRun scriptRun  = docx.createParagraph().createRun();
									for(String scriptText : approvalSequence.getScriptText().split(";")){
										scriptRun.setText(scriptText + ";");
										scriptRun.addBreak();
									}
								}
							}
						}
					}
					
					// Writing Printer Friendly Documents
					List<PrinterDocument> printerDocList = stories.getPrinterDocList();
					if(printerDocList != null && printerDocList.size() > 0){
						docx.createParagraph();
						addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum++ + "  " + "Printer Friendly Documents");
						int numOfColumns = 4;
						XWPFTable utilTable = docx.createTable(printerDocList.size()+1, numOfColumns);
						XWPFTableRow headerRow = utilTable.getRow(0);
						String[] headerNames = {"Document Name", "Variable Name", "Description", "Commerce Process Linked"};
						addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);
						int count = 1;
						for(PrinterDocument printerDoc : printerDocList){
							if(printerDoc != null){
								XWPFTableRow newRow = utilTable.getRow(count++);
								newRow.getCell(0).setText(printerDoc.getDocName());
								newRow.getCell(1).setText(printerDoc.getVariableName());
								newRow.getCell(2).setText(printerDoc.getDescription());
								newRow.getCell(2).setText(printerDoc.getCommerceProcessLinked());
								setTableSize(utilTable, 2600, 3000, 3600, 0);
							}
						}
					}
					
					docx.createParagraph().setPageBreak(true);
					storyNum++;
				}
				
				
				// Add Data Table List
				
				addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, "Data Table List");
				
//				XWPFRun dataTableRun = docx.createParagraph().createRun();
//				dataTableRun.setBold(true);
//				dataTableRun.setColor("800000");
//				dataTableRun.setUnderline(UnderlinePatterns.SINGLE);
//				dataTableRun.setText("Data Table List");
				
				int numOfColumns = 2;

				XWPFTable dataTable = docx.createTable(dataTableList.size() + 1,numOfColumns );
				XWPFTableRow tableHeaderRow = dataTable.getRow(0);

				String[] headerNames = {"Data Table Name", "Data Table Columns"};

				addHeaderNameColorBold(tableHeaderRow, headerNames ,numOfColumns);

//				tableHeaderRow.getCell(0).setColor("4bacc6");
//				tableHeaderRow.getCell(1).setColor("4bacc6");
//
//				tableHeaderRow.getCell(0).setText("Data Table Name");
//				tableHeaderRow.getCell(1).setText("Data Table Columns");
				
				setTableSize(dataTable, 3000, 6200, 0, 0);
				
				if(dataTable != null){
					int i = 1;
					for (DataTable dataTableTemp : dataTableList) {
						if(dataTableTemp != null){
							XWPFTableRow newRow = dataTable.getRow(i);
							newRow.getCell(0).setText(dataTableTemp.getTableName());
							newRow.getCell(1).setText(dataTableTemp.getDescription());
							i++;
						}
					}
				}
				
				docx.createParagraph().setPageBreak(true);
				
	            // Add users Lis
				
//				XWPFRun usersRun = docx.createParagraph().createRun();
//				usersRun.setBold(true);
//				usersRun.setColor("800000");
//				usersRun.setUnderline(UnderlinePatterns.SINGLE);
//				usersRun.setText("Users List");
				
				addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, "Users List");
				
				numOfColumns = 2;
				XWPFTable userTable = docx.createTable(usersList.size() + 1, numOfColumns );
				XWPFTableRow userHeaderRow = userTable.getRow(0);
				
				String[] userTableHeaderNames = {"User ID", "User Login Name"};

				addHeaderNameColorBold(userHeaderRow, userTableHeaderNames ,numOfColumns);
				
//				userHeaderRow.getCell(0).setColor("4bacc6");
//				userHeaderRow.getCell(1).setColor("4bacc6");
//
//				userHeaderRow.getCell(0).setText("User ID");
//				userHeaderRow.getCell(1).setText("User Login Name");
				
				setTableSize(userTable, 4600, 4600, 0, 0);
				
				if(userTable != null){
					int i = 1;
					for (Users usersTemp : usersList) {
						if(usersTemp != null){
							XWPFTableRow newRow = userTable.getRow(i);
							newRow.getCell(0).setText(usersTemp.getUserId());
							newRow.getCell(1).setText(usersTemp.getUserLoginName());
							i++;
						}
					}
				}
				
				docx.createParagraph().setPageBreak(true);
				
				//add group List
//				XWPFRun groupRun = docx.createParagraph().createRun();
//				groupRun.setBold(true);
//				groupRun.setColor("800000");
//				groupRun.setUnderline(UnderlinePatterns.SINGLE);
//				groupRun.setText("Groups List");
				
				addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, "Groups List");

				numOfColumns = 2;
				XWPFTable groupTable = docx.createTable(groupList.size() + 1, numOfColumns );
				XWPFTableRow groupHeaderRow = groupTable.getRow(0);
				
				String[] groupTableHeaderNames = {"Group Label", "Group Name"};

				addHeaderNameColorBold(groupHeaderRow, groupTableHeaderNames ,numOfColumns);
				
//				groupHeaderRow .getCell(0).setColor("4bacc6");
//				groupHeaderRow .getCell(1).setColor("4bacc6");
//
//				groupHeaderRow .getCell(0).setText("Group Label");
//				groupHeaderRow .getCell(1).setText("Group Name");
				
				setTableSize(groupTable, 4600, 4600, 0, 0);
				
				if(groupTable != null){
					int i = 1;
					for (Groups groupTemp : groupList) {
						if(groupTemp != null){
							XWPFTableRow newRow = groupTable.getRow(i);
							newRow.getCell(0).setText(groupTemp.getGroupLabel());
							newRow.getCell(1).setText(groupTemp.getGroupName());
							i++;
						}
					}
				}
			// Table of contents:
			/*XWPFParagraph p = docx.createParagraph();
			CTP ctP = p.getCTP();
			CTSimpleField toc = ctP.addNewFldSimple();
			toc.setInstr("TOC \\h");
			toc.setDirty(STOnOff.TRUE);*/
			docx.write(fos);
			Printer.println("Output File Path: " + outputFile.getAbsolutePath());
			fos.close();
			docx.close();
		}
	}


	private void addSectionTitle(XWPFDocument docx, boolean bold, String color, UnderlinePatterns underlinePatterns,
			String title) {
		XWPFRun run = docx.createParagraph().createRun();
		run.setBold(bold);
		run.setColor(color);
		run.setUnderline(underlinePatterns);
		run.setText(title);
	}


	private void createRuleTable(XWPFDocument docx, List<Rule> ruleList, String string, int storyNum, int storySubNum) {
		if(ruleList.size() > 0){
			
			addSectionTitle(docx, true, Constants.SECTIONTITLECOLOR, UnderlinePatterns.SINGLE, storyNum + "." + storySubNum + "  " + string);
			
//			XWPFParagraph paragraph = docx.createParagraph();
//			XWPFRun run = paragraph.createRun();
//			run.setBold(true);
//			run.setColor("800000");
//			run.setUnderline(UnderlinePatterns.SINGLE);
//			run.setText(storyNum + "." + storySubNum + "  " + string);
			//run.setText(string);

			int numOfColumns = 3; 
			XWPFTable ruleTable = docx.createTable(ruleList.size() + 1, numOfColumns);
			XWPFTableRow headerRow = ruleTable.getRow(0);
			
			String[] headerNames = {"Rule Name", "Variable Name", "Description"};

			addHeaderNameColorBold(headerRow, headerNames ,numOfColumns);

//			headerRow.getCell(0).setColor("4bacc6");
//			headerRow.getCell(1).setColor("4bacc6");
//			headerRow.getCell(2).setColor("4bacc6");

//			headerRow.getCell(0).setText("Rule Name");
//			headerRow.getCell(1).setText("Variable Name");
//			headerRow.getCell(2).setText("Description");
			
			setTableSize(ruleTable, 2600, 3000, 3600, 0);

			int i = 1;
			for(Rule rule : ruleList){
				if(rule != null){
					XWPFTableRow newRow = ruleTable.getRow(i);
					newRow.getCell(0).setText(rule.getName());
					newRow.getCell(1).setText(rule.getVariableName());
					newRow.getCell(2).setText(rule.getDescription());
//					XWPFRun scriptTextRun = docx.createParagraph().createRun();
//					scriptTextRun.setText("Script Text : "+rule.getScriptText());
					i++;
				}
			}
		}
		
	}

	private void addHeaderNameColorBold(XWPFTableRow headerRow, String[] headerNames, int numOfColumns) {
		for(int i = 0; i < numOfColumns ; i++){
			headerRow.getCell(i).removeParagraph(0);
			XWPFRun row0Run = headerRow.getCell(i).addParagraph().createRun();
			row0Run.setBold(true);
			row0Run.setText(headerNames[i]);
			headerRow.getCell(i).setColor("4bacc6");
		}
	}


	//table size = 9200 divided into number of columns
	private void setTableSize(XWPFTable table, long row0, long row1, long row2, long row3){
		for (int i = 0; i < table.getNumberOfRows(); i++) {
	        XWPFTableRow row = table.getRow(i);
	        int numCells = row.getTableCells().size();
	        for (int j = 0; j < numCells; j++) {
	            XWPFTableCell cell = row.getCell(j);
	            CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
	            CTTcPr pr = cell.getCTTc().addNewTcPr();
	            pr.addNewNoWrap();
	            if(j == 0 && row0 > 0){
		            cellWidth.setW(BigInteger.valueOf(row0));
	            }else if(j==1 && row1 > 0){
		            cellWidth.setW(BigInteger.valueOf(row1));
	            }else if(j==2 && row2 > 0){
		            cellWidth.setW(BigInteger.valueOf(row2));
	            }else if(j==3 && row3 > 0){
	            	cellWidth.setW(BigInteger.valueOf(row3));
	            }

	        }
	    }
	}

}
