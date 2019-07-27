package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.filechooser.*;

public class OpenActionListener implements ActionListener {

	JFileChooser chooser;
	
	OpenActionListener() {
		chooser = new JFileChooser(); //파일 다이얼로그 생성
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		FileNameExtensionFilter filter = new FileNameExtensionFilter("모든 파일", "jpg", "gif");
		chooser.setFileFilter(filter);
		
		//파일 다이얼로그 출력
		int ret = chooser.showOpenDialog(null);
		
		//창을 강제로 닫거나 취소 버튼 누른 경우
		if(ret != JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
			return;
		}
		//사용자가 파일을 선택하고 "열기" 누른 경우
		String filePath = chooser.getSelectedFile().getPath();//파일 경로명 알아오기
		//파일 처리
	}

}
