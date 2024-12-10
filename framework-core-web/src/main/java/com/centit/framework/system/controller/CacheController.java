package com.centit.framework.system.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.centit.framework.common.ViewDataTransform;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUnitFilterEngine;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.components.impl.UserUnitMapTranslate;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.controller.WrapUpResponseBody;
import com.centit.framework.model.basedata.*;
import com.centit.framework.model.security.CentitUserDetails;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.common.ObjectException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
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
public class CacheController extends BaseController {

    public String getOptId (){
        return "cp";
    }
    /**
     * cp标签中MAPVALUE实现，获取数据字典对应的值
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,
     *                             unitcode,depno,rolecode,optid,optcode,optdesc,]
     *                             以及数据目录中的catalogCode变量值
     * @param key      对应的变量值 或 数据字典中的 dataCode
     * @return ResponseData
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
    @WrapUpResponseBody
    public String mapvalue(@PathVariable String catalog, @PathVariable String key) {
        return CodeRepositoryUtil.getValue(catalog, key);
    }

    /**
     * cp标签中MAPCODE实现，获取数据字典对应的值
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,unitcode,depno,rolecode,optid,optcode,optdesc,]以及数据目录中的catalogCode变量值
     * @param value    对应的变量值 或 数据字典中的 dataValue
     * @return String 数据字典代码
     */
    @ApiOperation(value="数据字典取健",notes="和mapvalue相反他是根据数据字典的类别和value获取对应的key。")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "catalog", value="数据字典的类别代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "value", value="数据字典的值",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/mapcode/{catalog}/{value}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String mapcode(@PathVariable String catalog, @PathVariable String value) {
        return CodeRepositoryUtil.getCode(catalog, value);
    }


    /**
     * cp标签中LVB实现，获取 数据字典 key value 对
     *
     * @param catalog  系统内置的类别
     *                 字符串[userCode,loginName,unitcode,depno,
     *                 rolecode,optid,optcode,optdesc,]，
     *                 数据目录中的catalogCode变量值
     * @return Map 数据字典对应表
     */
    @ApiOperation(value="获取数据字典明细",notes="根据数据字典类别代码获取数据字典明细。")
    @ApiImplicitParam(
        name = "catalog", value="数据字典的类别代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/lvb/{catalog}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String,String> lvb(@PathVariable String catalog, HttpServletRequest request) {
        Map<String,String> keyValueMap = CodeRepositoryUtil.getLabelValueMap(catalog);
        if(keyValueMap==null || keyValueMap.isEmpty()){
            throw new ObjectException(catalog, ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.dictionary_not_found", request));
        }
        return keyValueMap;
    }

    /**
     * cp标签中MAPEXPRESSION实现
     * 把表达式中的字典代码都 转换为 数据字典值，其他的字符 位置不变，
     *
     * @param catalog    数据字典代码
     * @param expression 表达式
     * @return ResponseData
     */
    @ApiOperation(value = "将字典代码转字典值", notes = "根据字典代码转成对应的字典值，其他的字符位置不变")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "catalog", value="数据字典的类别代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "expression", value="表达式",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/mapexpression/{catalog}/{expression}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String mapexpression(@PathVariable String catalog, String expression) {
        return CodeRepositoryUtil.transExpression(catalog, expression);
    }

    /**
     * cp标签中MAPSTATE实现，获得数据字典条目的状态
     *
     * @param catalog  系统内置的类别 字符串[userCode,loginName,unitcode,rolecode,]以及数据目录中的catalogCode变量值
     * @param key      对应的变量值 或 数据字典中的 dataCode
     * @return ResponseData
     */
    @ApiOperation(value = "获得数据字典条目的状态", notes = "根据字典代码和条目获取条目的状态")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "catalog", value="数据字典的类别代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "key", value="数据字典的条目代码",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/mapstate/{catalog}/{key}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String mapstate(@PathVariable String catalog, @PathVariable String key, HttpServletRequest request) {
        return CodeRepositoryUtil.getItemState(catalog, key,WebOptUtils.getCurrentTopUnit(request));
    }

    /**
     * cp标签中SUBUNITS实现
     * 获取机构下面的所有下级机构，并且排序
     * @param request 请求体
     * @param unitCode 机构代码
     * @param unitType 机构类别
     * @return ResponseData
     */
    @ApiOperation(value = "获取机构下面的所有下级机构", notes = "获取机构下面的所有下级机构，并且排序")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "unitCode", value="机构的代码",
        required=true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "unitType", value="机构类别",
        required= true, paramType = "path", dataType= "String"
    )})
    @RequestMapping(value = "/subunits/{unitCode}/{unitType}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> subunits(@PathVariable String unitCode, @PathVariable String unitType, HttpServletRequest request) {
        return CodeRepositoryUtil.getSortedSubUnits(WebOptUtils.getCurrentTopUnit(request), unitCode, unitType);
    }

    /**
     * cp标签中ALLUNITS实现
     * 根据状态获取所有机构信息，
     * @param request 请求体
     * @param state    A表示所有状态
     * @return ResponseData
     */
    @ApiOperation(value = "根据状态获取所有机构信息", notes = "根据状态获取所有机构信息")
    @ApiImplicitParam(
        name = "state", value="状态，A表示所有状态",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/allunits/{state}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> allunits(@PathVariable String state, HttpServletRequest request) {
        return CodeRepositoryUtil.getAllUnits(WebOptUtils.getCurrentTopUnit(request), state);
    }

    /**
     * 根据用户编码获取用户信息
     * @param request 请求体
     * @param userCode 用户编码
     * @return ResponseData
     */
    @ApiOperation(value = "根据用户编码获取用户信息", notes = "根据用户编码获取用户详细信息")
    @ApiImplicitParam(
        name = "userCode", value="用户编码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/userinfo/{userCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public UserInfo getUserInfo(@PathVariable String userCode, HttpServletRequest request) {
        return CodeRepositoryUtil.getUserInfoByCode(WebOptUtils.getCurrentTopUnit(request), userCode);
    }

    /**
     * 根据机构编码获取机构信息
     * @param request 请求体
     * @param unitCode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "根据机构代码获取机构信息", notes = "根据机构代码获取机构详细信息")
    @ApiImplicitParam(
        name = "unitCode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/unitinfo/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public UnitInfo getUintInfo(@PathVariable String unitCode, HttpServletRequest request) {
        return CodeRepositoryUtil.getUnitInfoByCode(WebOptUtils.getCurrentTopUnit(request), unitCode);
    }

    /**
     * 根据机构代码获取父机构
     * @param request 请求体
     * @param unitCode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "根据机构代码获取父机构", notes = "根据机构代码获取父机构详细信息")
    @ApiImplicitParam(
        name = "unitCode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/parentunit/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String getParentUintInfo(@PathVariable String unitCode, HttpServletRequest request) {
        UnitInfo ui = CodeRepositoryUtil.getUnitInfoByCode(WebOptUtils.getCurrentTopUnit(request), unitCode);
        if(ui!=null){
           return ui.getParentUnit();
        }else {
           throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION,
               getI18nMessage("error.604.unit_not_found", request, unitCode));
        }
    }

    /**
     * 根据机构代码获取父机构路径
     * @param request 请求体
     * @param unitCode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "根据机构代码获取父机构路径", notes = "根据机构代码获取父机构的机构路径")
    @ApiImplicitParam(
        name = "unitCode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/parentpath/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> getParentUintPath(@PathVariable String unitCode, HttpServletRequest request) {
        UnitInfo ui = CodeRepositoryUtil.getUnitInfoByCode(WebOptUtils.getCurrentTopUnit(request), unitCode);
        if(ui!=null){
            List<UnitInfo> parentUnits = new ArrayList<>();
            while(true) {
                if(StringUtils.isBlank(ui.getParentUnit())){
                    break;
                }
                UnitInfo parentUnit = CodeRepositoryUtil.getUnitInfoByCode(ui.getTopUnit(), ui.getParentUnit());
                if(parentUnit==null){
                    break;
                }
                parentUnits.add(parentUnit);
                ui = parentUnit;
            }
            return parentUnits;
        }else {
            throw new ObjectException(ObjectException.DATA_NOT_FOUND_EXCEPTION,
                getI18nMessage("error.604.unit_not_found", request, unitCode));
        }
    }
    /**
     * cp标签中RECURSEUNITS实现
     * 获得已知机构 下级的所有有效机构并返回map，包括下级机构的下级机构
     * @param request 请求体
     * @param parentUnit 父级机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "获得已知机构 下级的所有有效机构", notes = "获得已知机构 下级的所有有效机构并返回map，包括下级机构的下级机构")
    @ApiImplicitParam(
        name = "parentUnit", value="父级机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/recurseunits/{parentUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, UnitInfo> recurseUnits(@PathVariable String parentUnit, HttpServletRequest request) {
        return CodeRepositoryUtil.getUnitMapBuyParaentRecurse(WebOptUtils.getCurrentTopUnit(request), parentUnit);
    }

    /**
     * CP标签中DICTIONARY实现
     * 获取数据字典
     *
     * @param catalog  数据目类别码
     * @param extraCode  扩展代码
     * @param request  HttpServletRequest
     * @return ResponseData
     *
     */
    @ApiOperation(value = "获取数据字典", notes = "根据类别编码和扩展编码获取数据字典")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "catalog", value="字典类别代码",
        required= true, paramType = "path", dataType= "String"
    ),@ApiImplicitParam(
        name = "extraCode", value="扩展代码",
        paramType = "query", dataType= "String"
    ),@ApiImplicitParam(
        name = "dataTag", value="数据标签",
        paramType = "query", dataType= "String"
    )})
    @RequestMapping(value = "/dictionary/{catalog}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray dictionary(@PathVariable String catalog,
                                @RequestParam(required=false, name = "extraCode") String extraCode,
                                @RequestParam(required=false, name = "dataTag") String dataTag,
                                HttpServletRequest request) {
        List<DataDictionary> listObjects = CodeRepositoryUtil.getDictionary(catalog);
        if(listObjects==null){
            return null;
        }
        String lang = WebOptUtils.getCurrentLang(request);
        JSONArray dictJson = new JSONArray();
        for(DataDictionary dict : listObjects){
            // 级联或者树形数据字典明细查询
            if (StringUtils.isNotBlank(extraCode) && !extraCode.equals(dict.getExtraCode()))
                continue;
            if (StringUtils.isNotBlank(dataTag) && !dataTag.equals(dict.getDataTag()))
                continue;
            JSONObject obj = (JSONObject)JSON.toJSON(dict);
            obj.put("dataValue", dict.getLocalDataValue(lang));
            dictJson.add(obj);
        }
        return dictJson;
    }

    /**
     * CP标签中DICTIONARY_D实现
     * 根据字典类别编码获取数据字典 ，忽略 tag 为 'D'的条目 【delete】
     *
     * @param catalog  数据目录代码
     * @param request  HttpServletRequest
     * @return ResponseData
     */
    @ApiOperation(value = "根据字典类别编码获取数据字典", notes = "根据字典类别编码获取数据字典 ，忽略 tag 为 'D'的条目 【delete】")
    @ApiImplicitParam(
        name = "catalog", value="字典类别编码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/dictionaryd/{catalog}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray dictionaryd(@PathVariable String catalog,
            HttpServletRequest request) {
        List<DataDictionary> listObjects = CodeRepositoryUtil.getDictionaryIgnoreD(catalog);
        if(listObjects==null){
            return null;
        }
        String lang = WebOptUtils.getCurrentLang(request);
        JSONArray dictJson = new JSONArray();
        for(DataDictionary dict : listObjects){
            JSONObject obj = (JSONObject)JSON.toJSON(dict);
            obj.put("dataValue", dict.getLocalDataValue(lang));
            dictJson.add(obj);
        }
        return dictJson;
    }

    /**
     * CP标签中UNITUSER实现
     * 获取一个机构下面的所有用户，并且根据排序号排序
     * @param request 请求体
     * @param unitCode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "获取一个机构下面的所有用户", notes = "获取一个机构下面的所有用户，并且根据排序号排序")
    @ApiImplicitParam(
        name = "unitCode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/unituser/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    @Deprecated
    public List<UserInfo> unituser(@PathVariable String unitCode, HttpServletRequest request) {
        List<UserInfo> userInfos = CodeRepositoryUtil.getSortedUnitUsers(WebOptUtils.getCurrentTopUnit(request), unitCode);
        String relType = request.getParameter("relType");
        if ("T".equals(relType) && CollectionUtils.isNotEmpty(userInfos)) {
            userInfos.removeIf(user -> !unitCode.equals(user.getPrimaryUnit()));
        }
        return userInfos;
    }


    /**
     * CP标签中UNITROLE实现
     * 获取一个机构下面的用户的岗位 或者 行政职务
     * @param request 请求体
     * @param unitCode 机构代码
     * @param roleType 角色类别 ST 岗位 RT 行政职务
     * @return ResponseData
     */
    @ApiOperation(value = "获取一个机构下面的所有用户", notes = "获取一个机构下面的所有用户，并且根据排序号排序")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "unitCode", value="机构代码",
            required= true, paramType = "path", dataType= "String"
        ),
        @ApiImplicitParam(
            name = "roleType", value="角色类别 ST 岗位 RT 行政职务",
            required= true, paramType = "path", dataType= "String"
        )})
    @RequestMapping(value = "/unitrole/{roleType}/{unitCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<DataDictionary> unitRole(@PathVariable String roleType, @PathVariable String unitCode, HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        List<DataDictionary> dataDictionaries = new ArrayList<>();
        List<UserUnit> unitUsers = CodeRepositoryUtil.listUnitUsers(unitCode);
        Set<String> roles = new HashSet<>();
        List<DataDictionary> allDataDictionaries = null;
        if("RT".equals(roleType)){
            allDataDictionaries = CodeRepositoryUtil.getDictionary(topUnit + "-RT");
            for(UserUnit uu : unitUsers){
                roles.add(uu.getUserRank());
            }
        } else if("ST".equals(roleType)){
            allDataDictionaries = CodeRepositoryUtil.getDictionary(topUnit + "-ST");
            for(UserUnit uu : unitUsers){
                roles.add(uu.getUserStation());
            }
        }
        if(allDataDictionaries!=null) {
            for (DataDictionary dict : allDataDictionaries) {
                if (roles.contains(dict.getDataCode())) {
                    dataDictionaries.add(dict);
                }
            }
        }
        return dataDictionaries;
    }

    /**
     * CP标签中ALLUSER实现
     * 获取当前租户所有的用户信息
     * @param request 请求体
     * @param state 租户信息
     * @return ResponseData
     */
    @ApiOperation(value = "获取所有符合状态标记的用户", notes = "根据状态获取所有用户")
    @ApiImplicitParam(
        name = "state", value="用户状态 A 表示所有状态",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/alluser/{state}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UserInfo> alluser(@PathVariable String state,HttpServletRequest request) {
        return CodeRepositoryUtil.getAllUsers(WebOptUtils.getCurrentTopUnit(request), state);
    }

    /**
     * CP标签中UNITSLIST实现
     * 获取所有下级机构，
     * @param request 请求体
     * @param unitcode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "获取所有下级机构", notes = "根据机构代码获取所有下级机构")
    @ApiImplicitParam(
        name = "unitcode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/subunits/{unitcode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> getSubUnits(@PathVariable String unitcode, HttpServletRequest request) {
        return CodeRepositoryUtil.getSubUnits(WebOptUtils.getCurrentTopUnit(request), unitcode);
    }

    /**
     * 获取机构的下级机构，并按照树形排列
     *
     * @param unitcode 机构代码
     * @return ResponseData
     */
    @ApiOperation(value = "获取所有下级机构树形排列", notes = "根据机构代码获取所有下级机构，并按照树形排列")
    @ApiImplicitParam(
        name = "unitcode", value="机构代码",
        required= true, paramType = "path", dataType= "String"
    )
    @WrapUpResponseBody
    @RequestMapping(value = "/allsubunits/{unitcode}", method = RequestMethod.GET)
    public List<UnitInfo> getAllSubUnits(@PathVariable String unitcode, HttpServletRequest request) {
        return CodeRepositoryUtil.getAllSubUnits(WebOptUtils.getCurrentTopUnit(request), unitcode);

    }

    static Map<String, Object> makeCalcParam(Object ud){
        Map<String, Object> dpf = new HashMap<>();
        if(ud == null){
            return dpf;
        }
        UserInfo userInfo = null;
        CentitUserDetails userDetails = null;
        String userCode = null;
        String topUnit = null;
        if(ud instanceof CentitUserDetails){
            userDetails = (CentitUserDetails) ud;
            userInfo = userDetails.getUserInfo();
            userCode = userDetails.getUserCode();
            topUnit = userDetails.getTopUnitCode();
        }else if(ud instanceof UserInfo){
            userCode = ((UserInfo)ud).getUserCode();
            topUnit = ((UserInfo)ud).getTopUnit();
        }
        //当前用户信息
        dpf.put("currentUser", userInfo);
        if(userDetails!=null) {
            dpf.put("currentStation", userDetails.getCurrentStation());
            //当前用户主机构信息
            dpf.put("primaryUnit", CodeRepositoryUtil
                .getUnitInfoByCode(userDetails.getUserInfo().getTopUnit(),
                    userDetails.getUserInfo().getPrimaryUnit()));
            //当前用户的角色信息
            dpf.put("userRoles", userDetails.getUserRoles());
        }
        //当前用户所有机构关联关系信息
        if(StringUtils.isNotBlank(userCode)) {
            List<UserUnit> userUnits = CodeRepositoryUtil
                .listUserUnits(topUnit, userCode);
            if (userUnits != null) {
                dpf.put("userUnits", userUnits);
                Map<String, List<UserUnit>> rankUnits = new HashMap<>(5);
                Map<String, List<UserUnit>> stationUnits = new HashMap<>(5);
                for (UserUnit uu : userUnits) {
                    List<UserUnit> rankUnit = rankUnits.get(uu.getUserRank());
                    if (rankUnit == null) {
                        rankUnit = new ArrayList<>(4);
                    }
                    rankUnit.add(uu);
                    rankUnits.put(uu.getUserRank(), rankUnit);

                    List<UserUnit> stationUnit = stationUnits.get(uu.getUserStation());
                    if (stationUnit == null) {
                        stationUnit = new ArrayList<>(4);
                    }
                    stationUnit.add(uu);
                    stationUnits.put(uu.getUserStation(), rankUnit);
                }
                dpf.put("rankUnits", rankUnits);
                dpf.put("stationUnits", stationUnits);
            }
        }
        return dpf;
    }

    /**
     * 实现 机构表达式过滤
     * 获取所有符合状态标记的用户，
     *
     * @param unitfilter 机构代码
     * @param request HttpServletRequest
     * @return UnitInfo 机构列表
     */
    @ApiOperation(value = "根据机构表达式获取符合条件的机构",
        notes = "根据机构表达式获取符合条件的结构，系统通过机构规则引擎计算;表达式完整式 D()DT()DL()")
    @ApiImplicitParam(
        name = "unitfilter", value="机构表达式，示例：D('D111' - 2)",
        required= true, paramType = "path", dataType= "String"
    )
    //@RecordOperationLog(content = "查询机构表达式为{unitfilter}的机构")
    // @ParamName("unitfilter")
    @RequestMapping(value = "/unitfilter/{unitfilter}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<UnitInfo> unitfilter(@PathVariable String unitfilter,
                           HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Object centitUserDetails = WebOptUtils.getLoginUser(request);
        Set<String> units = SysUnitFilterEngine.calcSystemUnitsByExp(
            StringEscapeUtils.unescapeHtml4(unitfilter), topUnit,null,
            new UserUnitMapTranslate(CacheController.makeCalcParam(centitUserDetails))
        );

        List<UnitInfo> retUntis = CodeRepositoryUtil.getUnitInfosByCodes(WebOptUtils.getCurrentTopUnit(request), units);
        CollectionsOpt.sortAsTree(retUntis,
            ( p,  c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()) );
        return retUntis;
    }

    /**
     * 实现 机构表达式过滤
     * 获取所有符合状态标记的用户，
     *
     * @param userfilter 用户代码
     * @param request request
     * @return UserInfo 用户列表
     */
    @ApiOperation(value = "根据用户表达式计算符合条件的用户",
        notes = "根据用户表达式计算符合条件的用户; 表达式完整式 D()DT()DL()GW()XZ()R()UT()UL()U()")
    @ApiImplicitParam(
        name = "userfilter", value="用户表达式，示例：D(all)DT('D')xz('部门经理')",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/userfilter/{userfilter}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public  List<UserInfo> userfilter(@PathVariable String userfilter,
                           //@RequestBody Map<String,Object> varMap,
                           HttpServletRequest request) {
        String topUnit = WebOptUtils.getCurrentTopUnit(request);
        Object centitUserDetails = WebOptUtils.getLoginUser(request);
        Set<String> users = SysUserFilterEngine.calcSystemOperators(
                StringEscapeUtils.unescapeHtml4(userfilter), topUnit,
            null,null,null,
            new UserUnitMapTranslate(CacheController.makeCalcParam(centitUserDetails)));
        return CodeRepositoryUtil.getUserInfosByCodes(topUnit, users);
    }

    private JSONArray makeMenuFuncsJson(List<OptInfo> menuFunsByUser) {
        return ViewDataTransform.makeTreeViewJson(menuFunsByUser,
                ViewDataTransform.createStringHashMap("id", "optId",
                        "optId", "optId",
                        "pid", "preOptId",
                        "text", "localOptName",
                        "optName", "optName",
                        "url", "optRoute",
                        "icon", "icon",
                        "children", "children",
                        "isInToolbar", "isInToolbar",
                        "state", "state",
                        "topOptId","topOptId"
                ), (jsonObject, obj) ->
                        jsonObject.put("external", !("D".equals(obj.getPageType()))));
    }

    /**
     * CP标签中OPTINFO实现
     * 按类别获取 业务定义信息
     *    S:实施业务, O:普通业务, W:流程业务, I:项目业务  , M:菜单   A: 为所有
     * @param optType  业务类别
     * @return ResponseData
     */
    @ApiOperation(value = "按类别获取业务菜单信息", notes = "按类别获取业务菜单信息")
    @ApiImplicitParam(
        name = "optType", value="业务类别",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/optinfo/{optType}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public JSONArray optinfoByTypeAsMenu(@PathVariable String optType,HttpServletRequest request) {
        List<OptInfo> listObjects = CodeRepositoryUtil.getOptinfoList(WebOptUtils.getCurrentTopUnit(request),optType);
        return makeMenuFuncsJson(listObjects);
    }

    /**
     * CP标签中OPTDEF实现
     * 获得一个业务下面的操作定义
     *
     * @param optID    系统业务代码
     * @return ResponseData
     */
    @ApiOperation(value = "获得业务下面的操作定义", notes = "获得一个业务下面的操作定义")
    @ApiImplicitParam(
        name = "optID", value="系统业务代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/optdef/{optID}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<OptMethod> optdef(@PathVariable String optID, HttpServletRequest request) {
        return CodeRepositoryUtil.getOptMethodByOptID(WebOptUtils.getCurrentTopUnit(request), optID);
    }

    /**
     * 根据租户获取角色
     * @param topUnit 租户
     * @return ResponseData
     */
    @ApiOperation(value = "根据租户代码获取角色", notes = "根据租户代码获取角色")
    @ApiImplicitParam(
        name = "topUnit", value="租户代码",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/roleinfo/{topUnit}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public List<RoleInfo> roleinfo(@PathVariable String topUnit, HttpServletRequest request) {
        return CodeRepositoryUtil.listAllRole(topUnit);
    }

    /**
     * CP标签中 SYS_VALUE 实现
     *  获取系统设置的值
     * @param request  HttpServletRequest
     * @return ResponseData
     */
    @ApiOperation(value = "获取系统设置的值", notes = "根据参数代码获取系统设置的值")
    @ApiImplicitParam(
        name = "paramCode", value="参数代码",
        required= true,paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/sysconfig/{paramCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String getSysConfigValue(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String paramCode = uri.substring(uri.lastIndexOf('/')+1);
        return  CodeRepositoryUtil.getSysConfigValue(paramCode);
    }

    /**
     * CP标签中 SYS_VALUE 实现
     * 系统设置的参数的前缀
     * @param request HttpServletRequest
     * @return ResponseData
     **/
    @ApiOperation(value = "根据参数前缀获取系统设置", notes = "根据参数前缀获取系统设置")
    @ApiImplicitParam(
        name = "prefix", value="前缀",
        required= true,paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/sysconfigbyprefix/{prefix}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, Object> getSysConfigByPrefix(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String prefix = uri.substring(uri.lastIndexOf('/')+1);
        return CodeRepositoryUtil.getSysConfigByPrefix(prefix);
    }

     /**
     * CP标签中USERSETTING实现
     * 获取用户当前设置值
     *
     * @param paramCode 用户设置的参数
     * @return ResponseData
     */
    @ApiOperation(value = "获取用户当前设置值", notes = "获取用户当前设置值")
    @ApiImplicitParam(
        name = "paramCode", value="用户设置的参数",
        required= true,paramType = "path", dataType= "String"
    )
    @RequestMapping(value = "/usersetting/{paramCode}", method = RequestMethod.GET)
    @WrapUpResponseBody
    public String getUserSettingValue(@PathVariable String paramCode, HttpServletRequest request) {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUserSettingValue(paramCode);
    }

    /**
     * 获取用户所有设置
     * @return ResponseData
     */
    @ApiOperation(value = "获取用户所有设置", notes = "获取用户所有设置")
    @RequestMapping(value = "/usersettings", method = RequestMethod.GET)
    @WrapUpResponseBody
    public Map<String, String> getUserAllSettings(HttpServletRequest request) {
        CentitUserDetails userDetails = WebOptUtils.getCurrentUserDetails(request);
        if(userDetails==null)
            return null;
        return userDetails.getUserSettings();
    }

    /**
     * 验证当前用户是否有某个操作方法的权限
     *
     * @param optId 系统业务代码
     * @param method 权限代码
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @ApiOperation(value = "验证当前用户是否有某个操作方法的权限", notes = "验证当前用户是否有某个操作方法的权限")
    @ApiImplicitParams({
        @ApiImplicitParam(
            name = "optId", value="系统业务代码",
            required= true,paramType = "path", dataType= "String"),
        @ApiImplicitParam(
        name = "method", value="操作方法",
        required= true,paramType = "path", dataType= "String")
    })
    @RequestMapping(value = "/checkuserpower/{optId}/{method}", method = { RequestMethod.GET })
    @WrapUpResponseBody
    public boolean checkUserOptPower(@PathVariable String optId,@PathVariable String method, HttpServletRequest request) {
        return CodeRepositoryUtil.checkUserOptPower(optId, method, request);
    }

    /**
     * 获取用户所有的 操作方法
     * 返回一个map，key为optid+‘-’+method value 为 'T'
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @ApiOperation(value = "获取用户所有的 操作方法", notes = "获取用户所有的 操作方法")
    @RequestMapping(value = "/userallpowers", method = { RequestMethod.GET })
    @WrapUpResponseBody
    public Map<String,String> getUserAllPowers(HttpServletRequest request) {
        return CodeRepositoryUtil.getUserAllOptPowers(request);
    }

}
