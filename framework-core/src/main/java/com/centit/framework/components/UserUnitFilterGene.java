package com.centit.framework.components;

import java.util.HashSet;
import java.util.Set;

public class UserUnitFilterGene {

    private boolean hasUserTypeFilter;
    private boolean hasUserTagFilter;
    private boolean hasUnitTypeFilter;
    private boolean hasUnitTagFilter;
    private boolean hasUnitFilter;
    private boolean hasUserFilter;
    private boolean hasGWFilter;
    private boolean hasXZFilter;
    private boolean hasRankFilter;

    private boolean onlyGetPrimaryUser;

    private Set<String> userTypes;
    private Set<String> userTags;
    private Set<String> unitTypes;
    private Set<String> unitTags;
    private Set<String> units;
    private Set<String> users;
    private Set<String> xzRoles;
    private Set<String> gwRoles;

    private int xzRank;
    private boolean rankPlus;
    private boolean rankMinus;
    private boolean rankAllTop;
    private boolean rankAllSub;

    public UserUnitFilterGene() {
        hasUnitTypeFilter = hasUnitTagFilter
            = hasUserTypeFilter = hasUserTagFilter = hasUnitFilter = hasUserFilter
            = hasGWFilter = hasXZFilter = hasRankFilter = rankPlus
            = rankMinus = rankAllTop = rankAllSub = false;

        userTypes = new HashSet<>();
        userTags = new HashSet<>();
        unitTypes = new HashSet<>();
        unitTags = new HashSet<>();
        units = new HashSet<>();
        users = new HashSet<>();
        xzRoles = new HashSet<>();
        gwRoles = new HashSet<>();
        xzRank = 0;
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

    public int getXzRank() {
        return xzRank;
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

    public void addUnitType(String rc) {
        unitTypes.add(rc);
        hasUnitTypeFilter = true;
    }

    public Set<String> getUnits() {
        return units;
    }

    public Set<String> getUsers() {
        // if(users==null)
        // users = new HashSet<String>();
        return users;
    }

    public Set<String> getXzRoles() {
        return xzRoles;
    }

    public Set<String> getGwRoles() {
        return gwRoles;
    }

    public void setXzRank(int r) {
        xzRank = r;
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

    public boolean isOnlyGetPrimaryUser() {
        return onlyGetPrimaryUser;
    }

    public void setOnlyGetPrimaryUser(boolean onlyGetPrimaryUser) {
        this.onlyGetPrimaryUser = onlyGetPrimaryUser;
    }

    public void setRankAllTop() {
        this.rankAllTop = true;
        this.rankAllSub = false;
        xzRank--;
        setRankMinus();
    }

    public boolean isRankAllSub() {
        return rankAllSub;
    }

    public void setRankAllSub() {
        this.rankAllTop = false;
        this.rankAllSub = true;
        xzRank++;
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

    public void addGwRole(String rc) {
        gwRoles.add(rc);
        hasGWFilter = true;
    }

    public boolean matchRank(int nR) {
        return rankPlus ? (nR >= xzRank) : (rankMinus ? nR <= xzRank : nR == xzRank);
    }

}// end of class RoleFilterGene
