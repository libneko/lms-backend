package com.neko.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.captcha20230305.AsyncClient;
import com.aliyun.sdk.service.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.sdk.service.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.aliyun.sdk.service.captcha20230305.models.VerifyIntelligentCaptchaResponseBody;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CaptchaUtil {
    private static final String ENDPOINT = "captcha.cn-shanghai.aliyuncs.com";
    private static AsyncClient client;

    static {
        // 初始化阿里云 Client（只初始化一次）
        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                        .accessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                        .build()
        );

        client = AsyncClient.builder()
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create().setEndpointOverride(ENDPOINT)
                )
                .build();
    }

    /**
     * 校验智能验证码
     *
     * @param captchaVerifyParam 前端传来的验证码校验参数
     * @param sceneId 控制台的 SceneId
     * @return 校验是否通过
     */
    public static boolean verifyCaptcha(String captchaVerifyParam, String sceneId) {
        try {
            VerifyIntelligentCaptchaRequest request =
                    VerifyIntelligentCaptchaRequest.builder()
                            .captchaVerifyParam(captchaVerifyParam)
                            .sceneId(sceneId)
                            .build();

            CompletableFuture<VerifyIntelligentCaptchaResponse> future =
                    client.verifyIntelligentCaptcha(request);

            VerifyIntelligentCaptchaResponse resp = future.get(); // 阻塞等待结果

            if (resp.getStatusCode() == 200) {
                VerifyIntelligentCaptchaResponseBody body = resp.getBody();
                if (body.getSuccess()) {
                    VerifyIntelligentCaptchaResponseBody.Result result = body.getResult();
                    if (result.getVerifyResult()) {
                        return Objects.equals(result.getVerifyCode(), "T001");
                    }
                }
            }

            return false;

        } catch (Exception e) {
            System.err.println("Captcha verify error: " + e.getMessage());
            return false;
        }
    }

    /**
     * 程序关闭时调用，避免连接池泄露
     */
    public static void close() {
        if (client != null) {
            client.close();
        }
    }
}
