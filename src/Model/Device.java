package Model;

import java.util.ArrayList;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class Device {
	
	public static void main(String[] args) {
		getPcap();
	}

	public static Pcap getPcap() {
		ArrayList<PcapIf> allDevs = new ArrayList<PcapIf>(); //디바이스를 담을 변수를 arraylist로 생성
		StringBuilder errbuf = new StringBuilder(); //에러 처리
		
		int r = Pcap.findAllDevs(allDevs, errbuf); //접근 가능한 디바이스를 allDevs에 담음. 2번째 인자는 에러처리
		
		if((r==Pcap.NOT_OK) || allDevs.isEmpty()) {
			System.out.println("네트워크 장치 찾기 실패" + errbuf.toString());
			return null;
		} //예외처리
		
		int i=0;
		for(PcapIf device : allDevs) { //탐색한 장비를 출력
			String description = (device.getDescription() != null ) ? device.getDescription() : "장비에 대한 설명이 없습니다.";
			if(description.equals("MS NDIS 6.0 LoopBack Driver")) {
				break;
			}
			else i++;
		}
		
		PcapIf device = allDevs.get(i);
		System.out.printf("선택된 장치: %s\n", (device.getDescription() != null) ? device.getDescription() : device.getName());
		
		int snaplen = 64*1024; //65536Byte만큼 패킷 캡쳐
		int flags = Pcap.MODE_NON_PROMISCUOUS; //무차별모드
		int timeout = 10*1000;	//타임아웃 10초
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		
		if (pcap == null) {
			System.out.printf("Network Device Access Failed. Error: "+ errbuf.toString());
			return null;
		}
		
		return pcap;
	}	
}
