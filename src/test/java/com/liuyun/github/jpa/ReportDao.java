package com.liuyun.github.jpa;

import java.util.List;

/**
 * @author: lewis
 * @create: 2020/3/13 下午6:02
 */
public interface ReportDao {


    List<ReportTo> findByCase(String companyId, String reportToType);


}
