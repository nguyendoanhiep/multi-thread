package com.example.demo.service;

import com.example.demo.dto.LivenessRequest;
import com.example.demo.dto.SHA;
import com.example.demo.response.DataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

@Slf4j
@Service
public class Liveness {
    private final String secret = "R6ygKsTZ0wRqJVmeAkTy6IGmyLClk265";

    private String LIVENESS_CHECK = "/process";

    @Value("${liveness-server.host:192.168.0.176}")
    private String host;
    @Value("${liveness-server.port:23005}")
    private String port;
    @Value("${liveness-server.enable:false}")
    private boolean enableLiveness;

    @Value("${liveness-server.ssl.enable:true}")
    private boolean ssl;
    @Value("${liveness-server.ssl.client-cert.need:false}")
    private boolean needClientCert;
    @Value("${liveness-server.ssl.client-cert.certPath:client1.p12}")
    private String certPath;
    @Value("${liveness-server.ssl.client-cert.password:Ab123456}")
    private String certPass;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${liveness-server.secure:true}")
    private boolean isSecureMode;

    @Value("${image.folder.path}")
    private String imageFolderPath;

    @Value("${image.folder.path-fake-image}")
    private String fakeImagesFolder;

    @Value("${image.folder.path-error-image}")
    private String errorImagesFolder;

    @Value("${image.is-save-image:false}")
    private boolean isSaveImage;

    @Value("${nThread:1}")
    private Integer nThread;
    private SSLContext g_SSLContext = null;


