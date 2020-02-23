package com.capgroup.dcip.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.domain.journal.InvestmentActionType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentActionTypeRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
@Commit
public class InvestmentActionTypeRepositoryIT extends AbstractTemporalEntityRepository<InvestmentActionType> {

    @Autowired
    private InvestmentActionTypeRepository investmentActionTypeRepository;

    @Autowired
    TestEntityManager entityManager;

    public InvestmentActionTypeRepositoryIT() {
        super(InvestmentActionType.class);
    }

    @Test
    public void saveTest() throws Exception {
        InvestmentActionType investmentActionType = new InvestmentActionType("Test_Investment_Action_Type", 1, true);

        preSave("CREATE_INVESTMENT_ACTION_TYPE", 7L, investmentActionType);

        investmentActionTypeRepository.save(investmentActionType);

        entityManager.flush();

        assertThat(investmentActionType.getId()).isNotEqualTo(-1L);

        entityManager.clear();

        InvestmentActionType readIn = investmentActionTypeRepository.findById(investmentActionType.getId()).get();

        assertThat(readIn).isEqualTo(investmentActionType);
    }
}
