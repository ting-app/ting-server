package ting.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import ting.Constant;
import ting.annotation.LoginRequired;
import ting.dto.ResponseError;
import ting.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Intercepts the requests that are annotated by {@link ting.annotation.LoginRequired}.
 * If a route is annotated by {@link ting.annotation.LoginRequired} and current user is anonymous,
 * it will send a 401 http status code to client.
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if (!((HandlerMethod) handler).getMethod().isAnnotationPresent(LoginRequired.class)) {
            return true;
        }

        UserDto user = (UserDto) request.getSession().getAttribute(Constant.ME);

        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream()
                    .write(this.objectMapper.writeValueAsBytes(new ResponseError("请先登陆")));

            return false;
        } else {
            return true;
        }
    }
}
