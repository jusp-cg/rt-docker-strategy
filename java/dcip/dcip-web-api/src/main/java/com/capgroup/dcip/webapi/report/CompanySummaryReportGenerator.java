package com.capgroup.dcip.webapi.report;


import com.capgroup.dcip.app.data.TimeSeriesModel;
import com.capgroup.dcip.app.reference.company.CompanySummaryModel;
import com.capgroup.dcip.app.canvas_summary.CanvasSummaryModel;
import com.capgroup.dcip.app.data.TimeSeriesQueryResultModel;
import com.capgroup.dcip.app.identity.ProfileModel;
import com.capgroup.dcip.app.thesis.model.ThesisTreeModel;
import com.capgroup.dcip.domain.data.TimeSeries;

import com.capgroup.dcip.util.DateUtil;
import com.capgroup.dcip.util.CurrencyUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFChart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutMode;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutTarget;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.PresetColor;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.AxisTickMark;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.DisplayBlanks;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFChartAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
public class CompanySummaryReportGenerator {

    private static final String FILE_NAME_DATE_PATTERN = "MM_dd_yyyy_mm_ss";
    private static final String HEADER_DATE_PATTERN = "MMMM dd, yyyy";
    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String FONT_FAMILY = "AvenirNext LT Com Regular";
    private static final String WHITE_SPACE_CHAR = "-";
    private static final Long MAX_ESTIMATE_YEARS = 3l;

    /*
     * This sorting order is used for the final report table
     * It may eventually be moved to the database
     *
     * ** This is also on the UI; summary-report.component.ts
     */
    private static final String[] FIN_TABLE_SORT_ORDER = new String[]
            {"Total Revenues", "EBIT", "Tax Rate", "Net Income",
                    "EPS GAAP", "EPS Non-GAAP", "Free Cash Flow / Share",
                    "Diluted Shares Outstanding", "Dividend / Share", "Payout Ratio",
                    "Gross Margin", "EBIT Margin", "Total Revenues / Share Growth", "EBIT / Share Growth", "Earnings / Share Growth",
                    "Dividend / Share Growth", "Cash from Operations", "Capital Expenditure", "Free Cash Flow", "Levered Free Cash Flow",
                    "FCF / Net Income", "Net Debt", "Net Debt / EBITDA", "Net Debt / Total Equity", "Return on Equity",
                    "P / EPS", "P / B", "EV / Total Revenues", "EV / EBITDA", "Dividend Yield", "FCF Yield"};

    /*
     * Formatting the table with specific trailing items
     * This requirement came from DCIP-1374
     */
    private static final Map<String, String> FIN_TABLE_FORMAT_ENDING = new HashMap<>();

    static {
        FIN_TABLE_FORMAT_ENDING.put("P / EPS", "x");
        FIN_TABLE_FORMAT_ENDING.put("P / B", "x");
        FIN_TABLE_FORMAT_ENDING.put("EV / Total Revenues", "x");
        FIN_TABLE_FORMAT_ENDING.put("EV / EBITDA", "x");
        FIN_TABLE_FORMAT_ENDING.put("Tax Rate", "%");
        FIN_TABLE_FORMAT_ENDING.put("Payout Ratio", "%");
        FIN_TABLE_FORMAT_ENDING.put("Gross Margin", "%");
        FIN_TABLE_FORMAT_ENDING.put("EBIT Margin", "%");
        FIN_TABLE_FORMAT_ENDING.put("Total Revenues / Share Growth", "%");
        FIN_TABLE_FORMAT_ENDING.put("EBIT / Share Growth", "%");
        FIN_TABLE_FORMAT_ENDING.put("Earnings / Share Growth", "%");
        FIN_TABLE_FORMAT_ENDING.put("Dividend / Share Growth", "%");
        FIN_TABLE_FORMAT_ENDING.put("FCF / Net Income", "%");
        FIN_TABLE_FORMAT_ENDING.put("Net Debt / Total Equity", "%");
        FIN_TABLE_FORMAT_ENDING.put("Return on Equity", "%");
        FIN_TABLE_FORMAT_ENDING.put("Dividend Yield", "%");
        FIN_TABLE_FORMAT_ENDING.put("FCF Yield", "%");
    }

    /*
     * Formatting the digits on each data item
     *
     */
    private static final Map<String, Long> FIN_TABLE_DECIAMLS = new HashMap<>();
    static {
        FIN_TABLE_DECIAMLS.put("Total Revenues", 2L);
        FIN_TABLE_DECIAMLS.put("EBIT", 2L);
        FIN_TABLE_DECIAMLS.put("Tax Rate", 1L);
        FIN_TABLE_DECIAMLS.put("Net Income",  2L);
        FIN_TABLE_DECIAMLS.put("EPS GAAP", 2L);
        FIN_TABLE_DECIAMLS.put("EPS Non-GAAP",  2L);
        FIN_TABLE_DECIAMLS.put("Free Cash Flow / Share",  2L);
        FIN_TABLE_DECIAMLS.put("Diluted Shares Outstanding", 2L);
        FIN_TABLE_DECIAMLS.put("Dividend / Share",  2L);
        FIN_TABLE_DECIAMLS.put("Payout Ratio",  1L);
        FIN_TABLE_DECIAMLS.put("Gross Margin",  1L);
        FIN_TABLE_DECIAMLS.put("EBIT Margin",  1L);
        FIN_TABLE_DECIAMLS.put("Total Revenues / Share Growth",  1L);
        FIN_TABLE_DECIAMLS.put("EBIT / Share Growth",  1L);
        FIN_TABLE_DECIAMLS.put("Earnings / Share Growth",  1L);
        FIN_TABLE_DECIAMLS.put("Dividend / Share Growth", 1L);
        FIN_TABLE_DECIAMLS.put("Cash from Operations",  2L);
        FIN_TABLE_DECIAMLS.put("Capital Expenditure",  2L);
        FIN_TABLE_DECIAMLS.put("Free Cash Flow",  2L);
        FIN_TABLE_DECIAMLS.put("Levered Free Cash Flow", 2L);
        FIN_TABLE_DECIAMLS.put("FCF / Net Income",  1L);
        FIN_TABLE_DECIAMLS.put("Net Debt",  2L);
        FIN_TABLE_DECIAMLS.put("Net Debt / EBITDA",  2L);
        FIN_TABLE_DECIAMLS.put("Net Debt / Total Equity",  1L);
        FIN_TABLE_DECIAMLS.put("Return on Equity",  1L);
        FIN_TABLE_DECIAMLS.put("P / EPS",  1L);
        FIN_TABLE_DECIAMLS.put("P / B",  1L);
        FIN_TABLE_DECIAMLS.put("EV / Total Revenues",  1L);
        FIN_TABLE_DECIAMLS.put("EV / EBITDA",  1L);
        FIN_TABLE_DECIAMLS.put("Dividend Yield",  1L);
        FIN_TABLE_DECIAMLS.put("FCF Yield",  1L);
        FIN_TABLE_DECIAMLS.put("FY End Price", 2L);
        FIN_TABLE_DECIAMLS.put("FY End EV", 2L);
        FIN_TABLE_DECIAMLS.put("Gross Profit",  2L);
        FIN_TABLE_DECIAMLS.put("EBITDA", 2L);
    }


    /*
     * Formatting the table with specific header column items
     * It may eventually be stored in the database
     * This requirement came from DCIP-861 / DCIP-1374
     */
    private static final Map<String, String> FIN_TABLE_HEADERS = new HashMap<>();

    static {
        FIN_TABLE_HEADERS.put("Total Revenues", "Income Statement");
        FIN_TABLE_HEADERS.put("Gross Margin", "Margins");
        FIN_TABLE_HEADERS.put("Total Revenues / Share Growth", "Growth");
        FIN_TABLE_HEADERS.put("Cash from Operations", "Cash Flow");
        FIN_TABLE_HEADERS.put("Net Debt", "Balance Sheet");
        FIN_TABLE_HEADERS.put("P / EPS", "Valuation");
    }

