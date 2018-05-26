package com.kNoAPP.IL;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.media.Manager;
import javax.media.Player;

public class Spider implements Runnable {

	private volatile boolean exit = false;
	
	private List<String> hasVisited;
	private List<String> toVisit;
	private List<String> musicQueue;
	
	private boolean skip;
	
	public Spider() {
		this.hasVisited = new ArrayList<String>();
		this.toVisit = new ArrayList<String>();
		this.musicQueue = new ArrayList<String>();
		this.skip = false;
	}

	public void search(String url, int size) {
		toVisit.add(url);
		
		while(hasVisited.size() < size) {
			String next = nextUrl();
			if(next != null) {
				SpiderLeg leg = new SpiderLeg(next);
				if(toVisit.size() < 10000) toVisit.addAll(leg.getLinks());
				musicQueue.addAll(leg.getMusic());
			} else break;
		}
		
		System.out.println("[Done] Visited " + hasVisited.size() + " web page(s).");
	}
	
	public void search(String url) {
		toVisit.add(url);
		
		while(true) {
			String next = nextUrl();
			if(next != null) {
				SpiderLeg leg = new SpiderLeg(next);
				if(toVisit.size() < 1000) toVisit.addAll(leg.getLinks());
				musicQueue.addAll(leg.getMusic());
			} else break;
		}
		
		System.out.println("[Done] Visited " + hasVisited.size() + " web page(s).");
	}
	
	public void skip() {
		skip = true;
	}
	
	public void skip(int skip) {
		skip();
		skip--;
		
		for(int i=0; i<skip; i++) if(musicQueue.size() > 0) musicQueue.remove(0);
	}
	
	public void stop() {
		exit = true;
	}
	
	public void run() {
		while(!exit) {
			if(musicQueue.size() > 0) {
				String url = musicQueue.get(0);
				musicQueue.remove(0);
				try {
					Player player = Manager.createPlayer(new URL(url));
					long time = System.currentTimeMillis();
					player.start();
					
					while(System.currentTimeMillis() < player.getDuration().getSeconds()*1000 + time && !skip) continue;
					skip = false;
					
					player.stop();
				} catch(Exception e) {}
			}
		}
	}
	
	/*private void playWav(String url) {
		AudioInputStream din = null;
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(new URL(url));
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
					baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if(line != null) {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				// Start
				line.start();

				int nBytesRead;
				while((nBytesRead = din.read(data, 0, data.length)) != -1) {
					line.write(data, 0, nBytesRead);
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(din != null) {
				try {
					din.close();
				} catch (IOException e) {}
			}
		}
	}*/

	private String nextUrl() {
		String nextUrl;
		do {
			if(toVisit.size() > 0) nextUrl = toVisit.remove(0);
			else return null;
		} while(hasVisited.contains(nextUrl));
		hasVisited.add(nextUrl);
		return nextUrl;
	}
}
