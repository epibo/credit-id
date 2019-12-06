### 项目

1. 打包
sbt assembly

2. 运行
java -jar target/scala-2.12.2/credit-id_2.12-0.1.0.jar "start --public-key asdfasdfasdfaskdjffkalsfd "
//目前问题：JWTAuthenticator.pstateless.inBearerToken 会在 runtime 出现 NoSuchMethodError

### 数字信用链与雄安数字身份公共服务平台间的交互需求					
					
* _（10月15日之前出接口）_

#### 一、交互接口需求					
* 注：本次不给每个自然人分配独立的公私钥对、而是平台拥有一个公私钥对。每次上传信息时，平台可以使用拥有的私钥对信息签名后，并一起上传签名。					

> `平台`是机构提供的（调用我们接口的）平台。


**1. 机构注册**  
 _a. 机构登记_  
-  IN：机构 ID，公钥数组（只有最后一个公钥有效），签名（`pubKey[].map(->privKey).foreach(sign(机构 ID))`）。  
- OUT：状态码。

> 提供一个公钥更新接口？

 _b. Get 机构 ID 对应的公钥数组_  
-  IN：机构 ID；  
- OUT：公钥数组，状态码。


**2. 个人注册**  
 _a. CID 登记_  
-  IN：CID，个人信息（data），签名（`sign(hash(CID + data))`）。  
- OUT：状态码。

 _b. 操作记录_  
-  IN：CID，记录（data），签名（`sign(hash(CID + data))`）。  
- OUT：状态码。


**3. 凭证签发**  
* _`凭证`是指由不同机构各自根据 CID 生成的 **子 ID 及其附属属性**。_  

 _a. 凭证登记_  
-  IN：CID，机构 ID，凭证内容（包括：ID、所属 CID 、有效期和其他属性），签名（`sign(hash(CID + 机构 ID + 凭证内容))`）。  
- OUT：凭证的`链上 ID`，状态码。

 _b. 注销凭证_（`链上 ID` -> 凭证 -> invalid）  
-  IN：凭证的`链上 ID`，签名（`sign(链上 ID)`）。　　
- OUT：状态码。

**4. 凭证使用**  
 _a. 验证凭证_  
-  IN：凭证的`链上 ID`，签名（`sign(random + 链上 ID)`）　　
- OUT：凭证状态（有效、不存在、已失效），凭证明文，状态码。

> 访问`获取随机数`接口（表明用途）。保留会话 session。  
> _签名的目的是 **限制未授权的访问**，但是这与 [DDO的定义](https://github.com/ontio/ontology-DID/blob/master/docs/cn/ONTID_protocol_spec_cn.md#15-%E8%BA%AB%E4%BB%BD%E6%8F%8F%E8%BF%B0%E5%AF%B9%E8%B1%A1ddo%E8%A7%84%E8%8C%83)
> 相冲突：_
>> **由DDO的控制人写入到区块链，并向所有用户开放读取。**


#### 二、数字信用链系统内部的功能需求（该部分需求可根据实际需要选择实施或用其它方式实施）

1. 维持一个 CID 到凭证的对应关系；  
  a. CID 的添加；  
  b. 通过 CID 查到凭证列表；  
  c. 通过凭证查到 CID。

2. 同时提供凭证的存储和凭证的状态核验功能；  
  a. 凭证的添加；  
  b. 凭证的状态查询；  
  c. 根据凭证的有效期智能合约控制凭证状态。

3. 维持 CID 到认证记录的关系；  
  a. 可以通过 CID，查到认证记录列表。

备注：CID 是自然的身份 ID。每个凭证都是属于某一个 CID 的。  

> `认证记录`是指验证凭证有效性的记录。
