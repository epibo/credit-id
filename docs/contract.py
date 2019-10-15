OntCversion = '2.0.0'

from boa.interop.System.ExecutionEngine import GetScriptContainer, GetExecutingScriptHash
from boa.interop.System.Blockchain import GetHeight, GetHeader
from ontology.interop.System.Storage import GetContext, Get, Put, Delete
from ontology.interop.System.Runtime import Log, GetTrigger, CheckWitness, Notify, Serialize, Deserialize
from ontology.interop.Ontology.Native import Invoke
from ontology.builtins import state
from ontology.interop.Ontology.Runtime import Base58ToAddress

# MAP_CID_GLOBAL_KEY = 'map_cid_global_key'
# MAP_ORG_GLOBAL_KEY = 'map_org_global_key'

_OWNER_KEY = '_owner_key'
_owner = [b'\x00']


# set the owner codeAddr to your base58 codeAddr (starting with a captical A)
# Owner = Base58ToAddress('APa7uMYqdqpFK2chwwmeE7SrQAWZukuGbX')
# 注意：`Base58`是小端地址编码而来的，而`GetExecutingScriptHash`获取的就是小端地址；
# 合约的`HASH`是大端地址，也是合约的账户地址。如果要向合约转账，就需要账户地址（也就是大端地址）。


def Main(operation, args):
    if operation == 'init':
        # 把参数的字符串转换为字节数组
        account = _toBytes(args[0])
        return init(account)

    ############################################################
    if operation == 'org_register':
        org_id = args[0]
        pubkeys = args[1]
        return OrgRegister(org_id, pubkeys)

    if operation == 'org_upd_pubkey':
        org_id = args[0]
        pubkey = args[1]
        return OrgUpdPubkey(org_id, pubkey)

    if operation == 'org_get_pubkeys':
        org_id = args[0]
        return OrgGetPubkeys(org_id)

    ############################################################
    if operation == 'cid_register':
        cid = args[0]
        data = args[1]  # 个人信息（严格来说是不能上链的）
        return CidRegister(cid, data)

    if operation == 'cid_record':
        cid = args[0]
        data = args[1]  # 表示记录的数据
        return CidRecord(cid, data)

    ############################################################
    if operation == 'credit_register':
        cid = args[0]
        org_id = args[1]
        data = args[2]  # 凭证内容（包括：ID、所属 CID 、有效期和其他属性）
        # 定义是返回凭证的`链上 ID`。
        # 我们这里没有`链上 ID`的概念。
        return CreditRegister(cid, org_id, data)

    if operation == 'credit_destroy':
        cid = args[0]
        org_id = args[1]
        return CreditDestroy(cid, org_id)

    ############################################################
    if operation == 'credit_use':
        cid = args[0]
        org_id = args[1]
        return CreditUse(cid, org_id)

    return False


#################################################################################
#
# :org_id  -> 字符串
# :pubkeys -> List, 不知道能不能传到`Main()`。
#
def OrgRegister(org_id, pubkeys):
    try:
        assert pubkeys
        assert _isOwner()
        assert not _isOrgidRegisterd(org_id)

        map = {}
        key = ''
        for k in pubkeys:
            key = k
            map[k] = False
        map[key] = True

        Put(GetContext(), org_id, Serialize(map))
    except:
        Notify(['org_register', False])
        return False
    else:
        Notify(['org_register', True])
        return True


def OrgUpdPubkey(org_id, pubkey):
    try:
        assert pubkey
        assert _isOwner()
        assert _isOrgidRegisterd(org_id)
        context = GetContext()
        org_map = Deserialize(Get(context, org_id))
        assert org_map

        map = {}
        for k in org_map.keys():
            map[k] = False
        map[pubkey] = True

        Put(context, org_id, Serialize(map))
    except:
        Notify(['org_upd_pubkey', False])
        return False
    else:
        Notify(['org_upd_pubkey', True])
        return True


# TODO: 这个 Notify 有数据。
#
def OrgGetPubkeys(org_id):
    # list = []
    try:
        assert _isOwner()
        assert _isOrgidRegisterd(org_id)
        org_map = Deserialize(Get(GetContext(), org_id))
        assert org_map
        # org_map.items()
        # for k in org_map.keys():
        #     list.append((k, org_map[k]))
    except:
        Notify(['org_get_pubkeys', False])
        return False
    else:
        Notify(['org_get_pubkeys', True, org_map.items()])
        return True


