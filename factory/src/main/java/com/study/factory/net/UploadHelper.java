package com.study.factory.net;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.study.factory.Factory;

/**
 * @author power sky
 * @version 1.0
 * @createTime 2020年03月29日
 */
public class UploadHelper {

    private static final String TAG = "uploader";

    private static final String END_POINT = "http://oss-cn-hangzhou.aliyuncs.com";
    private static final String STS_SERVER = "STS应用服务器地址，例如http://abc.com";
    private static final String BUCKET_NAME = "jk-italk";

    private static OSS getClient() {
        //存储区域相关

// 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(STS_SERVER);

// 配置类如果不设置，会有默认配置。
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。

        return new OSSClient(Factory.app(), END_POINT, credentialProvider);
    }


    /**
     * 上传最终方法，成功放回一个路径
     *
     * @param objKey 上传上去后，在服务器上创建一个KEY
     * @param path   需要上传文件的路径
     * @return 存储的地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, objKey, path);

        // 文件元信息的设置是可选的。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type。
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5。
        // put.setMetadata(metadata);

        try {
            //初始化上传的client
            OSS oss = getClient();
            //开始上传
            PutObjectResult putResult = oss.putObject(put);
            //得到一个外网可访问的地址
            String url = oss.presignPublicObjectURL(BUCKET_NAME,objKey);
            Log.d(TAG,String.format("PbulicObjectURL :%s",url));
            return url;
        } catch (ClientException e) {
            // 本地异常，如网络异常等。
            e.printStackTrace();
            return null;
        } catch (ServiceException e) {
            // 服务异常。
            Log.e("RequestId", e.getRequestId());
            Log.e("ErrorCode", e.getErrorCode());
            Log.e("HostId", e.getHostId());
            Log.e("RawMessage", e.getRawMessage());
        }
        return null;
    }
}
