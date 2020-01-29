# Kotlin + SpringBootによる書籍管理システム

## 内容

Kotlin + SpringBoot + JPA + Thymeleaf + Security で書籍管理のWebアプリを作成しました。  
概要は以下の通りです。  

- 書籍には著者の属性を持つ
- データはRDBに保持する
- アプリはCRUD機能を持つ
- 書籍のタイトルと著者は空白では登録、更新できない
- 排他制御を行う
- ログ出力を行う
- データの登録・更新時にタイムスタンプとバージョンとユーザを自動的に更新する
- ログイン機能を持つ。
- ユーザ認証と権限によりアクセスを制御する。
- DBはdockerで起動したpostgreSQLに接続する。
- 単体テストではH2DBを使用する。

## 注意点

- etc/dockerフォルダで"docker-compose up"すると、springが起動しなくなるため、別のフォルダに移動させてから起動する必要がある。
- 各WebDriverをが置いてあるフォルダにPATHを通してから実行する必要がある。

## 確認方法

SpringBootアプリケーション起動後、以下のURLで起動できます。  

<http://localhost:8080/>