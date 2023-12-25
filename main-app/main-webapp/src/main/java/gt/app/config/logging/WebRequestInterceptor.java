package gt.app.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class WebRequestInterceptor implements AsyncHandlerInterceptor {

    private final ThreadLocal<Long> exeTimeThreadLocal = new ThreadLocal<>();

    private final HibernateStatementStatInterceptor statisticsInterceptor;

    public WebRequestInterceptor(HibernateStatementStatInterceptor statisticsInterceptor) {
        this.statisticsInterceptor = statisticsInterceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        exeTimeThreadLocal.set(System.currentTimeMillis());
        statisticsInterceptor.startCounter();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object handler, ModelAndView modelAndView) {
        Long queryCount = statisticsInterceptor.getQueryCount();
        if (modelAndView != null) {
            //to display to the View
            modelAndView.addObject("_queryCount", queryCount);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse resp, Object handler, Exception ex) {
        long duration = System.currentTimeMillis() - exeTimeThreadLocal.get();
        Long queryCount = statisticsInterceptor.getQueryCount();
        statisticsInterceptor.clearCounter();
        exeTimeThreadLocal.remove();

        log.debug("Queries executed: {}, Request Url: {}, Time: {} ms", queryCount, request.getRequestURI(), duration);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        //concurrent handling cannot be supported here
        statisticsInterceptor.clearCounter();
        exeTimeThreadLocal.remove();
    }
}


