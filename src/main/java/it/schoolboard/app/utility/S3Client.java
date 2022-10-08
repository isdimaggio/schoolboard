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

package it.schoolboard.app.utility;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("s3Client")
public class S3Client {
    private final MinioClient s3Client;

    private final String privateBucketName;

    private final String publicBucketName;

    @Autowired
    public S3Client(
            @Value("${s3.endpoint}") String endpoint,
            @Value("${s3.user}") String username,
            @Value("${s3.secretkey}") String secret,
            @Value("${s3.private-bucket}") String privateBucketName,
            @Value("${s3.public-bucket}") String publicBucketName,
            @Value("${s3.region}") String region
    ) {
        this.privateBucketName = privateBucketName;
        this.publicBucketName = publicBucketName;

        s3Client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, secret)
                .region(region)
                .build();
    }

    public MinioClient getS3Client() {
        return s3Client;
    }

    public String getPrivateBucketName() {
        return privateBucketName;
    }

    public String getPublicBucketName() {
        return publicBucketName;
    }

}
