package com.capgroup.dcip.app.data;

import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgroup.dcip.app.ServiceTestConfig;

@ContextConfiguration(classes = ServiceTestConfig.class, loader = AnnotationConfigContextLoader.class)
@RunWith(SpringRunner.class)
@Import(TimeSeriesDataSourceConnectionResolver.class)
public class TimeSeriesDataSourceConnectionResolverTest {
	@Autowired
	TimeSeriesDataSourceConnectionResolver resolver;
	
	@MockBean
	TimeSeriesDataSourceConnectionManager connectionManager;
	
	@Test
	public void resolveTest() {
		TimeSeriesDataSourceConnection conn1 = Mockito.mock(TimeSeriesDataSourceConnection.class);
		TimeSeriesDataSourceConnection conn2 = Mockito.mock(TimeSeriesDataSourceConnection.class);
		TimeSeriesQuery query = new TimeSeriesQuery();
		
		given(connectionManager.dataSourceConnections()).willReturn(Arrays.asList(conn1, conn2));
		
		
	}
}
