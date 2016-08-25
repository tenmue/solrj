package org.ten.mue;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yang_yancy on 2016/8/21.
 * Solr Demo
 * 参考资料
 * http://wiki.apache.org/solr/IntegratingSolr?highlight=%28%28SolJava%29%29#Java
 * http://wiki.apache.org/solr/Solrj
 */
public class SolrDemo {

    public static String solrURL = "http://localhost:8080/solr/core1";

    public static void main(String[] args) {
        //addDoc();
        //addDoc2();
        //addDoc3();
        queryDoc();
        //delDoc();
        //delDoc2();
        //delDoc3();
    }

    /**SolrClient solr = new HttpSolrClient(solrURL);
     * 查询
     */
    public static void queryDoc(){
        SolrClient solr = new HttpSolrClient(solrURL);

        SolrQuery solrQuery = new SolrQuery();
        //solrQuery.setQuery("*ten*");
        solrQuery.setQuery("name:ten* id:9988");
        solrQuery.setFields("id", "name", "price");//需要返回的数据
        //solrQuery.setRows(Integer.MAX_VALUE);//查询文档中所有符合条件的数据
        solrQuery.setStart(0);//从文档中第0条数据开始检索，默认时从第0条开始
        solrQuery.setRows(10);
        solrQuery.setSort("price", SolrQuery.ORDER.desc);//排序方式

        //只有当设置了分词后，高亮才会有效果
        /*solrQuery.setHighlight(true);
        solrQuery.addHighlightField("name");//高亮显示字段
        solrQuery.setHighlightSimplePre("<font color='red'>");//标记高亮关键字前缀
        solrQuery.setHighlightSimplePost("</font>");//标记高亮关键字后缀*/
        try {
            QueryResponse response = solr.query(solrQuery);
            SolrDocumentList list = response.getResults();
            System.out.println("文档个数：" + list.getNumFound());
            System.out.println("查询耗时：" + response.getQTime());
            //普通方式处理查询结果
            /*for (SolrDocument document : list){
                System.out.println(document.getFieldValue("id"));
                System.out.println(document.getFieldValue("name"));
                System.out.println(document.getFieldValue("price"));
            }*/

            //绑定JavaBean对象处理查询结果
            DocumentObjectBinder documentObjectBinder = new DocumentObjectBinder();
            List<SolrBean> solrBeanList = documentObjectBinder.getBeans(SolrBean.class, list);
            for (SolrBean solrBean : solrBeanList){
                System.out.println("id:" + solrBean.getId());
                System.out.println("name:" + solrBean.getName());
                System.out.println("price:" + solrBean.getPrice());
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 方法一
     * 创建索引
     */
    public static void addDoc() {

        SolrClient solr = new HttpSolrClient(solrURL);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "9988");
        doc.addField("name", "my name is yancy");
        doc.addField("price", "49.9");
        try {
            UpdateResponse response = solr.add(doc);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 方法二
     * 创建索引
     */
    public static  void addDoc2(){
        SolrClient solr = new HttpSolrClient(solrURL);

        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        for(int i = 0;i <= 1000000;i++){
            SolrInputDocument solrDocument = new SolrInputDocument();
            solrDocument.addField("id", "100" + i);
            solrDocument.addField("name", "my name is mue" + i);
            solrDocument.addField("price", "45.7" + i);
            docs.add(solrDocument);
        }
        try {
            solr.add(docs);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法三
     * 通过对象的方式添加索引
     */
    public static void addDoc3(){
        SolrClient solr = new HttpSolrClient(solrURL);

        Collection<SolrBean> docs = new ArrayList<SolrBean>();
        DocumentObjectBinder documentObjectBinder = new DocumentObjectBinder();
        for (int i = 0;i < 10;i++){
            SolrBean solrBean = new SolrBean();
            solrBean.setId("200" + i);
            solrBean.setName("ten" + i);
            solrBean.setPrice(22 + i);
            SolrInputDocument doc = documentObjectBinder.toSolrInputDocument(solrBean);
            docs.add(solrBean);
        }

        try {
            solr.addBeans(docs);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法一
     * 删除指定索引
     */
    public static void delDoc(){
        SolrClient solr = new HttpSolrClient(solrURL);

        try {
            solr.deleteById("2001");//根据id删除索引
            //solr.deleteByQuery("name:ten0");//根据指定字段删除索引
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法二
     * 删除指定索引
     */
    public static void delDoc2(){
        SolrClient solr = new HttpSolrClient(solrURL);

        List<String> list = new ArrayList<String>();
        for(int i = 0;i < 5;i++){
            list.add("200" + i);//根据id删除
        }
        try {
            UpdateResponse response = solr.deleteById(list);
            solr.commit();
            System.out.println(response.getStatus());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 方法三
     * 清空所有索引
     */
    public static void delDoc3(){
        SolrClient solr = new HttpSolrClient(solrURL);

        try {
            UpdateResponse response = solr.deleteByQuery("*:*");
            solr.commit();
            System.out.println(response.getStatus());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
