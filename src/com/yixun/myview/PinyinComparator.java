package com.yixun.myview;

import java.util.Comparator;
import java.util.HashMap;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null)
			o1 = "#";
		if (o2 == null)
			o2 = "#";
		// String ss= PingYinUtil.
		HashMap<String,Object> map1 = (HashMap<String,Object>)o1;
		HashMap<String,Object> map2 = (HashMap<String,Object>)o2;
		return PingYinUtil.getPingYin((String)map1.get("name")).compareTo(PingYinUtil.getPingYin((String) map2.get("name")));
	}

}
