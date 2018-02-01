package com.athena.util.publication.search;

import com.athena.annotation.PublicationSearchParam;
import com.athena.model.publication.search.PublicationSearchVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tommy on 2018/2/1.
 */
public class PublicationSearchParamResolver implements HandlerMethodArgumentResolver {

    private final Integer searchCount;
    private final ObjectMapper objectMapper;

    public PublicationSearchParamResolver(Integer searchCount, ObjectMapper objectMapper) {
        this.searchCount = searchCount;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // parameter must have PublicationSearchParam annotation and it's type must be subclass or subinterface of PublicationSearchVo
        return methodParameter.getParameterAnnotation(PublicationSearchParam.class) != null && PublicationSearchVo.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        PublicationSearchParam publicationSearchParam = methodParameter.getParameterAnnotation(PublicationSearchParam.class);
        List<String> keys = Arrays.asList(publicationSearchParam.values());
        Map<String, String[]> requestParameterMap = nativeWebRequest.getParameterMap();
        Map<String, Object> parameters = new HashMap<>();
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            if (keys.indexOf(entry.getKey()) != -1) {
                // if entry key is in PublicationSearchParam keys
                if (entry.getValue().length == 1) {
                    // if the value only have 1 instance, then unwrap.
                    parameters.put(entry.getKey(), entry.getValue()[0]);
                } else {
                    // else put to result
                    parameters.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (!parameters.containsKey("count")) {
            // use default value if client is not pass value
            parameters.put("count", this.searchCount);
        }
        return this.objectMapper.convertValue(parameters, methodParameter.getParameterType());
    }
}
