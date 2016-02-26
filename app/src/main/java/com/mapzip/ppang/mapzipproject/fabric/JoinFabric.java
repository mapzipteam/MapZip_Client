package com.mapzip.ppang.mapzipproject.fabric;

/**
 * Created by myZZUNG on 2016. 2. 25..
 */
/**
 * fabric SignUp Event Preferences
 */
public class JoinFabric{
    /**
     * Fabric Join flag part
     */
    public final int FABRIC_JOIN_ERROR_ID = 11;
    public final int FABRIC_JOIN_ERROR_ID_PATTERN = 12;
    public final int FABRIC_JOIN_ERROR_ID_SHORT = 13;
    public final int FABRIC_JOIN_ERROR_DUPID = 14;
    public final int FABRIC_JOIN_ERROR_ID_LONG = 15;

    public final int FABRIC_JOIN_ERROR_PW = 16;
    public final int FABRIC_JOIN_ERROR_PW_SHORT = 17;
    public final int FABRIC_JOIN_ERROR_PW_CONFIRM = 18;
    public final int FABRIC_JOIN_SUCCESS = 19;

    /**
     * Join Fail Category part
     */
    public final String KEY_JOIN_FAIL_CATEGORY = "Fail Category";
    public final String VALUE_FAIL_CATEGORY_ID = "ID";
    public final String VALUE_FAIL_CATEGORY_PW = "PW";

    /**
     * Join Fail Detail Reason part
     */
    public final String KEY_JOIN_FAIL_REASON = "Fail Detail Reason";
    public final String VALUE_FAIL_ID_LENGTH_SHORT = "ID Length Short";
    public final String VALUE_FAIL_ID_LENGTH_LONG = "ID Length Long";
    public final String VALUE_FAIL_ID_PATTERN_MISS = "ID Pattern miss match";
    public final String VALUE_FAIL_ID_DUP = "ID Duplicate in Server";
    public final String VALUE_FAIL_PW_LENGTH_SHORT = "PW Length Short";
    public final String VALUE_FAIL_PW_CONFIRM = "PW Confirm";
}
