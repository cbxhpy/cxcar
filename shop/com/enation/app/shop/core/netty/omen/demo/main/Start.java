package com.enation.app.shop.core.netty.omen.demo.main;

import com.enation.app.shop.core.netty.omen.netty.server.Main;

public class Start {

	public static void main(String[] strs) throws Exception {

		/*		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		t.start();*/
		System.out.println("============================Start classloader:" +  Start.class.getClassLoader().hashCode());
		Main.main(null);

	}
}
