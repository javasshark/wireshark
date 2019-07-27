package View;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MainView extends JFrame implements Runnable {

	public JTextArea area_SpecificPackinfo,area_HexCodeinfo;
	public JTextField text_filter;
	public JPanel upper_pan, lower_pan, Main_pan, pan_fst, pan_btn, pan_filter, pan_packetTable, pan_PacketInfo,
			pan_PacketHexCode;
	public JButton btn_run;
	public JButton btn_stop;
	public JButton btn_search;
	public JLabel la_filter;
	public JMenuBar menubar;
	public JMenu file, edit, view, go;
	public JTable table;

	public JTable area_packinfo;
	public JScrollPane scrol_packinfo;
	/**
	 * Create the frame.
	 */

	// packet_table을 텍스트 필드에서 JTable로 바꿀까하는 실험하는 중
	String[] colName = {"No.","Time","Source","Destination","Protocol","Info"};
	String[][] val = {{"0","0","0","0","0","0"}};
	//

	Scanner in;

	public MainView() {

		// JFrame
		Main_pan = new JPanel(new GridLayout(3, 1));
		// 하나의 Main 패널 안에 upper_pan, packet_table, lower_pan이 담김
		upper_pan = new JPanel(new GridLayout(3, 1));
		lower_pan = new JPanel(new GridLayout(2, 1));

		setResizable(false); // frame 크기 조절
		setTitle("WireShark"); // frame title
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 버튼
		setBounds(300, 100, 800, 600); // wireshark 사이즈

		// 1.메뉴바
		menubar = new JMenuBar();

		// file 메뉴와 아이템 객체들
		file = new JMenu("file");
		JMenuItem open = new JMenuItem("open");
		JMenuItem close = new JMenuItem("close");
		JMenuItem save = new JMenuItem("save");
		file.add(open);
		file.addSeparator();
		file.add(close);
		file.addSeparator();
		file.add(save);
		open.addActionListener(new MenuActionListener());
		open.addActionListener(new OpenActionListener());
		close.addActionListener(new MenuActionListener());
		save.addActionListener(new MenuActionListener());

		// edit 메뉴와 아이템 객체들
		edit = new JMenu("edit");
		JMenuItem FindPacket = new JMenuItem("FindPacket");
		JMenuItem MarkUnMark = new JMenuItem("Mark / UnMark");
		JMenuItem Ignore = new JMenuItem("Ignore");
		edit.add(FindPacket);
		edit.add(MarkUnMark);
		edit.add(Ignore);
		FindPacket.addActionListener(new MenuActionListener());
		MarkUnMark.addActionListener(new MenuActionListener());
		Ignore.addActionListener(new MenuActionListener());

		// view 메뉴와 아이템 객체들
		view = new JMenu("view");
		JMenuItem ShowPacketinNewWindow = new JMenuItem("Show Packet in New Window");
		view.add(ShowPacketinNewWindow);
		ShowPacketinNewWindow.addActionListener(new MenuActionListener());

		// go 메뉴와 아이템 객체들
		go = new JMenu("go");
		JMenuItem Gotopacket = new JMenuItem("Go to packet");
		JMenuItem Previouspacket = new JMenuItem("Previous packet");
		JMenuItem Nextpacket = new JMenuItem("Next packet");
		JMenuItem Firstpacket = new JMenuItem("First packet");
		JMenuItem Lastpacket = new JMenuItem("Last packet");
		go.add(Gotopacket);
		go.add(Previouspacket);
		go.add(Nextpacket);
		go.add(Firstpacket);
		go.add(Lastpacket);
		Gotopacket.addActionListener(new MenuActionListener());
		Previouspacket.addActionListener(new MenuActionListener());
		Nextpacket.addActionListener(new MenuActionListener());
		Firstpacket.addActionListener(new MenuActionListener());
		Lastpacket.addActionListener(new MenuActionListener());

		// 메뉴바에 위의 메뉴들을 전부 붙임.
		menubar.add(file);
		menubar.add(edit);
		menubar.add(view);
		menubar.add(go);

		// Frame에 메뉴바 추가
		setJMenuBar(menubar);

		// 2. upper_pan에 추가할 3가지 판넬
		// pan_btn : 실행 및 정지 버튼 담을 판넬
		// pan_filter : 검색을 위한 filter 판넬
		pan_btn = new JPanel();
		// pan_sec.setBackground(Color.blue);
		btn_run = new JButton("RUN");
		btn_stop = new JButton("STOP");
		pan_btn.add(btn_run);
		pan_btn.add(btn_stop);
		btn_run.addActionListener(new ButtonActionListener());
		btn_stop.addActionListener(new ButtonActionListener());

		pan_filter = new JPanel();
		// pan_thd.setBackground(Color.red);
		text_filter = new JTextField(20);
		la_filter = new JLabel("  filter : ");
		btn_search = new JButton("SEARCH");
		pan_filter.add(la_filter);
		pan_filter.add(text_filter);
		pan_filter.add(btn_search);
		btn_search.addActionListener(new ButtonActionListener());

		// 3. 캡처된 패킷의 자세한 정보 요약하는 판넬
		pan_packetTable = new JPanel();
		// pan_packetTable.setBackground(Color.yellow);
		pan_packetTable.setLayout(new GridLayout(1, 1));
		//area_packinfo = new JTextArea();
		scrol_packinfo = new JScrollPane(area_packinfo);

		// 실험중인 테이블화
		DefaultTableModel model = new DefaultTableModel(val,colName);
		area_packinfo = new JTable(model);
		scrol_packinfo = new JScrollPane(area_packinfo);
		
		area_packinfo.updateUI();
		pan_packetTable.add(scrol_packinfo);
		//add(scrol_packinfo);
		
		//area_packinfo.setSize(400,800);
		//scrol_packinfo.setPreferredSize(new Dimension(500,600));
		//scrol_packinfo.setBounds(300, 500, 800, 200);
		
		 MainView runnable= new MainView(area_packinfo);
	        Thread th = new Thread(runnable);
			th.start();
			
			// * 정지 버튼
			btn_stop.addActionListener(new ActionListener() 
			{ 
				@Override 
				public void actionPerformed(ActionEvent e) 
				{ 
					th.interrupt(); //종료 
					JButton btn_stop = (JButton)e.getSource(); 
					btn_stop.setEnabled(false); // 버튼 비활성화 
				} 
			}); 
		
		// 4.lower 테이블
		// pan_PacketInfo : 해당 패킷에 대한 구체적인 정보 출력
		// pan_PacketHexCode : 해당 패킷에 대한 헥사 값 출력
		pan_PacketInfo = new JPanel();
		area_SpecificPackinfo = new JTextArea();
		scrol_packinfo = new JScrollPane(area_SpecificPackinfo);
		scrol_packinfo.setSize(100, 100);
		pan_PacketInfo.add(scrol_packinfo);
		pan_PacketInfo.setLayout(new GridLayout(1, 1));
		// setContentPane(pan_fiv);

		pan_PacketHexCode = new JPanel();
		area_HexCodeinfo = new JTextArea();
		scrol_packinfo = new JScrollPane(area_HexCodeinfo);
		scrol_packinfo.setSize(100, 100);
		pan_PacketHexCode.add(scrol_packinfo);
		pan_PacketHexCode.setLayout(new GridLayout(1, 1));

		// upper 팬
		upper_pan.add(pan_btn);
		upper_pan.add(pan_filter);

		// lower 팬
		lower_pan.add(pan_PacketInfo);
		lower_pan.add(pan_PacketHexCode);

		// 메인 팬 = uppper 팬 + 패킷 테이블 + lower 팬
		Main_pan.add(upper_pan);
		Main_pan.add(pan_packetTable);
		Main_pan.add(lower_pan);

		add(Main_pan);
		setVisible(true);
	}

	public MainView(JTable area_packinfo)
	{
		this.area_packinfo = area_packinfo;
	}
	
	@Override
	public void run() {
		DefaultTableModel m =(DefaultTableModel)area_packinfo.getModel();
		int n =1;
		while(true)
		{
			m.insertRow(0,new Object[]{n,"a1","a2","a3","a4","a5"});
			n++;
			try {
				Thread.sleep(500);
			}
			catch(InterruptedException e)
			{
				return;
			}
		}
	}

	class ButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			Object obj = e.getSource();
			if ((JButton) obj == btn_run) {
				System.out.println("실행버튼이 눌렸습니다.");
			} else if ((JButton) obj == btn_stop) {
				System.out.println("정지버튼이 눌렸습니다.");
			} else if ((JButton) obj == btn_search) {
				System.out.println("검색버튼이 눌렸습니다.");
			}
		}
	}

	class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			switch (cmd) {
			case "open":
				System.out.println("파일 불러오기 기능");
				break;
			case "close":
				System.out.println("파일 검색 닫기");
				break;
			case "save":
				System.out.println("해당 패킷정보들 저장하기");
				break;
			case "FindPacket":
				System.out.println("특정 패킷 찾기");
				break;
			case "MarkUnMark":
				System.out.println("");
				break;
			case "Ignore":
				System.out.println("");
				break;
			case "ShowPacketinNewWindow":
				System.out.println("선택된 패킷의 자세한 정보와 헥사값을 한번에 볼 수 있음. or 더블클릭");
				break;
			case "Gotopacket":
				System.out.println("마지막으로 눌려져 있던 패킷으로 이동");
				break;
			case "Previouspacket":
				System.out.println("이전 패킷으로 이동");
				break;
			case "Nextpacket":
				System.out.println("다음 패킷으로 이동");
				break; 
			case "Firstpacket":
				System.out.println("가장 맨 처음 패킷으로 이동");
				break;
			case "Lastpacket":
				System.out.println("가장 최근 패킷으로 이동");
				break;
			
			}
		}
	}

	public static void main(String[] args) {
		new MainView();
	}
}
