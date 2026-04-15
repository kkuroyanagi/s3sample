# s3sample

Amazon S3 にアクセスするサンプル REST API アプリケーション。

## 技術スタック

- Java 21
- Spring Boot 3.5
- AWS SDK for Java v2 (`software.amazon.awssdk:s3`)
- Lombok

## 事前準備

### 必要な環境変数

| 変数名 | 説明 |
|--------|------|
| `AWS_ACCESS_KEY_ID` | AWS アクセスキー ID |
| `AWS_SECRET_ACCESS_KEY` | AWS シークレットアクセスキー |
| `AWS_S3_BUCKET_NAME` | 対象の S3 バケット名 |

### `.env` ファイルの作成

VSCode の起動構成は `.env` ファイルを参照します。プロジェクトルートに作成してください。

```
AWS_ACCESS_KEY_ID=your-access-key-id
AWS_SECRET_ACCESS_KEY=your-secret-access-key
AWS_S3_BUCKET_NAME=your-bucket-name
```

## 起動方法

```bash
./mvnw spring-boot:run
```

## API リファレンス

### オブジェクト一覧取得

```
GET /api/s3/objects
```

| パラメータ | 必須 | 説明 |
|-----------|------|------|
| `prefix`  | 任意 | フィルタするキープレフィックス（例: `images/`） |

**レスポンス例:**

```json
[
  {
    "key": "images/photo.jpg",
    "size": 204800,
    "lastModified": "2026-04-16T00:00:00Z"
  }
]
```

**curl 例:**

```bash
# 全オブジェクトを取得
curl http://localhost:8080/api/s3/objects

# プレフィックスでフィルタ
curl "http://localhost:8080/api/s3/objects?prefix=images/"
```

---

### ファイルアップロード

```
POST /api/s3/objects
Content-Type: multipart/form-data
```

| パラメータ | 必須 | 説明 |
|-----------|------|------|
| `file`    | 必須 | アップロードするファイル |
| `prefix`  | 任意 | 保存先フォルダパス（例: `documents/2026`） |

**レスポンス例 (201 Created):**

```json
{
  "key": "documents/2026/report.pdf",
  "size": 102400
}
```

**curl 例:**

```bash
# ルートにアップロード
curl -X POST http://localhost:8080/api/s3/objects \
  -F "file=@/path/to/file.txt"

# フォルダを指定してアップロード
curl -X POST http://localhost:8080/api/s3/objects \
  -F "file=@/path/to/file.txt" \
  -F "prefix=documents/2026"
```

## launch.json 例

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "S3sampleApplication",
            "request": "launch",
            "mainClass": "com.example.s3sample.S3sampleApplication",
            "projectName": "s3sample",
            "envFile": "${workspaceFolder}/.env"
        }
    ]
}
```