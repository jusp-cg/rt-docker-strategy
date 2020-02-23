package com.capgroup.dcip.webapi.controllers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.capgroup.dcip.webapi.controllers.journal.InvestmentActionController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;

import com.capgroup.dcip.app.journal.InvestmentActionModel;
import com.capgroup.dcip.app.journal.InvestmentActionService;
import com.capgroup.dcip.app.journal.InvestmentActionTypeModel;
import com.capgroup.dcip.app.journal.InvestmentTypeModel;
import com.capgroup.dcip.webapi.report.JournalReportGenerator;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ControllersTestConfig.class, loader = AnnotationConfigWebContextLoader.class)
@WebMvcTest(controllers = InvestmentActionController.class, secure = false)
@Import(InvestmentActionController.class)
public class InvestmentActionControllerTest {

    @MockBean
    InvestmentActionService investmentActionService;

    @MockBean
    JournalReportGenerator journalReportGenerator;

    @Autowired
    MockMvc mockMvc;

    private List<InvestmentTypeModel> investmentTypeList;

    private InvestmentActionTypeModel investmentActionType;

    @Before
    public void init() {
        InvestmentTypeModel investmentType1 = new InvestmentTypeModel();
        investmentType1.setId(34l);
        investmentType1.setName("it name");
        investmentType1.setDescription("it description");
        investmentType1.setDefaultFlag(true);

        InvestmentTypeModel investmentType2 = new InvestmentTypeModel();
        investmentType2.setId(35l);
        investmentType2.setName("it name");
        investmentType2.setDescription("it description");
        investmentType2.setDefaultFlag(false);

        investmentTypeList = Arrays.asList(investmentType1, investmentType2);
        investmentActionType = new InvestmentActionTypeModel();
        investmentActionType.setInvestmentAction("InvestmentActionTypeN");
    }

    @Test
    public void findAllWithParameters() throws Exception {
        given(investmentActionService.findAll(1L)).willReturn(new ArrayList<>());

        mockMvc.perform(get("/api/dcip/journal/investment-actions?profileId=1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getWithResults() throws Exception {
        InvestmentActionModel investmentActionModel = new InvestmentActionModel();
        investmentActionModel.setActionDate(LocalDateTime.now());
        investmentActionModel.setPublicComment("PublicComment");
        investmentActionModel.setOrderInfo("OrderInfo");
        investmentActionModel.setInvestmentTypes(investmentTypeList);
        investmentActionModel.setInvestmentActionType(investmentActionType);

        given(investmentActionService.findAll(1L)).willReturn(Arrays.asList(investmentActionModel));

        mockMvc.perform(get("/api/dcip/journal/investment-actions?profileId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testCsvReportExport() throws Exception {
        MockMultipartFile csvFile = new MockMultipartFile("csv", "InvestmentJournal.csv",
                "text/csv", "Record ID, Manager, Analyst, Fund, Stock, Investment Type, Public Comment, Order Info, Reason for Buying, Date".getBytes());
        doAnswer((Answer) invocation -> status().isOk()).when(journalReportGenerator).exportCsvReport(any(), any(), any());
        mockMvc.perform(get("/api/dcip/journal/investment-actions/125/downloadCsv"))
                .andExpect(status().isOk());
    }
}
