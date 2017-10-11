/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Lmarbouh Mhamed
 */
public class QueryUtil {

    
    private SearchUtil searchUtil;

    public QueryUtil(SearchUtil searchUtil) {
        this.searchUtil = searchUtil;
    }

    
     
    /**
     *
     * @param <T>
     * @param returnClass
     * @param entityManager
     * @param description 
     * @param constraints 
     * @return
     */
    public  <T> List<T>  query(Class<T> returnClass,EntityManager entityManager, String description, Object... constraints) {
        List<Object> results = null;
        
        //
        String[] keys = getKeys(description);

        String operation = keys[0];
        String beanName = keys[1];
        keys = ArrayUtils.remove(keys, 0);
        keys = ArrayUtils.remove(keys, 0);

        String query = initializeQuery(description, keys, beanName, operation);

        query = addConstraints(query, constraints, keys, beanName);
        results = entityManager.createQuery(query).getResultList();
        System.out.println(query);
        return castResulte(results, returnClass);
    }

    private <T> List<T> castResulte(List srcList, Class<T> clas) {
        List<T> list = new ArrayList<>();
        srcList.stream().filter((obj) -> (obj != null && clas.isAssignableFrom(obj.getClass()))).forEach((obj) -> {
            list.add(clas.cast(obj));
        });
        return list;
    }


    private  String addConstraints(String query, Object[] constraints, String[] keys, String beanName) {
        System.out.println("addConstraints");

        int cmp = 0;

        for (String key : keys) {
            
            Object constraint = constraints[cmp];

            if (key.contains("<") && key.contains(">")) {

                query += addConstraint(key, beanName, constraint, constraints[cmp + 1]);
                cmp++;

            } else {

                query += addConstraint(key, beanName, constraint);

            }

            cmp++;
        }
        return query;
    }

    /**
     *
     * this method will take in the description and initialize a JPQL query
     *
     * @param description
     * @return a query String initialization based on the description
     */
    private  String initializeQuery(String description, String[] keys, String beanName, String operation) {

        String query = "";

        if (operation.equals("find")) {
            query = "SELECT " + beanName.toLowerCase() + " FROM " + beanName + " " + beanName.toLowerCase() + "  where 1=1 ";
        }

        if (operation.equals("delete")) {
            query = "DELETE FROM " + beanName + " " + beanName.toLowerCase() + " WHERE 1=1 ";
        }

        return query;
    }

    private  String addConstraint(String key, String beanName, Object... constraint) {
        System.out.println("add a Constraint");
        String query = "";
        if (constraint[0] instanceof Date) {
            query = addDateConstraint(key, query, beanName, constraint);
        } else {
            query = addSimpleConstraint(key, query, beanName, constraint);
        }

        return query;

    }

    private  String addSimpleConstraint(String key, String query, String beanName, Object[] constraint) {
        if (key.contains("<") && key.contains(">")) {
            key = key.replace(">", "");
            key = key.replace("<", "");
            query += searchUtil.addConstraintMinMax(beanName.toLowerCase(), key, constraint[0], constraint[1]);
        } else if (key.contains("<")) {
            key = key.replace("<", "");
            if (key.contains("=")) {
                key = key.replace("=", "");
                query += searchUtil.addConstraint(beanName.toLowerCase(), key, "<=", constraint[0]);
            } else {
                query += searchUtil.addConstraint(beanName.toLowerCase(), key, "<", constraint[0]);
            }
        } else if (key.contains(">")) {
            key = key.replace(">", "");
            if (key.contains("=")) {
                key = key.replace("=", "");
                query += searchUtil.addConstraint(beanName.toLowerCase(), key, ">=", constraint[0]);
            } else {
                query += searchUtil.addConstraint(beanName.toLowerCase(), key, ">", constraint[0]);
            }
        } else {
            query += searchUtil.addConstraint(beanName.toLowerCase(), key, "=", constraint[0]);
        }
        return query;
    }

    private String addDateConstraint(String key, String query, String beanName, Object[] constraint) {
        if (key.contains("<") && key.contains(">")) {
            key = key.replace(">", "");
            key = key.replace("<", "");
            query += searchUtil.addConstraintMinMaxDate(beanName.toLowerCase(), key, (Date) constraint[0], (Date) constraint[1]);
        } else if (key.contains("<")) {
            key = key.replace("<", "");
            if (key.contains("=")) {
                key = key.replace("=", "");
                query += searchUtil.addConstraintDate(beanName.toLowerCase(), key, "<=", (Date) constraint[0]);
            } else {
                query += searchUtil.addConstraintDate(beanName.toLowerCase(), key, "<", (Date) constraint[0]);
            }
        } else if (key.contains(">")) {
            key = key.replace(">", "");
            if (key.contains("=")) {
                key = key.replace("=", "");
                query += searchUtil.addConstraintDate(beanName.toLowerCase(), key, ">=", (Date) constraint[0]);
            } else {
                query += searchUtil.addConstraintDate(beanName.toLowerCase(), key, ">", (Date) constraint[0]);
            }
        } else {
            query += searchUtil.addConstraintDate(beanName.toLowerCase(), key, "=", (Date) constraint[0]);
        }
        return query;
    }

    private String[] getKeys(String description) {
        String[] firstSplite = description.split("By");
        String[] keys = firstSplite[0].split("(?<!^)(?=[A-Z])");
        keys = ArrayUtils.addAll(keys, firstSplite[1].toLowerCase().split("&"));
        return keys;
    }
    
    

    
   
}
