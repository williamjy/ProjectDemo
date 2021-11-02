package com.nuggets.valueeats.controller.decorator.token;

import com.nuggets.valueeats.controller.exception.InvalidTokenException;
import com.nuggets.valueeats.entity.User;
import com.nuggets.valueeats.repository.DinerRepository;
import com.nuggets.valueeats.repository.EateryRepository;
import com.nuggets.valueeats.repository.UserRepository;
import com.nuggets.valueeats.utils.JwtUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
class CheckTokenAspect<T extends User> {
    @Autowired
    private UserRepository<T> userRepository;
    @Autowired
    private EateryRepository eateryRepository;
    @Autowired
    private DinerRepository dinerRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Before("@annotation(com.nuggets.valueeats.controller.decorator.token.CheckUserToken)")
    public void checkSession(JoinPoint joinPoint) {
        checkXSession(joinPoint, userRepository);
    }

    @Before("@annotation(com.nuggets.valueeats.controller.decorator.token.CheckEateryToken)")
    public void checkEaterySession(JoinPoint joinPoint) {
        checkXSession(joinPoint, eateryRepository);
    }

    @Before("@annotation(com.nuggets.valueeats.controller.decorator.token.CheckDinerToken)")
    public void checkDinerSession(JoinPoint joinPoint) {
        checkXSession(joinPoint, dinerRepository);
    }

    private <U extends UserRepository<V>, V extends User> void checkXSession(final JoinPoint joinPoint, final U repository) {
        if (joinPoint.getArgs().length == 2) {
            if (jwtUtils.decode((String) joinPoint.getArgs()[0]) == null || !repository.existsByToken((String) joinPoint.getArgs()[0])) {
                throw new InvalidTokenException("Token is invalid");
            }
        }
    }
}
