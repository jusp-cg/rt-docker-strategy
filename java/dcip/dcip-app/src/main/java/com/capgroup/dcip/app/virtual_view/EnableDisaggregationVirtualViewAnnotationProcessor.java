package com.capgroup.dcip.app.virtual_view;

import com.capgroup.dcip.app.context.RequestContextService;
import com.capgroup.dcip.domain.identity.Profile;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;

/**
 * Processes the Annotation @EnableDisaggregationVirtualView - adds the disaggregation criteria to DB queries
 * Currently there is no requirement to disable the disaggregation filter once it has been enabled
 */
@Slf4j
@Configuration
@Aspect
public class EnableDisaggregationVirtualViewAnnotationProcessor {

    static {
        try {
            // Hibernate does not add filters to findOne, findById, etc. This bytecode manipulation adds the logic to
            // add the filters to the query. When upgrading hibernate it is possible that this code might break
            // see
            // https://stackoverflow.com/questions/45169783/hibernate-filter-is-not-applied-for-findone-crud-operation
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            CtClass cl = pool.get("org.hibernate.loader.plan.exec.internal.EntityLoadQueryDetails");
            CtMethod me = cl.getDeclaredMethod("applyRootReturnFilterRestrictions");
            String s = "{final org.hibernate.persister.entity.Queryable rootQueryable = (org.hibernate.persister" +
                    ".entity" +
                    ".Queryable) getRootEntityReturn().getEntityPersister();" +
                    "$1.appendRestrictions(" +
                    "rootQueryable.filterFragment(" +
                    "entityReferenceAliases.getTableAlias()," +
                    "getQueryBuildingParameters().getQueryInfluencers().getEnabledFilters()" +
                    "));}";
            me.setBody(s);
            cl.toClass();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    private RequestContextService requestContext;
    private VirtualView virtualView;

    @Autowired
    public EnableDisaggregationVirtualViewAnnotationProcessor(
            RequestContextService requestContextService,
            @Named("DisaggregationVirtualView") DisaggregationVirtualView virtualView) {
        this.virtualView = virtualView;
        this.requestContext = requestContextService;
    }

    @Pointcut("execution(@com.capgroup.dcip.app.virtual_view.EnableDisaggregationVirtualView * *.*(..))")
    void enableDisaggregationVirtualViewMethod() {
    }

    @Pointcut("execution(* (@com.capgroup.dcip.app.virtual_view.EnableDisaggregationVirtualView *).*(..))")
    void enableDisaggregationVirtualViewClass() {
    }

    @Before("enableDisaggregationVirtualViewMethod() && @annotation(annotation)")
    public void onDisaggregationMethod(EnableDisaggregationVirtualView annotation) {
        enable();
    }

    @Before("enableDisaggregationVirtualViewClass() && !enableDisaggregationVirtualViewMethod() && @within(annotation)")
    public void adviseMethodsOfAnnotatedClass(EnableDisaggregationVirtualView annotation) {
        enable();
    }

    private void enable() {
        Profile profile = requestContext.currentProfile();
        if (virtualView.canEnable(profile)) {
            virtualView.enable(profile);
        }
    }
}
