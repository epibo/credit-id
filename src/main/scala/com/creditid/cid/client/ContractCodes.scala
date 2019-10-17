package com.creditid.cid.client

/**
 * @author Wei.Chou
 * @version 1.0, 16/10/2019
 */
object ContractCodes {
  val ABI_JSON =
    """{"contractHash":"b1a8f45ecd39f66e75b1e01489d03f0963f49416",
      |"abi":{
      |   "CompilerVersion":"2.0.0",
      |   "hash":"b1a8f45ecd39f66e75b1e01489d03f0963f49416",
      |   "entrypoint":"Main",
      |   "functions":[
      |       {"name":"OrgRegister","parameters":[{"name":"org_id","type":""},{"name":"pubkeys","type":""}]},
      |       {"name":"OrgUpdPubkey","parameters":[{"name":"org_id","type":""},{"name":"pubkey","type":""}]},
      |       {"name":"OrgGetPubkeys","parameters":[{"name":"org_id","type":""}]},
      |       {"name":"CidRegister","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
      |       {"name":"CidRecord","parameters":[{"name":"cid","type":""},{"name":"data","type":""}]},
      |       {"name":"CreditRegister","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""},{"name":"data","type":""}]},
      |       {"name":"CreditDestroy","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
      |       {"name":"CreditUse","parameters":[{"name":"cid","type":""},{"name":"org_id","type":""}]},
      |       {"name":"Init","parameters":[{"name":"account","type":""}
      | ]}]}}""".stripMargin

