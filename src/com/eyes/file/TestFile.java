package com.eyes.file;

import java.util.List;
import java.util.Scanner;

public class TestFile {
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("�������ȡ���ļ�/�ļ���·����");
		String pathRead = scan.nextLine();
		System.out.println("�����뱣����ļ�/�ļ���·����");
		String pathWrite = scan.nextLine();
		
		
		
		FileOperation fr = new FileOperation(pathRead);
		List<String> lf = fr.getAllChildPaths();
		fr.setAllSavePaths(pathWrite);
		fr.copyDirectoriesToLocal(pathWrite, new FileOperation.WriteFileToDisk());
		
	}
}


