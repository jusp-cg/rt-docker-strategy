package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.identity.Profile;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

@Slf4j
@Configuration
@Aspect
public class EnableApplicationRoleVirtualViewAnnotationProcessor {

    private RequestContextService requestContext;
    private VirtualView virtualView;

    @Autowired
    public EnableApplicationRoleVirtualViewAnnotationProcessor(RequestContextService requestContextService,
                                                               @Named("ApplicationRoleVirtualView") VirtualView virtualView) {
        this.requestContext = requestContextService;
        this.virtualView = virtualView;
    }

    @Pointcut("execution(@com.capgroup.dcip.app.virtual_view.EnableApplicationRoleVirtualView * *.*(..))")
    void testMethod() {
    }

    @Pointcut("execution(* (@com.capgroup.dcip.app.virtual_view.EnableApplicationRoleVirtualView *).*(..))")
    void testClass() {
    }

    @Before("testMethod() && @annotation(annotation)")
    public void onTestMethod(EnableApplicationRoleVirtualView annotation) {
        enableTest();
    }

    @Before("testClass() && !testMethod() && @within(annotation)")
    public void adviseMethodsOfTestClass(EnableApplicationRoleVirtualView annotation) {
        enableTest();
    }

    private void enableTest() {
        Profile profile = requestContext.currentProfile();
        if (virtualView.canEnable(profile)) {
            virtualView.enable(profile);
        }
    }
}
