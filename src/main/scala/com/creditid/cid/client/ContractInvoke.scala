package com.creditid.cid.client

import com.github.ontio.account.Account
import com.github.ontio.smartcontract.NeoVm
import com.github.ontio.smartcontract.neovm.abi.Parameter.Type
import com.github.ontio.smartcontract.neovm.abi.{AbiFunction, Parameter}
import scala.collection.convert.ImplicitConversionsToJava._

/**
  * @author Wei.Chou
  * @version 1.0, 17/10/2019
  */
object ContractInvoke {

  sealed trait ContractBuilding {

    def parameters: List[Parameter]
    private def name: String = this.getClass.getName
    def toTx(
        vm: NeoVm,
        contractAddress: String,
        account: Account,
        gasLimit: Long,
        gasPrice: Long
    ): AnyRef = {
      vm.sendTransaction(
        contractAddress,
        account,
        account,
        gasLimit,
        gasPrice,
        new AbiFunction(name, parameters: _*),
        false
      )
    }
  }

  final case class Init(contractAddress: String) extends ContractBuilding {
    override def parameters: List[Parameter] =
      List(new Parameter("account", Type.String, contractAddress))
  }

  final case class OrgRegister(org_id: String, pubkeys: List[String])
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("org_id", Type.String, org_id),
      new Parameter("pubkeys", Type.Array, pubkeys.toArray)
    )
  }

  final case class OrgUpdPubkey(org_id: String, pubkey: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("org_id", Type.String, org_id),
      new Parameter("pubkey", Type.String, pubkey)
    )
  }

  //{"name":"OrgGetPubkeys","parameters":[{"name":"org_id","type":""}]},
  final case class OrgGetPubkeys(org_id: String) extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("org_id", Type.String, org_id)
    )

  }

  //{"name":"CidRegister","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
  final case class CidRegister(cid: String, data: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("cid", Type.String, cid),
      new Parameter("data", Type.String, data)
    )

  }

  //{"name":"CidRecord","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
  final case class CidRecord(cid: String, data: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("cid", Type.String, cid),
      new Parameter("data", Type.String, data)
    )

  }

  //{"name":"CreditRegister","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""},{"name":"data","type":""}]},
  final case class CreditRegister(cid: String, org_id: String, data: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("cid", Type.String, cid),
      new Parameter("org_id", Type.String, org_id),
      new Parameter("data", Type.String, data)
    )
  }

  //{"name":"CreditDestroy","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
  final case class CreditDestroy(cid: String, org_id: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("cid", Type.String, cid),
      new Parameter("org_id", Type.String, org_id)
    )
  }

  //{"name":"CreditUse","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
  final case class CreditUse(cid: String, org_id: String)
      extends ContractBuilding {
    override def parameters: List[Parameter] = List(
      new Parameter("cid", Type.String, cid),
      new Parameter("org_id", Type.String, org_id)
    )
  }
}
