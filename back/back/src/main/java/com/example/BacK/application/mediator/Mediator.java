package com.example.BacK.application.mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.AopProxyUtils;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

@Component
public class Mediator {

    private final ApplicationContext context;
    private final Validator validator;

    @Autowired
    private List<RequestHandler<?, ?>> handlers;


    public Mediator(ApplicationContext context, Validator validator) {
        this.context = context;
        this.validator = validator;
    }

    public <C, R> List<R> sendToHandlers(C command) {
        // 🔍 Étape 1 : Valider manuellement la requête (le "command")
        Set<ConstraintViolation<C>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<C> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("Validation failed:\n" + sb.toString());
        }

        // 🔄 Étape 2 : Traiter les handlers normalement
        List<R> responses = new ArrayList<>();
 
        for (RequestHandler<?, ?> handler : handlers) {
            try {
                // Check if this handler can handle the command type
                if (canHandle(handler, command)) {
                    @SuppressWarnings("unchecked")
                    RequestHandler<C, R> typedHandler = (RequestHandler<C, R>) handler;
     
                    R response = typedHandler.handle(command);
                    if (response != null) {
                        responses.add(response);
                    }
                }
            } catch (Exception e) {
                // Handler failed, re-throw to see actual error
                throw new RuntimeException("Handler failed: " + handler.getClass().getSimpleName(), e);
            }
        }
        return responses;
    }
    
    private boolean canHandle(RequestHandler<?, ?> handler, Object command) {
        try {
            // Get the actual target class if this is a Spring proxy (CGLIB or JDK)
            Class<?> targetClass = AopUtils.isAopProxy(handler) 
                ? AopProxyUtils.ultimateTargetClass(handler)
                : handler.getClass();
            
            // Get the generic interfaces implemented by the target class
            Type[] genericInterfaces = targetClass.getGenericInterfaces();
            
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericInterface;
                    
                    // Check if it's RequestHandler interface
                    if (paramType.getRawType().equals(RequestHandler.class)) {
                        Type[] typeArgs = paramType.getActualTypeArguments();
                        if (typeArgs.length > 0) {
                            Type commandType = typeArgs[0];
                            
                            // Check if the command type matches
                            if (commandType instanceof Class) {
                                Class<?> expectedCommandClass = (Class<?>) commandType;
                                return expectedCommandClass.isAssignableFrom(command.getClass());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If we can't determine, assume it can handle
            return true;
        }
        return false;
    }
}

