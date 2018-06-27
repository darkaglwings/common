package org.frame.common.protocol.snmp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;


public class SNMP {
	
	public static int SYNC = 0;
	
	public static int ASYN = 1;
	
	private final String protocol = "udp";

	private String ip;
	
	private String community;
	
	private int port = 161;
	
	private int interval = 500;
	
	private int retry = 3;
	
	private long timeout = 3 * 1000L;
	
	private final int version = SnmpConstants.version2c;
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public SNMP(String ip, String community) {
		this.ip = ip;
		this.community = community;
	}
	
	public SNMP(String ip, int port, String community) {
		this.ip = ip;
		this.port = port;
		this.community = community;
	}
	
	public String get(String oid) {
		String result = null;
		
		CommunityTarget target = create();
		Snmp snmp = null;
		
		try {
			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();
			
			snmp = new Snmp(transport);
			
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GET);
			
			ResponseEvent responseEvent = snmp.send(pdu, target);
			PDU response = responseEvent.getResponse();
			if (response == null) {
				System.err.println("can not get snmp value, response is null, oid: " + oid);
			} else {
				for (int i = 0; i < response.size(); i++) {
					VariableBinding vb = response.get(i);
					result = vb.getVariable().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	@Deprecated
	public String get(String oid, int type) {
		if (type == SNMP.SYNC) {
			return this.get(oid);
		} else if (type == SNMP.ASYN) {
			Map<String, String> result = new HashMap<String, String>();
			
			CommunityTarget target = create();
			Snmp snmp = null;
			try {
				DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport);
				snmp.listen();

				PDU pdu = new PDU();
				pdu.add(new VariableBinding(new OID(oid)));

				final CountDownLatch latch = new CountDownLatch(1);
				ResponseListener listener = new ResponseListener() {
					public void onResponse(ResponseEvent event) {
						((Snmp) event.getSource()).cancel(event.getRequest(), this);
						PDU response = event.getResponse();
						if (response == null) {
							System.err.println("can not get snmp value, response is null, oid: " + oid);
						} else if (response.getErrorStatus() != 0) {
							System.err.println("response error, status: " + response.getErrorStatus() + " message: " + response.getErrorStatusText());
						} else {
							for (int i = 0; i < response.size(); i++) {
								VariableBinding vb = response.get(i);
								result.put(oid, vb.getVariable().toString());
							}
							latch.countDown();
						}
					}
				};

				pdu.setType(PDU.GET);
				snmp.send(pdu, target, null, listener);

				latch.await(this.interval, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (snmp != null) {
					try {
						snmp.close();
					} catch (IOException e) {
						snmp = null;
						e.printStackTrace();
					}
				}
			}
			
			return result.get(oid);
		} else return null;
	}
	
	public String trap(String oid) {
		String result = null;
		
		final CommunityTarget target = create();
		Snmp snmp = null;
		
		try {
			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();
			
			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.TRAP);
			
			ResponseEvent responseEvent = snmp.send(pdu, target);
			PDU response = responseEvent.getResponse();
			if (response == null) {
				System.err.println("can not get snmp value, response is null, oid: " + oid);
			} else {
				for (int i = 0; i < response.size(); i++) {
					VariableBinding vb = response.get(i);
					result = vb.getVariable().toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					snmp = null;
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	
	public String walk(String oid) {
		String result = null;
		
		CommunityTarget target = create();
		Snmp snmp = null;
		
		try {
			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();

			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));

			boolean finished = false;
			
			VariableBinding vb = null;
			
			while (!finished) {
				ResponseEvent respEvent = snmp.get(pdu, target);

				PDU response = respEvent.getResponse();

				if (response == null) {
					System.err.println("can not walk snmp value, response is null, oid: " + oid);
					finished = true;
					break;
				} else {
					for (int i = 0; i < response.size(); i++) {
						vb = response.get(i);
						result = vb.getVariable().toString();

						// check finish
						finished = checkWalkFinished(vb.getOid(), pdu, vb);
						if (finished) {
							// Set up the variable binding for the next entry.
							pdu.setRequestID(new Integer32(0));
							pdu.set(0, vb);
						} else {
							//System.out.println("SNMP walk OID has finished.");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}
		}
		
		return result;
	}
	
	public Map<String, String> walkNext(String oid) {
		Map<String, String> result = new HashMap<String, String>();
		
		CommunityTarget target = create();
		Snmp snmp = null;
		
		try {
			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			transport.listen();

			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));

			boolean finished = false;
			
			VariableBinding vb = null;
			
			while (!finished) {
				ResponseEvent respEvent = snmp.getNext(pdu, target);

				PDU response = respEvent.getResponse();

				if (response == null) {
					System.err.println("can not walk snmp value, response is null, oid: " + oid);
					finished = true;
					break;
				} else {
					for (int i = 0; i < response.size(); i++) {
						vb = response.get(i);
						result.put(vb.getOid().toString(), vb.getVariable().toString());

						// check finish
						finished = checkWalkFinished(vb.getOid(), pdu, vb);
						if (finished) {
							// Set up the variable binding for the next entry.
							pdu.setRequestID(new Integer32(0));
							pdu.set(0, vb);
						} else {
							//System.out.println("SNMP walk OID has finished.");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException ex1) {
					snmp = null;
				}
			}
		}
		
		return result;
	}
	
	@Deprecated
	public String walk(String oid, int type) {
		if (type == SNMP.SYNC) {
			return this.walk(oid);
		} else if (type == SNMP.ASYN) {
			Map<String, String> result = new HashMap<String, String>();

			final CommunityTarget target = create();
			Snmp snmp = null;
			try {
				DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport);
				snmp.listen();

				PDU pdu = new PDU();
				pdu.add(new VariableBinding(new OID(oid)));

				final CountDownLatch latch = new CountDownLatch(1);

				ResponseListener listener = new ResponseListener() {
					public void onResponse(ResponseEvent event) {
						((Snmp) event.getSource()).cancel(event.getRequest(), this);

						try {
							PDU response = event.getResponse();
							if (response == null) {
								System.err.println("can not walk snmp value, response is null, oid: " + oid);
							} else if (response.getErrorStatus() != 0) {
								System.err.println("response error, status: " + response.getErrorStatus() + " message: " + response.getErrorStatusText());
							} else {
								for (int i = 0; i < response.size(); i++) {
									VariableBinding vb = response.get(i);
									result.put(vb.getOid().toString(), vb.getVariable().toString());

									boolean finished = checkWalkFinished(vb.getOid(), pdu, vb);
									if (!finished) {
										pdu.setRequestID(new Integer32(0));
										pdu.set(0, vb);
										((Snmp) event.getSource()).get(pdu, target, null, this);
									} else {
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

				snmp.get(pdu, target, null, listener);

				latch.await(interval, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (snmp != null) {
					try {
						snmp.close();
					} catch (IOException e) {
						snmp = null;
						e.printStackTrace();
					}
				}
			}

			return result.get(oid);
		} else return null;
	}
	
	@Deprecated
	public Map<String, String> walkNext(String oid, int type) {
		if (type == SNMP.SYNC) {
			return this.walkNext(oid);
		} else if (type == SNMP.ASYN) {
			Map<String, String> result = new HashMap<String, String>();

			final CommunityTarget target = create();
			Snmp snmp = null;
			try {
				DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
				snmp = new Snmp(transport);
				snmp.listen();

				PDU pdu = new PDU();
				pdu.add(new VariableBinding(new OID(oid)));

				final CountDownLatch latch = new CountDownLatch(1);

				ResponseListener listener = new ResponseListener() {
					public void onResponse(ResponseEvent event) {
						((Snmp) event.getSource()).cancel(event.getRequest(), this);

						try {
							PDU response = event.getResponse();
							if (response == null) {
								System.err.println("can not walk snmp value, response is null, oid: " + oid);
							} else if (response.getErrorStatus() != 0) {
								System.err.println("response error, status: " + response.getErrorStatus() + " message: " + response.getErrorStatusText());
							} else {
								for (int i = 0; i < response.size(); i++) {
									VariableBinding vb = response.get(i);
									result.put(vb.getOid().toString(), vb.getVariable().toString());

									boolean finished = checkWalkFinished(vb.getOid(), pdu, vb);
									if (!finished) {
										pdu.setRequestID(new Integer32(0));
										pdu.set(0, vb);
										((Snmp) event.getSource()).getNext(pdu, target, null, this);
									} else {
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

				latch.await(interval, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (snmp != null) {
					try {
						snmp.close();
					} catch (IOException e) {
						snmp = null;
						e.printStackTrace();
					}
				}
			}

			return result;
		} else return null;
	}
	
	private CommunityTarget create() {
		Address address = GenericAddress.parse(this.protocol + ":" + this.ip + "/" + this.port);

		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(this.community));
		target.setAddress(address);
		target.setVersion(this.version);
		target.setTimeout(this.timeout);// milliseconds
		target.setRetries(this.retry);

		return target;
	}
	
	private static boolean checkWalkFinished(OID walkOID, PDU pdu, VariableBinding vb) {
		boolean finished = false;
		
		if (pdu.getErrorStatus() != 0) {
			finished = true;
		} else if (vb.getOid() == null) {
			finished = true;
		} else if (vb.getOid().size() < walkOID.size()) {
			finished = true;
		} else if (walkOID.leftMostCompare(walkOID.size(), vb.getOid()) != 0) {
			finished = true;
		} else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
			finished = true;
		} else if (vb.getOid().compareTo(walkOID) <= 0) {
			finished = true;
		}
		
		return finished;
	}
	
	public class TrapReceiver implements CommandResponder {
		  
	    private MultiThreadedMessageDispatcher dispatcher;
	    private Snmp snmp = null;
	    private Address listenAddress;
	    private ThreadPool threadPool;
	  
	    public TrapReceiver() {
	        // BasicConfigurator.configure();
	    }
	  
	    private void init() throws UnknownHostException, IOException {
	        threadPool = ThreadPool.create("Trap", 2);
	        dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
	        listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:localhost/162"));//本地IP与监听端口
	        TransportMapping transport;
	        // 对TCP与UDP协议进行处理
	        if (listenAddress instanceof UdpAddress) {
	            transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
	        } else {
	            transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
	        }
	        snmp = new Snmp(dispatcher, transport);
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
	        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
	        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
	        SecurityModels.getInstance().addSecurityModel(usm);
	        snmp.listen();
	    }
	      
	    public void run() {
	        try {
	            init();
	            snmp.addCommandResponder(this);
	            System.out.println("开始监听Trap信息!");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	  
	    /** 
	     * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息
	     * 当接收到trap时，会自动进入这个方法
	     *  
	     * @param respEvnt
	     */  
	    @SuppressWarnings("unchecked")
		public void processPdu(CommandResponderEvent respEvnt) {
	        // 解析Response
	        if (respEvnt != null && respEvnt.getPDU() != null) {
	            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getPDU().getVariableBindings();
	            for (int i = 0; i < recVBs.size(); i++) {
	                VariableBinding recVB = recVBs.elementAt(i);
	                System.out.println(recVB.getOid() + " : " + recVB.getVariable());
	            }
	        }
	    }
	  
	    /*public static void main(String[] args) {
	        MultiThreadedTrapReceiver multithreadedtrapreceiver = new MultiThreadedTrapReceiver();
	        multithreadedtrapreceiver.run();
	    }*/
	  
	} 
	
	public static void main(String[] args) {

		String ip = "10.10.10.125";

		String community = "public";

		//String oid = ".1.3.6.1.4.1.2021.11.9.0";
		
		List<String> oid = new ArrayList<String>();
		oid.add(".1.3.6.1.2.1.1.1");//sysDescr walk
		oid.add(".1.3.6.1.2.1.1.2");//sysObjectID walk
		oid.add(".1.3.6.1.2.1.1.3");//sysUpTime walk
		oid.add(".1.3.6.1.2.1.1.4");//sysContact walk
		oid.add(".1.3.6.1.2.1.1.5");//sysName walk
		oid.add(".1.3.6.1.2.1.1.6");//sysLocation walk
		oid.add(".1.3.6.1.2.1.1.7");//sysServices walk
		
		oid.add(".1.3.6.1.2.1.2.1");//ifNumber walk

		/*oid.add(".1.3.6.1.2.1.25.2.3.1.4.1");//磁盘1 c盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.2");//磁盘2 d盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.3");//磁盘3 e盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.5");//虚拟内存       族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.6");//物理内存       族大小 walk
*/		
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.0");//磁盘1 c盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.1");//磁盘2 d盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.2");//磁盘3 e盘 族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.4");//虚拟内存       族大小 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.4.5");//物理内存       族大小 walk
		
		/*oid.add(".1.3.6.1.2.1.25.2.3.1.5.1");//磁盘1 c盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.2");//磁盘2 d盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.3");//磁盘3 e盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.5");//虚拟内存       族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.6");//物理内存       族数目 walk
*/		
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.0");//磁盘1 c盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.1");//磁盘2 d盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.2");//磁盘3 e盘 族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.4");//虚拟内存       族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.5.5");//物理内存       族数目 walk
		
		//族大小 * 族数目 = 磁盘 或 内存 大小
		
		/*oid.add(".1.3.6.1.2.1.25.2.3.1.6.1");//磁盘1 c盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.2");//磁盘2 d盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.3");//磁盘3 e盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.5");//虚拟内存       已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.6");//物理内存       已使用族数目 walk
*/		
		
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.0");//磁盘1 c盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.1");//磁盘2 d盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.2");//磁盘3 e盘 已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.4");//虚拟内存       已使用族数目 walk
		oid.add(".1.3.6.1.2.1.25.2.3.1.6.5");//物理内存       已使用族数目 walk
		
		//已使用 / 总大小 = 使用率
		
		//cpu负载 4核8线程 共8个oid 求平均即为cpu负载 walk
		/*oid.add(".1.3.6.1.2.1.25.3.3.1.2.5");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.6");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.7");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.8");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.9");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.10");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.11");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.12");*/
		
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.4");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.5");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.6");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.7");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.8");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.9");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.10");
		oid.add(".1.3.6.1.2.1.25.3.3.1.2.11");
		
		
		/*//网卡速率
		oid.add(".1.3.6.1.2.1.2.2.1.10.14");//当前收到的字节数,是总数,和尚一秒获得数相减就是新增数
		oid.add(".1.3.6.1.2.1.2.2.1.16.14");//当前发送的字节数,是总数,和尚一秒获得数相减就是新增数
		oid.add(".1.3.6.1.2.1.2.2.1.5.14");//网卡带宽
		//两个相除得占用率
*/		
		
		//网卡速率
		oid.add(".1.3.6.1.2.1.2.2.1.10.13");//当前收到的字节数,是总数,和尚一秒获得数相减就是新增数
		oid.add(".1.3.6.1.2.1.2.2.1.16.13");//当前发送的字节数,是总数,和尚一秒获得数相减就是新增数
		oid.add(".1.3.6.1.2.1.2.2.1.5.13");//网卡带宽
		//两个相除得占用率
		
		for (String o : oid) {
			System.out.println("get: " + new SNMP(ip, community).get(o));
			System.out.println("walk: " + new SNMP(ip, community).walk(o));
		}
		
		//new SNMP(ip, community).trap(oid);

		/*	SnmpWalk walk = new SnmpWalk("192.168.100.10","1.3.6.1.2.1.1.5.0");//物理位置
		//  SnmpWalk walk = new SnmpWalk("127.0.0.1",".1.3.6.1.2.1.25.2.2");//RAM
		//  SnmpWalk walk = new SnmpWalk("127.0.0.1","1.3.6.1.2.1.25.2.3.1.6");//Hard Disk
		//  SnmpWalk walk = new SnmpWalk("127.0.0.1",".1.3.6.1.2.1.25.5.1.1.1");//CPU Utilization

		//  SnmpWalk walk = new SnmpWalk("127.0.0.1","1.3.6.1.2.1.25.1");//也含本机物理总内存
		 */
	}
	
}
