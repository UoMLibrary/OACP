package uk.ac.man.library.oacpv2.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.man.library.oacpv2.model.Author;
import uk.ac.man.library.oacpv2.model.Publication;

public class PublicationExcelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<Publication> listPublications;

	public PublicationExcelExporter(List<Publication> listPublication) {
		this.listPublications = listPublication;
		workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() {
		sheet = workbook.createSheet("Publicationv2");

		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		int columnCount = 0;
		createCell(row, columnCount++, "ID", style);
		createCell(row, columnCount++, "Pure ID", style);
		createCell(row, columnCount++, "Title", style);
		createCell(row, columnCount++, "Accepted date", style);
		createCell(row, columnCount++, "E-publication date", style);
		createCell(row, columnCount++, "Published date", style);
		createCell(row, columnCount++, "Publisher", style);
		createCell(row, columnCount++, "Author first name", style);
		createCell(row, columnCount++, "Author last name", style);
		createCell(row, columnCount++, "Journal title", style);
		createCell(row, columnCount++, "School", style);
		createCell(row, columnCount++, "Faculty", style);
		createCell(row, columnCount++, "Gateway depositor", style);
		createCell(row, columnCount++, "Output type", style);
		createCell(row, columnCount++, "DOI", style);
		createCell(row, columnCount++, "OACP created date", style);
		createCell(row, columnCount++, "Pure record creator", style);
		createCell(row, columnCount++, "Pure record created date", style);
		createCell(row, columnCount++, "Deposit route", style);
//		createCell(row, columnCount++, "Requested press release", style);
		createCell(row, columnCount++, "Help raise visibility", style);
//		createCell(row, 20, "Enabled", style);
//		createCell(row, 21, "Enabled", style);
//		createCell(row, 22, "Enabled", style);
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if(value instanceof Date) {
			cell.setCellValue((Date)value);
			
		}
		else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines() {
		int rowCount = 1;
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		CellStyle styleDate = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		styleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		for (Publication publication : listPublications) {

			for (Author author : publication.getAuthors()) {

				Row row = sheet.createRow(rowCount++);
				int columnCount = 0;
				createCell(row, columnCount++, publication.getId().toString(), style);
				createCell(row, columnCount++, publication.getPureId(), style);
				createCell(row, columnCount++, publication.getTitle(), style);
				createCell(row, columnCount++, publication.getAcceptedDate(), styleDate);
				createCell(row, columnCount++, publication.getePublicationDate(), styleDate);
				createCell(row, columnCount++, publication.getPublicationDate(), styleDate);
				createCell(row, columnCount++, publication.getPublisherName(), style);
				createCell(row, columnCount++, author.getForename(), style);
				createCell(row, columnCount++, author.getSurname(), style);
				createCell(row, columnCount++, publication.getJournal(), style);
				createCell(row, columnCount++, author.getSchool_name(), style);
				createCell(row, columnCount++, author.getFaculty_name(), style);
				createCell(row, columnCount++, publication.getGateway_depositor(), style);
				createCell(row, columnCount++, publication.getOutputType(), style);
				createCell(row, columnCount++, publication.getDoi(), style);
				createCell(row, columnCount++, publication.getOacp_createdDate(), styleDate);
				createCell(row, columnCount++, publication.getCreatedBy(), style);
				createCell(row, columnCount++, publication.getCreatedDate(), styleDate);
				createCell(row, columnCount++, publication.getDeposit_route(), style);
//				createCell(row, columnCount++, publication.isRequested_press_release(), style);
				createCell(row, columnCount++, publication.isHelp_raise_visibility(), style);
			}
		}

	}
	
//all authors' name in one fields.
	private void writeDataLinesPureId() {
		int rowCount = 1;
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		for (Publication publication : listPublications) {

			String AuthorForename = "";
			String AuthorSurname = "";
			String AuthorSchool = "";
			String AuthorFaculty = "";
			int index =0;
			for (Author author : publication.getAuthors()) {
				index++;
				AuthorForename += index + "." + author.getForename() + ";";
				AuthorSurname +=  index + "." + author.getSurname() + ";";
				AuthorSchool +=  index + "." + author.getSchool_name() + ";";
				AuthorFaculty +=  index + "." + author.getFaculty_name() + ";";
			}

				Row row = sheet.createRow(rowCount++);
				int columnCount = 0;
				createCell(row, columnCount++, publication.getId().toString(), style);
				createCell(row, columnCount++, publication.getPureId(), style);
				createCell(row, columnCount++, publication.getTitle(), style);
				createCell(row, columnCount++, publication.getAcceptedDate(), style);
				createCell(row, columnCount++, publication.getePublicationDate(), style);
				createCell(row, columnCount++, publication.getPublicationDate(), style);
				createCell(row, columnCount++, publication.getPublisherName(), style);
				createCell(row, columnCount++, AuthorForename, style);
				createCell(row, columnCount++, AuthorSurname, style);
				createCell(row, columnCount++, publication.getJournal(), style);
				createCell(row, columnCount++, AuthorSchool, style);
				createCell(row, columnCount++, AuthorFaculty, style);
				createCell(row, columnCount++, publication.getGateway_depositor(), style);
				createCell(row, columnCount++, publication.getOutputType(), style);
				createCell(row, columnCount++, publication.getDoi(), style);
				createCell(row, columnCount++, publication.getOacp_createdDate(), style);
				createCell(row, columnCount++, publication.getCreatedBy(), style);
				createCell(row, columnCount++, publication.getCreatedDate(), style);
				createCell(row, columnCount++, publication.getDeposit_route(), style);
//				createCell(row, columnCount++, publication.isRequested_press_release(), style);
				createCell(row, columnCount++, publication.isHelp_raise_visibility(), style);
		}

	}

	public void export(HttpServletResponse response) throws IOException {
		writeHeaderLine();
		writeDataLinesPureId();
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}
	
	public void exportperAuthor(HttpServletResponse response) throws IOException {
		writeHeaderLine();
		writeDataLines();
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

}
