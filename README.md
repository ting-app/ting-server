# ting-server (WIP) [![Java CI with Maven](https://github.com/Frederick-S/ting-server/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/Frederick-S/ting-server/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/Frederick-S/ting-server/branch/main/graph/badge.svg?token=2ZS54PB3DB)](https://codecov.io/gh/Frederick-S/ting-server)
一个更自由的听力平台。

## Getting Started
### Prerequisite
1. Amazon S3
    1. Create a bucket
    2. Edit `Cross-origin resource sharing (CORS)` setting under the `Permissions` tab to add an allowed origin (the host of [ting-app](https://github.com/Frederick-S/ting-app)):
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

## License
[MIT](LICENSE)
