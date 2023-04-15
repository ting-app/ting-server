package dekiru.ting.support;

import dekiru.ting.annotation.Me;
import dekiru.ting.dto.UserDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import dekiru.ting.Constant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * If an argument of a route is annotated by {@link Me}
 * and its type is {@link UserDto},
 * then the value of this argument is resolved to current login user in this session.
 */
@Component
public class CurrentUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Me.class) != null
                && UserDto.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpSession session = ((HttpServletRequest) webRequest.getNativeRequest()).getSession();
        UserDto user = (UserDto) session.getAttribute(Constant.ME);

        return user;
    }
}
