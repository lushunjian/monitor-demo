package lsj.snmp;

import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SnmpUtil {

    private static Logger logger = Logger.getLogger(SnmpUtil.class);

    private static Snmp snmp = null;

    private static TransportMapping transport = null;
    private static String community = "public";

    public static synchronized void initSnmp() throws IOException {
        // 设置Agent方传输协议并打开监听
        if(transport == null) {
            transport = new DefaultUdpTransportMapping();
            transport.listen();
        }
        if(snmp == null) {
            snmp = new Snmp(transport);
            snmp.listen();
        }
    }

    public static synchronized void closeSnmp() throws IOException {
        snmp.close();
    }



    public static Map<String, List<String>> getInfoByOidWalk(String ip, String port, String oid){
        if (validateParam(ip, port, oid)){
            logger.error("GetInfosByOid Param Error...");
            return null;
        }
        Map<String, List<String>> map = null;
        Target target = getTarget(ip + "/" + port);
        try{
            initSnmp();
            PDUFactory pf = new DefaultPDUFactory(PDU.GET);
            TableUtils tu = new TableUtils(snmp, pf);
            OID[] columns = new OID[1];
            columns[0] = new VariableBinding(new OID(oid)).getOid();
            List list = tu.getTable(target, columns, null, null);
            map = new LinkedHashMap<String, List<String>>();
            for (Object aList : list) {
                TableEvent te = (TableEvent) aList;
                VariableBinding[] vb = te.getColumns();
                if (vb != null) {
                    for (VariableBinding aVb : vb) {
                        OID toid = aVb.getOid();
                        String val = aVb.getVariable().toString();
                        String processId = getProcessId(toid);
                        List<String> dlist = getDataList(map, processId);
                        dlist.add(val);
                    }
                }
            }
        }
        catch (Exception e){
            logger.error("GetInfosByOid Fail...", e);
        }
        return map;
    }

    public static String getInfoByOidGet(String ip, String port, String oid){
        try {
            //1、初始化snmp,并开启监听
            initSnmp();
            //2、创建目标对象
            Target target = getTarget(ip + "/" + port);
            //3、创建报文
            PDU pdu = createPDU(oid);
            //4、发送报文，并获取返回结果
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if(response != null) {
                Vector vector = response.getVariableBindings();
                if (vector == null || vector.isEmpty()) {
                    return "";
                } else {
                    String str = vector.get(0).toString();
                    str = str.split("=")[1].trim();
                    return str;
                }
            }else {
                return "";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static PDU createPDU(String oid){
        PDU pdu = new PDUv1();
        pdu.setType(PDU.GET);
        //可以添加多个变量oid
        pdu.add(new VariableBinding(new OID(oid)));
        return pdu;
    }

    private static Target getTarget(String ip){
        Address targetAddress = GenericAddress.parse(ip);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(300L);
        target.setRetries(2);
        return target;
    }

    private static String getProcessId(OID oid){
        String t_oid = oid.toString().trim();
        int pos = t_oid.lastIndexOf(".");
        return t_oid.substring(pos + 1, t_oid.length());
    }

    private static List<String> getDataList(Map<String, List<String>> map, String processId){
        if (map.containsKey(processId)) {
            return map.get(processId);
        }
        LinkedList<String> dlist = new LinkedList<String>();
        map.put(processId, dlist);
        return dlist;
    }

    private static boolean validateParam(String ip, String port, String oid){
        return null == ip || "".equals(ip) || null == port || "".equals(port) || null == oid || "".equals(oid);
    }


    //异步walk方式
    public static void snmpAsynWalk(String ip, String oid) {
        final CommunityTarget target = createDefault(ip);
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            final PDU pdu = new PDU();
            final OID targetOID = new OID(oid);
            final CountDownLatch latch = new CountDownLatch(1);
            pdu.add(new VariableBinding(targetOID));

            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    try {
                        PDU response = event.getResponse();
                        if (response == null) {
                            System.out.println("[ERROR]: response is null");
                        } else if (response.getErrorStatus() != 0) {
                            System.out.println("[ERROR]: response status" + response.getErrorStatus() + " Text:" + response.getErrorStatusText());
                        } else {
                            VariableBinding vb = response.get(0);
                            boolean finished = checkWalkFinished(targetOID,pdu, vb);
                            if (!finished) {
                                System.out.println(vb.getOid() + " = " + vb.getVariable());
                                pdu.setRequestID(new Integer32(0));
                                pdu.set(0, vb);
                                ((Snmp) event.getSource()).getNext(pdu, target,null, this);
                            } else {
                                System.out.println("SNMP Asyn walk OID value success !");
                                latch.countDown();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        latch.countDown();
                    }

                }
            };
            snmp.getNext(pdu, target, null, listener);
            boolean wait = latch.await(30, TimeUnit.SECONDS);
            System.out.println("latch.await =:" + wait);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Asyn Walk Exception:" + e);
        }
    }


    /**
     * walk异步方式
     * list
     * */
    public static Map<String,String> snmpAsynWalk(String ip, final List<String> oidList) {
        final Map<String,String> map = new HashMap<String,String>();
        final CommunityTarget target = createDefault(ip);
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            final PDU pdu = new PDU();
            final List<OID> oidArray = new ArrayList<OID>();
            final CountDownLatch latch = new CountDownLatch(1);
            if(oidList != null && oidList.size()>0){
                for(String oid : oidList){
                    OID oidBean = new OID(oid);
                    oidArray.add(oidBean);
                    pdu.add(new VariableBinding(oidBean));
                }
            }else
                return map;
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    if(map.size()==oidArray.size())
                        return;
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    try {
                        PDU response = event.getResponse();
                        if (response == null) {
                            System.out.println("[ERROR]: response is null");
                        } else if (response.getErrorStatus() != 0) {
                            System.out.println("[ERROR]: response status" + response.getErrorStatus() + " Text:" + response.getErrorStatusText());
                        } else {
                            for(int i=0;i<oidArray.size();i++){
                                VariableBinding vb = response.get(i);
                                OID oidTemp = oidArray.get(i);
                                boolean finished = checkWalkFinished(oidArray.get(i),pdu, vb);
                                if (!finished) {
                                    map.put(oidTemp.toString(),vb.getVariable().toString());
                                    pdu.setRequestID(new Integer32(0));
                                    pdu.set(0, vb);
                                    ((Snmp) event.getSource()).getNext(pdu, target,null, this);
                                } else {
                                    System.out.println("SNMP Asyn walk OID value success !");
                                    latch.countDown();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        latch.countDown();
                    }

                }
            };
            snmp.getNext(pdu, target, null, listener);
            boolean wait = latch.await(5, TimeUnit.SECONDS);
            System.out.println("latch.await =:" + wait);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP Asyn Walk Exception:" + e);
        }
        return map;
    }

    private static boolean checkWalkFinished(OID walkOID, PDU pdu,
                                             VariableBinding vb) {
        boolean finished = false;
        if (pdu.getErrorStatus() != 0) {
            System.out.println("[true] pdu.getErrorStatus() != 0 ");
            System.out.println(pdu.getErrorStatusText());
            finished = true;
        } else if (vb.getOid() == null) {
            System.out.println("[true] vb.getOid() == null");
            finished = true;
        } else if (vb.getOid().size() < walkOID.size()) {
            System.out.println("[true] vb.getOid().size() < targetOID.size()");
            finished = true;
        } else if (walkOID.leftMostCompare(walkOID.size(), vb.getOid()) != 0) {
            System.out.println("[true] targetOID.leftMostCompare() != 0");
            finished = true;
        } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
            System.out
                    .println("[true] Null.isExceptionSyntax(vb.getVariable().getSyntax())");
            finished = true;
        } else if (vb.getOid().compareTo(walkOID) <= 0) {
            System.out.println("[true] vb.getOid().compareTo(walkOID) <= 0 ");
            finished = true;
        }
        return finished;

    }

    private static CommunityTarget createDefault(String ip) {
        Address address = GenericAddress.parse("udp:" + ip + "/161");
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(3 * 1000L); // milliseconds
        target.setRetries(3);
        return target;
    }

    private static CommunityTarget createTarget(String ip,String port) {
        Address address = GenericAddress.parse("udp:" + ip + "/"+port);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(3 * 1000L); // milliseconds
        target.setRetries(3);
        return target;
    }


    /**
     * get方式
     * list
     * */
    public static Map<String,String> snmpGetList(String ip,String port, List<String> oidList) {
        Map<String,String> map = new HashMap<String,String>();
        CommunityTarget target = SnmpUtil.createTarget(ip,port);
        try {
            PDU pdu = new PDU();
            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            PDU response = respEvent.getResponse();
            if (response == null) {
                System.out.println("response is null, request time out");
            } else {
                for (int i = 0; i<response.size(); i++) {
                    VariableBinding vb = response.get(i);
                    map.put(vb.getOid().toString(),vb.getVariable().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
