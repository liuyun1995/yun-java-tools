package com.liuyun.github.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: lewis
 * @create: 2020/3/12 下午6:12
 */
public interface ReportRepository extends JpaRepository<ReportTo, String>, ReportDao {

}
