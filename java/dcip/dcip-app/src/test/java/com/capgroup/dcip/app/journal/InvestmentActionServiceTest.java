package com.capgroup.dcip.app.journal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.capgroup.dcip.app.alert.service.AlertSubscriptionEventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.app.reference.capital_system.AccountService;
import com.capgroup.dcip.app.reference.listing.ListingService;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionRepository;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(InvestmentActionServiceImpl.class)
public class InvestmentActionServiceTest {

    @MockBean
    InvestmentActionRepository investmentActionRepository;

    @MockBean
    InvestmentActionMapper investmentActionMapper;

    @MockBean
    EntityManager entityManager;

    @MockBean
    ListingService listingService;

    @MockBean
    AccountService accountService;

    @MockBean
    AlertSubscriptionEventService alertSubscriptionEventService;

    @Autowired
    private InvestmentActionService investmentActionService;

    private List<InvestmentType> investmentTypeList;

    private InvestmentActionType investmentActionType;

    private Profile profile;

    @Before
    public void init() {
        InvestmentType investmentType1 = new InvestmentType(34, "it name", "it description", true);
        InvestmentType investmentType2 = new InvestmentType(35, "it name", "it description", true);

        investmentTypeList = Arrays.asList(investmentType1, investmentType2);
        investmentActionType = new InvestmentActionType("InvestmentActionTypeN", 1, true);

        User user = new User(1L, "TEST_USER", "TEST_USER", new InvestmentUnit());
        Role role = new Role(1L, "TEST_ROLE", "TEST_ROLE");
        profile = new Profile(user, role);
    }

    @Test
    public void createTest() {
    	/*
        InvestmentAction investmentAction = new InvestmentAction(LocalDateTime.now(),
                "PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, "1L", false);

        investmentTypeList.forEach(investmentAction::addInvestmentType);

        InvestmentActionModel input = new InvestmentActionModel();
        InvestmentActionModel result = new InvestmentActionModel();

        AccountModel accountModel1 = new AccountModel();
        accountModel1.setId(1234);
        AccountModel accountModel2 = new AccountModel();
        accountModel2.setId(5678);
        input.setAccounts(Arrays.asList(accountModel1, accountModel2));

        ListingModel listingModel = new ListingModel();
        listingModel.setId("1L");
        input.setListing(listingModel);

        given(investmentActionMapper.fromInvestmentActioneModel(input)).willReturn(investmentAction);
        given(investmentActionRepository.save(investmentAction)).willReturn(investmentAction);
        given(investmentActionMapper.toInvestmentActionModel(investmentAction)).willReturn(result);	

        InvestmentActionModel output = investmentActionService.create(input);

        assertSame(result, output);*/
    }
    // Remove when upgrade to 2.0
    
/*
    @EnableApplicationRoleVirtualView
    public void readTest() {
        InvestmentAction investmentAction1 = new InvestmentAction(LocalDateTime.now(), "InvestmentActionName",
                "PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, false);

        investmentTypeList.forEach(investmentAction1::addInvestmentType);

        Stream<InvestmentAction> stream = Stream.of(investmentAction1, investmentAction1);

        InvestmentActionModel investmentActionModel1 = new InvestmentActionModel();
        investmentActionModel1.setListingModels(null);
        Stream<InvestmentActionModel> itmStream = Stream.of(investmentActionModel1, investmentActionModel1);

        given(investmentActionRepository.findByEventProfileId(1L)).willReturn(stream);
        given(listingService.findAlertTypeById("1234")).willReturn(Optional.empty());
        given(investmentActionMapper.toInvestmentActionModel(investmentAction1)).willReturn(investmentActionModel1);

        Stream<InvestmentActionModel> result = investmentActionService.findByProfileId(1L);

        List<InvestmentActionModel> resultList = result.collect(Collectors.toList());
        List<InvestmentActionModel> itmLeast = itmStream.collect(Collectors.toList());

        resultList.forEach(r -> itmLeast.forEach(i -> assertEquals(r, i)));
    }

    @EnableApplicationRoleVirtualView
    public void updateTest() {
        InvestmentActionModel input = new InvestmentActionModel();
        InvestmentAction it = new InvestmentAction(LocalDateTime.now(), "InvestmentActionName",
                "PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, false);

        investmentTypeList.forEach(it::addInvestmentType);
        InvestmentActionModel output = new InvestmentActionModel();

        given(investmentActionRepository.findAlertTypeById(1L)).willReturn(Optional.of(it));
        given(investmentActionRepository.save(it)).willReturn(it);
        given(investmentActionMapper.toInvestmentActionModel(it)).willReturn(output);

        InvestmentActionModel result = investmentActionService.update(1L,  input);

        assertSame(output, result);
    }
*/
    @Test
    public void deleteTest() {
        InvestmentAction it = new InvestmentAction();

        given(investmentActionRepository.findById(1L)).willReturn(Optional.of(it));

        investmentActionService.delete(1L);

        then(investmentActionRepository).should().delete(it);
    }
}
