package com.gill.user.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Relation
 *
 * @author gill
 * @version 2024/02/08
 **/
@Getter
@Setter
public class Relation {

    private String subject;

    private List<String> items;
}
