package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类
 * 实现公共字段的自动填充
 */
@Aspect     //标识该类为一个AOP类
@Component  //交给IOC容器
@Slf4j      //日志
public class AutoFillAspect {
    //切入点

    //注解为什么这样写？
        //1:只有前面部分会将不需要的方法也扫描进去，如查询语句
        //2：只写后面的部分，可以精准识别到方法，但是会扫描访问为整个项目，加上前面的可以减小扫描范围
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")      //抽取切入点表达式
    public void autoFillPoinCut(){}

    @Before("autoFillPoinCut()")        //前置通知
    public void autoFill(JoinPoint joinPoint){
        log.info("开始执行公共字段的自动填充...");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();     //获得方法签名（方法的名称和参数的额类型）
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);    //获得方法上的注解对象
        OperationType operationType = autoFill.value();  //获得数据库操作类型

        //获取当前被拦截到的方法的参数
        Object[] args = joinPoint.getArgs();	//目标方法运行时传入的参数
        if(args==null||args.length==0){return ;}    //防止出现空指针。这种情况不会出现

        Object arg = args[0];   //约定：第一个参数为需要操作的对象

        //公共属性赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();    //当前登录的用户id


        //根据不同的操作类型，为对应的属性进行赋值
        if(operationType==OperationType.INSERT){    //操作类型为插入
            try {
                //反射：获取对象的class对象，获取class对象中的成员方法。
                //使用常量代替方法名
                Method setCreateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //反射：调用方法
                setCreateTime.invoke(arg,now);
                setCreateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(operationType==OperationType.UPDATE){  //操作类型为修改
            try {
                //反射：获取对象的class对象，获取class对象中的成员方法。
                //使用常量代替方法名
                Method setUpdateTime = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //反射：调用方法
                setUpdateTime.invoke(arg,now);
                setUpdateUser.invoke(arg,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
