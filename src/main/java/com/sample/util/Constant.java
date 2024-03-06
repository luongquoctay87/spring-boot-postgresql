package com.sample.util;

public interface Constant {

    interface Regex {
        String SEARCH_OPERATOR = "(\\w+?)(:|<|>)(.*)";
        String SORT_OPERATOR = "(\\w+?)(:)(.*)";
    }
}
