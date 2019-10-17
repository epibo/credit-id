### 数字信用链 API 文档


**1. 机构注册**  
_a. 机构登记_  
 
```http request
POST /org_register

params:
    org_id  -> 机构 ID
    pubkeys -> 公钥数组（只有最后一个公钥有效）
    sign    -> 签名（`pubKey[].map(->privKey).foreach(sign(机构 ID))`）

response:
    0x01 && OK(200)
```

_b. 公钥更新_  
 
```http request
POST /org_upd_pubkey

params:
    org_id  -> 机构 ID
    pubkey  -> 公钥
    sign    -> 签名（`pubKey.map(->privKey).foreach(sign(机构 ID))`）

response:
    0x01 && OK(200)
```

_c. Get 机构 ID 对应的公钥数组_  
 
```http request
GET /org_get_pubkeys

params:
    org_id  -> 机构 ID

response:
    {pubkeys: [...]} && OK(200)
```

**2. 个人注册**  
_a. CID 登记_  

```http request
POST /cid_register

params:
    cid  -> CID
    data -> 个人信息
    sign -> 签名（`sign(hash(CID + data))`）

response:
    0x01 && OK(200)
```

_b. 操作记录_  

```http request
POST /cid_record

params:
    cid  -> CID
    data -> 记录
    sign -> 签名（`sign(hash(CID + data))`）

response:
    0x01 && OK(200)
```

**3. 凭证签发**  
* _`凭证`是指由不同机构各自根据 CID 生成的 **子 ID 及其附属属性**。_  

_a. 凭证登记_  

```http request
POST /credit_register

params:
    cid    -> CID
    org_id -> 机构 ID
    data   -> 凭证内容（包括：ID、所属 CID 、有效期和其他属性）
    sign   -> 签名（`sign(hash(CID + 机构 ID + 凭证内容))`）

response:
    0x01 && OK(200)*
```
> 合约中没有 **凭证的`链上 ID`** 这个概念。
> 以下有“*”标记的就是修订过的。

_b. 注销凭证_（凭证 -> invalid）  

```http request
POST /credit_destroy

params:
    cid    -> CID*
    org_id -> 机构 ID*
    sign   -> 签名（`sign(hash(CID + 机构 ID))`）*

response:
    0x01 && OK(200)
```

**4. 凭证使用**  
_a. 验证凭证_  

```http request
GET /credit_use

params:
    cid    -> CID*
    org_id -> 机构 ID*
    sign   -> 签名（`sign(random + CID + 机构 ID)`）*

response:
    {
        status: valid/not exist/invalid,
        credit: {
            与 /credit_register 接口的 data 属性一致。
        }
    } && OK(200)
```

_b. 随机数_  

```http request
GET /random

params:
    usage -> `credit_use/???`（目前仅有一个用途`credit_use`）

response:
    random && OK(200)
```
