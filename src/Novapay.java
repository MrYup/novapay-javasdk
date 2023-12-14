import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.Base64;

public class Novapay {
    private final String mchId;
    private final String mchRsrPriKeyString;
    private final String hostname;

    public Novapay(String mchId, String mchRsrPriKeyString, String hostname) {
        this.mchId = mchId;
        this.mchRsrPriKeyString = mchRsrPriKeyString;
        this.hostname = hostname;
    }

    public static void main(String[] args) {
        //支付系统识别的商户id
        String merchantId = "1010001";
        //支付系统分配的代付appID
        String disburseAppId = "1010001007";
        //支付系统分配的代收appID
        String paymentAppId = "1010001008";

        //商户自行生产的RSA密钥对 PKCS8；移除首尾标识，以及换行
//        String mchRsrPriKeyString = "Your RSA Private Key";
        //商户RSA 私钥，商户自行保存，请勿泄密
        String mchRsrPriKeyString = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCDgkN412J0O/3GIoH0TXfQk407LUVagNqoxdpwh3fSR072/mfiwoLIi/g8XEH2sTHgefrvljxNhzqvRCc0NUG8TgZWElhM43EGZmX9dFo2EcU/A+oYBmXx5pinm2GSjzDb+vpCajHarFk4pXOD+Kfkboh+q9hp63TXleMEXOuiSkyGrWqrI9IxIShCrhRCsXw46b1pIMv8HxfAxqZyIzFJGkrHk4DdzIjoZcHgX6M0EPSFuLoiwJz4iuoA8uvaXHpqFlQojIWARsAaiVN0gnyYThqm6s9RRgFAusu5RZswHKDhynlMq8fjUIP81E7E2QjzuIJGwr9FclTm4m5uaLzJAgMBAAECggEBAIFNMfP9qdLpLVKDmUzQYm7UtPcrQtaCfPuWVXlw6NH6xNw7NX+Sii6O2MrYVw8sGANDTAQHrlD4Fi4/j2BYwMJZ4Qb90I4Azp9hTQRQQ2aIQGz990wZ9mBKv2uEbgJBAeQ4t5lXep8r+CmnG5X1hXYna9NIOWejV2Wk/OnMRu5wPYAXHcO4G+qIdd+Fx+JsZsrwCW2l+dkC+CKu5a9xHmrapj/Fo61GDxtPveZHVCVLC9rG8nmRJ4BVkOViPZKAuHLECnCbDrgzILVBzZiqRshmYlb0cNxiasoXIB90H3EoEuGM2pyFYx+ZsjfftRJQcIWqcLx0tK45j6e9uIDPiQECgYEAz9wvDj69u+2xbNskFY3uTRXcQEaA5UKTVYK9HWsHVflx7FGAgLcaxL7TlcUaXjOMLWkTmkOvYMMBciUSHz6qWFDy9is60tHpV4PHTbJntvnKW2OIIozw30rPnhObfpdzt3cCCE14jk0/Ltdy4G4L6B0GXgTCxYiZRthtKzPhgiECgYEAofdKbqPD5l0pzMgoHh1lFPFC6BcX/snc/dMrunLu8KAwA63t5Wn/+ILu9DRDVUO2CnLlmWhNwfY+sJuMf8EFEjJjRiJCylvA6dpMdjhu4T5uF48Y7FZp+kYJNFXL/ysL1AZSbjfln83mMiSK6HQDBFhEbNi5l3HzR8Lhb115NakCgYBYdDmuFTMGsyrqyQK9DW4YbbIRxC774O3z2LQDvdUcWA+L4k8N912z1gUSWhNTfRotBOcroZwTUEjgOzhQBYnWFXSfH1xj9KIY9OIuhTovmnLrmxqGoXWrgBmECpIEOA0tSL6bq+Qeom69zz96lDoK57ca8Wm1R+D6TgV++hAAIQKBgFmatuoI3ds3VWW+ojsu5fo1E1/VTugdg45FYp7O8RPce1O9yKwv/XXd8hbWRx4HzNFxCUbappIVCk+obo1LGuQbTkLBt5O+OTbnF4Ab7nPclwmfmBlAIXddgcAbazVq+is9qCLj97x2K9E32EWL0RN5Rbm2GM8JMp9T2GZFH0apAoGAQdZokMl86WNBHxADRavjdysxJGwB8l6ZfDgglLrzl/S6iss+loeTTuQavrItU8nxor15YgjzRq+qmqB0oo1tE3/ofldfZ7+rBdgDz6b+tpD+hW0Fj2BvMC0tSkD1E7DIhNjeZXtnz7spafv85hnAaBqJr1IDnQjAgw9Hv98yVEk=";

        //商户RSA 公钥，需提交给支付系统
        String mchRsrPubKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg4JDeNdidDv9xiKB9E130JONOy1FWoDaqMXacId30kdO9v5n4sKCyIv4PFxB9rEx4Hn675Y8TYc6r0QnNDVBvE4GVhJYTONxBmZl/XRaNhHFPwPqGAZl8eaYp5thko8w2/r6Qmox2qxZOKVzg/in5G6IfqvYaet015XjBFzrokpMhq1qqyPSMSEoQq4UQrF8OOm9aSDL/B8XwMamciMxSRpKx5OA3cyI6GXB4F+jNBD0hbi6IsCc+IrqAPLr2lx6ahZUKIyFgEbAGolTdIJ8mE4apurPUUYBQLrLuUWbMByg4cp5TKvH41CD/NROxNkI87iCRsK/RXJU5uJubmi8yQIDAQAB";

        //支付系统RSA 公钥，用于支付系统回调场景的 或者response签名验签是哟个
        String pltRsrPubKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA36F+ZQlEoDkB5qNGdByCXLXScYR495UHkOTqRVwcXXcEzueuJ7DzLBQK3Fqjhx8AWLuwVZhJevSfNWYcQ1DvxwxnhrLsKC/v8hmdMMKZVMXJTc/pQaTwPbzjY+Zmvha5B3BoiMTD7+/KfQb8+65fNj5+fnwHx7c+P6vdR33Hvc37s6kwgjCoqmQlH1fa9zrIRNTClfFPkeYtKZYsX5qxt0oX/fboJ8lQ81S5IymZch0ELvyjTnQ7FygYXwr7IISBvHGBGICNrmx/9EnC4n0oJspvYSI6/AXMOoAsgW74mmFH/uXZez1RgwwqZTG5msc7ig6NnlbbaHcYNysMVE3bTwIDAQAB";

        //测试环境域名
        String hostname = "https://sandbox-open.swordfishpay.com";

        Novapay novapay = new Novapay(merchantId, mchRsrPriKeyString, hostname);

        //线上银行转账
        try {
            novapay.bankTransfer(disburseAppId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean verifySignature(String string, String signature, String publicKey) throws Exception {
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(pubKey);
        sign.update(string.getBytes(StandardCharsets.UTF_8));
        return sign.verify(Base64.getDecoder().decode(signature));
    }

    public void bankTransfer(String disburseAppId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("mchOrderNo", (int) (Math.random() * 1000 + 1));
        body.put("amount", "10000");
        body.put("currency", "currency");
        body.put("bankCode", "GRABPAY");
        body.put("bankCard", "09053005108");
        body.put("userName", "Manila");
        body.put("userMobile", "639053005108");
        body.put("appId", disburseAppId);
        Map<String, String> options = new HashMap<>();
        options.put("firstName", "Manila");
        options.put("lastName", "");
        options.put("address", "Lucky");
        body.put("options", options);
        Map<String, Object> customExtra = new HashMap<>();
        customExtra.put("userId", 123);
        customExtra.put("othersInfo", "xxxx");
        body.put("customExtra", customExtra);

        String result = hitEndpoint("/disburse/bank-transfer/create", body, "POST");
        System.out.println(result);
    }

    private String hitEndpoint(String uri, Map<String, Object> body, String method) throws Exception {
        //13位时间戳
        long timestamp = System.currentTimeMillis();

        //ASCII升序
        body = kSortInDeep(body);
        String payload = JSONObject.toJSONString(body);

        //签名明文串
        String signString = String.join(".", method, uri, mchId, String.valueOf(timestamp), payload);
        String signature = buildSignature(signString);

        String url = hostname + uri;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .addHeader("X-TIMESTAMP", String.valueOf(timestamp))
                .addHeader("X-SIGNATURE", signature)
                .addHeader("X-MERCHANT-ID", mchId)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();

        int httpCode = response.code();
        String responseBody = response.body().string();
        Headers responseHeaders = response.headers();
        System.out.println("httpCode:" + httpCode + "\nresponseBody:" + responseBody + "\nresponseHeadersTimestamp:" + responseHeaders.get("X-TIMESTAMP") + "\nresponseHeadersSignature:" + responseHeaders.get("X-SIGNATURE"));
        return responseBody;
    }

    private String buildSignature(String signString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(mchRsrPriKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(signString.getBytes(StandardCharsets.UTF_8));

        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    private Map<String, Object> kSortInDeep(Map<String, Object> map) {
        Map<String, Object> sortedMap = new TreeMap<>(map);
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                sortedMap.put(entry.getKey(), kSortInDeep((Map<String, Object>) entry.getValue()));
            }
        }
        return sortedMap;
    }
}
