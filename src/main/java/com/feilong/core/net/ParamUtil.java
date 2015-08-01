/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.core.net;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.core.io.CharsetType;
import com.feilong.core.util.ArrayUtil;
import com.feilong.core.util.CollectionsUtil;
import com.feilong.core.util.MapUtil;
import com.feilong.core.util.Validator;

/**
 * 处理参数相关.
 * 
 * @author feilong
 * @version 1.0.0 2010-4-15 下午04:01:29
 * @version 1.3.1 2015-8-1 22:08
 * @since 1.0.0
 */
public final class ParamUtil{

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamUtil.class);

    /** Don't let anyone instantiate this class. */
    private ParamUtil(){
        //AssertionError不是必须的. 但它可以避免不小心在类的内部调用构造器. 保证该类在任何情况下都不会被实例化.
        //see 《Effective Java》 2nd
        throw new AssertionError("No " + getClass().getName() + " instances for you!");
    }

    /**
     * 将多值的参数map转成单值的参数map.
     *
     * @param arrayValueMap
     *            the array value map
     * @return the map< string, string>
     * @since 1.3.1
     */
    public static Map<String, String> toSingleValueMap(Map<String, String[]> arrayValueMap){//返回 TreeMap 方便log 显示
        if (Validator.isNullOrEmpty(arrayValueMap)){
            return Collections.emptyMap();
        }

        Map<String, String> singleValueMap = new TreeMap<String, String>();
        for (Map.Entry<String, String[]> entry : arrayValueMap.entrySet()){
            String key = entry.getKey();
            String[] values = entry.getValue();
            String singleValue = Validator.isNotNullOrEmpty(values) ? values[0] : StringUtils.EMPTY;
            singleValueMap.put(key, singleValue);
        }
        return singleValueMap;
    }

    /**
     * To array value map.
     *
     * @param nameAndValueMap
     *            the name and value map
     * @return the map< string, string[]>
     * @since 1.3.1
     */
    public static Map<String, String[]> toArrayValueMap(Map<String, String> nameAndValueMap){
        if (Validator.isNullOrEmpty(nameAndValueMap)){
            return Collections.emptyMap();
        }

        Map<String, String[]> keyAndArrayMap = new TreeMap<String, String[]>();//返回 TreeMap 方便log 显示
        for (Map.Entry<String, String> entry : nameAndValueMap.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            keyAndArrayMap.put(key, new String[] { value });
        }
        return keyAndArrayMap;
    }

    // ************************************addParameter******************************************************

    /**
     * 添加参数,如果uri包含指定的参数名字,那么会被新的值替换.
     *
     * @param uriString
     *            the uri string
     * @param paramName
     *            添加的参数名称
     * @param parameValue
     *            添加的参数值
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return 添加参数,如果uri包含指定的参数名字,那么会被新的值替换
     * @see #addParameter(URI, String, Object, String)
     */
    public static String addParameter(String uriString,String paramName,Object parameValue,String charsetType){
        Map<String, String> nameAndValueMap = new HashMap<String, String>();
        nameAndValueMap.put(paramName, "" + parameValue);
        return addParameterSingleValueMap(uriString, nameAndValueMap, charsetType);
    }

    /**
     * 添加参数,如果uri包含指定的参数名字,那么会被新的值替换.
     * 
     * @param uri
     *            如果带有? 和参数,会先被截取,最后再拼接,<br>
     *            如果不带?,则自动 增加?
     * @param paramName
     *            添加的参数名称
     * @param parameValue
     *            添加的参数值
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return 添加参数,如果uri包含指定的参数名字,那么会被新的值替换
     * @see #addParameterArrayValueMap(URI, Map, String)
     */
    public static String addParameter(URI uri,String paramName,Object parameValue,String charsetType){
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put(paramName, new String[] { "" + parameValue });
        return addParameterArrayValueMap(uri, map, charsetType);
    }

    /**
     * 添加参数,如果uri包含指定的参数名字,那么会被新的值替换.
     *
     * @param uriString
     *            the uri string
     * @param nameAndValueMap
     *            nameAndValueMap param name 和value 的键值对
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     * @see #addParameterArrayValueMap(String, Map, String)
     */
    public static String addParameterSingleValueMap(String uriString,Map<String, String> nameAndValueMap,String charsetType){
        Map<String, String[]> keyAndArrayMap = toArrayValueMap(nameAndValueMap);
        return addParameterArrayValueMap(uriString, keyAndArrayMap, charsetType);
    }

    /**
     * 添加参数,如果uri包含指定的参数名字,那么会被新的值替换.
     *
     * @param uriString
     *            the uri string
     * @param nameAndArrayValueMap
     *            the name and array value map
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return 添加参数,如果uri包含指定的参数名字,那么会被新的值替换
     * @see #addParameterArrayValueMap(URI, Map, String)
     * @since 1.3.1
     */
    public static String addParameterArrayValueMap(String uriString,Map<String, String[]> nameAndArrayValueMap,String charsetType){
        //此处不能直接调用  addParameterArrayValueMap(URI uri 方法, 因为 uriString可能不是个符合规范的uri

        if (null == uriString){
            return StringUtils.EMPTY;
        }

        if (Validator.isNullOrEmpty(nameAndArrayValueMap)){
            return uriString;
        }
        String queryString = URIUtil.getQueryString(uriString);
        return addParameterArrayValueMap(uriString, queryString, nameAndArrayValueMap, charsetType);
    }

    /**
     * 添加参数.
     * 
     * <p>
     * 如果uri包含指定的参数名字,那么会被新的值替换，比如原来是a=1&a=2,现在使用a,[3,4]调用这个方法， 会返回a=3&a=4.
     * </p>
     * 
     * @param uri
     *            如果带有? 和参数,会先被截取,最后再拼接,<br>
     *            如果不带?,则自动 增加?
     * @param nameAndArrayValueMap
     *            nameAndValueMap 类似于 request.getParameterMap
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return 添加参数,如果uri包含指定的参数名字,那么会被新的值替换
     */
    public static String addParameterArrayValueMap(URI uri,Map<String, String[]> nameAndArrayValueMap,String charsetType){
        if (null == uri){
            return StringUtils.EMPTY;
        }

        String uriString = uri.toString();
        if (Validator.isNullOrEmpty(nameAndArrayValueMap)){
            return uriString;
        }
        String queryString = uri.getRawQuery();
        return addParameterArrayValueMap(uriString, queryString, nameAndArrayValueMap, charsetType);
    }

    /**
     * 添加 parameter array value map.
     *
     * @param uriString
     *            the uri string
     * @param queryString
     *            the query
     * @param nameAndArrayValueMap
     *            the name and array value map
     * @param charsetType
     *            the charset type
     * @return the string
     * @since 1.3.1
     */
    private static String addParameterArrayValueMap(
                    String uriString,
                    String queryString,
                    Map<String, String[]> nameAndArrayValueMap,
                    String charsetType){
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        paramsMap.putAll(nameAndArrayValueMap);
        if (Validator.isNotNullOrEmpty(queryString)){
            // 注意 action before 可能带参数
            // "action": "https://202.6.215.230:8081/purchasing/purchase.do?action=loginRequest",
            // "fullEncodedUrl":"https://202.6.215.230:8081/purchasing/purchase.do?action=loginRequest?miscFee=0&descp=&klikPayCode=03BELAV220&transactionDate=23%2F03%2F2014+02%3A40%3A19&currency=IDR",
            Map<String, String[]> originalMap = parseQueryStringToArrayValueMap(queryString, null);
            paramsMap.putAll(originalMap);
        }
        paramsMap.putAll(nameAndArrayValueMap);
        return combineUrl(URIUtil.getFullPathWithoutQueryString(uriString), paramsMap, charsetType);
    }

    // ********************************removeParameter*********************************************************************

    /**
     * 删除参数.
     *
     * @param uriString
     *            the uri string
     * @param paramName
     *            the param name
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     * @see #removeParameterList(String, List, String)
     */
    public static String removeParameter(String uriString,String paramName,String charsetType){
        if (null == paramName){
            return uriString;
        }
        List<String> list = new ArrayList<String>();
        list.add(paramName);
        return removeParameterList(uriString, list, charsetType);
    }

    /**
     * 删除参数.
     * 
     * @param uri
     *            the uri
     * @param paramName
     *            the param name
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     * @see #removeParameterList(URI, List, String)
     */
    public static String removeParameter(URI uri,String paramName,String charsetType){
        List<String> paramNameList = null;
        if (Validator.isNotNullOrEmpty(paramName)){
            paramNameList = new ArrayList<String>();
            paramNameList.add(paramName);
        }
        return removeParameterList(uri, paramNameList, charsetType);
    }

    /**
     * 删除参数.
     * 
     * @param uriString
     *            the url
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     */
    public static String removeParameterList(String uriString,List<String> paramNameList,String charsetType){
        if (null == uriString){
            return StringUtils.EMPTY;
        }
        if (Validator.isNullOrEmpty(paramNameList)){// 如果 paramNameList 是null 原样返回
            return uriString;
        }
        String queryString = URIUtil.getQueryString(uriString);
        return removeParameterList(uriString, queryString, paramNameList, charsetType);
    }

    /**
     * 删除参数.
     * 
     * @param uri
     *            the uri
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     */
    public static String removeParameterList(URI uri,List<String> paramNameList,String charsetType){
        if (null == uri){
            return StringUtils.EMPTY;
        }
        String uriString = uri.toString();
        if (Validator.isNullOrEmpty(paramNameList)){// 如果 paramNameList是null原样返回
            return uriString;
        }
        String queryString = uri.getRawQuery();// 返回此URI的原始查询组成部分。 URI的查询组成部分（如果定义了）只包含合法的 URI字符。
        return removeParameterList(uriString, queryString, paramNameList, charsetType);
    }

    /**
     * 删除 parameter list.
     *
     * @param uriString
     *            the uri string
     * @param queryString
     *            the query string
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            the charset type
     * @return the string
     * @since 1.3.1
     */
    private static String removeParameterList(String uriString,String queryString,List<String> paramNameList,String charsetType){
        if (Validator.isNullOrEmpty(queryString)){
            return uriString;// 不带参数原样返回
        }
        Map<String, String[]> paramsMap = parseQueryStringToArrayValueMap(queryString, null);
        for (String paramName : paramNameList){
            paramsMap.remove(paramName);
        }
        return combineUrl(URIUtil.getFullPathWithoutQueryString(uriString), paramsMap, charsetType);
    }

    // **************************************retentionParams********************************************************

    /**
     * url里面仅保留指定的参数.
     *
     * @param uriString
     *            the uri string
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            编码,see {@link CharsetType}
     * @return the string
     * @see #retentionParamList(URI, List, String)
     */
    public static String retentionParamList(String uriString,List<String> paramNameList,String charsetType){
        if (null == uriString){
            return StringUtils.EMPTY;
        }

        if (Validator.isNullOrEmpty(paramNameList)){ // 如果 paramNameList 是null 原样返回
            return uriString;
        }
        String queryString = URIUtil.getQueryString(uriString);
        return retentionParamList(uriString, queryString, paramNameList, charsetType);
    }

    /**
     * url里面仅保留指定的参数.
     * 
     * @param uri
     *            the uri
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            编码,see {@link CharsetType} , 何种编码，如果是null或者 empty,那么参数部分原样返回,自己去处理兼容性问题<br>
     *            否则会先解码,再加码,因为ie浏览器和chrome 浏览器 url中访问路径 ,带有中文情况下 不一致
     * @return the string
     * @see #parseQueryStringToArrayValueMap(String, String)
     */
    public static String retentionParamList(URI uri,List<String> paramNameList,String charsetType){
        if (null == uri){
            return StringUtils.EMPTY;
        }

        String uriString = uri.toString();

        if (Validator.isNullOrEmpty(paramNameList)){ // 如果 paramNameList 是null 原样返回
            return uriString;
        }
        String queryString = uri.getRawQuery(); // 返回此 URI的原始查询组成部分。 URI的查询组成部分（如果定义了）只包含合法的 URI字符。
        return retentionParamList(uriString, queryString, paramNameList, charsetType);
    }

    /**
     * Retention param list.
     *
     * @param uriString
     *            the uri string
     * @param queryString
     *            the query string
     * @param paramNameList
     *            the param name list
     * @param charsetType
     *            the charset type
     * @return the string
     * @since 1.3.1
     */
    private static String retentionParamList(String uriString,String queryString,List<String> paramNameList,String charsetType){
        if (Validator.isNullOrEmpty(queryString)){
            return uriString; //不带参数原样返回
        }
        Map<String, String[]> originalMap = parseQueryStringToArrayValueMap(queryString, null);
        Map<String, String[]> paramsMap = new LinkedHashMap<String, String[]>();
        for (String paramName : paramNameList){
            paramsMap.put(paramName, originalMap.get(paramName));
        }
        return combineUrl(URIUtil.getFullPathWithoutQueryString(uriString), paramsMap, charsetType);
    }

    /**
     * 将{@code a=1&b=2}这样格式的数据转换成map (如果charsetType 不是null或者empty 返回安全的 key和value).
     *
     * @param queryString
     *            the query string
     * @param charsetType
     *            何种编码，如果是null或者 empty,那么参数部分原样返回,自己去处理兼容性问题<br>
     *            否则会先解码,再加码,因为ie浏览器和chrome 浏览器 url中访问路径 ,带有中文情况下 不一致
     * @return the {@code map<string, string>}
     * @see #parseQueryStringToArrayValueMap(String, String)
     * @since 1.3.1
     */
    public static Map<String, String> parseQueryStringToSingleValueMap(String queryString,String charsetType){
        Map<String, String[]> arrayValueMap = parseQueryStringToArrayValueMap(queryString, charsetType);
        return ParamUtil.toSingleValueMap(arrayValueMap);
    }

    /**
     * 将{@code a=1&b=2}这样格式的数据转换成map (如果charsetType 不是null或者empty 返回安全的 key和value).
     *
     * @param queryString
     *            {@code a=1&b=2}类型的数据,支持{@code a=1&a=1}的形式， 返回map的值是数组
     * @param charsetType
     *            何种编码， {@link CharsetType} 如果是null或者 empty,那么参数部分原样返回,自己去处理兼容性问题<br>
     *            否则会先解码,再加码,因为ie浏览器和chrome 浏览器 url中访问路径 ,带有中文情况下 不一致
     * @return 将{@code a=1&b=2}这样格式的数据转换成map (如果charsetType 不是null或者empty 返回安全的 key和value)
     * @since 1.3.1
     */
    public static Map<String, String[]> parseQueryStringToArrayValueMap(String queryString,String charsetType){
        if (Validator.isNullOrEmpty(queryString)){
            return Collections.emptyMap();
        }
        String[] nameAndValueArray = queryString.split(URIComponents.AMPERSAND);
        if (Validator.isNullOrEmpty(nameAndValueArray)){
            return Collections.emptyMap();
        }

        Map<String, String[]> map = new LinkedHashMap<String, String[]>();

        for (int i = 0, j = nameAndValueArray.length; i < j; ++i){
            String nameAndValue = nameAndValueArray[i];
            String[] tempArray = nameAndValue.split("=", 2);

            if (tempArray != null && tempArray.length == 2){
                String key = tempArray[0];
                String value = tempArray[1];

                if (Validator.isNotNullOrEmpty(charsetType)){
                    // 浏览器传递queryString()参数差别(浏览器兼容问题);chrome会将query进行 encoded再发送请求;而ie原封不动的发送

                    // 由于暂时不能辨别是否encoded过,所以先强制decode再encode;此处不能先转decode(query,charsetType),参数就是想传 =是转义符
                    key = URIUtil.encode(URIUtil.decode(key, charsetType), charsetType);
                    value = URIUtil.encode(URIUtil.decode(value, charsetType), charsetType);
                }

                String[] valuesArrayInMap = map.get(key);

                List<String> list = null;
                if (Validator.isNullOrEmpty(valuesArrayInMap)){
                    list = new ArrayList<String>();
                }else{
                    list = ArrayUtil.toList(valuesArrayInMap);
                }
                list.add(value);
                map.put(key, CollectionsUtil.toArray(list, String.class));
            }
        }
        return map;
    }

    /**
     * 将map混合成 queryString.
     *
     * @param paramsMap
     *            类似于 request.getParamMap
     * @param charsetType
     *            {@link CharsetType} 何种编码，如果是null或者 empty,那么参数部分原样返回,自己去处理兼容性问题<br>
     *            否则会先解码,再加码,因为ie浏览器和chrome 浏览器 url中访问路径 ,带有中文情况下 不一致
     * @return if isNullOrEmpty(appendMap) ,return ""
     * @see CharsetType
     * @see MapUtil#toNaturalOrderingString(Map)
     * @since 1.3.1
     */
    public static String parseArrayValueMapToQueryString(Map<String, String[]> paramsMap,String charsetType){
        if (Validator.isNullOrEmpty(paramsMap)){
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();

        int i = 0;
        int size = paramsMap.size();
        for (Map.Entry<String, String[]> entry : paramsMap.entrySet()){
            String key = entry.getKey();
            String[] paramValues = entry.getValue();

            // **************************************************************
            if (Validator.isNotNullOrEmpty(charsetType)){
                // 统统先强制 decode再 encode,浏览器兼容问题
                key = URIUtil.encode(URIUtil.decode(key, charsetType), charsetType);
            }
            // **************************************************************
            if (Validator.isNullOrEmpty(paramValues)){
                LOGGER.warn("the param key:[{}] value is null", key);
                sb.append(key).append("=").append("");
            }else{
                List<String> paramValueList = null;
                if (Validator.isNotNullOrEmpty(charsetType)){
                    paramValueList = new ArrayList<String>();
                    for (String value : paramValues){
                        if (Validator.isNotNullOrEmpty(value)){
                            // 浏览器传递queryString()参数差别(浏览器兼容问题);chrome会将query进行 encoded再发送请求;而ie原封不动的发送
                            // 由于暂时不能辨别是否encoded过,所以先强制decode再encode;此处不能先转decode(query,charsetType),参数就是想传 =是转义符
                            paramValueList.add(URIUtil.encode(URIUtil.decode(value.toString(), charsetType), charsetType));
                        }else{
                            paramValueList.add("");
                        }
                    }
                }else{
                    paramValueList = ArrayUtil.toList(paramValues);
                }

                for (int j = 0, z = paramValueList.size(); j < z; ++j){
                    String value = paramValueList.get(j);
                    sb.append(key).append("=").append(value);
                    if (j != z - 1){// 最后一个& 不拼接
                        sb.append(URIComponents.AMPERSAND);
                    }
                }
            }
            if (i != size - 1){// 最后一个& 不拼接
                sb.append(URIComponents.AMPERSAND);
            }
            ++i;
        }
        return sb.toString();
    }

    /**
     * Combine url.
     *
     * @param beforePathWithoutQueryString
     *            the before path without query string
     * @param paramsMap
     *            the map
     * @param charsetType
     *            the charset type
     * @return the string
     * @since 1.3.1
     */
    private static String combineUrl(String beforePathWithoutQueryString,Map<String, String[]> paramsMap,String charsetType){
        if (Validator.isNullOrEmpty(beforePathWithoutQueryString)){
            return StringUtils.EMPTY;
        }
        if (Validator.isNullOrEmpty(paramsMap)){//没有参数 直接return
            return beforePathWithoutQueryString;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(beforePathWithoutQueryString);
        sb.append(URIComponents.QUESTIONMARK);
        sb.append(parseArrayValueMapToQueryString(paramsMap, charsetType));

        return sb.toString();
    }
}
