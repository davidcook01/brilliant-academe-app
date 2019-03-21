package com.brilliant.academe.domain.filter;

import java.io.Serializable;
import java.util.List;

public class FilterCourseRequest implements Serializable {

    private FilterCourse filter;
    private String type;
    private List<FilterCourse> filters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FilterCourse getFilter() {
        return filter;
    }

    public void setFilter(FilterCourse filter) {
        this.filter = filter;
    }

    public List<FilterCourse> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterCourse> filters) {
        this.filters = filters;
    }
}
