package com.liuyun.github;

import com.liuyun.github.jpa.ReportRepository;
import com.liuyun.github.jpa.ReportTo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: lewis
 * @create: 2020/3/12 下午6:08
 */
public class JpaTest extends BaseTest {

    @Autowired
    private ReportRepository reportRepository;

    @Test
    public void test1() {
        ReportTo reportTo = reportRepository.findById("1").orElse(null);
        System.out.println(reportTo);
    }


    @Test
    public void test2() {
        List<ReportTo> list = reportRepository.findByCase(null, null);
        System.out.println(list);
    }


}
