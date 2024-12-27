# 開発用 Dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Maven インストール
RUN apk update \
  && apk add --no-cache maven

# 作業ディレクトリ作成
WORKDIR /app

# Maven のローカルリポジトリ用ディレクトリ (後で docker-compose でマウント)
# システム的には /root/.m2 がデフォルトですが、人によっては /root/.m2/repository を指定する場合も
VOLUME /root/.m2

# 最後に Maven コマンドを実行する (spring-boot:run)
CMD ["mvn", "spring-boot:run"]