import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportGenerator {
    private final int START_INDEX;
    private final Date START_DATE;
    private final Date END_DATE;
    private final String DIRECTORY_PATH;
    private final String TEMPLATE_PATH;
    private final String AUTHOR;
    private final String WORKLOAD_PER_DAY;
    private final String WORKLOAD_PER_WEEK;
    private final String LOCATION;

    ReportGenerator(int startIndex, String startDate, String endDate, String directoryPath, String templatePath, String author, String workloadPerDay, String workloadPerWeek, String location) {
        this.START_INDEX = startIndex < 0 ? 1 : startIndex;
        this.START_DATE = this.parseDate(startDate);
        this.END_DATE = this.parseDate(endDate);
        this.DIRECTORY_PATH = directoryPath == null ? System.getProperty("user.home") + "\\reports" : directoryPath;
        this.TEMPLATE_PATH = templatePath == null ? System.getProperty("user.home") + "\\reports\\template\\template.pdf" : templatePath;
        this.AUTHOR = author == null ? "" : author;
        this.WORKLOAD_PER_DAY = workloadPerDay == null ? "8" : workloadPerDay;
        this.WORKLOAD_PER_WEEK = workloadPerWeek == null ? "40" : workloadPerWeek;
        this.LOCATION = location == null ? "" : location;
    }

    public void generateFiles() {
        this.createDirectory();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.START_DATE);
        int index = 0;
        Date currentDate = this.START_DATE;
        while (currentDate.before(this.END_DATE)) {
            try {
                int currentIndex = index + this.START_INDEX;
                Date endDate = Date.from(currentDate.toInstant().plus(Duration.ofDays(4)));

                File createdFile = this.createFile(currentIndex, currentDate, endDate);
                this.writeContentsToFile(createdFile, currentIndex, currentDate, endDate);
            } catch (IOException e) {
                e.printStackTrace();
            }

            index++;
            calendar.add(Calendar.DATE, 7);
            currentDate = calendar.getTime();
        }
    }

    private File createFile(int index, Date startDate, Date endDate) throws IOException {
        File from = new File(this.TEMPLATE_PATH);
        File to = new File(this.DIRECTORY_PATH, index + ". " + this.formatDate(startDate) + "-" + this.formatDate(endDate) + ".pdf");

        try {
            to.createNewFile();
            FileUtils.copyFile(from, to);
        } catch (IOException e) {
            throw e;
        }
        return to;
    }

    private void writeContentsToFile(File file, int number, Date startDate, Date endDate) throws IOException {
        if (!file.exists() || !file.isFile() || !file.canWrite()) {
            return;
        }

        try {
            PDDocument pdDocument = PDDocument.load(file);
            PDDocumentCatalog pdDocumentCatalog = pdDocument.getDocumentCatalog();
            PDAcroForm pdAcroForm = pdDocumentCatalog.getAcroForm();
            List<PDField> pdFields = pdAcroForm.getFields();

            pdFields.get(FormFields.NUMBER.getValue()).setValue(String.valueOf(number));
            pdFields.get(FormFields.YEAR.getValue()).setValue(String.valueOf(this.getCurrentYear(startDate)));
            pdFields.get(FormFields.NAME.getValue()).setValue(this.AUTHOR);
            pdFields.get(FormFields.WEEK_OF_YEAR.getValue()).setValue(String.valueOf(this.getCurrentWeekOfYear(startDate)));
            pdFields.get(FormFields.DATE_FROM.getValue()).setValue(this.formatDate(startDate));
            pdFields.get(FormFields.DATE_TO.getValue()).setValue(this.formatDate(endDate));
            pdFields.get(FormFields.MONDAY_HOURS.getValue()).setValue(this.WORKLOAD_PER_DAY);
            pdFields.get(FormFields.TUESDAY_HOURS.getValue()).setValue(this.WORKLOAD_PER_DAY);
            pdFields.get(FormFields.WEDNESDAY_HOURS.getValue()).setValue(this.WORKLOAD_PER_DAY);
            pdFields.get(FormFields.THURSDAY_HOURS.getValue()).setValue(this.WORKLOAD_PER_DAY);
            pdFields.get(FormFields.FRIDAY_HOURS.getValue()).setValue(this.WORKLOAD_PER_DAY);
            pdFields.get(FormFields.WEEK_TOTAL.getValue()).setValue(this.WORKLOAD_PER_WEEK);
            pdFields.get(FormFields.SIGNATURE_DATE.getValue()).setValue(this.formatDate(endDate));
            pdFields.get(FormFields.SIGNATURE_LOCATION.getValue()).setValue(this.LOCATION);

            pdDocument.save(file);
            pdDocument.close();

        } catch (IOException e) {
            throw e;
        }
    }

    private void createDirectory() {
        new File(this.DIRECTORY_PATH).mkdirs();
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd.MM.yy").format(date);
    }

    private Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private int getCurrentYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    private int getCurrentWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
