//pdfService
package com.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import com.dao.ExpenseDetailDAO;
import com.dao.PaymentVoucherDAO;
import com.dao.SummaryDFUnpaidByDetailAsOfDateDAO;
import com.dao.SummaryRevenueByDetailDAO;
import com.util.DbConnector;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import com.util.Property;
import com.dao.BatchDao;

public class CreatePDFService {

	ByteArrayOutputStream pdfOutputStream = null;

	// สร้างไฟล์ pdf
	public ByteArrayOutputStream createFilePDF(List<JasperPrint> listJasper, String passwords)
			throws JRException, IOException, SQLException, AddressException, MessagingException {

		// String password = "1234";
		String password = passwords;

		pdfOutputStream = new ByteArrayOutputStream();

		// construct exports report to pdf
		JRPdfExporter exporter = new JRPdfExporter();
		// ไฟล์ขาเข้า
		exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
		// ไฟล์ขาออก ที่ต้อง return กลับ เป็น ByteArrayOutputStream ไม่มีการวางไฟล์ไว้ใน
		// โฟลเดอ
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));
		// ตั่งค่า pdf file
		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();

		exportConfig.set128BitKey(true);
		// ใส่รหัสผ่าน ให้ pdf file
		exportConfig.setUserPassword(password);
		exportConfig.setOwnerPassword(password);
		exportConfig.setMetadataAuthor("baeldung");
		exportConfig.setEncrypted(true);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		System.out.println("Export file PDF success...!!");

		return pdfOutputStream;

	}

	// ฟังก์ชันการ map ค่า parameter ดึงค่ามาจาก application.propertise
	public JasperPrint getInJasperFile(String jasperFile, String code_doctor)
			throws JRException, IOException, SQLException {

		System.out.println("\n\nHere \n");

		// 1. รับไฟล์ jasper เพื่อ put ค่า
		String file = new File(this.getClass().getResource("/jasperReport/" + jasperFile).getFile()).getAbsoluteFile()
				.toString();
		// 1.1. รับค่า parameter
		String from_doctor = code_doctor;
		String to_doctor = code_doctor;
		String doctor = code_doctor;
		String hospitalCode = Property.getCenterProperty("/application.properties").getProperty("hospitalCode");
		String yyyy = Property.getCenterProperty("/application.properties").getProperty("yyyy");
		String to_date = Property.getCenterProperty("/application.properties").getProperty("to_date");
		String from_date = Property.getCenterProperty("/application.properties").getProperty("from_date");
		String mm = Property.getCenterProperty("/application.properties").getProperty("mm");
		String absoluteDiskPath = new File(CreatePDFService.class.getClass().getResource("/jasperReport").getFile())
				.getPath().toString();
		System.out.println("PATH report >> " + absoluteDiskPath);

		// String mm = null;
		// String yyyy = null;
		try {
			// mm = BatchDao.getMonth(hospitalCode);
			// yyyy = BatchDao.getYear(hospitalCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		// รับค่า จาก Property

		// --------------------------------------------------------------------------
		String[] jasperFiles = Property.getCenterProperty("/application.properties").getProperty("jasperFiles")
				.split(",");

		for (String jasFile : jasperFiles) {
			// เลือก ว่าจะเข้าอันไหนบ้าง
			if (jasperFile.equals(jasFile)) {
				System.out.print("----------------------------------------------\n" + jasFile + "\n");

				// หา คอลัมใน เจสเปอนั้นๆ
				String[] getParaJas = Property.getCenterProperty("/application.properties")
						.getProperty("jasperFiles[" + jasFile + "]").split(",");
				System.out.println(getParaJas.length);

				for (String parameter : getParaJas) {
					// คอลัมที่เท่าไหร่ก็ตามที่เจอคำนี้ ให้ ทำการ save แบบนี้
					if (parameter.equals("from_doctor")) {

						System.out.println(parameter + "			" + from_doctor);
						params.put(parameter, from_doctor);
						// ทำเสร็จไป column ถัดไป
						continue;

					}
					if (parameter.equals("to_doctor")) {

						System.out.println(parameter + "			" + to_doctor);
						params.put(parameter, to_doctor);
						continue;

					}
					if (parameter.equals("doctor")) {

						System.out.println(parameter + "			" + doctor);
						params.put(parameter, doctor);
						continue;

					}
					if (parameter.equals("from_date")) {
						System.out.println(parameter + "			" + from_date);
						params.put(parameter, from_date);
						continue;
					}
					if (parameter.equals("to_date")) {
						System.out.println(parameter + "			" + to_date);
						params.put(parameter, to_date);
						continue;
					}
					if (parameter.equals("hospital_code")) {
						System.out.println(parameter + "			" + hospitalCode);
						params.put(parameter, hospitalCode);
						continue;

					}
					if (parameter.equals("month")) {
						System.out.println(parameter + "			" + mm);
						params.put(parameter, mm);
						continue;

					}
					if (parameter.equals("year")) {
						System.out.println(parameter + "			" + yyyy);
						params.put(parameter, yyyy);
						continue;

					}
					if (parameter.equals("SUBREPORT_DIR")) {
						System.out.println(parameter + "			" + absoluteDiskPath);
						params.put(parameter, absoluteDiskPath);
						continue;
					} else {
						System.out.println(
								parameter + "			" + Property.getCenterProperty("/application.properties")
										.getProperty("jasperFiles[" + jasFile + "][" + parameter + "]"));
						params.put(parameter, Property.getCenterProperty("/application.properties")
								.getProperty("jasperFiles[" + jasFile + "][" + parameter + "]"));
					}

				}

				System.out.println("put data in method getInJasperFile Success");
				// add column เสร็จให้ ไป fillReport
				break;
			}
		}

		// creatReport
		JasperPrint jasperPrint = JasperFillManager.fillReport(file, params, DbConnector.getDBConnection());
		System.out.println("fill Report in method getInJasperFile Success");
		return jasperPrint;
	}

	// check ว่าหมอมีข้อมูลใน ไฟล์นั้นๆไหม
	public static int getNRowReport(String jasperFile, String code_doctor) throws SQLException, IOException {

		int n_row = 0;
		switch (jasperFile) {

		case "ExpenseDetail.jasper":
			n_row = ExpenseDetailDAO.getNExpenseDetail(code_doctor);
			break;

		case "PaymentVoucher.jasper":
			n_row = PaymentVoucherDAO.getNPaymentVoucher(code_doctor);
			break;

		case "SummaryDFUnpaidByDetailAsOfDate.jasper":
			n_row = SummaryDFUnpaidByDetailAsOfDateDAO.getNSummaryDFUnpaidByDetailAsOfDate(code_doctor);
			break;

		case "SummaryRevenueByDetail.jasper":
			n_row = SummaryRevenueByDetailDAO.getNSummaryDFUnpaidByDetailAsOfDate(code_doctor);
			break;
		}
		System.out.println("\ncheck row in method getNRowReport Success");

		return n_row;

	}

}
