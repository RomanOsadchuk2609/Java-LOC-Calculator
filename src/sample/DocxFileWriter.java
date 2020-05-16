package sample;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DocxFileWriter {

	private DocxFileWriter() {
	}

	public static void writeResultsToFile(List<Result> results, String initialFileName, File resultFile) {
		XWPFDocument document = new XWPFDocument();
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setBold(true);
		run.setFontSize(14);
		run.setText("Результати обчислення LOC " + initialFileName);
		run.addBreak();

		XWPFTable table = document.createTable();
		table.setTableAlignment(TableRowAlign.LEFT);
		table.setWidthType(TableWidthType.PCT);
		table.setWidth("100%");
		XWPFTableRow tableHeader = table.getRow(0);
		tableHeader.getCell(0).setText("Критерій");
		tableHeader.addNewTableCell().setText("Значення");

		results.forEach(result -> {
			XWPFTableRow tableRow = table.createRow();
			tableRow.getCell(0).setText(result.getKey());
			tableRow.getCell(1).setText(result.getValue());
		});

		try (FileOutputStream out = new FileOutputStream(resultFile)) {
			document.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
