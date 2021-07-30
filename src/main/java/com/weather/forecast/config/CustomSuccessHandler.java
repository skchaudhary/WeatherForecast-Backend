package com.weather.forecast.config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSuccessHandler.class);
    private final GrantedAuthority adminAuthority = new SimpleGrantedAuthority(
            "USER");

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        //if redirected from some specific url, need to remove the cachedRequest to force use defaultTargetUrl
        if (isUserAuthority(authentication))
        {
            String targetUrl = super.determineTargetUrl(request, response);
            // this logic is only for demo purpose, please do not use it on production application.
            if(StringUtils.isBlank(targetUrl) || StringUtils.equals(targetUrl, "/"))
            {
                targetUrl ="/home"; // we can build custom logic
            }
            clearAuthenticationAttributes(request);
            LOG.info("Redirecting customer to the following location {} ",targetUrl);
            redirectStrategy.sendRedirect(request, response, targetUrl);

            //You can let Spring security handle it for you.
            // super.onAuthenticationSuccess(request, response, authentication);

        }
        clearAuthenticationAttributes(request);
    }

    protected boolean isUserAuthority(final Authentication authentication)
    {
        return CollectionUtils.isNotEmpty(authentication.getAuthorities())
                && authentication.getAuthorities().contains(adminAuthority);
    }
}
