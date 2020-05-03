package com.liuyun.github.base;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    private Long id;
    /** 创建人 */
    private String creator;
    /** 创建时间 */
    private Date createTime;
    /** 修改人 */
    private String updater;
    /** 更新时间 */
    private Date updateTime;
    /** 版本号 */
    private Integer version;
    /** 备注信息 */
    private String memo;
    /** 记录状态, 0-未删除, 1-已删除 */
    private Integer status;

}
