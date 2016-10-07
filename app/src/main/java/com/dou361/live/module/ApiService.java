package com.dou361.live.module;


import com.dou361.live.utils.SystemConfig;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;


/**
 * ========================================
 * <p/>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2016/3/7
 * <p/>
 * 描 述：网络接口api
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public interface ApiService {

    /**
     * 直播相关接口
     */
    @Multipart
    @POST(SystemConfig.LIVE_HEAD + "/{method}")
    Call<String> live(@Path("method") String method, @PartMap Map<String, RequestBody> map);

    @Multipart
    @POST(SystemConfig.LIVE_HEAD + "/{method}")
    Call<String> lives(@Path("method") String method, @PartMap Map<String, RequestBody> map);


}
