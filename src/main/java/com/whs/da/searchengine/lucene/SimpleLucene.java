package com.whs.da.searchengine.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SortOrder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.whs.da.searchengine.LuceneField;
import com.whs.da.searchengine.LuceneQuery;

/**
 * Lucene简单测试
 * @author haiswang
 *
 */
public class SimpleLucene {

    //索引存储路径
    public static final String LUCENE_INDEX_PATH = "E:\\luceneIndex";
    
    private Directory indexDir = null;
    
    private Analyzer analyzer = null;
    
    private IndexWriterConfig indexWriterConfig = null;
    
    /**
     * 创建索引目录
     * @throws IOException
     */
    private void createIndexDir() throws IOException {
        indexDir = FSDirectory.open(new File(LUCENE_INDEX_PATH));
    }
    
    /**
     * 创建分词器
     */
    private void createAnalyzer() {
        analyzer = new StandardAnalyzer(Version.LUCENE_40);
    }
    
    /**
     * 索引写入对象
     * @throws IOException
     */
    private void createIndexWriterConfig() {
        indexWriterConfig = new IndexWriterConfig(Version.LUCENE_40, analyzer);
    }
    
    /**
     * 初始化
     * @throws IOException
     */
    public void init() throws IOException {
        createIndexDir();
        createAnalyzer();
        createIndexWriterConfig();
    }
    