    /*
     *	Series Mapping
     *	This links the Summary Data Name (key) with the Estimate Series Id (val)
     */
    private static final Map<String, Long> FIN_EST_SERIES_MAPPING = new HashMap<>();

    static {
        FIN_EST_SERIES_MAPPING.put("Total Revenues", 10L);
        FIN_EST_SERIES_MAPPING.put("EBIT", 11L);
        FIN_EST_SERIES_MAPPING.put("Tax Rate", 12L);
        FIN_EST_SERIES_MAPPING.put("Net Income", 13L);
        FIN_EST_SERIES_MAPPING.put("Diluted Shares Outstanding", 14L);
        FIN_EST_SERIES_MAPPING.put("Dividend / Share", 15L);
        FIN_EST_SERIES_MAPPING.put("Cash from Operations", 16L);
        FIN_EST_SERIES_MAPPING.put("Capital Expenditure", 17L);
        FIN_EST_SERIES_MAPPING.put("Net Debt", 18L);
        FIN_EST_SERIES_MAPPING.put("Gross Profit", 19L);
        FIN_EST_SERIES_MAPPING.put("EBITDA", 20L);
        FIN_EST_SERIES_MAPPING.put("Shareholder Equity", 21L);
        FIN_EST_SERIES_MAPPING.put("FY End Price", 22L);
        FIN_EST_SERIES_MAPPING.put("FY End EV", 23L);
        FIN_EST_SERIES_MAPPING.put("EPS Non-GAAP", 25L);
        FIN_EST_SERIES_MAPPING.put("Levered Free Cash Flow", 26L);
    }

    /*
     *  Calculation Path
     *  These are needed to calculate future values from estimates
     */
    private static final String CALC_FILE_PATH = "calculations.txt";


    public void exportDocxReportBlank(HttpServletResponse response)
            throws IOException {
        String fileName = String.format("attachment; filename=\"BlankDocument_%s.docx\"",
                DateUtil.format(LocalDateTime.now(), FILE_NAME_DATE_PATTERN));

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader(HEADER_CONTENT_DISPOSITION, fileName);

        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText("Blank Document");

            try (OutputStream os = response.getOutputStream()) {
                doc.write(os);
            }
        }
    }


    public void exportDocxReport(CanvasSummaryModel canvasSummary,
                                 Optional<CompanySummaryModel> companySummary,
                                 ProfileModel profile, Iterable<ThesisTreeModel> thesisTree,
                                 TimeSeriesModel<TimeSeries.Entry> compPrice,
                                 TimeSeriesModel<TimeSeries.Entry> compIndex,
                                 Iterable<TimeSeriesQueryResultModel<TimeSeries.Entry>> estimateModels,
                                 HttpServletResponse response) throws IOException {

    	/*
    	 * Debugging to see how long the Document takes to generate
    	 * Not needed on QA 
    	
    		System.out.println("Document Creation Started");
    		long startTime = System.nanoTime();
    	*/


        /*
         * Setting the Locale to be ROOT to get the proper currency codes
         * Do I need this? More testing needed
         */
        Locale.setDefault(Locale.ROOT);


        /*
         * 	Get all needed canvas summary objects
         */

        //Company Summary
        CompanySummaryModel compSummary = new CompanySummaryModel();
        if (companySummary.isPresent()) {
            compSummary = companySummary.get();
        }

        //Get the Thesis Tree
        ArrayList<String> thesisPoints = new ArrayList<String>();
        if (thesisTree != null) {
            for (ThesisTreeModel tree : thesisTree) {
                thesisPoints.add(tree.getThesisPoint().getText());
            }
        }

        //Build an estimate Map<Map<>>
        //	HashMap <Series, <Date, Value>>
        //	We iterate through each Row, so this is easier to retrieve values

        // Filter out values that don't match the FY End Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
        LocalDate fyEnd = LocalDate.parse(compSummary.getFiscalYearEnd() + "-9999", formatter);

        Map<Long, Map<LocalDateTime, BigDecimal>> estimateMap = new HashMap<Long, Map<LocalDateTime, BigDecimal>>();
        HashSet<String> estimateYears = new HashSet<>();
        estimateModels.forEach(entry -> {
            Map<LocalDateTime, BigDecimal> eMap = new HashMap<>();
            
            // related to bug, DCIP-1833, duplicate keys coming in
            // related to bug, DCIP-1860, new user estiamtes may return a valid error
            if(entry.getTimeSeries() != null && entry.getErrorMessage() == null && entry.getTimeSeries().getValues() != null ) {
                entry.getTimeSeries().getValues().forEach(val -> {
                    LocalDateTime t = val.getTimestamp();
                    //Only save the correct estimates
                    if (t.getMinute() == 0 && t.getHour() == 0 && t.getSecond() == 0 &&
                            t.getMonthValue() == fyEnd.getMonthValue() && t.getDayOfMonth() == fyEnd.getDayOfMonth()) {
                        eMap.put(val.getTimestamp(), val.getValue());
                        estimateYears.add(String.valueOf(val.getTimestamp().getYear()));
                    }
                });
                estimateMap.put(entry.getQuery().getSeriesId(), eMap);
            }
        });

        // Get the calculations JSON
        ObjectMapper objMapper = new ObjectMapper();
        InputStream calcInput = getClass().getClassLoader().getResourceAsStream(CALC_FILE_PATH);
        List<Calculation> estimateCalculations = objMapper.readValue(calcInput, new TypeReference<List<Calculation>>() {});

        /*
         * 	Start Building the MetaData for the Document
         */
        String ticker = compSummary.getCompanyTicker();
        String initials = profile.getInitials();
        String date = DateUtil.format(LocalDateTime.now(), FILE_NAME_DATE_PATTERN).toString();
        String fileName = ticker + "_" + date + ".docx";

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader(HEADER_CONTENT_DISPOSITION, fileName);


        //Create the docx document
        try (XWPFDocument doc = new XWPFDocument()) {

            // Set the Page Margins
            // Page margins are measured at 1440p per inch
            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar pageMar = sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(720L));
            pageMar.setTop(BigInteger.valueOf(488L));
            pageMar.setRight(BigInteger.valueOf(720L));
            pageMar.setBottom(BigInteger.valueOf(200L));
            pageMar.setHeader(BigInteger.valueOf(288L));
            pageMar.setFooter(BigInteger.valueOf(288L));



            /*	Section 1 - CoverPage Header
             * 	Includes the Date
             *
             */

            String reportDate = DateUtil.format(LocalDateTime.now(), HEADER_DATE_PATTERN);

            CTP ctP = CTP.Factory.newInstance();
            XWPFParagraph[] pars = new XWPFParagraph[1];
            XWPFParagraph pHeader = new XWPFParagraph(ctP, doc);
            XWPFRun rHeader = pHeader.createRun();
            pHeader.setAlignment(ParagraphAlignment.RIGHT);
            rHeader.setText(reportDate);
            rHeader.setFontFamily(FONT_FAMILY);
            rHeader.setFontSize(8);
            rHeader.setItalic(true);
            pHeader.setSpacingAfter(0);

            pars[0] = pHeader;
            XWPFHeaderFooterPolicy hfPolicy = doc.createHeaderFooterPolicy();
            hfPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, pars);


            /*	Section 2 - Company Header
             * 	Includes:
             * 	 Company Name, Ticker, Author, IUnit
             * 	 Canvas Summary Taglines
             * 	 Header data in a table
             */


            //  Create canvas author string
            String canvasAuthorHeader = new String();
			/*
			 * 	Changing Full Name -> Initials as per JMKB's email 4/22
			 * 
    			canvasAuthorHeader = profile.getUserName();
    			canvasAuthorHeader = canvasAuthorHeader + " (" + initials + ")";
    			canvasAuthorHeader = canvasAuthorHeader + ", " + profile.getInvestmentUnit();
    		*/
            canvasAuthorHeader = initials + ", " + profile.getInvestmentUnit();


            String companyHeader = new String();
            companyHeader = compSummary.getCompanyName();

            /*
             * Company name might be too long; do we a) shrink font size, b) shrink name?
             * Only allow 35 characters
             * How do we measure the amount of space?
             */

            if (companyHeader.length() > 45) {
                companyHeader = companyHeader.substring(0, 45) + "...";
            }


            companyHeader = companyHeader + " (" + ticker + ")";

            XWPFParagraph pCHeader = doc.createParagraph();
            XWPFRun rCHeader = pCHeader.createRun();
            pCHeader.setAlignment(ParagraphAlignment.LEFT);
            pCHeader.setSpacingAfter(0);
            rCHeader.setText(companyHeader);
            rCHeader.setBold(true);
            rCHeader.setColor("6658E5");
            rCHeader.setFontFamily("AvenirNext LT Com Medium");
            rCHeader.setFontSize(12);

            //Add a tab stop to push the author all the way to the right
            CTTabStop ctTS = pCHeader.getCTP().getPPr().addNewTabs().addNewTab();
            ctTS.setVal(STTabJc.RIGHT);

            /*
             * 	1440p per inch
             * 	8.5" - (2*0.5") for both margins = 7.5*1440=10800
             */
            ctTS.setPos(BigInteger.valueOf(10800));
            rCHeader.addTab();
            rCHeader = pCHeader.createRun();
            rCHeader.setText(canvasAuthorHeader);
            rCHeader.setBold(true);
            rCHeader.setFontFamily(FONT_FAMILY);
            rCHeader.setColor("DC143C");    // red
            rCHeader.setFontSize(9);
            rCHeader.setItalic(true);


            XWPFParagraph pCHeader2 = doc.createParagraph();
            XWPFRun rCHeader2 = pCHeader2.createRun();
            rCHeader2.setText("Summary: ");
            //rCHeader2.addTab();	// Removed from DCIP-1374 new req 4/1/19
            rCHeader2.setText(canvasSummary.getTagline());
            rCHeader2.setFontSize(11);
            rCHeader2.setBold(true);
            rCHeader2.setItalic(true);
            rCHeader2.setFontFamily(FONT_FAMILY);
            rCHeader2.addBreak();

            rCHeader2.setText("Action: ");
            //rCHeader2.addTab();	// Removed from DCIP-1374 new req 4/1/19
            rCHeader2.setText(canvasSummary.getAction());
            rCHeader2.setFontSize(11);
            rCHeader2.setBold(true);
            rCHeader2.setItalic(true);
            rCHeader2.setFontFamily(FONT_FAMILY);
            pCHeader2.setSpacingAfter(60);    //these are 1/20th of a font point
            pCHeader2.setSpacingBetween(1);

            /** Create the Company Header Table
             * 	Market Cap | Exch Rate | Exchange | Float
             *  Price | Dividend Yield | Daily Vol
             */
            int rows = 2;
            int cols = 8;
            Map<String, String> companyHeaderTable = new LinkedHashMap<String, String>();    //Order matter! Too small for sorting array
            BigDecimal divPerShare = new BigDecimal(compSummary.getDividendPerShare());
            BigDecimal divYield = new BigDecimal("0");



            /*
             * Calculate the latest Dividend Yield
             * Check to make sure currencies are the same
             */
            if (divPerShare != null && divPerShare.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal latestPrice = new BigDecimal(compSummary.getLatestPrice());

                if (compSummary.getTradingCurrency() == compSummary.getReportingCurrency()) {
                    divYield = (divPerShare.divide(latestPrice, 5, RoundingMode.HALF_UP)).scaleByPowerOfTen(2);
                } else {
                    BigDecimal reportingExRate = new BigDecimal(compSummary.getReportingExchangeRate());
                    BigDecimal tradingExRate = new BigDecimal(compSummary.getExchangeRate());
                    BigDecimal exRate = reportingExRate.divide(tradingExRate, 10, RoundingMode.HALF_UP);
                    divYield = (divPerShare.divide(latestPrice, 5, RoundingMode.HALF_UP)).multiply(exRate).scaleByPowerOfTen(2);
                }

            }

            String tradingCur = compSummary.getTradingCurrency();
            String tradingCurSym = CurrencyUtil.getCurrencySymbol(tradingCur);
            String printedPrice = tradingCurSym + roundStr(compSummary.getLatestPrice(), 2);
            String marketCap = compSummary.getMarketCap();
            int marketCapRounding = marketCap.substring(0, marketCap.indexOf('.')).length() >= 5 ? 0 : 2;
            String dailyVol = compSummary.getDailyVolume();
