### 数字信用链与雄安数字身份公共服务平台

#### 一、项目部署

1. 打包  
sbt assembly

2. 部署  
将文件`target/scala-2.12.2/credit-id_2.12-0.1.0.jar`移动到目标目录。

3. 运行  
`java -jar [your/target/dir/]credit-id_2.12-0.1.0.jar "start --public-key [your_hmac256_key]"`


#### 二、API 文档

请参阅 [docs/APIs3.md](./docs/APIs3.md)
