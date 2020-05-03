package com.liuyun.github.jpa;

import com.google.common.collect.Maps;
import com.liuyun.github.utils.TemplateUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

public class ReportRepositoryImpl implements ReportDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static String sqlPath = "mysql/student.sql";

    @Override
    public List<ReportTo> findByCase(String companyId, String reportToType) {
        Map<String, String> params = Maps.newHashMap();
        params.put("companyId", companyId);
        params.put("reportToType", reportToType);
        String sql = TemplateUtils.getRenderString(sqlPath, "findByCase", params);
        Query query = entityManager.createNativeQuery(sql, ReportTo.class);
        query.setFirstResult(2);
        query.setMaxResults(5);
        List<ReportTo> list = query.getResultList();
        return list;
    }

}
