package com.eyes.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;


public class FileOperation {
	private static final String BLACKSLASH_X4 = "\\\\";
	private static final String SPACE_X0 = "";
	private String path;
	private List<String> allAbsolutePaths;
	private List<String> allSavePaths;
	public FileOperation(String path) throws Exception {
		if (path == null || path.equals("")) {
			throw new IllegalArgumentException("path�������ݲ���ȷ��");
		}
		this.path = path;
		allAbsolutePaths = new ArrayList<>();
		allSavePaths = new ArrayList<>();
		getAllPaths(path);
	}

	
	
	private void getAllPaths(String path) throws Exception{
		File file = new File(path);
		if (file.isDirectory()) {//���ļ���
			File[] files = file.listFiles();
			if (files == null) {
				throw new Exception("��ȡ�ļ�·������");
			}else if(files.length == 0) {
				throw new IllegalArgumentException("��ǰ�ļ��пյ�");
			}else{
				//�ݹ��ȡ��path
				recursionPaths(files);
			}
		} else {				//���ļ�
			//������ļ�Ŀ¼
			String p = file.getAbsolutePath();
			allAbsolutePaths.add(p);
		}
	}

	private void recursionPaths(File[] files) throws Exception{
		for (File file : files) {
			if (file.isDirectory()) {
				File[] childFiles = file.listFiles();
				if (childFiles == null) {
					//throw new Exception("��ȡ�ļ�·������");
					System.out.println("��ȡ�ļ���"+file.getAbsolutePath()+"�����ļ�����");
					//continue;
				}else if(childFiles.length == 0) {
					//������ļ���Ŀ¼
					String p = file.getAbsolutePath();
					allAbsolutePaths.add(p);
				}else{
					//�ݹ��ȡ��path
					recursionPaths(childFiles);
				}
			}else{
				//������ļ�Ŀ¼
				String p = file.getAbsolutePath();
				allAbsolutePaths.add(p);
			}
		}
	}

	//��ȡ��·����������·��������ļ�
	public List<String> getAllChildPaths(){
		List<String> list = new ArrayList<String>();
		if (allAbsolutePaths == null) {
			return null;
		}else if(allAbsolutePaths.size() == 1){
			String filename = new File(path).getName();
			list.add(filename);
			return list;
		}else{
			String newPath = moreBlackSlash(path);
			for (String string : allAbsolutePaths) {
				String tmpstr = string.replaceFirst(newPath, SPACE_X0);
				//				System.out.println(tmpstr);
				list.add(tmpstr);
			}
			return list;
		}
	}

	//��ȡ��·����������·��������ļ�
	public void setAllSavePaths(String parentPath){
		//û�ļ�
		if (allAbsolutePaths == null) {
			return;
			//��һ���ļ�
		}else if(allAbsolutePaths.size() == 1){
			String filename = parentPath + new File(path).getName();
			allSavePaths.add(filename);
			return;
			//��һ���ļ���
		}else{
			String newPath = moreBlackSlash(path);
			String newPPath = moreBlackSlash(parentPath);
			for (String string : allAbsolutePaths) {
				String tmpstr = string.replaceFirst(newPath, newPPath);
				System.out.println(tmpstr);
				allSavePaths.add(tmpstr);
			}
		}
	}

	private String moreBlackSlash(String path){
		String[] paths = path.split(BLACKSLASH_X4);
		StringBuilder sb = new StringBuilder();
		for (String string : paths) {
			sb.append(string);
			sb.append(BLACKSLASH_X4);
		}
		return sb.toString();
	}

	private String getSingleSavePath(String absolutePath, String targetParentPath){
		String p = moreBlackSlash(path);
		String pP = moreBlackSlash(targetParentPath);
		return absolutePath.replaceFirst(p, pP);
	}

	public void copyDirectoriesToLocal(String targetPath, WriteFileTo wft){
		long begin = System.currentTimeMillis();
		for (String  readStr: allAbsolutePaths) {
			File readFile = new File(readStr);
			String savePath = getSingleSavePath(readStr, targetPath);
			File writeFile = new File(savePath);
			if (readFile.isFile()) {
				//��ȡ�ļ�
				wft.writeFile(readFile, writeFile);
			}else{
				//�����ļ���
				writeFile.mkdir();
			}
		}

		long end = System.currentTimeMillis();
		printTime(begin, end);

	}
	
	public void copyDirectoriesToSocket(){
		
	}
	
	private void printTime(long before, long after){
		if (before <=0 || after <= 0 || after < before) {
			throw new IllegalArgumentException("ʱ��������Ϸ�");
		}

		double time = ((double)after - (double)before)/1000;

		String second = String.format("%.4f", time%60);
		int minute = (int) (time/60)%60;
		int hour = (int)(time/3600)%24;
		int day = (int)time/(3600*24);
		if (day == 0) {
			if (hour == 0) {
				if (minute == 0) {
					System.out.println("���Ƴɹ���������"+second+"��");
				}else {
					System.out.println("���Ƴɹ���������"+minute+"����"+time+"��");
				}
			}else {
				System.out.println("���Ƴɹ���������"+hour+"Сʱ"+minute+"����"+time+"��");
			}
		}else{
			System.out.println("���Ƴɹ���������"+day+"��"+hour+"Сʱ"+minute+"����"+time+"��");
		}
	}

	public static class WriteFileToDisk implements WriteFileTo{

		@Override
		public void writeFile(File sourceFile, File targetFile) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				if (!targetFile.exists()) {
					File parentFile = targetFile.getParentFile();
					if (parentFile != null && !parentFile.exists()) {
						//������ͬ�����ļ������򴴽��ļ��л�ʧ��
						if (parentFile != null && parentFile.isFile()) {
							if (!parentFile.delete()) {
								System.out.println("�����ļ������Ŀ¼������ͬ��ɾ��ʧ��");
								return;
							}
						}
						if (!parentFile.mkdirs()) {
							System.out.println("����"+targetFile.getParent()+"�ļ���ʧ��");
							return;
						}
						if (!targetFile.createNewFile()) {
							System.out.println("����"+targetFile+"�ļ�ʧ��");
							return;
						}
					}
				}
				fis = new FileInputStream(sourceFile);
				fos = new FileOutputStream(targetFile);
				byte[] bytes = new byte[102400];
				int len = 0;
				while ((len = fis.read(bytes)) >= 0) {
					fos.write(bytes, 0, len);
					fos.flush();
				}
				System.out.println(
						"����"+sourceFile.getAbsolutePath()+
						"��"+targetFile.getAbsolutePath()+"�ɹ�");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					fis = null;
					fis = null;
				}
			}
		}

	}

}
