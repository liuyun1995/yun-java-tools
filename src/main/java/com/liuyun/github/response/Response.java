package com.liuyun.github.response;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;

@Data
public class Response<T> implements Serializable {

    /** 返回状态 */
    private Integer status;
    /** 返回信息 */
    private String message;
    /** 返回数据 */
    private T data;
    /** 分页信息 */
    private Paging paging;

    public static final ResponseStatus SUCCESS = ResponseStatus.SUCCESS;
    public static final ResponseStatus ERROR_2000 = ResponseStatus.ERROR_2000;
    public static final ResponseStatus ERROR_2001 = ResponseStatus.ERROR_2001;

    private Response(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static Response success() {
        return Response.of(SUCCESS, null);
    }

    public static <T> Response success(T data) {
        return Response.of(SUCCESS, data);
    }

    public static Response error() {
        return Response.of(ERROR_2000, null);
    }

    public static Response of(ResponseStatus status) {
        return Response.of(status, null);
    }

    public static <T> Response of(ResponseStatus status, T data) {
        return Response.of(status.code, status.text, data);
    }

    public static <T> Response of(Integer status, String message, T data) {
        return new Response(status, message, data);
    }

    public Response paged(int pageNo, int pageSize, int listSize, int totalHits) {
        this.paging = new Paging(pageNo, pageSize, listSize, totalHits);
        return this;
    }

    public Response paged(Page page) {
        this.paging = new Paging(page.getNumber(), page.getSize(), page.getContent().size(), page.getTotalElements());
        return this;
    }

    @Data
    public static class Paging implements Serializable {

        /** 当前页 */
        private int pageNo;
        /** 页面大小 */
        private int pageSize;
        /** 总记录数 */
        private long totalRows;
        /** 总页数 */
        private int totalPage;
        /** 是否有下一页 */
        private boolean hasNext;

        /**
         * 构造方法
         * @param pageNo 当前页码
         * @param pageSize 页面大小
         * @param listSize 当前页记录数
         * @param totalRows 总记录数
         */
        public Paging(int pageNo, int pageSize, int listSize, long totalRows) {
            this.hasNext = hasNext(pageNo, pageSize, listSize, totalRows);
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.totalRows = totalRows;
            this.totalPage = getTotalPage(pageSize, totalRows);
        }

        private boolean hasNext(int pageNo, int pageSize, int listSize, long totalRows) {
            if (totalRows <= 0 || listSize <= 0){
                return false;
            }
            return totalRows > (pageNo - 1) * pageSize + listSize;
        }

        private Integer getTotalPage(int pageSize, long totalRows) {
            return Long.valueOf((totalRows - 1) / pageSize + 1).intValue();
        }

    }

    private enum ResponseStatus {

        /** 请求成功 */
        SUCCESS(1000, "请求成功"),

        /** 内部错误 */
        ERROR_2000(2000, "内部错误"),

        /** 参数错误 */
        ERROR_2001(2001, "参数错误");

        public final Integer code;
        public final String text;

        ResponseStatus(Integer code, String text) {
            this.code = code;
            this.text = text;
        }

    }

}