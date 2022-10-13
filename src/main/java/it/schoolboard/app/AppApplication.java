/*
    SchoolBoard: the free and open source all-in-one platform for school
    Copyright (C) 2020-2022 Lo Mele Vittorio
    Copyright (C) 2022 Flowopia Network

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    INFO:           info@schoolboard.it
    COMMERCIAL:     commerciale@schoolboard.it
    DEVELOPEMENT:   sviluppo@schoolboard.it
 */

package it.schoolboard.app;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import it.schoolboard.app.utility.S3Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRedisHttpSession
public class AppApplication {

    protected static final Logger parentLogger = LogManager.getLogger();

    @Autowired
    private S3Client s3Client;

    // entrypoint
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // initialization tasks
    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            parentLogger.info("AUTH SERVER IS " + environment.getProperty("keycloak.auth-server-url"));

            // ---- STORAGE CHECK -----
            boolean publicBucket = s3Client.getS3Client().bucketExists(
                    BucketExistsArgs.builder().bucket(
                            s3Client.getPublicBucketName()
                    ).build()
            );

            if (!publicBucket) {
                parentLogger.warn("S3 public storage bucket not found! Creating it with name \"" +
                        s3Client.getPublicBucketName() + "\"");

                String publicPolicy = "{\n" +
                        "    \"Version\": \"2012-10-17\",\n" +
                        "    \"Statement\": [\n" +
                        "        {\n" +
                        "            \"Effect\": \"Allow\",\n" +
                        "            \"Principal\": {\n" +
                        "                \"AWS\": [\n" +
                        "                    \"*\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"Action\": [\n" +
                        "                \"s3:GetObject\"\n" +
                        "            ],\n" +
                        "            \"Resource\": [\n" +
                        "                \"arn:aws:s3:::" + s3Client.getPublicBucketName() +
                        "/*\"\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";

                // public bucket does not exist, so create it
                s3Client.getS3Client().makeBucket(
                        MakeBucketArgs.builder().bucket(
                                s3Client.getPublicBucketName()
                        ).build()
                );

                // set bucket type to public
                s3Client.getS3Client().setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(s3Client.getPublicBucketName())
                                .config(publicPolicy)
                                .build()
                );
            } else {
                parentLogger.info("S3 public storage bucket OK");
            }

            boolean privateBucket = s3Client.getS3Client().bucketExists(
                    BucketExistsArgs.builder().bucket(
                            s3Client.getPrivateBucketName()
                    ).build()
            );

            if (!privateBucket) {
                parentLogger.warn("S3 private storage bucket not found! Creating it with name \"" +
                        s3Client.getPrivateBucketName() + "\"");

                // private bucket does not exist, so create it
                s3Client.getS3Client().makeBucket(
                        MakeBucketArgs.builder().bucket(
                                s3Client.getPrivateBucketName()
                        ).build()
                );
            } else {
                parentLogger.info("S3 private storage bucket OK");
            }

        };
    }

}
