package com.capgroup.dcip.webapi.report;

import com.capgroup.dcip.app.common.LinkModel;
import com.capgroup.dcip.app.common.PropertyModel;
import com.capgroup.dcip.app.identity.ProfileService;
import com.capgroup.dcip.app.identity.UserModel;
import com.capgroup.dcip.app.identity.UserService;
import com.capgroup.dcip.app.journal.InvestmentActionModel;
import com.capgroup.dcip.app.journal.InvestmentReasonModel;
import com.capgroup.dcip.app.journal.InvestmentTypeModel;
import com.capgroup.dcip.app.reference.capital_system.AccountModel;
import com.capgroup.dcip.domain.identity.ApplicationRole;
import com.capgroup.dcip.util.DateUtil;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JournalReportGenerator {

    private static final String CONTENT_TYPE_CSV = "text/csv";
    private static final String FILE_NAME_DATE_PATTERN = "MM_dd_yyyy_mm_ss";
    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String PROPERTY_PATH = "ui/investment-journal";

    @AllArgsConstructor
    private static class ReportColumn {
        String headerTitle;
        Function<InvestmentActionModel, String> renderContent;
    }

    private static ReportColumn idColumn = new ReportColumn("Record ID", model -> model.getId().toString());
    private static ReportColumn managerColumn = new ReportColumn("Manager", model -> model.getInitials() != null ? model.getInitials() : "");
    private static ReportColumn analystColumn = new ReportColumn("Analyst", model -> model.getProfile() != null ? model.getProfile().getInitials() : "");
    private        ReportColumn partnerColumn = new ReportColumn("Partner", model -> buildPartnersName(model.getPartners()));
    private static ReportColumn fundColumn = new ReportColumn("Fund", model -> buildFunds(model.getAccounts()));
    private static ReportColumn actionColumn = new ReportColumn("Action", model -> model.getInvestmentTypes() != null ? model.getInvestmentActionType().getInvestmentAction() : "");
    private static ReportColumn securityColumn = new ReportColumn("Security", model -> model.getCompany() != null ? model.getCompany().getShortName() : "");
    private static ReportColumn reasonCodeColumn = new ReportColumn("Reason Code", model -> buildInvestmentReasonsName(model.getInvestmentReasons()));
    private static ReportColumn investmentTypeColumn = new ReportColumn("Investment Type", model -> buildInvestmentTypesName(model.getInvestmentTypes()));
    private static ReportColumn convictionLevelColumn = new ReportColumn("Conviction Level", model -> model.getConvictionLevel() != null ? model.getConvictionLevel().toString() : "");
    private static ReportColumn orderInfoColumn = new ReportColumn("Order Info for Investment Control", InvestmentActionModel::getOrderInfo);
    private static ReportColumn reasonForActionColumn = new ReportColumn("Comments", InvestmentActionModel::getReasonForBuying);
    private static ReportColumn dateColumn = new ReportColumn("Date", model -> DateUtil.format(model.getActionDate(), "MM/dd/yyyy"));

    private ProfileService profileService;
    private UserService userService;

    @Autowired
    public JournalReportGenerator(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    private List<ReportColumn> getReportColumns(Long profileId) {
        Map<String, String> journalProperties = new HashMap<>();
        for (PropertyModel property : profileService.findPropertiesFor(profileId, PROPERTY_PATH)) {
            journalProperties.put(property.getKey().substring(PROPERTY_PATH.length() + 1), property.getValue());
        }
        List<ReportColumn> result = new ArrayList<>();
        result.add(idColumn);
        result.add(managerColumn);
        if (journalProperties.getOrDefault("analyst-visible", "false").equals("true")) result.add(analystColumn);
        if (journalProperties.getOrDefault("partner-visible", "false").equals("true")) {
            result.add(partnerColumn);
        }
        result.add(fundColumn);
        result.add(actionColumn);
        result.add(securityColumn);
        if (journalProperties.getOrDefault("reason-code-visible", "false").equals("true")) result.add(reasonCodeColumn);
        result.add(investmentTypeColumn);
        result.add(convictionLevelColumn);
        result.add(orderInfoColumn);
        result.add(reasonForActionColumn);
        result.add(dateColumn);
        return result;
    }

    public void exportCsvReport(Long profileId, Iterable<InvestmentActionModel> modelStream, HttpServletResponse response) throws IOException {
        response.setContentType(CONTENT_TYPE_CSV);
        String fileName = String.format("attachment; filename=\"InvestmentAction_%s.csv\"",
                DateUtil.format(LocalDateTime.now(), FILE_NAME_DATE_PATTERN));
        response.setHeader(HEADER_CONTENT_DISPOSITION, fileName);

        List<ReportColumn> columns = getReportColumns(profileId);

        try (CsvListWriter csvWriter = new CsvListWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE)) {
            csvWriter.writeHeader((String[])columns.stream().map(c -> c.headerTitle).toArray(String[]::new));
            if (modelStream != null) {
                for (InvestmentActionModel model : modelStream) {
                    csvWriter.write(columns.stream().map(c -> c.renderContent.apply(model)).collect(Collectors.toList()));
                }
            }
        }
    }

    public void exportExcelReport(Long profileId, Iterable<InvestmentActionModel> modelStream, HttpServletResponse response) throws IOException {
        String fileName = String.format("attachment; filename=\"InvestmentAction_%s.xls\"",
                DateUtil.format(LocalDateTime.now(), FILE_NAME_DATE_PATTERN));
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HEADER_CONTENT_DISPOSITION, fileName);

        //build sheet
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Investment Journal");
        sheet.setDefaultColumnWidth(30);

        //build header cells style
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setBold(true);
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style.setFont(font);

        List<ReportColumn> columns = getReportColumns(profileId);

        //create header row
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            header.createCell(i).setCellValue(columns.get(i).headerTitle);
            header.getCell(i).setCellStyle(style);
        }

        int rowCount = 1;

        if (modelStream != null) {
            for (InvestmentActionModel model : modelStream) {
                Row investmentJournalRow = sheet.createRow(rowCount++);
                for (int i = 0; i < columns.size(); i++) {
                    investmentJournalRow.createCell(i).setCellValue(columns.get(i).renderContent.apply(model));
                }
            }
        }

        workbook.write(response.getOutputStream());
    }

    private static String buildInvestmentTypesName(List<InvestmentTypeModel> investmentTypes) {
        return investmentTypes == null ? "" : investmentTypes.stream()
                .map(InvestmentTypeModel::getName)
                .reduce((s1, s2) -> s1 + ";" + s2)
                .orElse("");
    }

    private static String buildInvestmentReasonsName(List<InvestmentReasonModel> investmentReasons) {
        return investmentReasons == null ? "" : investmentReasons.stream()
                .map(InvestmentReasonModel::getName)
                .reduce((s1, s2) -> s1 + ";" + s2)
                .orElse("");
    }

    private String buildPartnersName(List<LinkModel<Long>> partnerLinks) {
        return partnerLinks == null ? "" : partnerLinks.stream()
                .map(link -> userService.findById(link.getId()).getInitials())
                .reduce((s1, s2) -> s1 + ';' + s2)
                .orElse("");
    }

    private static String buildFunds(List<AccountModel> accountModelList) {
        return accountModelList == null ? "" : accountModelList.stream()
                .map(AccountModel::getName)
                .reduce((s1, s2) -> s1 + ";" + s2)
                .orElse("");
    }
}
