package com.capgroup.dcip.app.journal;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.Role;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.journal.InvestmentType;
import com.capgroup.dcip.infrastructure.repository.journal.InvestmentTypeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest()
@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(InvestmentTypeServiceImpl.class)
public class InvestmentTypeServiceImplTest {
    @MockBean
    InvestmentTypeRepository investmentTypeRepository;

    @MockBean
    InvestmentTypeMapper investmentTypeMapper;

    @MockBean
    EntityManager entityManager;

    @Autowired
    InvestmentTypeService investmentTypeService;

    @MockBean
    RequestContextService requestContextService;

    @Test
    public void createTest() {
        InvestmentType investmentType = new InvestmentType();
        InvestmentTypeModel input = new InvestmentTypeModel();

        Profile profile = new Profile(new User("me", "me", null), new Role("role", "role"));

        given(requestContextService.currentProfile()).willReturn(profile);
        given(investmentTypeMapper.map(input)).willReturn(investmentType);
        given(investmentTypeRepository.save(investmentType)).willReturn(investmentType);
        given(investmentTypeMapper.map(investmentType)).willReturn(input);

        InvestmentTypeModel output = investmentTypeService.create(input);

        assertSame(input, output);
    }

    @Test
    public void readTest() {
        InvestmentType t1 = new InvestmentType();
        InvestmentType t2 = new InvestmentType();
        List<InvestmentType> stream = Arrays.asList(t1, t2);

        InvestmentTypeModel m1 = new InvestmentTypeModel();
        InvestmentTypeModel m2 = new InvestmentTypeModel();
        List<InvestmentTypeModel> itmStream = Arrays.asList(m1, m2);

        given(investmentTypeRepository.findAllByUserInitials("me")).willReturn(stream);
        given(investmentTypeMapper.mapAll(stream)).willReturn(itmStream);


        List<InvestmentTypeModel> entries = new ArrayList();
        investmentTypeService.findByUserInitials("me", false)
                .iterator().forEachRemaining(entries::add);

        assertSame(m1, entries.get(0));
        assertSame(m2, entries.get(1));
    }

    @Test
    public void updateTest() {
        InvestmentTypeModel input = new InvestmentTypeModel();
        InvestmentType it = new InvestmentType();
        InvestmentTypeModel output = new InvestmentTypeModel();

        given(investmentTypeRepository.findById(12l)).willReturn(Optional.of(it));
        given(investmentTypeRepository.save(it)).willReturn(it);
        given(investmentTypeMapper.map(it)).willReturn(output);

        InvestmentTypeModel result = investmentTypeService.update(12l, input);

        assertSame(output, result);
    }

    @Test
    public void deleteTest() {
        InvestmentType it = new InvestmentType();

        given(investmentTypeRepository.findById(12l)).willReturn(Optional.of(it));

        investmentTypeService.delete(12l);

        then(investmentTypeRepository).should().delete(it);
    }

}