  val VM_CODE = "54c56b05322e302e306a00527ac40a5f6f776e65725f6b65796a51527ac4010051c176c96a52527ac46c0121c56b6a00527ac46a51527ac46a52527ac46a51c304496e69747d9c7c75644800006a53527ac46a52c300c3516a53c3936a53527ac46a53c36a00c365af116a54527ac4006a53527ac46a54c3516a53c3936a53527ac46a53c36a00c365990e6c75666203006a51c30b4f726752656769737465727d9c7c756446006a52c300c36a55527ac46a52c351c36a56527ac4006a53527ac46a56c3516a53c3936a53527ac46a55c3516a53c3936a53527ac46a53c36a00c36565036c75666203006a51c30c4f72675570645075626b65797d9c7c756446006a52c300c36a55527ac46a52c351c36a57527ac4006a53527ac46a57c3516a53c3936a53527ac46a55c3516a53c3936a53527ac46a53c36a00c36568046c75666203006a51c30d4f72674765745075626b6579737d9c7c75642f006a52c300c36a55527ac4006a53527ac46a55c3516a53c3936a53527ac46a53c36a00c365bd056c75666203006a51c30b43696452656769737465727d9c7c756446006a52c300c36a58527ac46a52c351c36a59527ac4006a53527ac46a59c3516a53c3936a53527ac46a58c3516a53c3936a53527ac46a53c36a00c365af066c75666203006a51c3094369645265636f72647d9c7c756446006a52c300c36a58527ac46a52c351c36a59527ac4006a53527ac46a59c3516a53c3936a53527ac46a58c3516a53c3936a53527ac46a53c36a00c36536076c75666203006a51c30e43726564697452656769737465727d9c7c75645d006a52c300c36a58527ac46a52c351c36a55527ac46a52c352c36a59527ac4006a53527ac46a59c3516a53c3936a53527ac46a55c3516a53c3936a53527ac46a58c3516a53c3936a53527ac46a53c36a00c36524086c75666203006a51c30d43726564697444657374726f797d9c7c756446006a52c300c36a58527ac46a52c351c36a55527ac4006a53527ac46a55c3516a53c3936a53527ac46a58c3516a53c3936a53527ac46a53c36a00c36557096c75666203006a51c3094372656469745573657d9c7c756446006a52c300c36a58527ac46a52c351c36a55527ac4006a53527ac46a55c3516a53c3936a53527ac46a58c3516a53c3936a53527ac46a53c36a00c365830a6c7566620300006c75660111c56b6a00527ac46a51527ac46a51c300947600a0640c00c16a52527ac4620e007562030000c56a52527ac46a52c3c0517d9c7c75641c00006a53527ac46a52c300c36a54527ac4516a55527ac4625c006a52c3c0527d9c7c756421006a52c300c36a53527ac46a52c351c36a54527ac4516a55527ac4616232006a52c3c0537d9c7c756424006a52c300c36a53527ac46a52c351c36a54527ac46a52c352c36a55527ac462050000f100c176c96a56527ac46a53c36a57527ac46a57c36a54c37d9f7c756419006a56c36a57c3c86a57c36a55c3936a57527ac462e0ff6a56c36c75660111c56b6a00527ac46a51527ac46a52527ac46a53527ac46203006a53c3f1006a54527ac46a54c36a00c365630cf1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c365ee0b91f1c76a55527ac4006a56527ac4006a57527ac46a53c36a58527ac46a58c3c06a59527ac46a57c36a59c39f642d006a58c36a57c3c36a5a527ac46a57c351936a57527ac46a5ac36a56527ac4006a55c36a5ac37bc462cfff516a55c36a56c37bc46a55c3681853797374656d2e52756e74696d652e53657269616c697a656a52c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e5075740b4f726752656769737465726a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660b4f726752656769737465726a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75660111c56b6a00527ac46a51527ac46a52527ac46a53527ac46203006a53c3f1006a54527ac46a54c36a00c365060bf1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c365910af1681953797374656d2e53746f726167652e476574436f6e746578746a55527ac46a52c36a55c3681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a56527ac46a56c3f1c76a57527ac4006a58527ac46a56c36a59527ac46a59c3c06a5a527ac46a58c36a5ac39f6427006a59c36a58c3c36a5b527ac46a58c351936a58527ac4006a57c36a5bc300c37bc462d5ff516a57c36a53c37bc46a57c3681853797374656d2e52756e74696d652e53657269616c697a656a52c36a55c3681253797374656d2e53746f726167652e5075740c4f72675570645075626b65796a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660c4f72675570645075626b65796a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75665dc56b6a00527ac46a51527ac46a52527ac462030000c176c96a53527ac4006a54527ac46a54c36a00c3656e09f1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c365f908f16a52c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a55527ac46a55c3f1006a56527ac46a55c36a57527ac46a57c3c06a58527ac46a56c36a58c39f6423006a57c36a56c3c36a59527ac46a56c351936a56527ac46a53c36a59c3c862d9ff0d4f72674765745075626b6579736a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660d4f72674765745075626b6579736a52c3516a53c354c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c756658c56b6a00527ac46a51527ac46a52527ac46a53527ac4620300006a54527ac46a54c36a00c3652708f1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c3655a0791f16a53c36a52c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e5075740b43696452656769737465726a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660b43696452656769737465726a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75665cc56b6a00527ac46a51527ac46a52527ac46a53527ac4620300006a54527ac46a54c36a00c3654907f1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c3657c06f1077265636f72643a6a52c3936a55527ac4681953797374656d2e53746f726167652e476574436f6e746578746a56527ac46a55c36a56c3681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a57527ac46a57c391640f0000c176c96a57527ac46203006a57c36a53c3c86a57c3681853797374656d2e52756e74696d652e53657269616c697a656a55c36a56c3681253797374656d2e53746f726167652e507574094369645265636f72646a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c7566094369645265636f72646a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75665ec56b6a00527ac46a51527ac46a52527ac46a53527ac46a54527ac4620300006a55527ac46a55c36a00c365e305f1006a55527ac46a52c3516a55c3936a55527ac46a55c36a00c3651605f1006a55527ac46a53c3516a55c3936a55527ac46a55c36a00c3655105f1076372656469743a6a52c3936a56527ac4681953797374656d2e53746f726167652e476574436f6e746578746a57527ac46a56c36a57c3681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a58527ac46a58c391640c00c76a58527ac46203006a54c36a58c36a53c37bc46a58c3681853797374656d2e52756e74696d652e53657269616c697a656a56c36a57c3681253797374656d2e53746f726167652e5075740e43726564697452656769737465726a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660e43726564697452656769737465726a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75665cc56b6a00527ac46a51527ac46a52527ac46a53527ac4620300006a54527ac46a54c36a00c3655a04f1006a54527ac46a52c3516a54c3936a54527ac46a54c36a00c3658d03f1006a54527ac46a53c3516a54c3936a54527ac46a54c36a00c365c803f1076372656469743a6a52c3936a55527ac4681953797374656d2e53746f726167652e476574436f6e746578746a56527ac46a55c36a56c3681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a57527ac46a57c391640c00c76a57527ac46203006a57c36a53c3ca6a57c3681853797374656d2e52756e74696d652e53657269616c697a656a55c36a56c3681253797374656d2e53746f726167652e5075740d43726564697444657374726f796a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c75660d43726564697444657374726f796a52c35153c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c75665dc56b6a00527ac46a51527ac46a52527ac46a53527ac4620300c76a54527ac4006a55527ac46a55c36a00c365d102f1006a55527ac46a52c3516a55c3936a55527ac46a55c36a00c3650402f1006a55527ac46a53c3516a55c3936a55527ac46a55c36a00c3653f02f1076372656469743a6a52c3936a56527ac4681953797374656d2e53746f726167652e476574436f6e746578746a57527ac46a56c36a57c3681253797374656d2e53746f726167652e476574681a53797374656d2e52756e74696d652e446573657269616c697a656a54527ac46a54c391640c00c76a54527ac4620300094372656469745573656a52c30053c176c9681553797374656d2e52756e74696d652e4e6f74696679006c7566094372656469745573656a52c3516a54c36a53c3c354c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c756658c56b6a00527ac46a51527ac46a52527ac46203006a52c3681b53797374656d2e52756e74696d652e436865636b5769746e657373645c00006a53527ac400516a53c3936a53527ac46a53c36a00c365be01006a53527ac46a53c36a00c365790291f1006a53527ac46a52c3516a53c3936a53527ac46a53c36a00c3650c02006a53527ac46a53c36a00c365820162280004496e69740052c176c9681553797374656d2e52756e74696d652e4e6f74696679006c756604496e69740052c176c9681553797374656d2e52756e74696d652e4e6f74696679006c756604496e69745152c176c9681553797374656d2e52756e74696d652e4e6f74696679516c75666c756657c56b6a00527ac46a51527ac46a52527ac46203006a52c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e476574640a00516c7566620700006c75666c756657c56b6a00527ac46a51527ac46a52527ac46203006a52c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e476574640a00516c7566620700006c75666c756655c56b6a00527ac46a51527ac4620300006a52527ac46a52c36a00c365080191641500006a52527ac46a52c36a00c3652b006203006a00c352c3681b53797374656d2e52756e74696d652e436865636b5769746e6573736c756656c56b6a00527ac46a51527ac46a51c3009c640600620b006a52527ac4620900516a52527ac46a00c351c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e4765746a53527ac46a52c3641600006a54527ac46a54c36a00c3655900f16203006c756655c56b6a00527ac46a51527ac46a52527ac46203006a52c36a00c351c3681953797374656d2e53746f726167652e476574436f6e74657874681253797374656d2e53746f726167652e5075746c756655c56b6a00527ac46a51527ac46203006a00c352c3c05a7da07c756c756656c56b6a00527ac46a51527ac46a52527ac46203006a52c36c7566"
}
