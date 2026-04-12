package ltd.xiaomizha.xuyou.common.utils.debounce;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * SpEL表达式解析工具类
 */
public class SpELUtil {

    // SpEL解析器, 全局单例, 避免重复创建
    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 解析单个SpEL表达式, 适配key数组中的每个元素
     *
     * @param spEL      单个表达式, 示例: #userId、#request.ip、#phone
     * @param joinPoint 切面连接点, 用于获取方法参数
     * @param method    目标方法, 用于获取参数名和参数类型
     * @return 解析后的结果
     */
    public static String parse(String spEL, ProceedingJoinPoint joinPoint, Method method) {
        // 获取方法参数名和对应的参数值
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取方法所有参数
        Parameter[] parameters = method.getParameters();
        // 获取方法参数对应的值
        Object[] args = joinPoint.getArgs();
        // 创建SpEL上下文, 将所有方法参数放入上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameters.length; i++) {
            // 参数名 -> 变量名
            // 参数值 -> 变量值
            context.setVariable(parameters[i].getName(), args[i]);
        }
        // 解析单个SpEL表达式, 转为字符串
        Expression expression = PARSER.parseExpression(spEL);
        try {
            // 解析失败时返回原表达式
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            return spEL;
        }
    }

}
