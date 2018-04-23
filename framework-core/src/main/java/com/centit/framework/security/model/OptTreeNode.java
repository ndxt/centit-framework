package com.centit.framework.security.model;

import com.centit.support.algorithm.StringBaseOpt;

import java.util.HashMap;
import java.util.Map;


public class OptTreeNode {
    //public String urlWord;  
    public String optCode; 
    public Map<String,OptTreeNode> childList;  
    
    public OptTreeNode(){
        
    }
    
    public String getOptCode() {
        return optCode;
    }
    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }
    public Map<String, OptTreeNode> getChildList() {
        return childList;
    }
    
    public OptTreeNode getChild(String surl){
        if(childList==null)
            childList = new HashMap<String,OptTreeNode>();
        return childList.get(surl);
    }
    
    
    public OptTreeNode setChildPath(String surl){
        if(childList==null)
            childList = new HashMap<String,OptTreeNode>();
        OptTreeNode child = childList.get(surl);
        if(child==null){
            child = new OptTreeNode();
            childList.put(surl, child);
        }
        return child;
    }
    
    public OptTreeNode setChildOptCode(String surl,String optCode){
        if(childList==null)
            childList = new HashMap<String,OptTreeNode>();
        OptTreeNode child = childList.get(surl);
        if(child==null){
            child = new OptTreeNode();
            childList.put(surl, child);
        }
        child.setOptCode(optCode);
        return child;
    }
    
    public void setChildList(Map<String, OptTreeNode> childList) {
        this.childList = childList;
    }
    
    private void printTreeNode(OptTreeNode treeNode,int n){
        
        if(treeNode.getChildList()==null)
            return;
        for(Map.Entry<String,OptTreeNode> child : treeNode.getChildList().entrySet()){
            System.out.println( StringBaseOpt.multiplyString("  ",n) + child.getKey()+":"+child.getValue().getOptCode());
            printTreeNode(child.getValue(),n+1);
        }
    }
    
    public void printTreeNode(){
        System.out.println(this.optCode);
        printTreeNode(this,1);
        System.out.println("--------------------------------");
    }
}
