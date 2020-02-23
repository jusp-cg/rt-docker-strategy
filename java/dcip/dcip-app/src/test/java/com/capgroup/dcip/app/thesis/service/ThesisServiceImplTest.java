package com.capgroup.dcip.app.thesis.service;

import com.capgroup.dcip.app.ServiceTestConfig;
import com.capgroup.dcip.app.canvas.CanvasService;
import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.app.thesis.model.ThesisMapper;
import com.capgroup.dcip.app.thesis.model.ThesisPointModel;
import com.capgroup.dcip.domain.identity.InvestmentUnit;
import com.capgroup.dcip.domain.identity.Profile;
import com.capgroup.dcip.domain.identity.User;
import com.capgroup.dcip.domain.thesis.ThesisPoint;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisEdgeRepository;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisPointRepository;
import com.capgroup.dcip.infrastructure.repository.thesis.ThesisRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.EntityManager;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(ThesisServiceImpl.class)
public class ThesisServiceImplTest {

    @Autowired
    ThesisServiceImpl thesisServiceImpl;

    @MockBean
    ThesisRepository thesisRepository;

    @MockBean
    EntityManager entityManager;

    @MockBean
    private ThesisMapper thesisMapper;

    @MockBean
    private CanvasService canvasService;

    @MockBean
    private ThesisPointRepository thesisPointRepository;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private ThesisEdgeRepository thesisEdgeRepository;

    @MockBean
    private RequestContextService requestContextService;

    @Test
    public void findVersionsOfThesisPointTest(){
        InvestmentUnit investmentUnit = new InvestmentUnit(10, "iu");
        User user = new User("me", "my name",investmentUnit);
        Profile profile = new Profile(user, null);
        Long key = 10l;

        List<ThesisPoint> thesisPointList = List.of(new ThesisPoint());
        List<ThesisPointModel> modelList = List.of(new ThesisPointModel());

        given(requestContextService.currentProfile()).willReturn(profile);
        given(thesisPointRepository.findAllVersions(key, 10)).willReturn(thesisPointList);
        given(thesisMapper.mapThesisPoints(thesisPointList)).willReturn(modelList);

        Iterable<ThesisPointModel> result = thesisServiceImpl.findVersionsOfThesisPoint(key);

        Assert.assertSame(modelList, result);
    }

    public Iterable<ThesisPointModel> findVersionsOfThesisPoint(long id) {
        return thesisMapper.mapThesisPoints(thesisPointRepository.findAllVersions(id,
                requestContextService.currentProfile().getUser().getInvestmentUnit().getId())
        );
    }

}
