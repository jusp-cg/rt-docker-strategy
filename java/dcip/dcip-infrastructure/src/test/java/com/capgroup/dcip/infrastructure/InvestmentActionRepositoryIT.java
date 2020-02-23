package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.journal.InvestmentAction;
import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.ProfileRepository;
import com.capgroup.dcip.infrastructure.repository.RoleRepository;
import com.capgroup.dcip.infrastructure.repository.UserRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionTypeRepository;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class InvestmentActionRepositoryIT extends AbstractTemporalEntityRepository<InvestmentAction> {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    private InvestmentActionRepository investmentActionRepository;
    @Autowired
    private InvestmentTypeRepository investmentTypeRepository;
    @Autowired
    private InvestmentActionTypeRepository investmentActionTypeRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private List<InvestmentType> investmentTypeList;

    private InvestmentActionType investmentActionType;

    private Profile profile;

    public InvestmentActionRepositoryIT() {
        super(InvestmentAction.class);
    }

    @Before
    public void prepareData() throws Exception {
        InvestmentType it1 = new InvestmentType("InvestmentType1", "Test Investment", true);
        preSave("CREATE_INVESTMENT_TYPE", 5L, it1);
        investmentTypeRepository.save(it1);
        entityManager.flush();

        InvestmentType it2 = new InvestmentType("InvestmentType2", "Test Investment", false);
        preSave("CREATE_INVESTMENT_TYPE", 5L, it2);
        investmentTypeRepository.save(it2);
        entityManager.flush();

        investmentTypeList = Arrays.asList(it1, it2);

        investmentActionType = new InvestmentActionType("InvestmentActionTypeN", 1, true);
        preSave("CREATE_INVESTMENT_ACTION_TYPE", 7L, investmentActionType);
        investmentActionTypeRepository.save(investmentActionType);
        entityManager.flush();

        User user = new User("TU", "TEST_USER", new InvestmentUnit());
        preSave("UNDEFINED", 1L, user);
        userRepository.save(user);
        entityManager.flush();

        Role role = new Role("TEST_ROLE", "Role for testing purposes");
        preSave("UNDEFINED", 3L, role);
        roleRepository.save(role);
        entityManager.flush();

        profile = new Profile(user, role);
        preSave("CREATE_PROFILE", 2L, profile);
        profileRepository.save(profile);
        entityManager.flush();
    }

    @Test
    public void saveTest() throws Exception {
        InvestmentAction investmentAction = new InvestmentAction(LocalDateTime.now(),
                "PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, 1L, false, null);

        investmentTypeList.forEach(investmentAction::addInvestmentType);

        investmentAction.addAccountId(1234);
        investmentAction.addAccountId(5678);

        preSave("CREATE_INVESTMENT_ACTION_ACCOUTNS_IN_ACTION", 9L,
				investmentAction.getAccountsInActions().stream().findFirst().get());
        preSave("CREATE_INVESTMENT_ACTION_ACCOUTNS_IN_ACTION", 9L,
				investmentAction.getAccountsInActions().stream().skip(1).findFirst().get());
        preSave("CREATE_INVESTEMNT_ACTION_INVESTMENT_TYPE", 8L,
				investmentAction.getInvestmentTypesInActions().stream().findFirst().get());
        preSave("CREATE_INVESTEMNT_ACTION_INVESTMENT_TYPE", 8L,
				investmentAction.getInvestmentTypesInActions().stream().skip(1).findFirst().get());


        preSave("CREATE_INVESTMENT_ACTION", 6L, investmentAction);

        investmentActionRepository.save(investmentAction);

        entityManager.flush();

        assertThat(investmentAction.getId()).isNotEqualTo(-1L);

        entityManager.clear();

        InvestmentAction readIn = investmentActionRepository.findById(investmentAction.getId()).get();

        assertThat(readIn).isEqualTo(investmentAction);
    }

    @Test
    public void queryForProfile() throws Exception {
        InvestmentAction investmentAction = new InvestmentAction(LocalDateTime.now(),
                "PublicComment", "OrderInfo", "ReasonForBuying", investmentActionType, profile, 1L, false, (short)5);

        investmentTypeList.forEach(investmentAction::addInvestmentType);

        investmentAction.addAccountId(1234);
        investmentAction.addAccountId(5678);

        preSave("CREATE_INVESTMENT_ACTION_ACCOUTNS_IN_ACTION", 9L,
				investmentAction.getAccountsInActions().stream().findFirst().get());
        preSave("CREATE_INVESTMENT_ACTION_ACCOUTNS_IN_ACTION", 9L,
				investmentAction.getAccountsInActions().stream().skip(1).findFirst().get());
        preSave("CREATE_INVESTEMNT_ACTION_INVESTMENT_TYPE", 8L,
				investmentAction.getInvestmentTypesInActions().stream().findFirst().get());
        preSave("CREATE_INVESTEMNT_ACTION_INVESTMENT_TYPE", 8L,
				investmentAction.getInvestmentTypesInActions().stream().skip(1).findFirst().get());

        preSave("CREATE_INVESTMENT_ACTION", 6L, investmentAction);
        investmentActionRepository.save(investmentAction);
        entityManager.flush();

        final long profileId = investmentAction.getEvent().getProfile().getId();

        List<InvestmentAction> investmentActionList = investmentActionRepository.findByEventProfileId(profileId);

        assertNotNull("Result list should be initialized", investmentActionList);
        assertThat(profileId).isEqualTo(investmentActionList.get(0).getEvent().getProfile().getId());
    }
}
