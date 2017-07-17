package com.centit.framework.system.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ObjectException;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.DataCatalog;
import com.centit.framework.system.po.DataDictionary;
import com.centit.framework.system.po.DataDictionaryId;
import com.centit.framework.system.service.DataDictionaryManager;

@Controller
@RequestMapping("/dictionary")
public class DataDictionaryController extends BaseController {

    public static final String F = "F";
    public static final String S = "S";
    public static final String U = "U";
    public static final String T = "T";

    @Value("${sys.multi_lang}")
    private boolean multiLang;
    
    @Resource
    protected DataDictionaryManager dataCatalogManager;

    @Resource
    private DataDictionaryManager dataDictionaryManager;

    /**
     * 查询所有数据目录列表
     *
     * @param field    只需要的属性名
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {data:[]}
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);

        if (null == searchColumn.get(CodeBook.SELF_ORDER_BY)) {
            searchColumn.put(CodeBook.ORDER_BY_HQL_ID, "lastModifyDate DESC");
        }
        
        List<DataCatalog> listObjects = dataCatalogManager.listObjects(searchColumn, pageDesc);

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(DataCatalog.class, field);
        }

        if (null == pageDesc) {
            JsonResultUtils.writeSingleDataJson(listObjects, response, simplePropertyPreFilter);

            return;
        }
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);
        resData.addResponseData(CodeBook.SELF_ORDER_BY, searchColumn.get(CodeBook.SELF_ORDER_BY));

        JsonResultUtils.writeResponseDataAsJson(resData, response, simplePropertyPreFilter);
    }

    /**
     * 查询单个数据目录
     *
     * @param catalogCode DataCatalog主键
     * @param response    {@link HttpServletResponse}
     * @return {data:{}}
     */
    @RequestMapping(value = "/{catalogCode}", method = {RequestMethod.GET})
    public void getCatalog(@PathVariable String catalogCode, HttpServletResponse response) {
        
        DataCatalog dbDataCatalog = dataCatalogManager.getCatalogIncludeDataPiece(catalogCode);

        JsonResultUtils.writeSingleDataJson(dbDataCatalog, response);
    }

    /**
     * catalogCode是否已存在
     *
     * @param catalogCode
     * @param response
     */
    @RequestMapping(value = "/notexists/{catalogCode}", method = {RequestMethod.GET})
    public void isNotExistsCatalogCode(@PathVariable String catalogCode, HttpServletResponse response) throws IOException {
        DataCatalog dbDataCatalog = dataCatalogManager.getObjectById(catalogCode);
        JsonResultUtils.writeOriginalObject(null == dbDataCatalog, response);
    }

    /**
     * dataCode是否已存在
     *
     * @param catalogCode
     * @param dataCode
     * @param response
     */
    @RequestMapping(value = "/notexists/dictionary/{catalogCode}/{dataCode}", method = {RequestMethod.GET})
    public void isNotExistsDataCode(@PathVariable String catalogCode, @PathVariable String dataCode, HttpServletResponse response) throws IOException {
        DataDictionary dbDataDictionary = dataDictionaryManager.getDataDictionaryPiece(new DataDictionaryId(catalogCode,
                dataCode));

        JsonResultUtils.writeOriginalObject(null == dbDataDictionary, response);
    }

    /**
     * 查询单个数据字典
     *
     * @param catalogCode DataCatalog主键
     * @param dataCode    DataDictionary主键
     * @param response    {@link HttpServletResponse}
     * @return {data:{}}
     */
    @RequestMapping(value = "/dictionary/{catalogCode}/{dataCode}", method = {RequestMethod.GET})
    public void getDictionary(@PathVariable String catalogCode, @PathVariable String dataCode,
                              HttpServletResponse response) {
        DataDictionary dbDataDictionary = dataDictionaryManager.getDataDictionaryPiece(new DataDictionaryId(catalogCode,
                dataCode));

        JsonResultUtils.writeSingleDataJson(dbDataDictionary, response);
    }