//            int dailyVolRounding = dailyVol.substring(0, dailyVol.indexOf('.')).length() > 3 ? 0 : 2;
            int dailyVolRounding = 1;

            /*
             * Add each item to the LINKED hash map companyHeaderTable
             * Make sure the decimals are accurate
             */
            companyHeaderTable.put("Market Cap ($USD Mil)", roundStr(compSummary.getMarketCap(), marketCapRounding));
            companyHeaderTable.put("Exchange Rate ($USD/" + getCurAndSym(tradingCurSym, tradingCur) + ")", roundStr(compSummary.getExchangeRate(), 2));
            companyHeaderTable.put("Exchange", compSummary.getExchange());
            companyHeaderTable.put("Float", roundStr(compSummary.getPercentFloat(), 1) + "%");
            companyHeaderTable.put("Price (" + tradingCurSym + ")", roundStr(compSummary.getLatestPrice(), 3));
            companyHeaderTable.put("Dividend Yield", divYield.setScale(1, RoundingMode.HALF_UP).toString() + "%");
            companyHeaderTable.put("Daily Volume ($USD Mil)", roundStr(compSummary.getDailyVolume(), dailyVolRounding));


            XWPFTable hTable = createHeaderTable(companyHeaderTable, rows, cols, doc);
            hTable.getCTTbl().getTblPr().addNewTblStyle().setVal("StyledTable");
            hTable.setInsideHBorder(XWPFBorderType.SINGLE, 1, 0, "ffffff");        //white
            hTable.setInsideVBorder(XWPFBorderType.SINGLE, 1, 0, "ffffff");        //white
            hTable.setWidth(10800);        //7.5"




            /*
             *  Create company description
             *
             *  Magic number: 550(+3) characters (+...)
             *  Figure out how to calculate the number of lines of text, then reduce to 4 lines
             *
             */

            String compDescription = compSummary.getCompanyDescription();
            if (compDescription != null && !compDescription.isEmpty()) {
                int descCharLength = Math.min(550, compDescription.length());
                compDescription = compDescription.substring(0, descCharLength);
                compDescription = compDescription.substring(0, compDescription.lastIndexOf(' '));
                compDescription = compDescription + "...";
            } else {
                compDescription = "Company description is not available in S&P Xpressfeed.";
            }

            XWPFParagraph pDesc = doc.createParagraph();
            XWPFRun rDesc = pDesc.createRun();
            pDesc.setAlignment(ParagraphAlignment.LEFT);
            pDesc.setSpacingAfter(60);
            pDesc.setSpacingBefore(60);

            rDesc.setText("Company Description");
            rDesc.setBold(true);
            rDesc.setUnderline(UnderlinePatterns.SINGLE);
            rDesc.setFontFamily(FONT_FAMILY);
            rDesc.setFontSize(9);
            rDesc.addBreak();
            rDesc = pDesc.createRun();
            rDesc.setText(compDescription);
            rDesc.setFontFamily(FONT_FAMILY);
            rDesc.setFontSize(8);





            /*	Section 3 - Thesis
             * 	Includes:
             * 	 The text from each header node only
             * 	 Up to 5 nodes only
             */

            //Bullet List
            CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
            cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));
            CTLvl cTLvl = cTAbstractNum.addNewLvl();
            cTLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);
            cTLvl.addNewLvlText().setVal("\u2022");    //the bullet char

            XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
            XWPFNumbering numbering = doc.createNumbering();
            BigInteger abstractNumId = numbering.addAbstractNum(abstractNum);
            BigInteger numId = numbering.addNum(abstractNumId);

            XWPFParagraph pHThesis = doc.createParagraph();
            XWPFRun rHThesis = pHThesis.createRun();
            rHThesis.setText("Investment Thesis");
            rHThesis.setBold(true);
            rHThesis.setUnderline(UnderlinePatterns.SINGLE);
            rHThesis.setFontFamily(FONT_FAMILY);
            rHThesis.setFontSize(9);
            pHThesis.setSpacingBefore(60);
            pHThesis.setSpacingAfter(20);


            int maxBullets = thesisPoints.size() > 5 ? 5 : thesisPoints.size();

            for (int i = 0; i < maxBullets; i++) {
                String str = i < thesisPoints.size() ? thesisPoints.get(i) : " ";

                XWPFParagraph pThesis = doc.createParagraph();
                XWPFRun rThesis = pThesis.createRun();
                rThesis.setFontFamily(FONT_FAMILY);
                rThesis.setFontSize(8);
                rThesis.setText(str);
                pThesis.setNumID(numId);
                pThesis.setSpacingAfter(30);

                //We remove/set the indentation for bullets if the text wraps to mroe than 1 line
                pThesis.setIndentationLeft(400);
                pThesis.setIndentationFirstLine(-400);

            }


            /*
             * Formatting look and feel line
             * Add a line break after the last bullet for spacing fewer than 3 bullets
             */
            if (thesisPoints.size() < 2) {
                XWPFParagraph pThesisSpacing = doc.createParagraph();
                XWPFRun rThesisSpacing = pThesisSpacing.createRun();
                pThesisSpacing.setSpacingAfter(0);
                rThesisSpacing.setText(" ");
                rThesisSpacing.setFontFamily(FONT_FAMILY);
                rThesisSpacing.setFontSize(7);
            }


            /*	Section 4 - Chart
             * 	Includes:
             * 	 Company Price - Axis 1
             * 	 Index Price - Axis 2
             *
             * 	 Chart measured in EMU - 1 inch = 914400 emu
             */


            //Create the chart elements and arrays
            String chartIndex = compSummary.getCountryLoc().equals("US-Canada") ? "SP500" : "MSCI";
            //String chartTitle = "Price and Price Relative to " + chartIndex + ".";
            String chartTitle = "Price and Index Values.";
            chartTitle = chartTitle + " Current Price = " + printedPrice;

            String[] chartSeries = new String[3];
            chartSeries[0] = "Date";
            chartSeries[1] = compSummary.getCompanyTicker() + " (left)";
            chartSeries[2] = chartIndex + " (right)";


            //Get the Time Series
            List<TimeSeries.Entry> compPriceTs = compPrice.getValues();
            List<TimeSeries.Entry> compIndexTs = compIndex.getValues();

            /*
             * We turn each time series into lists with the values
             * 	1. dates used; 2. Company Price; 3. Index Values
             *
             */
            Map<String, BigDecimal> indexMap = new HashMap<String, BigDecimal>();
            compIndexTs.forEach(e -> {
                LocalDateTime d = e.getTimestamp();
                indexMap.put(Timestamp.valueOf(d).toString(), e.getValue());
            });

            List<String> dates = new ArrayList<String>();
            List<BigDecimal> compPrices = new ArrayList<BigDecimal>();
            List<BigDecimal> indexVals = new ArrayList<BigDecimal>();

            /*
             * latestVal is the "latest value" of the Index.
             * This is used when there is a Company Price Date but no corresponding IndexValue on the same Date
             *
             */
            BigDecimal latestVal = compIndexTs.get(0).getValue();

            for (TimeSeries.Entry entry : compPriceTs) {
                String day = Timestamp.valueOf(entry.getTimestamp()).toString();
                Calendar cal = Calendar.getInstance();
                cal.setTime(Timestamp.valueOf(entry.getTimestamp()));
                String yr = String.valueOf(cal.get(Calendar.YEAR));

                dates.add(yr);
                compPrices.add(entry.getValue());

                if (indexMap.containsKey(day)) {
                    //if there is a value, reset the latestVal to current value
                    latestVal = indexMap.get(day);
                    indexVals.add(latestVal);
                } else {
                    //if the day is missing, add the latest value latestVal
                    indexVals.add(latestVal);
                }

            }


            /*
             * 	Create the chart
             */
            try {

                //Measured in EMU; 914400EMU to 1"
                XWPFChart prChart = doc.createChart(6858000, 1828800);

                //data arrays
                String[] categories = dates.toArray(new String[dates.size()]);
                BigDecimal[] values1 = compPrices.toArray(new BigDecimal[compPrices.size()]);
                BigDecimal[] values2 = indexVals.toArray(new BigDecimal[indexVals.size()]);

                XDDFChartAxis bottomAxis = prChart.createCategoryAxis(AxisPosition.BOTTOM);
                bottomAxis.setMajorTickMark(AxisTickMark.NONE);

                XDDFValueAxis leftAxis = prChart.createValueAxis(AxisPosition.LEFT);
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
                leftAxis.setMajorTickMark(AxisTickMark.OUT);
                Double axisMin = Collections.min(compPrices).setScale(0, RoundingMode.FLOOR).setScale(-1, RoundingMode.FLOOR).doubleValue();
                leftAxis.setMinimum(axisMin);

                XDDFShapeProperties gridLines = leftAxis.getOrAddMajorGridProperties();
                XDDFSolidFillProperties gridFill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.GRAY));
                XDDFLineProperties gridLine = new XDDFLineProperties();
                gridLine.setFillProperties(gridFill);
                gridLines.setLineProperties(gridLine);


                final int numOfPoints = categories.length;
                final String categoryDataRange = prChart.formatRange(new CellRangeAddress(1, numOfPoints, 0, 0));
                final String valuesDataRange = prChart.formatRange(new CellRangeAddress(1, numOfPoints, 1, 1));
                final String valuesDataRange2 = prChart.formatRange(new CellRangeAddress(1, numOfPoints, 2, 2));

                final XDDFDataSource<?> categoriesData = XDDFDataSourcesFactory.fromArray(categories, categoryDataRange, 0);
                final XDDFNumericalDataSource<? extends Number> valuesData = XDDFDataSourcesFactory.fromArray(values1, valuesDataRange, 1);
                final XDDFNumericalDataSource<? extends Number> valuesData2 = XDDFDataSourcesFactory.fromArray(values2, valuesDataRange2, 2);

                XDDFLineChartData line = (XDDFLineChartData) prChart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
                XDDFLineChartData.Series series = (XDDFLineChartData.Series) line.addSeries(categoriesData, valuesData);
                series.setTitle(chartSeries[1], null);
                series.setSmooth(true);
                series.setMarkerStyle(MarkerStyle.NONE);
                solidLineSeries(series, PresetColor.DARK_BLUE);

                prChart.plot(line);
                prChart.displayBlanksAs(DisplayBlanks.GAP);

                bottomAxis = prChart.createCategoryAxis(AxisPosition.BOTTOM);
                bottomAxis.setVisible(false);

                /*
                 * The series on the right axis actually has its' own bottom axis
                 * however, this bottom axis is invisible
                 */
                XDDFValueAxis rightAxis = prChart.createValueAxis(AxisPosition.RIGHT);
                rightAxis.setCrosses(AxisCrosses.MAX);
                rightAxis.setMajorTickMark(AxisTickMark.OUT);
                axisMin = Collections.min(indexVals).setScale(0, RoundingMode.FLOOR).setScale(-2, RoundingMode.FLOOR).doubleValue();
                rightAxis.setMinimum(axisMin);

                gridLines = rightAxis.getOrAddMajorGridProperties();
                gridFill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.LIGHT_GRAY));
                gridLine = new XDDFLineProperties();
                gridLine.setFillProperties(gridFill);
                gridLines.setLineProperties(gridLine);

                //Set Axis cross for right and new bottom
                bottomAxis.crossAxis(rightAxis);
                rightAxis.crossAxis(bottomAxis);

                line = (XDDFLineChartData) prChart.createData(ChartTypes.LINE, bottomAxis, rightAxis);
                series = (XDDFLineChartData.Series) line.addSeries(categoriesData, valuesData2);
                series.setTitle(chartSeries[2], null);
                series.setSmooth(false);
                series.setMarkerStyle(MarkerStyle.NONE);

                prChart.plot(line);

                //This is a bug from POI 4.0.1
                //Add index values
                prChart.getCTChart().getPlotArea().getLineChartArray(1).getSerArray(0).getIdx().setVal(1);
                prChart.getCTChart().getPlotArea().getLineChartArray(1).getSerArray(0).getOrder().setVal(1);

                solidLineSeries(series, PresetColor.DARK_GRAY);

                XDDFChartLegend legend = prChart.getOrAddLegend();
                legend.setPosition(LegendPosition.TOP);
                legend.setOverlay(true);

                prChart.setTitleText(chartTitle);
                prChart.setTitleOverlay(false);


                //remove rounded corners
                //https://stackoverflow.com/questions/44255527/changing-the-shape-of-chart-generated-by-apache-poi-for-excel-sheet/44265659#44265659
                if (prChart.getCTChartSpace().getRoundedCorners() == null)
                    prChart.getCTChartSpace().addNewRoundedCorners();
                prChart.getCTChartSpace().getRoundedCorners().setVal(false);

                //make the series lines thinner to be read easier --- 12700 = 1pt
                CTShapeProperties ctSP = prChart.getCTChart().getPlotArea().getLineChartArray(0).getSerArray(0).getSpPr();
                ctSP.getLn().setW(9525);

                ctSP = prChart.getCTChart().getPlotArea().getLineChartArray(1).getSerArray(0).getSpPr();
                ctSP.getLn().setW(9525);


                //set the tick spacing so the bottom axis has fewer points
                prChart.getCTChart().getPlotArea().getCatAxArray(0).addNewTickLblSkip().setVal(255);

                //Format the Category axis for font and size
                CTTextBody ctTxPr = prChart.getCTChart().getPlotArea().getCatAxArray(0).addNewTxPr();
                CTTextBodyProperties ctTBP = ctTxPr.addNewBodyPr();
                CTTextCharacterProperties ctCPP = ctTxPr.addNewP().addNewPPr().addNewDefRPr();
                ctCPP.setSz(700);
                ctCPP.addNewLatin().setTypeface(FONT_FAMILY);
                ctCPP.addNewCs().setTypeface(FONT_FAMILY);

                //Format Value Axes 1
                ctTxPr = prChart.getCTChart().getPlotArea().getValAxArray(0).addNewTxPr();
                ctTBP = ctTxPr.addNewBodyPr();
                ctCPP = ctTxPr.addNewP().addNewPPr().addNewDefRPr();
                ctCPP.setSz(700);
                ctCPP.addNewLatin().setTypeface(FONT_FAMILY);
                ctCPP.addNewCs().setTypeface(FONT_FAMILY);

                //Format Value Axes 2
                ctTxPr = prChart.getCTChart().getPlotArea().getValAxArray(1).addNewTxPr();
                ctTBP = ctTxPr.addNewBodyPr();
                ctCPP = ctTxPr.addNewP().addNewPPr().addNewDefRPr();
                ctCPP.setSz(700);
                ctCPP.addNewLatin().setTypeface(FONT_FAMILY);
                ctCPP.addNewCs().setTypeface(FONT_FAMILY);


                //Format the Title
                //Sz is 1/100 of point --- https://stackoverflow.com/questions/50418856/apache-poi-charts-title-formatting
                prChart.getCTChart().getTitle().getTx().getRich().getPArray(0).getRArray(0).getRPr().setSz(800);
                prChart.getCTChart().getTitle().getTx().getRich().getPArray(0).getRArray(0).getRPr().addNewLatin().setTypeface(FONT_FAMILY);
                prChart.getCTChart().getTitle().getTx().getRich().getPArray(0).getRArray(0).getRPr().addNewCs().setTypeface(FONT_FAMILY);

                //Move the title to the top left corner
                CTManualLayout ctML = prChart.getCTChart().getTitle().getLayout().addNewManualLayout();
                ctML.addNewXMode().setVal(STLayoutMode.EDGE);
                ctML.addNewYMode().setVal(STLayoutMode.EDGE);
                //Manual Layout vals are basically %ages
                ctML.addNewX().setVal(0.02);
                ctML.addNewY().setVal(0.02);
                ctML.addNewW().setVal(0.99);
                ctML.addNewH().setVal(0.99);


                //format the legend
                ctTxPr = prChart.getCTChart().getLegend().addNewTxPr();
                ctTBP = ctTxPr.addNewBodyPr();
                ctCPP = ctTxPr.addNewP().addNewPPr().addNewDefRPr();
                ctCPP.setSz(700);
                ctCPP.addNewLatin().setTypeface(FONT_FAMILY);
                ctCPP.addNewCs().setTypeface(FONT_FAMILY);

                ctML = prChart.getCTChart().getLegend().addNewLayout().addNewManualLayout();
                ctML.addNewXMode().setVal(STLayoutMode.EDGE);
                ctML.addNewYMode().setVal(STLayoutMode.EDGE);
                ctML.addNewX().setVal(0.0001);
                ctML.addNewY().setVal(0.1);
                ctML.addNewW().setVal(0.25);
                ctML.addNewH().setVal(0.06);


                //Move the plot
                ctML = prChart.getCTChart().getPlotArea().addNewLayout().addNewManualLayout();
                ctML.addNewLayoutTarget().setVal(STLayoutTarget.INNER);
                ctML.addNewXMode().setVal(STLayoutMode.EDGE);
                ctML.addNewYMode().setVal(STLayoutMode.EDGE);
                ctML.addNewX().setVal(0.06);
                ctML.addNewY().setVal(0.18);
                ctML.addNewW().setVal(0.95);
                ctML.addNewH().setVal(0.75);


            } catch (Exception e) {/*System.out.println(e.getStackTrace());*/}


            /*	Section 5 - Historic Data Table
             * 	Includes:
             * 	 Initial headers with currency and FY End Date
             * 	 Historic Data Table
             * 	 Estimates
             *
             */

            XWPFParagraph histPTable = doc.createParagraph();
            histPTable.setSpacingAfter(10);
            XWPFRun rHistPTable = histPTable.createRun();

            // We reversed the Currency and FY End Date so we need to create this run
            // then set currency, then set date
            // Goto line 828
            // Requirement from DCIP-1374

            /*
             *	Make the initial table
             * 	Skip column 1 to be used for column headers
             *
             */

            int initialColsToSkip = 1;
            XWPFTable histTable = doc.createTable(1, (initialColsToSkip + 1));

            Map<String, List<String>> historicData = compSummary.getHistoricData();
            histTable.getCTTbl().getTblPr().addNewTblStyle().setVal("StyledTable");
            histTable.setInsideHBorder(XWPFBorderType.SINGLE, 1, 0, "ffffff");
            histTable.setInsideVBorder(XWPFBorderType.SINGLE, 1, 0, "ffffff");
            histTable.setWidth(10800);
            int tableFontSize = 7;

            /*
             * 	Setup the table with proper columns
             * 	This pattern is repeated in addDataTableRow()
             */
            XWPFTableRow row = histTable.getRows().get(0);
            List<String> rowVals = historicData.get("fiscalyear");

            // This is used if we have >Max # of estimate years
            Long lastReportYear = Long.valueOf(rowVals.get(rowVals.size() -1)) + MAX_ESTIMATE_YEARS;

            /*
             *	If there are estimates, then add extra years for estimates
             *	These will be populated last but we want to make all columns
             *  Sorting will be easier to add later
             */

            if (estimateYears.size() > 0) {
                List<String> a = new ArrayList<>(estimateYears);
                Collections.sort(a);
                rowVals.addAll(a);

                // Check if there are any missing years and back fill
                // E.g. Historic ends at 2018, Estimate for 2020 but none for 2019

                // Get the estimate currency
                String firstEstYear = estimateYears.stream().findFirst().get();
                String estCurrency = firstEstYear.contains("(") ? firstEstYear.substring(firstEstYear.indexOf('(') -1 , firstEstYear.length()) : null;
                List<Integer> miaYrs = rowVals.stream()
                                            .map(yr -> Integer.parseInt(yr.replaceAll("[^0-9]", ""), 10))
                                            .collect(Collectors.toList());

                // Find Missing in Action Values
                List<String> mia = new ArrayList();
                Integer eFirst = miaYrs.get(0);
                Integer eLast = miaYrs.get(miaYrs.size() - 1);
                for(Integer i = eFirst+1; i<eLast; i++){
                    if(!miaYrs.contains(i)){
                        String m = estCurrency==null ? i.toString() : i.toString() + estCurrency;
                        mia.add(m);
                    }
                }

                // If we have any missing values, add them to both sets
                if(mia.size() > 0){
                    estimateYears.addAll(mia);
                    rowVals.addAll(mia);
                    Collections.sort(rowVals);
                }
            }


            XWPFParagraph pTable = row.getTableCells().get(0).getParagraphs().get(0);
            pTable.setSpacingAfter(0);
            pTable.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun rTable = pTable.createRun();
            rTable.setText("Section");
            rTable.setFontFamily(FONT_FAMILY);
            rTable.setFontSize(tableFontSize);
            rTable.setBold(true);

            pTable = row.getTableCells().get(initialColsToSkip).getParagraphs().get(0);
            pTable.setSpacingAfter(0);
            pTable.setAlignment(ParagraphAlignment.CENTER);
            rTable = pTable.createRun();
            rTable.setText("Item");
            rTable.setFontFamily(FONT_FAMILY);
            rTable.setFontSize(tableFontSize);
            rTable.setBold(true);
            for (int i = initialColsToSkip; i < (rowVals.size() + initialColsToSkip); i++) {
                XWPFTableCell cell = row.createCell();
                pTable = cell.getParagraphs().get(0);
                pTable.setSpacingAfter(0);
                pTable.setAlignment(ParagraphAlignment.LEFT);
                rTable = pTable.createRun();
                rTable.setText(rowVals.get(i - initialColsToSkip));
                rTable.setBold(true);
                rTable.setFontFamily(FONT_FAMILY);
                rTable.setFontSize(7);
            }


            /*
             * 	Get currency info
             */
            rowVals = historicData.get("CurrencyCode");

            //How many currencies are in the table?
            List<String> uniqCur = rowVals.stream().distinct().collect(Collectors.toList());
            String currentCur = new String();
            if (uniqCur.size() > 1) {
                //Reset the column headers
                row = histTable.getRows().get(0);
                for (int i = initialColsToSkip; i < (rowVals.size() + initialColsToSkip); i++) {
                    XWPFRun curRun = row.getTableCells().get(i + 1).getParagraphs().get(0).getRuns().get(0);
                    String modCur = rowVals.get(i - initialColsToSkip).charAt(0) == 'c' ? rowVals.get(i - initialColsToSkip).substring(1) : rowVals.get(i - initialColsToSkip);
                    String curSym = CurrencyUtil.getCurrencySymbol(modCur);

                    curSym = getCurAndSym(curSym, modCur);
                    currentCur = curSym;
                    curRun.setText(" (" + curSym + ")");
                    curRun.setFontFamily(FONT_FAMILY);
                    curRun.setFontSize(7);
                    curRun.setBold(true);
                }
                //Display the warning
                rHistPTable.setText("Reporting Currency has changed YOY. ");
                rHistPTable.setFontFamily(FONT_FAMILY);
                rHistPTable.setFontSize(7);
                rHistPTable.setBold(true);
            } else {
                //This section should go away when we finish currency in DCIP-?
                if (uniqCur.get(0).charAt(0) == 'c') {
                    rHistPTable.setText("Currency: ");
                    rHistPTable.setFontFamily(FONT_FAMILY);
                    rHistPTable.setFontSize(7);
                    rHistPTable.setBold(true);
                    rHistPTable = histPTable.createRun();

                    String modCurCode = uniqCur.get(0).substring(1);
                    String curSym = CurrencyUtil.getCurrencySymbol(modCurCode);
                    curSym = getCurAndSym(curSym, modCurCode);
                    rHistPTable.setText(curSym + ". ");
                    rHistPTable.setFontFamily(FONT_FAMILY);
                    rHistPTable.setFontSize(7);
                    rHistPTable.setBold(true);

                    rHistPTable = histPTable.createRun();
                    rHistPTable.setText("If currency is unexpectedly in $USD, this reflects the difference in Reporting currency and Trading currency.");
                    rHistPTable.setFontFamily(FONT_FAMILY);
                    rHistPTable.setFontSize(7);
                } else {
                    rHistPTable.setText("Currency: ");
                    rHistPTable.setFontFamily(FONT_FAMILY);
                    rHistPTable.setFontSize(7);
                    rHistPTable.setBold(true);
                    rHistPTable = histPTable.createRun();

                    String curSym = CurrencyUtil.getCurrencySymbol(uniqCur.get(0));
                    curSym = getCurAndSym(curSym, uniqCur.get(0));
                    rHistPTable.setText(curSym + ". ");
                    rHistPTable.setFontFamily(FONT_FAMILY);
                    rHistPTable.setFontSize(7);
                    rHistPTable.setBold(true);
                }
            }

            String histTableLanguage = "FY End Date: ";
            rHistPTable.setText(histTableLanguage);
            rHistPTable.setBold(true);
            rHistPTable.setFontFamily(FONT_FAMILY);
            rHistPTable.setFontSize(7);
            rHistPTable.setItalic(true);

            rHistPTable = histPTable.createRun();
            rHistPTable.setText(compSummary.getFiscalYearEnd() + ". ");
            rHistPTable.setItalic(true);
            rHistPTable.setBold(true);
            rHistPTable.setFontFamily(FONT_FAMILY);
            rHistPTable.setFontSize(7);



            /*
             * Add Rows to the Table
             * Iterate through the KeyList to
             * 	a) find the calculated fields
             *  b) pull keys by sort order
             *  c) find estimates if they exist (estimateModels)
             */

            List<String> calcFields = new ArrayList<String>();
            List<String> keys = new ArrayList<String>(historicData.keySet());

            for (String key : keys) {
                //if the field was calculated;
                // add to calc list + rename the key
                if (key.charAt(0) == 'c') {
                    calcFields.add(key.substring(1));
                    historicData.put(key.substring(1), historicData.remove(key));
                }
            }

            // 	Add items to the table according to SortOrder
            for (String item : FIN_TABLE_SORT_ORDER) {
                row = histTable.createRow();
                rowVals = historicData.get(item);
                String headerCell = WHITE_SPACE_CHAR;

                // if this specific row has a section header, get it
                if (FIN_TABLE_HEADERS.keySet().contains(item)) {
                    headerCell = FIN_TABLE_HEADERS.get(item);
                }

                addDataTableRow(initialColsToSkip, row, item, rowVals, headerCell, tableFontSize);
            }


            /*
             *  Add Estimates (and fix currency)
             *  if(currentCur != null) then there is a currencyChange
             */

            List<String> estYears = new ArrayList<>(estimateYears);
            Collections.sort(estYears);
            List<String> histYears = historicData.get("fiscalyear");
            histYears.removeAll(estYears);