############################################################

def CidRegister(cid, data):
    try:
        assert _isOwner()
        assert not _isCidRegisterd(cid)

        Put(GetContext(), cid, data)
    except:
        Notify(['cid_register', False])
        return False
    else:
        Notify(['cid_register', True])
        return True


def CidRecord(cid, data):
    try:
        assert _isOwner()
        assert _isCidRegisterd(cid)
        data_key = 'record:' + cid
        context = GetContext()
        record = Deserialize(Get(context, data_key))
        if not record:
            record = []
        record.append(data)

        Put(context, data_key, Serialize(record))
    except:
        Notify(['cid_record', False])
        return False
    else:
        Notify(['cid_record', True])
        return True


############################################################

def CreditRegister(cid, org_id, data):
    try:
        assert _isOwner()
        assert _isCidRegisterd(cid)
        assert _isOrgidRegisterd(org_id)

        data_key = 'credit:' + cid
        context = GetContext()
        map = Deserialize(Get(context, data_key))
        if not map: map = {}
        map[org_id] = data

        Put(context, data_key, Serialize(map))
    except:
        Notify(['credit_register', False])
        return False
    else:
        Notify(['credit_register', True])
        return True


def CreditDestroy(cid, org_id):
    try:
        assert _isOwner()
        assert _isCidRegisterd(cid)
        assert _isOrgidRegisterd(org_id)

        data_key = 'credit:' + cid
        context = GetContext()
        map = Deserialize(Get(context, data_key))
        if not map: map = {}
        map.pop(org_id)

        Put(context, data_key, Serialize(map))
    except:
        Notify(['credit_destroy', False])
        return False
    else:
        Notify(['credit_destroy', True])
        return True


############################################################

# TODO: 这个 Notify 有数据。这个接口在链上仅取出数据，然后在服务端处理真正要返回的数据。
#
def CreditUse(cid, org_id):
    map = {}
    try:
        assert _isOwner()
        assert _isCidRegisterd(cid)
        assert _isOrgidRegisterd(org_id)

        data_key = 'credit:' + cid
        context = GetContext()
        map = Deserialize(Get(context, data_key))
        if not map: map = {}
    except:
        Notify(['credit_use', False])
        return False
    else:
        Notify(['credit_use', True, map[org_id]])
        return True


#################################################################################

def init(account):
    if CheckWitness(account):
        _loadOwner(False)
        assert not _isOwnerLoaded()
        _storeOwner(account)
        _loadOwner()
    # 继续往下执行
    else:
        Notify(['init', False])
        return False

    # 但是这样有个问题：是不是调用 native 合约的 initContractAdmin 之后，本合约中的所有函数都不能被除 adminOntID 之外的账户调用。
    # param = state(adminOntID)
    # governanceContractAddress = bytearray(b'\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x07')
    # res = Invoke(0, governanceContractAddress, 'initContractAdmin', [param])
    # assert res and res == b'\x01'

    try:
        # init list
        # list1 = [1,2,3]
        # list1Info = Serialize(list1)
        # Put(GetContext(), LISTKEY, list1Info)

        # init map
        cid_map = {
            'test_key': 'cid_string'
        }
        org_map = {
            'test_key': 'org_id_string'
        }
        context = GetContext()
        # Put(context, MAP_CID_GLOBAL_KEY, Serialize(cid_map))
        # Put(context, MAP_ORG_GLOBAL_KEY, Serialize(org_map))
    except:
        Notify('init', False)
        return False
    else:
        Notify('init', True)
        return True


def _isCidRegisterd(cid):
    if Get(GetContext(), cid):
        return True
    else:
        return False


def _isOrgidRegisterd(org_id):
    if Get(GetContext(), org_id):
        return True
    else:
        return False


def _isOwner():
    if not _isOwnerLoaded():
        _loadOwner()
    return CheckWitness(_owner)


def _loadOwner(check=True):
    _owner = Get(GetContext(), _OWNER_KEY)
    if check:
        assert _isOwnerLoaded()


def _storeOwner(account):
    Put(GetContext(), _OWNER_KEY, account)


def _isOwnerLoaded():
    return len(_owner) > 10


def _toBytes(str):
    return bytearray(str)
