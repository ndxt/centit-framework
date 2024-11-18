package com.centit.framework.components;

import com.centit.support.algorithm.StringBaseOpt;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserUnitFilterGene {

    private boolean hasUserTypeFilter;
    private boolean hasUserTagFilter;
    private boolean hasUserRoleFilter;
    private boolean hasUnitTypeFilter;
    private boolean hasUnitTagFilter;
    private boolean hasUnitFilter;
    private boolean hasUserFilter;
    private boolean hasGWFilter;
    private boolean hasXZFilter;
    private boolean hasRankFilter;
    private boolean hasRelationFilter;

    private Set<String> userTypes;
    private Set<String> userTags;
    private Set<String> unitTypes;
    private Set<String> unitTags;
    private Set<String> units;
    private Set<String> users;
    private Set<String> xzRoles;
    private Set<String> gwRoles;
    private Set<String> optRoles;
    private Set<String> userUnitRelTypes;

    private Set<String> unitWords; // DW
    private Set<String> userWords; // UW

    private String xzRank; //postRank
    private boolean rankPlus;
    private boolean rankMinus;
    private boolean rankAllTop;
    private boolean rankAllSub;

    public UserUnitFilterGene() {
        hasUnitTypeFilter = hasUnitTagFilter = hasRelationFilter
            = hasUserTypeFilter = hasUserTagFilter = hasUnitFilter = hasUserFilter
            = hasGWFilter = hasXZFilter = hasRankFilter = rankPlus = hasUserRoleFilter
            = rankMinus = rankAllTop = rankAllSub = false;

        userTypes = new HashSet<>();
        userTags = new HashSet<>();
        unitTypes = new HashSet<>();
        unitTags = new HashSet<>();
        units = new HashSet<>();
        users = new HashSet<>();
        xzRoles = new HashSet<>();
        gwRoles = new HashSet<>();
        optRoles = new HashSet<>();
        unitWords = new HashSet<>();
        userWords = new HashSet<>();
        userUnitRelTypes = new HashSet<>();
        xzRank = null;
    }

    public boolean isHasUnitTypeFilter() {
        return hasUnitTypeFilter;
    }

    public boolean isHasUnitTagFilter() {
        return hasUnitTagFilter;
    }

    public Set<String> getUnitTypes() {
        return unitTypes;
    }

    public Set<String> getUnitTags() {
        return unitTags;
    }

    public boolean isHasUserTypeFilter() {
        return hasUserTypeFilter;
    }

    public boolean isHasUserTagFilter() {
        return hasUserTagFilter;
    }

    public Set<String> getUserTypes() {
        return userTypes;
    }

    public Set<String> getUserTags() {
        return userTags;
    }

    public Set<String> getUserWords(){
        return userWords;
    }

    public Set<String> getUnitWords(){
        return unitWords;
    }

    public void addUserWord(String word){
        userWords.add(word);
    }
    public void addUserWords(Collection<String> words){
        userWords.addAll(words);
    }

    public void addUserTag(String rc) {
        userTags.add(rc);
        hasUserTagFilter = true;
    }

    public void addUserType(String rc) {
        userTypes.add(rc);
        hasUserTypeFilter = true;
    }

    public void addUnitTag(String rc) {
        unitTags.add(rc);
        hasUnitTagFilter = true;
    }

    public void addUnitWord(String word) {
        unitWords.add(word);
    }

    public void addUnitWords(Collection<String> words) {
        unitWords.addAll(words);
    }

    public void addUnitType(String rc) {
        unitTypes.add(rc);
        hasUnitTypeFilter = true;
    }

    public Set<String> getUnits() {
        return units;
    }

    public Set<String> getUsers() {
        return users;
    }

    public Set<String> getXzRoles() {
        return xzRoles;
    }

    public Set<String> getOptRoles() {
        return optRoles;
    }

    public Set<String> getGwRoles() {
        return gwRoles;
    }

    public void setXzRank(String postRank) {
        xzRank = postRank;
        hasRankFilter = true;
    }

    public boolean isHasUnitFilter() {
        return hasUnitFilter;
    }

    public boolean isHasUserFilter() {
        return hasUserFilter;
    }

    public boolean isHasGWFilter() {
        return hasGWFilter;
    }

    public boolean isHasXZFilter() {
        return hasXZFilter;
    }

    public boolean isHasRankFilter() {
        return hasRankFilter;
    }

    public boolean isHasRoleFilter() {
        return hasUserRoleFilter;
    }

    public boolean isRankMinus() {
        return rankMinus;
    }

    public void setRankMinus() {
        this.rankMinus = true;
        this.rankPlus = false;
    }

    public boolean isRankPlus() {
        return rankPlus;
    }

    public void setRankPlus() {
        this.rankPlus = true;
        this.rankMinus = false;
    }

    public boolean isRankAllTop() {
        return rankAllTop;
    }

    public boolean isHasRelationFilter() {
        return hasRelationFilter;
    }


    public Set<String> getUserUnitRelTypes() {
        return userUnitRelTypes;
    }

    public void addUserUnitRelType(String reltype) {
        if (StringUtils.isNotBlank(reltype)) {
            this.userUnitRelTypes.add(reltype);
            hasRelationFilter = true;
        }
    }

    public void setRankAllTop() {
        this.rankAllTop = true;
        this.rankAllSub = false;
        StringBaseOpt.prevCode(xzRank);
        //xzRank--;
        setRankMinus();
    }

    public boolean isRankAllSub() {
        return rankAllSub;
    }

    public void setRankAllSub() {
        this.rankAllTop = false;
        this.rankAllSub = true;
        //xzRank++;
        StringBaseOpt.nextCode(xzRank);
        setRankPlus();
    }

    public void addUnit(String sucs) {
        units.add(sucs);
        hasUnitFilter = true;
    }

    public void addUnits(Set<String> sucs) {
        if (sucs != null && sucs.size() > 0) {
            units.addAll(sucs);
            hasUnitFilter = true;
        }
    }

    public void addUser(String suc) {
        users.add(suc);
        hasUserFilter = true;
    }

    public void addUsers(Set<String> sucs) {
        if (sucs != null && sucs.size() > 0) {
            users.addAll(sucs);
            hasUserFilter = true;
        }
    }

    public void addXzRole(String rc) {
        xzRoles.add(rc);
        hasXZFilter = true;
    }

    public void addOptRole(String rc) {
        optRoles.add(rc);
        hasUserRoleFilter = true;
    }

    public void addGwRole(String rc) {
        gwRoles.add(rc);
        hasGWFilter = true;
    }

    public boolean matchRank(String nR) {
        int nc = StringUtils.compare(nR, xzRank);
        return rankPlus ?
            (nc >= 0) : (rankMinus ? nc < 0 : nc == 0);
    }

}// end of class RoleFilterGene
