package com.centit.framework.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ViewDataTransform;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUnitFilterEngine;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.impl.UserUnitMapTranslate;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.framework.filter.RequestThreadLocal;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.database.utils.DBType;
import com.centit.support.file.FileSystemOpt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author codefan@sina.com
 * Date: 14-11-26
 * Time: 下午1:53
 * 返回原框架中使用CP标签的结果数据
 */
@Controller
@RequestMapping("/cp")
@Api(value="框架将所有的系统信息都缓存在内存中，这个类提供了大量的访问框架数据的接口",
    tags= "框架数据缓存接口")
public class CacheController {

    private static Log logger = LogFactory.getLog(CacheController.class);
    private String optId = "cache";
    /**
     * cp标签中MAPVALUE实现，获取数据字典对应的值
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,
     *                             unitcode,depno,rolecode,optid,optcode,optdesc,]
     *                             以及数据目录中的catalogCode变量值
     * @param key      对应的变量值 或 数据字典中的 dataCode
     * @param response HttpServletResponse
     */
    @ApiOperation(value="数据字典取值",notes="根据数据字典的类别和key获取对应的value。")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "catalog", value="数据字典的类别代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "key", value="数据字典的条目代码",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/mapvalue/{catalog}/{key}", method = RequestMethod.GET)
    //@RecordOperationLog(content = "查询字典{arg0}中{arg1}的值",timing = true, appendRequest = true)
    public void mapvalue(@PathVariable String catalog, @PathVariable String key,
            HttpServletResponse response) {
        String value = CodeRepositoryUtil.getValue(catalog, key);
        JsonResultUtils.writeSingleDataJson(value, response);
    }

    /**
     * cp标签中MAPCODE实现，获取数据字典对应的值
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,unitcode,depno,rolecode,optid,optcode,optdesc,]以及数据目录中的catalogCode变量值
     * @param value    对应的变量值 或 数据字典中的 dataValue
     * @param response HttpServletResponse
     */
    @ApiOperation(value="数据字典取健",notes="和mapvalue相反他是根据数据字典的类别和value获取对应的key。")
    @RequestMapping(value = "/mapcode/{catalog}/{value}", method = RequestMethod.GET)
    public void mapcode(@PathVariable String catalog, @PathVariable String value, HttpServletResponse response) {
        String key = CodeRepositoryUtil.getValue(catalog, value);
        JsonResultUtils.writeSingleDataJson(key, response);
    }


    /**
     * cp标签中LVB实现，获取 数据字典 key value 对
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,unitcode,depno,rolecode,optid,optcode,optdesc,]，数据目录中的catalogCode变量值
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/lvb/{catalog}", method = RequestMethod.GET)
    public void lvb(@PathVariable String catalog, HttpServletResponse response) {
        Map<String,String> keyValueMap = CodeRepositoryUtil.getLabelValueMap(catalog);
        JsonResultUtils.writeSingleDataJson(keyValueMap, response);
    }

    /**
     * cp标签中MAPEXPRESSION实现
     * 把表达式中的字典代码都 转换为 数据字典值，其他的字符 位置不变，
     *
     * @param catalog    数据字典代码
     * @param expression 表达式
     * @param response   HttpServletResponse
     */
    @RequestMapping(value = "/mapexpression/{catalog}/{expression}", method = RequestMethod.GET)
    public void mapexpression(@PathVariable String catalog, String expression, HttpServletResponse response) {
        String s = CodeRepositoryUtil.transExpression(catalog, expression);
        JsonResultUtils.writeSingleDataJson(s, response);
    }

    /**
     * cp标签中MAPSTATE实现，获得数据字典条目的状态
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,unitcode,rolecode,]以及数据目录中的catalogCode变量值
     * @param key      对应的变量值 或 数据字典中的 dataCode
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/mapstate/{catalog}/{key}", method = RequestMethod.GET)
    public void mapstate(@PathVariable String catalog, @PathVariable String key, HttpServletResponse response) {
        String s = CodeRepositoryUtil.getItemState(catalog, key);
        JsonResultUtils.writeSingleDataJson(s, response);
    }

    /**
     * cp标签中SUBUNITS实现
     * 获取机构下面的所有下级机构，并且排序
     *
     * @param unitCode 机构代码
     * @param unitType 机构类别
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/subunits/{unitCode}/{unitType}", method = RequestMethod.GET)
    public void subunits(@PathVariable String unitCode, @PathVariable String unitType, HttpServletResponse response) {
        List<IUnitInfo> listObjects = CodeRepositoryUtil.getSortedSubUnits(unitCode, unitType);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * cp标签中ALLUNITS实现
     * 根据状态获取所有机构信息，
     *
     * @param state    A表示所有状态
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/allunits/{state}", method = RequestMethod.GET)
    public void allunits(@PathVariable String state, HttpServletResponse response) {
        List<IUnitInfo> listObjects = CodeRepositoryUtil.getAllUnits(state);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    @RequestMapping(value = "/userinfo/{userCode}", method = RequestMethod.GET)
    public void getUserInfo(@PathVariable String userCode, HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(CodeRepositoryUtil.getUserInfoByCode(userCode), response);
    }

    @RequestMapping(value = "/unitinfo/{unitCode}", method = RequestMethod.GET)
    public void getUintInfo(@PathVariable String unitCode, HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(CodeRepositoryUtil.getUnitInfoByCode(unitCode), response);
    }

    @RequestMapping(value = "/parentunit/{unitCode}", method = RequestMethod.GET)
    public void getParentUintInfo(@PathVariable String unitCode, HttpServletResponse response) {
        IUnitInfo ui = CodeRepositoryUtil.getUnitInfoByCode(unitCode);
        if(ui!=null){
            JsonResultUtils.writeSingleDataJson(
                CodeRepositoryUtil.getUnitInfoByCode(ui.getParentUnit()), response);
        }else {
            JsonResultUtils.writeErrorMessageJson("没有代码为: "+ unitCode+" 的机构！",response);
        }
    }

    @RequestMapping(value = "/parentpath/{unitCode}", method = RequestMethod.GET)
    public void getParentUintPath(@PathVariable String unitCode, HttpServletResponse response) {
        IUnitInfo ui = CodeRepositoryUtil.getUnitInfoByCode(unitCode);
        if(ui!=null){
            List<IUnitInfo> parentUnits = new ArrayList<>();
            while(true) {
                if(StringUtils.isBlank(ui.getParentUnit())){
                    break;
                }
                IUnitInfo parentUnit = CodeRepositoryUtil.getUnitInfoByCode(ui.getParentUnit());
                if(parentUnit==null){
                    break;
                }
                parentUnits.add(parentUnit);
                ui = parentUnit;
            }
            JsonResultUtils.writeSingleDataJson(parentUnits, response);
        }else {
            JsonResultUtils.writeErrorMessageJson("没有代码为: "+ unitCode+" 的机构！",response);
        }
    }
    /**
     * cp标签中RECURSEUNITS实现
     * 获得已知机构 下级的所有有效机构并返回map，包括下级机构的下级机构
     *
     * @param parentUnit 父级机构代码
     * @param response   HttpServletResponse
     */
    @RequestMapping(value = "/recurseunits/{parentUnit}", method = RequestMethod.GET)
    public void recurseUnits(@PathVariable String parentUnit, HttpServletResponse response) {
        Map<String, IUnitInfo> objects = CodeRepositoryUtil.getUnitMapBuyParaentRecurse(parentUnit);
        JsonResultUtils.writeSingleDataJson(objects, response);
    }

    /**
     * CP标签中DICTIONARY实现
     * 获取数据字典
     *
     * @param catalog  数据目录代码
     * @param extraCode  extraCode
     * @param request  request
     * @param response HttpServletResponse
     *
     */
    @RequestMapping(value = "/dictionary/{catalog}", method = RequestMethod.GET)
    public void dictionary(@PathVariable String catalog, String extraCode,
            HttpServletRequest request,HttpServletResponse response) {
        List<? extends IDataDictionary> listObjects = CodeRepositoryUtil.getDictionary(catalog);

        String lang = WebOptUtils.getCurrentLang(request);
        JSONArray dictJson = new JSONArray();
        for(IDataDictionary dict : listObjects){
            // 级联或者树形数据字典明细查询
            if (StringUtils.isNotBlank(extraCode) && !extraCode.equals(dict.getExtraCode()))
                continue;
            JSONObject obj = (JSONObject)JSON.toJSON(dict);
            obj.put("dataValue", dict.getLocalDataValue(lang));
            dictJson.add(obj);
        }
        JsonResultUtils.writeSingleDataJson(dictJson, response);
    }

    /**
     * CP标签中DICTIONARY_D实现
     * 获取数据字典 ，忽略 tag 为 'D'的条目 【delete】
     *
     * @param catalog  数据目录代码
     * @param request  request
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/dictionaryd/{catalog}", method = RequestMethod.GET)
    public void dictionaryd(@PathVariable String catalog,
            HttpServletRequest request,HttpServletResponse response) {
        List<? extends IDataDictionary> listObjects = CodeRepositoryUtil.getDictionaryIgnoreD(catalog);

        String lang = WebOptUtils.getCurrentLang(request);
        JSONArray dictJson = new JSONArray();
        for(IDataDictionary dict : listObjects){
            JSONObject obj = (JSONObject)JSON.toJSON(dict);
            obj.put("dataValue", dict.getLocalDataValue(lang));
            dictJson.add(obj);
        }
        JsonResultUtils.writeSingleDataJson(dictJson, response);
    }

    /**
     * CP标签中UNITUSER实现
     * 获取一个机构下面的所有用户，并且根据排序号排序
     *
     * @param unitCode 机构代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unituser/{unitCode}", method = RequestMethod.GET)
    public void unituser(@PathVariable String unitCode, HttpServletResponse response) {
        List<IUserInfo> listObjects = CodeRepositoryUtil.getSortedUnitUsers(unitCode);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * CP标签中ALLUSER实现
     * 获取所有符合状态标记的用户，
     *
     * @param state    用户状态， A 表示所有状态
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/alluser/{state}", method = RequestMethod.GET)
    public void alluser(@PathVariable String state, HttpServletResponse response) {
        List<IUserInfo> listObjects = CodeRepositoryUtil.getAllUsers(state);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * CP标签中UNITSLIST实现
     * 获取所有下级机构，
     *
     * @param unitcode 机构代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/subunits/{unitcode}", method = RequestMethod.GET)
    public void getSubUnits(@PathVariable String unitcode, HttpServletResponse response) {
        List<IUnitInfo> listObjects = CodeRepositoryUtil.getSubUnits(unitcode);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    @RequestMapping(value = "/allsubunits/{unitcode}", method = RequestMethod.GET)
    public void getAllSubUnits(@PathVariable String unitcode, HttpServletResponse response) {
        List<IUnitInfo> listObjects = CodeRepositoryUtil.getAllSubUnits(unitcode);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * 实现 机构表达式过滤
     * 获取所有符合状态标记的用户，
     *
     * @param unitfilter 机构代码
     * @param request request
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unitfilter/{unitfilter}", method = RequestMethod.GET)
    public void unitfilter(@PathVariable String unitfilter,
                           //@RequestBody Map<String,Object> varMap,
                           HttpServletRequest request, HttpServletResponse response) {

        Map<String, Set<String>> unitParams = null;
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud!=null){
            //String usercode = ud.getUserCode();
            String userUnit = ud.getCurrentUnitCode();
            if(userUnit!=null){
                unitParams = new HashMap<>();
                unitParams.put("U", CollectionsOpt.createHashSet(userUnit));
            }
        }
        Set<String> units =  SysUnitFilterEngine.calcSystemUnitsByExp(
                unitfilter,unitParams, new UserUnitMapTranslate());
        /*List<IUnitInfo> listObjects = new ArrayList<>();
        for(String uc : units){
            listObjects.add( CodeRepositoryUtil.getUnitInfoByCode(uc) );
        }*/
        JsonResultUtils.writeSingleDataJson(units, response);
    }

    /**
     * 实现 机构表达式过滤
     * 获取所有符合状态标记的用户，
     *
     * @param userfilter 机构代码
     * varMap varMap
     * @param request request
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/userfilter/{userfilter}", method = RequestMethod.GET)
    public void userfilter(@PathVariable String userfilter,
                           //@RequestBody Map<String,Object> varMap,
                           HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> unitParams = null;
        Map<String, Set<String>> userParams = null;
        CentitUserDetails ud = WebOptUtils.getLoginUser(request);
        if(ud!=null){
            String userCode = ud.getUserInfo().getUserCode();
            if(userCode!=null){
                unitParams = new HashMap<>();
                unitParams.put("O", CollectionsOpt.createHashSet(userCode));
            }
            String userUnit = ud.getCurrentUnitCode();
            if(userUnit!=null){
                unitParams = new HashMap<>();
                unitParams.put("U", CollectionsOpt.createHashSet(userUnit));
            }
        }
        Set<String> users =  SysUserFilterEngine.calcSystemOperators(userfilter,
                unitParams,userParams,null, new UserUnitMapTranslate());
        /*List<IUserInfo> listObjects = new ArrayList<>();
        for(String uc : users){
            listObjects.add( CodeRepositoryUtil.getUserInfoByCode(uc));
        }*/
        JsonResultUtils.writeSingleDataJson(users, response);
    }


    private JSONArray makeMenuFuncsJson(List<IOptInfo> menuFunsByUser) {
        return ViewDataTransform.makeTreeViewJson(menuFunsByUser,
                ViewDataTransform.createStringHashMap("id", "optId",
                        "optId", "optId",
                        "pid", "preOptId",
                        "text", "optName",
                        "optName", "optName",
                        "url", "optRoute",
                        "icon", "icon",
                        "children", "children",
                        "isInToolbar", "isInToolbar",
                        "state", "state"
                ), (jsonObject, obj) ->
                        jsonObject.put("external", !("D".equals(obj.getPageType()))));
    }

    /**
     * CP标签中OPTINFO实现
     * 按类别获取 业务定义信息
     *    S:实施业务, O:普通业务, W:流程业务, I:项目业务  , M:菜单   A: 为所有
     * @param optType  业务类别
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/optinfo/{optType}", method = RequestMethod.GET)
    public void optinfoByTypeAsMenu(@PathVariable String optType, HttpServletResponse response) {
        List<IOptInfo> listObjects = CodeRepositoryUtil.getOptinfoList(optType);
        JsonResultUtils.writeSingleDataJson(makeMenuFuncsJson(listObjects), response);
    }



    /**
     * CP标签中OPTDEF实现
     * 获得一个业务下面的操作定义
     *
     * @param optID    系统业务代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/optdef/{optID}", method = RequestMethod.GET)
    public void optdef(@PathVariable String optID, HttpServletResponse response) {
        List<? extends IOptMethod> listObjects = CodeRepositoryUtil.getOptMethodByOptID(optID);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     *
     * 根据角色类别获取角色
     *
     * @param roleType   角色类别
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/roleinfo/{roleType}", method = RequestMethod.GET)
    public void roleinfo(@PathVariable String roleType, HttpServletResponse response) {
        List<IRoleInfo> listObjects = CodeRepositoryUtil.getRoleinfoListByType(roleType);
        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * CP标签中 SYS_VALUE 实现
     * 获取系统设置的值
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/sysconfig/{paramCode}", method = RequestMethod.GET)
    public void getSysConfigValue(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        String paramCode = uri.substring(uri.lastIndexOf('/')+1);
        String pv =  CodeRepositoryUtil.getSysConfigValue(paramCode);
        JsonResultUtils.writeSingleDataJson(pv, response);
    }

    /**
     * CP标签中 SYS_VALUE 实现
     * 系统设置的参数的前缀
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     **/
    @RequestMapping(value = "/sysconfigbyprefix/{prefix}", method = RequestMethod.GET)
    public void getSysConfigByPrefix(HttpServletRequest request,
                                  HttpServletResponse response) {
        String uri = request.getRequestURI();
        String prefix = uri.substring(uri.lastIndexOf('/')+1);
        Map<String, Object> pv =  CodeRepositoryUtil.getSysConfigByPrefix(prefix);
        JsonResultUtils.writeSingleDataJson(pv, response);
    }

    /**
     * 获取用户信息
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/userdetails", method = RequestMethod.GET)
    public void getUserDetails(
            HttpServletResponse response) {
        CentitUserDetails userDetails = WebOptUtils.getLoginUser(RequestThreadLocal.getHttpThreadWrapper()
                .getRequest());
        JsonResultUtils.writeSingleDataJson(userDetails, response);
    }

    /**
     * CP标签中USERSETTING实现
     * 获取用户当前设置值
     *
     * @param paramCode 用户设置的参数
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/usersetting/{paramCode}", method = RequestMethod.GET)
    public void getUserSettingValue(@PathVariable String paramCode,
            HttpServletResponse response) {
        String pv =  CodeRepositoryUtil.getUserSettingValue(paramCode);
        JsonResultUtils.writeSingleDataJson(pv, response);
    }

    /**
     * 获取用户所有设置
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/usersettings", method = RequestMethod.GET)
    public void getUserAllSettings(HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
                CodeRepositoryUtil.getUserAllSettings(), response);
    }

    /**
     *
     * @param optId optId
     * @param method method
     * @param response response
     */
    @RequestMapping(value = "/checkuserpower/{optId}/{method}", method = { RequestMethod.GET })
    public void checkUserOptPower(@PathVariable String optId,
            @PathVariable String method, HttpServletResponse response) {
        boolean s = CodeRepositoryUtil
                .checkUserOptPower(optId,method);
        JsonResultUtils.writeSingleDataJson(s, response);
    }

    /**
     * 获取用户所有的 操作方法
     * 返回一个map，key为optid+‘-’+method value 为 'T'
     * @param response response
     */
    @RequestMapping(value = "/userallpowers", method = { RequestMethod.GET })
    public void getUserAllPowers( HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson(
                CodeRepositoryUtil.getUserAllOptPowers(), response);
    }


    @Value("${app.home:./}")
    private String appHome;

    @Value("${jdbc.url:}")
    private String jdbcUrl;

    @Value("${spring.datasource.url:}")
    private String springDatasourceUrl;

    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * 重新load Sql ExtendedMap
     * @param response response
     */
    @RequestMapping(value = "/reloadextendedsqlmap", method = { RequestMethod.GET })
    public void reloadExtendedSqlMap( HttpServletResponse response) {
        boolean hasError = false;
        StringBuilder errorMsg = new StringBuilder();
        List<File> files = FileSystemOpt.findFilesByExt(
                appHome +"/sqlscript","xml");
        DBType dbType = DBType.mapDBType(
                StringUtils.isBlank(jdbcUrl)? springDatasourceUrl :jdbcUrl );
        if(files!=null & files.size()>0){
            for(File file:files) {
                try {
                    ExtendedQueryPool.loadExtendedSqlMap(
                            new FileInputStream(file),dbType
                    );
                } catch (DocumentException | IOException e) {
                    hasError = true;
                    errorMsg.append(e.getMessage());
                    logger.error(e.getMessage(),e);
                }
            }
        }
        try {
            ExtendedQueryPool.loadResourceExtendedSqlMap(dbType);
        } catch (DocumentException | IOException e) {
            hasError = true;
            errorMsg.append(e.getMessage());
            logger.error(e.getMessage(),e);
        }
        if(hasError){
            JsonResultUtils.writeErrorMessageJson(errorMsg.toString(), response );
        }else {
            JsonResultUtils.writeSingleDataJson("Reload Extended Sql Map succeed！", response);
        }
    }
}