    public void run() {
        log.info("Please Wait , The System Is Processing");
        List<String> knownErrors = (Arrays.asList(
                "IMAGE_BLUR",
                "IMAGE_NOT_PORTRAIT",
                "FACE_TOO_SMALL_ABS",
                "FACE_TOO_SMALL_REL",
                "FACE_TOO_DARK",
                "FACE_NOT_DETECTED",
                "FACE_MULTI_DETECTION",
                "EYE_OCCLUDED",
                "MOUTH_OCCLUDED"
        ));
        AtomicInteger totalImageScan = new AtomicInteger(0);
        AtomicInteger totalFakeImages = new AtomicInteger(0);
        AtomicInteger totalErrorImages = new AtomicInteger(0);
        AtomicInteger rfScore095 = new AtomicInteger(0);
        AtomicInteger rfScore096 = new AtomicInteger(0);
        AtomicInteger rfScore097 = new AtomicInteger(0);
        AtomicInteger rfScore098 = new AtomicInteger(0);
        AtomicInteger rfScore099 = new AtomicInteger(0);
        AtomicInteger rfScore100 = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(nThread);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try {
            File folder = new File(imageFolderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                log.error("Lỗi xử lý file {}: {}");
            }
            List<File> images = new ArrayList<>();

            File[] folderParent = folder.listFiles();
            for (File file : folderParent){
                if (file.isDirectory()) {
                    images.addAll(collectImages(file));
                } else if (file.getName().toLowerCase().endsWith(".jpg")
                        || file.getName().toLowerCase().endsWith(".jpeg")
                        || file.getName().toLowerCase().endsWith(".png")) {
                    images.add(file);
                }
            }

            for (File file : images) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        log.info("Image Name {}: ", file.getName());
                        byte[] fileBytes = Files.readAllBytes(file.toPath());
                        String base64 = Base64.getEncoder().encodeToString(fileBytes);
                        String any = checkLiveness(base64);
                        if(any != null){
                            DataResponse response = objectMapper.readValue(any, DataResponse.class);
                            log.info("response {}: ", any);
                            totalImageScan.incrementAndGet();
                            if (response.getSuccess().equalsIgnoreCase("true") && !response.getIs_rf()) {
                                if(isSaveImage){
                                    Path destinationPath = Paths.get(fakeImagesFolder, file.getName());
                                    Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                                }
                                totalFakeImages.incrementAndGet();
                            }
                            if (response.getSuccess().equalsIgnoreCase("false") && knownErrors.contains(response.getErr_code())) {
                                if(isSaveImage) {
                                    Path destinationPath = Paths.get(errorImagesFolder, file.getName());
                                    Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                                }
                                totalErrorImages.incrementAndGet();
                            }
                            if(response.getRf_score() != null && response.getRf_score() > 0.94){
                                if(response.getRf_score().equals(0.95)) {
                                    rfScore095.incrementAndGet();
                                    return;
                                }
                                if(response.getRf_score().equals(0.96)) {
                                    rfScore096.incrementAndGet();
                                    return;
                                }
                                if(response.getRf_score().equals(0.97)) {
                                    rfScore097.incrementAndGet();
                                    return;
                                }
                                if(response.getRf_score().equals(0.98)) {
                                    rfScore098.incrementAndGet();
                                    return;
                                }
                                if(response.getRf_score().equals(0.99)) {
                                    rfScore099.incrementAndGet();
                                    return;
                                }
                                if(response.getRf_score().equals(1.0)) {
                                    rfScore100.incrementAndGet();
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("Lỗi xử lý file {}: {}", file.getName(), e.getMessage());
                    }
                }, executor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("The process is completed");
            int total = totalImageScan.get();
            int fake = totalFakeImages.get();
            int error = totalErrorImages.get();

            double fakePercentage = total > 0 ? (fake * 100.0 / total) : 0;
            double errorPercentage = total > 0 ? (error * 100.0 / total) : 0;

            log.info("Total images processed: {}", total + " (100.00%)");
            log.info("Total fake images: {} ({}%)", fake, String.format("%.2f", fakePercentage));
            log.info("Total error images: {} ({}%)", error, String.format("%.2f", errorPercentage));
            log.info("Total images have rf_score is 0.95: {} ", rfScore095.get());
            log.info("Total images have rf_score is 0.96: {} ", rfScore096.get());
            log.info("Total images have rf_score is 0.97: {} ", rfScore097.get());
            log.info("Total images have rf_score is 0.98: {} ", rfScore098.get());
            log.info("Total images have rf_score is 0.99: {} ", rfScore099.get());
            log.info("Total images have rf_score is 1.0: {} ", rfScore100.get());

            try {
                String jarPath = new File(System.getProperty("java.class.path")).getAbsolutePath();
                String jarDir = new File(jarPath).getParent();
                File resultFile = new File(jarDir, "log-result.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
                    writer.write("Total images processed: " + total + " (100.00%)" + System.lineSeparator());
                    writer.write("Total fake images: " + fake + " (" + String.format("%.2f", fakePercentage) + "%)" + System.lineSeparator());
                    writer.write("Total error images: " + error + " (" + String.format("%.2f", errorPercentage) + "%)" + System.lineSeparator());
                    writer.write("Total images have rf_score is 0.95 " + rfScore095.get() );
                    writer.write("Total images have rf_score is 0.96 " + rfScore096.get() );
                    writer.write("Total images have rf_score is 0.97 " + rfScore097.get() );
                    writer.write("Total images have rf_score is 0.98 " + rfScore098.get() );
                    writer.write("Total images have rf_score is 0.99 " + rfScore099.get() );
                    writer.write("Total images have rf_score is 1.0 " + rfScore100.get() );
                }

                log.info("Log file written at: {}", resultFile.getAbsolutePath());

            } catch (IOException e) {
                log.error("Failed to write the result log file: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.info("ERROR :{}", e.getMessage());

        } finally {
            executor.shutdown();
        }
    }

   List<File> collectImages(File folder) {
        List<File> images = new ArrayList<>();
        if (folder == null || !folder.exists()) {
            return null;
        }

        File[] files = folder.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file.isDirectory()) {
                images.addAll(collectImages(file));
            }
            else if (file.getName().toLowerCase().endsWith(".jpg")
                    || file.getName().toLowerCase().endsWith(".jpeg")
                    || file.getName().toLowerCase().endsWith(".png")) {
                images.add(file);
            }
        }
        return images;
    }

    public String checkLiveness(String b64Image) {
        LivenessRequest request = new LivenessRequest();
        if (isSecureMode) {
            b64Image = b64Image + calculateHash(b64Image);
        }
        request.setImg_src(b64Image);
        String sResult = sendRequestToLivenessServer(LIVENESS_CHECK, request);
        log.debug("Liveness check: " + sResult);
        return sResult;
    }

    private String calculateHash(String b64Image) {
        try {
            if (b64Image.length() >= 40) {
                String random = b64Image.substring(b64Image.length() - 40, b64Image.length() - 1);
                byte[] bytes = random.getBytes(StandardCharsets.UTF_8);
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) (bytes[i] + i);
                }
                random = SHA.sha1(new String(bytes, StandardCharsets.UTF_8));
                String data = secret + random + b64Image;
                return SHA.sha1(data);
            }
        } catch (Exception ex) {
            log.error("hash exception: {}", ex.getMessage());
        }
        return SHA.sha1(secret + b64Image);
    }

    private String sendRequestToLivenessServer(String url, Object data) {
        try {
            String serverUrl = (ssl) ? "https://" + host + ":" + port : "http://" + host + ":" + port;
            serverUrl = serverUrl + url;
            //log.debug(url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<String> livenessServiceResult = getRestTemplate().exchange(serverUrl, HttpMethod.POST, new HttpEntity<>(data, headers),
                    new ParameterizedTypeReference<String>() {
                    });
            return livenessServiceResult.getBody();

        } catch (Exception e) {
            log.warn("API call failed but will be ignored: {}", e.getMessage());
        }
        return null;
    }

    private RestTemplate getRestTemplate() {
        try {
            HttpClient client = null;
            if (ssl) {
                SSLContext sslContext = getSSLContext();
                client = HttpClients.custom().setSSLContext(sslContext).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            } else {
                client = HttpClients.createDefault();
            }
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
            return new RestTemplate(requestFactory);
        } catch (Exception ex) {
            log.error("getRestTemplate exception: {}", ex.getMessage());
        }
        return null;
    }

    private SSLContext getSSLContext() {
        if (g_SSLContext == null) {
            //log.info("getSSLContext");
            if (needClientCert) {
                try {
                    char[] password = certPass.toCharArray();
                    g_SSLContext = SSLContextBuilder.create()
                            .loadKeyMaterial(getKeyStore(certPath, password), password)
                            .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
                } catch (Exception ex) {
                    log.error("getSSLContext exception: {}", ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                try {
                    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

                    SSLContextBuilder sslcontext = new SSLContextBuilder();
                    sslcontext.loadTrustMaterial(null, acceptingTrustStrategy);
                    g_SSLContext = sslcontext.build();
                } catch (Exception ex) {
                    log.error("getSSLContext exception: {}", ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        return g_SSLContext;
    }

    private KeyStore getKeyStore(String file, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }


}

