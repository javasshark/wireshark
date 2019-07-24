package Model;

import java.util.Date;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.tcpip.Http;
public class Packet {


	public static void main(String[] args) {
		
		//계층별 객체 생성
		Icmp icmp = new Icmp();
		Icmp.DestinationUnreachable unreach = new Icmp.DestinationUnreachable();
		Ethernet eth = new Ethernet();
		Ip4 ip = new Ip4();
		Tcp tcp = new Tcp();
		Udp udp = new Udp();
		Payload payload = new Payload();
		PcapHeader header = new PcapHeader(JMemory.POINTER);
		JBuffer buf = new JBuffer(JMemory.POINTER);
		Arp arp = new Arp();
		Http http = new Http();
		Pcap pcap=Device.getPcap();
		int id = JRegistry.mapDLTToId(pcap.datalink()); //pcap의 datalink 유형을 jNetPcap의 프로토콜 id에 맵핑
		
		while(pcap.nextEx(header, buf) == Pcap.NEXT_EX_OK) { //헤더와 버퍼를 피어링
			PcapPacket packet = new PcapPacket(header, buf);
			
			packet.scan(id); //새로운 패킷을 스캔하여 포함된 헤더를 찾는다
			
			System.out.printf("[ #%d ]\n", packet.getFrameNumber());
			System.out.println("#############packet#############");
			if(packet.hasHeader(icmp) && icmp.hasSubHeader(unreach)) {
     	    	   System.out.printf("type=%d, code=%d, crc=0x%x reserved=%d\n", 
     	    	       icmp.type(), icmp.code(), icmp.checksum(), unreach.reserved()); 
				System.out.println("icmp");
				System.out.printf("출발지 MAC 주소 = %s\n도착지 MAC 주소 = %s\n" ,macSource(packet, eth), macDestination(packet, eth));
			}
			if (packet.hasHeader(eth)) {
				System.out.printf("출발지 MAC 주소 = %s\n도착지 MAC 주소 = %s\n" ,macSource(packet, eth), macDestination(packet, eth));
			}
			if (packet.hasHeader(ip)) {
				System.out.printf("출발지 IP 주소 = %s\n도착지 IP 주소 = %s\n" ,ipSource(packet, ip) , ipDestination(packet, ip));
			}
			if (packet.hasHeader(arp)) {
				System.out.println("arp");
				//System.out.println("ARP protocol type:\t" + arp.protocolType());
			}
			if (packet.hasHeader(Tcp.ID)) {
				 if(packet.hasHeader(http)&&(tcp.destination() == 80)) {
		               if(http.hasField(Http.Request.Accept) && http.fieldValue(Http.Request.Accept).contains("text/html")) {
		            	   System.out.println("http"); 
		            	   String host = http.fieldValue(Http.Request.Host);
		            	   String url = host + http.fieldValue(Http.Request.RequestUrl);
		               }

				packet.getHeader(tcp);
				System.out.printf("tcp.ack=%x%n", tcp.ack());
				System.out.printf("출발지 TCP 포트 = %d\n도착지 TCP 포트 = %d\n" , tcpSource(packet, tcp), tcpDestination(packet, tcp));
				 }
			}
			if (packet.hasHeader(udp)) {
				if(udp.destination() == 5535) //LLMNR 
					System.out.println("LLMNR");
				else if(udp.destination()== 1900) //SSDP
					System.out.println("SSDP");
				System.out.printf("출발지 UDP 포트 = %d\n도착지 UDP 포트 = %d\n" , udpSource(packet, udp), udpDestination(packet, udp));
			}
			if (packet.hasHeader(payload)) {
				System.out.printf("페이로드의 길이 = %d\n", getlength(packet, payload));
				System.out.print(hexdump(packet, payload)); //hexdump 출력
			}
			
			System.out.printf("capture time: %s\ncapture length: %d\n", CaptureTime(packet), dataLength(packet) );			
		}
			
		pcap.close();
		}
	
	
	/*	icmp 주소가 ip주소랑 같..? 못찾음
	public static String icmpSource(PcapPacket packet, Icmp icmp) {
		
	}
	*/	
	public static Date CaptureTime(PcapPacket packet) {
		Date captureTime = new Date(packet.getCaptureHeader().timestampInMillis());
		return captureTime;
	}
	public static int dataLength(PcapPacket packet) {
		int dataLength = packet.getCaptureHeader().caplen();
		return dataLength;
	}
	public static String macSource(PcapPacket packet, Ethernet eth) {
			String source = FormatUtils.mac(eth.source());
			return source;
		}
	public static String macDestination(PcapPacket packet, Ethernet eth) {
		String destination = FormatUtils.mac(eth.destination());
		return destination;
	}
	
	public static String ipSource(PcapPacket packet, Ip4 ip) {
		String source = FormatUtils.ip(ip.source());
		return source;	
		}
	public static String ipDestination(PcapPacket packet, Ip4 ip) {
		String destination = FormatUtils.ip(ip.destination());
		return destination;
	}
	public static int tcpSource(PcapPacket packet, Tcp tcp) {
		return tcp.source();
	}
	
	public static int tcpDestination(PcapPacket packet, Tcp tcp) {
		return tcp.destination();
	}
	public static int udpSource(PcapPacket packet, Udp udp) {
		return udp.source();
	}
	
	public static int udpDestination(PcapPacket packet, Udp udp) {
		return udp.destination();
	}
	
	public static int getlength(PcapPacket packet, Payload payload) {
		return payload.getLength();
	}
	
	public static String hexdump(PcapPacket packet, Payload payload) {
		return payload.toHexdump();
	}
}
	


