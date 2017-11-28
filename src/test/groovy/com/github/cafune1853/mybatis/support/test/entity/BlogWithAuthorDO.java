package com.github.cafune1853.mybatis.support.test.entity;

import lombok.Data;

@Data
public class BlogWithAuthorDO {
    private Long id;
    private String title;
    private String content;
    private AuthorDO authorDO;
}
