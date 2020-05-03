
<findALL>
select *
from student s
where 1 = 1
<#if id??>
and s.id = '${id}'
</#if>
<#if name??>
and s.name = '${name}'
</#if>
</findALL>

<map>
SELECT t.company_id, t.report_to_type, COUNT(1) as count
FROM tab_report_to t
where t.company_id in ('111', '122', '123')
group by t.company_id, t.report_to_type
</map>

<findByCase>

SELECT *
FROM tab_report_to t
where 1 = 1
-- 如果存在companyId则查询
<#if companyId??>
and t.company_id = '${companyId}'
</#if>
-- 如果存在reportToType则查询
<#if reportToType??>
and t.report_to_type = '${reportToType}'
</#if>

</findByCase>