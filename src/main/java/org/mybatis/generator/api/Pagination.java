package org.mybatis.generator.api;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 */
public class Pagination<T> implements Serializable {
    private static final long serialVersionUID = -5631795318226681173L;
    public final static int DEFAULT_OFFSET = 0;
    public final static int DEFAULT_LIMIT = 20;
    public final static int DEFAULT_PAGENUM = 1;


    private List<T> data;

    /** 查询下标 */
    private int offset;
    /** 条数 */
    private int limit;
    /** 记录总数 */
    private int total = 0;
    /** 页码，默认为1 */
    private int pageNum;
    /** 总页数 */
    private int pageTotal = DEFAULT_PAGENUM;


    public Pagination() {}

    public Pagination(int pageNum, int limit) {
        this.pageNum = pageNum;
        this.limit = limit;
    }

    public Pagination(int pageNum, int limit, List<T> data) {
        this.pageNum = pageNum;
        this.limit = limit;
        this.data = data;

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
        if (data != null && data.size() > this.total) {
            this.data = null;
        }
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * 计算查询开始的下标
     */
    public int getOffset() {
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGENUM;
        }
        if (limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        return (pageNum - 1) * 10;
    }

    /**
     * 获取页码总数
     */
    public int getPageTotal() {
        if (total == 0) {
            return 1;
        }
        // 向上舍入
        return (int) Math.ceil(((double) this.total * 1.0D) / (double) limit);
    }


    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    @Override
    public String toString() {
        return "Pagination [data=" + data + ", offset=" + offset + ", limit=" + limit + ", pageNum=" + pageNum
                        + ", total=" + total + ", pageTotal=" + pageTotal + "]";
    }



}