    /**
     * 新增数据目录
     *
     * @param dataCatalog DataCatalog
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST})
    public void createCatalog(@Valid DataCatalog dataCatalog,
            HttpServletRequest request,HttpServletResponse response) {
        catalogPrInsertHander(dataCatalog,request);
        dataCatalogManager.mergeObject(dataCatalog);
        JsonResultUtils.writeBlankJson(response);
    }


    /**
     * 新增或保存数据目录
     *
     * @param catalogCode DataCatalog主键
     * @param dataCatalog {@link DataCatalog}
     * @param response    {@link HttpServletResponse}
     */
    @RequestMapping(value = "/{catalogCode}", method = {RequestMethod.PUT})
    public void updateCatalog(@PathVariable String catalogCode,
            @Valid DataCatalog dataCatalog, 
            HttpServletRequest request,HttpServletResponse response) {
    	
        DataCatalog dbDataCatalog = dataCatalogManager.getObjectById(catalogCode);

        if (null != dbDataCatalog) {
            catalogPrUpdateHander(dataCatalog, dbDataCatalog);
            
            BeanUtils.copyProperties(dataCatalog, dbDataCatalog, new String[]{"catalogStyle","catalogCode", "dataDictionaries"});
            boolean isAdmin = isLoginAsAdmin(request);
            String datastyle = isAdmin?"S":"U";
            for(DataDictionary d:dataCatalog.getDataDictionaries()){
            	d.setDataStyle(datastyle);
            }
            dbDataCatalog.addAllDataPiece(dataCatalog.getDataDictionaries());
            
            dataCatalogManager.saveCatalogIncludeDataPiece(dbDataCatalog,isAdmin);
        } else {

            JsonResultUtils.writeErrorMessageJson("当前对象不存在", response);
            return;
        }

        JsonResultUtils.writeBlankJson(response);
    }

    private boolean isLoginAsAdmin(HttpServletRequest request){
    	 Object obj = request.getSession().getAttribute(MainFrameController.ENTRANCE_TYPE);  
         return obj!=null && MainFrameController.DEPLOY_LOGIN.equals(obj.toString());
    }
    
    /**
     * 数据目录的新增权限进行业务数据判断
     *
     * @param dataCatalog
     */
    protected void catalogPrInsertHander(DataCatalog dataCatalog,HttpServletRequest request) {
    	
    	if(isLoginAsAdmin(request)){
    		dataCatalog.setCatalogStyle("S");
    	}else{
    		dataCatalog.setCatalogStyle("U");
    	}
        // 业务异常规则地址
        // http://develop.centit.com/wiki/pages/viewpage.action?pageId=65273913
        /*if (!S.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !U.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
            throw new ObjectException("catalogStyle 字段只可填写 S 或 U");
        }*/
    }

    /**
     * 数据目录的编辑权限进行业务数据判断
     *
     * @param dataCatalog
     */
    protected void catalogPrUpdateHander(DataCatalog dataCatalog, DataCatalog dbDataCatalog) {
        //如果业务异常
        if (null == dataCatalog) {
            throw new ObjectException("DataCatalog 对象为空");
        }

        if (!U.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !S.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !S.equalsIgnoreCase(dbDataCatalog.getCatalogStyle())) {
            throw new ObjectException("catalogStyle 字段只可填写 S 或 U");
        }

        if (S.equalsIgnoreCase(dbDataCatalog.getCatalogStyle()) && !S.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
            throw new ObjectException("管理员不能将 catalogStyle 原值为 S 修改为其它值");
        }
    }

 
   
    /**
     * 新增或保存数据字典
     *
     * @param catalogCode    DataCatalog主键
     * @param dataCode       DataDictionary主键
     * @param dataDictionary {@link DataDictionary}
     * @param dataDictionary {@link DataDictionary}
     * @param response       {@link HttpServletResponse}
     */
    @RequestMapping(value = "/dictionary/{catalogCode}/{dataCode}", 
    		method = {RequestMethod.POST, RequestMethod.PUT})
    public void editDictionary(@PathVariable String catalogCode, @PathVariable String dataCode,
                               @Valid DataDictionary dataDictionary,
                               HttpServletRequest request,HttpServletResponse response) {

        DataDictionary dbDataDictionary = dataDictionaryManager.getDataDictionaryPiece(new DataDictionaryId(catalogCode,
                dataCode));
        
        DataCatalog dbDataCatalog = dataCatalogManager.getObjectById(catalogCode);
        
        dictionaryPreHander(dbDataCatalog, dataDictionary);

        if (null != dbDataDictionary) { // update
            dictionaryPreUpdateHander(dbDataCatalog, dbDataDictionary,request);
            BeanUtils.copyProperties(dataDictionary, dbDataDictionary, new String[]{"id","dataStyle"});
            dictionaryPreUpdateHander(dbDataCatalog, dbDataDictionary,request);
            dataDictionaryManager.saveDataDictionaryPiece(dbDataDictionary);
        } else { // insert            
            dictionaryPreInsertHander(dbDataCatalog, dataDictionary,request);
            dataDictionaryManager.saveDataDictionaryPiece(dataDictionary);
        }

        JsonResultUtils.writeBlankJson(response);
    }

