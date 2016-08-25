package org.ten.mue;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Created by yang_yancy on 2016/8/21.
 */
public class SolrBean {

    @Field
    private String id;

    @Field
    private String name;

    @Field
    private float price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
