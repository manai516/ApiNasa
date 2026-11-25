import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final String DEMO_API_URL =
            "https://api.nasa.gov/planetary/apod?api_key=B6k7QfknWkWwwmGHTWTctTPdcgfNdrMRcMRLfmmj";

    public static final ObjectMapper mapper = new ObjectMapper();

    static void main(String[] args) throws IOException {
        try (CloseableHttpClient httpClient = createHttpClient();
                CloseableHttpResponse response = httpClient.execute(new HttpGet(DEMO_API_URL))) {

            System.out.println("HTTP status: " + response.getStatusLine());
            String jsonResponse = EntityUtils.toString(response.getEntity());

            NasaApodResponse nasa = mapper.readValue(jsonResponse, NasaApodResponse.class);

            System.out.println("Copyright: " + nasa.copyright());
            System.out.println("Date: " + nasa.date());
            System.out.println("Explanation: " + nasa.explanation());
            System.out.println("hdurl: " + nasa.hdurl());
            System.out.println("Media_type: " + nasa.media_type());
            System.out.println("Service_version: " + nasa.service_version());
            System.out.println("Title: " + nasa.title());
            System.out.println("URL: " + nasa.url());

            String fileUrl = nasa.url();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            try (CloseableHttpResponse downloadResponse = httpClient.execute(new HttpGet(fileUrl))) {
                byte[] content = EntityUtils.toByteArray(downloadResponse.getEntity());
                Files.write(Path.of(fileName), content);
                System.out.println("Файл сохранён: " + fileName);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
    }
}