  /**
     * 数据字典公共验证
     *
     * @param dataDictionary
     */
    protected void dictionaryPreHander(DataCatalog dataCatalog, DataDictionary dataDictionary) {
        //附加代码 EXTRACODE  字段
        //这是一个自解释字段，业务系统可以自行解释这个字段的意义，单作为树形结构的数据字典时，这个字段必需为上级字典的代码。

        if (T.equalsIgnoreCase(dataCatalog.getCatalogType())) {
            String extraCode = dataDictionary.getExtraCode();
            if (StringUtils.isBlank(extraCode)) {
                throw new ObjectException("extraCode 字段不可为空");
            }

            if (extraCode.equals(dataDictionary.getDataCode())) {
                throw new ObjectException("extraCode 与 dataCode 不能一致");
            }

            DataDictionary dd = dataDictionaryManager.getDataDictionaryPiece(new DataDictionaryId(dataDictionary.getCatalogCode(), extraCode));
            if (null == dd) {
                throw new ObjectException("当前父节点不存在");
            }
        }
    }

    /**
     * 数据字典的新增权限进行业务数据判断
     *
     * @param dataCatalog    DataCatalog
     * @param dataDictionary DataDictionary
     */
    protected void dictionaryPreInsertHander(DataCatalog dataCatalog, DataDictionary dataDictionary,
    		HttpServletRequest request) {
    	if(isLoginAsAdmin(request)){ 
    		dataDictionary.setDataStyle(S);
    	}else{    		
	        if (!S.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !U.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
	            throw new ObjectException("catalogStyle 字段只可填写 S 或 U");
	        }
	        dataDictionary.setDataStyle(U);
	        if (!U.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("dataStyle 字段只可填写 U");
	        }
	
	    }
    }

   
    /**
     * 数据字典的删除权限进行业务数据判断
     *
     * @param dataCatalog    DataCatalog
     * @param dataDictionary DataDictionary
     */
    protected void dictionaryPreDeleteHander(DataCatalog dataCatalog, DataDictionary dataDictionary,
    		HttpServletRequest request) {    	
    	if(isLoginAsAdmin(request)){
    	  if (!S.equalsIgnoreCase(dataDictionary.getDataStyle()) && !U.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("只能删除 catalogStyle为 S 或 U 的数据目录");
	        }
	    }else{
	        if (!U.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("dataStyle 字段只可填写 U");
	        }
	    }
    }


   
    /**
     * 数据字典的编辑权限进行业务数据判断
     *
     * @param dataCatalog    DataCatalog
     * @param dataDictionary DataDictionary
     */
    protected void dictionaryPreUpdateHander(DataCatalog dataCatalog, DataDictionary dataDictionary,
    		HttpServletRequest request) {
    	if(isLoginAsAdmin(request)){
	   
	        if (F.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("dataStyle 为 F 类型的数据字典，任何地方都不允许编辑，只能有开发人员给出更新脚本添加、更改和删除");
	        }
	
	        if (F.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !S.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("只能修改 dataStyle 为 S 的数据字典");
	        }
	        if (!S.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !U.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
	            throw new ObjectException("catalogStyle 字段只可填写 S 或 U");
	        }
	        if (!S.equalsIgnoreCase(dataDictionary.getDataStyle()) && !U.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("dataStyle 字段只可填写 S 或 U");
	        }
	    }else {
	        if (!U.equalsIgnoreCase(dataDictionary.getDataStyle())) {
	            throw new ObjectException("dataStyle 字段只可填写 U");
	        }
	    }
    }


    protected void catalogPrDeleteHander(DataCatalog dataCatalog,HttpServletRequest request) {
    	if(isLoginAsAdmin(request)){
	        if (!S.equalsIgnoreCase(dataCatalog.getCatalogStyle()) && !U.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
	            throw new ObjectException("只能删除 catalogStyle为 S 或 U 的数据目录");
	        }
    	}else{
	          if (!U.equalsIgnoreCase(dataCatalog.getCatalogStyle())) {
	            throw new ObjectException("只可删除 catalogStyle 为 U 的数据目录");
	          }
        }
    }

