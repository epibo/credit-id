### 数字信用链与雄安数字身份公共服务平台 API 文档


**1. 机构注册**  
_a. 机构登记_  
 
```http request
POST /org_register

params:
    org_id  -> 机构 ID
    pubkeys -> 公钥数组（只有最后一个公钥有效）
  * hmac    -> 算法（`hmac256(org_id + pubkeys.reduce(_ + _))`）

response: 
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

_b. 公钥更新_  
 
```http request
POST /org_upd_pubkey

params:
    org_id  -> 机构 ID
    pubkey  -> 公钥
  * hmac    -> 算法（`hmac256(org_id + pubkey)`）

response:
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

_c. Get 机构 ID 对应的公钥数组_  
 
```http request
GET /org_get_pubkeys

params:
    org_id  -> 机构 ID

response:
    OK(200) && {
        { pubkeys: [...] }
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

**2. 个人注册**  
_a. CID 登记_  

```http request
POST /cid_register

params:
    cid  -> CID
    data -> 个人信息
  * hmac -> 算法（`hmac256(cid + data)`）

response:
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

_b. 操作记录_  

```http request
POST /cid_record

params:
    cid  -> CID
    data -> 记录
  * hmac -> 算法（`hmac256(cid + data)`）

response:
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
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
  * hmac   -> 算法（`hmac256(cid + org_id + data)`）

response:
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```
> 合约中没有 **凭证的`链上 ID`** 这个概念。

_b. 注销凭证_（凭证 -> invalid）  

```http request
POST /credit_destroy

params:
    cid    -> CID
    org_id -> 机构 ID
  * hmac   -> 算法（`hmac256(cid + org_id)`）

# 由于注销操作只能进行一次，所以不需要随机数。（使用随机数的目的是为了避免黑客在使用同一个`hmac`
调用多次接口的情况下仍然能通过验证。）

response:
    OK(200) && {
        0x1 -> 成功
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

**4. 凭证使用**  
_a. 验证凭证_  

```http request
GET /credit_use

params:
    cid    -> CID
    org_id -> 机构 ID
  * hmac   -> 算法（`hmac256(cid + org_id + random)`）

response:
    OK(200) && {
        { status: valid/not exist/invalid,
          credit: {
             与 /credit_register 接口的 data 属性一致。
          }
        }
     || 0x2 -> HMAC验证失败
     || 0x3 -> 合约调用失败
    }
```

_b. 随机数_  

```http request
GET /random

params:
    usage -> `credit_use/???`（目前仅有一个用途`credit_use`）

response:
    OK(200) && random
```
