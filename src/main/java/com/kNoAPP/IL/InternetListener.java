package com.kNoAPP.IL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class InternetListener {
	
	public static Spider spider = new Spider();

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		System.out.println("Listen to the Web - by kNoAPP");
		System.out.println("-------------HELP------------");
		System.out.println("1. Type skip + ENTER to skip a .wav");
		System.out.println("2. Type skip <#> + ENTER to skip multiple .wavs");
		System.out.println("3. Type stop + ENTER to stop.");
		System.out.print(">> Enter a URL to begin at: ");
		String url = reader.nextLine();
		
		new Thread(spider).start();
		new Thread(new UserInput()).start();
		
		spider.search(url);
		reader.close();
	}
	
	//TODO Finish this input thread after class.
	public static class UserInput implements Runnable {

		private volatile boolean exit = false;
		private BufferedReader in;
		
		public UserInput() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}
		
		public void run() {
			while(!exit) {
				try {
					String input = in.readLine();
					if(input != null) {
						String args[] = input.split("\\s+");
						
						if(args.length == 1) {
							if(args[0].equals("skip")) {
								System.out.println("Skipping...");
								spider.skip();
							}
							if(args[0].equals("stop")) {
								System.out.println("Bye!");
								spider.stop();
								stop();
								System.exit(0);
							}
						}
						if(args.length == 2) {
							if(args[0].equals("skip")) {
								try {
									int i = Integer.parseInt(args[1]);
									System.out.println("Skipping " + i + "...");
									spider.skip(i);
								} catch(NumberFormatException ex) {
									System.out.println("Invalid number!");
								}
							}
						}
					}
					Thread.sleep(500);
				} catch (Exception ex) {}
			}
		}
		
		public void stop() {
			exit = true;
		}
	}
}
