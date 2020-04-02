package com.centit.framework.security.model;

import com.centit.support.algorithm.StringBaseOpt;
import org.springframework.security.access.ConfigAttribute;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OptTreeNode {
    //public String urlWord;
    public List<ConfigAttribute> roleList;
    public Map<String, OptTreeNode> childList;

    public OptTreeNode(){
        roleList = null;
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
}
