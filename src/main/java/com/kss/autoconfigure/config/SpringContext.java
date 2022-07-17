package com.kss.autoconfigure.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Returns the Spring managed bean instance of the given class type if it exists.
     * Returns null otherwise.
     *
     * @param beanClass
     * @return
     */
    public static <T extends Object> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static boolean hasBean(String name) {
        return context.containsBean(name);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        // store ApplicationContext reference to access required beans later on
        SpringContext.context = context;
    }
}