//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
//            LocalDate fyEnd = LocalDate.parse(compSummary.getFiscalYearEnd() + "-9999", formatter);

            if (estimateYears.size() > 0) {
                for (XWPFTableRow tblRow : histTable.getRows()) {
                    String itemName = tblRow.getCell(initialColsToSkip).getParagraphs().get(0).getRuns().get(0).getText(0);

                    //  fix the currency for estimates in top row
                    //  fix the style of the year
                    if (itemName == "Item") {
                        for (XWPFTableCell cell : tblRow.getTableCells()) {
                            XWPFRun r = cell.getParagraphs().get(0).getRuns().get(0);
                            String yr = r.getText(0);
                            if (estYears.indexOf(yr) > -1) {
                                if (!currentCur.isEmpty()) {
                                    yr = yr + " (" + currentCur + ") ";
                                    r.setText(yr, 0);

                                }
                                r.setBold(true);
                                r.setColor("FF4081");
                            }
                        }
                    }

                    //add the estimate
                    Long seriesId = FIN_EST_SERIES_MAPPING.get(itemName);
                    Map<LocalDateTime, BigDecimal> eMap = estimateMap.get(seriesId);
                    if (eMap != null && eMap.entrySet() != null && !eMap.entrySet().isEmpty() && !eMap.isEmpty()) {
                        eMap.forEach((k, v) -> {
                            if (k.getHour() == 0 && k.getMinute() == 0 && k.getSecond() == 0
                                    && k.getDayOfMonth() == fyEnd.getDayOfMonth() && k.getMonth() == fyEnd.getMonth()
                                    && k.getYear() <= lastReportYear.intValue()) {

                                int colPos = estYears.indexOf(String.valueOf(k.getYear()));
                                XWPFTableCell c = tblRow.getTableCells().get(histYears.size() + colPos + 2);
                                XWPFParagraph p = c.getParagraphs().get(0);
                                XWPFRun r = p.createRun();
                                r.setText(formatEstimateEntry(itemName, v.toString(), true));

                                p.setAlignment(ParagraphAlignment.LEFT);
                                p.setSpacingAfter(0);
                                r.setFontFamily(FONT_FAMILY);
                                r.setFontSize(tableFontSize);
                                r.setColor("000000");
                                r.setBold(true);
                            }
                        });
                    }

                    // Add calculation for estimate years
                    // RowName is a calculated field
                    // Pull data from estimateMap and historicData
                    if (calcFields.contains(itemName)) {
                        // Get Calculation and Fill in Dependency Values

                        //Calc = estimateCalculations.stream().filter(f -> f.getFieldName().equals(itemName)).findFirst().ifPresent(this::EvaluateCalculations())
                        Calculation calc = estimateCalculations
                                .stream()
                                .filter(f -> f.getFieldName().equals(itemName))
                                .findFirst().orElse(null);

                        if (calc != null) {
                            List<Dependency> deps = calc.getDependencys();
                            for (String yr : estYears) {
                                // Get all dependency values
                                deps.forEach(d -> {
                                    d.setDependencyValue(getDependencyValue(estimateMap, historicData, estYears,
                                                                d.getDependencyName(), d.getRelativeYear(), yr));
                                });

                                // Evaluate the Expression
                                calc.setValue(evalCalc(calc));

                                // Fill in the estimate (with formatting!)
                                String calcVal = calc.getValue();
                                if (calcVal == null || calcVal.equals("")) {
                                    calcVal = "-";
                                }else{
                                    calcVal = formatEstimateEntry(itemName, calcVal, true);
                                }
                                int colPos = estYears.indexOf(yr);
                                XWPFTableCell c = tblRow.getTableCells().get(histYears.size() + colPos + 2);
                                XWPFParagraph p = c.getParagraphs().get(0);
                                XWPFRun r = p.createRun();
                                r.setText(calcVal);

                                p.setAlignment(ParagraphAlignment.LEFT);
                                p.setSpacingAfter(0);
                                r.setFontFamily(FONT_FAMILY);
                                r.setFontSize(tableFontSize);
                                r.setBold(true);
                                r.setColor("FF4081");
                            }
                        }
                    }

                    // There are a few more rows that are not calculations and are not estimates
                    // Non-GAAP EPS and Levered Free Cash Flow
                    // Format these cells
                    if (seriesId == null && !calcFields.contains(itemName)) {
                        for (String yr : estYears) {
                            int colPos = estYears.indexOf(yr);
                            XWPFTableCell c = tblRow.getTableCells().get(histYears.size() + colPos + 2);
                            XWPFParagraph p = c.getParagraphs().get(0);
                            XWPFRun r = p.createRun();
                            r.setText(WHITE_SPACE_CHAR);

                            p.setAlignment(ParagraphAlignment.LEFT);
                            p.setSpacingAfter(0);
                            r.setFontFamily(FONT_FAMILY);
                            r.setFontSize(tableFontSize);
                            r.setColor("ffffff");

                        }
                    }
                }
            }




            /*
             * Format calculated cells
             * This will change when we integrate Estimates
             */
            for (XWPFTableRow tblRow : histTable.getRows()) {
                String itemName = tblRow.getCell(initialColsToSkip).getParagraphs().get(0).getRuns().get(0).getText(0);
                if (calcFields.contains(itemName)) {
                    //Skip the Section and Item
                    for (int i = (initialColsToSkip + 1); i < tblRow.getTableCells().size(); i++) {
                        XWPFTableCell tblCell = tblRow.getTableCells().get(i);
                        XWPFRun tblRun;
                        if (tblCell.getParagraphs().get(0).getRuns().size() <= 0) {
                            tblRun = tblCell.getParagraphs().get(0).createRun();
                        } else {
                            tblRun = tblCell.getParagraphs().get(0).getRuns().get(0);
                        }

                        tblRun.setItalic(true);
                        tblRun.setColor("6658E5");
                    }
                }else{
                    for(int i = 0; i < tblRow.getTableCells().size(); i++){
                        XWPFTableCell tblCell = tblRow.getTableCells().get(i);
                        XWPFParagraph tblP = tblCell.getParagraphs().get(0);
                        XWPFRun tblRun;
                        if (tblCell.getParagraphs().get(0).getRuns().size() <= 0) {
                            tblRun = tblP.createRun();
                            tblRun.setText(WHITE_SPACE_CHAR);
                        } else {
                            tblRun = tblP.getRuns().get(0);
                        }

                        tblP.setSpacingAfter(0);
                        tblRun.setFontFamily(FONT_FAMILY);
                        tblRun.setFontSize(tableFontSize);
                    }
                }
            }


            /*
             * Add the table footNote
             */
            XWPFParagraph pFootNote = doc.createParagraph();
            pFootNote.setSpacingAfter(0);
            XWPFRun rFootNote = pFootNote.createRun();
            String footNote = "Historic Data Fetched from S&P Xpressfeed as of ";
            footNote = footNote + DateUtil.format(LocalDateTime.now(), HEADER_DATE_PATTERN).toString() + ". ";
            rFootNote.setText(footNote);
            rFootNote.setFontFamily(FONT_FAMILY);
            rFootNote.setFontSize(7);
            rFootNote = pFootNote.createRun();
            footNote = "Blue and Italic Data is Calculated from Xpressfeed DataItems";
            rFootNote.setText(footNote);
            rFootNote.setFontFamily(FONT_FAMILY);
            rFootNote.setFontSize(7);
            rFootNote.setColor("6658E5");
            rFootNote.setItalic(true);


            /*
             * 	Final Document Formatting
             * 	1. If we did not change default spacingAfter on a paragraph, change it
             *  2. If we did not change default spacingBefore on a paragraph, change it
             *
             */
            List<XWPFParagraph> list = doc.getParagraphs();
            XWPFParagraph p;
            for (int i = 0; i < list.size(); i++) {
                p = list.get(i);
                if (p.getSpacingAfter() == -1) {
                    p.setSpacingAfter(30);
                }
                if (p.getSpacingBefore() == -1) {
                    p.setSpacingBefore(0);
                }
            }

            /*
             * long endTime = System.nanoTime();
             * long duration = (endTime - startTime)/1000000;
             * System.out.println(duration);
             */

            try (OutputStream os = response.getOutputStream()) {
                doc.write(os);
            }
        }
    }


    public String evalCalc(Calculation c) {
        String f = c.getFormula();
        int nullDeps = 0;
        for (Dependency d : c.getDependencys()) {
            Long l = d.getCalculationOrder();
            String v = d.getDependencyValue() == null ? "" : d.getDependencyValue().toString();
            nullDeps = d.getDependencyValue() == null ? (nullDeps + 1) : nullDeps;

            f = f.replace(String.format("dependency%s", l.toString()), v);
        }

        String eval = new String("");
        ExpressionParser parser = new SpelExpressionParser();
        try {
            Expression exp = parser.parseExpression(f);
            eval = String.valueOf(exp.getValue());
        } catch (Exception e) {
        }

        if(nullDeps > 0) {return "";}
        return eval;
    }

    public BigDecimal getDependencyValue(Map<Long, Map<LocalDateTime, BigDecimal>> estimates, Map<String, List<String>> historicData, List<String> estYears,
                                         String depName, Long relYear, String inputYear) {

        // Two options
        //  1- the needed year is an estimate
        //  2- the needed year is historic data

        BigDecimal val = new BigDecimal("1");
        int newYr = Integer.parseInt(inputYear) + relYear.intValue();

        if(estYears.contains(String.valueOf(newYr))){
            Long seriesId = FIN_EST_SERIES_MAPPING.get(depName);

            /*
             * If the dependency series is null, the stream() fails with NPE
             */
            if(estimates.get(seriesId) != null) {
                val = estimates.get(seriesId)
                        .entrySet().stream()
                        .filter(l -> l.getKey().getYear() == newYr)
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()))
                        .entrySet().stream().findFirst().map(Map.Entry::getValue).orElse(null);
            }else{
                val = null;
            }
        }else{
            List<String> rVals = historicData.get("fiscalyear");
            rVals.removeAll(estYears);
            int col = rVals.indexOf(String.valueOf(newYr));
            String t = historicData.get(depName).get(col);
            /*
             * Historic data may be null!
             * E.g. Div/Share = null last year, but it goes into a calc
             */
            if(t == null || t.equals("null")){
                val = null;
            }else{
                val = new BigDecimal(historicData.get(depName).get(col));
            }
        }

        return val;
    }

    public String formatEstimateEntry(String itemName, String entry, boolean estimate) {
        Long digitsNeeded = FIN_TABLE_DECIAMLS.containsKey(itemName) ? FIN_TABLE_DECIAMLS.get(itemName) : 1L;
        String valEnding = FIN_TABLE_FORMAT_ENDING.containsKey(itemName) ? FIN_TABLE_FORMAT_ENDING.get(itemName) : "";
        BigDecimal val = new BigDecimal(entry);

        // Estimate % are saved as 0.11 instead of 11%
        if (valEnding.equals("%") && estimate) {
            val = val.scaleByPowerOfTen(2);
        }
        return (roundStr(val.toString(), digitsNeeded.intValue()) + valEnding);
    }

    public String formatTableEntry(String itemName, String entry) {
        Long digitsNeeded = FIN_TABLE_DECIAMLS.containsKey(itemName) ? FIN_TABLE_DECIAMLS.get(itemName) : 1L;
        String valEnding = FIN_TABLE_FORMAT_ENDING.containsKey(itemName) ? FIN_TABLE_FORMAT_ENDING.get(itemName) : "";
        return (roundStr(entry, digitsNeeded.intValue()) + valEnding);
    }

    public static void addDataTableRow(int initialColsToSkip, XWPFTableRow row, String key, List<String> values, String header, int fontSize) {
        XWPFParagraph pTable = row.getTableCells().get(0).getParagraphs().get(0);
        pTable.setSpacingAfter(0);
        pTable.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun rTable = pTable.createRun();
        rTable.setText(header);
        rTable.setFontFamily(FONT_FAMILY);
        rTable.setFontSize(fontSize);
        rTable.setBold(true);
        if (header.equals(WHITE_SPACE_CHAR)) rTable.setColor("ffffff");

        pTable = row.getTableCells().get(initialColsToSkip).getParagraphs().get(0);
        pTable.setSpacingAfter(0);
        pTable.setAlignment(ParagraphAlignment.LEFT);
        rTable = pTable.createRun();
        rTable.setText(key);
        rTable.setFontFamily(FONT_FAMILY);
        rTable.setFontSize(fontSize);
        rTable.setBold(true);

        for (int i = initialColsToSkip; i < (values.size() + initialColsToSkip); i++) {
            XWPFTableCell cell = row.getTableCells().get(i + 1);
            pTable = cell.getParagraphs().get(0);
            pTable.setAlignment(ParagraphAlignment.LEFT);
            pTable.setSpacingAfter(0);
            rTable = pTable.createRun();
            rTable.setFontFamily(FONT_FAMILY);
            rTable.setFontSize(fontSize);

            String value = values.get(i - initialColsToSkip);



            if (value != null) {

                Float valueFloat = Math.abs(Float.parseFloat(value));
                String valFloat = valueFloat.toString();
                int valDigits = valFloat.substring(0, valFloat.indexOf('.')).length();
                String valEnding = FIN_TABLE_FORMAT_ENDING.containsKey(key) ? FIN_TABLE_FORMAT_ENDING.get(key) : "";
                Long digitsNeeded = 0L;

                // If it is not 'x' and not '%' and abs()>100, then no decimals
                if (valEnding == "" && valDigits > 2) {
                    rTable.setText(roundStr(value, digitsNeeded.intValue()) + valEnding);
                } else {
                    digitsNeeded = FIN_TABLE_DECIAMLS.containsKey(key) ? FIN_TABLE_DECIAMLS.get(key) : 2L;
                    rTable.setText(roundStr(value, digitsNeeded.intValue()) + valEnding);
                }

            } else {
                rTable.setText("-");
            }
        }

        if (!header.equals(WHITE_SPACE_CHAR)) addTopCellBorder(row);

    }

    public static void addTopCellBorder(XWPFTableRow row) {

        //Adding a top black boarder to the rows with new section header
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.getCTTc().addNewTcPr().addNewTcBorders();
            CTBorder cellTop = cell.getCTTc().getTcPr().getTcBorders().addNewTop();

            cellTop.setVal(STBorder.SINGLE);
            cellTop.setColor("000000");
            cellTop.setSz(BigInteger.valueOf(5));
        }

    }


    public XWPFRun createParagraph(String data, String lineBreak, XWPFParagraph p) {
        XWPFRun run = p.createRun();

        if (data.contains(lineBreak)) {
            String[] lines = data.split(lineBreak);
            run.setText(lines[0], 0); // set first line into XWPFRun
            for (int i = 1; i < lines.length; i++) {
                // add break and insert new text
                run.addBreak();
                run.setText(lines[i]);
            }
        } else {
            run.setText(data, 0);
        }

        return run;
    }

    public XWPFTable createHeaderTable(Map<String, String> data, int rows, int cols, XWPFDocument doc) {
        XWPFTable table = doc.createTable(rows, cols);
        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                //Format the Cells
                XWPFParagraph p = table.getRow(i).getCell(j).getParagraphs().get(0);
                XWPFRun r = p.createRun();
                p.setSpacingAfter(0);
                r.setFontFamily(FONT_FAMILY);
                r.setFontSize(8);
                r.setColor("000000");
                r.setItalic(true);

                XWPFParagraph p2 = table.getRow(i).getCell(j + 1).getParagraphs().get(0);
                XWPFRun r2 = p2.createRun();
                p2.setSpacingAfter(0);
                r2.setFontFamily(FONT_FAMILY);
                r2.setFontSize(8);
                r2.setColor("6658E5");
                r.setItalic(true);

                //Set the Cells
                if (it.hasNext()) {
                    Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
                    r.setText((String) pair.getKey());
                    r2.setText((String) pair.getValue());
                } else {
                    r.setText(WHITE_SPACE_CHAR);
                    r.setColor("ffffff");
                    r.setItalic(true);
                    r2.setText(WHITE_SPACE_CHAR);
                    r2.setColor("ffffff");
                    r2.setItalic(true);
                }

                p.setAlignment(ParagraphAlignment.CENTER);
                p2.setAlignment(ParagraphAlignment.CENTER);

                //	Since we used the Key and Value we increment J
                j++;
            }
        }
        return table;
    }


    public static String roundStr(String str, int digits) {
        BigDecimal val = new BigDecimal(str);
        val = val.setScale(digits, RoundingMode.HALF_UP);

        String valStr = NumberFormat.getNumberInstance(Locale.US).format(val);
        return valStr;
    }

    private static void solidLineSeries(XDDFLineChartData.Series series, PresetColor color) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(color));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        series.setShapeProperties(properties);

    }

    public static String getCurAndSym(String curSym, String cur) {
        String str = "";

        //Currency Match RegEx
        Pattern pt = Pattern.compile("\\p{Sc}");
        Matcher match = pt.matcher(curSym);
        while (match.find()) {
            str = match.group();
        }

        if (str.length() == 0) {
            return cur;
        } else {
            return (str + cur);
        }
    }


}
