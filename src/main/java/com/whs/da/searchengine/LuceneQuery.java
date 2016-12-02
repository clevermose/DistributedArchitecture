package com.whs.da.searchengine;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

/**
 * 查询条件
 * @author haiswang
 *
 */
public class LuceneQuery {
    
    private Query query;
    
    private Occur occur;
    
    public LuceneQuery() {}
    
    public LuceneQuery(Query query, Occur occur) {
        this.query = query;
        this.occur = occur;
    }
    
    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Occur getOccur() {
        return occur;
    }

    public void setOccur(Occur occur) {
        this.occur = occur;
    }
}
