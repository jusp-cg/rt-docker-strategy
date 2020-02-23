package com.capgroup.dcip.infrastructure;

import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = InfrastructureTestConfig.class, loader = AnnotationConfigContextLoader.class)
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
public class InvestmentTypeRepositoryIT extends AbstractTemporalEntityRepository<InvestmentType> {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    private InvestmentTypeRepository investmentTypeRepository;

    public InvestmentTypeRepositoryIT() {
        super(InvestmentType.class);
    }

    @Test
    public void saveTest() throws Exception {
        InvestmentType investmentType = new InvestmentType("InvestmentTypeName", "Investment Type Description", true);

        preSave("CREATE_INVESTMENT_TYPE", 5L, investmentType);

        investmentTypeRepository.save(investmentType);

        entityManager.flush();

        assertThat(investmentType.getId()).isNotEqualTo(0l);

        entityManager.clear();

        InvestmentType readIn = investmentTypeRepository.findById(investmentType.getId()).get();

        assertThat(readIn).isEqualTo(investmentType);
    }

    @Test
    public void queryForUserNotPublic() throws Exception {
        InvestmentType investmentType1 = new InvestmentType("InvestmentTypeName", "Investment Type Description", true);

        // creates a profile/event/etc.
        preSave("CREATE_INVESTMENT_TYPE", 5L, investmentType1);
        investmentTypeRepository.save(investmentType1);
        entityManager.flush();
        entityManager.clear();

        InvestmentType investmentType2 = new InvestmentType("InvestmentTypeName", "Investment Type Description", false);

        // creates a new profile/event/etc. for a different user
        preSave("CREATE_INVESTMENT_TYPE", 5L, investmentType2);
        investmentTypeRepository.save(investmentType2);
        entityManager.flush();
        entityManager.clear();

        // if I query for the 1st user it will only pick up entities from that user
        List<InvestmentType> result1 = new ArrayList<>();
        investmentTypeRepository
                .findAllByUserInitials(investmentType1.getEvent().getProfile().getUser().getInitials())
                .iterator().forEachRemaining(result1::add);

        assertTrue(result1.stream().allMatch(x -> x.getEvent().getProfile().getUser().getInitials()
                .equals(investmentType1.getEvent().getProfile().getUser().getInitials())));
    }

    @Test
    public void queryForUserWithPublic() throws Exception {
        InvestmentType investmentType1 = new InvestmentType("InvestmentTypeName", "Investment Type Description", true);

        // creates a profile/event/etc.
        preSave("CREATE_INVESTMENT_TYPE", 5L, investmentType1);
        investmentTypeRepository.save(investmentType1);
        entityManager.flush();
        entityManager.clear();

        InvestmentType investmentType2 = new InvestmentType("InvestmentTypeName", "Investment Type Description", true);

        // creates a new profile/event/etc. for a different user
        preSave("CREATE_INVESTMENT_TYPE", 5L, investmentType2);
        investmentTypeRepository.save(investmentType2);
        entityManager.flush();
        entityManager.clear();

        // if I query for the 1st user with the public flag then I should get at least
        // the 2 that I added
        List<InvestmentType> result1 = new ArrayList<>();
        investmentTypeRepository
                .findAllByUserInitials(investmentType1.getEvent().getProfile().getUser().getInitials())
                .iterator().forEachRemaining(result1::add);

        assertTrue(result1.contains(investmentType2));
        assertTrue(result1.contains(investmentType1));

        // remove the ones for this user
        List<InvestmentType> filteredItems = result1.stream().filter(x -> {
            return !x.getEvent().getProfile().getUser().getInitials()
                    .equals(investmentType1.getEvent().getProfile().getUser().getInitials());
        }).collect(Collectors.toList());

        // the remaining items should all be public
        assertTrue(filteredItems.stream().allMatch(x -> {
            return x.isDefaultFlag();
        }));
    }

}
