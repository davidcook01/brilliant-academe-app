package com.brilliant.academe.domain.filter;

import java.io.Serializable;

public class FilterCourse implements Serializable {

    private String filterName;
    private String filterValue;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
}
