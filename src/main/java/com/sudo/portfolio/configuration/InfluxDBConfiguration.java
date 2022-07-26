package com.sudo.portfolio.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * This class instantiates an influxDBClient
 * as a bean for spring boot
 */
@Configuration
@Getter @Setter
public class InfluxDBConfiguration {
    @Value("${spring.influx.url}")
    private String url;
    @Value("${spring.influx.user}")
    private String username;
    @Value("${spring.influx.password}")
    private String password;
    @Value("${spring.influx.bucket}")
    private String bucket;
    @Value("${spring.influx.organization}")
    private String org;
    @Value("${spring.influx.token}")
    private String token;

    /**
     * Instantiate InfluxDBClient based on values in application.properties file
     * @return influxDBClient
     */
    @Bean
    public InfluxDBClient getInfluxDBClient() {
        InfluxDBClientOptions influxDBClientOptions =
                InfluxDBClientOptions.builder()
                        .url(getUrl())
                        .authenticate(getUsername(), getPassword().toCharArray())
                        .bucket(getBucket())
                        .org(getOrg())
                        .authenticateToken(getToken().toCharArray())
                        .build();

        return InfluxDBClientFactory.create(influxDBClientOptions);
    }
}
