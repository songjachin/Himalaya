package com.songjachin.himalaya.constants;

/**
 * Created by matthew on 2020/4/24 11:18
 * day day up!
 */
public class Constants {
    public static final int COUNT_RECOMMEND = 50;

    public  static  final int COUNT_TRACKS = 50;
    public static final int COUNT_DEFAULT = 50;
    public static final int COUNT_HOT_WORD = 10;

    //数据库相关的常量
    public static final String DB_NAME = "ximala.db";
    //数据库的版本
    public static final int DB_VERSION_CODE = 1;
    //订阅的表名
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_TRACKS_COUNT = "tracksCount";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";
    //订阅最多个数
    public static final int MAX_SUB_COUNT = 100;
    //历史记录的表名
    public static final String HISTORY_TB_NAME = "tb_history";
    public static final String HISTORY_ID = "_id";
    public static final String HISTORY_TRACK_ID = "historyTrackId";
    public static final String HISTORY_TITLE = "historyTitle";
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";
    public static final String HISTORY_DURATION = "historyDuration";
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";
    public static final String HISTORY_COVER = "historyCover";
    public static final String HISTORY_AUTHOR = "history_author";
    //最大的历史记录数
    public static final int MAX_HISTORY_COUNT = 100;

}
