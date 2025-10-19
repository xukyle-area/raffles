package com.gantenx.raffles.util;

import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.functions.ScalarFunction;
import com.google.common.collect.Sets;

/**
 * Created on 2020/6/3
 *
 * @author: xionggp
 */
public class FindInSet extends ScalarFunction {

    private static final long serialVersionUID = 5047363857836588713L;

    public boolean eval(String str, String stringList) {
        if (StringUtils.isNotEmpty(str) && StringUtils.isNotEmpty(stringList)) {
            String[] strAry = StringUtils.split(stringList, ",");
            Set<String> stringSet = Sets.newHashSet(strAry);
            if (stringSet.contains(str)) {
                return true;
            }
        }

        return false;
    }
}
