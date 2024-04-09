package com.centit.framework.model.security;

import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.ConfigAttribute;

import java.io.Serializable;
import java.util.*;


public class OptTreeNode implements Serializable {
    //public String urlWord;
    public List<ConfigAttribute> roleList;
    public Map<String, OptTreeNode> childList;

    public OptTreeNode(){
        roleList = new ArrayList<>();
        childList = null;
    }

    public List<ConfigAttribute> getRoleList() {
        return roleList;
    }

    public void addRoleList(List<ConfigAttribute> attributes) {
        if(attributes==null){
            return;
        }
        if(this.roleList == null) {
            this.roleList = attributes;
        } else {
            for(ConfigAttribute role : attributes) {
                if(!this.roleList.contains(role)) {
                    this.roleList.add(role);
                }
            }
        }
        //排序
        if(this.roleList != null) {
            this.roleList.sort(
                Comparator.comparing(ConfigAttribute::getAttribute));
        }
    }

    public Map<String, OptTreeNode> getChildList() {
        return childList;
    }

    public boolean isLeafNode() {
        return childList==null || childList.isEmpty();
    }

    public OptTreeNode setChildPath(String surl){
        if(childList==null)
            childList = new HashMap<>();
        OptTreeNode child = childList.get(surl);
        if(child==null){
            child = new OptTreeNode();
            childList.put(surl, child);
        }
        return child;
    }

    private void printTreeNode(OptTreeNode treeNode, int n){
        if(treeNode.getChildList()==null)
            return;
        for(Map.Entry<String,OptTreeNode> child : treeNode.getChildList().entrySet()){
            System.out.println( StringBaseOpt.multiplyString("  ",n) +
                child.getKey() + ":" + StringBaseOpt.castObjectToString(child.getValue().getRoleList()));
            printTreeNode(child.getValue(),n+1);
        }
    }

    public void printTreeNode(){
        System.out.println(StringBaseOpt.castObjectToString(this.roleList));
        printTreeNode(this,1);
        System.out.println("--------------------------------");
    }

    /**
     * C D R  U  :  POST DELETE GET PUT
     *
     * @param sOptDefUrl sOptDefUrl
     * @param sMethod    sMethod
     * @return List parseUrl
     */
    public List<List<String>> parsePowerDefineUrl(String sOptDefUrl, String sMethod) {

        String sUrls[] = (sOptDefUrl).split("/");
        List<String> sopts = new ArrayList<>();
        for (String s : sUrls) {
            if (StringUtils.isNotBlank(s)/* && !"*".equals(s)*/)
                sopts.add(s);
        }

        List<List<String>> swords = new ArrayList<>();
        /*if(StringUtils.isBlank(sMethod)){
            List<String> fullOpts = new ArrayList<>(sopts.size()+2);
            fullOpts.add("GET");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
            return swords;
        }*/
        if (sMethod.indexOf('C') >= 0) {
            List<String> fullOpts = new ArrayList<>(sopts.size() + 2);
            fullOpts.add("POST");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if (sMethod.indexOf('D') >= 0) {
            List<String> fullOpts = new ArrayList<>(sopts.size() + 2);
            fullOpts.add("DELETE");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if (sMethod.indexOf('R') >= 0) {
            List<String> fullOpts = new ArrayList<>(sopts.size() + 2);
            fullOpts.add("GET");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }
        if (sMethod.indexOf('U') >= 0) {
            List<String> fullOpts = new ArrayList<>(sopts.size() + 2);
            fullOpts.add("PUT");
            fullOpts.addAll(sopts);
            swords.add(fullOpts);
        }

        return swords;
    }
}
