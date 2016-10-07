package com.dou361.live.module;

import com.alibaba.fastjson.JSON;
import com.dou361.baseutils.utils.DateUtils;
import com.dou361.baseutils.utils.FileUtils;
import com.dou361.baseutils.utils.LogUtils;
import com.dou361.baseutils.utils.NetworkUtils;
import com.dou361.baseutils.utils.StringUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.live.bean.CallBean;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.bean.MessageBean;
import com.dou361.live.bean.UserBean;
import com.dou361.live.utils.SystemConfig;
import com.dou361.live.utils.UserManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

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
 * 描 述：具体的网络接口请求，分阻塞线程和异步线程
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class ApiServiceUtils {

    /**
     * 首先，判断网络，
     * 有网络，则从网络获取，并保存到缓存中，
     * 无网络，则从缓存中获取
     * 判断网络设置缓存时间
     * 有网络，设置1分钟失效，
     * 无网络，设置两周失效
     */
    private static final String TAG = ApiServiceUtils.class.getSimpleName();

    /**
     * 加载协议，是否从本地取数据true是false否
     */
    public static String loadData(Map<String, String> map, String preRequest, int index, boolean isSave) {
        String key = getKey(map);
        LogUtils.logTagName(TAG).log(SystemConfig.IP + preRequest + "?" + key);
        String json = null;
        /** 有网络再看过期时间 */
        if (NetworkUtils.isNetworkConnected(UIUtils.getContext())) {
            /** 1.从本地缓存读取数据，查看缓存时间 */
            if (isSave) {
                json = loadFromLocal(key, index, true);
            }
            /** 2.如果缓存时间过期，从网络加载 */
            if (StringUtils.isEmpty(json)) {
                json = loadFromNet(map, preRequest, index);
                if (json == null) {
                    /** 网络出错 */
                    return null;
                } else {
                    /** 3.把数据保存到本地保存到本地 */
                    saveToLocal(json, key, index);
                }
            }
        } else {
            /** 无网络两周失效 */
            json = loadFromLocal(key, index, false);
        }
        return json;
    }


    /**
     * 本地加载数据 @param hasnetwork是否有网络
     */
    private static String loadFromLocal(String key, int index, boolean hasnetwork) {
        String path = UIUtils.getContext().getCacheDir() + File.separator + "httpcache" + File.separator;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path
                    + key + "_" + index));
            String line = reader.readLine();// 第一行是时间
            Long time = Long.valueOf(line);
            if ((hasnetwork && time > System.currentTimeMillis()) || (!hasnetwork && time + 1000 * 60 * 60 * 24 * 14 > System.currentTimeMillis())) {// 如果时间未过期
                StringBuilder sb = new StringBuilder();
                String result;
                while ((result = reader.readLine()) != null) {
                    sb.append(result);
                }
                return sb.toString();
            }
        } catch (Exception e) {
        } finally {
            FileUtils.close(reader);
        }
        return null;
    }

    /**
     * 同步网络加载数据
     */
    private static String loadFromNet(Map<String, String> map, String preRequest, int index) {
        Call<String> call = getJsonObjectCall(map, preRequest);
        Response<String> response = null;
        try {
            response = call.execute();
            String body = response.body();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步网络加载数据
     */
    private static void loadData(Map<String, String> map, String preRequest, Callback cb) {
        String key = getKey(map);
        LogUtils.logTagName(TAG).log(SystemConfig.IP + preRequest + "?" + key);
        Call<String> call = getJsonObjectCall(map, preRequest);
        call.enqueue(cb);
    }

    /**
     * 获得json回调
     */
    private static Call<String> getJsonObjectCall(Map<String, String> map, String preRequest) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(logging)
                /**get请求才有效后续再看看有没有解决版本*/
//                .addInterceptor(interceptor)
//                .addNetworkInterceptor(interceptor)
//                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SystemConfig.IP)
                .client(client)
                // add the converter-scalars for coverting String
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService mApiServicePresenter = retrofit.create(ApiService.class);
        Map<String, RequestBody> mapBody = new HashMap<String, RequestBody>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapBody.put(entry.getKey(), RequestBody.create(MediaType.parse("text/plain"), entry.getValue()));
        }
//        File imgFile = new File("");
//        if (imgFile != null) {
//            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), imgFile);
//            mapBody.put("image\"; filename=\""+imgFile.getName()+"\"", fileBody);
//        }
        Call<String> call = mApiServicePresenter.live(preRequest, mapBody);
        return call;
    }

    /**
     * 保存数据
     */
    private static void saveToLocal(String json, String key, int index) {
        String path = UIUtils.getContext().getCacheDir() + File.separator + "httpcache" + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path +
                    key + "_" + index));
            long time = System.currentTimeMillis() + 1000 * 5;// 先计算出过期时间，写入第一行
            writer.write(time + "\r\n");
            writer.write(json.toCharArray());
            writer.flush();
        } catch (Exception e) {
            LogUtils.log(e);
        } finally {
            FileUtils.close(writer);
        }
    }

    /**
     * 参数列表转换为get方式的参数
     */
    private static String getKey(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(entry.getKey() + "=" + entry.getValue());
            }
        }
        return sb.toString();
    }


    /**
     * 网络请求拦截器
     */
    private static Interceptor interceptor = new Interceptor() {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtils.isNetworkAvailable(UIUtils.getContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                LogUtils.log("---------------暂无网络-----------------");
            }

            okhttp3.Response response = chain.proceed(request);
            if (NetworkUtils.isNetworkAvailable(UIUtils.getContext())) {
                /**
                 * 设缓存有效期为1分钟
                 */
                int maxAge = 60 * 60; // read from cache for 1 minute
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                /**
                 * 设缓存有效期为两个星期
                 */
                int maxStale = 60 * 60 * 24 * 14; // tolerate 2-weeks stale
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    };

    /**
     * 该协议的访问日期
     */
    protected static String getDateTime() {
        return (String) DateUtils.getData(DateUtils.DateType.sdf_yyyy_MM_dd_HH_mm_ss, new Date());
    }


    /**
     * 用户登录操作
     */
    public static void login(String name, String password, Callback cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", name);
        map.put("password", password);
        loadData(map, "Login", cb);
    }

    /**
     * 用户注册操作
     */
    public static void register(String name, String password, Callback cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", name);
        map.put("password", password);
        loadData(map, "Register", cb);
    }

    /**
     * 获取用户名
     */
    private static String getUserName() {
        UserBean userBean = UserManager.getInstance().getUserBean();
        if (userBean == null || userBean.getUsername() == null) {
            return "";
        } else {
            return userBean.getUsername();
        }
    }

    /**
     * 开始直播
     */
    public static void startLive(String title, Callback cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getUserName());
        map.put("title", title);
        loadData(map, "Auditing_Live_Telecast", cb);
    }

    /**
     * 关闭直播
     */
    public static void closeLive(int dirid, Callback cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getUserName());
        map.put("dirid", dirid + "");
        map.put("LiveState", 1 + "");
        loadData(map, "Live_Close", cb);
    }

    /**
     * 获取直播列表
     */
    public static List<LiveRoom> getLiveList(int index) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("state", 2 + "");
        map.put("pagepieces", index + "");
        String json = loadData(map, "LiveIn", index, true);
        CallBean mCallBean = JSON.parseObject(json, CallBean.class);
        if (mCallBean != null && mCallBean.getCode() == SystemConfig.Req_Success) {
            String data = mCallBean.getData();
            if (data != null && !"".equals(data)) {
                return JSON.parseArray(data, LiveRoom.class);
            }
        }
        return null;
    }

    /**
     * 获取直播间聊天记录列表
     */
    public static List<MessageBean> getMessageList(int dirid, int index) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getUserName());
        map.put("dirid", dirid + "");
        map.put("pagepieces", index + "");
        String json = loadData(map, "Live_Review_ajax", index, true);
        CallBean mCallBean = JSON.parseObject(json, CallBean.class);
        if (mCallBean != null && mCallBean.getCode() == SystemConfig.Req_Success) {
            String data = mCallBean.getData();
            if (data != null && !"".equals(data)) {
                return JSON.parseArray(data, MessageBean.class);
            }
        }
        return null;
    }

    /**
     * 发送消息
     */
    public static void sendMessage(String replyerName, int dirid, String content, Callback cb) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", getUserName());
        map.put("dirid", dirid + "");
        if (replyerName != null && !"".equals(replyerName)) {
            map.put("replyer", replyerName);
        } else {
            map.put("replyer", "0");
        }
        map.put("comments", content);
        map.put("client", "Android");
        loadData(map, "Submit_Comment", cb);
    }


}