    /**
     * 插入文档
     * @param LuceneFields
     * @throws IOException
     */
    public void insert(Set<LuceneField> LuceneFields) {
        
        try(IndexWriter indexWriter = new IndexWriter(indexDir, indexWriterConfig)) {
            Document doc = new Document();
            for (LuceneField luceneField : LuceneFields) {
                if(luceneField.isAnalyzer()) {
                    //需要分词的Field
                    doc.add(new TextField(luceneField.getFieldName(), luceneField.getFieldValue(), Field.Store.YES));
                } else {
                    //无需分词的Field
                    switch (luceneField.getValueType()) {
                        case INT:
                            doc.add(new IntField(luceneField.getFieldName(), Integer.parseInt(luceneField.getFieldValue().toString()), Field.Store.YES));
                            break;
                        case STRING:
                            doc.add(new StringField(luceneField.getFieldName(), luceneField.getFieldValue(), Field.Store.YES));
                            break;
                    default:
                        break;
                    }
                }
            }
            
            indexWriter.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 查找文档
     * @param queryStr
     * @param queryFields
     * @throws ParseException 
     * @throws IOException 
     */
    public List<Map<String, String>> select(String queryStr, String ... queryFields) {
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_40, queryFields, analyzer);
        
        List<Map<String, String>> searchResult = null;
        
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            Query query = queryParser.parse(queryStr);
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(query, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        
        return searchResult;
    }
    
    /**
     * 
     * @param conditionTerm
     * @return
     */
    public List<Map<String, String>> selectByTerm(Term conditionTerm) {
        
        Query termQuery = new TermQuery(conditionTerm);
        List<Map<String, String>> searchResult = null;
        
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(termQuery, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return searchResult;
    }
    
    /**
     * 通配符匹配, 类似于SQL中的like
     * @param fieldName
     * @param wildcard
     * @return
     */
    public List<Map<String, String>> selectByWildcard(String fieldName, String wildcard) {
        Term wildcardTerm = new Term(fieldName, wildcard);
        WildcardQuery wildcardQuery = new WildcardQuery(wildcardTerm);
        
        List<Map<String, String>> searchResult = null;
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(wildcardQuery, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return searchResult;
    } 
    
    /**
     * 
     * @param fieldName 字段
     * @param word_1 第一个单词
     * @param word_2 第二个单词
     * @param slop word_1 和 word_2 之间不超过slop个单词
     * @return
     */
    public List<Map<String, String>> selectByPhrase(String fieldName, String word_1, String word_2, int slop) {
        
        PhraseQuery phraseQuery = new PhraseQuery();
        phraseQuery.add(new Term(fieldName, word_1));
        phraseQuery.add(new Term(fieldName, word_2));
        phraseQuery.setSlop(slop);
        
        List<Map<String, String>> searchResult = null;
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(phraseQuery, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return searchResult;
        
    }
    
    /**
     * 某个字段的范围查询
     * @param fieldName
     * @param min
     * @param max
     * @return
     */
    public List<Map<String, String>> selectByRange(String fieldName, int min, int max) {
        
        NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange(fieldName, min, max, true, true);
        List<Map<String, String>> searchResult = null;
        
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(rangeQuery, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return searchResult;
    }
    
    /**
     * 多条件查询
     * @param querys
     * @return
     */
    public List<Map<String, String>> selectByMultiCondition(LuceneQuery ... querys) {
        BooleanQuery booleanQuery = new BooleanQuery();
        for (LuceneQuery query : querys) {
            booleanQuery.add(query.getQuery(), query.getOccur());
        }
        
        List<Map<String, String>> searchResult = null;
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(booleanQuery, null, 10).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return searchResult;
    } 
    
    /**
     * 排序
     * @param queryString
     * @param sortFieldName
     * @param sortFieldType
     * @param doReverse
     * @param searchFields
     * @return
     */
    public List<Map<String, String>> selectSort(String queryString, String sortFieldName, SortField.Type sortFieldType, boolean doReverse, String ... searchFields) {
        Sort sort = new Sort();
        SortField sortField = new SortField(sortFieldName, sortFieldType, doReverse);
        sort.setSort(sortField);
        
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_40, searchFields, analyzer);
        List<Map<String, String>> searchResult = null;
        
        try(DirectoryReader dirReader = DirectoryReader.open(indexDir)) {
            Query query = queryParser.parse(queryString);
            IndexSearcher indexSearcher = new IndexSearcher(dirReader);
            ScoreDoc[] scoreDocs = indexSearcher.search(query, null, 10, sort).scoreDocs;
            searchResult = new LinkedList<>();
            if(null != scoreDocs && 0 != scoreDocs.length) {
                for (ScoreDoc scoreDoc : scoreDocs) {
                    Map<String, String> singleResult = new HashMap<String, String>();
                    int docId = scoreDoc.doc;
                    Document doc = indexSearcher.doc(docId);
                    for (IndexableField indexableField : doc.getFields()) {
                        singleResult.put(indexableField.name(), indexableField.stringValue());
                    }
                    searchResult.add(singleResult);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        
        return searchResult;
    }
    
    /**
     * 删除document
     * @param fieldName 
     * @param fieldValue
     */
    public void delete(String fieldName, String fieldValue) {
        try(IndexWriter indexWriter = new IndexWriter(indexDir, indexWriterConfig)) {
            //依据Term来删除Document,是一个精确匹配
            Term deleteCondition = new Term(fieldName, fieldValue);
            indexWriter.deleteDocuments(deleteCondition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        SimpleLucene simpleLucene = new SimpleLucene();
        try {
            simpleLucene.init();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        //List<Map<String, String>> results = simpleLucene.select("Lucene", "title");
        
        //这种条件可以查询到对应的数据
        //Term conditionTerm = new Term("isbn", "193398817");
        //这种条件查不到对应的数据
        //Term conditionTerm = new Term("isbn", "19339");
        //这种条件查不到对应的数据
        //Term conditionTerm = new Term("title", "Lucene");
        //这种条件查不到对应的数据
        //Term conditionTerm = new Term("title", "Lucene in Action");
        //List<Map<String, String>> results = simpleLucene.selectByTerm(conditionTerm);
        
        //范围查询
        //List<Map<String, String>> results = simpleLucene.selectByRange("price", 20, 40);
        
        //如果这边设置title字段查询(该字段进行分词),那么就会无法查询
        //如果这边设置price字段查询(该字段类型是Int),那么也无法查询
        //当用isbn字段查询(该字段时String类型,且没有进行分词),可以查询成功
        //?表示0或者1个字符,*表示0或多个字符
        //List<Map<String, String>> results = simpleLucene.selectByWildcard("isbn", "19339881?");
        //List<Map<String, String>> results = simpleLucene.selectByWildcard("isbn", "19339*");
        
        //Phrase查询
        //List<Map<String, String>> results = simpleLucene.selectByPhrase("content", "hello", "am", 0);
        
//        //多条件查询
//        //条件1
//        PhraseQuery query_1 = new PhraseQuery();
//        query_1.add(new Term("content", "hello"));
//        query_1.add(new Term("content", "am"));
//        query_1.setSlop(1);
//        //条件二
//        //Occur.MUST 该条件必须满足  
//        //Occur.MUST_NOT 该条件必须不满足
//        //Occur.SHOULD 该条件可以满足,可以不满足,返回值：满足的会靠前,不满足的会靠后
//        LuceneQuery luceneQuery_1 = new LuceneQuery(query_1, Occur.MUST);
//        Term wildcardTerm = new Term("author", "haiswa*");
//        WildcardQuery query_2 = new WildcardQuery(wildcardTerm);
//        LuceneQuery luceneQuery_2 = new LuceneQuery(query_2, Occur.MUST);
//        List<Map<String, String>> results = simpleLucene.selectByMultiCondition(luceneQuery_1, luceneQuery_2);
        
        //查询title字段中存在"Lucene"字符的document,按照price的降序排序
        List<Map<String, String>> results = simpleLucene.selectSort("Lucene", "price", SortField.Type.INT, true, "title");
        
        for (Map<String, String> map : results) {
            printMap(map);
        }

        
//        //删除文档
//        simpleLucene.delete("isbn", "193398817");
//        simpleLucene.delete("isbn", "55320055Z");
//        simpleLucene.delete("isbn", "55063554A");
//        simpleLucene.delete("isbn", "9900333X");
//        simpleLucene.delete("author", "haiswang");
//        simpleLucene.delete("author", "xtwu");
//        
//        //插入文档
//        LuceneField luceneFieldTitle = new LuceneField("title", "Lucene in Action", ValueType.STRING, true);
//        LuceneField luceneFieldIsbn = new LuceneField("isbn", "193398817", ValueType.STRING, false);
//        LuceneField luceneFieldPrice = new LuceneField("price", "20", ValueType.INT, false);
//        Set<LuceneField> luceneFields = new HashSet<>();
//        luceneFields.add(luceneFieldTitle);
//        luceneFields.add(luceneFieldIsbn);
//        luceneFields.add(luceneFieldPrice);
//        simpleLucene.insert(luceneFields);
//        
//        luceneFieldTitle = new LuceneField("title", "Lucene for Dummies", ValueType.STRING, true);
//        luceneFieldIsbn = new LuceneField("isbn", "55320055Z", ValueType.STRING, false);
//        luceneFieldPrice = new LuceneField("price", "40", ValueType.INT, false);
//        luceneFields.clear();
//        luceneFields.add(luceneFieldTitle);
//        luceneFields.add(luceneFieldIsbn);
//        luceneFields.add(luceneFieldPrice);
//        simpleLucene.insert(luceneFields);
//        
//        luceneFieldTitle = new LuceneField("title", "Managing Gigabytes", ValueType.STRING, true);
//        luceneFieldIsbn = new LuceneField("isbn", "55063554A", ValueType.STRING, false);
//        luceneFieldPrice = new LuceneField("price", "60", ValueType.INT, false);
//        luceneFields.clear();
//        luceneFields.add(luceneFieldTitle);
//        luceneFields.add(luceneFieldIsbn);
//        luceneFields.add(luceneFieldPrice);
//        simpleLucene.insert(luceneFields);
//        
//        luceneFieldTitle = new LuceneField("title", "The Art of Computer Science", ValueType.STRING, true);
//        luceneFieldIsbn = new LuceneField("isbn", "9900333X", ValueType.STRING, false);
//        luceneFieldPrice = new LuceneField("price", "80", ValueType.INT, false);
//        luceneFields.clear();
//        luceneFields.add(luceneFieldTitle);
//        luceneFields.add(luceneFieldIsbn);
//        luceneFields.add(luceneFieldPrice);
//        simpleLucene.insert(luceneFields);
//        
//        LuceneField luceneFieldContent = new LuceneField("content", "hello i am wang hai sheng", ValueType.STRING, true);
//        LuceneField authorField = new LuceneField("author", "haiswang", ValueType.STRING, false);
//        luceneFields.clear();
//        luceneFields.add(luceneFieldContent);
//        luceneFields.add(authorField);
//        simpleLucene.insert(luceneFields);
//        
//        luceneFieldContent = new LuceneField("content", "hello i am wu xiao tian", ValueType.STRING, true);
//        authorField = new LuceneField("author", "xtwu", ValueType.STRING, false);
//        luceneFields.clear();
//        luceneFields.add(luceneFieldContent);
//        luceneFields.add(authorField);
//        simpleLucene.insert(luceneFields);
        
        
        System.out.println("over...");
    }
    
    public static void printMap(Map<String, String> map) {
        System.out.println("start--------------------------");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println("end----------------------------");
    }
}
