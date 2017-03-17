/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dhaval
 */
public enum Platform
{
    Codeforces, Topcoder, HackerEarth;

    private static final Map<Integer, Platform> INT_TO_ENUM_MAP;

    static
    {
	INT_TO_ENUM_MAP = new HashMap<>();
	for (Platform value : Platform.values())
	{
	    INT_TO_ENUM_MAP.put(value.ordinal(), value);
	}
    }

    public static Platform fromOrdinal(int n)
    {
	return INT_TO_ENUM_MAP.get(n);
    }

}
