package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.identity.Profile;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Slf4j
@Configuration
@Aspect
public class EnableAllVirtualViewsAnnotationProcessor {

    private RequestContextService requestContext;
    private Collection<VirtualView> virtualViews;

    @Autowired
    public EnableAllVirtualViewsAnnotationProcessor(RequestContextService requestContextService,
                                                    Collection<VirtualView> virtualViews) {
        this.requestContext = requestContextService;
        this.virtualViews = virtualViews;
    }

    @Pointcut("execution(@com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews * *.*(..))")
    void enableAllVirtualViewsMethod() {
    }

    @Pointcut("execution(* (@com.capgroup.dcip.app.virtual_view.EnableAllVirtualViews *).*(..))")
    void enableAllVirtualViewsClass() {
    }

    @Before("enableAllVirtualViewsMethod() && @annotation(annotation)")
    public void onEnableAllVirtualViewsMethod(EnableAllVirtualViews annotation) {
        enable();
    }

    @Before("enableAllVirtualViewsClass() && !enableAllVirtualViewsMethod() && @within(annotation)")
    public void adviseMethodsOfEnableAllVirtualViewsClass(EnableAllVirtualViews annotation) {
        enable();
    }

    private void enable() {
        Profile profile = requestContext.currentProfile();
        virtualViews.stream().filter(x -> x.canEnable(profile)).forEach(x -> x.enable(profile));
    }
}
