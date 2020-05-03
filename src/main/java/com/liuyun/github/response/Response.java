package com.liuyun.github.response;

import java.io.Serializable;

public class Response<T> implements Serializable {

    /** 返回状态 */
    private Integer status;
    /** 返回信息 */
    private String message;
    /** 返回数据 */
    private T data;
    /** 分页信息 */
    private Paging paging;

    private Response(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

    class Paging implements Serializable {

        /** 当前页 */
        private int pageNo;
        /** 页面大小 */
        private int pageSize;
        /** 总记录数 */
        private int totalHits;
        /** 是否有下一页 */
        private boolean hasNext;

        /**
         * 构造方法
         * @param pageNo 当前页码
         * @param pageSize 页面大小
         * @param listSize 当前页记录数
         * @param totalHits 总记录数
         */
        public Paging(int pageNo, int pageSize, int listSize, int totalHits) {
            this.hasNext = hasNext(pageNo, pageSize, listSize, totalHits);
            this.pageNo = pageNo;
            this.pageSize = pageSize;
            this.totalHits = totalHits;
        }

        private boolean hasNext(int pageNo, int pageSize, int listSize, int totalHits) {
            if (totalHits <= 0 || listSize <= 0){
                return false;
            }
            return totalHits > (pageNo - 1) * pageSize + listSize;
        }

    }

}