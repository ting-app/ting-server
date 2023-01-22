# ting-server [![Java CI with Maven](https://github.com/Frederick-S/ting-server/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/Frederick-S/ting-server/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/ting-app/ting-server/branch/main/graph/badge.svg?token=2ZS54PB3DB)](https://codecov.io/gh/ting-app/ting-server)
Server side code of [Ting](https://ting.dekiru.app).

## Getting Started
### Prerequisite
1. Amazon S3
    1. Create a bucket
    2. Edit `Cross-origin resource sharing (CORS)` setting under the `Permissions` tab to add an allowed origin (the host of [ting-app](https://github.com/ting-app/ting-app)):
        ```
        [
            {
                "AllowedHeaders": [
                    "*"
                ],
                "AllowedMethods": [
                    "PUT",
                    "GET"
                ],
                "AllowedOrigins": [
                    "http://example.com"
                ],
                "ExposeHeaders": []
            }
        ]
        ```
2. Amazon Simple Email Service
    1. Add a verified identity
3. AWS IAM User
    1. Create an IAM user that has the permission of `AmazonSESFullAccess` and `AmazonS3FullAccess`, then export `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` as environment variables
4. Create a MySQL database called `ting`, then import table structures with [init.sql](db/init.sql)

### Run with Docker
```
sudo docker run -e MYSQL_HOST=your_mysql_host \
  -e MYSQL_USER=your_mysql_user \
  -e MYSQL_PASSWORD=your_mysql_user_password \
  -e REDIS_HOST=your_redis_host \
  -e REDIS_PORT=6379 \
  -e TING_CONFIRM_REGISTRATION_RETURN_URL=https://ting.dekiru.app/#/confirmRegistration \
  -e TING_ALLOWED_ORIGIN=https://ting.dekiru.app \
  -e AWS_SES_REGION=your_ses_region \
  -e AWS_SES_FROM_ADDRESS=ting@ting-noreply.dekiru.app \
  -e AWS_S3_REGION=your_s3_region \
  -e AWS_S3_BUCKET_NAME=ting-static \
  -e AWS_ACCESS_KEY_ID=your_aws_access_key \
  -e AWS_SECRET_ACCESS_KEY=your_aws_secret_access_key \
  -p 8080:8080 \
  -d xiaodanmao/ting-server:latest
```

## License
[MIT](LICENSE)
