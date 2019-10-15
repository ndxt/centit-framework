package com.centit.framework.model.adapter;

import com.centit.support.compiler.VariableTranslate;
import java.util.Set;

public interface UserUnitVariableTranslate extends VariableTranslate {


    /**
     * 返回权限表达式中的自定义变量对应的用户组
     *
     * @param varName 自定义变量
     * @return  Set 权限表达式中的自定义变量对应的用户组
     */
    Set<String> getUsersVariable(String varName);

    /**
     * 返回机构表达式中的自定义变量对应的机构组
     *
     * @param varName 自定义变量
     * @return  Set 返回机构表达式中的自定义变量对应的机构组
     */
    Set<String> getUnitsVariable(String varName);

    /**变量名--变量值的转变 保持历史版本兼容
     *变量 是用 ${变量名}
     *如果这个变量不存在，返回空字符串 "''"
     * @param varName 自定义变量
     * @return  Object
     */
    default Object getGeneralVariable(String varName){
        return getVarValue(varName);
    }

}