    /**
     * 删除数据目录
     *
     * @param catalogCode DataCatalog主键
     */
    @RequestMapping(value = "/{catalogCode}", method = RequestMethod.DELETE)
    public void deleteCatalog(@PathVariable String catalogCode,
    		HttpServletRequest request,HttpServletResponse response) {
        DataCatalog dataCatalog = dataCatalogManager.getObjectById(catalogCode);
        catalogPrDeleteHander(dataCatalog,request);

        dataCatalogManager.deleteDataDictionary(catalogCode);
        JsonResultUtils.writeBlankJson(response);
    }

    /**
     * 删除数据字典
     *
     * @param catalogCode DataCatalog主键
     */
    @RequestMapping(value = "/dictionary/{catalogCode}/{dataCode}", method = RequestMethod.DELETE)
    public void deleteDictionary(@PathVariable String catalogCode, @PathVariable String dataCode,
    		HttpServletRequest request,HttpServletResponse response) {
        DataCatalog dataCatalog = dataCatalogManager.getObjectById(catalogCode);
        DataDictionary dataDictionary = dataDictionaryManager.getDataDictionaryPiece(new DataDictionaryId(catalogCode, dataCode));

        dictionaryPreDeleteHander(dataCatalog, dataDictionary,request);

        dataDictionaryManager.deleteDataDictionaryPiece(dataDictionary.getId());

        JsonResultUtils.writeBlankJson(response);
    }

    
    @RequestMapping(value = "/dictionaryPiece/{catalogCode}", method = {RequestMethod.GET})
    public void getDataDictionary(@PathVariable String catalogCode, HttpServletResponse response) {
        List<DataDictionary> datas = dataCatalogManager.getDataDictionary(catalogCode);
        JsonResultUtils.writeSingleDataJson(datas, response);
    }
   
    @RequestMapping(value = "/editDictionary/{catalogCode}", method = {RequestMethod.GET})
    public void getDataDictionaryDetail(@PathVariable String catalogCode, HttpServletResponse response) {
        List<DataDictionary> datas = dataCatalogManager.getDataDictionary(catalogCode);
        ResponseData resData = new ResponseData();
        resData.addResponseData("dataDictionary", datas);
        resData.addResponseData("multiLang", multiLang);
        resData.addResponseData("langs", CodeRepositoryUtil.getLabelValueMap("SUPPORT_LANG"));
        JsonResultUtils.writeSingleDataJson(datas, response);
    }
    
    @RequestMapping(value = "/allCatalog", method = {RequestMethod.GET})
    public void getAllCatalog(HttpServletResponse response) {
        List<DataCatalog> catalogs = dataCatalogManager.listAllDataCatalog();
        JsonResultUtils.writeSingleDataJson(catalogs, response);
    }
    
    @RequestMapping(value = "/wholeDictionary", method = {RequestMethod.GET})
    public void getWholeDictionary(HttpServletResponse response) {
        List<DataCatalog> catalogs = dataCatalogManager.listAllDataCatalog();
        List<DataDictionary> dictionarys = dataCatalogManager.getWholeDictionary();
        
        ResponseData resData = new ResponseData();        
        resData.addResponseData("catalog", catalogs);
        resData.addResponseData("dictionary", dictionarys);        
        JsonResultUtils.writeResponseDataAsJson(resData,response);
    }
    
	@RequestMapping("/dictionaryprop")
	public ResponseEntity<byte[]> downloadProperties() throws IOException{
		List<DataDictionary> dictionarys = dataCatalogManager.getWholeDictionary();
		ByteArrayOutputStream out = new  ByteArrayOutputStream();
		out.write("#dictionaryprop_zh_CN.Properties\r\n".getBytes());
		 
		for(DataDictionary dict : dictionarys){
			out.write((dict.getCatalogCode()+"."+dict.getDataCode()+
					 "=" +dict.getDataValue() +"\r\n") .getBytes());
		}
		
		HttpHeaders headers = new HttpHeaders();  
		headers.setContentDispositionFormData("attachment", "dictionaryprop_zh_CN.Properties");   
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);   
 
        return new ResponseEntity<byte[]>(out.toByteArray(),    
                                          headers, HttpStatus.CREATED);
	}
}
