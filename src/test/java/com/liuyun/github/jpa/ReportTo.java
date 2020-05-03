package com.liuyun.github.jpa;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: lewis
 * @create: 2020/3/12 下午6:12
 */
@Data
@Entity
@Table(name = "tab_report_to")
public class ReportTo {

    @Id
    private String id;

    @Column
    private String companyId;

    @Column
    private String reportToType;

    @Column
    private String staffId;

    @Column
    private String superiorId;

}
